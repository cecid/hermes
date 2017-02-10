/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/Manifest.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
/**
 * An ebXML <code>Manifest</code> in the SOAP Body of a
 * <code>HeaderContainer</code>
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class Manifest extends BodyElement {

    /** <code>Manifest</code> element name */
    static final String MANIFEST = "Manifest";

    private final ArrayList references;

    /** Constructs a <code>Manifest</code> */
    Manifest(SOAPEnvelope soapEnvelope) throws SOAPException {
        super(soapEnvelope, MANIFEST);
        references = new ArrayList();
    }

    Manifest(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        references = new ArrayList();
        for (Iterator i=getChildElements(Reference.REFERENCE) ;
             i.hasNext() ; ) {
            references.add(new Reference(soapEnvelope,
                                         (SOAPElement) i.next()));
        }
    }

    public Reference addReference(String id, String href) 
        throws SOAPException {
        final Reference reference = new Reference(soapEnvelope,
            addChildElement(Reference.REFERENCE).getSOAPElement());
        reference.setId(id);
        reference.setHref(href);
        references.add(reference);
        return reference;
    }

    public Iterator getReferences() {
        return references.iterator();
    }
}
