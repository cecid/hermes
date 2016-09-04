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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/Signature.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import hk.hku.cecid.ebms.pkg.pki.CertResolver;
import hk.hku.cecid.ebms.pkg.validation.SOAPValidationException;

import java.util.ArrayList;
import java.util.Iterator;

import javax.activation.DataSource;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
/**
 * <P>An ebXML <code>Signature</code> is a <code>HeaderElement</code>
 * in <code>HeaderContainer</code> [ebMSS 4.1.1 and 4.1.3].</P>
 *
 * <P>This class is a partial implementation of the XML-Signature Syntax and
 * Processing / RFC 3275. Please refer to these documents for details.</P>
 *
 * @see <a href="http://www.w3.org/TR/xmldsig-core/">
 *      XML-Signature Syntax and Processing</a>
 * @author cyng
 * @version $Revision: 1.1 $
 */
public abstract class Signature extends HeaderElement {

    /** 
     * Name of the Signature element [ebMSS 4.1.1, XMLDSIG 4.1].
     */
    public static final String ELEMENT_SIGNATURE = "Signature";

    /** 
     * Name of the SignedInfo element [ebMSS 4.1.3, XMLDSIG 4.3] containing
     * information about the signature. They include:
     * <ul>
     * <li>Canonicalization method</li>
     * <li>Signature method</li>
     * <li>References made during signature generation</li>
     * </ul>
     */
    public static final String ELEMENT_SIGNED_INFO = "SignedInfo";

    /** 
     * Name of the Canonicalization Method element [ebMSS 4.1.3, XMLDSIG 4.3.1]
     * used in signature generation.
     */
    public static final String ELEMENT_CANONICALIZATION_METHOD =
        "CanonicalizationMethod";

    /** 
     * Name of the Signature Method element [ebMSS 4.1.3, XMLDSIG 4.3.2].
     */
    public static final String ELEMENT_SIGNATURE_METHOD = "SignatureMethod";

    /** 
     * Name of the Signature Value element [ebMSS 4.1.3, XMLDSIG 4.2].
     */
    public static final String ELEMENT_SIGNATURE_VALUE = "SignatureValue";

    /** 
     * Name of the Reference element which specifies a digest algorithm and
     * digest value and other optional information [ebMSS 4.1.3, XMLDSIG 4.3.3].
     */
    public static final String ELEMENT_REFERENCE = "Reference";

    /** 
     * Name of the DigestMethod element which specifies the digest algorithm 
     * to be applied to the signed object [XMLDSIG 4.3.3.5].
     */
    public static final String ELEMENT_DIGEST_METHOD = "DigestMethod";

    /** 
     * Name of the DigestValue element which contains the encoded value of the
     * digest [XMLDSIG 4.3.3.6].
     */
    public static final String ELEMENT_DIGEST_VALUE = "DigestValue";

    /** 
     * Name of the Transforms element which is an ordered list of 
     * transformations applied to obtain the data object to be signed 
     * [XMLDSIG 4.3.3.4]. 
     */
    public static final String ELEMENT_TRANSFORMS = "Transforms";

    /** 
     * Name of the Transform element which describes the transformation
     * applied on the data object [XMLDSIG 4.3.3.4]. Transformation algorithms
     * are described in XMLDSIG 6.6: Transform Algorithms. 
     */
    public static final String ELEMENT_TRANSFORM = "Transform";

    /** 
     * Name of the XPath element [XMLDSIG 6.6.3].
     */
    public static final String ELEMENT_XPATH = "XPath";

    /** 
     * Name of the Object element [XMLDSIG 4.5].
     */
    public static final String ELEMENT_OBJECT = "Object";

    /** 
     * Name of the KeyInfo element which enables the recipient(s) to 
     * obtain the key needed to validate the signature [XMLDSIG 4.4].
     */
    public static final String ELEMENT_KEY_INFO = "KeyInfo";

    /** 
     * Name of the X509Data element which contains identifier(s) of
     * keys or X509 certificates [XMLDSIG 4.4.4].
     */
    public static final String ELEMENT_X509_DATA = "X509Data";

    /** 
     * Name of the X509Certificate element which contains a binary (ASN.1 DER)
     * X.509 Certificate [XMLDSIG 4.4].
     */
    public static final String ELEMENT_X509_CERTIFICATE = "X509Certificate";

    /** 
     * Name of the Algorithm attribute.
     */
    public static final String ATTRIBUTE_ALGORITHM = "Algorithm";

    /** 
     * Name of the Id attribute.
     */
    public static final String ATTRIBUTE_ID = "Id";

    /** 
     * Name of the URI attribute.
     */
    public static final String ATTRIBUTE_URI = "URI";

    /** 
     * Namespace prefix of <code>Signature</code>. 
     */
    public static final String NAMESPACE_PREFIX_DS = "ds";

    /** 
     * Namespace URI of <code>Signature</code>. 
     */
    public static final String NAMESPACE_URI_DS =
        "http://www.w3.org/2000/09/xmldsig#";

    /** 
     * Namespace URI of the canonicalization method as specified in
     * <a href="http://www.w3.org/TR/xml-exc-c14n/">Exclusive XML 
     * Canonicalization Version 1.0</a>.
     */
    public static final String CANONICALIZATION_METHOD =
        "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

    /** 
     * Name of the digital signature method required, qualified by the
     * digital signature namespace [XMLDSIG 6.1].
     */
    public static final String SIGNATURE_METHOD =
        NAMESPACE_URI_DS + "dsa-sha1";

    /** 
     * Name of the enveloped signature required, qualified by the digital
     * signature namespace [XMLDSIG 6.1].
     */
    public static final String TRANSFORM_ALGORITHM_ENVELOPED_SIGNATURE =
        NAMESPACE_URI_DS + "enveloped-signature";

    /** 
     * Name of the XPath transform algorithm recommended [XMLDSIG 6.1].
     */
    public static final String TRANSFORM_ALGORITHM_XPATH =
        "http://www.w3.org/TR/1999/REC-xpath-19991116";

    /**
     * XPath transform string used in the implementation.
     */
    public static final String TRANSFORM_XPATH =
        "not(ancestor-or-self::node()[@"
        + NAMESPACE_PREFIX_SOAP_ENVELOPE + XML_NS_SEPARATOR + HeaderElement.
        ATTRIBUTE_ACTOR + "=\"" + HeaderElement.ACTOR_NEXT_MSH_URN + "\"] | "
        + "ancestor-or-self::node()[@" + NAMESPACE_PREFIX_SOAP_ENVELOPE
        + XML_NS_SEPARATOR + HeaderElement.ATTRIBUTE_ACTOR + "=\""
        + HeaderElement.ACTOR_NEXT_MSH_SCHEMAS + "\"])";

    /** 
     * Name of the Digest method required, qualified by namespace [XMLDSIG 6.1].
     */
    public static final String DIGEST_METHOD = NAMESPACE_URI_DS + "sha1";

    /** 
     * Name of the digital signature algorithm.
     */
    public static final String SIGNATURE_ALGORITHM = "SHA1withDSA";

    /** 
     * Name of the message digest algorithm.
     */
    public static final String DIGEST_ALGORITHM = "SHA";

    /** 
     * Default character coding.
     */
    public static final String CHARACTER_ENCODING = "utf-8";

    final ArrayList references;

    String signatureValue;

    /** 
     * Initializes the <code>Signature</code> object using the given
     * <code>SOAPEnvelope</code>, local name, namespace prefix and namespace
     * URI.
     * 
     * @param soapEnvelope      <code>SOAPEnvelope</code> on which digital
     *                          signature will be applied.
     * @param localName         Local name of the signature element.
     * @param prefix            Namespace prefix of the signature element.
     * @param uri               Namespace URI of the signature element.
     * @throws SOAPException 
     */
    Signature(SOAPEnvelope soapEnvelope, String localName, String prefix,
              String uri) throws SOAPException {
        super(soapEnvelope, localName, prefix, uri);
        references = new ArrayList();
        signatureValue = null;
    }

    Signature(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        references = new ArrayList();
        Iterator childElements = getChildElements(soapEnvelope.createName
            (ELEMENT_SIGNED_INFO, NAMESPACE_PREFIX_DS, NAMESPACE_URI_DS));
        if (childElements.hasNext()) {
            SOAPElement signedInfo = (SOAPElement) childElements.next();
            childElements = signedInfo.getChildElements(soapEnvelope.createName
                (SignatureReference.SIGNATURE_REFERENCE, NAMESPACE_PREFIX_DS,
                 NAMESPACE_URI_DS));
            if (childElements.hasNext()) {
                while (childElements.hasNext()) {
                    references.add(new SignatureReference(soapEnvelope,
                        (SOAPElement) childElements.next()));
                }
            }
            else {
                throw new SOAPValidationException(SOAPValidationException.
                    SOAP_FAULT_CLIENT, "<" + NAMESPACE_PREFIX_DS + ":" +
                    SignatureReference.SIGNATURE_REFERENCE +
                    "> is not found in <" + NAMESPACE_PREFIX_DS + ":" +
                    ELEMENT_SIGNATURE + ">!");
            }
        }
        else {
            throw new SOAPValidationException(SOAPValidationException.
                SOAP_FAULT_CLIENT, "<" + NAMESPACE_PREFIX_DS + ":" +
                ELEMENT_SIGNED_INFO + "> is not found in <" +
                NAMESPACE_PREFIX_DS + ":" + ELEMENT_SIGNATURE + ">!");
        }

        childElements = getChildElements(soapEnvelope.createName
            (ELEMENT_SIGNATURE_VALUE, NAMESPACE_PREFIX_DS, NAMESPACE_URI_DS));
        if (childElements.hasNext()) {
            signatureValue = ((SOAPElement) childElements.next()).getValue();
        }
        else {
            throw new SOAPValidationException(SOAPValidationException.
                SOAP_FAULT_CLIENT, "<" + NAMESPACE_PREFIX_DS + ":" +
                ELEMENT_SIGNATURE_VALUE + "> is not found in <" +
                NAMESPACE_PREFIX_DS + ":" + ELEMENT_SIGNATURE + ">!");
        }
    }

    /** 
     * Get a new instance of <code>Signature</code> which will be used to
     * sign or verify the given <code>EbxmlMessage</code>
     */
    static Signature newInstance(EbxmlMessage ebxmlMessage)
        throws SOAPException {
        synchronized(PKISignatureImpl.class) {
            return new PKISignatureImpl(ebxmlMessage);
        }
    }

    static Signature newInstance(SOAPEnvelope soapEnvelope,
        SOAPElement soapElement) throws SOAPException {
        return new PKISignatureImpl(null, soapEnvelope, soapElement);
    }

    static Signature newInstance(EbxmlMessage ebxmlMessage,
        SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        return new PKISignatureImpl(ebxmlMessage, soapEnvelope, soapElement);
    }

    public Iterator getReferences() {
        return references.iterator();
    }

    String getSignatureValue() {
        return signatureValue;
    }

    /**
     * Add a reference URI to this <code>Signature</code>.
     * 
     * @param uri                   Reference URI to be added.
     * @throws SignatureException 
     */
    abstract void addReference(String uri) throws SignatureException;

    /** 
     * Sign the <code>EbxmlMessage</code> with the <code>username<code> and
     * <code>password</code> used to retrieve private key from the keystore.
     *
     * @param username              User name required to open the private key.
     * @param password              Password required to open the private key. 
     * @throws SignatureException 
    */
    abstract void sign(String username, char[] password)
        throws SignatureException;

    /** 
     * Sign the <code>EbxmlMessage</code> with the <code>username<code> and
     * <code>password</code> used to retrieve private key from the keystore
     *
     * @param username              User name required to open the private key.
     * @param password              Password required to open the private key. 
     * @param keyStoreLocation      File location of the keystore.
     * @param algorithm             Name of the algorithm used to sign the 
     *                              message.
     * @throws SignatureException 
    */
    abstract void sign(String username, char[] password,
                       String keyStoreLocation, String algorithm)
        throws SignatureException;

    /** 
     * Sign the <code>EbxmlMessage</code> with the <code>username<code> and
     * <code>password</code> used to retrieve private key from the keystore
     *
     * @param username              User name required to open the private key.
     * @param password              Password required to open the private key. 
     * @param keyStoreLocation      File location of the keystore.
     * @param algorithm             Name of the algorithm used to sign the 
     *                              message.
     * @param digestAlgo            Name of the algorithm used to make the
     *                              digest.
     * @param signEnvelopeOnly      whether sign the envelope only.,
     * @throws SignatureException 
    */
    abstract void sign(String username, char[] password,
                       String keyStoreLocation, String algorithm,
                       String digestAlgo, boolean signEnvelopeOnly)
        throws SignatureException;

    /** 
     * Sign the <code>EbxmlMessage</code> with the <code>username<code> and
     * <code>password</code> used to retrieve private key from the keystore
     *
     * @param username              User name required to open the private key.
     * @param password              Password required to open the private key. 
     * @param keyStoreLocation      File location of the keystore.
     * @throws SignatureException 
    */
    abstract void sign(String username, char[] password,
                       String keyStoreLocation) throws SignatureException;

    /** 
     * Sign the <code>EbxmlMessage</code> with the <code>username<code> and
     * <code>password</code> used to retrieve private key from the keystore.
     *
     * @param username              User name required to open the private key.
     * @param password              Password required to open the private key. 
     * @param keyStoreLocation      File location of the keystore
     * @param type              
     * @param provider 
     * @throws SignatureException 
    */
    abstract void sign(String username, char[] password,
                       String keyStoreLocation, String type, String provider)
        throws SignatureException;

    /** 
     * Sign the <code>EbxmlMessage</code> using the private key supplied and 
     * attach X.509 certificates to the signature.
     *
     * @param privateKey            Private key used to sign the message.
     * @param certificates          List of certificates to be included in the
     *                              signature.
     * @throws SignatureException 
    */
    abstract void sign(java.security.PrivateKey privateKey,
                       java.security.cert.X509Certificate[] certificates)
        throws SignatureException;

    /** 
     * Verify the message using trusted keystore.
     * 
     * @param password              Password to open the keystore.
     * @param keyStoreLocation      File location of the key store.
     * @param certResolver          Resolve a certificate chain in order to
     *                              verify the message. If it is null, the
     *                              certificate chain is extracted directly
     *                              from the <ds:Signature> element in the
     *                              message.
     *
     * @return true if the digital signature is valid; false otherwise.
     * @throws SignatureException 
     */
    abstract boolean verify(char[] password, String keyStoreLocation,
                            CertResolver certResolver, DataSource datasource)
        throws SignatureException;

    /**
     * Verify the XML signature of the <code>EbxmlMessage</code> 
     * 
     * @param documentElement       Document fragment which contains the 
     *                              digital signature.  
     * @param publicKey             Public key used to verify the digitall
     *                              signature.
     * @return true if digital signature is valid; false otherwise.
     * @throws SignatureException 
     */
    abstract boolean verify(org.w3c.dom.Element documentElement,
                            java.security.PublicKey publicKey)
        throws SignatureException;

    /**
     * Verify the XML signature of the <code>EbxmlMessage</code> 
     * 
     * @param publicKey             Public key used to verify the digitall
     *                              signature.
     * @return true if digital signature is valid; false otherwise.
     * @exception SignatureException
     */
    abstract boolean verify(java.security.PublicKey publicKey)
        throws SignatureException;
}
