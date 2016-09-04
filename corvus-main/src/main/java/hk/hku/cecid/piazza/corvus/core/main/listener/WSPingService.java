/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.corvus.core.main.listener;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.soap.SOAPFaultException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;

/**
 * WSPingService is a simple web service which provides a ping-pong function.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class WSPingService extends WebServicesAdaptor {

    /**
     * Processes the web service ping and replies with a pong message.
     * 
     * @throws SOAPRequestException if unable to process the ping request.
     */
    public SOAPElement[] serviceRequested(SOAPElement[] bodies)
            throws SOAPRequestException {
        try {
            Name actionName = super.soapFactory.createName("action", "",
                    "http://service.main.core.corvus.piazza.cecid.hku.hk/");
            Name actionTypeName = super.soapFactory.createName("type", "",
                    "http://www.w3.org/2001/XMLSchema-instance");

            if (bodies.length > 0
                    && "ping".equalsIgnoreCase(bodies[0].getValue()
                            )
                    && "action".equals(bodies[0].getElementName().getLocalName()
                            )) {
                Sys.main.log.debug("Web Services Ping request received");

                SOAPElement actionElement = super.soapFactory.createElement(actionName);
                actionElement.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
                actionElement.addAttribute(actionTypeName, "xsd:string");
                actionElement.addTextNode("pong");
                return new SOAPElement[]{actionElement};
            }
            else {
                Sys.main.log
                        .debug("Invalid Web Services Ping request received");
                SOAPFaultException sfe = new SOAPFaultException(
                        SOAPFaultException.SOAP_FAULT_CLIENT,
                        "Invalid body content");
                sfe.addDetailEntry(actionName,
                        "Action not specified or invalid");
                throw sfe;
            }
        }
        catch (Exception e) {
            throw new SOAPRequestException(
                    "Error in processing WS ping request", e);
        }
    }
}