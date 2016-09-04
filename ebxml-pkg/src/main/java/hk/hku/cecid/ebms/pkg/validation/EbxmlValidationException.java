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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/validation/EbxmlValidationException.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * frankielam [2002-11-14]
 *
 * Code reviewed by:
 *
 * username [YYYY-MM-DD]
 *
 * Remarks:
 *
 * =====
 */
package hk.hku.cecid.ebms.pkg.validation;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.ErrorList;
import hk.hku.cecid.ebms.pkg.MessageHeader;
import hk.hku.cecid.ebms.pkg.Utility;

import java.util.Date;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
/** 
 * Exception class that can generate ebXML error messages from the information 
 * it has been given.
 * 
 * @author Frankie Lam
 * @version $Revision: 1.1 $
 */
public class EbxmlValidationException extends ValidationException {

    // Ebxml Document Errors

    /** ebXML error indicating that the value of an element / attribute cannot
     *  be recognized. */
    public static final String EBXML_ERROR_VALUE_NOT_RECOGNIZED = 
        ErrorList.CODE_VALUE_NOT_RECOGNIZED;

    /** ebXML error indicating that the function is not supported. */
    public static final String EBXML_ERROR_NOT_SUPPORTED = 
        ErrorList.CODE_NOT_SUPPORTED;

    /** ebXML error indicating that the ebXML message is inconsistent. */
    public static final String EBXML_ERROR_INCONSISTENT = 
        ErrorList.CODE_INCONSISTENT;

    /** ebXML error indicating that the ebXML message do not conform to the
     *  rules or constraints specified in the specification, and cannot be
     *  represented using other error codes. */
    public static final String EBXML_ERROR_OTHER_XML = 
        ErrorList.CODE_OTHER_XML;


    // Ebxml Non-document Errors

    /** ebXML message cannot be delivered. */
    public static final String EBXML_ERROR_DELIVERY_FAILURE = 
        ErrorList.CODE_DELIVERY_FAILURE;

    /** ebXML message is expired. */
    public static final String EBXML_ERROR_TIME_TO_LIVE_EXPIRED = 
        ErrorList.CODE_TIME_TO_LIVE_EXPIRED;

    /** Validation of signatures or authenticity / authority has failed. */
    public static final String EBXML_ERROR_SECURITY_FAILURE = 
        ErrorList.CODE_SECURITY_FAILURE;

    /** xlink:href error cannot be resolved. */
    public static final String EBXML_ERROR_MIME_PROBLEM = 
        ErrorList.CODE_MIME_PROBLEM;

    /** An error has occurred but cannot be represented by other error codes. */
    public static final String EBXML_ERROR_UNKNOWN = 
        ErrorList.CODE_UNKNOWN;


    /** Warning severity level */
    public static final String SEVERITY_WARNING = ErrorList.SEVERITY_WARNING;

    /** Error severity level */
    public static final String SEVERITY_ERROR = ErrorList.SEVERITY_ERROR;

    /**
     * Service name reserved for services described in ebXML Message Service
     * Specification [ebMSS 3.1.4].
     */
    public static final String SERVICE = "urn:oasis:names:tc:ebxml-msg:service";

    /**
     * Error action reserved for standalone errors described in ebXML Message 
     * Service Specification [ebMSS 4.2.4.3].
     */
    public static final String ACTION = "MessageError";

    /** Severity of the error. */
    protected final String severity;

    /** location of the message containing the error. */
    protected final String location;

    /** The ebXML message being referred to. */
    protected EbxmlMessage refToMessage;

    /** 
     * Constructs a <code>EbxmlValidationException</code> object given its 
     * error code, severity level, error string and the ebXML message being
     * referred.
     * 
     * @param errorCode     One of the error codes defined in ebMS 4.2.3.4.1.
     * @param severity      Severity level of the error, which should be either 
     *                      SEVERITY_WARNING or SEVERITY_ERROR
     * @param errorString   String describing the error.
     */
    public EbxmlValidationException(String errorCode, String severity,
        String errorString) {
        this(errorCode, severity, errorString, null);
    }

    /** 
     * Constructs a <code>EbxmlValidationException</code> object given its 
     * error code, severity level, error string and the ebXML message being
     * referred.
     * 
     * @param errorCode     One of the error codes defined in ebMS 4.2.3.4.1.
     * @param severity      Severity level of the error, which should be either 
     *                      SEVERITY_WARNING or SEVERITY_ERROR
     * @param errorString   String describing the error.
     * @param location      Location of the message containing the error.
     */
    public EbxmlValidationException(String errorCode, String severity,
        String errorString, String location) {
        super(ERROR_EBXML, errorCode, errorString);
        this.severity = severity;
        this.location = location;
    }

    /** 
     * Set the <code>EbxmlMessage</code> being referred to by the error message.
     * 
     * @param refToMessage <code>EbxmlMessage</code> object to be referred to.
     */
    public void setRefToMessage(EbxmlMessage refToMessage) {
        this.refToMessage = refToMessage;
    }

    /** 
     * Get <code>EbxmlMessage</code> error message from the information given
     * int this object.
     * 
     * @return <code>EbxmlMessage</code> containing the error information.
     */
    public EbxmlMessage getEbxmlMessage() {

        try {
            final EbxmlMessage errorMessage = new EbxmlMessage();
            final String fromPartyId = ((MessageHeader.PartyId)
                refToMessage.getToPartyIds().next()).getId();
            final String toPartyId = ((MessageHeader.PartyId)
                refToMessage.getFromPartyIds().next()).getId();
            final Date date = new Date();
            final String timeStamp = Utility.toUTCString(date);
            final String messageId = Utility.generateMessageId(date, 
                toPartyId, refToMessage.getCpaId(), SERVICE, ACTION);

            errorMessage.addMessageHeader(fromPartyId, toPartyId,
                refToMessage.getCpaId(), refToMessage.getConversationId(),
                SERVICE, ACTION, messageId, timeStamp);
            errorMessage.getMessageHeader().
                setRefToMessageId(refToMessage.getMessageId());
            errorMessage.addErrorList(errorCode, severity, errorString, location);

            return errorMessage;
        }
        catch (SOAPException e) {
        }
        return null;
    }

    /** 
     * Get the error message in SOAP format.
     * 
     * @return <code>javax.xml.soap.SOAPMessage</code> object.
     */
    public SOAPMessage getSOAPMessage() {
        return getEbxmlMessage().getSOAPMessage();
    }

    /** 
     * Get the location attribute of the ebXML error element.
     * 
     * @return Value of location attribute; null if it is unspecified.
     */
    public String getLocation() {
        return location;
    }
}
