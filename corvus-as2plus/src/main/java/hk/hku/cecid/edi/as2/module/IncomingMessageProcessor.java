/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.module;

import hk.hku.cecid.edi.as2.AS2Exception;
import hk.hku.cecid.edi.as2.dao.AS2DAOHandler;
import hk.hku.cecid.edi.as2.dao.MessageDAO;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.edi.as2.dao.RepositoryDAO;
import hk.hku.cecid.edi.as2.dao.RepositoryDVO;
import hk.hku.cecid.edi.as2.pkg.AS2Header;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.edi.as2.pkg.Disposition;
import hk.hku.cecid.edi.as2.pkg.DispositionNotification;
import hk.hku.cecid.piazza.commons.module.SystemComponent;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.security.SMimeMessage;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;

import java.io.InputStream;


/**
 * IncomingMessageProcessor
 * 
 * @author Hugo Y. K. Lam
 * 
 *
 */
public class IncomingMessageProcessor extends SystemComponent {
	
	private static final String KEYSTORE_MANAGER = "keystore-manager";

    public void processReceipt(AS2Message receipt) throws AS2Exception {
        try {
            getLogger().info(receipt + " received");

            if (receipt.getMessageID() == null) {
                receipt.setMessageID(AS2Message.generateID());
            }
            
            DispositionNotification dn;
            try {
                dn = receipt.getDispositionNotification();
            }
            catch (Exception e) {
                throw new AS2Exception("Invalid disposition notification\n"+new String(receipt.toByteArray()), e);
            }
            
            AS2DAOHandler daoHandler = new AS2DAOHandler(getDAOFactory());
            PartnershipDVO partnership = daoHandler.findPartnership(receipt, true);
            MessageDAO messageDAO = daoHandler.createMessageDAO();
            MessageDVO originalMessageDVO = (MessageDVO)messageDAO.createDVO();
            originalMessageDVO.setMessageId(dn.getOriginalMessageID());
            originalMessageDVO.setMessageBox(MessageDVO.MSGBOX_OUT);
                        
            if (!messageDAO.retrieve(originalMessageDVO) || !originalMessageDVO.isReceiptRequested()) {
                throw new RequestListenerException("Unexpected disposition notification for message " + dn.getOriginalMessageID());
            }
            if (originalMessageDVO.isAcknowledged()) {
                throw new RequestListenerException("Duplicated disposition notification for message " + dn.getOriginalMessageID());
            }
            
            SMimeMessage smime = new SMimeMessage(receipt.getBodyPart(), partnership.getEffectiveVerifyCertificate());
            
            if (originalMessageDVO.getMicValue() != null) {
                try {
                    if (smime.isSigned()) {
                        try {
                            smime = smime.verify();
                        }
                        catch (Exception e) {
                            throw new AS2Exception("Signature verification failed", e);
                        }
                    }
                    else {
                        throw new AS2Exception("Signed receipt is required.");
                    }
                }
                catch (Exception e) {
                    AS2Exception t = new AS2Exception("Receipt ("+receipt.getMessageID()+") rejected for security reason. Original message: "+dn.getOriginalMessageID(), e);
                    originalMessageDVO.setStatusDescription(t.toString());
                    daoHandler.createMessageDAO().persist(originalMessageDVO);
                    throw t;
                }
                
                if (!dn.matchOriginalContentMIC(originalMessageDVO.getMicValue())) {
                    getLogger().warn("Message Integrity Check failed - Original Message: " + originalMessageDVO.getMessageId() + ", Original MIC: " + originalMessageDVO.getMicValue() + ", Recevied MIC: " +  dn.getReceivedContentMIC());
                }
                else {
                    getLogger().info("Message Integrity Check succeeded - Original Message: " + originalMessageDVO.getMessageId());
                }
            }
            
            Disposition disposition = dn.getDisposition();
            try {
                disposition.validate();
            }
            catch (Exception e) {
                getLogger().warn("Message " + originalMessageDVO.getMessageId() + " was sent but the receipt indicated an error occurred", e);
            }
            
            /* Persist the receipt message */
            RepositoryDVO replyRepositoryDVO = daoHandler.createRepositoryDVO(receipt, true);
            MessageDVO replyMessageDVO = daoHandler.createMessageDVO(receipt, true);
            
            replyMessageDVO.setStatus(MessageDVO.STATUS_PROCESSED);
            originalMessageDVO.setIsAcknowledged(true);
            originalMessageDVO.setStatusDescription(disposition.toString());
            if (disposition.isError()) {
                originalMessageDVO.setStatus(MessageDVO.STATUS_PROCESSED_ERROR);
            }
            else {
                originalMessageDVO.setStatus(MessageDVO.STATUS_PROCESSED);
            }
            
            daoHandler.createMessageStore().storeReceipt(
                    replyMessageDVO, replyRepositoryDVO, originalMessageDVO);
					
			AS2EventModule eventModule = (AS2EventModule) 
				getModule().getGroup().getModule(AS2EventModule.MODULE_ID);
           	if (disposition.isError()) {
           		eventModule.fireErrorOccurred(receipt);
           	} else {
           		eventModule.fireResponseReceived(receipt);
            }
            
            getLogger().info("Receipt for message " + originalMessageDVO.getMessageId() + " has been processed successfully");
        }
        catch (Exception e) {
            throw new AS2Exception("Error in processing AS2 receipt message", e);
        }
    }
    
    public AS2Message processMessage(AS2Message requestMessage) throws AS2Exception {
        try {
            getLogger().info(requestMessage + " received");
            
            /* Persist the incoming message */
            AS2DAOHandler daoHandler = new AS2DAOHandler(getDAOFactory());
            RepositoryDVO requestRepositoryDVO = daoHandler.createRepositoryDVO(requestMessage, true);
            MessageDVO requestMessageDVO = daoHandler.createMessageDVO(requestMessage, true);
            requestMessageDVO.setStatus(requestMessage.isReceiptSynchronous()? MessageDVO.STATUS_PROCESSING:MessageDVO.STATUS_RECEIVED);
            daoHandler.createMessageStore().storeMessage(requestMessageDVO, requestRepositoryDVO);
            
            /* Process the message at once if it is a synchronous request */
            if (requestMessage.isReceiptSynchronous()) {
                try {
                    return processReceivedMessage(requestMessage);
                }
                catch (Exception e) {
                    requestMessageDVO.setStatus(MessageDVO.STATUS_PROCESSED_ERROR);
                    requestMessageDVO.setStatusDescription(e.toString());
                    daoHandler.createMessageDAO().persist(requestMessageDVO);
                    throw e;
                }
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            throw new AS2Exception("Error in processing AS2 incoming message", e);
        }
    }
            
    protected AS2Message processReceivedMessage(AS2Message requestMessage) throws AS2Exception {
        try{
            getLogger().info(requestMessage + " is being processed");
            
            AS2DAOHandler daoHandler = new AS2DAOHandler(getDAOFactory());
            KeyStoreManager keyman = (KeyStoreManager) getComponent(KEYSTORE_MANAGER);
            IncomingMessage imsg = new IncomingMessage(requestMessage,keyman,daoHandler, getLogger());
            
            /* Handle the SMIME features */
            imsg.processSMime();
            
            AS2Message responseMessage = null;
            RepositoryDVO responseRepositoryDVO = null;
            MessageDVO responseMessageDVO = null;
            
            /* Generate a reply message if requested */
            if (requestMessage.isReceiptRequested()) {
                getLogger().info(requestMessage + " is being replied");
                
                responseMessage = imsg.generateReceipt();
                responseMessageDVO = daoHandler.createMessageDVO(responseMessage, false);
                responseRepositoryDVO = daoHandler.createRepositoryDVO(responseMessage, false);
                
                if (requestMessage.isReceiptSynchronous()) {
                    responseMessageDVO.setStatus(MessageDVO.STATUS_PROCESSED);
                }
                else {
                    responseMessageDVO.setReceiptUrl(requestMessage.getHeader(AS2Header.RECEIPT_DELIVERY_OPTION));
                }
            }
            
            /* Update the original message and persist the reply message if any */
            MessageDAO messageDAO = daoHandler.createMessageDAO();
            MessageDVO requestMessageDVO = (MessageDVO)messageDAO.createDVO();
            requestMessageDVO.setMessageId(requestMessage.getMessageID());
            requestMessageDVO.setMessageBox(MessageDVO.MSGBOX_IN);
            if (!messageDAO.retrieve(requestMessageDVO)) {
                throw new AS2Exception("Unable to update message status. Missing message: "+requestMessage.getMessageID());
            }
            
            requestMessageDVO.setIsAcknowledged(requestMessage.isReceiptRequested());
            requestMessageDVO.setStatusDescription(imsg.getDisposition().toString());
            
            /* Dispatch the message if there is no error */
            if (imsg.getDisposition().isError()) {
                requestMessageDVO.setStatus(MessageDVO.STATUS_PROCESSED_ERROR);
            }
            else {
                //imsg.dispatchMessage(); //Instead of dispatch message to repository, push to database instread
                
            	//Retrieve Original Payload Content
            	RepositoryDAO repoDAO = daoHandler.createRepositoryDAO();
            	RepositoryDVO repoDVO = (RepositoryDVO) repoDAO.createDVO();
            	repoDVO.setMessageId(requestMessageDVO.getMessageId());
            	repoDVO.setMessageBox(requestMessageDVO.getMessageBox());
            	if(!repoDAO.retrieve(repoDVO)){
            		throw new AS2Exception("Unable to update payload content. Missing message: " + requestMessageDVO.getMessageId());
            	}
            	
            	AS2Message decrytedMessage = imsg.getDecrytedMessage();
            	InputStream contentIns =
            		decrytedMessage.getBodyPart().getDataHandler().getInputStream();
            	repoDVO.setContent(decrytedMessage.toByteArray());
            	repoDAO.persist(repoDVO);
            	requestMessageDVO.setStatus(MessageDVO.STATUS_PROCESSED);
            }

            daoHandler.createMessageStore().storeReceipt(responseMessageDVO, responseRepositoryDVO, requestMessageDVO);
			
            if (!imsg.getDisposition().isError()) {
	            AS2Message decrytedMessage = imsg.getDecrytedMessage();
	            AS2EventModule eventModule = (AS2EventModule) 
	            	getModule().getGroup().getModule(AS2EventModule.MODULE_ID);
           		eventModule.fireMessageReceived(decrytedMessage);
            }
            
            getLogger().info(requestMessage + " has been processed successfully");

            return responseMessage;
        }
        catch (Exception e) {
            throw new AS2Exception("Error in processing AS2 received message", e);
        }
    }
}