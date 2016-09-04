/**
 * 
 */
package hk.hku.cecid.edi.sfrm.handler;

import java.util.List;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDVO;
import hk.hku.cecid.edi.sfrm.handler.SFRMMessageHandler;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.spa.SFRMComponent;
import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * 
 * @author Patrick Yip
 */
public class SFRMExternalRequestHandler extends SFRMComponent {
	
	private static SFRMExternalRequestHandler handler = new SFRMExternalRequestHandler();

	public SFRMExternalRequestHandler getInstance(){
		return handler;
	}
	
	protected void init() throws Exception{
		super.init();
	}
	
	public synchronized void suspendMessage(String messageId) throws DAOException, SFRMException, InterruptedException{
		//TODO: To wait for the message lock to release before to suspend the message
		SFRMMessageHandler mHandler = this.getMessageHandler();
		SFRMMessageSegmentHandler msHandler = this.getMessageSegmentHandler();
		//Retrieve the message from outbox only, it mean that suspend operation can only be done on sender side
		SFRMMessageDVO mDVO = mHandler.retrieveMessage(messageId, SFRMConstant.MSGBOX_OUT) ;
		
		if(mDVO == null){
			throw new DAOException("Message with ID '" + messageId + "' didn't exist");
		}
		
		//Check if the message status is processing, if it is not, there is no need to make a suspend request
//		if(!mDVO.getStatus().equals(SFRMConstant.MSGS_PROCESSING) || !mDVO.getStatus().equals(SFRMConstant.MSGS_SEGMENTING)){
		if(!mDVO.getStatus().equals(SFRMConstant.MSGS_PROCESSING)){
			String logMsg = "Message Id '" + messageId + "' status is not segmenting or processing, no necessary to suspend";
			this.getLogger().info(logMsg);
			throw new SFRMException(logMsg);
		}
		
		if(mDVO.getStatus().equals(SFRMConstant.MSGS_PROCESSING)){		
			mDVO.setStatus(SFRMConstant.MSGS_PRE_SUSPENDED);
			mDVO.setStatusDescription(SFRMConstant.getStatusDescription(SFRMConstant.MSGS_PRE_SUSPENDED));
			if(mHandler.updateMessage(mDVO) == false){
				throw new DAOException("DB Error");
			}
					
			//Mark the existing message segment to SD status if it status is in PD or DF  
			List segDVOs = msHandler.retrieveMessages(messageId, SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, new String[]{SFRMConstant.MSGS_PENDING, SFRMConstant.MSGS_DELIVERY_FAILURE});
			SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) msHandler.getDAOInstance();
			
			for(int i=0 ; segDVOs.size() > i ;i++){
				SFRMMessageSegmentDVO msDVO = (SFRMMessageSegmentDVO) segDVOs.get(i);
				msDVO.setStatus(SFRMConstant.MSGS_SUSPENDED);
				if(msDAO.persist(msDVO) == false){
					throw new DAOException("DB Error");
				}
			}
		}else if(mDVO.getStatus().equals(SFRMConstant.MSGS_SEGMENTING)){
			mDVO.setStatus(SFRMConstant.MSGS_PRE_DELIVERY_FAILED);
			mDVO.setStatusDescription(SFRMConstant.getStatusDescription(SFRMConstant.MSGS_PRE_DELIVERY_FAILED));
			if(mHandler.updateMessage(mDVO) == false){
				throw new DAOException("DB Error");
			}
			
			//Mark the existing message segment to DF status if it status is in PD or DF  
//			List segDVOs = msHandler.retrieveMessages(messageId, SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, new String[]{SFRMConstant.MSGS_PENDING, SFRMConstant.MSGS_DELIVERY_FAILURE});
			
			SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) msHandler.getDAOInstance();
			List segDVOs = msHandler.retrieveMessages(messageId, SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, new String[]{SFRMConstant.MSGS_PENDING, SFRMConstant.MSGS_DELIVERY_FAILURE, SFRMConstant.MSGS_PRE_PROCESSED, SFRMConstant.MSGS_DELIVERED, SFRMConstant.MSGS_PROCESSING});
//			SFRMMessageSegmentDVO queryMSDVO = (SFRMMessageSegmentDVO) msDAO.createDVO();
//			queryMSDVO.setMessageBox(SFRMConstant.MSGT_PAYLOAD);
//			queryMSDVO.setMessageId(messageId);
			for(int i=0 ; segDVOs.size() > i ;i++){
				SFRMMessageSegmentDVO msDVO = (SFRMMessageSegmentDVO) segDVOs.get(i);
				msDVO.setStatus(SFRMConstant.MSGS_DELIVERY_FAILURE);
				if(msDAO.persist(msDVO) == false){
					throw new DAOException("DB Error");
				}
			}
			
		}
		
		
	}
	
	public synchronized void resumeMessage(String messageId) throws DAOException, SFRMException{
		SFRMMessageHandler mHandler = this.getMessageHandler();
		SFRMMessageDVO mDVO = mHandler.retrieveMessage(messageId, SFRMConstant.MSGBOX_OUT);
		
		if(mDVO == null){
			throw new DAOException("Message with ID '" + messageId + "' didn't exist");
		}
		
		//Check if the message status is suspended, if it is not, no need to resume message
		if(!mDVO.getStatus().equals(SFRMConstant.MSGS_SUSPENDED)){
			String logMsg = "Message Id '" + messageId + "' status is not suspended, no necessary to resume";
			this.getLogger().info(logMsg);
			throw new SFRMException(logMsg);
		}
		
		//Update the message status
		mDVO.setStatus(SFRMConstant.MSGS_PRE_RESUME);
		mDVO.setStatusDescription(SFRMConstant.getStatusDescription(SFRMConstant.MSGS_PRE_RESUME));	
		
		//Update the message segment status
		SFRMMessageSegmentHandler msHandler = this.getMessageSegmentHandler();
		List segDVOs = msHandler.retrieveMessages(messageId, SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, new String[]{SFRMConstant.MSGS_SUSPENDED});
		SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) msHandler.getDAOInstance();
		
		for(int i=0; segDVOs.size() > i;i++){
			SFRMMessageSegmentDVO msDVO = (SFRMMessageSegmentDVO) segDVOs.get(i);
			msDVO.setStatus(SFRMConstant.MSGS_PENDING);
			if(msDAO.persist(msDVO) == false){
				throw new DAOException("DB Error");
			}
		}
		
		if(mHandler.updateMessage(mDVO) == false){
			throw new DAOException("DB Error");
		}
	}
}

