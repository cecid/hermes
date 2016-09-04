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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/ErrorList.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import hk.hku.cecid.ebms.pkg.validation.EbxmlValidationException;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
/**
 * An ebXML <code>ErrorList</code> in the SOAP Header of a
 * <code>HeaderContainer</code>
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class ErrorList extends HeaderElement {

    /** An <code>Error</code> inside an <code>ErrorList</code> */
    public static final class Error {

        /** 
         * Error code attribute indicating the nature of error in the message
         * in error [ebMSS 4.2.3.2.3].
         */
        private final String errorCode;

        /** 
         * Severity attribute indicating the severity of error in the message
         * in error [ebMSS 4.2.3.2.4].
         */
        private final String severity;

        /** 
         * Description element containing a narrative description of error
         * in the language defined in xml:lang [ebMSS 4.2.3.4.6].
         */
        private final Description description;

        /**
         * Location points to the part of the message containing the error
         * [ebMS 4.2.3.2.5].
         */
        private final String location;

        /** 
         * Initializes Error object using given error code, severity and
         * description.
         * 
         * @param errorCode     Error code value. Please refer to 
         *                      [ebMSS 4.2.3.4.1] for a list of valid error 
         *                      codes.
         * @param severity      Severity of the error reported.
         * @param description   Human-readable description of error.
         */
        Error(String errorCode, String severity, Description description) {
            this(errorCode, severity, description, null);
        }

        /** 
         * Initializes Error object using given error code, severity and
         * description.
         * 
         * @param errorCode     Error code value. Please refer to 
         *                      [ebMSS 4.2.3.4.1] for a list of valid error 
         *                      codes.
         * @param severity      Severity of the error reported.
         * @param description   Human-readable description of error.
         * @param location      Location of the message containing the error.
         */
        Error(String errorCode, String severity, Description description,
              String location) {
            this.errorCode = errorCode;
            this.severity = severity;
            this.description = description;
            this.location = location;
        }

        /** 
         * Get the error code contained in the object.
         * 
         * @return Error code.
         */
        public String getErrorCode() {
            return errorCode;
        }

        /** 
         * Get the severity level contained in the object.
         * 
         * @return Severity level.
         */
        public String getSeverity() {
            return severity;
        }

        /** 
         * Get the description contained in the object.
         * 
         * @return Description of the error.
         */
        public Description getDescription() {
            return description;
        }

        /** 
         * Get the location of the message containing the error.
         * 
         * @return Location string.
         */
        public String getLocation() {
            return location;
        }
    }

    /** 
     * Text for "Warning" severity level.
     */
    public static final String SEVERITY_WARNING = "Warning";

    /** 
     * Text for "Error" severity level.
     */
    public static final String SEVERITY_ERROR = "Error";

    /** 
     * Document error: element content or attribute value not recognized 
     * [4.2.3.4.1].
     */
    public static final String CODE_VALUE_NOT_RECOGNIZED =
        "ValueNotRecognized";

    /** 
     * Document error: element or attribute not supported [4.2.3.4.1].
     */
    public static final String CODE_NOT_SUPPORTED = "NotSupported";

    /** 
     * Document error: element content or attribute value inconsistent with 
     * other elements or attributes [4.2.3.4.1].
     */
    public static final String CODE_INCONSISTENT = "Inconsistent";

    /** 
     * Document error: other error in an element content or attribute value 
     * [4.2.3.4.1].
     */
    public static final String CODE_OTHER_XML = "OtherXml";

    /** 
     * Non-XML document error: message delivery failure [4.2.3.4.2].
     */
    public static final String CODE_DELIVERY_FAILURE = "DeliveryFailure";

    /** 
     * Non-XML document error: message time to live expired [4.2.3.4.2].
     */
    public static final String CODE_TIME_TO_LIVE_EXPIRED = "TimeToLiveExpired";

    /** 
     * Non-XML document error: message security check failed [4.2.3.4.2].
     */
    public static final String CODE_SECURITY_FAILURE = "SecurityFailure";

    /** 
     * Non-XML document error: URI resolve error.
     */
    public static final String CODE_MIME_PROBLEM = "MimeProblem";

    /** 
     * Non-XML document error: Unknown error.
     */
    public static final String CODE_UNKNOWN = "Unknown";

    /** <code>ErrorList</code> element name */
    static final String ERROR_LIST = "ErrorList";

    /* Constants for ErrorList elements */
    /** 
     * Error element name in ErrorList [ebMSS 4.2.3.2].
     */
    static final String ELEMENT_ERROR = "Error";

    /** 
     * Name of highestSeverity attribute [ebMSS 4.2.3.1].
     */
    static final String ATTRIBUTE_HIGHEST_SEVERITY = "highestSeverity";

    /** 
     * Name of the codeContext attribute [ebMSS 4.2.3.2.2].
     */
    static final String ATTRIBUTE_CODE_CONTEXT = "codeContext";

    /** 
     * Name of the errorCode attribute [ebMSS 4.2.3.2.3].
     */
    static final String ATTRIBUTE_ERROR_CODE = "errorCode";

    /** 
     * Name of the severity attribute [ebMSS 4.2.3.2.4].
     */
    static final String ATTRIBUTE_SEVERITY = "severity";

    /** 
     * Name of the location attribute [ebMSS 4.2.3.2.5].
     */
    static final String ATTRIBUTE_LOCATION = "location";

    private String highestSeverity;

    private final ArrayList errorList;

    /** 
     * Constructs an <code>ErrorList</code> with the given mandatory
     * fields.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header of which
     *                          the <code>ErrorList</code> is added.
     * @param errorCode         Error code value of the error.
     * @param severity          Severity of the error.
     * @param description       Description of the error.
     * @throws SOAPException 
    */
    ErrorList(SOAPEnvelope soapEnvelope, String errorCode, String severity,
              String description)
        throws SOAPException {
        this(soapEnvelope, errorCode, severity, description, LANG_TYPE, null);
    }

    /** 
     * Constructs an <code>ErrorList</code> with the given mandatory
     * fields.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header of which
     *                          the <code>ErrorList</code> is added.
     * @param errorCode         Error code value of the error.
     * @param severity          Severity of the error.
     * @param description       Description of the error.
     * @param lang              Language of the description specified in 
     *                          <a href="http://www.ietf.org/rfc/rfc1766.txt">
     *                          RFC 1766</a> and ISO639.
     * @throws SOAPException 
    */
    ErrorList(SOAPEnvelope soapEnvelope, String errorCode, String severity,
              String description, String lang)
        throws SOAPException {
        this(soapEnvelope, errorCode, severity, description, lang, null);
    }

    /** 
     * Constructs an <code>ErrorList</code> with the given mandatory
     * fields.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header of which
     *                          the <code>ErrorList</code> is added.
     * @param errorCode         Error code value of the error.
     * @param severity          Severity of the error.
     * @param description       Description of the error.
     * @param lang              Language of the description specified in 
     *                          <a href="http://www.ietf.org/rfc/rfc1766.txt">
     *                          RFC 1766</a> and ISO639.
     * @throws SOAPException 
    */
    ErrorList(SOAPEnvelope soapEnvelope, String errorCode, String severity,
              String description, String lang, String location)
        throws SOAPException {
        super(soapEnvelope, ERROR_LIST);
        this.highestSeverity = SEVERITY_WARNING;
        setHighestSeverity(severity);
        addAttribute(ATTRIBUTE_HIGHEST_SEVERITY, severity);

        errorList = new ArrayList();
        addError(errorCode, severity, description, lang, location);
    }

    /** 
     * Constructs an <code>ErrorList</code> object by parsing the given 
     * <code>SOAPElement</code> object.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header of which
     *                          the <code>ErrorList</code> is added.
     * @param soapElement       <code>SOAPElement</code> object to be read into
     *                          the <code>ErrorList</code> object.
     * @throws SOAPException 
    */
    ErrorList(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        highestSeverity = getAttributeValue(ATTRIBUTE_HIGHEST_SEVERITY);
        errorList = new ArrayList();
        for (Iterator i=getChildElements(ELEMENT_ERROR) ; i.hasNext() ; ) {
            ExtensionElement error =
                new ExtensionElementImpl(soapEnvelope, (SOAPElement) i.next());
            final String errorCode = error.
                getAttributeValue(ATTRIBUTE_ERROR_CODE);
            final String severity = error.
                getAttributeValue(ATTRIBUTE_SEVERITY);
            String location = error.getAttributeValue(ATTRIBUTE_LOCATION);
            if (location != null && location.length() == 0) {
                location = null;
            }
            Description description = null;
            Iterator j = error.getChildElements(Description.DESCRIPTION);
            if (j.hasNext()) {
                final SOAPElement child = (SOAPElement) j.next();
                final String text = child.getValue();
                final Name name = soapEnvelope.
                    createName(ATTRIBUTE_LANG, NAMESPACE_PREFIX_XML,
                               NAMESPACE_URI_XML);
                final String lang = child.getAttributeValue(name);
                description = new Description(text, lang);
            }
            errorList.add(new ErrorList.Error(errorCode, severity,
                                              description, location));
        }
    }

    /** 
     * Get the highest severity of all the <code>ErrorList.Error</code> objects 
     * in the <code>ErrorList</code>.
     * 
     * @return Highest severity of the errors.
     */
    public String getHighestSeverity() {
        return highestSeverity;
    }

    /** 
     * Gets all <code>ErrorList.Error</code>s in this <code>ErrorList</code>
     */
    public Iterator getErrors() {
        return errorList.iterator();
    }

    /** 
     * Add an <Error> element to the error list.
     *
     * @param errorCode         Error code value.
     * @param severity          Severity of the error.
     * @param description       Description of the error.
     * @throws SOAPException 
     */
    public void addError(String errorCode, String severity, String description)
        throws SOAPException {
        addError(errorCode, severity, description, LANG_TYPE);
    }

    /** 
     * Add an <Error> element to the error list.
     *
     * @param errorCode         Error code value.
     * @param severity          Severity of the error.
     * @param description       Description of the error.
     * @param lang              Language of the description specified in 
     *                          <a href="http://www.ietf.org/rfc/rfc1766.txt">
     *                          RFC 1766</a> and ISO639.
     * @throws SOAPException 
     */
    public void addError(String errorCode, String severity, String description,
                         String lang)
        throws SOAPException {
        addError(errorCode, severity, description, lang, null);
    }

    /** 
     * Add an <Error> element to the error list.
     *
     * @param errorCode         Error code value.
     * @param severity          Severity of the error.
     * @param description       Description of the error.
     * @param lang              Language of the description specified in 
     *                          <a href="http://www.ietf.org/rfc/rfc1766.txt">
     *                          RFC 1766</a> and ISO639.
     * @param location          Location of the message containing error.
     * @throws SOAPException 
     */
    public void addError(String errorCode, String severity, String description,
                         String lang, String location)
        throws SOAPException {
        final ExtensionElement error = addChildElement(ELEMENT_ERROR);
        error.addAttribute(ATTRIBUTE_ERROR_CODE, errorCode);
        error.addAttribute(ATTRIBUTE_SEVERITY, severity);
        if (location != null) {
            error.addAttribute(ATTRIBUTE_LOCATION, location);
        }
        if (description != null) {
            if (lang == null) {
                lang = LANG_TYPE;
            }
            error.addChildElement(Description.DESCRIPTION, description).
                addAttribute(ATTRIBUTE_LANG, NAMESPACE_PREFIX_XML,
                             NAMESPACE_URI_XML, lang);
            errorList.add(new ErrorList.Error(errorCode, severity,
                                              new Description(description,
                                                              lang), location));
        }
        else {
            errorList.add(new ErrorList.Error(errorCode, severity, null, 
                                              location));
        }
        setHighestSeverity(severity);
    }

    /** 
     * Set the highest severity attribute in the <code>ErrorList</code> object.
     * 
     * @param severity          Highest severity string.
     * @throws SOAPException 
     */
    private void setHighestSeverity(String severity) 
        throws SOAPException {
        if (!severity.equalsIgnoreCase(SEVERITY_WARNING) &&
            !severity.equalsIgnoreCase(SEVERITY_ERROR)) {
            throw new EbxmlValidationException
                (EbxmlValidationException.EBXML_ERROR_VALUE_NOT_RECOGNIZED,
                 EbxmlValidationException.SEVERITY_ERROR,
                 "<Error> severity " + severity + " is not recognized");
        }

        if (highestSeverity.equals(SEVERITY_WARNING)) {
            highestSeverity = severity;
        }

        addAttribute(ATTRIBUTE_HIGHEST_SEVERITY, highestSeverity);
    }
}
