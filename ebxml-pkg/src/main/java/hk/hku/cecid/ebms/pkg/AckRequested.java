/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/AckRequested.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;

/**
 * An ebXML <code>AckRequested</code> in the SOAP Header of a
 * <code>HeaderContainer</code> [ebMSS 6.3.1].
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class AckRequested extends HeaderElement {

    /** <code>AckRequested</code> element name. */
    static final String ACK_REQUESTED = "AckRequested";

    /* Constants for AckRequested elements */
    /** Name of the Signed attribute. */
    static final String ATTRIBUTE_SIGNED = "signed";

    private final boolean signed;

    /** 
     * Constructs a <code>AckRequested</code> with the given mandatory
     * fields.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header of which
     *                          the <code>AckRequested</code> element is added
     *                          to.
     * @param signed            Indicates if a signed acknowledgement response
     *                          is requested. True if a signed response is 
     *                          requested; false otherwise.
     * @throws SOAPException 
    */
    AckRequested(SOAPEnvelope soapEnvelope, boolean signed)
        throws SOAPException {
        super(soapEnvelope, ACK_REQUESTED);
        this.signed = signed;
        addAttribute(ATTRIBUTE_SIGNED, String.valueOf(signed));
        setActor(ACTOR_TO_PARTY_MSH_URN);
    }

    /** 
     * Constructs a <code>AckRequested</code> with the given mandatory
     * fields.
     * 
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header of which
     *                          the <code>AckRequested</code> element is added
     *                          to.
     * @param soapElement       <code>SOAPElement</code> containing
     *                          acknowledgement request.
     * @throws SOAPException 
     */
    AckRequested(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        final String isSigned = getAttributeValue(ATTRIBUTE_SIGNED);
        if (isSigned == null) {
            signed = false;
        }
        else {
            signed = Boolean.valueOf(isSigned).booleanValue();
        }
    }

    /** 
     * Gets the flag that if a signed acknowledgement response is requested.
     * 
     * @return true if a signed acknowledgement response is requested; false
     *         otherwise.
     */
    public boolean getSigned() {
        return signed;
    }
}
