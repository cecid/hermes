/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

import java.io.IOException;

import java.util.Base64;
import java.util.Iterator;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.MessageHeader;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandler;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandlerException;
import hk.hku.cecid.ebms.spa.listener.EbmsRequest;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.rest.RestRequest;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;

/**
 * HermesMessageSendApiListener
 * 
 * @author Patrick Yee
 *
 */
public class HermesMessageSendApiListener extends HermesProtocolApiListener {

    protected void processApi(RestRequest request, JsonObjectBuilder jsonBuilder) throws RequestListenerException {
        HttpServletRequest httpRequest = (HttpServletRequest) request.getSource();
        String protocol = this.getProtocolFromPathInfo(httpRequest.getPathInfo());

        if (protocol.equalsIgnoreCase("ebms")) {
            if (httpRequest.getMethod().equalsIgnoreCase("GET")) {
            }
            else if (httpRequest.getMethod().equalsIgnoreCase("POST")) {
                JsonObject jsonObject = null;
                String partnership_id = null;
                //byte[] payload = null;

                try {
                    JsonReaderFactory factory = Json.createReaderFactory(null);
                    JsonReader jsonReader = factory.createReader(httpRequest.getInputStream());
                    jsonObject = jsonReader.readObject();
                    jsonReader.close();

                    partnership_id = jsonObject.getString("partnership_id");
                    //String payload_string = jsonObject.getString("payload");

                    //Base64.Decoder decoder = Base64.getDecoder();
                    //payload = decoder.decode(payload_string.getBytes());
                }
                catch (IOException e) {
                    this.fillError(jsonBuilder, -1, "Error reading request data");
                    return;
                }
                catch (Exception e) {
                    this.fillError(jsonBuilder, -1, "Error parsing request data");
                    return;
                }

                if (partnership_id == null) {
                    this.fillError(jsonBuilder, -1, "Missing required field: partnership_id");
                    return;
                }
                //if (payload == null) {
                //    this.fillError(jsonBuilder, -1, "Missing required field: payload");
                //    return;
                //}

                EbmsRequest ebmsRequest;
                try {
                    PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao.createDAO(PartnershipDAO.class);
                    PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO.createDVO();
                    partnershipDVO.setPartnershipId(partnership_id);
                    if (!partnershipDAO.retrieve(partnershipDVO)) {
                        throw new DAOException("No partnership [" + partnership_id + "] is found");
                    }

                    EbxmlMessage ebxmlMessage = new EbxmlMessage();
                    MessageHeader msgHeader = ebxmlMessage.addMessageHeader();
                    msgHeader.setCpaId(partnershipDVO.getCpaId());
                    msgHeader.setService(partnershipDVO.getService());
                    msgHeader.setAction(partnershipDVO.getAction());

                    ebmsRequest = new EbmsRequest(request);
                    ebmsRequest.setMessage(ebxmlMessage);
                }
                catch (DAOException e) {
                    this.fillError(jsonBuilder, -1, "Error loading partinership");
                    return;
                }
                catch (SOAPException e) {
                    this.fillError(jsonBuilder, -1, "Error constructing ebXML message");
                    return;
                }

                MessageServiceHandler msh = MessageServiceHandler.getInstance();
                try {
                    msh.processOutboundMessage(ebmsRequest, null);
                } catch (MessageServiceHandlerException e) {
                    EbmsProcessor.core.log.error(
                            "Error in passing ebms Request to msh outbound", e);
                    throw new RequestListenerException(
                            "Error in passing ebms Request to msh outbound", e);
                }
            }
            else {
                throw new RequestListenerException("Request method not supported");
            }
        }
        else {
            this.fillError(jsonBuilder, -1, "Protocol unknown");
        }
    }
}
