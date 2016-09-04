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
