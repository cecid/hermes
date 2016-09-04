/**
 * Contains the set of active module collector for 
 * handling packaging, segmenting, sending, joining and unpacking of payloads.
 */
package hk.hku.cecid.edi.sfrm.task;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.Calendar;

import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;

import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.edi.sfrm.spa.SFRMLog;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.spa.SFRMProperties;
import hk.hku.cecid.edi.sfrm.util.StatusQuery;
import hk.hku.cecid.edi.sfrm.util.StopWatch;
import hk.hku.cecid.edi.sfrm.activation.FileRegionDataSource;
import hk.hku.cecid.edi.sfrm.com.PackagedPayloads;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;

import hk.hku.cecid.edi.sfrm.handler.MessageStatusQueryHandler;
import hk.hku.cecid.edi.sfrm.handler.OutgoingMessageHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMMessageFactory;
import hk.hku.cecid.edi.sfrm.handler.SFRMMessageHandler;

import hk.hku.cecid.piazza.commons.module.ActiveTaskAdaptor;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * <strong> What the task does </strong>
 * <ul>
 * 	<li> Update the status of message to segmentating (status: ST).</li>
 * 	<li> Analyze the payload and save the segment record to the database. </li>
 * 	<li> Update the status of message to processing (status: PR). </li>  
 * </ul>	
 * 
 * Creation Date: 24/10/2006.<br><br>
 * 
 * @author Twinsen Tsang
 * @version 1.0.3 
 * @since 	1.0.1
 * 
 * @version 2.0.0
 * @since	1.0.4 
 */
public class OutgoingPayloadsTask extends ActiveTaskAdaptor {
		
	/**
	 * The archive payload of this task.
	 */
	private final PackagedPayloads 	 payload;
	
	/**
	 * The message record for this task.
	 */
	private SFRMMessageDVO	 msgDVO;
	
	/**
	 * The SFRM partnership DVO for this task.
	 */
	private final SFRMPartnershipDVO pDVO;
		
	/**
	 * 
	 */
	private boolean retryEnabled;
	
	/**
	 * 
	 */
	private int currentRetried;
	
	
	private String taskStatus;
		
	/** 
	 * Explicit Constructor.
	 * @param payload
	 * 			The packaged payload file.
	 * @param pDVO TODO
	 * @param status The status of the for process
	 * @throws NullPointerException
	 * 			if the input payload is null.
	 * @throws DAOException
	 * 			if fail to retreve the partnership.
	 * @throws IOException 
	 */	
	public OutgoingPayloadsTask(PackagedPayloads payload, SFRMPartnershipDVO pDVO, String status) throws DAOException, IOException{
		if(payload == null){
			throw new NullPointerException(SFRMLog.OPT_CALLER + "Packaged payload cannot be null");
		}
		
		SFRMProcessor.getInstance().getLogger().debug(SFRMLog.OPT_CALLER + "Creating a new task:\npayload location:" + payload.getOriginalRootname() + "\nmsg ID: " + payload.getRefMessageId());
		
		//Retrieve the partnership DVO
		this.pDVO = pDVO;
		
		if(this.pDVO == null){
			throw new NullPointerException(SFRMLog.OPT_CALLER + "Missing Partnership");
		}
		
		this.payload = payload;
		this.currentRetried = 0;
		this.retryEnabled 	= this.pDVO.getRetryMax() > 0;
		taskStatus = status;
	}
	
	/**
	 * Create a new message in db or find am existing message record. create or find depend on the status of message request to retrieve
	 * @param mId message ID
	 * @param pId partnership ID
	 * @throws Exception
	 */
	private void createOrFindMessage(String mId, String pId) throws Exception{
		//If the there didn't have message record in DB, create it
		if(taskStatus == SFRMConstant.MSGS_PACKAGED){
			//If retried before, check whether the message is existing in the DB
			if(this.currentRetried != 0){
				SFRMMessageDVO tempDVO = SFRMProcessor.getInstance().getMessageHandler().retrieveMessage(mId, SFRMConstant.MSGBOX_OUT);
				if(tempDVO != null){
					this.msgDVO = tempDVO;
					return;
				}
			}
			
			SFRMMessageDAO msgDAO = (SFRMMessageDAO) SFRMProcessor.getInstance().getMessageHandler().getDAOInstance();
			this.msgDVO = (SFRMMessageDVO) msgDAO.createDVO();
			//Set the message related information
			msgDVO.setMessageId(mId);
			msgDVO.setMessageBox(SFRMConstant.MSGBOX_OUT);
			msgDVO.setPartnershipId	(pId);
			msgDVO.setPartnerEndpoint	(pDVO.getPartnerEndpoint());
			msgDVO.setSignAlgorithm(pDVO.getSignAlgorithm());
			msgDVO.setEncryptAlgorithm(pDVO.getEncryptAlgorithm());
			msgDVO.setStatus		 	(SFRMConstant.MSGS_HANDSHAKING);
			msgDVO.setStatusDescription(SFRMConstant.MSGSDESC_HANDSHAKING);
			msgDVO.setCreatedTimestamp	(new Timestamp(System.currentTimeMillis()));
			msgDVO.setFilename			(this.payload.getFilename());
			msgDVO.setIsHostnameVerified(pDVO.isHostnameVerified());
			if(msgDVO.getEncryptAlgorithm() != null)
				msgDVO.setPartnerCertContent(pDVO.getEncryptX509CertificateBase64());
			
			msgDAO.create(msgDVO);
		}
		
		//If it need to retrieve the segmenting message, it mean it already existing in the database 
		if(taskStatus == SFRMConstant.MSGS_SEGMENTING){
			SFRMMessageHandler mHandler = SFRMProcessor.getInstance().getMessageHandler();
			this.msgDVO = mHandler.retrieveMessage(mId, SFRMConstant.MSGBOX_OUT);
		}
	}
	
	/**
	 * To perform the checking of maximum allowed payload size and do the handshaking with receiver 
	 * @param mId
	 * @param pId
	 * @throws Exception
	 */
	private void handshaking(String mId, String pId) throws Exception{
		String logInfo = " msg id: " + mId;
		SFRMProcessor.getInstance().getLogger().debug(SFRMLog.OPT_CALLER + SFRMLog.SEND_HDSK + logInfo);
		// Step 2	: Handshaking Steps (Try to communicate with the receiver)
		// 		2.0 - Check whether the payload is exceeding file limit.
		//		2.1 - Estimate number of segments.
		//		2.2 - Generate handshaking request and send to receiver.
		// ------------------------------------------------------------------------

		// ------------------------------------------------------------------------
		// 2.0 - Check whether the payload is exceeding file limit.
		long payloadSize = payload.getSize();
		long maxPayloadSize= SFRMProperties.getMaxPayloadSize();
		if (payloadSize > maxPayloadSize){
			this.msgDVO.setStatus(SFRMConstant.MSGS_PROCESSING_ERROR);
			SFRMProcessor.getInstance().getMessageHandler().updateMessage(this.msgDVO);
			//Stop the handshaking immediately
			setRetried(this.getMaxRetries());
			throw new SFRMException(
				"Payload Exceeding file size limit: "
			  +  payloadSize
			  +" can allow file size under: "
			  +  maxPayloadSize);
		}
		
		//		2.1 - Estimate number of segments.
		long numOfSegment = payloadSize / SFRMProperties.getPayloadSegmentSize() + 1;
		
		//		2.2 - Generate handshaking request and send to receiver.
		//TODO: Added the catching the exception for handshaking
		try{
			OutgoingMessageHandler.getInstance().sendMessage(
					// Create Handshaking segment request
					SFRMMessageFactory.getInstance()
						.createHandshakingRequest(mId, pId, (int)numOfSegment, payloadSize, payload.getFilename()),
					msgDVO.getPartnerEndpoint(), msgDVO.getIsHostnameVerified(), 
					msgDVO.getSignAlgorithm(), msgDVO.getEncryptAlgorithm(), msgDVO.getPartnerX509Certificate());
			
		}catch(Exception e){
			//If handshaking is failed, mark the message as delivery failed
			this.msgDVO.setStatus(SFRMConstant.MSGS_PROCESSING_ERROR);
			SFRMProcessor.getInstance().getMessageHandler().updateMessage(this.msgDVO);
			//Stop the handshaking immediately
			setRetried(this.getMaxRetries());
			throw e;
		}
	
		// Log information.
		SFRMProcessor.getInstance().getLogger().info(SFRMLog.OPT_CALLER + SFRMLog.PACK_MSG + logInfo);
		
		// ------------------------------------------------------------------------
		// Step 4: Update the message status to ST for next modules 
		//		   and the payload status to processed.
		// ------------------------------------------------------------------------
		// Update the proceeding time and total size.
		this.msgDVO.setTotalSegment		((int)numOfSegment);
		this.msgDVO.setTotalSize		(payload.getSize());
		this.msgDVO.setProceedTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		SFRMProcessor.getInstance().getMessageHandler().updateMessage(this.msgDVO);
	}
	
	
	private void segmenting() throws Exception{
		taskStatus = SFRMConstant.MSGS_SEGMENTING;
		// ------------------------------------------------------------------------
		// Step 5: Update the message status to 'ST' and save to database. 
		// ------------------------------------------------------------------------
		SFRMMessageHandler mHandle = SFRMProcessor.getInstance().getMessageHandler();
		this.msgDVO.setStatus			(SFRMConstant.MSGS_SEGMENTING);
		this.msgDVO.setStatusDescription(SFRMConstant.MSGSDESC_SEGMENTING);		
		mHandle.updateMessage(this.msgDVO);

		// ------------------------------------------------------------------------
		// Step 6: Insert all segments record into database.		
		// ------------------------------------------------------------------------
		SFRMMessageSegmentDAO segDAO = (SFRMMessageSegmentDAO) 
			SFRMProcessor.getInstance().getMessageSegmentHandler().getDAOInstance();
		SFRMMessageSegmentDVO segDVO = (SFRMMessageSegmentDVO) 
			segDAO.createDVO();
		
		// Setup all parameters that are common for each segment.
		segDVO.setMessageId	 (this.payload.getRefMessageId());
		segDVO.setMessageBox (SFRMConstant.MSGBOX_OUT);
		segDVO.setStatus	 (SFRMConstant.MSGS_PENDING);
		segDVO.setSegmentType(SFRMConstant.MSGT_PAYLOAD);
		
		long startPos = 0;
		long endPos = 0;
		long segmentSize	= SFRMProperties.getPayloadSegmentSize();
		long fileSize		= msgDVO.getTotalSize();		
		int	 numOfSegments	= msgDVO.getTotalSegment();
		long lastSegmentSize= fileSize - ((numOfSegments -1)* segmentSize);		
		
		StopWatch sw = new StopWatch();
		sw.start();
		
		// Find out the maximum segment no created in the DB.
		// This is used for when there is inproper system shutdown
		// during the creating segment, the module still can 
		// recover the system.
		// TODO: Implement same function to SFRM_MSH.
		int  maxSegmentNo	= segDAO.findMaxSegmentNoByMessageIdAndBoxAndType(
			this.payload.getRefMessageId(), 
			SFRMConstant.MSGBOX_OUT,
			SFRMConstant.MSGT_PAYLOAD);
		
		if (maxSegmentNo > 0)	
			SFRMProcessor.getInstance().getLogger().info(
				  SFRMLog.OPT_CALLER
			   + "Resume segmentation with msg id: " 
			   +  this.payload.getRefMessageId()
			   +" at sgt no: "
			   +  maxSegmentNo);			   								

		for (int i = maxSegmentNo + 1; i <= numOfSegments; i++){						
			// Retrieve the segment type and their byte range
			// by the result. The possible case are shown in the following:
			// Support there are n segments where each segment has 
			// S bytes. then 
			// For case i == r < n
			//		it is a payload message. The start and end pos   
			// 		are (i-1)*S and (i)*S resp.
			// For case i == n
			//		it is the last payload message. The start and end pos
			//		are (i-1)*S and (start pos) + (last segment size).						
			startPos = (i - 1) * segmentSize;				
			if (i == numOfSegments)
				endPos = startPos + lastSegmentSize;
			else
				endPos = startPos + segmentSize;
									
			segDVO.setSegmentNo(i);
			segDVO.setSegmentStart(startPos);
			segDVO.setSegmentEnd(endPos);
			
			//Set Mic value for segment
			if( i == numOfSegments){
//				segDVO.setMD5Value(SFRMMessage.digest((new FileRegionDataSource(payload.getRoot(), startPos, lastSegmentSize)).getInputStream()));
				segDVO.setMD5Value(SFRMMessage.digest(new FileRegionDataSource(payload.getRoot(), startPos, lastSegmentSize)));
			}
			else{
//				segDVO.setMD5Value(SFRMMessage.digest((new FileRegionDataSource(payload.getRoot(), startPos, segmentSize)).getInputStream()));
				segDVO.setMD5Value(SFRMMessage.digest(new FileRegionDataSource(payload.getRoot(), startPos, segmentSize)));
			}
						
			segDAO.create(segDVO);
		}
				
		// ------------------------------------------------------------------------
		// Step 7: Update the proceed time and the status to PROCESSING. 
		// ------------------------------------------------------------------------
		sw.stop();
		
		this.msgDVO.setProceedTimestamp(new Timestamp(sw.getEndTime()));
		this.msgDVO.setStatus			(SFRMConstant.MSGS_PROCESSING);
		this.msgDVO.setStatusDescription(SFRMConstant.MSGSDESC_PROCESSING);
		mHandle.updateMessage(this.msgDVO);
		
		// Log information.
		SFRMProcessor.getInstance().getLogger().info(
			  SFRMLog.OPT_CALLER 
		   +  SFRMLog.INSERT_SGTS
		   +" msg id: "
		   +  payload.getRefMessageId() 
		   +" have inserted " 
		   +  (numOfSegments - maxSegmentNo)
		   +" sgt with time: " 
		   +  sw.getElapsedTimeInSecond());												
		
		// We are done, terminate this thread.
		this.retryEnabled = false;
	}
	
	/**
	 * Start to monitor the speed of message sending
	 * @throws MalformedURLException 
	 */
	private void markSpeed() throws MalformedURLException{
		SFRMProcessor.getInstance().getLogger().debug("Start monitoring the network speed");
		MessageStatusQueryHandler speedHandler = MessageStatusQueryHandler.getInstance();
		try{
			SFRMProcessor.getInstance().getLogger().debug("Marking the speed msg id: " + msgDVO.getMessageId());
			StatusQuery query = speedHandler.addMessageSpeedQuery(msgDVO.getMessageId());
			query.init();
			query.start();
			query.tick();
		}catch(Exception e){
			SFRMProcessor.getInstance().getLogger().error("Fail to mark the network speed", e);
		}
	}
	
	/**
	 * Execute the active task.
	 * 
	 * @see hk.hku.cecid.piazza.commons.module.ActiveTask#execute()
	 */
	public void execute() throws Exception{
		String pId = payload.getPartnershipId();
		String mId = payload.getRefMessageId();
		createOrFindMessage(mId, pId);
		SFRMProcessor.getInstance().getLogger().debug(SFRMLog.OPT_CALLER + "Status: " + taskStatus);
		if(taskStatus == SFRMConstant.MSGS_PACKAGED){
			handshaking(mId, pId);
		}
		markSpeed();
		segmenting();
	}
	
	
	/**
	 * 
	 */
	public void setRetried(int retried){
		this.currentRetried = retried;
	}		

	/**
	 * 
	 */
	public boolean isRetryEnabled(){
		return this.retryEnabled;
	}	
	
	/**
	 * 
	 */
	public int getMaxRetries() {
		return this.pDVO.getRetryMax();
	}

	/**
	 * 
	 */
	public long getRetryInterval() {
		return this.pDVO.getRetryInterval();
	}
	
	/**
	 * Invoke when failure.
	 */
	public void 
	onFailure(Throwable e) 
	{
		SFRMProcessor.getInstance().getLogger().error(SFRMLog.OPT_CALLER + "Error", e);
		if (!this.retryEnabled || this.currentRetried >= this.getMaxRetries()){
			try {				
				// ---------------------------------------------------------------------
				// Step 0: Update the sfrm message record to be fail and clear the cache
				// ---------------------------------------------------------------------
				SFRMMessageHandler mHandle = SFRMProcessor.getInstance().getMessageHandler();
				if(!this.msgDVO.getStatus().equalsIgnoreCase(SFRMConstant.MSGS_PROCESSING_ERROR)){
					this.msgDVO.setStatus(SFRMConstant.MSGS_PRE_DELIVERY_FAILED);
				}
				this.msgDVO.setStatusDescription(e.toString());
				mHandle.updateMessage(this.msgDVO);
				mHandle.clearCache(this.msgDVO);
				SFRMProcessor.getInstance().getPartnershipHandler().clearCache(
					this.msgDVO.getPartnershipId(), 
					this.msgDVO.getMessageId());						
				// ---------------------------------------------------------------
				// Step 1: Update the payload to pending for restart by users.
				// ---------------------------------------------------------------
				//TODO: Uncomment this when all of the previous bug was corrected
//				this.payload.setToPending();
				this.retryEnabled = false;				
			} 
			catch (Exception ex) {
				SFRMProcessor.getInstance().getLogger().fatal(
					SFRMLog.OPT_CALLER 
				  + "Unable to mark failure for msg: " 
				  + this.msgDVO.getMessageId(), ex); 
			}		
		}			
		else{
			SFRMProcessor.getInstance().getLogger().error(SFRMLog.OPT_CALLER + "Unknown Error", e);
		}
	}
}

