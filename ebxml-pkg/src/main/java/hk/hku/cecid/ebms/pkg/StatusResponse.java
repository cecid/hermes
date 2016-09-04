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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/StatusResponse.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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
 * An ebXML <code>StatusResponse</code> in the SOAP Body of a
 * <code>HeaderContainer</code> [ebMSS 7.1.2].
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class StatusResponse extends BodyElement {

    /** <code>StatusRequest</code> element name */
    static final String STATUS_RESPONSE = "StatusResponse";

    /* Constants for StatusRequest element */
    /** 
     * Name of the RefToMessageID element.
     */
    static final String ELEMENT_REF_TO_MESSAGE_ID = "RefToMessageId";

    /** 
     * Name of the Timestamp element.
     */
    static final String ELEMENT_TIMESTAMP = "Timestamp";

    /** 
     * Name of the messageStatus attribute.
     */
    static final String ATTRIBUTE_MESSAGE_STATUS = "messageStatus";

    private final String refToMessageId;

    private final String messageStatus;

    private String timestamp;

    /** 
     * Constructs a <code>StatusResponse</code> with the given mandatory
     * fields
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code> to which the 
     *                          status response element will be added.
     * @param refToMessageId    Identifier of the message whose status is
     *                          being reported. [ebMSS 7.3.1].
     * @param messageStatus     Message status response string [ebMSS 7.3.3].
     * @throws SOAPException 
    */
    StatusResponse(SOAPEnvelope soapEnvelope, String refToMessageId,
                   String messageStatus)
        throws SOAPException {
        this(soapEnvelope, refToMessageId, messageStatus, null);
    }

    /** 
     * Constructs a <code>StatusResponse</code> with the given mandatory
     * fields
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code> to which the 
     *                          status response element will be added.
     * @param refToMessageId    Identifier of the message whose status is
     *                          being reported. [ebMSS 7.3.1].
     * @param messageStatus     Message status response string [ebMSS 7.3.3].
     * @param timestamp         Timestamp of the message the status is being
     *                          reported.
     * @throws SOAPException 
    */
    StatusResponse(SOAPEnvelope soapEnvelope, String refToMessageId,
                   String messageStatus, String timestamp)
        throws SOAPException {
        super(soapEnvelope, STATUS_RESPONSE);
        this.refToMessageId = refToMessageId;
        this.messageStatus = messageStatus;
        this.timestamp = timestamp;
        addAttribute(ATTRIBUTE_MESSAGE_STATUS, messageStatus);
        addChildElement(ELEMENT_REF_TO_MESSAGE_ID, refToMessageId);
        if (timestamp != null) {
            addChildElement(ELEMENT_TIMESTAMP, timestamp);
        }
    }

    /** 
     * Constructs a <code>StatusResponse</code> object by parsing the given
     * <code>SOAPElement</code>.
     * 
     * @param soapEnvelope      <code>SOAPEnvelope</code> to which the 
     *                          status response element will be added.
     * @param soapElement       <code>SOAPElement</code> from which a 
     *                          <code>StatusResponse</code> object is
     *                          constructed.
     * @throws SOAPException 
     */
    StatusResponse(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        Iterator childElements = getChildElements(ELEMENT_REF_TO_MESSAGE_ID);
        if (childElements.hasNext()) {
            refToMessageId = ((SOAPElement) childElements.next()).getValue();
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":" 
                 + ELEMENT_REF_TO_MESSAGE_ID + "> in <" + NAMESPACE_PREFIX_EB 
                 + ":" + STATUS_RESPONSE + ">!");
        }

        messageStatus = getAttributeValue(ATTRIBUTE_MESSAGE_STATUS);

        childElements = getChildElements(ELEMENT_TIMESTAMP);
        if (childElements.hasNext()) {
            timestamp = ((SOAPElement) childElements.next()).getValue();
        }
        else {
            timestamp = null;
        }
    }

    /** 
     * Gets Identifier of the message whose status is reported.
     * 
     * @return Identifier of the message being referred. 
     */
    public String getRefToMessageId() {
        return refToMessageId;
    }

    /** 
     * Gets the status of the message whose status is reported.
     * 
     * @return Message status.
     */
    public String getMessageStatus() {
        return messageStatus;
    }

    /** 
     * Get the timestamp of the message when it was received by the party
     * who reported its status.
     * 
     * @return Message timestamp when it was received.
     */
    public String getTimestamp() {
        return timestamp;
    }
}
