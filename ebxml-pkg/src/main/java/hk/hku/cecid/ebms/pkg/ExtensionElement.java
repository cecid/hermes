/*
 * Copyright(c) 2002 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Academic Free License Version 1.0
 *
 * Academic Free License
 * Version 1.0
 *
 * This Academic Free License applies to any software and associated 
 * documentation (the "Software") whose owner (the "Licensor") has placed the 
 * statement "Licensed under the Academic Free License Version 1.0" immediately 
 * after the copyright notice that applies to the Software. 
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of the Software (1) to use, copy, modify, merge, publish, perform, 
 * distribute, sublicense, and/or sell copies of the Software, and to permit 
 * persons to whom the Software is furnished to do so, and (2) under patent 
 * claims owned or controlled by the Licensor that are embodied in the Software 
 * as furnished by the Licensor, to make, use, sell and offer for sale the 
 * Software and derivative works thereof, subject to the following conditions: 
 *
 * - Redistributions of the Software in source code form must retain all 
 *   copyright notices in the Software as furnished by the Licensor, this list 
 *   of conditions, and the following disclaimers. 
 * - Redistributions of the Software in executable form must reproduce all 
 *   copyright notices in the Software as furnished by the Licensor, this list 
 *   of conditions, and the following disclaimers in the documentation and/or 
 *   other materials provided with the distribution. 
 * - Neither the names of Licensor, nor the names of any contributors to the 
 *   Software, nor any of their trademarks or service marks, may be used to 
 *   endorse or promote products derived from this Software without express 
 *   prior written permission of the Licensor. 
 *
 * DISCLAIMERS: LICENSOR WARRANTS THAT THE COPYRIGHT IN AND TO THE SOFTWARE IS 
 * OWNED BY THE LICENSOR OR THAT THE SOFTWARE IS DISTRIBUTED BY LICENSOR UNDER 
 * A VALID CURRENT LICENSE. EXCEPT AS EXPRESSLY STATED IN THE IMMEDIATELY 
 * PRECEDING SENTENCE, THE SOFTWARE IS PROVIDED BY THE LICENSOR, CONTRIBUTORS 
 * AND COPYRIGHT OWNERS "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE 
 * LICENSOR, CONTRIBUTORS OR COPYRIGHT OWNERS BE LIABLE FOR ANY CLAIM, DAMAGES 
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE. 
 *
 * This license is Copyright (C) 2002 Lawrence E. Rosen. All rights reserved. 
 * Permission is hereby granted to copy and distribute this license without 
 * modification. This license may not be modified without the express written 
 * permission of its copyright owner. 
 */

/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/ExtensionElement.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import javax.xml.soap.SOAPException;
/**
 * An <code>ExtensionElement</code> in the <code>HeaderContainer</code>
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
interface ExtensionElement extends Element {

    /** Attribute prefix for XML namespace declarations. */
    static final String XML_NS_DECL_PREFIX = "xmlns";

    /** Character that separates the namespace prefix and local name in an
        XML tag.
    */
    static final char XML_NS_SEPARATOR = ':';

    /** Namespace prefix of SOAP envelope. */
    static final String NAMESPACE_PREFIX_SOAP_ENVELOPE = "SOAP-ENV";

    /** Namespace URI of SOAP envelope. */
    static final String NAMESPACE_URI_SOAP_ENVELOPE =
        "http://schemas.xmlsoap.org/soap/envelope/";

    /** Namespace prefix of <code>ExtensionElement</code>. */
    static final String NAMESPACE_PREFIX_EB = "eb";

    /** Namespace URI of <code>ExtensionElement</code>. */
    static final String NAMESPACE_URI_EB = "http://www.oasis-open.org/" +
        "committees/ebxml-msg/schema/msg-header-2_0.xsd";

    /** Namespace prefix of XML Schema Instance */
    static final String NAMESPACE_PREFIX_XSI = "xsi";

    /** Namespace URI of XML Schema Instance */
    static final String NAMESPACE_URI_XSI = 
        "http://www.w3.org/2001/XMLSchema-instance";

    /** URI of SOAP envelope schema location */
    static final String SCHEMA_LOCATION_SOAP_ENVELOPE = 
        NAMESPACE_URI_SOAP_ENVELOPE + " "
        + "http://www.oasis-open.org/committees/ebxml-msg/schema/envelope.xsd";

    /** URI of SOAP header schema location */
    static final String SCHEMA_LOCATION_SOAP_HEADER = 
        NAMESPACE_URI_EB + " " + NAMESPACE_URI_EB;

    /** URI of SOAP SOAP body schema location */
    static final String SCHEMA_LOCATION_SOAP_BODY = 
        NAMESPACE_URI_EB + " " + NAMESPACE_URI_EB;

    /** Namespace prefix of XLINK. */
    static final String NAMESPACE_PREFIX_XLINK = "xlink";

    /** Namespace URI of XLINK. */
    static final String NAMESPACE_URI_XLINK = "http://www.w3.org/1999/xlink";

    /** Namespace prefix of XML. */
    static final String NAMESPACE_PREFIX_XML = "xml";

    /** Namespace URI of XML. */
    static final String NAMESPACE_URI_XML =
        "http://www.w3.org/XML/1998/namespace";

    /** Name of the ID attribute. */
    static final String ATTRIBUTE_ID = "id";

    /** Name of the Schema location attribute */
    static final String ATTRIBUTE_SCHEMA_LOCATION = "schemaLocation";

    /** Name of the version attribute. */
    static final String ATTRIBUTE_VERSION = "version";

    /** Name of the language attribute. */
    static final String ATTRIBUTE_LANG = "lang";

    /** Version of ebXML Messaging Service. */
    static final String VERSION = "2.0";

    /** Default language type of the MSH. */
    static final String LANG_TYPE = "en-US";

    /** Add an attribute whose namespace is the same as this
        <code>ExtensionElement</code>.
    */
    ExtensionElement addAttribute(String localName, String value)
        throws SOAPException;

    /** Add a namespace qualified attribute. */
    ExtensionElement addAttribute(String localName, String prefix, String uri,
                                  String value) throws SOAPException;

    /** Get an attribute whose namespace is the same as this
        <code>ExtensionElement</code>.
    */
    String getAttributeValue(String localName) throws SOAPException;

    /** Get an attribute with the specified prefix and namespace */
    String getAttributeValue(String localName, String prefix, String uri)
        throws SOAPException;

    /** Add a child element without text node value and whose namespace is
        the same as this <code>ExtensionElement</code>, i.e., the parent.
    */
    ExtensionElement addChildElement(String localName) 
        throws SOAPException;

    /** Add a child element with the specified value and whose namespace is
        the same as this <code>ExtensionElement</code>, i.e., the parent.
    */
    ExtensionElement addChildElement(String localName, String value)
        throws SOAPException;

    /** Add a child element without text node value and with the specified
        namespace.
    */
    ExtensionElement addChildElement(String localName, String prefix,
                                     String uri) throws SOAPException;

    /** Add a child element with the specified value and namespace. */
    ExtensionElement addChildElement(String localName, String prefix,
                                     String uri, String value)
        throws SOAPException;

    /** Get all descendant child elements of the specified
        <code>localName</code> whose namespace is the same as this
        <code>ExtensionElement</code>, in the order in which they are
        encountered in a preorder traversal of this
        <code>ExtensionElement</code> tree. Each <code>Iterator</code> entry
        is in the form of an <code>javax.xml.soap.SOAPElement</code>.
    */
    Iterator getChildElements(String localName) throws SOAPException;
}
