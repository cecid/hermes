/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.module;

import hk.hku.cecid.edi.as2.dao.AS2DAOHandler;
import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.edi.as2.dao.RawRepositoryDVO;
import hk.hku.cecid.edi.as2.dao.RepositoryDVO;
import hk.hku.cecid.edi.as2.pkg.AS2Header;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.edi.as2.pkg.DispositionNotificationOption;
import hk.hku.cecid.edi.as2.util.AS2Util;
import hk.hku.cecid.piazza.commons.activation.ByteArrayDataSource;
import hk.hku.cecid.piazza.commons.activation.InputStreamDataSource;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.module.SystemComponent;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.security.SMimeException;
import hk.hku.cecid.piazza.commons.security.SMimeMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.internet.MimeBodyPart;


public class OutgoingMessageProcessor extends SystemComponent {

	private Properties payloadTypes;
	
	@Override
	protected void init() throws Exception {
		super.init();

		String[] types = getProperties().getProperties("/as2/content_type/type");
		Properties props = new Properties();
		for(String type :types){
			String[] tokens = type.split(";");
			props.setProperty(tokens[0], tokens[1]);
		}
		payloadTypes = props;
	}
	
	public AS2Message resendAsNew(String primalMessageId) throws Exception {		
		AS2DAOHandler daoHandler = new AS2DAOHandler(getDAOFactory());
		
		// Checking precondition
		if (primalMessageId == null || 0 == primalMessageId.length()) {
			throw new Exception("Null / Empty primal message ID");
		}		

		MessageDVO primalMsgDVO = daoHandler.findMessageDVO(primalMessageId, MessageDVO.MSGBOX_OUT);
		String primalStatus = primalMsgDVO.getStatus();
		if (MessageDVO.STATUS_PENDING.equals(primalStatus) || MessageDVO.STATUS_PROCESSING.equals(primalStatus)) {
			throw new Exception("Message can only be resent as new when its status is not Pending or Processing");
		}
		
		if (primalMsgDVO.isReceipt()) {
			throw new Exception("Receipt cannot be resent as new");
		}
		
		String partnershipId = primalMsgDVO.getPartnershipId();
		if (null == partnershipId) {
			throw new Exception("Null partnership ID");
		}
		
		PartnershipDVO partnershipDVO = daoHandler.findPartnership(primalMsgDVO.getPartnershipId());
		
		primalMsgDVO.setHasResendAsNew("true");
		
		// Reconstruct old as2 message
		RawRepositoryDVO primalRawRepoDVO = daoHandler.findRawRepositoryDVO(primalMessageId);
		ByteArrayInputStream messageContent = new ByteArrayInputStream(primalRawRepoDVO.getContent());
		AS2Message oldAs2Message = new AS2Message(messageContent);
		messageContent.close();
		
		// Construct new as2 message
		String[] values = oldAs2Message.getBodyPart().getHeader("Content-Disposition");
		String fileName = AS2Util.getFileNameFromMIMEHeader(values);
		
		MimeBodyPart bodyPart = oldAs2Message.getBodyPart();
		String contentType = bodyPart.getContentType();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOHandler.pipe(bodyPart.getInputStream(), baos);
        DataSource dataSource = new ByteArrayDataSource(baos.toByteArray(), contentType);
        DataHandler dataHandler = new DataHandler(dataSource);
        
        String type = getType(contentType);
		
		InputStreamDataSource insDS = new InputStreamDataSource(dataHandler.getInputStream(), type, fileName);
		AS2Message newAs2Message = storeOutgoingMessage(AS2Message.generateID(), type, partnershipDVO, insDS, primalMsgDVO);

		return newAs2Message;
	}
	
	public AS2Message storeOutgoingMessage(
			String messageID, String type, PartnershipDVO partnership, 
			DataSource attachementSource) throws Exception{
		return storeOutgoingMessage(messageID, type, partnership, attachementSource, null);
	}
	
	public AS2Message storeOutgoingMessage(
			String messageID, String type, PartnershipDVO partnership, 
			DataSource attachementSource, MessageDVO primalMsgDVO) throws Exception{
		
		AS2Message as2Message;
		try {			
			as2Message = new AS2Message();

			// Attach Header Value
			as2Message.setMessageID(messageID);
			as2Message.setFromPartyID(partnership.getAS2From());
			as2Message.setToPartyID(partnership.getAs2To());
			as2Message.setHeader(AS2Header.SUBJECT, partnership.getSubject());

			// Set Content to Message
			as2Message.setContent(attachementSource, getPayloadContentType(attachementSource.getContentType()));
			if(attachementSource.getName() != null){
				as2Message.getBodyPart().addHeader("Content-Disposition", "attachment; filename=" + attachementSource.getName());
			}
			
			AS2DAOHandler daoHandler = new AS2DAOHandler(getDAOFactory());
			RawRepositoryDVO rawRepoDVO = daoHandler.createRawRepositoryDVO(as2Message);
			
			String mic = processMessage(as2Message, partnership);
	        
	        persistMessage(as2Message, mic, rawRepoDVO, primalMsgDVO);
	        
	        return as2Message;	        
		} catch (Exception e) {
			throw new Exception("OutgoingPayloadProcessor error", e);
		}		
	}
	
	private void persistMessage(AS2Message as2Message, String mic, RawRepositoryDVO rawRepoDVO, MessageDVO primalMsgDVO) throws Exception {
		AS2DAOHandler daoHandler = new AS2DAOHandler(getDAOFactory());
		
        // Persist Message to Database
		getLogger().info("Persisting outbound " + as2Message);
        RepositoryDVO repoDVO = daoHandler.createRepositoryDVO(as2Message, false);	        

        MessageDVO msgDVO = daoHandler.createMessageDVO(as2Message, false); 
        msgDVO.setStatus(MessageDVO.STATUS_PENDING);
        msgDVO.setMicValue(mic);
        if (null != primalMsgDVO) {
        	msgDVO.setPrimalMessageId(primalMsgDVO.getMessageId());
        }
        
        /* Capture the outgoing message */
       	daoHandler.createMessageStore().storeMessage(primalMsgDVO, msgDVO, repoDVO, rawRepoDVO);
        getLogger().debug("AS2 Message is stored on database. ["+as2Message.getMessageID()+"]");
	}
	
	private String processMessage(AS2Message as2Message, PartnershipDVO partnership) throws Exception {
        String micAlg = null;
        
        // Add header fields for request receipt
        if (partnership.isReceiptRequired()) {
            String returnUrl = null;
            if (!partnership.isSyncReply()) {
                returnUrl = partnership.getReceiptAddress();
            }
            if (partnership.isReceiptSignRequired()) {
                micAlg = partnership.getMicAlgorithm();
            }
            as2Message.requestReceipt(returnUrl, micAlg);
        }
       
        KeyStoreManager keyman =(KeyStoreManager) getComponent("keystore-manager");
        SMimeMessage smime = new SMimeMessage(as2Message.getBodyPart(), keyman.getX509Certificate(), keyman.getPrivateKey());
        smime.setContentTransferEncoding(SMimeMessage.CONTENT_TRANSFER_ENC_BINARY);
        
        String mic = calculateMIC(smime, partnership);
        
        if (partnership.isOutboundCompressRequired()) {
            getLogger().info("Compressing outbound "+as2Message);
            smime = smime.compress();
            if (partnership.isOutboundSignRequired()) {
                mic = calculateMIC(smime, partnership);
            }
        }
        
        if (partnership.isOutboundSignRequired()) {
            getLogger().info("Signing outbound "+as2Message);
            String alg = partnership.getSignAlgorithm();
            if (alg != null && alg.equalsIgnoreCase(PartnershipDVO.ALG_SIGN_MD5)) {
                smime.setDigestAlgorithm(SMimeMessage.DIGEST_ALG_MD5);
            }
            else {
                smime.setDigestAlgorithm(SMimeMessage.DIGEST_ALG_SHA1);
            }
            smime = smime.sign();
        }
        
        if (partnership.isOutboundEncryptRequired()) {
            getLogger().info("Encrypting outbound "+as2Message);
            String alg = partnership.getEncryptAlgorithm();
            if (alg != null && alg.equalsIgnoreCase(PartnershipDVO.ALG_ENCRYPT_RC2)) {
                smime.setEncryptAlgorithm(SMimeMessage.ENCRYPT_ALG_RC2_CBC);
            }
            else {
                smime.setEncryptAlgorithm(SMimeMessage.ENCRYPT_ALG_DES_EDE3_CBC);
            }
            smime = smime.encrypt(partnership.getEncryptX509Certificate());
        }
        as2Message.setBodyPart(smime.getBodyPart());
        
        return mic;
	}
	
    public String calculateMIC(SMimeMessage smime, PartnershipDVO partnership) throws SMimeException {
        String mic = null;
        if (partnership.isReceiptSignRequired()) {
            boolean isSMime = partnership.isOutboundCompressRequired() ||
                              partnership.isOutboundSignRequired() ||
                              partnership.isOutboundEncryptRequired();
            
            String micAlg = partnership.getMicAlgorithm();
            if (micAlg !=null && micAlg.equalsIgnoreCase(PartnershipDVO.ALG_MIC_MD5)) {
                mic = smime.digest(SMimeMessage.DIGEST_ALG_MD5, isSMime);
                micAlg = DispositionNotificationOption.SIGNED_RECEIPT_MICALG_MD5;
            }
            else {
                mic = smime.digest(SMimeMessage.DIGEST_ALG_SHA1, isSMime);
                micAlg = DispositionNotificationOption.SIGNED_RECEIPT_MICALG_SHA1;
            }
            mic =  mic + ", " + micAlg;
        }
        return mic;
    }

    //Return the content-type mapping that defined on OutgoingMessageProcessor Component Parameters
    private String getPayloadContentType(String type){
    	if(type == null){
    		return "application/octet-stream";
    	}else{
    		 String t = payloadTypes.getProperty(type);
             if (t == null) {
                 return"application/octet-stream";
             }
             else {
                 return t.toString();
             }
    	}
    }
    
    // The inverse function of getPayloadContentType
    private String getType(String contentType) {
        if ("application/octet-stream".equals(contentType)) {
        	return null;
        } else {
			Set<?> keySet = payloadTypes.keySet();
			Iterator<?> iter = keySet.iterator();
			while (iter.hasNext()) {
				String iterType = (String) iter.next();
				String iterContentType = (String) payloadTypes.get(iterType);
				if (contentType.equals(iterContentType)) {
					return iterType;
				}
			}
        	return null;
        }
    }    
}