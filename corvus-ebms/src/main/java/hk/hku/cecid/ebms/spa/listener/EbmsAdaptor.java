/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.listener;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;
import hk.hku.cecid.piazza.commons.soap.SOAPHttpAdaptor;
import hk.hku.cecid.piazza.commons.soap.SOAPRequest;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.SOAPResponse;

/**
 * @author Donahue Sze
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EbmsAdaptor extends SOAPHttpAdaptor {

    public abstract void processRequest(EbmsRequest request,
            EbmsResponse response) throws RequestListenerException;

    public void processRequest(SOAPRequest soapRequest,
            SOAPResponse soapResponse) throws SOAPRequestException {

        try {
            EbxmlMessage ebxmlRequestMessage = new EbxmlMessage(soapRequest
                    .getMessage());
            ebxmlRequestMessage.setBytes(soapRequest.getBytes());
            
            EbmsRequest ebmsRequest = new EbmsRequest(soapRequest);
            ebmsRequest.setMessage(ebxmlRequestMessage);
            
            EbmsResponse ebmsResponse = new EbmsResponse(soapResponse);
            
            processRequest(ebmsRequest, ebmsResponse);

            EbxmlMessage ebxmlResponseMessage = ebmsResponse.getMessage();

            if (ebxmlResponseMessage != null) {
                soapResponse.setMessage(ebxmlResponseMessage.getSOAPMessage());
            } else {
            	// Modified by Steve Chan
                // Empty HTTP BODY response for async request
            	soapResponse.setMessage(null);
            }
        } catch (Throwable e) {
            throw new SOAPRequestException("Error in processing EbXML message",
                    e);
        }
    }
}