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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/validation/SOAPValidationException.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
/** 
 * Exception class that can generate SOAP fault messages from the information it
 * has been given.
 * 
 * @author Frankie Lam
 * @version $Revision: 1.1 $
 */
public class SOAPValidationException extends ValidationException {

    // SOAP Fault codes
    
    /** SOAP Fault code indicating version mismatch. */
    public static final String SOAP_FAULT_VERSION_MISMATCH = "VersionMismatch";

    /** SOAP Fault code indicating that the client cannot interpret an immediate
     *  child of the header element having mustUnderstand equals to "1". */
    public static final String SOAP_FAULT_MUST_UNDERSTAND = "MustUnderstand";

    /** SOAP Fault code indicating a client fault that the message should not
     *  be resent without change. */
    public static final String SOAP_FAULT_CLIENT = "Client";

    /** SOAP Fault code indicating a server fault that the message may succeed
     *  by resending at a later time. */
    public static final String SOAP_FAULT_SERVER = "Server";


    /** SOAP Envelope namespace prefix. */
    static final String NAMESPACE_PREFIX_SOAP_ENVELOPE = "soap";

    /** SOAP Envelope namespace. */
    static final String NAMESPACE_URI_SOAP_ENVELOPE =
        "http://schemas.xmlsoap.org/soap/envelope/";

    /** CECID namespace prefix */
    static final String NAMESPACE_PREFIX_CECID = "cecid";

    /** CECID Elements namespace */
    static final String NAMESPACE_URI_CECID =
        "http://www.cecid.hku.hk";

    /** Error element to be embedded in the detail entries */
    static final String ELEMENT_ERROR = "Error";

    /** SOAP Fault Actor. */
    protected final String faultActor;

    /** SOAP Fault detail. */
    protected final String detail;

    /** 
     * Constructs a <code>SOAPValidationException</code> object given its 
     * fault code and fault string.
     * 
     * @param faultCode     Fault codes as specified in the SOAP 1.1
     *                      specification
     * @param faultString   Human readable explanation of the fault.
     */
    public SOAPValidationException(String faultCode, String faultString) {
        super(ERROR_SOAP, faultCode, faultString);
        this.faultActor = null;
        this.detail = null;
    }
    
    /** 
     * Constructs a <code>SOAPValidationException</code> object given its 
     * fault code and fault string.
     * 
     * @param faultCode     Fault codes as specified in the SOAP 1.1
     *                      specification
     * @param faultString   Human readable explanation of the fault.
     * @param detail        Specific error information related to the SOAP Body 
     *                      element. It must be present if the SOAP Body cannot
     *                      be processed successfully, and must NOT be present
     *                      if the error is caused by header entries.
     */
    public SOAPValidationException(String faultCode, String faultString, 
                                   String detail) {
        super(ERROR_SOAP, faultCode, faultString);
        this.faultActor = null;
        this.detail = detail;
    }

    /** 
     * Constructs a <code>SOAPValidationException</code> object given its 
     * fault code and fault string.
     * 
     * @param faultCode     Fault codes as specified in the SOAP 1.1
     *                      specification
     * @param faultString   Human readable explanation of the fault.
     * @param faultActor    Information on who caused the fault to happen.
     * @param detail        Specific error information related to the SOAP Body 
     *                      element. It must be present if the SOAP Body cannot
     *                      be processed successfully, and must NOT be present
     *                      if the error is caused by header entries.
     */
    public SOAPValidationException(String faultCode, String faultString,
                                   String faultActor, String detail) {
        super(ERROR_SOAP, faultCode, faultString);
        this.faultActor = faultActor;
        this.detail = detail;
    }

    /** 
     * Get SOAP Fault message from the information given.
     * 
     * @return <code>SOAPMessage</code> object containing Fault element.
     */
    public SOAPMessage getSOAPMessage() {
        try {
            final MessageFactory mf = MessageFactory.newInstance();
            final SOAPMessage message = (SOAPMessage)mf.createMessage();
            
            // set default SOAP XML declaration and encoding
            message.setProperty(SOAPMessage.WRITE_XML_DECLARATION, 
            		Boolean.toString(EbxmlMessage.WRITE_XML_DECLARATION));
            message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, 
            		EbxmlMessage.CHARACTER_SET_ENCODING);
            
            final SOAPPart part = message.getSOAPPart();
            final SOAPEnvelope envelope = part.getEnvelope();
            final SOAPBody body = envelope.getBody();
            final SOAPFault fault = body.addFault();

            fault.setFaultCode(errorCode);
            fault.setFaultString(errorString);
            if (faultActor != null) {
                fault.setFaultActor(faultActor);
            }
            if (detail != null) {
                Detail d = fault.addDetail();
                Name name = envelope.createName(ELEMENT_ERROR, 
                    NAMESPACE_PREFIX_CECID, NAMESPACE_URI_CECID);
                DetailEntry entry = d.addDetailEntry(name);
                entry.addTextNode(detail);
            }

            return message;
        }
        catch (SOAPException e) {
        }

        return null;
    }
}
