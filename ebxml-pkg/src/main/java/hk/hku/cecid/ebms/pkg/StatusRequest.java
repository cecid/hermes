/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/StatusRequest.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * cyng [2002-03-21]
 *
 * Code reviewed by:
 *
 * username [YYYY-MM-DD]
 *
 * Remarks:
 *
 * =====
 */

package hk.hku.cecid.ebms.pkg;

import hk.hku.cecid.ebms.pkg.validation.SOAPValidationException;

import java.util.Iterator;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
/**
 * An ebXML <code>StatusRequest</code> in the SOAP Body of a
 * <code>HeaderContainer</code> [ebMSS 7.1.1].
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class StatusRequest extends BodyElement {

    /** 
     * Name of the <code>StatusRequest</code> element.
     */
    static final String STATUS_REQUEST = "StatusRequest";

    /* Constants for StatusRequest element */
    /** 
     * Name of the RefToMessageID element.
     */
    static final String ELEMENT_REF_TO_MESSAGE_ID = "RefToMessageId";

    private final String refToMessageId;

    /** 
     * Constructs a <code>StatusRequested</code> with the given mandatory
     * fields.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header of which
     *                          status request element will be added to.
     * @param refToMessageId    Identifier of the message that this status
     *                          message intends to query.
     * @throws SOAPException 
    */
    StatusRequest(SOAPEnvelope soapEnvelope, String refToMessageId)
        throws SOAPException {
        super(soapEnvelope, STATUS_REQUEST);
        this.refToMessageId = refToMessageId;
        addChildElement(ELEMENT_REF_TO_MESSAGE_ID, refToMessageId);
    }

    /** 
     * Constructs a <code>StatusRequest</code> object by parsing the given
     * <code>SOAPElement</code>.
     * 
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header of which
     *                          status request element will be added to.
     * @param soapElement       <code>SOAPElement</code> from which a 
     *                          <code>StatusRequest</code> object is
     *                          constructed.
     * @throws SOAPException 
     */
    StatusRequest(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        final Iterator childElements =
            getChildElements(ELEMENT_REF_TO_MESSAGE_ID);
        if (childElements.hasNext()) {
            refToMessageId = ((SOAPElement) childElements.next()).getValue();
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":"
                 + ELEMENT_REF_TO_MESSAGE_ID + "> in <" + NAMESPACE_PREFIX_EB
                 + ":" + STATUS_REQUEST + ">!");
        }
    }

    /** 
     * Get the identifier of the message the status request refers to.
     * 
     * @return Identifier of the message being referred to by status request.
     */
    public String getRefToMessageId() {
        return refToMessageId;
    }
}
