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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/PayloadContainer.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
/**
 * <P>An encapsulation of the payload container in an <code>EbxmlMessage</code>
 * [ebMSS 2.1.4].</P>
 *
 * <P>A Payload Container contains an ebXML MIME header as well as application
 * payload, as illustrated in the following diagram:</P>
 *
 * <TT>
 * <BR/>1. Content-ID: <unique-id@cecid.hku.hk>    
 * <BR/>2. Content-type: application/xml           
 * <BR/>3.
 * <BR/>4.&lt;PurchaseOrder&gt;                  
 * <BR/>5.  &lt;Product&gt;...&lt;/Product&gt;
 * <BR/>6.  ...
 * <BR/>7.&lt;/PurchaseOrder&gt;
 * </TT>
 *
 * <BR/>
 * <BR/>Line 1-2: ebXML MIME headers.
 * <BR/>Line 4-7: Application payload
 * <P>This class encapsulates the structure of payload container in an ebXML 
 * message.</P>
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class PayloadContainer {

    /** 
     * The prefix of "href" attribute of this <code>PayloadContainer</code>.
     */
    public final static String HREF_PREFIX = Reference.HREF_PREFIX;

    /**
     * <code>DataHandler</code> representing this
     * <code>PayloadContainer</code>.
     */
    private final DataHandler dataHandler;

    /**
     * <code>Reference</code> inside the <code>Manifest</code> associated
     * with this <code>PayloadContainer</code>.
     */
    private final Reference reference;

    /*
     * MIME headers of this <code>PayloadContainer</code>.
     */
    private final HashMap mimeHeaders;

    /** 
     * Create a <code>PayloadContainer</code> from the specified
     * <code>DataHandler</code>.
     */
    public PayloadContainer(DataHandler dataHandler, String contentId,
                            Reference reference) {
        this.dataHandler = dataHandler;
        this.reference = reference;
        this.mimeHeaders = new HashMap();
        String id = contentId;
        if (id != null && id.startsWith("<") && EbxmlMessage.needPatch) {
            id = id.substring(1);
            if (id.endsWith(">")) {
                id = id.substring(0, id.length() - 1);
            }
        }
        mimeHeaders.put(Constants.CONTENT_TYPE, dataHandler.getContentType());
        mimeHeaders.put(Constants.CONTENT_ID, id);
    }

    /** 
     * Get contentId. 
     */
    public String getContentId() {
        return (String) mimeHeaders.get(Constants.CONTENT_ID);
    }

    /** 
     * Get "href" attribute which is equal to the prefixed contentId.
     */
    public String getHref() {
        return HREF_PREFIX + getContentId();
    }

    /** 
     * Get content type of this attachment. 
     */
    public String getContentType() {
        return dataHandler.getContentType();
    }

    /*
     * Get all MIME headers of this attachment.
     */
    public Map getMimeHeaders() {
        return mimeHeaders;
    }

    /*
     * Set a MIME header of this attachment.
     */
    public void setMimeHeader(String name, String value) {
        if (name != null) {
            mimeHeaders.put(name, value);
        }
    }

    /** 
     * Get <code>javax.activation.DataHandler</code> of this attachment.
     */
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * Get <code>Reference</code> inside the <code>Manifest</code> associated
     * with this <code>PayloadContainer</code>.
     */
    public Reference getReference() {
        return reference;
    }
    
    /**
     * Get the content length of this payload. Note that the content length 
     * returned will be -1 if this container is not created from 
     * AttachmentDataSource. Also, the length returned does not take 
     * Content-Transfer-Encoding into account, if any.
     * 
     * @return content length of this payload
     */
    public long getContentLength() {
        DataSource source = dataHandler.getDataSource();
        if (source instanceof AttachmentDataSource) {
            AttachmentDataSource ads = (AttachmentDataSource) source;
            return ads.getLength();
        }
        else {
            return -1;
        }
    }
}
