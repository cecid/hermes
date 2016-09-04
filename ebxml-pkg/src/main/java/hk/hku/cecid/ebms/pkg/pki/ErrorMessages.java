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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/ErrorMessages.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2003-03-24]
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

import java.util.Iterator;
import java.util.TreeMap;
/**
 * A class holding error codes and the corresponding error messages.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class ErrorMessages {

    public static final int ERR_PKI_UNKNOWN_ERROR = 10200;
    public static final int ERR_PKI_INVALID_KEYSTORE = 10201;
    public static final int ERR_PKI_CANNOT_ENCRYPT = 10202;
    public static final int ERR_PKI_CANNOT_DECRYPT = 10203;
    public static final int ERR_PKI_CANNOT_SIGN = 10204;
    public static final int ERR_PKI_VERIFY_SIGNATURE_FAILED = 10205;

    protected static TreeMap errorMsg;
    protected static boolean isConfigured = false;

    protected static synchronized void configure() {

        if (isConfigured) return;

        errorMsg = new TreeMap();

        load(ERR_PKI_INVALID_KEYSTORE, "Invalid keystore");
        load(ERR_PKI_CANNOT_ENCRYPT, "Cannot encrypt message");
        load(ERR_PKI_CANNOT_DECRYPT, "Cannot decrypt message");
        load(ERR_PKI_CANNOT_SIGN, "Cannot sign message");
        load(ERR_PKI_VERIFY_SIGNATURE_FAILED, 
            "Verification of signature failed");


    }

    protected static void load(int code, String msg) {
        errorMsg.put(new Integer(code), msg);
    }

    public static String getMessage(int code) {
        return getMessage(code, null, "");
    }

    public static String getMessage(int code, String extraMsg) {
        return getMessage(code, null, extraMsg);
    }

    public static String getMessage(int code, Throwable e) {
        return getMessage(code, e, "");
    }

    public static String getMessage(int code, Throwable e, String extraMsg) {
        configure();
    
        StringBuffer sb = new StringBuffer();
        sb.append("[").append(code).append("] ");
        String err = (String) errorMsg.get(new Integer(code));
        if (err == null) {
            sb.append("Unknown error");
        }
        else {
            sb.append(err);
        }
        if (!extraMsg.equals("")) {
            sb.append(" - ").append(extraMsg);
        }
        if (e != null) {
            sb.append("\nException: ").append(e.getClass().getName());
            sb.append("\nMessage: ").append(e.getMessage());
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        configure();
        Iterator keys = errorMsg.keySet().iterator();
        while (keys.hasNext()) {
            Integer err = (Integer) keys.next();
            String msg = (String) errorMsg.get(err);
            System.out.println(err.intValue() + "\t" + msg);
        }

    }
}

