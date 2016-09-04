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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/validation/ValidationException.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/** 
 * Base class for message validation exceptions. All classes extending this
 * class must be able to generate a SOAP message to indicate the error, but it
 * is not necessary a SOAP Fault message (e.g. an ebXML message containing
 * ErrorList element).
 * 
 * @author  Frankie Lam
 * @version $Revision 1.0$
 */
public abstract class ValidationException extends SOAPException {

    /** The error represented by this exception is unknown. */
    public static final int ERROR_UNKNOWN = 0;

    /** The error represented by this exception is caused by an incorrectly
     *  packaged ebXML message. */
    public static final int ERROR_EBXML = 1;

    /** The error represented by this exception is a SOAP Fault */
    public static final int ERROR_SOAP = 2;


    // Basic information about the error occurred. Derived classes may interpret
    // these fields in their own way.

    /** The type of error represented by this exception object. */
    protected final int errorType;

    /** Error code. */
    protected final String errorCode;

    /** A string describing the error occurred. */
    protected final String errorString;


    /** 
     * Constructs a <code>ValidationException</code> object. 
     * 
     * @param errorType     The type of error represented by this exception 
     *                      object.
     * @param errorCode     An error code in string to be processed by the
     *                      applications.
     * @param errorString   A human-readable description string.
     */
    public ValidationException(int errorType, String errorCode, 
                               String errorString) {
        super(errorCode + ": " + errorString);
        this.errorType = errorType;
        this.errorCode = errorCode;
        this.errorString = errorString;
    }

    /** 
     * Get the SOAP message containing the error information.
     * 
     * @return <code>SOAPMessage</code> object containing the error information.
     */
    public abstract SOAPMessage getSOAPMessage();
}
