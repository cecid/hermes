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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/CertSource.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2002-04-30]
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
/**
 * This class loads a X509 certificate file. Basically this class only acts as 
 * a convenience wrapper of java.security.cert.X509Certificate.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class CertSource {

    /**
     * internal X509 certificate storage.
     */
    protected X509Certificate x509Cert;

    /**
     * Default constructor. The user should call load() to further 
     * initialize the certificate.
     */
    public CertSource() {
        x509Cert = null;
    }

    /**
     * Constructor with initialization parameters. The certificate will be 
     * loaded from the file specified.
     *
     * @param certFile the certificate file
     * @throws CertificateException if the file specified cannot be read, or
     *                              any errors occurred when loading the file.
     */
    public CertSource(File certFile) throws CertificateException {
        this();
        load(certFile);
    }

    /**
     * Constructor with initialization parameters. The certificate will be 
     * loaded from the file specified.
     *
     * @param certFile the file name of the certificate file
     * @throws CertificateException if the file specified cannot be read, or
     *                              any errors occurred when loading the file.
     */
    public CertSource(String certFile) throws CertificateException {
        this();
        load(certFile);
    }

    /**
     * Constructor with initialization parameters. The class will be 
     * initialized with the specified certificate.
     *
     * @param cert the preloaded certificate
     */
    public CertSource(X509Certificate cert) {
        x509Cert = cert;
    }

    /**
     * Loads the certificate file.
     *
     * @param certFile the certificate file
     * @throws CertificateException if the file specified cannot be read, or
     *                              any errors occurred when loading the file.
     */
    public void load(File certFile) throws CertificateException {
        try {
            InputStream inStream = new FileInputStream(certFile);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            x509Cert = (java.security.cert.X509Certificate) 
                cf.generateCertificate(inStream);
            inStream.close();
        }
        catch (IOException e) {
            throw new CertificateException("IO exception when loading " +
                "certificate file.\n" + e.getMessage());
        }
    }

    /**
     * Loads the certificate file.
     *
     * @param certFile the file name of the certificate file
     * @throws CertificateException if the file specified cannot be read, or
     *                              any errors occurred when loading the file.
     */
    public void load(String certFile) throws CertificateException {
        this.load(new File(certFile));
    }

    /**
     * Checks whether the certificate is valid in current time.
     *
     * @return true if the certificate is still valid, false if otherwise.
     */
    public boolean isValid() throws InitializationException {
        if (x509Cert == null) {
            throw new InitializationException("Not yet initialized.");
        }
        boolean ret = false;
        try {
            x509Cert.checkValidity();
            ret = true;
        }
        catch (CertificateExpiredException e) {}
        catch (CertificateNotYetValidException e) {}
        return ret;
    }

    /**
     * Checks whether the certificate is valid in the specified time.
     *
     * @param d   the specified time
     * @return true if the certificate is valid, false if otherwise.
     */
    public boolean isValid(Date d) throws InitializationException {
        if (x509Cert == null) {
            throw new InitializationException("Not yet initialized.");
        }
        boolean ret = false;
        try {
            x509Cert.checkValidity(d);
            ret = true;
        }
        catch (CertificateExpiredException e) {}
        catch (CertificateNotYetValidException e) {}
        return ret;
    }

    /**
     * Verifies whether the certificate is signed by the private key 
     * corresponding to the specified public key.
     *
     * @param pubKey the public key for verification
     * @return true if the verification is passed, false if otherwise.
     */
    public boolean verify(PublicKey pubKey) throws InitializationException {
        if (x509Cert == null) {
            throw new InitializationException("Not yet initialized.");
        }
        boolean ret = false;
        try {
            x509Cert.verify(pubKey);
            ret = true;
        }
        catch (CertificateException e) {}
        catch (NoSuchAlgorithmException e) {}
        catch (InvalidKeyException e) {}
        catch (NoSuchProviderException e) {}
        catch (SignatureException e) {}
        return ret;
    }

    /**
     * Verifies whether the certificate is signed by the private key 
     * corresponding to public key in the specified certificate.
     *
     * @param cert the certificate for verification
     * @return true if the verification is passed, false if otherwise.
     */
    public boolean verify(Certificate cert) throws InitializationException {
        if (x509Cert == null) {
            throw new InitializationException("Not yet initialized.");
        }
        return verify(x509Cert.getPublicKey());
    }

    /**
     * Verifies whether the certificate is signed by the private key 
     * corresponding to public key in the specified certificate.
     *
     * @param cert the certificate for verification
     * @return true if the verification is passed, false if otherwise.
     */
    public boolean verify(CertSource cert) throws InitializationException {
        if (x509Cert == null) {
            throw new InitializationException("Not yet initialized.");
        }
        return verify(x509Cert.getPublicKey());
    }

    /**
     * Gets the public key in this certificate.
     *
     * @return the public key in this certificate.
     */
    public PublicKey getPublicKey() {
        try {
            return x509Cert.getPublicKey();
        }
        catch (NullPointerException e) {
            return null;
        }
    }
    
    /**
     * Gets the X509Certificate stored internally.
     *
     * @return the X509Certificate stored internally.
     */
    public X509Certificate getInternalCert() {
        return x509Cert;
    }

    /**
     * Gets the distinguished name (DN) of the issuer of the certificate.
     *
     * @return the DN of the issuer
     * @throws InitializationException the object is not yet initialized
     */
    public String getIssuer() throws InitializationException {
        if (x509Cert == null) {
            throw new InitializationException("Not yet initialized.");
        }
        return x509Cert.getIssuerDN().getName();
    }
}
