package hk.hku.cecid.edi.sfrm.handler;

import hk.hku.cecid.edi.sfrm.com.PackagedPayloads;
import hk.hku.cecid.edi.sfrm.com.PayloadException;
import hk.hku.cecid.edi.sfrm.com.PayloadsState;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;
import hk.hku.cecid.edi.sfrm.pkg.SFRMAcknowledgementParser;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.spa.SFRMComponent;
import hk.hku.cecid.edi.sfrm.util.StatusQuery;
import hk.hku.cecid.piazza.commons.dao.DAOException;

import java.sql.Timestamp;

import java.util.List;
import java.util.Vector;

public class AcknowledgementHandler extends SFRMComponent {
	private static AcknowledgementHandler ackHandler;
	
	public static AcknowledgementHandler getInstance(){
		return ackHandler;
	}
	
	/**
	 * Initialization of this Component
	 */
	protected void init() throws Exception{
		super.init();
		ackHandler = this;
	}
	
	
	public void processAcknowledgementResponse(String responseContent) throws Exception{
		SFRMAcknowledgementParser parser = new SFRMAcknowledgementParser(responseContent);
		List<String> msgIDs = parser.getMessagesIDs();
		if(msgIDs.size() != 1){
			getLogger().debug("Acknowledgement response should contains only one message information");
			throw new Exception("Acknowledgement response should contains only one message information");
		}
		
		String messageId = msgIDs.get(0);
		String responseMessageStatus = parser.getMessageStatus(messageId);
		
		SFRMMessageDVO mDVO = this.getMessageHandler().retrieveMessage(messageId, SFRMConstant.MSGBOX_OUT);
		
		if(mDVO == null){
			String logStr = "Message with ID '" + messageId + "' doesn't exist, cannot process the acknowledgement response";
			this.getLogger().error(logStr);
			throw new DAOException(logStr);
		}
		
		//If the message status responsed is PS, DF or SD, update the local message status accordingly
		if(responseMessageStatus.equals(SFRMConstant.MSGS_PROCESSED)
				|| responseMessageStatus.equals(SFRMConstant.MSGS_DELIVERY_FAILURE)
				|| responseMessageStatus.equals(SFRMConstant.MSGS_SUSPENDED)
				//Resume Case
				|| (responseMessageStatus.equals(SFRMConstant.MSGS_PROCESSING) && (mDVO.getStatus().equals(SFRMConstant.MSGS_PRE_RESUME))))
				{
			updateMessageStatus(mDVO, responseMessageStatus);
		//TODO: didn't it need to check the message status for updating the message segment status
		}else {
			List<Integer> segNums = parser.getMessageSegmentNums(messageId);
			updateMessageSegmentStatus(mDVO, segNums, parser);
		}
		
		preCompleteMessageIfNeeded(mDVO);
		
		
	}
	
	private void stopSpeedMonitor(String messageId){
		MessageStatusQueryHandler queryHandler = MessageStatusQueryHandler.getInstance();
		StatusQuery query = queryHandler.removeMessageSpeedQuery(messageId);
		if(query != null){
			query.stop();
		}
	}
	
	/**
	 * Update the message segment status according to the acknowledgement request content
	 * @param messageId
	 * @param segNums
	 * @param parser
	 * @throws DAOException
	 */	
	private void updateMessageSegmentStatus(SFRMMessageDVO mDVO, List<Integer> segNums, SFRMAcknowledgementParser parser) throws DAOException{
		if(segNums.size() != 0){

			Vector<Integer> DFList = new Vector<Integer>();
			Vector<Integer> PSList = new Vector<Integer>(); 
			//Categorize the segment to different status group		
			for(int i=0 ; segNums.size() > i; i++){
				String responseStatus = parser.getMessageSegmentStatus(mDVO.getMessageId(), segNums.get(i));
				if(responseStatus.equals(SFRMConstant.MSGS_DELIVERY_FAILURE)){
					DFList.add(segNums.get(i));
				}else if(responseStatus.equals(SFRMConstant.MSGS_PROCESSED)){
					PSList.add(segNums.get(i));
				}
			}
			
			SFRMMessageSegmentDAO segDAO = (SFRMMessageSegmentDAO) this.getMessageSegmentHandler().getDAOInstance();
			//Update segment status to PD if the response status is DF
			if(DFList.size() != 0){
				segDAO.updateBatchSegmentsRecoveryStatus(SFRMConstant.MSGS_PENDING, mDVO.getMessageId(), SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, DFList);
			}
			
			//Update segment status to PS if the response status is PS
			if(PSList.size() != 0){
				segDAO.updateBatchSegmentsStatus(SFRMConstant.MSGS_PROCESSED, new Timestamp(System.currentTimeMillis()), mDVO.getMessageId(), SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, PSList);
			}
		}
	}
	
	
	
	/**
	 * Check whether all of the message segements was received, if it is, set the message status to PPS
	 * @param messageId message ID
	 * @return whether the message status was marked as PPS
	 */
	private boolean preCompleteMessageIfNeeded(SFRMMessageDVO mDVO) throws DAOException{
		if(mDVO == null){
			throw new DAOException("Message ID " + mDVO.getMessageId() + " is null");
		}
		
		if(mDVO.getStatus().equals(SFRMConstant.MSGS_PROCESSED))
			return true;
		
		int numTotalSegs = mDVO.getTotalSegment();
		int numPSSegs = this.getMessageSegmentHandler().retrieveMessageSegmentCount(mDVO.getMessageId(), SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, SFRMConstant.MSGS_PROCESSED);
		
		if(numPSSegs == numTotalSegs){
			mDVO.setStatus(SFRMConstant.MSGS_PRE_PROCESSED);
			boolean peristed = this.getMessageHandler().updateMessage(mDVO);
			if(peristed == false){
				throw new DAOException("Message ID " + mDVO.getMessageId() + " not found");
			}
			return true;
			
		}
		return false;
		
	}
	
	private SFRMMessageDVO updateMessageStatus(SFRMMessageDVO mDVO, String status) throws DAOException, PayloadException{
		getLogger().debug("Inside updateMessageStatus");
		mDVO.setStatus(status);
		mDVO.setStatusDescription(SFRMConstant.getStatusDescription(status));
		
		if(status.equals(SFRMConstant.MSGS_PROCESSED)){
			mDVO.setCompletedTimestamp(new Timestamp(System.currentTimeMillis()));
			completePayload(mDVO);
			
			//Stop the Network speed monitoring if the message status is being PS
			stopSpeedMonitor(mDVO.getMessageId());
		}
		this.getMessageHandler().updateMessage(mDVO);
		return mDVO;
	}
		
	private void completePayload(SFRMMessageDVO mDVO) throws PayloadException{
		getLogger().debug("Inside the complete payload");
		//TODO: Fix the clear the packaged payload
		PackagedPayloads pp = (PackagedPayloads) getOutgoingRepository().getPayload(new Object[]{
				mDVO.getPartnershipId(),
				mDVO.getMessageId(),
				mDVO.getFilename()},
				PayloadsState.PLS_PROCESSING);
		
		if (pp != null){
    		pp.clearPayloadCache();
		}else{
			throw new PayloadException("Payload " + pp.getFilename() + " didn't existed");
		}
	}
	
}
