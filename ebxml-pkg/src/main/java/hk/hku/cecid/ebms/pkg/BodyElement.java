/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/BodyElement.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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
 * An <code>ExtensionElement</code> in the SOAP Body of a
 * <code>HeaderContainer</code>
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
class BodyElement extends ExtensionElementImpl {

    BodyElement(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
    }

    BodyElement(SOAPEnvelope soapEnvelope, String localName)
        throws SOAPException {
        super(soapEnvelope, localName, false);
        addAttribute(ATTRIBUTE_VERSION, VERSION);
    }
}
