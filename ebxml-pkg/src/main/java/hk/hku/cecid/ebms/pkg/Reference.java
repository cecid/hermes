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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/Reference.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
/**
 * A <code>Reference</code> inside a <code>Manifest</code>
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class Reference extends ExtensionElementImpl {

    /** 
     * The prefix of "href" attribute of this <code>Reference</code>.
     */
    public final static String HREF_PREFIX = "cid:";

    /** 
     * A <code>Schema</code> inside a <code>Reference</code> [ebMSS 3.2.1.1].
     * The schema is not necessarily an XML or DTD schema; it can be any kind
     * of schema such as Database schema.
     */
    public static final class Schema {

        /** 
         * Required URI of the schema.
         */
        private final String location;

        /** 
         * Version identifier of the schema.
         */
        private final String version;

        /** 
         * Initializes the schema object using given location and version
         * inforamtion.
         * 
         * @param location      URI of the schema.
         * @param version       Version identifier of the schema.
         */
        Schema(String location, String version) {
            this.location = location;
            this.version = version;
        }

        /** 
         * Get the location URI of the schema.
         * 
         * @return URI of the schema.
         */
        public String getLocation() {
            return location;
        }

        /** 
         * Get version identifier oif the schema.
         * 
         * @return Version identifier of the schema.
         */
        public String getVersion() {
            return version;
        }
    }

    /** <code>Reference</code> element name */
    static final String REFERENCE = "Reference";

    /* Constants for Reference elements */
    /**
     * Name of the Schema element
     */
    static final String ELEMENT_SCHEMA = "Schema";

    /** 
     * Name of the required href attribute in <code>Reference</code>
     * specifying the URI of the payload.
     */
    static final String ATTRIBUTE_HREF = "href";

    /** 
     * Name of the type attribute in <code>Reference</code> specifying 
     * the type of XLINK link.
     */
    static final String ATTRIBUTE_TYPE = "type";

    /**
     * Name of the optional role attribute in <code>Reference</code>
     * specifying the URI of the payload.
     */
    static final String ATTRIBUTE_ROLE = "role";

    /** 
     * Name of the required Location attribute of the <code>Schema</code>
     * element.
     */
    static final String ATTRIBUTE_LOCATION = "location";

    /** 
     * XLINK link type. It has a fixed value of "simple" [ebMSS 3.2.1].
     */
    private static final String XLINK_TYPE = "simple";

    /** 
     * id attribute of <code>Reference</code> element.
     */
    private String id;

    /** 
     * href attribute of <code>Reference</code> element.
     */
    private String href;

    /** 
     * role attribute of <code>Reference</code> element.
     */
    private String role;

    /** 
     * List of schemas in the <code>Reference</code> element [ebMSS 3.2.1.1].
     */
    private final ArrayList schemas;

    /** 
     * List of descriptions in the <code>Reference</code> 
     * element [ebMSS 3.2.1.2].
     */
    private final ArrayList descriptions;

    /** 
     * Parse the given <code>SOAPElement</code> to reconstruct a 
     * <code>Reference</code> object with <code>SOAPEnvelope</code> as the 
     * parent.
     * 
     * @param soapEnvelope      <code>SOAPEnvelope</code> object into which
     *                          the <code>Reference</code> object is added.
     * @param soapElement       <code>SOAPElement</code> object to be parsed
     *                          to reconstruct the <code>Reference</code>
     *                          object.
     * @throws SOAPException 
     */
    Reference(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        this.id = getAttributeValue(ATTRIBUTE_ID);
        this.href = getAttributeValue(ATTRIBUTE_HREF, NAMESPACE_PREFIX_XLINK,
                                      NAMESPACE_URI_XLINK);
        this.role = getAttributeValue(ATTRIBUTE_ROLE, NAMESPACE_PREFIX_XLINK,
                                      NAMESPACE_URI_XLINK);
        schemas = new ArrayList();
        descriptions = new ArrayList();
        for (Iterator i=getChildElements(ELEMENT_SCHEMA) ; i.hasNext() ; ) {
            ExtensionElement schema =
                new ExtensionElementImpl(soapEnvelope, (SOAPElement) i.next());
            final String location = schema.
                getAttributeValue(ATTRIBUTE_LOCATION);
            final String version = schema.getAttributeValue(ATTRIBUTE_VERSION);
            schemas.add(new Schema(location, version));
        }
        for (Iterator i=getChildElements(Description.DESCRIPTION) ;
             i.hasNext() ; ) {
            final SOAPElement child = (SOAPElement) i.next();
            final String text = child.getValue();
            final Name name = soapEnvelope.
                createName(ATTRIBUTE_LANG, NAMESPACE_PREFIX_XML,
                           NAMESPACE_URI_XML);
            final String lang = child.getAttributeValue(name);
            descriptions.add(new Description(text, lang));
        }
    }

    /** 
     * Constructs a <code>Reference</code> object with the given mandatory
     * fields.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code> object into which
     *                          the <code>Reference</code> object is added.
     * @param id                ID attribute of the <code>Reference</code>
     *                          object.
     * @param href              href attribute of the <code>Reference</code>
     *                          object.
     * @throws SOAPException 
    */
    /*
    Reference(SOAPEnvelope soapEnvelope, String id, String href)
        throws SOAPException {
        super(soapEnvelope, REFERENCE, false);
        this.id = id;
        this.href = href;
        this.role = null;
        addAttribute(ATTRIBUTE_ID, id);
        addAttribute(ATTRIBUTE_TYPE, NAMESPACE_PREFIX_XLINK,
                     NAMESPACE_URI_XLINK, XLINK_TYPE);
        addAttribute(ATTRIBUTE_HREF, NAMESPACE_PREFIX_XLINK,
                     NAMESPACE_URI_XLINK, href);
        schemas = new ArrayList();
        descriptions = new ArrayList();
    }
    */

    /** 
     * Create a <code>Schema</code> object using the given location and
     * version, and add it to the <code>Reference</code> object.
     * 
     * @param location      URI of the schema.
     * @param version       Version identifier of the schema.
     * @throws SOAPException 
     */
    public void addSchema(String location, String version)
        throws SOAPException {
        if (location != null) {
            final ExtensionElement schema = addChildElement(ELEMENT_SCHEMA);
            schema.addAttribute(ATTRIBUTE_LOCATION, location);
            if (version != null) {
                schema.addAttribute(ATTRIBUTE_VERSION, version);
            }
            schemas.add(new Schema(location, version));
        }
    }

    /**
     * Add <Description> element with default <code>xml:lang</code> 
     * 
     * @param description   Description to be added to the 
     *                      <code>Reference</code>.
     * @throws SOAPException 
     */
    public void addDescription(String description) 
        throws SOAPException {
        addDescription(description, LANG_TYPE);
    }

    /**
     * Add <Description> element with specified <code>xml:lang</code> 
     * 
     * @param description   Description to be added to the 
     *                      <code>Reference</code>.
     * @param lang              Language of the description specified in 
     *                          <a href="http://www.ietf.org/rfc/rfc1766.txt">
     *                          RFC 1766</a> and ISO639.
     * @throws SOAPException 
     */
    public void addDescription(String description, String lang)
        throws SOAPException {
        if (description != null) {
            if (lang == null) {
                lang = LANG_TYPE;
            }
            addChildElement(Description.DESCRIPTION, description).
                addAttribute(ATTRIBUTE_LANG, NAMESPACE_PREFIX_XML,
                             NAMESPACE_URI_XML, lang);
            descriptions.add(new Description(description, lang));
        }
    }

    /** 
     * Get ID attribute of the <code>Reference</code> element.
     * 
     * @return ID attribute.
     */
    public String getId() {
        return id;
    }

    void setId(String id) throws SOAPException {
        if (this.id != null) {
            throw new SOAPValidationException(SOAPValidationException.
                SOAP_FAULT_CLIENT, "<" + NAMESPACE_PREFIX_XLINK + ":" +
                ATTRIBUTE_ID + "> has already been set in <" +
                NAMESPACE_PREFIX_EB + ":" + REFERENCE + ">!");
        }
        this.id = id;
        addAttribute(ATTRIBUTE_ID, id);
        addAttribute(ATTRIBUTE_TYPE, NAMESPACE_PREFIX_XLINK,
                     NAMESPACE_URI_XLINK, XLINK_TYPE);
    }

    /** 
     * Get href attribute of the <code>Reference</code> element.
     * 
     * @return Href attribute.
     */
    public String getHref() {
        return href;
    }

    void setHref(String href) throws SOAPException {
        if (this.href != null) {
            throw new SOAPValidationException(SOAPValidationException.
                SOAP_FAULT_CLIENT, "<" + NAMESPACE_PREFIX_XLINK + ":" +
                ATTRIBUTE_HREF + "> has already been set in <" +
                NAMESPACE_PREFIX_EB + ":" + REFERENCE + ">!");
        }
        if (this.id == null) {
            throw new SOAPValidationException(SOAPValidationException.
                SOAP_FAULT_CLIENT, "<" + NAMESPACE_PREFIX_XLINK + ":" +
                ATTRIBUTE_ID + "> has not been set in <" +
                NAMESPACE_PREFIX_EB + ":" + REFERENCE + ">!");
        }
        this.href = href;
        addAttribute(ATTRIBUTE_HREF, NAMESPACE_PREFIX_XLINK,
                     NAMESPACE_URI_XLINK, href);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) throws SOAPException {
        if (this.role != null) {
            throw new SOAPValidationException(SOAPValidationException.
                SOAP_FAULT_CLIENT, "<" + NAMESPACE_PREFIX_XLINK + ":" +
                ATTRIBUTE_ROLE + "> has already been set in <" +
                NAMESPACE_PREFIX_EB + ":" + REFERENCE + ">!");
        }
        this.role = role;
        addAttribute(ATTRIBUTE_ROLE, NAMESPACE_PREFIX_XLINK,
                     NAMESPACE_URI_XLINK, role);
    }

    /** 
     * Get the schemas in the <code>Reference</code> element.
     * 
     * @return An iterator pointing to a list of schemas of type
     *         <code>Schema</code>.
     */
    public Iterator getSchemas() {
        return schemas.iterator();
    }

    /** 
     * Get the descriptions in the <code>Reference</code> element.
     * 
     * @return An iterator pointing to a list of descriptions of type
     *         <code>Description</code>.
     */
    public Iterator getDescriptions() {
        return descriptions.iterator();
    }

    /**
     * Gets the number of <code>Description</code> elements in this <code>Reference</code>.
     * 
     * @return The number of <code>Description</code> elements.
     */
    public int getDescriptionCount() {
        return descriptions.size();
    }
}
