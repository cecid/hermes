/**
 * Contains the set of active module collector for 
 * handling packaging, segmenting of payloads.
 */
package hk.hku.cecid.edi.sfrm.task;

import java.io.IOException;
import java.sql.Timestamp;

import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.activation.DataSource;

import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.edi.sfrm.spa.SFRMLog;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.activation.EmptyDataSource;
import hk.hku.cecid.edi.sfrm.activation.FileRegionDataSource;
import hk.hku.cecid.edi.sfrm.com.PackagedPayloads;

import hk.hku.cecid.edi.sfrm.handler.OutgoingMessageHandler;

import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDVO;

import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessageException;

import hk.hku.cecid.piazza.commons.module.ActiveTaskAdaptor;
// import hk.hku.cecid.piazza.commons.module.ActiveTaskModule;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;

/**
 * 
 * 
 * 
 * Creation Date: 9/10/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.0
 */
public class OutgoingSegmentTask extends ActiveTaskAdaptor {

	/**
	 * The packaged payload.
	 */
	private final PackagedPayloads 		payload;
	
	/**
	 * The sending payload of this tasks.
	 */
	private final SFRMMessageSegmentDVO	segDVO;
	
	/**
	 * The partnership record for the sending message.
	 */
	private final SFRMPartnershipDVO 	pDVO;
	
	/**
	 * The message record for the sending message.
	 */
	private SFRMMessageDVO			msgDVO;

	/**
	 * The current retried time of sending.
	 */
	private int currentRetried;
	
	/**
	 * The flag of message retry flag.
	 */
	private boolean retryEnabled;
	
	/**
	 * The constant field for the suffix of the log. (each thread has different value).
	 */
	private final String SGT_LOG_SUFFIX; 

	/** 
	 * Explicit Constructor.<br><br>
	 * 
	 * @param sgtDVO	
	 * 			The payload need to be send out.
	 * @param pDVO
	 * 			The partnership record associated to this segment.
	 * @param msgDVO 
	 * 			The message record associated to this segment.
	 * @param payload 
	 * 			The packaged payloads
	 * @since	
	 * 			1.0.0 
	 * @throws NullPointerException
	 * 			If the message, partnership and segment is null.
	 */
	public 
	OutgoingSegmentTask(
		SFRMMessageSegmentDVO 	sgtDVO,
		SFRMPartnershipDVO 		pDVO,
		SFRMMessageDVO 			msgDVO,
		PackagedPayloads 		payload)
	{				
		// Error reporting.
		if (pDVO == null)
			throw new NullPointerException("Outgoing Segment Payloads Task: Missing partnership object");				
		if (sgtDVO == null)
			throw new NullPointerException("Outgoing Segment Payloads Task: Missing segment object");		
		if (msgDVO == null)
			throw new NullPointerException("Outgoing Segment Payloads Task: Missing message object");		
		if (payload == null && 
			sgtDVO.getSegmentType().equals(SFRMConstant.MSGT_PAYLOAD)){
			throw new NullPointerException("Outgoing Segment Payloads Task: Missing payload object for msg id: " 
										  + msgDVO.getMessageId());
		}		
		this.pDVO 	 = pDVO;
		this.msgDVO	 = msgDVO;
		this.segDVO	 = sgtDVO;
		this.payload = payload; 
		this.retryEnabled	= (this.pDVO.getRetryMax() > 0);
		this.currentRetried	= this.segDVO.getRetried();
		
		this.SGT_LOG_SUFFIX	= 
			" msg id: " 
		   +  this.segDVO.getMessageId() 
		   +" and sgt no: " 
		   +  this.segDVO.getSegmentNo() 
		   +" and sgt type: " 
		   +  this.segDVO.getSegmentType();
		
		SFRMProcessor.getInstance().getLogger().debug(SGT_LOG_SUFFIX + "being addedd to task list");
	}
		
	/**
	 * Create a SFRM Message for sending.<br><br>
	 * 
	 * The SFRM Message maybe sign, encrypt according 
	 * to the partnership configuration of this message.
	 * 
	 * This is the first steps of this active task.<br><br>
	 * 
	 * @return 
	 * 			A new SFRM message for sending.
	 * @since	
	 * 			1.0.0 
	 * @throws DAOException 			
	 * 			if database I/O Errors.
	 * @throws SFRMMessageException
	 * @throws NoSuchAlgorithmException
	 * 			if the signing and encryption can't be found.
	 * @throws UnrecoverableKeyException 
	 * @throws SMimeException
	 * @throws SFRMException 
	 */
	private SFRMMessage 
	createSFRMMessage() throws 
		IOException, 
		DAOException,
		SFRMMessageException, 
		NoSuchAlgorithmException,
		UnrecoverableKeyException, 
		/*SMimeException, */SFRMException
	{					
		// TODO: Refactoring.
		
		// Check if it is meta message.
		boolean isMeta = this.segDVO.getSegmentNo() == 0 &&
		 				 this.segDVO.getSegmentType().
		 				 equalsIgnoreCase(SFRMConstant.MSGT_META);
		boolean isPayload = this.segDVO.getSegmentType().
		 					equalsIgnoreCase(SFRMConstant.MSGT_PAYLOAD);
		
		// Create return message.
		SFRMMessage sfrmMessage = new SFRMMessage();		
		// Construct the message header. Fill in general information
		sfrmMessage.setMessageID	(this.segDVO.getMessageId());
		sfrmMessage.setPartnershipId(this.msgDVO.getPartnershipId());
		sfrmMessage.setSegmentNo	(this.segDVO.getSegmentNo());		
		sfrmMessage.setSegmentType	(this.segDVO.getSegmentType());
		
		DataSource cacheSource = new EmptyDataSource();
		if (isPayload || isMeta){			
			// Calculate the segment size.
			long size = this.segDVO.getSegmentEnd() - 
						this.segDVO.getSegmentStart();					
			if (isMeta){
				sfrmMessage.setTotalSize(this.msgDVO.getTotalSize());
				sfrmMessage.setTotalSegment(this.msgDVO.getTotalSegment());
			}
			sfrmMessage.setSegmentOffset(this.segDVO.getSegmentStart());	
			sfrmMessage.setSegmentLength(size);
			if (isPayload){		
				cacheSource = new FileRegionDataSource(payload.getRoot(),
						this.segDVO.getSegmentStart(), size);
			}			
		}			
		
		String contentType = payload == null ? SFRMConstant.DEFAULT_CONTENT_TYPE
				: payload.getContentType();
		sfrmMessage.setContent(cacheSource, contentType);
		
		// Create SMIME Header.
		KeyStoreManager keyman = SFRMProcessor.getInstance().getKeyStoreManager();
				
		// Generate checksum value using MD5 Hash algorithm.		
		String mic = "";
		
		// TUNE: 
		if (isPayload){ 						
			mic = segDVO.getMD5Value();
		}			
				
		// Set the mic value and body part to the message.
		sfrmMessage.setMicValue(mic);
		
		// Setup up signing using MD5 or SHA1		
		if (this.msgDVO.getSignAlgorithm() != null){
			SFRMProcessor.getInstance().getLogger().info(SFRMLog.OSPT_CALLER + SFRMLog.SIGNING_SGT + SGT_LOG_SUFFIX);
			sfrmMessage.sign(keyman.getX509Certificate(), keyman.getPrivateKey(), msgDVO.getSignAlgorithm());
			sfrmMessage.setIsSigned(true);			
		}
		
		// Setup up encrypting using RC2, DES
		if (this.msgDVO.getEncryptAlgorithm() != null){
			SFRMProcessor.getInstance().getLogger().info(SFRMLog.OSPT_CALLER + SFRMLog.ENCRYPT_SGT + SGT_LOG_SUFFIX);
			sfrmMessage.encrypt(msgDVO.getPartnerX509Certificate(), msgDVO.getEncryptAlgorithm());
			sfrmMessage.setIsEncrypted(true);
		}		
		return sfrmMessage;		
	}
			
	/**
	 * Send a SFRM Message using Fast HTTP Connector.<br><br>
	 * 
	 * This is step 2 of this active task.<br><br> 
	 * 
	 * @param message
	 * 			The sfrm message to be sent.
	 * @since	
	 * 			1.0.3
	 */
	private void 
	sendSFRMMessage(SFRMMessage message) throws	Exception 
	{						
		OutgoingMessageHandler.getInstance().sendMessage(
				message, msgDVO.getPartnerEndpoint(), msgDVO.getIsHostnameVerified(), 
				msgDVO.getSignAlgorithm(), msgDVO.getEncryptAlgorithm(), msgDVO.getPartnerX509Certificate());
			// Update the payload status to processed if the segment is receipt.	
			this.segDVO.setStatus(SFRMConstant.MSGS_DELIVERED);
			this.segDVO.setProceedTimestamp(new Timestamp(System.currentTimeMillis()));
			SFRMProcessor.getInstance().getMessageSegmentHandler().getDAOInstance().persist(this.segDVO);
			this.retryEnabled = false;
	}
			
	/**
	 * Execute the active task.
	 * 
	 * @since	1.0.0
	 * 
	 * @see hk.hku.cecid.piazza.commons.module.ActiveTask#execute()
	 */
	public void 
	execute() throws Exception 
	{	
		SFRMProcessor.getInstance().getLogger().debug("OutgoingSegmentTask Enter, Thread " + Thread.currentThread().getId());
		// Log information.
		SFRMProcessor.getInstance().getLogger().info(SFRMLog.OSPT_CALLER + SFRMLog.OUTG_TASK + SGT_LOG_SUFFIX);
		// --------------------------------------------------------------------------- 
		// Step 0: Check whether it has exceed the retries times.		
		// ---------------------------------------------------------------------------
		if (this.currentRetried > this.getMaxRetries())
			throw new SFRMMessageException(
				  SFRMLog.OSPT_CALLER 
			   +" this sending segment has exceeding retries time: "
			   +  this.currentRetried
			   +  SGT_LOG_SUFFIX);
		
		// --------------------------------------------------------------------------- 
		// Step 1: Check whether the message has been failed.		
		// ---------------------------------------------------------------------------
		SFRMMessageDVO msgDVO = SFRMProcessor.getInstance().getMessageHandler()
		.retrieveMessage(
			this.segDVO.getMessageId(),
			this.msgDVO.getMessageBox());
	
		if (msgDVO.getStatus().equalsIgnoreCase(SFRMConstant.MSGS_DELIVERY_FAILURE)){
			SFRMProcessor.getInstance().getLogger().info(
				  SFRMLog.OSPT_CALLER 
			   +" Failed msg with msg id:" 
			   +  msgDVO.getMessageId()
			   +" and sgt no: " 
			   +  this.segDVO.getSegmentNo());
			return;
		}
		
		// ---------------------------------------------------------------------------
		// Step 2: create the sfrm message for sending and cache it for retry
		// ---------------------------------------------------------------------------
		// FIXME: use back cache / create each times ?
		SFRMMessage sendMessage = null;
		sendMessage = this.createSFRMMessage();
		// ---------------------------------------------------------------------------
		// Step 3: send the message.
		// ---------------------------------------------------------------------------
		this.sendSFRMMessage(sendMessage);
		sendMessage = null;
	}
	
	/**
	 * Set the retries of active task.<br><br>
	 * 
	 * The parameter <code>retried</code> is useless here
	 * as we use the field "retried" in the database segment  
	 * table for reference.
	 * 
	 * @since	
	 * 			1.0.0
	 * 
	 * @param retried The number of times that has been tried.
	 */
	public void 
	setRetried(int retried) 
	{
		this.currentRetried++;
		try{
			this.segDVO.setRetried(this.currentRetried);
			SFRMProcessor.getInstance().getMessageSegmentHandler().getDAOInstance()
				.persist(this.segDVO);
		}
		catch(DAOException daoe){
			SFRMProcessor.getInstance().getLogger().error("Error in database", daoe);
		}
	}
	
	/**
	 * @since	
	 * 			1.0.0
	 * 
	 * @return return the max retries allowed for this active task.
	 * 
	 */
	public int 
	getMaxRetries() 
	{
		return this.pDVO.getRetryMax();
	}

	/**
	 * @since	
	 * 			1.0.0
	 * 
	 * @return return the interval between each sending retry.
	 */
	public long 
	getRetryInterval() 
	{
		return this.pDVO.getRetryInterval();
	}
	
	/**
	 * @since	
	 * 			1.0.0
	 * 
	 * @return return true if this task can be retried.
	 */
	public boolean 
	isRetryEnabled() 
	{
		return this.retryEnabled;
	}

	/**
	 * The method is invoked upon the task fails to send.<br><br>
	 * 
	 * The message segment and message will treat as FAIL. 
	 * with status DF (Delivery Failure).<br><br>
	 * 
	 * Also, if the outgoing segment is a RECEIPT,
	 * then the PAYLOAD segment corresponding to this 
	 * RECEIPT is also treated as FAIL.
	 * 
	 * @param e
	 * 			The failure cause.
	 * @since	
	 * 			1.0.0  
	 */
	public void 
	onFailure(Throwable e) 
	{
		SFRMProcessor.getInstance().getLogger().error(
			"Error in Outgoing Segmented Payload Task , Retried: " + Integer.toString(this.currentRetried) + " Max retried: " + Integer.toString(this.getMaxRetries()), e);
		// Unrecoverable exception
		if (!this.retryEnabled ||
			 this.currentRetried >= this.getMaxRetries()){
			try {
				// ---------------------------------------------------------------
				// Step 0: Update the sfrm message record to be fail
				// ---------------------------------------------------------------				
				this.msgDVO.setStatus(SFRMConstant.MSGS_PRE_DELIVERY_FAILED);
				this.msgDVO.setStatusDescription(
					"Segment: " + this.segDVO.getSegmentNo() + " has error: " + e.toString());
				this.msgDVO.setCompletedTimestamp(new Timestamp(System.currentTimeMillis()));
				SFRMProcessor.getInstance().getMessageHandler().updateMessage(this.msgDVO);				
				// ---------------------------------------------------------------
				// Step 1: Update the sfrm segment record to fail
				// ---------------------------------------------------------------
				this.segDVO.setStatus(SFRMConstant.MSGS_DELIVERY_FAILURE);
				this.segDVO.setCompletedTimestamp(new Timestamp(System.currentTimeMillis()));
				SFRMProcessor.getInstance().getMessageSegmentHandler().getDAOInstance().persist(this.segDVO);
				// ---------------------------------------------------------------
				// Step 2: clear all the cache.
				SFRMProcessor.getInstance().getMessageHandler().clearCache(this.msgDVO);
				SFRMProcessor.getInstance().getPartnershipHandler().clearCache(
					this.msgDVO.getPartnershipId(), 
					this.msgDVO.getMessageId());
				
				this.retryEnabled = false;
			} catch (Exception ex) {
				SFRMProcessor.getInstance().getLogger().fatal(
					"Unable to mark failure to outgoing SFRM message: "
				   + this.msgDVO.getMessageId(), ex);
			}		
		}
		else{
			SFRMProcessor.getInstance().getLogger().error("Unknown Error", e);
		}
	}
	
	/*
	private void changeSetting(){
		ActiveTaskModule am = (ActiveTaskModule) SFRMProcessor.getInstance().getModuleGroup().getModule("sfrm.outgoing.segment.collector");
		am.getMonitor().setMaxThreadCount(15);
				
	}
	*/
}
