/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.service;

import hk.hku.cecid.edi.as2.AS2Exception;
import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.edi.as2.dao.PartnershipDAO;
import hk.hku.cecid.edi.as2.module.PayloadCache;
import hk.hku.cecid.edi.as2.module.PayloadRepository;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.soap.SOAPFaultException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequest;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesResponse;

import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.InflaterInputStream;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Element;


/**
 * AS2MessageSenderService
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class AS2MessageSenderService extends WebServicesAdaptor {

    public void serviceRequested(WebServicesRequest request,
            WebServicesResponse response) throws SOAPRequestException, DAOException {
        
        Element[] bodies = request.getBodies();
        String as2From = getText(bodies, "as2_from");
        String as2To = getText(bodies, "as2_to");
        String type = getText(bodies, "type");
        
        if (as2From == null || as2To == null || type == null) {
            throw new SOAPFaultException(SOAPFaultException.SOAP_FAULT_CLIENT,
                    "Missing delivery information");
        }
        else {
            PartnershipDAO dao = (PartnershipDAO)AS2Processor.core.dao.createDAO(PartnershipDAO.class);
            if (dao.findByParty(as2From, as2To) == null) {
                throw new SOAPFaultException(SOAPFaultException.SOAP_FAULT_CLIENT, "No registered partnership");
            }
        }
        
        AS2Processor.core.log.info("Outbound payload received - From: " + as2From + ", To: " + as2To + ", Type: " + type);

        SOAPRequest soapRequest = (SOAPRequest)request.getSource();
        SOAPMessage soapRequestMessage = soapRequest.getMessage();
        
        PayloadCache cache = null;
        Iterator attachments = soapRequestMessage.getAttachments();
        if (attachments.hasNext()) {
            AttachmentPart attachment = (AttachmentPart)attachments.next();
            try {
                InputStream ins = attachment.getDataHandler().getInputStream();
                if ("application/deflate".equals(attachment.getContentType())) {
                    ins = new InflaterInputStream(ins);
                }

                PayloadRepository repository = AS2Processor.getOutgoingPayloadRepository();
                cache = repository.createPayloadCache(AS2Message.generateID(), as2From, as2To, type);
                cache.save(ins);
                
                if (cache.checkIn()) {
                    AS2Processor.core.log.info("Outbound payload persisted - From: " + as2From + ", To: " + as2To + ", Type: " + type);
                }
                else {
                    throw new AS2Exception("Unable to persist payload - From: " + as2From + ", To: " + as2To + ", Type: " + type);
                }
            }
            catch (Exception e) {
                throw new SOAPRequestException("Unable to extract payload", e);
            }
        }
        else {
            throw new SOAPFaultException(SOAPFaultException.SOAP_FAULT_CLIENT, "Missing payload");
        }
        
        generateReply(response, cache.getMessageID());
    }
    
    private void generateReply(WebServicesResponse response, String messageId) throws SOAPRequestException {
        try {
            SOAPElement responseElement = createText("message_id", messageId, "http://service.as2.edi.cecid.hku.hk/");
            response.setBodies(new SOAPElement[]{responseElement});
        }
        catch (Exception e) {
            throw new SOAPRequestException("Unable to generate reply message", e);
        }
    }

    protected boolean isCacheEnabled() {
        return false;
    }
}
