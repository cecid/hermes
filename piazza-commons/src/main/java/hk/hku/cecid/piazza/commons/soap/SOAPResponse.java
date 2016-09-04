/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.soap;

import java.util.Iterator;

import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

/**
 * The SOAPResponse class represents a SOAP response. It is independent of which
 * transport protocol it is using and contains the SOAP message of the target
 * response.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class SOAPResponse {

    private SOAPMessage message;

    private Object      target;

    /**
     * Creates a new instance of SOAPResponse.
     */
    SOAPResponse() {
    }

    /**
     * Creates a new instance of SOAPResponse.
     * 
     * @param target the target that this response should be committed to.
     */
    SOAPResponse(Object target) {
        this.target = target;
    }

    /**
     * Gets the SOAP message of this response.
     * 
     * @return the SOAP message of this response.
     */
    public SOAPMessage getMessage() {
        return message;
    }

    /**
     * Gets the target that this response should be committed to.
     * 
     * @return the target that this response should be committed to.
     */
    public Object getTarget() {
        return target;
    }

    /**
     * Sets the SOAP message of this response.
     * 
     * @param message the SOAP message of this response.
     */
    public void setMessage(SOAPMessage message) {
        this.message = message;
    }

    /**
     * Sets the target that this response should be committed to.
     * 
     * @param target the target that this response should be committed to.
     */
    void setTarget(Object target) {
        this.target = target;
    }

    /**
     * Adds a SOAP fault to the SOAP message of this response.
     * 
     * @param code the fault code.
     * @param actor the fault actor.
     * @param desc the fault description.
     * @return the SOAP fault which has been added to the SOAP message. null if
     *         there is no SOAP message in this response or the fault has
     *         already been added.
     * @throws SOAPException a SOAP error occurred when adding the the fault.
     */
    public SOAPFault addFault(String code, String actor, String desc)
            throws SOAPException {
        SOAPMessage msg = getMessage();
        if (msg != null) {
            SOAPEnvelope env = msg.getSOAPPart().getEnvelope();
            SOAPBody body = env.getBody();
            if (body != null && !body.hasFault()) {
                SOAPFault fault = body.addFault();
                if (code != null) {
                    fault.setFaultCode(env.getElementName().getPrefix() + ":"
                            + code);
                }
                if (actor != null) {
                    fault.setFaultActor(actor);
                }
                if (desc != null) {
                    fault.setFaultString(desc);
                }
                return fault;
            }
        }
        return null;
    }

    /**
     * Adds a SOAP fault to the SOAP message of this response.
     * 
     * @param cause the exception cause.
     * @return the SOAP fault which has been added to the SOAP message. null if
     *         there is no SOAP message in this response or the fault has
     *         already been added.
     * @throws SOAPException a SOAP error occurred when adding the the fault.
     */
    public SOAPFault addFault(Throwable cause) throws SOAPException {
        if ((cause instanceof SOAPRequestException)
                && ((SOAPRequestException) cause).isSOAPFault()) {
            SOAPFaultException sfe = ((SOAPRequestException) cause)
                    .getSOAPFault();
            SOAPFault fault = addFault(sfe.getFaultCode(), sfe.getFaultActor(),
                    sfe.getFaultString());
            if (fault != null && sfe.hasDetailEntries()) {
                Detail detail = fault.addDetail();
                Iterator detailEntries = sfe.getDetailEntryNames();
                while (detailEntries.hasNext()) {
                    Name entryName = (Name) detailEntries.next();
                    DetailEntry entry = detail.addDetailEntry(entryName);

                    Object entryValue = sfe.getDetailEntryValue(entryName);
                    if (entryValue instanceof SOAPElement) {
                        entry.addChildElement((SOAPElement) entryValue);
                    }
                    else {
                        entry.addTextNode(entryValue.toString());
                    }
                }
            }
            return fault;
        }
        else {
            return addFault(SOAPFaultException.SOAP_FAULT_SERVER, null, cause
                    .toString());
        }
    }
}