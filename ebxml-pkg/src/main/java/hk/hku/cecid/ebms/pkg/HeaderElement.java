/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/HeaderElement.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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
 * An <code>ExtensionElement</code> in the SOAP Header of a
 * <code>HeaderContainer</code>
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
class HeaderElement extends ExtensionElementImpl {

    static final String ATTRIBUTE_ACTOR = "actor";

    static final String ATTRIBUTE_MUST_UNDERSTAND = "mustUnderstand";

    static final String ACTOR_NEXT_MSH_URN =
        "urn:oasis:names:tc:ebxml-msg:actor:nextMSH";

    static final String ACTOR_TO_PARTY_MSH_URN =
        "urn:oasis:names:tc:ebxml-msg:actor:toPartyMSH";

    static final String ACTOR_NEXT_MSH_SCHEMAS =
        "http://schemas.xmlsoap.org/soap/actor/next";

    static final boolean MUST_UNDERSTAND = true;

    private String actor = null;

    HeaderElement(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        actor = getAttributeValue(soapEnvelope.createName(ATTRIBUTE_ACTOR,
            NAMESPACE_PREFIX_SOAP_ENVELOPE, NAMESPACE_URI_SOAP_ENVELOPE));
    }

    HeaderElement(SOAPEnvelope soapEnvelope, String localName)
        throws SOAPException {
        super(soapEnvelope, localName, true);
        addAttribute(ATTRIBUTE_VERSION, VERSION);
        /*
        addAttribute(ATTRIBUTE_MUST_UNDERSTAND, NAMESPACE_PREFIX_SOAP_ENVELOPE,
                     NAMESPACE_URI_SOAP_ENVELOPE, MUST_UNDERSTAND);
        */
    }

    HeaderElement(SOAPEnvelope soapEnvelope, String localName, String prefix,
                  String uri) throws SOAPException {
        super(soapEnvelope, localName, prefix, uri, true, false);
    }

    void setActor(String actor) throws SOAPException {
        if (this.actor == null && actor != null) {
            this.actor = actor;
            addAttribute(ATTRIBUTE_ACTOR, NAMESPACE_PREFIX_SOAP_ENVELOPE,
                         NAMESPACE_URI_SOAP_ENVELOPE, actor);
        }
    }
    
    public String getActor() {
        return actor;
    }
}
