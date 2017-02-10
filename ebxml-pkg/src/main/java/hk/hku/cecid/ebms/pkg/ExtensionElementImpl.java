/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/ExtensionElementImpl.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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
import java.util.List;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
/**
 * An implementation of <code>ExtensionElement</code>.
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
class ExtensionElementImpl implements ExtensionElement {

    /** 
     * <code>javax.xml.soap.SOAPElement</code> representing this
     * <code>ExtensionElement</code>
     */
    private SOAPElement soapElement;

    /** 
     * The <code>javax.xml.soap.SOAPEnvelope</code> encapsulating this
     * <code>ExtensionElement</code>
     */
    protected final SOAPEnvelope soapEnvelope;

    private final String localName;

    /** 
     * An <code>ExtensionElement</code> by default has a namespace and URI
     * equal to that of SOAP extension element as defined in ebXML
     * Messaging Service Specification
     */
    private String namespacePrefix = NAMESPACE_PREFIX_EB;

    private String namespaceUri = NAMESPACE_URI_EB;

    /** 
     * Construct an <code>ExtensionElement</code> using the given
     * <code>SOAPEnvelope</code> and <code>SOAPElement</code>
    */
    ExtensionElementImpl(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        this.soapEnvelope = soapEnvelope;
        this.soapElement = soapElement;
        Name name = soapElement.getElementName();
        localName = name.getLocalName();
        namespacePrefix = name.getPrefix();
        namespaceUri = name.getURI();
    }

    /** Construct an <code>ExtensionElement</code> using the given
        <code>SOAPEnvelope</code> and whose namespace and URI are equal to
        that of SOAP extension element as defined in ebXML Messaging Service
        Specification
    */
    ExtensionElementImpl(SOAPEnvelope soapEnvelope, String localName,
                         boolean isHeaderElement)
        throws SOAPException {
        this(soapEnvelope, localName, NAMESPACE_PREFIX_EB, NAMESPACE_URI_EB,
             isHeaderElement, true);
    }

    /** Construct an <code>ExtensionElement</code> using the given
        <code>SOAPEnvelope</code> and the specified namespace and URI.
        Change the default namespace and URI of this
        <code>ExtensionElement</code> to the new one so that subsequent
        added children elements will inherit the new namespace and URI.
    */
    ExtensionElementImpl(SOAPEnvelope soapEnvelope, String localName,
                         String prefix, String uri, boolean isHeaderElement,
                         boolean createSOAPElement) throws SOAPException {
        /*
        soapElement = SOAPFactory.newInstance().
            createElement(localName, prefix, uri);
        */
        this.soapEnvelope = soapEnvelope;
        this.localName = localName;
        if (createSOAPElement) {
            Name name = soapEnvelope.createName(localName, prefix, uri);
            if (isHeaderElement) {
                soapElement = soapEnvelope.getHeader().addHeaderElement(name);
                if (uri.equals(NAMESPACE_URI_EB)) {
                    ((SOAPHeaderElement) soapElement).
                        setMustUnderstand(HeaderElement.MUST_UNDERSTAND);
                    // actor is not necessary exist in MessageHeader
                    // ((SOAPHeaderElement) soapElement).setActor(null);
                }
            }
            else {
                soapElement = soapEnvelope.getBody().addBodyElement(name);
            }
        }
        else {
            soapElement = null;
        }
        namespacePrefix = prefix;
        namespaceUri = uri;
    }

    /** Add an attribute whose namespace is the same as this
        <code>ExtensionElement</code>
    */
    public ExtensionElement addAttribute(String localName, String value)
        throws SOAPException {
        return (ExtensionElement)
            addAttribute(soapEnvelope.createName(localName,
                                                 namespacePrefix,
                                                 namespaceUri), value);
    }

    /** Add a namespace qualified attribute */
    public ExtensionElement addAttribute(String localName, String prefix,
                                         String uri, String value)
        throws SOAPException {
        return (ExtensionElement)
            addAttribute(soapEnvelope.createName(localName, prefix, uri),
                         value);
    }

    /** Get an attribute whose namespace is the same as this
        <code>ExtensionElement</code>
    */
    public String getAttributeValue(String localName)
        throws SOAPException {
        return getAttributeValue(soapEnvelope.createName(localName,
                                                         namespacePrefix,
                                                         namespaceUri));
    }

    /** Get an attribute with the specified prefix and namespace */
    public String getAttributeValue(String localName, String prefix,
                                    String uri)
        throws SOAPException {
        return getAttributeValue
            (soapEnvelope.createName(localName, prefix, uri));
    }

    /** Add a child element without text node value and whose namespace is
        the same as this <code>ExtensionElement</code>, i.e., the parent
    */
    public ExtensionElement addChildElement(String localName)
        throws SOAPException {
        return addChildElement(localName, null);
    }

    /** Add a child element with the specified value and whose namespace is
        the same as this <code>ExtensionElement</code>, i.e., the parent
    */
    public ExtensionElement addChildElement(String localName, String value)
        throws SOAPException {
        return addChildElement(localName, namespacePrefix, namespaceUri,
                               value);
    }

    /** Add a child element without text node value and with the specified
        namespace
    */
    public ExtensionElement addChildElement(String localName, String prefix,
                                            String uri)
        throws SOAPException {
        return addChildElement(localName, prefix, uri, null);
    }

    /** Add a child element with the specified value and namespace.
        Change the default namespace and URI of this
        <code>ExtensionElement</code> to the new one so that subsequent
        added children elements will inherit the new namespace and URI.
    */
    public ExtensionElement addChildElement(String localName, String prefix,
                                            String uri, String value)
        throws SOAPException {
        final SOAPElement child =
            soapElement.addChildElement(localName, prefix, uri);
        if (value != null)
            child.addTextNode(value);
        namespacePrefix = prefix;
        namespaceUri = uri;

        return new ExtensionElementImpl(soapEnvelope, child);
    }

    /** Get all descendant child elements of the specified
        <code>localName</code> whose namespace is the same as this
        <code>ExtensionElement</code>, in the order in which they are
        encountered in a preorder traversal of this
        <code>ExtensionElement</code> tree. Each <code>Iterator</code> entry
        is in the form of an <code>javax.xml.soap.SOAPElement</code>.
    */
    public Iterator getChildElements(String localName)
        throws SOAPException {
        return getChildElements(soapEnvelope.createName(localName,
                                                        namespacePrefix,
                                                        namespaceUri));
    }

    /** Return the <code>javax.xml.soap.SOAPElement</code> representation of
        this <code>Element</code>
    */
    public SOAPElement getSOAPElement() throws SOAPException {
        if (soapElement == null) {
            Name name = soapEnvelope.createName
                (localName, namespacePrefix, namespaceUri);
            soapElement = soapEnvelope.getHeader().addHeaderElement(name);
        }
        return soapElement;
    }

    /*
    public void synchronizeWithParent(SOAPElement parent, int index)
        throws SOAPException {
        int count = 0;
        for (Iterator i=getChildElements(parent, soapElement.getElementName());
             i.hasNext() ; ) {
            final SOAPElement child = (SOAPElement) i.next();
            if (count == index) {
                soapElement = child;
                return;
            }
            count++;
        }

        throw new SOAPException
            ("Cannot synchronize <" + NAMESPACE_PREFIX_EB + ":" 
             + getElementName().getLocalName() + "> " + "with parent <" 
             + parent.getElementName().getPrefix() + ":" 
             + parent.getElementName().getLocalName() + "> at index " 
             + String.valueOf(index) + "!");
    }
    */

    /** Add an attribute of the given <code>name</code> and <code>value</code>
        to this <code>Element</code>. If the namespace is found to be non-null,
        non-empty and different from the current <code>Element</code>, the
        namespace is declared.
    */
    public Element addAttribute(Name name, String value)
        throws SOAPException {
        if (name.getPrefix() != null && name.getPrefix().equals("") == false &&
            name.getPrefix().equals(soapElement.getElementName().getPrefix())
            == false) {
            List prefixList = new ArrayList();
            for (Iterator i=soapEnvelope.getNamespacePrefixes() ;
                 i.hasNext() ; ) {
                String prefix = (String) i.next();
                String uri = soapEnvelope.getNamespaceURI(prefix);
                if (uri.equals(name.getURI())) {
                    prefixList.add(prefix);
                }
            }
            for (int i = 0; i < prefixList.size(); i++) {
                soapEnvelope.removeNamespaceDeclaration((String) prefixList.get(i));
            }
            soapEnvelope.addNamespaceDeclaration(name.getPrefix(),
                                                 name.getURI());
        }

        // new statement in hermes 2 ebms plugin coz new library used
        soapElement.removeAttribute(name);
        soapElement.addAttribute(name, value);
        return this;
    }

    /** Add a child element to this <code>Element</code> */
    public Element addChildElement(Element child)
        throws SOAPException {
        final SOAPElement soapChild = soapElement.
            addChildElement(child.getSOAPElement());
        return new ExtensionElementImpl(soapEnvelope, soapChild);
    }

    /** Get the element name of this <code>Element</code> */
    public Name getElementName() {
        return soapElement.getElementName();
    }

    /** Get the text node value of this <code>Element</code>. Returns
        <code>null</code> if it does not exist.
    */
    public String getValue() {
        return soapElement.getValue();
    }

    /** Get the attribute value of the specified attribute name */
    public String getAttributeValue(Name name) {
        return soapElement.getAttributeValue(name);
    }

    /** Get all attributes of this <code>Element</code>. Each
        <code>Iterator</code> entry is in the form of
        <code>Map.Entry</code> representing a
        (<code>javax.xml.soap.Name</code> name, <code>String</code> value)
        pair.
    */
    public Iterator getAllAttributes() {
        return soapElement.getAllAttributes();
    }

    /** Get all descendant child elements of the specified
        <code>javax.xml.soap.Name</code>, in the order in which they are
        encountered in a preorder traversal of this <code>Element</code> tree.
        Each <code>Iterator</code> entry is in the form of an
        <code>javax.xml.soap.SOAPElement</code>.
    */
    public Iterator getChildElements(Name name) {
        return getChildElements(soapElement, name);
    }

    /** Get all immediate child elements in the form of an
        <code>javax.xml.soap.SOAPElement</code> in each <code>Iterator</code>
        entry
    */
    public Iterator getChildElements() {
        return soapElement.getChildElements();
    }

    private static Iterator getChildElements(SOAPElement parent, Name name) {
        final ArrayList childElements = new ArrayList();

        for (Iterator i=parent.getChildElements() ; i.hasNext() ; ) {
            final Object object = i.next();
            if ((object instanceof SOAPElement) == false)
                continue;

            final SOAPElement child = (SOAPElement) object;
            final Name childName = child.getElementName();
            if (childName.getLocalName().equals(name.getLocalName()) &&
                childName.getURI().equals(name.getURI())) {
                childElements.add(child);
                continue;
            }

            for (Iterator j=getChildElements(child, name) ; j.hasNext() ; ) {
                final SOAPElement grandChild = (SOAPElement) j.next();
                childElements.add(grandChild);
            }
        }

        return childElements.iterator();
    }
}
