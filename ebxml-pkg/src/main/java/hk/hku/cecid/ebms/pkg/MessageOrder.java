/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/MessageOrder.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * tslam [2002-03-21]
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

import hk.hku.cecid.ebms.pkg.validation.EbxmlValidationException;
import hk.hku.cecid.ebms.pkg.validation.SOAPValidationException;

import java.util.Iterator;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
/**
 * An ebXML <code>MessageOrder</code> in the SOAP Header of a
 * <code>HeaderContainer</code> [ebMSS 9.1].
 *
 * @author tslam 
 * @version $Revision: 1.1 $
 */
public class MessageOrder extends HeaderElement {

    /** <code>MessageOrder</code> element name */
    static final String MESSAGE_ORDER = "MessageOrder";

    /* Constants for MessageOrder elements */
    /** SequenceNumber element name */
    static final String ELEMENT_SEQUENCE_NUMBER = "SequenceNumber";

    /** Name of the status attribute */
    static final String ATTRIBUTE_STATUS = "status";

    /** Name of the "Reset" status */
    static final String VALUE_STATUS_RESET = "Reset";

    /** Name of the "Continue" status */
    static final String VALUE_STATUS_CONTINUE = "Continue";

    /** First mesage in conversation / reset the sequence number counter. */
    public static final int STATUS_RESET = 0;

    /** Subsequent messages in the conversation and the sequence number is not
        reset. */
    public static final int STATUS_CONTINUE = 1;

    private final int sequenceNo;

    private final int status;

    /** 
     * Constructs a <code>MessageOrder</code> with the given mandatory
     * fields.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header container
     *                          of which the <code>MessageOrder</code> is 
     *                          attached to.
     * @param status            Status of the sequence number.
     * @param sequenceNo        The sequence number in the converstaion.
     * @throws SOAPException 
    */
    MessageOrder(SOAPEnvelope soapEnvelope, int status, int sequenceNo) 
        throws SOAPException {
        super(soapEnvelope, MESSAGE_ORDER);
        this.sequenceNo = sequenceNo;
        this.status = status;

        String statusName;
        if (status == STATUS_RESET) {
            statusName = VALUE_STATUS_RESET;
        }
        else {
            statusName = VALUE_STATUS_CONTINUE;
        }

        addChildElement(ELEMENT_SEQUENCE_NUMBER, Integer.toString(sequenceNo))
            .addAttribute(ATTRIBUTE_STATUS, statusName);
    }

    /** 
     * Constructs an <code>MessageOrder</code> object by parsing the given
     * <code>SOAPElement</code>.
     * 
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header container
     *                          of which the <code>MessageOrder</code> is 
     *                          attached to.
     * @param soapElement       <code>SOAPElement</code> containing
     *                          SequenceNumber element.
     *
     * @throws SOAPException
     */
    MessageOrder(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        Iterator childElements = getChildElements(ELEMENT_SEQUENCE_NUMBER);
        if (childElements.hasNext()) {
            try {
                SOAPElement element = (SOAPElement) childElements.next();
                // modify the statement to trim the value in hermes 2 ebms plugin coz new library used
                sequenceNo = Integer.parseInt(element.getValue().trim());
                String value = element.getAttributeValue(
                    soapEnvelope.createName(ATTRIBUTE_STATUS, 
                        NAMESPACE_PREFIX_EB, NAMESPACE_URI_EB));
                if (value == null || value.equals(VALUE_STATUS_CONTINUE)) {
                    status = STATUS_CONTINUE;
                }
                else if (value.equals(VALUE_STATUS_RESET)) {
                    status = STATUS_RESET;
                    if (sequenceNo != 0) {
                        throw new EbxmlValidationException
                            (EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                             EbxmlValidationException.SEVERITY_ERROR,
                             "MessageOrder element having status \"Reset\" must "
                             + "have sequence number equals to 0.");
                    }
                }
                else {
                    throw new EbxmlValidationException
                       (EbxmlValidationException.EBXML_ERROR_VALUE_NOT_RECOGNIZED,
                        EbxmlValidationException.SEVERITY_ERROR,
                         "MessageOrder/SequenceNumber has unrecognized status "
                         + "value.");
                }
            }
            catch (NumberFormatException e) {
                throw new EbxmlValidationException
                    (EbxmlValidationException.EBXML_ERROR_VALUE_NOT_RECOGNIZED,
                     EbxmlValidationException.SEVERITY_ERROR,
                     "Cannot parse sequence number in MessageOrder element");
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":" 
                 + ELEMENT_SEQUENCE_NUMBER + "> in <" + NAMESPACE_PREFIX_EB 
                 + ":" + MESSAGE_ORDER + ">!");
        }
    }

    /** 
     * Get the sequence number in the conversation. Note that although the word
     * "conversation" is used in the specification, the sequence number is set
     * and incremented by one party only. It means that both parties need to
     * keep their own sequence numbers for all outgoing messages.
     * 
     * @return sequence number.
     */
    public int getSequenceNumber() {
        return sequenceNo;
    }

    /** 
     * Get the status of the sequence number which can be "Reset" or "Continue".
     * 
     * @return Status of the sequence number.
     */
    public int getStatus() {
        return status;
    }
}
