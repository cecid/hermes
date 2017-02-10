/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/SignatureReference.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * cyng [2003-04-15]
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
 * <ds:Reference> element in <ds:Signature>.
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class SignatureReference extends HeaderElement {

    /** <ds:Reference> element name */
    static final String SIGNATURE_REFERENCE = "Reference";

    /** <ds:DigestValue> element name */
    static final String ELEMENT_DIGEST_VALUE = "DigestValue";

    /** URI attribute name */
    static final String ATTRIBUTE_URI = "URI";

    private final String uri;

    private final String digestValue;

    SignatureReference(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        uri = getAttributeValue(ATTRIBUTE_URI, "", "");
        Iterator childElements = getChildElements(ELEMENT_DIGEST_VALUE);
        if (childElements.hasNext()) {
            digestValue = ((SOAPElement) childElements.next()).getValue();
        }
        else {
            throw new SOAPValidationException(SOAPValidationException.
                SOAP_FAULT_CLIENT, "<" + Signature.NAMESPACE_PREFIX_DS + ":" +
                ELEMENT_DIGEST_VALUE + "> is not found in <" + Signature.
                NAMESPACE_PREFIX_DS + ":" + SIGNATURE_REFERENCE + ">!");
        }
    }

    /** 
     * Gets URI attribute of this <ds:Reference>.
     * 
     * @return URI attribute of this <ds:Reference>.
     */
    public String getUri() {
        return uri;
    }

    /** 
     * Gets <ds:DigestValue> element of this <ds:Reference>.
     * 
     * @return <ds:DigestValue> element of this <ds:Reference>.
     */
    public String getDigestValue() {
        return digestValue;
    }
}
