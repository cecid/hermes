/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/Element.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import java.util.Iterator;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
/**
 * Encapsulation of a <code>javax.xml.soap.SOAPElement</code>
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
interface Element {

    /** Return the <code>javax.xml.soap.SOAPElement</code> representation of
        this <code>Element</code>
    */
    SOAPElement getSOAPElement() throws SOAPException;

    /*
    void synchronizeWithParent(SOAPElement parent, int index)
        throws SOAPException;
    */

    /** Add an attribute of the given <code>name</code> and <code>value</code>
        to this <code>Element</code>

        @param name the attribute name in <code>javax.xml.soap.Name</code>
        representation
        @param value the attribute value
        @return this <code>Element</code> to which the attribute is added
    */
    Element addAttribute(Name name, String value) throws SOAPException;

    /** Add a child element to this <code>Element</code>

        @param child the child <code>Element</code>
        @return the newly added child <code>Element</code>
    */
    Element addChildElement(Element child) throws SOAPException;

    /** Get the element name of this <code>Element</code> */
    Name getElementName();

    /** Get the text node value of this <code>Element</code>. Returns
        <code>null</code> if it does not exist.
    */
    String getValue();

    /** Get the attribute value of the specified attribute name */
    String getAttributeValue(Name name);

    /** Get all attributes of this <code>Element</code>. Each
        <code>Iterator</code> entry is in the form of
        <code>Map.Entry</code> representing a
        (<code>javax.xml.soap.Name</code> name, <code>String</code> value)
        pair.
    */
    Iterator getAllAttributes();

    /** Get all descendant child elements of the specified
        <code>javax.xml.soap.Name</code>, in the order in which they are
        encountered in a preorder traversal of this <code>Element</code> tree.
        Each <code>Iterator</code> entry is in the form of an
        <code>javax.xml.soap.SOAPElement</code>.
    */
    Iterator getChildElements(Name name);

    /** Get all immediate child elements in the form of an
        <code>javax.xml.soap.SOAPElement</code> in each <code>Iterator</code>
        entry
    */
    Iterator getChildElements();
}
