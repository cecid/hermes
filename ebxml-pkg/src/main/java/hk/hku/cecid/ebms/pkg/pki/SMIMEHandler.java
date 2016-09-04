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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/SMIMEHandler.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * achong [2002-08-01]
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

import java.security.Security;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
/**
It is the base class of SMIMEEncrypter and SMIMEDecrypter. The BouncyCastle 's
SMIME engine only works properly with BouncyCastle 's JCE provider. The subclass
should call the method initiate(), which add the BouncyCastle 's Provider and
setup which DataHandler for what types of content type.
*/
public class SMIMEHandler {

    private static boolean initiated;
        
    /**
    Add the BouncyCastle 's Provider and setup which DataHandler for what types 
    of content type.
    */
    static void initiate() {
        if (!initiated) {
            synchronized(SMIMEHandler.class) {
                if (!initiated) {
                    Security.addProvider(new BouncyCastleProvider());   
                    setupMailCommandMap();
                    initiated = true;
                }
            }
        }
    }

    /**
    Setup the MailCommandMap programmatically. It sets which ContentType 
    should be handled by which type of DataHandler.
    */
    private static void setupMailCommandMap() {
        MailcapCommandMap mailcap = (MailcapCommandMap)CommandMap.
        getDefaultCommandMap();
        mailcap.addMailcap("application/pkcs7-mime;; x-java-content-handler=" + 
        "org.bouncycastle.mail.smime.handlers.pkcs7_mime");
        mailcap.addMailcap("application/x-pkcs7-mime;; x-java-content-handler" +
        "=org.bouncycastle.mail.smime.handlers.x_pkcs7_mime");
        mailcap.addMailcap("application/pkcs7-signature;; x-java-content-" + 
        "handler=org.bouncycastle.mail.smime.handlers.pkcs7_signature");
        mailcap.addMailcap("application/x-pkcs7-signature;; x-java-content-" + 
        "handler=org.bouncycastle.mail.smime.handlers.x_pkcs7_signature");
        mailcap.addMailcap("multipart/signed;; x-java-content-handler=" + 
        "org.bouncycastle.mail.smime.handlers.multipart_signed");
        CommandMap.setDefaultCommandMap(mailcap);
    }
}
