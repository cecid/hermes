/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.dao;

import hk.hku.cecid.edi.as2.AS2Exception;
import hk.hku.cecid.edi.as2.pkg.AS2Header;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.edi.as2.pkg.AS2MessageException;
import hk.hku.cecid.edi.as2.pkg.DispositionNotification;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DAOFactory;

import java.security.Principal;
import java.util.Date;

/**
 * AS2DAOHandler
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class AS2DAOHandler {

    private DAOFactory daoFactory;
    
    private Principal principal;
    
    public AS2DAOHandler(DAOFactory daoFactory) {
        this(daoFactory, null); 
    }
    
    public AS2DAOHandler(DAOFactory daoFactory, Principal principal) {
        this.daoFactory = daoFactory;
        this.principal = principal; 
    }
    
    public RawRepositoryDAO createRawRepositoryDAO() throws DAOException {
    	return (RawRepositoryDAO) daoFactory.createDAO(RawRepositoryDAO.class);
    }
    
    public RawRepositoryDVO createRawRepositoryDVO(AS2Message message) throws AS2MessageException, DAOException {
        RawRepositoryDAO dao = createRawRepositoryDAO();
        RawRepositoryDVO dvo = (RawRepositoryDVO) dao.createDVO();
        dvo.setMessageId(message.getMessageID());
        dvo.setContent(message.toByteArray());
        return dvo;
    }
    
    public RawRepositoryDVO findRawRepositoryDVO(String messageId) throws AS2Exception, DAOException {
    	RawRepositoryDAO dao = createRawRepositoryDAO();
        RawRepositoryDVO dvo = (RawRepositoryDVO) dao.createDVO();
        dvo.setMessageId(messageId);
        
        if (dao.retrieve(dvo)) {
        	return dvo;
        }
        throw new AS2Exception("No raw repository found - Message Id " + messageId);
    }
    
    public RepositoryDAO createRepositoryDAO() throws DAOException  {
        return (RepositoryDAO)daoFactory.createDAO(RepositoryDAO.class);
    }
        
    public RepositoryDVO createRepositoryDVO(AS2Message message, boolean isIncoming) 
            throws AS2MessageException, DAOException {
        RepositoryDAO dao = createRepositoryDAO();
        RepositoryDVO daoData = (RepositoryDVO) dao.createDVO();
        daoData.setMessageBox(isIncoming ? MessageDVO.MSGBOX_IN: MessageDVO.MSGBOX_OUT);
        daoData.setMessageId(message.getMessageID());
        daoData.setContent(message.toByteArray());
        return daoData;
    }
    
    public MessageDAO createMessageDAO() throws DAOException  {
        return (MessageDAO)daoFactory.createDAO(MessageDAO.class);
    }
        
    public MessageDVO createMessageDVO(AS2Message message, boolean isIncoming) 
            throws AS2MessageException, AS2Exception, DAOException {
        MessageDAO dao = createMessageDAO();
        MessageDVO daoData = (MessageDVO) dao.createDVO();
        daoData.setMessageId(message.getMessageID());
        daoData.setMessageBox(isIncoming ? MessageDVO.MSGBOX_IN: MessageDVO.MSGBOX_OUT);
        daoData.setAs2From(message.getFromPartyID());
        daoData.setAs2To(message.getToPartyID());
        daoData.setTimeStamp(new Date());
        daoData.setStatus(MessageDVO.STATUS_PENDING);

        try {
	        PartnershipDVO partnershipDVO = findPartnership(message, isIncoming);
	        daoData.setPartnershipId(partnershipDVO.getPartnershipId());
        } catch (Exception e) {
        	// Ignore exception
        	// because some invalid messages belongs to non-existing partnership 
        }        
      
        if (message.isDispositionNotification()) {
            DispositionNotification dn = message.getDispositionNotification();
            daoData.setIsReceipt(true);
            daoData.setIsReceiptRequested(false);
            daoData.setMicValue(dn.getReceivedContentMIC());
            daoData.setOriginalMessageId(dn.getOriginalMessageID());
        }
        else {
            daoData.setIsReceipt(false);
            daoData.setIsReceiptRequested(message.isReceiptRequested());
            daoData.setReceiptUrl(message.getHeader(AS2Header.RECEIPT_DELIVERY_OPTION));
        }
        return daoData;
    }
    
    public MessageDVO findMessageDVO(String messageId, String messageBox) throws AS2Exception, DAOException {
    	MessageDAO dao = createMessageDAO();
    	MessageDVO dvo = (MessageDVO) dao.createDVO();
    	dvo.setMessageId(messageId);
    	dvo.setMessageBox(messageBox);
    	if (dao.retrieve(dvo)) {
    		return dvo;
    	}
    	throw new AS2Exception("No message found - Message Id : " + messageId + ", Message Box : " + messageBox);
    }
    
    public PartnershipDAO createPartnershipDAO() throws DAOException  {
        return (PartnershipDAO)daoFactory.createDAO(PartnershipDAO.class);
    }
    
    public PartnershipDVO findPartnership(AS2Message message, boolean isIncoming) 
            throws AS2Exception, DAOException {
        String fromParty, toParty;
        if (isIncoming) {
            fromParty = message.getToPartyID();
            toParty = message.getFromPartyID();
        }
        else {
            fromParty = message.getFromPartyID();
            toParty = message.getToPartyID();
        }
        
        try {
            return findPartnership(fromParty, toParty);
        }
        catch (Exception e) {
            throw new AS2Exception("No partnership for message: " + message, e);
        }
    }
    
    public PartnershipDVO findPartnership(String partnershipId) throws AS2Exception, DAOException {
    	PartnershipDAO dao = createPartnershipDAO();
        PartnershipDVO dvo = (PartnershipDVO) dao.createDVO();
        dvo.setPartnershipId(partnershipId);
        if (dao.retrieve(dvo)) {
        	return dvo;
        }
        throw new AS2Exception("No partnership found - Partnership Id: " + partnershipId);
    }
    
    public PartnershipDVO findPartnership(String fromParty, String toParty) 
            throws AS2Exception, DAOException {
        PartnershipDAO dao = createPartnershipDAO();
        PartnershipDVO daoData = dao.findByParty(fromParty, toParty);
        if (daoData == null) {
            throw new AS2Exception("No partnership found - From party: " + fromParty + ", To party: " + toParty);
        }
        return daoData;
    }
    
    public MessageStoreDAO createMessageStore() throws DAOException {
        return (MessageStoreDAO) daoFactory.createDAO(MessageStoreDAO.class);
    }
}