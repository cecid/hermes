/* 
* Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.task;

import hk.hku.cecid.ebms.pkg.Description;
import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.ErrorList;
import hk.hku.cecid.ebms.pkg.SignatureHandler;
import hk.hku.cecid.ebms.pkg.validation.EbxmlMessageValidator;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.MessageServerDAO;
import hk.hku.cecid.ebms.spa.dao.OutboxDAO;
import hk.hku.cecid.ebms.spa.dao.OutboxDVO;
import hk.hku.cecid.ebms.spa.handler.EbxmlMessageDAOConvertor;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandler;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandlerException;
import hk.hku.cecid.ebms.spa.handler.SignalMessageGenerator;
import hk.hku.cecid.ebms.spa.listener.EbmsRequest;
import hk.hku.cecid.ebms.spa.listener.EbmsResponse;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.module.ActiveTask;
import hk.hku.cecid.piazza.commons.security.SMimeMessage;
import hk.hku.cecid.piazza.commons.security.TrustedHostnameVerifier;
import hk.hku.cecid.piazza.commons.soap.SOAPHttpConnector;
import hk.hku.cecid.piazza.commons.soap.SOAPMailSender;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * @author Donahue Sze, Twinsen Tsang (modifiers)
 * 
 */
public class OutboxTask implements ActiveTask {

	// The error message when internal error thrown.
    private static String ERROR_TYPE_INTERNAL_ERROR = "internal_error";

    // The error message when delivery failure (communication protocol error)
    private static String ERROR_TYPE_DELIVERY_FAILTURE = "delivery_failure";

    // The error message when the message is tried to deliver exceeding the retries times.
    private static String ERROR_TYPE_MAXIMUM_RETRIES_REACHED = "maximum_retries_reached";

    /**
     * The DAO for accessing the DVO Format of <code>EbxmlMessage</code>
     */
    private MessageDAO messageDAO;
    
    /**
     * The DVO Format representing <code>EbXML Message</code> needed 
     * to deliver in this task.
     */
    private MessageDVO messageDVO;
    
    /**
     * The flag indicating whether the active task is able to 
     * retry (repeat execution again).
     */
    private boolean retryEnabled = true;
    
    /**
     * The flag indicating whether the <code>EbXML Message</code> requests
     * recipient acknowledgment for reliable messaging.
     */
    private boolean isAckRequested;
    
    /**
     * The flag indicating whether the <code>EbXML Message</code> requested 
     * for processing has been processed by other thread.   
     */
    private boolean isProcessedAlready = false;
    
    /**
     * The number of times which the <code>EbXML Message</code> can be attempted 
     * to deliver to receipient.<br><br/>
     * 
     * It is calculated by maximum retries defined in the partnership agreement
     * for this message and minus 1 plus 1 non-retry delivery attempt.
     */
    private int maxAllowedAttempt = 0;

    /**
     * The number of times attempted to deliver the <code>EbXML Message</code>. This 
     * value should be less than {@link #maxAllowedAttempt}.   
     */
    private int attempted = 0;
       
    /**
     * The number of times that the active task has been repeated to execute. This  
     * value does not relate to the <em>Retries</em> of the <code>EbXML Message</code>,
     * it is just a counter counting how many times the {@link #execute()} has been 
     * invoked in this task. The scope is local and reset after thread terminate. 
     */
    private int localRetried = 0;
    
    /**
     * The agreement validator for the <code>EbXML Message</code> and it's corresponding
     * partnership.
     */
    private AgreementHandler outboxAgreement;
        
    private String errorMessage = null;

    private String contentType;
    
    /** 
     * Explicit Constructor.
     * 
     * @param message 
     */
    public OutboxTask(MessageDVO message){
        this.messageDVO = message;

        // Update the messageDVO status to 'Processing' (PR)
        try {
        	this.messageDAO = (MessageDAO) EbmsProcessor.core.dao.createDAO(MessageDAO.class);
            message.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSING);
            this.messageDAO.updateMessage(message);

            // get the agreement parameters and check by agreement guard
            boolean isOutboundAgreementCheck = MessageServiceHandler.getInstance()
            	.isOutboundAgreementCheck();
            if (isOutboundAgreementCheck) {
                EbmsProcessor.core.log.info("Outbound agreement checking for interop");
                outboxAgreement = new AgreementHandler(message, true);
            } else {
                outboxAgreement = new AgreementHandler(message, false);
            }

            // Get smtp parameters if send by mail
            if (outboxAgreement.getToPartyProtocol().equalsIgnoreCase("mailto")) {
                MessageServiceHandler msh = MessageServiceHandler.getInstance();
                if (!msh.isHasSmtp()) {
                    throw new DeliveryException(
                            "No smtp specified in ebms system properties, cannot delivery msg: "
                                    + message.getMessageId());
                }

                // smtp does not support sync reply
                if (message.getSyncReply().equals("true")) {
                    throw new DeliveryException(
                            "Smtp does not support sync reply, cannot delivery msg: "
                                    + message.getMessageId());
                }
            }

            // reset guard - reset is not allowed if previous msg have not ack
            if (message.getSequenceStatus() == 0) {
                checkResetIsAllow(message);
            }
            
            this.isAckRequested = this.messageDVO.getAckRequested().equals("true");

        } catch (MessageValidationException mve) {
            errorMessage = mve.getMessage();
            EbmsProcessor.core.log.error("Message Validation Exception: "
                    + messageDVO.getMessageId(), mve);
        } catch (Throwable e) {
            errorMessage = "Internal Server Error: " + e;
            EbmsProcessor.core.log.error("Internal Server Error: ", e);
        }

    }
     
    /**
     * Get the maximum execution times for this task. It returns the total
     * number of times that the <code>EbXML Message</code> can be attempted to deliver
     * (including non-retry delivery).<br/><br/>
     * 
     * For <code>EbXML Message</code> does not requests ACK, this method always
     * return zero because it does not support retry / re-sending schema when 
     * ACK does not requested. [ebMSS section 6.4.3]
     *  
     * It is calculated by : <br/>
     * 
     * <em>	Max(maximum retry defined in partnership for this message - 1,0) + 1<em>.   
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#getMaxRetries()
     * @see hk.hku.cecid.ebms.spa.dao.PartnershipDVO#getRetries()
     * @see hk.hku.cecid.ebms.spa.dao.OutboxDVO#getRetried() 
     */
    public int getMaxRetries() 
    {    	
    	if (this.isAckRequested)
    	{    	
	        // Get max retries in the agreement
	        try {
	            OutboxDAO outboxDao = (OutboxDAO) EbmsProcessor.core.dao.createDAO(OutboxDAO.class);
	            OutboxDVO outboxDVO = (OutboxDVO) outboxDao.createDVO();
	            outboxDVO.setMessageId(messageDVO.getMessageId());
	            if (!outboxDao.findOutbox(outboxDVO))	            	
	            {	            	
	            	//throw new NullPointerException("Unable to retrieve the outbox record for :" + messageDVO.getMessageId());
	            	EbmsProcessor.core.log.warn("Redundant working thread for : " + messageDVO.getMessageId());
	            		            	
	            	/*
	            	 * 18/12/2007  Temporary Solution for further guarding the status being overridden by the out-box 
	            	 * task. 
	            	 */
	            	MessageDVO ackDVO = (MessageDVO) messageDAO.createDVO();
	            	ackDVO.setMessageBox	(MessageClassifier.MESSAGE_BOX_INBOX);
	            	ackDVO.setMessageType	(MessageClassifier.MESSAGE_TYPE_ACKNOWLEDGEMENT);
	            	ackDVO.setRefToMessageId(this.messageDVO.getMessageId());
	            	
	            	if (!messageDAO.findRefToMessage(ackDVO))
	            	{
	            		ackDVO.setMessageType(MessageClassifier.MESSAGE_TYPE_ERROR);
	            		
	            		if (!messageDAO.findRefToMessage(ackDVO))
	            		{
	            			// Seems impossible ?	            		
		            		EbmsProcessor.core.log.error(
		            			"Redundant working thread, Cannot find the ACK / Error, Update message to PE:" + 
		            			this.messageDVO.getMessageId());
		            		
		            		this.messageDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED_ERROR);
		            		this.messageDVO.setStatusDescription("Cannot find the ACK / Error.");
	            			this.messageDVO.setTimeoutTimestamp(null);
	            			this.messageDAO.updateMessage(this.messageDVO);
	            		}
	            	}
	            	else
	            	{
	            		EbmsProcessor.core.log.warn(
	            			"Redundant working thread, found associated ACK / Error : " + ackDVO.getMessageId());
	            		
	            		 // Get the ebxml acknowledgment from repository.
	                    EbxmlMessage ebxmlMessage = EbxmlMessageDAOConvertor.getEbxmlMessage(
	            			ackDVO.getMessageId(), MessageClassifier.MESSAGE_BOX_INBOX);
	                    
	                    if (ebxmlMessage.getAcknowledgment() != null)
	                    {
	                    	EbmsProcessor.core.log.warn(
	                    		"Re-update status for message " + 
	                    		this.messageDVO.getMessageId()  + 
	                    		" to " + MessageClassifier.INTERNAL_STATUS_PROCESSED);
	                    	
	                    	// Update back the status
	                    	this.messageDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED);
	                    	this.messageDVO.setStatusDescription("Acknowledgement is received");
	                    	this.messageDVO.setTimeoutTimestamp(null);
							this.messageDAO.updateMessage(this.messageDVO);							
	                    }
	            		else if (ebxmlMessage.getErrorList() != null)
	                    {
	            			EbmsProcessor.core.log.warn(
		                    	"Re-update status for message " + 
		                    	this.messageDVO.getMessageId()  + 
		                    	" to " + MessageClassifier.INTERNAL_STATUS_PROCESSED_ERROR);
	            			
	            			this.messageDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED_ERROR);
	            			this.messageDVO.setTimeoutTimestamp(null);
	    					StringBuffer sb = new StringBuffer();
	    					Iterator i = ebxmlMessage.getErrorList().getErrors();
	    					while (i.hasNext()) 
	    					{
	    						ErrorList.Error error = (ErrorList.Error) i.next();
	    						Description description = error.getDescription();
	    						sb.append(error.getErrorCode() + ": " + description.getDescription());
	    					}
	    					
	    					this.messageDVO.setStatusDescription(sb.toString());	    					
	    					this.messageDAO.updateMessage(this.messageDVO);
	                    }
	            	}	            	
	            	
	            	/* The message is processed already by other thread. */
	            	this.isProcessedAlready = true;	            	
	            	return 0;
	            }
	            // NOTE: Lazy initialization here. 
	            this.maxAllowedAttempt = Math.max(outboxAgreement.getRetries() - 1, 0) + 1;
	            this.attempted = outboxDVO.getRetried();
	        } catch (Throwable t) {
	        	EbmsProcessor.core.log.error("Cannot get the maximum retries", t);
	        }
    	}
        return this.maxAllowedAttempt;
    }
    
    /**
     * @param retried The number of times that this active task has been retried (local retired times). 
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#setRetried(int)
     */
    public void setRetried(int retried) {
    	int delta = retried - this.localRetried;
    	this.attempted 	 += delta;    	
        this.localRetried = retried;            
    }

    /**
     * The main execution of <code>OutboxTask</code>. The overview 
     * procedure is listed in below:
     * <ol>
     * 	<li>Extract the <code>EbXMLMessage</code> from the <code><em>messageDVO</em></code>.</li>
     * 	<li>Sign the <code>EbXMLMessage</code> by it's keystore if necessary.</li>
     * 	<li>Send the <code>EbXMLMessage</code> through HTTP/HTTPS/SMTP protocol.</li>
     * 	<li>Update the number of retry attempted to deliver for this <code>EbXMLMessage</code>.</li>   
     * </ol>
     */
    public void execute() throws Exception 
    {
    	/*
    	 * Return immediately when the message is processed by others already.
    	 */
    	if (this.isProcessedAlready)
    	{
    		this.retryEnabled = false;
    		return ;
    	}
    	
    	// Declare share variable during exeuction.  
    	String mID = messageDVO.getMessageId();
    	
        // Get the ebxml message and sign the message from repository.
        EbxmlMessage ebxmlMessage = EbxmlMessageDAOConvertor.getEbxmlMessage(
			mID, MessageClassifier.MESSAGE_BOX_OUTBOX);

        // Create MessageServer DAO object.
        MessageServerDAO messageServerDAO = (MessageServerDAO) EbmsProcessor.core.dao
        	.createDAO(MessageServerDAO.class);
                
        try {
            checkAndSignEbxmlMessage(ebxmlMessage);
        } catch (MessageValidationException mve) {
            EbmsProcessor.core.log.error("Cannot get the sign the message: ", mve);
            errorMessage = mve.getMessage();
        }

        // Error occurred when constructing the outbox messageDVO or sign the EbXML message.  
        if (errorMessage != null) {            
            try {
                EbmsProcessor.core.log.info("Mark as failed (Message id: " + mID + ")");                
                messageDVO.setStatus(MessageClassifier.INTERNAL_STATUS_DELIVERY_FAILURE);
                messageDVO.setStatusDescription(errorMessage);
                messageServerDAO.clearMessage(messageDVO);
                generateErrorMessage(ERROR_TYPE_INTERNAL_ERROR);
            } catch (DAOException daoe) {
                EbmsProcessor.core.log.error("Cannot generate error message", daoe);
            }
            retryEnabled = false;
            throw new DeliveryException(errorMessage);
        }
               
        /*
         * Do some post operation when acknowledgment is required. 
         */
        if (this.isAckRequested)
        {
        	/*
             * The sender task does not receive an Acknowledgment Message after the maximum number of retries,
             * mark it as failure to receive due to maximum retries has been reached. [ebMSS section 6.5.4]  
             */
        	if ((attempted >= maxAllowedAttempt))
        	{        	
	        	this.messageDAO.findMessage(this.messageDVO); // Update it to latest from the persistence DB.
	        	
	       		if (this.checkUpdateStatusIsAllow()){       
	       			// TODO: The operations below is not atomic and may have race condition between the incoming thread.
	       			EbmsProcessor.core.log.info("Reliable message (" + mID
	       				+ ") - no acknowledgement received until maximum retries");
	       			try {
		        		EbmsProcessor.core.log.info("Mark as failed (Message id: " + mID + ")");
						this.messageDVO.setStatus(MessageClassifier.INTERNAL_STATUS_DELIVERY_FAILURE);
						this.messageDVO.setTimeoutTimestamp(null);
						messageServerDAO.clearMessage(messageDVO);
						generateErrorMessage(ERROR_TYPE_MAXIMUM_RETRIES_REACHED);
					} catch (DAOException e) {
						EbmsProcessor.core.log.error("Cannot generate error message", e);
					}				
	       		} else {
	       			EbmsProcessor.core.log.warn("Reliable message (" + mID
	       				+ ") - Redundant working thread is now terminated");  
	       		}       		
	       		this.retryEnabled = false;
				return;	// retrun immediately and terminate the thread.
			}        	
        	/*
        	 * It may be very dangerous when updates the message after delivery because 
        	 * it might have two thread (incoming ACK and outbox task) updating status together.
        	 */
        	try{
            	// Update the timeout timestamp for re-sending if necessary [ebMS_v2_0 section 6.5.4]            	
            	this.messageDAO.findMessage(this.messageDVO); // Update it to latest from the persistence.           		
           		/* 
           		 * Only need to set if the message has not reached final status.
           		 * There is a very small possibility that the acknowledgment has been 
           		 * received and processed in between the Order message delivery and here.
           		 * So we need to track whether the Order message has been processed before
           		 * setting the timeout timestamp. 
           		 */
            	if (this.checkUpdateStatusIsAllow()){
           			Timestamp timeout = new Timestamp(System.currentTimeMillis() + this.getRetryInterval());
           			this.messageDVO.setTimeoutTimestamp(timeout);
           			this.messageDAO.updateMessage(this.messageDVO);
           		}            	            	
            } catch (DAOException daoe){
            	String detail = "Cannot update the timeout timestamp (Message id: " + mID + ")";
            	EbmsProcessor.core.log.error(detail, daoe);
                throw new DeliveryException (detail, daoe);
            }            
        }
        
        Exception protocolCommErr = null; 
        
        // Send the ebxml message 
        try {
        	String protocol = outboxAgreement.getToPartyProtocol(); 
        	URL url 		= outboxAgreement.getToPartyURL();        	
        	// Send EbXML Message By HTTP/HTTPS Protocol
            if (protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("https")){                        	
                sendMsgByHttp(url, messageDVO.getSyncReply().equals("true"), ebxmlMessage);
            } else
           	// Send EbXML Message By SMTP Protocol            	
            if (protocol.equalsIgnoreCase("mailto")){
                sendMsgBySmtp(url, ebxmlMessage, contentType);
            // Unknown protocol.
            } else {
            	throw new DeliveryException("Unknown protocol (" + protocol + ") for delivery.");
            }            	
        } catch (DeliveryException de){            
            protocolCommErr = de;            
        }

        if (!this.isAckRequested)
        {
        	String toStatus = null;
        	String toStatusDesc = null;        	
        	
        	/*
             * According to the EbMS specification, there is no send retry / duplication
             * schema when no acknowledgment is requested. Mark the messsage record 
             * to delivery failure (DF) immediately and generate the appropriate error 
             * message when there is error in delivery.
             */
        	if (protocolCommErr != null){
        		EbmsProcessor.core.log.info("Mark as failed (Message id: " + mID + ")");
        		toStatus = MessageClassifier.INTERNAL_STATUS_DELIVERY_FAILURE;
        		toStatusDesc = "Delivery Failure when sending message: "
       				+ StringUtilities.toString(protocolCommErr);
        		// Generate the error before mark failure.
        		generateErrorMessage(ERROR_TYPE_DELIVERY_FAILTURE);
        	}
            /*
             * If the EbXML message does not request ACK and no connection exception thrown
             * encountered. We assume it is delivered successfully and mark it to (DL).
             */
        	else {
        		toStatus = MessageClassifier.INTERNAL_STATUS_DELIVERED;
        		toStatusDesc = "Message was sent.";
        	}
        	// Update the status and clear the message.
        	try{
        		this.messageDVO.setStatus(toStatus);
                this.messageDVO.setStatusDescription(toStatusDesc);
                messageServerDAO.clearMessage(this.messageDVO);
        	} catch (DAOException daoe){
            	String detail = "Error in clear the non-reliable message: " + mID;  
                EbmsProcessor.core.log.error(detail, daoe);
                throw new DeliveryException (detail, daoe);
            }
        }        
        // If the EbXML message request ACK, update the retried count and timeout time in the outbox. 
        else if (this.isAckRequested)
        {           	
        	try { 
            	// TODO: Refactor (prevent create outboxDAO object)
                // Update the retries count
                OutboxDAO outboxDao = (OutboxDAO) EbmsProcessor.core.dao.createDAO(OutboxDAO.class);
                OutboxDVO outboxDVO = (OutboxDVO) outboxDao.createDVO();
                outboxDVO.setMessageId(mID);            
                outboxDVO.setRetried(this.attempted + 1);
                outboxDao.updateOutbox(outboxDVO);
            } catch (DAOException daoe){
            	String detail = "Cannont update the retires count (Message id: " + mID + ")";
                EbmsProcessor.core.log.error(detail, daoe);
                throw new DeliveryException (detail, daoe);
            }           
        }
        
        // Message sent event is fired already in sendMsgByHttp for sync reply
        if (messageDVO.getSyncReply().equals("false") && null == protocolCommErr) {
			fireMessageSentEvent(ebxmlMessage);
        }
		
        // Terminate after this execution.
    	this.retryEnabled = false;        
        // Some problem has been captured when delivering ebxml message. throw it and handle by the active task.
        if (protocolCommErr != null) throw protocolCommErr;
    }

    /**
     * Sign the <code>EbXML Message</code> by the private key storing in the 
     * <code>KeyStoreManager</code> if the agreement is requested signing the 
     * <code>EbXML Message</code>  
     * 
	 * @param ebxmlMessage The <code>EbXML Message</code> needed to sign. 	
	 * @throws MessageValidationException
	 * 			Unable to sign the <code>ebxmlMessage</code>. 			
	 */
    private void checkAndSignEbxmlMessage(EbxmlMessage ebxmlMessage) throws MessageValidationException {
        try {
            MessageClassifier messageClassifier = new MessageClassifier(ebxmlMessage);

            // sign the order message
            if (messageClassifier.getMessageType().equalsIgnoreCase(
                    MessageClassifier.MESSAGE_TYPE_ORDER)) {
                boolean isSign = outboxAgreement.isSign();
                if (isSign){
                    EbmsProcessor.core.log.info("Sign the message: " + ebxmlMessage.getMessageId());
                    String dsAlgorithm = outboxAgreement.getDsAlgorithm();
                    String mdAlgorithm = outboxAgreement.getMdAlgorithm();
                    SignatureHandler signatureHandler = MessageServiceHandler
                            .createSignatureHandler(ebxmlMessage);
                    if (MessageServiceHandler.getInstance().isSignHeaderOnly()) {
                        EbmsProcessor.core.log.info("Sign header only for interop");
                        signatureHandler.sign(dsAlgorithm, mdAlgorithm, true);
                    } else {
                        signatureHandler.sign(dsAlgorithm, mdAlgorithm, false);
                    }
                }
            }
        } catch (Exception e) {
        	String detail = "Cannot sign the ebxml message";
//            EbmsProcessor.core.log.error(detail, e);
            throw new MessageValidationException(detail, e);
        }
    }

    /**
     * Deliver the <code>EbXML Message</code> through HTTP protocol.
     * 
     * @param link
     */
    private void sendMsgByHttp(URL link, boolean isSyncReply, EbxmlMessage ebxmlMessage) throws DeliveryException {
        EbmsProcessor.core.log.info("Send message " + messageDVO.getMessageId() + " to " + link);

        HttpURLConnection conn = null;
        SOAPMessage reply = null;

        try {
            SOAPHttpConnector connector = new SOAPHttpConnector(link);
            if (!outboxAgreement.isHostnameVerified()) {
                connector.setHostnameVerifier(new TrustedHostnameVerifier());
            }
            conn = connector.createConnection();
            conn.setDoOutput(true);
            reply = connector.send(ebxmlMessage.getSOAPMessage(), conn);
        } catch (Exception e) {
        	throw new DeliveryException("Cannot send the message", e);
        }
        
        // If it requests sync reply
        if (isSyncReply) {            
            try {
            	fireMessageSentEvent(ebxmlMessage);
            	
				EbxmlMessage replyMsg = new EbxmlMessage(reply);;
            
            	EbmsProcessor.core.log.info("Store incoming sync reply message");

            	// Philip 20081116 
            	// in case sync reply = true and ack requested = false
            	// this is not allowed in real situation
                try {
                    EbxmlMessageValidator validator = new EbxmlMessageValidator();
                    validator.validate(replyMsg);
                } catch (Exception e) {
                    EbmsProcessor.core.log.info("Reply message is not a valid ebxml message");
                    return;
                }

                EbmsRequest ebmsRequest = new EbmsRequest();
                ebmsRequest.setMessage(replyMsg);

                EbmsResponse ebmsResponse = new EbmsResponse();
                EbxmlMessage ebxmlResponseMessage = new EbxmlMessage();
                ebmsResponse.setMessage(ebxmlResponseMessage);

                MessageServiceHandler msh = MessageServiceHandler.getInstance();
                msh.processInboundMessage(ebmsRequest, ebmsResponse);
                
            } catch (Exception e) {
                EbmsProcessor.core.log.error("Cannot convert the reply message", e);
                throw new DeliveryException("Cannot convert the reply message", e);
            }
        }
    }

    private void sendMsgBySmtp(URL toPartyURL, EbxmlMessage ebxmlMessage,
            String contentType) throws DeliveryException, CertificateException,
            IOException {

        MessageServiceHandler msh = MessageServiceHandler.getInstance();

        String toMailAddress = toPartyURL.getPath();
        EbmsProcessor.core.log.info("Send message " + messageDVO.getMessageId()
                + " to " + toMailAddress);

        SOAPMailSender smtp = new SOAPMailSender(msh.smtpProtocol,
                msh.smtpHost, msh.smtpUsername, msh.smtpPassword);

        if (!msh.smtpPort.equalsIgnoreCase("")) {
            smtp.addProperty("mail.smtp.port", msh.smtpPort);
        }

        try {
            Session session = smtp.createSession();

            MimeMessage message = smtp.createMessage(msh.smtpFromMailAddress,
                    toMailAddress, null, "ebxml",
                    ebxmlMessage.getSOAPMessage(), session);
            
            message.setHeader(EbxmlMessage.SOAP_ACTION, EbxmlMessage.SOAP_ACTION_VALUE);            

            if (outboxAgreement.isEncrypt()) {
                EbmsProcessor.core.log.info("Encrypt the message");

                X509Certificate serverCert = null;
                if (outboxAgreement.getEncryptCert() != null) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(
                            outboxAgreement.getEncryptCert());
                    CertificateFactory cf = CertificateFactory
                            .getInstance("X.509");
                    serverCert = (X509Certificate) cf.generateCertificate(bais);
                    bais.close();
                    bais = null;
                } else {
                    EbmsProcessor.core.log.error("Please upload the cert");
                    throw new RuntimeException("Please upload the cert");
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                message.writeTo(baos);
                MimeBodyPart mimeBodyPart = new MimeBodyPart(
                        new ByteArrayInputStream(baos.toByteArray()));
                baos.close();

                SMimeMessage smsg = new SMimeMessage(mimeBodyPart, serverCert,
                        session);

                // set encryption algorithm
                if (outboxAgreement.getEncryptAlgorithm() != null) {
                    smsg.setEncryptAlgorithm(outboxAgreement
                            .getEncryptAlgorithm());
                }

                MimeBodyPart bp = smsg.encrypt().getBodyPart();
                message.setContent(bp.getContent(), bp.getContentType());
            }
            smtp.send(message);
            
        } catch (Exception e) {
            EbmsProcessor.core.log.error("Cannot send the message", e);
            throw new DeliveryException("Cannot send the message", e);
        }
    }

    /**
     * Invoke when {@link #execute()} throw any kind of uncaught exception.
     */
    public void onFailure(Throwable t) {
        EbmsProcessor.core.log.error("Error in outbox task", t);
    }
    
    public boolean isSucceedFast(){
        return this.isAckRequested;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#isRetryEnabled()
     */
    public boolean isRetryEnabled() {
        return retryEnabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#getRetryInterval()
     */
    public long getRetryInterval() {
        // get retry interval in agreement
        long retryInterval = 1000;
        try {
            retryInterval = new Long(outboxAgreement.getRetryInterval()).longValue();
        } catch (Throwable e) {
        }
        return retryInterval;
    }
    
    public void onAwake() {        
    }
    
    

    /**
     * @param description
     */
    private void generateErrorMessage(String failureType) {
        // get the ebxml message and sign the message from repository
        EbxmlMessage ebxmlMessage = null;
        try {
            ebxmlMessage = EbxmlMessageDAOConvertor.getEbxmlMessage(messageDVO
                    .getMessageId(), MessageClassifier.MESSAGE_BOX_OUTBOX);
        } catch (MessageValidationException mve) {
            EbmsProcessor.core.log.error("Cannot get the ebxml message: "
                    + messageDVO.getMessageId(), mve);
            try {
                ebxmlMessage = new EbxmlMessage();
            } catch (SOAPException e1) {
                EbmsProcessor.core.log.error("Cannot new the ebxml message: "
                        + messageDVO.getMessageId(), e1);
            }
        }

        try {
            EbxmlMessage errorEbxmlMessage = null;
            if (failureType.equalsIgnoreCase(ERROR_TYPE_INTERNAL_ERROR)) {
                EbmsProcessor.core.log.info("Generate internal error message");
                // messageDVO error, agreement error or repository error
                // when initiation
                errorEbxmlMessage = SignalMessageGenerator
                        .generateErrorMessageBySender(ebxmlMessage,
                                ErrorList.CODE_UNKNOWN,
                                ErrorList.SEVERITY_ERROR, errorMessage, null);
            } else 
            if (failureType.equalsIgnoreCase(ERROR_TYPE_DELIVERY_FAILTURE)) {
                EbmsProcessor.core.log.info("Generate delivery failure error message");
                // delivery failure if no error messageDVO
                errorEbxmlMessage = SignalMessageGenerator
                        .generateErrorMessageBySender(ebxmlMessage,
                                ErrorList.CODE_DELIVERY_FAILURE,
                                ErrorList.SEVERITY_ERROR, "Delivery failure",
                                null);
            } else 
            	if (failureType.equalsIgnoreCase(ERROR_TYPE_MAXIMUM_RETRIES_REACHED)) {
                EbmsProcessor.core.log.info("Generate delivery failure error message");
                // delivery failure if no error messageDVO
                errorEbxmlMessage = SignalMessageGenerator
                        .generateErrorMessageBySender(ebxmlMessage,
                                ErrorList.CODE_DELIVERY_FAILURE,
                                ErrorList.SEVERITY_ERROR,
                                "Delivery failure - Maximum retries reached",
                                null);
            }
            storeIncomingMessage(errorEbxmlMessage);
        } catch (SOAPException e) {
            EbmsProcessor.core.log.error("Cannot generate error message", e);
        } catch (MessageServiceHandlerException e) {
            EbmsProcessor.core.log.error("Cannot store incoming message", e);
        }
    }
   
    /**
     * Check whether the status of the current processing <code>EbxmlMessage</code>
     * is allowed to update to next status.
     */
    private boolean checkUpdateStatusIsAllow()
    {
    	if (this.messageDVO == null) return false;    	
    	
    	String status = this.messageDVO.getStatus();
    	
    	return !status.equalsIgnoreCase(MessageClassifier.INTERNAL_STATUS_DELIVERED) || 
    		   !status.equalsIgnoreCase(MessageClassifier.INTERNAL_STATUS_DELIVERY_FAILURE) ||
    		   !status.equalsIgnoreCase(MessageClassifier.INTERNAL_STATUS_PROCESSED) ||
    		   !status.equalsIgnoreCase(MessageClassifier.INTERNAL_STATUS_PROCESSED_ERROR);
    }
    
    private void checkResetIsAllow(MessageDVO sequenceResetMessage)
            throws DAOException, MessageValidationException {
        // this function will throw exception when corresponding outbox
        // messageDVO
        // acknowledgement did not exist
        MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                .createDAO(MessageDAO.class);
        MessageDVO finder = (MessageDVO) messageDAO.createDVO();
        finder.setMessageBox(MessageClassifier.MESSAGE_BOX_OUTBOX);
        finder.setCpaId(sequenceResetMessage.getCpaId());
        finder.setService(sequenceResetMessage.getService());
        finder.setAction(sequenceResetMessage.getAction());
        finder.setConvId(sequenceResetMessage.getConvId());
        finder.setStatus(MessageClassifier.INTERNAL_STATUS_DELIVERY_FAILURE);
        // find all the failed outbox sequence messageDVO using cpa
        List failedList = messageDAO
                .findOrderedMessagesByMessageBoxAndCpaAndStatus(finder);
        if (failedList.size() != 0) {
            throw new MessageValidationException(
                    "Reset is not allowed as previous msg delivery failed");
        }
        finder.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSING);
        // find all the processing outbox sequence messageDVO using cpa
        List processingList = messageDAO
                .findOrderedMessagesByMessageBoxAndCpaAndStatus(finder);
        // the number of processing sequence msg more than 1
        if (processingList.size() > 1) {
            throw new MessageValidationException(
                    "Reset is not allowed as previous msg has not acknowledged");
        }
        finder.setStatus(MessageClassifier.INTERNAL_STATUS_PROCESSED_ERROR);
        // find all the processing outbox sequence messageDVO using cpa
        List processedErrorList = messageDAO
                .findOrderedMessagesByMessageBoxAndCpaAndStatus(finder);
        // the number of processing sequence msg more than 1
        if (processedErrorList.size() > 1) {
            throw new MessageValidationException(
                    "Reset is not allowed as previous msg has error");
        }
    }

    private void storeIncomingMessage(EbxmlMessage ebxmlMessage)
            throws MessageServiceHandlerException {
        MessageClassifier messageClassifier = new MessageClassifier(ebxmlMessage);
        EbxmlMessageDAOConvertor message = new EbxmlMessageDAOConvertor(
				ebxmlMessage, MessageClassifier.MESSAGE_BOX_INBOX,
				messageClassifier.getMessageType());
        
        try {
            MessageDVO messageDVO = message.getMessageDVO();
            messageDVO.setStatus(MessageClassifier.INTERNAL_STATUS_PENDING);
            MessageServerDAO messageServerDAO = (MessageServerDAO) EbmsProcessor.core.dao
                    .createDAO(MessageServerDAO.class);
            messageServerDAO.storeMessage(messageDVO, message.getRepositoryDVO());
        } catch (DAOException daoe) {
        	String detail = "Error in storing incoming message.";
            EbmsProcessor.core.log.error(detail, daoe);
            throw new MessageServiceHandlerException(detail, daoe);
        }
    }
    
    private void fireMessageSentEvent(EbxmlMessage ebxmlMessage) {
    	MessageClassifier messageClassifier = new MessageClassifier(ebxmlMessage);
		String messageType = messageClassifier.getMessageType();
		if (MessageClassifier.MESSAGE_TYPE_ORDER.equalsIgnoreCase(messageType)) {
	    	EbmsEventModule eventModule = (EbmsEventModule) EbmsProcessor
				.getModuleGroup().getModule(EbmsEventModule.MODULE_ID);
	    	eventModule.fireMessageSent(ebxmlMessage);
		}
    }
    

    /*
    private String findPrincipalId(EbxmlMessage ebxmlRequestMessage) {
        // first - from ref to messageDVO
        // second - from receiver channel

        if (ebxmlRequestMessage.getAcknowledgment() != null) {
            // if it contains reference messageDVO id
            try {
                MessageDAO messageDAO = (MessageDAO) EbmsProcessor.core.dao
                        .createDAO(MessageDAO.class);
                MessageDVO messageDVO = (MessageDVO) messageDAO.createDVO();
                messageDVO.setMessageId(ebxmlRequestMessage.getMessageHeader()
                        .getRefToMessageId());
                messageDVO.setMessageBox(MessageClassifier.MESSAGE_BOX_INBOX);
                if (messageDAO.findMessage(messageDVO)) {
                    return messageDVO.getPrincipalId();
                }
            } catch (DAOException e) {
                EbmsProcessor.core.log.error("Error in finding principal id", e);
            }
        }
        // search in receiver channel
        try {
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
                    .createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO
                    .createDVO();
            partnershipDVO.setCpaId(ebxmlRequestMessage.getCpaId());
            partnershipDVO.setService(ebxmlRequestMessage.getService());
            partnershipDVO.setAction(ebxmlRequestMessage.getAction());
            partnershipDAO.findPartnershipByCPA(partnershipDVO);
            return partnershipDVO.getPrincipalId();
        } catch (DAOException e) {
            EbmsProcessor.core.log.error("Error in finding principal id", e);
        }
        return "nobody";
    }
	*/
   
}