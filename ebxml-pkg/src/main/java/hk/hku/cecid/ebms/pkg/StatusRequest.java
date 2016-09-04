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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/StatusRequest.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import hk.hku.cecid.ebms.pkg.validation.SOAPValidationException;

import java.util.Iterator;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
/**
 * An ebXML <code>StatusRequest</code> in the SOAP Body of a
 * <code>HeaderContainer</code> [ebMSS 7.1.1].
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class StatusRequest extends BodyElement {

    /** 
     * Name of the <code>StatusRequest</code> element.
     */
    static final String STATUS_REQUEST = "StatusRequest";

    /* Constants for StatusRequest element */
    /** 
     * Name of the RefToMessageID element.
     */
    static final String ELEMENT_REF_TO_MESSAGE_ID = "RefToMessageId";

    private final String refToMessageId;

    /** 
     * Constructs a <code>StatusRequested</code> with the given mandatory
     * fields.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header of which
     *                          status request element will be added to.
     * @param refToMessageId    Identifier of the message that this status
     *                          message intends to query.
     * @throws SOAPException 
    */
    StatusRequest(SOAPEnvelope soapEnvelope, String refToMessageId)
        throws SOAPException {
        super(soapEnvelope, STATUS_REQUEST);
        this.refToMessageId = refToMessageId;
        addChildElement(ELEMENT_REF_TO_MESSAGE_ID, refToMessageId);
    }

    /** 
     * Constructs a <code>StatusRequest</code> object by parsing the given
     * <code>SOAPElement</code>.
     * 
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header of which
     *                          status request element will be added to.
     * @param soapElement       <code>SOAPElement</code> from which a 
     *                          <code>StatusRequest</code> object is
     *                          constructed.
     * @throws SOAPException 
     */
    StatusRequest(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        final Iterator childElements =
            getChildElements(ELEMENT_REF_TO_MESSAGE_ID);
        if (childElements.hasNext()) {
            refToMessageId = ((SOAPElement) childElements.next()).getValue();
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":"
                 + ELEMENT_REF_TO_MESSAGE_ID + "> in <" + NAMESPACE_PREFIX_EB
                 + ":" + STATUS_REQUEST + ">!");
        }
    }

    /** 
     * Get the identifier of the message the status request refers to.
     * 
     * @return Identifier of the message being referred to by status request.
     */
    public String getRefToMessageId() {
        return refToMessageId;
    }
}
