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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/Constants.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * frankielam [2003-01-07]
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

/**
 * This class serves as a bag containing all the constants that fall in the
 * following criteria:
 *
 * (1) Related to property settings
 * (2) Public strings
 * (3) Previously public strings that are declared multiple times in various
 *     locations.
 *
 * @author  Frankie Lam
 * @version $Revision: 1.1 $
 */
public class Constants {

    /**
     * HTTP Header attribute specifying content type
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * HTTP Header attribute specifying content length.
     */
    public static final String CONTENT_LENGTH = "Content-Length";

    /**
     * HTTP Header attribute specifying content id.
     */
    public static final String CONTENT_ID = "Content-Id";

    /**
     * HTTP Header attribute specifying content transfer encoding.
     */
    public static final String CONTENT_TRANSFER_ENCODING =
        "Content-Transfer-Encoding";

    public static final String DEFAULT_CONTENT_TRANSFER_ENCODING = "binary";

    /**
     * MIME boundary.
     */
    public static final String MIME_BOUNDARY = "boundary";

    /**
     * Prefix to be applied to separate different parts of MIME data.
     */
    public static final String MIME_BOUNDARY_PREFIX = "--";

    /**
     * HTTP content type specifying xml data.
     */
    public static final String TEXT_XML_TYPE = "text/xml";

    /**
     * multipart/related content type of MIME message.
     */
    public static final String MULTIPART_RELATED = "multipart/related";

    /**
     * HTTP content type for multi-part data, which is used when the ebXML
     * message contains payload data.
     */
    public static final String MULTIPART_RELATED_TYPE =
        MULTIPART_RELATED
            + "; type=\""
            + TEXT_XML_TYPE
            + "\"; "
            + MIME_BOUNDARY
            + "=";

    /**
     * Content type character set attribute.
     */
    public static final String CHARACTER_SET = "charset";

    /**
     * Default XML character encoding.
     */
    public static final String CHARACTER_ENCODING = "UTF-8";

    /** 
     * CRLF
     */
    public static final String CRLF = "\r\n";

    /*
     * XML tags for Exports
     */

    /**
     * Reference time zone.
     */
    public static final String TIME_ZONE = "GMT";

    /*
     * Default values for Directory Manager
     */

    /**
     * Content type start attribute.
     */
    public static final String START = "start";

    /**
     * HTTP content type specifying binary data, which is a serialized command
     * object in <code>MessageServiceHandler</code>.
     */
    public static final String SERIALIZABLE_OBJECT = "application/octet-stream";


}
