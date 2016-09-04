/**
 * Provides database and message handler and some utility generators at
 * the high level architecture.  
 */
package hk.hku.cecid.edi.sfrm.handler;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.FileChannel;
import java.util.Properties;

import java.util.List;

import java.sql.Timestamp;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.mail.MessagingException;

import org.apache.commons.io.FileUtils;


import hk.hku.cecid.edi.sfrm.spa.SFRMComponent;
import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.edi.sfrm.spa.SFRMLog;
import hk.hku.cecid.edi.sfrm.spa.SFRMLogUtil;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.spa.SFRMProperties;

import hk.hku.cecid.edi.sfrm.pkg.SFRMAcknowledgementBuilder;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessageClassifier;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessageException;
import hk.hku.cecid.edi.sfrm.pkg.SFRMAcknowledgementParser;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDVO;

import hk.hku.cecid.edi.sfrm.handler.SFRMMessageHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMMessageSegmentHandler;
import hk.hku.cecid.edi.sfrm.io.ChecksumException;

import hk.hku.cecid.edi.sfrm.com.FoldersPayload;
import hk.hku.cecid.edi.sfrm.com.PayloadException;
import hk.hku.cecid.edi.sfrm.com.PayloadsRepository;
import hk.hku.cecid.edi.sfrm.com.PayloadsState;
import hk.hku.cecid.edi.sfrm.com.PackagedPayloads;

import hk.hku.cecid.piazza.commons.io.FileSystem;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.module.ActiveMonitor;
import hk.hku.cecid.piazza.commons.module.ActiveThread;
import hk.hku.cecid.piazza.commons.module.ActiveTaskAdaptor;
import hk.hku.cecid.piazza.commons.os.OSManager;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * The incoming message handler is the core class for handling
 * all incoming SFRM segment.<br><br>
 * 
 * It also handles:<br> 
 * <ol>
 * 	<li>Allocation of disk space for HANDSHAKING segment.</li>
 * 	<li>Insertion of data content to specified file for PAYLOAD segment.</li>
 * 	<li>RECEIPT Response handling</li>
 * 	<li>RECOVERY when data integrity check fails.<li> 
 * 	<li>Error Definition and handling<li>
 * </ol>  
 * 
 * For details, read {@link #processIncomingMessage(SFRMMessage, Object[])}
 * as the entry point for knowing how this class work.  
 * 
 * Creation Date: 11/10/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.4 [26/6/2007 - 3/7/2007]
 * @since 	1.0.0
 */
public class IncomingMessageHandler extends SFRMComponent {
		
	// Singleton Handler.
	private static IncomingMessageHandler imh = new IncomingMessageHandler();
			
	// The active thread pool.
	// TODO: Implement a thread pool with priority and thread.
	private ActiveMonitor monitor = new ActiveMonitor();	
	
	// The barrier for providing ONE THREAD working in the segment level.
	private SFRMDoSHandler segmentDoSHandler = new SFRMDoSHandler();
	
	/**
	 * @return an instnace of IncomingMessageHandler.
	 */
	public static IncomingMessageHandler getInstance(){
		return IncomingMessageHandler.imh;
	}
		
	/**
	 * Invoked for initialization.<br><br>
	 * 
	 * The IMH has serval properties : <br>
	 * <ol>
	 * 	<li>maxActive: The number of parallel threads for handling incoming segments. The default value is 10.
	 * 		[Integer] 
	 *	</li>
	 * </ol>
	 */
	protected void init() throws Exception { 
		super.init();
		Properties p = this.getParameters();
		int maxActive = StringUtilities.parseInt(p.getProperty("maxActive"), 10);
		this.monitor.setMaxThreadCount(maxActive);
	}

	/**
	 * Validate whether the partnership for the incoming message 
	 * is exist and return that partnership as return value.<br><br>
	 * 
	 * [SINGLE-THREADED].
	 * 
	 * @param incomingMessage
	 * 			The incoming SFRM message.
	 * @return
	 * 			A SFRM partnership record.
	 * @since
	 * 			1.0.0 
	 * @throws MalformedURLException
	 * 			throw if the partnership does not found or 
	 * 			any other database error. 
	 */
	public SFRMPartnershipDVO 
	extractPartnership(SFRMMessage incomingMessage) 
		throws MalformedURLException 
	{ 
		SFRMPartnershipDVO partnershipDVO = null;
		try {
			// Extract the partnership from the message and the partnership handler.
			partnershipDVO = (SFRMPartnershipDVO) getPartnershipHandler().retreivePartnership(incomingMessage);
			// Check null.
			if (partnershipDVO == null){
				String pID = incomingMessage.getPartnershipId();
				String err = "Missing partnership Information with PID: " + pID;
				getLogger().error(SFRMLog.IMH_CALLER + err);
				throw new MalformedURLException(err);					
			}														
		} catch (Exception e) {
			getLogger().error(SFRMLog.IMH_CALLER + "Partnership check failed: " + incomingMessage, e);
			throw new MalformedURLException("Partnership check failed");
		}
		return partnershipDVO;
	}
			
	/**
	 * Validate whether the incoming segment message has been 
	 * received once.
	 * 
	 * @param incomingMessage
	 * 			The incoming SFRM message.
	 * @return
	 * 			true if it is a duplicated (received already).
	 * @since
	 * 			1.0.1
	 * @throws DAOException
	 * 			throw if there is any kind of database error.
	 */
	public boolean 
	isDuplicateSegment(SFRMMessage incomingMessage) throws DAOException 
	{
		return (getMessageSegmentHandler()
				.retrieveMessageSegment(
					incomingMessage.getMessageID(),
					SFRMConstant.MSGBOX_IN,
					incomingMessage.getSegmentNo(),
					incomingMessage.getSegmentType()) != null); 
	}
	
	/**
	 * Validate whether the associated message 
	 * of this incoming segment is processing.<br><br>
	 * 
	 * Any state except {@link SFRMConstant#MSGS_PROCESSING}
	 * return false for this method invocation.<br><br>
	 *  
	 * If the message record does not exist in receiver,
	 * we treat this as failure because 
	 * every segment should has handshaking steps before
	 * sending. 
	 * 
	 * @param incomingMessage
	 * 			The incoming SFRM message.
	 * @return
	 * 			true if it is processing.
	 * @since
	 * 			1.0.2
	 * @throws DAOException
	 * 			throw if there is any kind of database error.
	 * @throws SFRMMesageException 			
	 */
	public boolean 
	isProcessingMessage(SFRMMessage incomingMessage)
		throws DAOException, SFRMMessageException
	{
		// Retrieve the message record.
		SFRMMessageDVO msgDVO = null;
		SFRMMessageClassifier mc = incomingMessage.getClassifier();
		
		// TODO: 1. Refactoring
		// TODO: 2. Can we use session instead query db each time?
		if (mc.isMeta() || mc.isPayload()){		
			msgDVO = getMessageHandler()
				.retrieveMessage(
					incomingMessage.getMessageID(),
					SFRMConstant.MSGBOX_IN);
		}
		else{
			getLogger().fatal(
				SFRMLog.IMH_CALLER
			  +"Segment Type checked in processing message validation.");
			throw new SFRMMessageException(
			   "Invalid Segment Type: " + incomingMessage.getSegmentType());			   
		}
					
		if (msgDVO != null){			
			// if the status not equal to PR or HS, then the 
			// message is still not considered as failure.
			String status = msgDVO.getStatus();		
			if ( status.equals(SFRMConstant.MSGS_PROCESSING) ||
				 status.equals(SFRMConstant.MSGS_SEGMENTING) ||
				 // FIXME: Hot fix for 26/12/2006 Release
				 status.equals(SFRMConstant.MSGS_PROCESSED)	 ||
				 status.equals(SFRMConstant.MSGS_UNPACKAGING)||
				 status.equals(SFRMConstant.MSGS_HANDSHAKING)||
				 // Added status for defining processing 
				 status.equals(SFRMConstant.MSGS_PRE_SUSPENDED) ||
				 status.equals(SFRMConstant.MSGS_SUSPENDED)
				 )
				return true;
			else
				getLogger().fatal(
					  SFRMLog.IMH_CALLER
				   + "The status of message is not processing."
				   +" It is "  + msgDVO.getStatus()
				   +" due to " + msgDVO.getStatusDescription());
		}		
		return false;
	}
	
	/**
	 * Validate whether the harddisk has enough space for this
	 * message.<br><br>
	 * 
	 * The validation formula is liked this:
	 * <PRE>	
	 * 	pS  : total payload size
	 * 	T   : threshold (the minimum hard disk space)
	 * 	HDDS: the remaining hard disk space
	 * 
	 *	true iff (HDDS >= pS + T)
	 *	false iff (HDDS < ps + T)
	 *  </PRE>
	 * 
	 * @param incomingMessage
	 * 			The incoming SFRM message. 
	 * @param threshold 
	 * 			The remaining disk space threshold. if 
	 * 			the remaining disk space is lower than (this value
	 * 			+ the payload size), in this case, it always return 
	 * 			false. 
	 * @since	
	 * 			1.0.2
	 * @return
	 * 			true if there is enough hard disk space or
	 * 			the associated payloads is created already 
	 * 			in the harddisk. vice versa. 			 			  
	 */
	public boolean 
	isNotEnoughRoom(SFRMMessage incomingMessage, long threshold)
		throws Exception
	{
		// Check whether the payloads has been existed or not. 
		PackagedPayloads pp = (PackagedPayloads) getIncomingRepository().createPayloads
				(new Object[]{incomingMessage.getPartnershipId()
							 ,incomingMessage.getMessageID()}
				,PayloadsState.PLS_UPLOADING);	
		
		if (pp.getRoot().exists())
			return false;
		// FIXME: Performance issue : OSManager.
		// Get how much disk space left from the incoming payload repository.
		long freespace = getOSManager().getCommander().getDiskFreespace(getIncomingRepository().getRepositoryPath());
		long reqspace  = incomingMessage.getTotalSize() + threshold;
		// There is enough space, return false.
		if (freespace > reqspace)
			return false;			
		// Log error.
		getLogger().error(
			  SFRMLog.IMH_CALLER
		   + "Free Diskspace not enough : "
		   +" Remaining "
		   +  freespace
		   +" bytes, requires "
		   +  reqspace
		   +" bytes.");		
		return false;
	}
			
	/**
	 * Unpack the SMIME (secure MIME) message to become raw SFRM Message.<br><br>
	 *   
	 * @param incomingMessage
	 * 			The incoming SFRM Message.
	 * @param partnershipDVO
	 * 			The partnership to valid against to.
	 * @return
	 * 			The raw SFRM Message.  
	 * @since
	 * 			1.0.0
	 * @throws Exception
	 * 			any kind of exceptions.
	 */
	public void unpackIncomingMessage(SFRMMessage message, SFRMPartnershipDVO partnershipDVO) throws SFRMException {
		
		KeyStoreManager keyman = SFRMProcessor.getInstance().getKeyStoreManager();
		
		// Encryption enforcement check and decrypt
		if (partnershipDVO.getEncryptAlgorithm() != null) {
			if (!message.isEncryptedContentType()) {
				SFRMProcessor.getInstance().getLogger().error(
					SFRMLog.IMH_CALLER + "Encryption enforcement check failed: " 
					 + message);
				throw new SFRMException("Insufficient message security");
			} else {
				try {
					message.decrypt(keyman.getX509Certificate(), keyman.getPrivateKey());
				} catch (SFRMException e) {
					SFRMProcessor.getInstance().getLogger().error(
						SFRMLog.IMH_CALLER + "Unable to decrypt " 
						   + message, e);
					throw e;
				} catch (NoSuchAlgorithmException e) {
					SFRMProcessor.getInstance().getLogger().error(
							SFRMLog.IMH_CALLER + "Unable to decrypt " 
							   + message, e);
					throw new SFRMException(e.getMessage(), e);
				} catch (UnrecoverableKeyException e) {
					SFRMProcessor.getInstance().getLogger().error(
							SFRMLog.IMH_CALLER + "Unable to decrypt " 
							   + message, e);
					throw new SFRMException(e.getMessage(), e);
				}
			}
		}
			
		// Signing enforcement check and unpack verified signature
		if (partnershipDVO.getSignAlgorithm() != null) {
			if (!message.isSignedContentType()) {
				SFRMProcessor.getInstance().getLogger().error(
					SFRMLog.IMH_CALLER + "Signature enforcement check failed: "
					   + message);
				throw new SFRMException("Insufficient message security");
				
			} else {
				try {
					message.verify(partnershipDVO.getVerifyX509Certificate());
				} catch (SFRMException e) {
					SFRMProcessor.getInstance().getLogger().error(
						SFRMLog.IMH_CALLER + "Unable to verify "  
							+ message, e);
					throw e;
				}
			}
		}
		
		// Log information
		try {
			SFRMProcessor.getInstance().getLogger().info(
				 SFRMLog.IMH_CALLER + SFRMLog.UNPACK_SGT 
			  + " msg id: " + message.getMessageID() 			    
			  + " with payload size : " 
			  + message.getBodyPart().getSize());
		} catch (MessagingException e) {
			throw new SFRMException("Unable to get body part size");
		}
	}		
	
	
	/**
	 * initalize the <strong>Guard</strong> so that there is <strong>ONLY ONE THREAD</strong>
	 * working per the <code>incomingMessage</code>.
	 * 
	 * @param incomgMessage		The incoming SFRM Message.
	 * @return
	 */
	public boolean 
	initGuardForSegment(final SFRMMessage incomingMessage)
	{		
		if (!segmentDoSHandler.enter(incomingMessage)){
			// Log illegal / duplicated working message at same working time.
			SFRMLogUtil.log(SFRMLog.IMH_CALLER, SFRMLog.ILLEGAL_SGT,
				incomingMessage.getMessageID(), incomingMessage.getSegmentNo());
			getLogger().debug(
				this.segmentDoSHandler.getResolvedKey(incomingMessage));
			return true;
		}
		return false;
	}
	
	/**
	 * <strong>Resolve</strong> the guard for the <code>incomingMessage</code> to 
	 * the new owner (another thread that process the <code>incomingMessage</code>.
	 * 
	 * @param incomingMesasge	The incoming SFRM Message.
	 * @return
	 */
	public boolean
	resolveGuardOwnerForSegment(final SFRMMessage incomingMessage)
	{
		synchronized(this.segmentDoSHandler){			
			if (!this.segmentDoSHandler.exit(incomingMessage)){
				SFRMLogUtil.log(SFRMLog.IMH_CALLER, SFRMLog.RESOLVE_FAIL,
						incomingMessage.getMessageID(), incomingMessage.getSegmentNo());
				return false;
			}			
			this.segmentDoSHandler.enter(incomingMessage);
		}
		return true;
	}
	
	/**
	 * <strong>Release</strong> the ONE THREAD working GUARD for <code>incomingMessage</code>
	 * 
	 * @param incomingMessage	The incoming SFRM Message.
	 * @return
	 */
	public boolean
	releaseGuardForSegment(final SFRMMessage incomingMessage)
	{
		return this.segmentDoSHandler.exit(incomingMessage);
	}
	
	/**
	 * <strong>Process all kind of incoming SFRM message.</strong><br><br>.
	 * 
	 * This method is invoked when the received HTTP request is transformed
	 * to SFRM Message from the SFRM inbound listener.<br><br>
	 * 
	 * @param incomingMessage  	The incoming SFRM Message.
	 * @param params 			RESERVED
	 *  
	 * @return  A SFRM message for response message.
	 * @since  	1.0.0
	 * @version 1.0.1 [26/6/2007 - 03/07/2007]  			
	 * @throws 	Exception
	 */	
	public SFRMMessage 
	processIncomingMessage(SFRMMessage incomingMessage, final Object[] params) 
		throws Exception 
	{
		// constant reference to incomingMessage.
		final SFRMMessage inputMessage = incomingMessage;
		// Verified and decrypted SFRM Message (segment).

		// Get the classifier. 
		final SFRMMessageClassifier mc = incomingMessage.getClassifier();
		// The associated partnership DVO 
		final SFRMPartnershipDVO pDVO;		
		
		SFRMMessage retMessage = null;
		try{
			// Log received Information.
			SFRMLogUtil.log(SFRMLog.IMH_CALLER,	SFRMLog.RECEIVE_SGT, inputMessage.getMessageID(), inputMessage.getSegmentNo());
						
			// ------------------------------------------------------------------------
			// Step 0: Pre-condition process
			//		0.0  - Atomic Thread working Barrier for incoming message.			
			//		0.1  - Extract partnership from DB or session.
			//		0.2  - Unpack (verify and decrypt) the segment.			
			//		0.3  - Handshaking processing [META] 
			// 		0.4  - Message Status validation (true if status == PS,HS,PR,ST,UK) 			
			// ------------------------------------------------------------------------

			// Step 0.0			
			if (this.initGuardForSegment (inputMessage)) return null;
			
			pDVO 		= this.extractPartnership(inputMessage); 			// Step 0.1
			this.unpackIncomingMessage(inputMessage, pDVO); 	// Step 0.2
			
			if (mc.isMeta()) { // Step 0.3
				this.processHandshakingMessage(inputMessage, params);
			}
			else if (mc.isAcknowledgementRequest()){
				retMessage = this.processAcknowledgement(inputMessage, pDVO);
				this.releaseGuardForSegment(inputMessage);
				return retMessage;
			}
			else { // Step 0.4
				boolean isProc = this.isProcessingMessage(inputMessage);
				if (!isProc) throw new SFRMMessageException("Message is not processing, ignore segments.");											
			}										
	
			// ------------------------------------------------------------------------
			// Step 1: Duplicate segment validation. [PAYLOAD ONLY]
			// 
			// 		TECHNICAL DECISION on putting duplication validation at here.
			//		Since hacker may try to get the receipt by pretending a segments
			//		(may be intercept through HTTP monitor). So it is better to  
			//		verify the signature before allowing the receipt can be re-send. 
			// ------------------------------------------------------------------------
			//FIXME: Whether it need to process the duplicate message now, when using sync sfrm message  
			if (mc.isPayload()){
				boolean isDuplicate = this.isDuplicateSegment(inputMessage);
				if(isDuplicate) return null;
			}
			
			// ------------------------------------------------------------------------
			// Step 2: Segment Processing, Dispatch it to a new thread.
			// ------------------------------------------------------------------------
			final IncomingMessageHandler owner = this;
			ActiveThread thread = null;
			try {
				thread = monitor.acquireThread();
				if (thread == null)	return null;
				thread.setTask(new ActiveTaskAdaptor() {
					
					public void execute() throws Exception 
					{
						try{
							// Re-assign the guard for inputMessage to this working thread.
							if (!owner.resolveGuardOwnerForSegment(inputMessage)) return;
							
							// Log spawn thread action.
							SFRMLogUtil.debug(SFRMLog.IMH_CALLER, SFRMLog.SPANNED_THRD, inputMessage.getSegmentType());
							
							if (mc.isPayload()){								
								// Write the segment into pages/disk.
								owner.processSegmentMessage	(inputMessage, params);
							}
							else if (mc.isMeta()){								
								// pre-allocate the payload.
								owner.processMetaMessage	(inputMessage, pDVO, params);
							}
							
						} finally {
							// If everything goes fine, the guard is released by this working thread
							// and the process of this message is considered as completed.
							owner.releaseGuardForSegment(inputMessage);
						}
						// For DEBUG Purpose only
						getLogger().debug(SFRMLog.IMH_CALLER + "Message info" + inputMessage);						
					}
					
					public void 
					onFailure(Throwable e) 
					{
						getLogger().error(SFRMLog.IMH_CALLER + "Error", e); 
					}				
				});
				
				thread.start();
			} 
			catch (Throwable e) {			
				monitor.releaseThread(thread);
			}
		}
		catch(Exception ex){
			// Release the guard when encountering exception.
			this.releaseGuardForSegment(inputMessage);
			throw ex; // Re-throw;
		}
		return retMessage;
	}

	// ------------------------------------------------------------------------
	// Message Processing Method
	// ------------------------------------------------------------------------

		
	/**
	 * Process handshaking for a new message.<br><br>
	 * 
	 * [SINGLE-THREADED].<br><br>
	 * 
	 * The message segment is also <strong>META</strong>
	 * type.<br><br>
	 * 
	 * In the handshaking steps, it create the 
	 * message record and check whether it 
	 * has enough space for receiving the message.<br><br>
	 *  
	 * This method does not block and return 
	 * immediately to let the sender know does the 
	 * receiver is available to receive this message.
	 * 
	 * @param rawMessage
	 * 			The incoming SFRM Message. 
	 * @param params
	 * 			RESERVED.
	 * @return  
	 * 			RESERVED.
	 * @since	
	 * 			1.0.3 			   
	 * @throws DAOException
	 * 			any kind of DB I/O Errors.
	 * @throws Exception
	 * 			thrown when pre-allocate the payload.  			
	 */
	public SFRMMessage 
	processHandshakingMessage(
			SFRMMessage 		rawMessage,
			final Object[] 		params)	throws 
			Exception
	{ 
		// --------------------------------------------------------------------
		// Pre	 : local Variable declaration
        // -------------------------------------------------------------------- 
		String 	mID 		= rawMessage.getMessageID();
		long	totalSize 	= rawMessage.getTotalSize(); 
		String logInfo = " msg id: " 		 + rawMessage.getMessageID()
						+" and total size: " + rawMessage.getTotalSize();
		
		// Log information
		getLogger().info(SFRMLog.IMH_CALLER + SFRMLog.RECEIVE_HDSK + logInfo);		
		// -----------------------------------------------------------------
		// Step 0: create the sfrm message record.
		// -----------------------------------------------------------------
		
		// Retrieve the message record if any.		
		// This query is used for special handling when the sender(S) 
		// is inproperly shutdown after sending the handshaking request
		// to recever. The receiver may have inserted a new Message
		// instance. Then, when the sender re-sends the handshaking 
		// request, the receiver will delete the existing one and 
		// create again. WHY WE DO NOT USE the existing row because
		// we don't assure that the setting of that message is 
		// still identical to the one in the (R) database. like 
		// signing and encryption setting.
		
        SFRMMessageHandler mHandle = getMessageHandler();
        SFRMMessageDVO msgDVO = mHandle.retrieveMessage(mID, SFRMConstant.MSGBOX_IN);        
        
        // Only remove message record when the message is still handshaking.
        if (msgDVO != null &&  msgDVO.getStatus().equals(SFRMConstant.MSGS_HANDSHAKING)){
        	getLogger().info(
        		  SFRMLog.IMH_CALLER
        	   + "Removing existing record with"
        	   +  logInfo);
        	mHandle.removeMessage(msgDVO);
        }
        else 
        if (msgDVO != null && !msgDVO.getStatus().equals(SFRMConstant.MSGS_HANDSHAKING)){
        	getLogger().debug("IncomingMessageHandler msg status from DB: " + msgDVO.getStatus());
        	throw new SFRMMessageException(
				"Message is not handshaking, invalid META segment received.");
        }        
        
        // Get the partnership record. 
        SFRMPartnershipDVO pDVO = this.extractPartnership(rawMessage);
        
        // Create a new message segment record.
        msgDVO = mHandle.createMessageBySFRMMetaMessage(
        		rawMessage,
        		pDVO,
        		SFRMConstant.MSGBOX_IN,
        		SFRMConstant.MSGS_HANDSHAKING,
        		SFRMConstant.MSGSDESC_HANDSHAKING);
        
        try{
    		// -----------------------------------------------------------------
            // Step 1: check how much disk free we left, if not enough,
            //		   throws IOException 
    		// -----------------------------------------------------------------
        	boolean isNotEnoughRoom = this.isNotEnoughRoom(
        		rawMessage, totalSize);		
        	
			if (isNotEnoughRoom)
				throw new IOException("Not enough hdd space to receive this message.");
    		// -----------------------------------------------------------------
            // Step 2: check if the payload exceeding file size limit, if yes
			//		   throws SFRMException
    		// -----------------------------------------------------------------
			long MPSize = SFRMProperties.getMaxPayloadSize(); 
			if (totalSize > MPSize)			
				throw new SFRMException(
					 "Payload Exceeding file size limit: "
				   +  totalSize
				   +" can allow file size under: "
				   +  MPSize);		
							
        } catch(Exception e){
        	getLogger().error(SFRMLog.IMH_CALLER + SFRMLog.FAIL_HDSK + "Reason: ", e); 
        	// Turn the message to fail.
			msgDVO.setStatus			(SFRMConstant.MSGS_DELIVERY_FAILURE);
			msgDVO.setCompletedTimestamp(new Timestamp(System.currentTimeMillis()));
			msgDVO.setStatusDescription	(e.toString());
			mHandle.updateMessage(msgDVO);
			mHandle.clearCache(msgDVO);
			getPartnershipHandler().clearCache(msgDVO.getPartnershipId(), mID);
			throw e;
        }				
		return null;
	}	
	
	/**
	 * Process all meta-typed message segment.<br><br>
	 * 
	 * [MULTI-THREADED].<br><br>
	 * 
	 * This method pre-allocates the payload and it blocks  
	 * until the file has been created.    
	 * 
	 * @param inputMessage
	 * 			The incoming SFRM Message. (unsigned and decrypted)
	 * @param partnershipDVO 
	 * 			The partnership DVO for this incoming message.
	 * @param params
	 * 			RESERVED.
	 * @return  
	 * 			RESERVED.
	 * @since	
	 * 			1.0.0 			   
	 * @throws DAOException
	 * 			any kind of DB I/O Errors.
	 * @throws Exception
	 * 			thrown when pre-allocate the payload.  			
	 */
	public SFRMMessage 
	processMetaMessage(
			SFRMMessage 		inputMessage,
			SFRMPartnershipDVO 	partnershipDVO, 
			final Object[] 		params)	
	{
		// --------------------------------------------------------------------
		// Pre	 : local Variable declaration
        // -------------------------------------------------------------------- 
		String mID = inputMessage.getMessageID();
        // -----------------------------------------------------------------
		// Step 0: create the pre-allocated files for on the fly recv mode.
		// -----------------------------------------------------------------
		PackagedPayloads pp 		= null;
		SFRMMessageHandler mHandle 	= getMessageHandler();
		SFRMMessageDVO msgDVO		= null;
		try{
			pp = (PackagedPayloads) getIncomingRepository()
    			.createPayloads(new Object[]{inputMessage.getPartnershipId(), mID},
    							PayloadsState.PLS_UPLOADING); 			
			File payload = pp.getRoot();
			
			getOSManager().getCommander().createDummyFile(payload.getAbsolutePath(), inputMessage.getTotalSize());
			
			msgDVO = mHandle.retrieveMessage(inputMessage.getMessageID(), SFRMConstant.MSGBOX_IN);
			msgDVO.setStatus(SFRMConstant.MSGS_PROCESSING);
			msgDVO.setStatusDescription(SFRMConstant.getStatusDescription(SFRMConstant.MSGS_PROCESSING));
			msgDVO.setProceedTimestamp(new Timestamp(System.currentTimeMillis()));
			mHandle.updateMessage(msgDVO);		
			
		}catch(Exception ioe){
	    	// --------------------------------------------------------------------
			// Alternative Path: Fail to create the dummy, set the message to DF			
			// --------------------------------------------------------------------
			getLogger().error(
				SFRMLog.IMH_CALLER 
			  + SFRMLog.RECEIVE_FAIL 
			  + "msg id: " 
			  +  mID, ioe);
			
			try{
				msgDVO = mHandle.retrieveMessage(mID, SFRMConstant.MSGBOX_IN);
				msgDVO.setStatus				(SFRMConstant.MSGS_DELIVERY_FAILURE);
				msgDVO.setCompletedTimestamp	(new Timestamp(System.currentTimeMillis()));
				msgDVO.setStatusDescription		(ioe.getMessage());
				mHandle.updateMessage(msgDVO);
				// Clear the cache also.
				if (pp != null)
					pp.clearPayloadCache();
				
				mHandle.clearCache(msgDVO);
				getPartnershipHandler().clearCache(msgDVO.getPartnershipId(), mID);				
			}
			catch(DAOException daoe){
				getLogger().fatal(
					  SFRMLog.IMH_CALLER
				   + "Handshaking Mark failure for msg id: "
				   +  mID);
			}
		}        
		return null;
	}
	
	/**
	 * Process payload-typed segment message.<br><br>
	 * 
	 * What the method has done:<br>
	 * <ul>
	 * 	<li> Create a segment file in the incoming segment repository. </li>
	 * 	<li> Create a inbox message segment record for the incoming message. </li>
	 * </ul>
	 * @param inputMessage 
	 * 			  The packed SFRMMessage.
	 * @param rawMessage
	 *            The unpacked SFRM Message. (i.e. no sign and encrypt here) 
	 * @param params
	 *            RESERVED
	 * 
	 * @return A SFRM message for response message.
	 */
	public SFRMMessage processSegmentMessage(SFRMMessage rawMessage, final Object[] params) throws 
		IOException, DAOException, SFRMMessageException, Exception {

	// TODO: Tuning 
		String mId	= rawMessage.getMessageID();
		String pId	= rawMessage.getPartnershipId();
		String logInfo = "";
		try{						
	        // --------------------------------------------------------------------
			// Step 0: Create the physical payload into incoming segment repostory.
	        // --------------------------------------------------------------------
			String path = getIncomingRepository().getRepositoryPath() + 
							File.separator + "~" + 
							pId + "$" + 
							mId + ".sfrm";
			File payload = new File(path);
	        
	        // --------------------------------------------------------------------        
	        // Step 1: CRC Check
	        // 		   if CRC check fail, return immediately with
	        //		   the negative receipt (recovery request).
	        // --------------------------------------------------------------------	              
			String micValue = rawMessage.digest();			
			
	        getLogger().info("Content Type: " + rawMessage.getBodyPart().getContentType());			
	        
			logInfo	= " msg id: " + mId  + " and sgt no: " + rawMessage.getSegmentNo();
			
			if (!micValue.equalsIgnoreCase(rawMessage.getMicValue())){
				getLogger().info(
					  SFRMLog.IMH_CALLER 
				   +  SFRMLog.FAIL_CRC
				   +  logInfo 
				   +" Expected MIC: " 
				   +  rawMessage.getMicValue() 
				   +" Result MIC: " + micValue);
				throw new ChecksumException("Invalid CRC Value.");
			}else {
				getLogger().info(
					  SFRMLog.IMH_CALLER
				   +  SFRMLog.SUCCESS_CRC
				   +  logInfo);
			}
			
			//Fix for the exception thrown from the windows OS for using NIO approach
			
			OSManager osm = SFRMProcessor.getInstance().getOSManager();
			
			if (osm.getCommander().getOSName().equals("WINDOWS")){	
		        java.io.FileOutputStream fos = new java.io.FileOutputStream(payload, true);
		        FileChannel fc = fos.getChannel();
		        long offset = rawMessage.getSegmentOffset();
		        int length  = (int)rawMessage.getSegmentLength();
		        ReadableByteChannel rbc = Channels.newChannel(rawMessage.getInputStream());
		        fc.transferFrom(rbc, offset, length);
		        fc.force(true);
		        rbc.close();
		        fc.close();
		        fos = null;
		        fc = null;
		        rbc = null;
			} else {
				RandomAccessFile raf = new RandomAccessFile(payload, "rw");
				FileChannel fc 		 = raf.getChannel();		
				long offset = rawMessage.getSegmentOffset();
		        int length  = (int)rawMessage.getSegmentLength();        
		        MappedByteBuffer mbb = fc.map(
		        		FileChannel.MapMode.READ_WRITE
		        	   ,offset
		        	   ,length);
		        mbb.limit(length);
				mbb.position(0);
				InputStream ins = rawMessage.getInputStream();
		        ReadableByteChannel rbc = Channels.newChannel(ins);        
		        rbc.read(mbb);
		        rbc.close(); rbc = null;
		        fc.close();  fc  = null;
		        raf.close(); raf = null;
		        ins.close();
		        mbb = null;         
			}
	        
			// Step 1: Create the message segment record in the database.
	        SFRMMessageSegmentHandler msHandle = 
	        	getMessageSegmentHandler();
	        // Create a new message segment record.
	        msHandle.createMessageSegmentBySFRMMessage(
					rawMessage,
					SFRMConstant.MSGBOX_IN, 
					SFRMConstant.MSGS_DELIVERED);	
		}
		catch(Exception e){
			// Create Recovery Message.
			getLogger().error(SFRMLog.IMH_CALLER + SFRMLog.RECEIVE_FAIL + logInfo, e);
			SFRMMessageSegmentHandler msHandle = 
	        	getMessageSegmentHandler();
			msHandle.createMessageSegmentBySFRMMessage(
					rawMessage,
					SFRMConstant.MSGBOX_IN, 
					SFRMConstant.MSGS_DELIVERY_FAILURE);
		}
        return null;		
	}
	
	public SFRMMessage processAcknowledgement(SFRMMessage msg, SFRMPartnershipDVO pDVO) throws Exception{
		String logInfo	= " msg id: " + msg.getMessageID();
		
		String micValue = msg.digest();			
		
		if (!micValue.equalsIgnoreCase(msg.getMicValue())){
			getLogger().info(SFRMLog.IMH_CALLER + SFRMLog.FAIL_CRC
			   + logInfo +" Expected MIC: " + msg.getMicValue() +" Result MIC: " + micValue);
			throw new ChecksumException("Invalid CRC Value.");
		} else {
			getLogger().info(SFRMLog.IMH_CALLER + SFRMLog.SUCCESS_CRC
			   + logInfo);
		}
				
		
		//Get the acknowledgement content
		String requestContent = new String(IOHandler.readBytes(msg.getContentStream()));
		
		SFRMMessageDVO mDVO = this.getMessageHandler().retrieveMessage(msg.getMessageID(), SFRMConstant.MSGBOX_IN);
		//Walk through the XML content
		SFRMAcknowledgementParser parser = new SFRMAcknowledgementParser(requestContent);
		//Get a message to deal with
		List<String> messageIDs = parser.getMessagesIDs();
		if(messageIDs.size() != 1){
			throw new Exception("Acknowledgement request should contains only one message information");
		}
		String messageId = messageIDs.get(0);
		String messageStatus = parser.getMessageStatus(messageId);
				
		String responseContent = "";
		
		//To dispatch the task base on the message status
		if(messageStatus.equals(SFRMConstant.MSGS_PROCESSING) || messageStatus.equals(SFRMConstant.MSGS_SEGMENTING)){
			responseContent = processPRAck(mDVO, parser);
		}else if(messageStatus.equals(SFRMConstant.MSGS_PRE_DELIVERY_FAILED)){
			responseContent = processPDFAck(mDVO);
		}else if(messageStatus.equals(SFRMConstant.MSGS_PRE_PROCESSED)){
			if(!mDVO.getStatus().equals(SFRMConstant.MSGS_PROCESSED)){
				final String pId = mDVO.getPartnershipId();
				final String mId = mDVO.getMessageId();
				final String filename = mDVO.getFilename();
				completePayload(mId, pId, filename);
			}
			responseContent = processPPSAck(mDVO);
		}else if(messageStatus.equals(SFRMConstant.MSGS_PRE_SUSPENDED)){
			responseContent = processPSDAck(mDVO);
		}else if(messageStatus.equals(SFRMConstant.MSGS_PRE_RESUME)){
			responseContent = processPRSAck(mDVO);
		}else{
			responseContent = "<messages></messages>";
		}
		
		SFRMMessage retMessage = SFRMMessageFactory
					.getInstance()
					.createAcknowledgement(mDVO, pDVO, SFRMConstant.MSGT_ACK_RESPONSE, responseContent);
								
		return retMessage;		
	}
	
	/**
	 * Process the acknowledgement request with PR status
	 * @param messageId message id
	 * @param parser parser that content the acknowledgement request parsing
	 * @return the acknowledgement response content
	 * @throws DAOException if anything fail on database operation
	 */
	public String processPRAck(SFRMMessageDVO mDVO, SFRMAcknowledgementParser parser) throws DAOException{
		SFRMMessageSegmentHandler msHandler = this.getMessageSegmentHandler();
		SFRMAcknowledgementBuilder ackBuilder = new SFRMAcknowledgementBuilder();
		SFRMMessageSegmentDAO msDAO = (SFRMMessageSegmentDAO) msHandler.getDAOInstance();
		List<Integer> segmentNums = parser.getMessageSegmentNums(mDVO.getMessageId());
		//Build a response acknowledgement XML content
		ackBuilder.setMessage(mDVO.getMessageId(), mDVO.getStatus());
		
		if(segmentNums.size() > 0){
			//Query the DB to get a list of message segment for specific message
			List segDVOs = msHandler.retrieveMessages(mDVO.getMessageId(), SFRMConstant.MSGBOX_IN, SFRMConstant.MSGT_PAYLOAD, segmentNums);
			for(int i=0 ; segDVOs.size() > i ; i++){
				SFRMMessageSegmentDVO msDVO = (SFRMMessageSegmentDVO) segDVOs.get(i);
				//If the local message segment status is DL, update the local segment status to PS, and response it with PS
				if(msDVO.getStatus().equals(SFRMConstant.MSGS_DELIVERED)){
					//Update local segment status to PS
					msDVO.setStatus(SFRMConstant.MSGS_PROCESSED);
					msDVO.setCompletedTimestamp(new Timestamp(System.currentTimeMillis()));
					if(!msDAO.persist(msDVO)){
						throw new DAOException("Error when updating the message segment");
					}
					//Response this message segment status to PS
					ackBuilder.setSegment(mDVO.getMessageId(), msDVO.getSegmentNo(), SFRMConstant.MSGS_PROCESSED);
					
				//If the local message segment status is DF, remove the local segment in DB, and response it with DF
				} else if(msDVO.getStatus().equals(SFRMConstant.MSGS_DELIVERY_FAILURE)){
					//Remove the local message segment in DB
					if(!msDAO.remove(msDVO)){
						throw new DAOException("Error when removing the message segment");
					}
					//Response this message segment status to DF
					ackBuilder.setSegment(mDVO.getMessageId(), msDVO.getSegmentNo(), SFRMConstant.MSGS_DELIVERY_FAILURE);
				//If local message segment status is another, just response with the current status
				}else{
					ackBuilder.setSegment(mDVO.getMessageId(), msDVO.getSegmentNo(), msDVO.getStatus());
				}
			}	
		}

		return ackBuilder.toString();
	}
	
	
	/**
	 * Process the acknowledgement with PDF status
	 * @param messageId message ID
	 * @return the acknowledgement response content
	 * @throws DAOException if anything fail on database operation
	 */
	public String processPDFAck(SFRMMessageDVO mDVO) throws DAOException{
		getLogger().debug("Message PDF Ack received");
		return changeAckStaus(mDVO, SFRMConstant.MSGS_DELIVERY_FAILURE);
	}
	
	/**
	 * Process the acknowledgement with PPS status
	 * @param messageId message ID
	 * @return the acknowledgement response content
	 * @throws DAOException if anything fail on database operation
	 */
	public String processPPSAck(SFRMMessageDVO mDVO) throws DAOException{
		getLogger().debug("Message PPS Ack received");
		//TODO: decide whether it need to remove all of the message segment record for this message after
		//message status changed to PS
		return changeAckStaus(mDVO, SFRMConstant.MSGS_PROCESSED);
	}
	
	private boolean completePayload(String messageId, String partnershipId, String filename) throws DAOException, IOException, PayloadException{
		PayloadsRepository inRepo = SFRMProcessor.getInstance().getIncomingRepository();
		PackagedPayloads payload = (PackagedPayloads) inRepo.getPayload(new String[]{partnershipId, messageId}, PayloadsState.PLS_UPLOADING);
		// --------------------------------------------------------
		// Step 1: Create a payload folder for this payload received
		// --------------------------------------------------------
		FoldersPayload dir = payload.getFoldersPayload(inRepo, PayloadsState.PLS_UPLOADING, true);
		
		// --------------------------------------------------------
		// Step 2: Copy the received payload to the payload folder with the filename specified before
		// --------------------------------------------------------
		File outFile = new File(dir.getRoot(), filename);
		FileUtils.moveFile(payload.getRoot(), outFile);
		
		// ------------------------------------------------------------------------		
		// Step 3: Delete the archive payload and rename the dir payload 
		// ------------------------------------------------------------------------
		payload.clearPayloadCache();
		
		// ------------------------------------------------------------------------		
		// Step 4: Rename the folder payload to the finished name format 
		// ------------------------------------------------------------------------
		if (!dir.setToPending()){			
			// TODO: Use the payload repository system.						
			File f = new File(SFRMProcessor.getInstance().getIncomingRepository()
				.getRepositoryPath(),
				dir.getOriginalRootname());			
			
			if (f.exists()){
				getLogger().warn(
					SFRMLog.IPT_CALLER
				   + "Deleting the payload folders with the same name: "
				   +  dir.getOriginalRootname());
				new FileSystem(f).purge();
			}
			dir.setToPending();
		}
		
		return true;
	}
	
	/**
	 * Process the acknowledgement with PSD status
	 * @param messageId message ID
	 * @return the acknowledgement response content
	 * @throws DAOException if anything fail on database operation
	 */
	public String processPSDAck(SFRMMessageDVO mDVO) throws DAOException{
		getLogger().debug("Message PSD Ack received");
		return changeAckStaus(mDVO, SFRMConstant.MSGS_SUSPENDED);
	}
	
	
	
	/**
	 * Process the acknowledgement with PRS status
	 * @param messageId message ID
	 * @return the acknowledgement response content
	 * @throws DAOException if anything fail on database operation
	 */
	public String processPRSAck(SFRMMessageDVO mDVO) throws DAOException{
		getLogger().debug("Message PRS Ack received");
		return changeAckStaus(mDVO, SFRMConstant.MSGS_PROCESSING);
	}
	
	/**
	 * Change the message status accordingly and return the acknowledgement content
	 * @param messageId - message id that need to change the status
	 * @param toStatus - the status to change the message to
	 * @return the acknowledgement response content
	 * @throws DAOException if anything fail on database operation
	 */
	private String changeAckStaus(SFRMMessageDVO mDVO, String toStatus) throws DAOException{
		
		mDVO.setStatus(toStatus);
		mDVO.setStatusDescription(SFRMConstant.getStatusDescription(toStatus));
		
		if(toStatus.equals(SFRMConstant.MSGS_PROCESSED)){
			mDVO.setCompletedTimestamp(new Timestamp(System.currentTimeMillis()));
		}
		this.getMessageHandler().updateMessage(mDVO);		
		SFRMAcknowledgementBuilder builder = new SFRMAcknowledgementBuilder();
		builder.setMessage(mDVO.getMessageId(), mDVO.getStatus());
		return builder.toString();
	}
	
	// ------------------------------------------------------------------------
	// Utility Logger Method
	// ------------------------------------------------------------------------
	
	/**
	 * Log the whole message for debug purpose.
	 * 
	 * @param incomingMessage
	 *            The incoming sfrm message.
	 */
	protected void 
	logMessage(SFRMMessage incomingMessage){
		// For DEBUG Purpose only
        getLogger().debug(
        	 SFRMLog.IMH_CALLER 
          + "msg info" 
          +  incomingMessage);
	}
		
	/**
	 * Log the message type for debug purpose.<br>
	 * <br>
	 * 
	 * The message type currently support for this version is 
	 * META, PAYLOAD ,RECEIPT and RECOVERY.
	 * 
	 * @param type
	 *            The message type.
	 */
	protected void 
	logMessageType(String type){
		// For DEBUG Purpose only
        getLogger().debug(
        	  SFRMLog.IMH_CALLER 
           +  SFRMLog.SPANNED_THRD 
           +" It is a sgt type of: " 
           +  type);        							 
	}
}


