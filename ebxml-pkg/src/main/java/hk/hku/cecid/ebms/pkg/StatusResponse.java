/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/StatusResponse.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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
 * An ebXML <code>StatusResponse</code> in the SOAP Body of a
 * <code>HeaderContainer</code> [ebMSS 7.1.2].
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class StatusResponse extends BodyElement {

    /** <code>StatusRequest</code> element name */
    static final String STATUS_RESPONSE = "StatusResponse";

    /* Constants for StatusRequest element */
    /** 
     * Name of the RefToMessageID element.
     */
    static final String ELEMENT_REF_TO_MESSAGE_ID = "RefToMessageId";

    /** 
     * Name of the Timestamp element.
     */
    static final String ELEMENT_TIMESTAMP = "Timestamp";

    /** 
     * Name of the messageStatus attribute.
     */
    static final String ATTRIBUTE_MESSAGE_STATUS = "messageStatus";

    private final String refToMessageId;

    private final String messageStatus;

    private String timestamp;

    /** 
     * Constructs a <code>StatusResponse</code> with the given mandatory
     * fields
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code> to which the 
     *                          status response element will be added.
     * @param refToMessageId    Identifier of the message whose status is
     *                          being reported. [ebMSS 7.3.1].
     * @param messageStatus     Message status response string [ebMSS 7.3.3].
     * @throws SOAPException 
    */
    StatusResponse(SOAPEnvelope soapEnvelope, String refToMessageId,
                   String messageStatus)
        throws SOAPException {
        this(soapEnvelope, refToMessageId, messageStatus, null);
    }

    /** 
     * Constructs a <code>StatusResponse</code> with the given mandatory
     * fields
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code> to which the 
     *                          status response element will be added.
     * @param refToMessageId    Identifier of the message whose status is
     *                          being reported. [ebMSS 7.3.1].
     * @param messageStatus     Message status response string [ebMSS 7.3.3].
     * @param timestamp         Timestamp of the message the status is being
     *                          reported.
     * @throws SOAPException 
    */
    StatusResponse(SOAPEnvelope soapEnvelope, String refToMessageId,
                   String messageStatus, String timestamp)
        throws SOAPException {
        super(soapEnvelope, STATUS_RESPONSE);
        this.refToMessageId = refToMessageId;
        this.messageStatus = messageStatus;
        this.timestamp = timestamp;
        addAttribute(ATTRIBUTE_MESSAGE_STATUS, messageStatus);
        addChildElement(ELEMENT_REF_TO_MESSAGE_ID, refToMessageId);
        if (timestamp != null) {
            addChildElement(ELEMENT_TIMESTAMP, timestamp);
        }
    }

    /** 
     * Constructs a <code>StatusResponse</code> object by parsing the given
     * <code>SOAPElement</code>.
     * 
     * @param soapEnvelope      <code>SOAPEnvelope</code> to which the 
     *                          status response element will be added.
     * @param soapElement       <code>SOAPElement</code> from which a 
     *                          <code>StatusResponse</code> object is
     *                          constructed.
     * @throws SOAPException 
     */
    StatusResponse(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        Iterator childElements = getChildElements(ELEMENT_REF_TO_MESSAGE_ID);
        if (childElements.hasNext()) {
            refToMessageId = ((SOAPElement) childElements.next()).getValue();
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":" 
                 + ELEMENT_REF_TO_MESSAGE_ID + "> in <" + NAMESPACE_PREFIX_EB 
                 + ":" + STATUS_RESPONSE + ">!");
        }

        messageStatus = getAttributeValue(ATTRIBUTE_MESSAGE_STATUS);

        childElements = getChildElements(ELEMENT_TIMESTAMP);
        if (childElements.hasNext()) {
            timestamp = ((SOAPElement) childElements.next()).getValue();
        }
        else {
            timestamp = null;
        }
    }

    /** 
     * Gets Identifier of the message whose status is reported.
     * 
     * @return Identifier of the message being referred. 
     */
    public String getRefToMessageId() {
        return refToMessageId;
    }

    /** 
     * Gets the status of the message whose status is reported.
     * 
     * @return Message status.
     */
    public String getMessageStatus() {
        return messageStatus;
    }

    /** 
     * Get the timestamp of the message when it was received by the party
     * who reported its status.
     * 
     * @return Message timestamp when it was received.
     */
    public String getTimestamp() {
        return timestamp;
    }
}
