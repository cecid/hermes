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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/DocumentResolver.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2002-05-16]
 *
 * Code reviewed by:
 *
 * username [YYYY-MM-DD]
 *
 * Remarks:
 *
 * =====
 */

package hk.hku.cecid.ebms.pkg.pki;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.w3c.dom.Attr;
/**
 * This class is needed by the Apache XML Security library for locating
 * and loading the document attachments.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class DocumentResolver extends ResourceResolverSpi {

    /** 
     * Internal variable for holding the document parameters.
     */
    protected DocumentDetail[] docs;

    /** 
     * Construct with an array of document parameters.
     *
     * @param docs array of document parameters
     */
    public DocumentResolver(DocumentDetail[] docs) {
        super();
        this.docs = docs;
    }

    /**
     * Gets the document (encapsulated in the XMLSignatureInput object)
     * by specifying the URI.
     *
     * @param uri
     * @param baseUri
     * @return the document encapsulated in the XMLSignatureInput object
     */
    public XMLSignatureInput engineResolve(Attr uri, String baseUri)
        throws ResourceResolverException {
    
        String href = uri.getNodeValue();

        if (!href.startsWith("cid:")) {
            Object exArgs[] = {"Reference URI does not start with 'cid:'"};
            throw new ResourceResolverException(href, exArgs, uri, baseUri);
        }

        int found = -1;
        for (int i=0 ; i<docs.length ; i++) {
            if (docs[i].uri != null && docs[i].uri.equals(href)) {
                found = i;
                break;
            }
        }

        if (found < 0) {
            Object exArgs[] = {"Reference URI = " + href + " does not exist!"};
            throw new ResourceResolverException(href, exArgs, uri, baseUri);
        }

        XMLSignatureInput input;
        try {
            final InputStream in = docs[found].stream;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final byte[] buffer = new byte[4096];
            for (int c=in.read(buffer) ; c!=-1 ; c=in.read(buffer))
                out.write(buffer, 0, c);
            input = new XMLSignatureInput(out.toByteArray());
        }
        catch (Exception e) {
            throw new ResourceResolverException(href, e, uri, baseUri);
        }
        input.setSourceURI(href);
        input.setMIMEType(docs[found].contentType);

        return input;
    }

    /**
     * Sees whether the resolver can resolve the document specified by
     * the URI or not.
     *
     * @param uri
     * @param baseUri
     * @return true if the resolver can locate the document specified, false
     *         if otherwise.
     */
    public boolean engineCanResolve(Attr uri, String baseUri) {
        String href = uri.getNodeValue();

        if (href.startsWith("cid:")) {
            for (int i=0 ; i<docs.length ; i++) {
                if (docs[i].uri != null && docs[i].uri.equals(href)) {
                    return true;
                }
            }
        }
        return false;
    }
}
