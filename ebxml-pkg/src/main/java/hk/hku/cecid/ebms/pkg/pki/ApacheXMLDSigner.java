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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/ApacheXMLDSigner.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
/**
 * This class hides the details for digital signature. The digital signature
 * routines are provided by the Apache XML Security library. We defined a
 * standard way to have the document signed as interface. Different classes
 * will implement the interface using different library behind.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class ApacheXMLDSigner implements XMLDSigner {

    /**
     * Logger
     */
    protected static Logger logger = Logger.getLogger(ApacheXMLDSigner.class);

    /**
     * Name of the Signature element [ebMSS 4.1.1, XMLDSIG 4.1]
     */
    public static final String ELEMENT_SIGNATURE = "Signature";

    /**
     * Name of the KeyInfo element which enables the recipient(s) to
     * obtain the key needed to validate the signature [XMLDSIG 4.4]
     */
    public static final String ELEMENT_KEY_INFO = "KeyInfo";

    /**
     * Name of the XPath element [XMLDSIG 6.6.3]
     */
    public static final String ELEMENT_XPATH = "XPath";

    /**
     * Namespace URI of <code>xmlns</code>.
     */
    public static final String NAMESPACE_URI_XML_NS =
        "http://www.w3.org/2000/xmlns/";

    /**
     * Namespace prefix of <code>Signature</code>.
     */
    public static final String NAMESPACE_PREFIX_DS = "ds";

    /**
     * Namespace URI of <code>Signature</code>.
     */
    public static final String NAMESPACE_URI_DS = Constants.SignatureSpecNS;

    /**
     * Name of the digital signature method required, qualified by the
     * digital signature namespace [XMLDSIG 6.1]
     */
    public static final String SIGNATURE_METHOD = "dsa-sha1";

    /**
     * Name of the Digest method required, qualified by namespace [XMLDSIG 6.1]
     */
    public static final String DIGEST_METHOD = Constants.ALGO_ID_DIGEST_SHA1;

    /**
     * Namespace prefix of SOAP envelope.
     */
    public static final String NAMESPACE_PREFIX_SOAP_ENVELOPE = "SOAP-ENV";

    /**
     * Namespace URI of SOAP envelope.
     */
    public static final String NAMESPACE_URI_SOAP_ENVELOPE =
        "http://schemas.xmlsoap.org/soap/envelope/";

    /**
     * Name of the XPath transform algorithm recommended [XMLDSIG 6.1]
     */
    public static final String TRANSFORM_ALGORITHM_XPATH =
        "http://www.w3.org/TR/1999/REC-xpath-19991116";

    public static final String ACTOR_NEXT_MSH_URN =
        "urn:oasis:names:tc:ebxml-msg:actor:nextMSH";

    public static final String ACTOR_NEXT_MSH_SCHEMAS =
        "http://schemas.xmlsoap.org/soap/actor/next";

    /**
     * XPath transform string used in the implementation.
     */
    public static final String TRANSFORM_XPATH =
        "not(ancestor-or-self::node()[@" + NAMESPACE_PREFIX_SOAP_ENVELOPE +
        ":actor=\"" + ACTOR_NEXT_MSH_URN + "\"] | ancestor-or-self::node()[@" +
        NAMESPACE_PREFIX_SOAP_ENVELOPE + ":actor=\"" + ACTOR_NEXT_MSH_SCHEMAS +
        "\"])";

    static {
        org.apache.xml.security.Init.init();
    }

    /**
     * Internal variable for holding the envelope of the signature.
     */
    protected Document envelope;

    /**
     * Internal variable for holding the documents needed to be referred
     * in the signature.
     */
    protected ArrayList documents;

    /**
     * Internal variable for holding the trusted anchor for certificate
     * path verification.
     */
    protected CompositeKeyStore trusted;

    /**
     * Internal variable of the Apache XML Security library signature object
     * for doing the actual signing/verifying algorithm.
     */
    protected XMLSignature signature;

    private CertResolver certResolver;

    private Object obj;

    private String digestAlgo;

    private String algo;
    /**
     * Default constructor to initialize the internal variables.
     */
    public ApacheXMLDSigner() {
        documents = new ArrayList();
        envelope = null;
        signature = null;
        trusted = null;
        certResolver = null;
        obj = null;
        digestAlgo = null;
    }

    /**
     * Set the envelope to host the Signature element. That is the
     * XML document where the Signature element to be added. The
     * digital signature here will always be an enveloped signature.
     * The envelope will be included in the process of signing.
     *
     * @param doc the XML document to host the Signature element
     * @param algo the algorithm used for digital signature. Currently, only 
     *             two values are tested: <code>dsa-sha1</code> and 
     *             <code>rsa-sha1</code>.
     * @param digestAlgo the algorithm used for making digest value. Currently,
     *             one value is supported: <code>sha1</code>
     * @throws SignException internal exception when doing initialization
     *                       on Apache XML Security library
     */
    public void setEnvelope(Document doc, String algo, String digestAlgo)
            throws SignException {
        setEnvelope(doc, algo);
        this.digestAlgo = digestAlgo;
    }
    
    /**
     * Set the envelope to host the Signature element. That is the
     * XML document where the Signature element to be added. The
     * digital signature here will always be an enveloped signature.
     * The envelope will be included in the process of signing.
     *
     * @param doc the XML document to host the Signature element
     * @param algo the algorithm used for digital signature. Currently, only 
     *             two values are tested: <code>dsa-sha1</code> and 
     *             <code>rsa-sha1</code>.
     * @throws SignException internal exception when doing initialization
     *                       on Apache XML Security library
     */
    public void setEnvelope(Document doc, String algo) throws SignException {
        envelope = doc;
        try {
            if (algo != null) {
                signature = new XMLSignature(envelope, NAMESPACE_URI_DS,
                                             NAMESPACE_URI_DS + algo);
            }
        } catch (XMLSecurityException e) {
            String err = "Cannot create XMLSignature object - " 
                + e.getMessage();
            logger.error(err);
            throw new SignException(err);
        }
        this.algo = algo;
        logger.debug("setEnvelope, using algorithm: " + algo);
    }

    /**
     * Set the envelope to host the Signature element. That is the
     * XML document where the Signature element to be added. The
     * digital signature here will always be an enveloped signature.
     * The envelope will be included in the process of signing.
     *
     * @param doc the XML document to host the Signature element
     * @throws SignException internal exception when doing initialization
     *                       on Apache XML Security library
     */
    public void setEnvelope(Document doc) throws SignException {
        setEnvelope(doc, null);
    }

    /**
     * Adds a reference to a document attachment to the signature.
     *
     * @param uri the URI of the document attachment
     * @param is the input stream of the content of the document
     * @param contentType the content type of the document
     */
    public void addDocument(String uri, InputStream is, String contentType) {
        DocumentDetail dd = new DocumentDetail();
        dd.uri = uri;
        dd.stream = is;
        dd.contentType = contentType;
        documents.add(dd);
        logger.debug(
            "addDocument URI: " + uri + ", contentType: " + contentType);
    }

    public void addCertResolver(CertResolver certResolver, Object obj) {
        this.certResolver = certResolver;
        this.obj = obj;
    }
    

    /**
     * Signs the envelope and documents by using the specified key 
     * in the keystore.
     *
     * @param ks the keystore holding the key for signing
     * @param alias the alias of the key for signing
     * @param password the password for accessing the key for signing
     * @throws SignException when there is any error in the processing of
     *                       signing
     */
    public void sign(CompositeKeyStore ks, String alias, char[] password)
            throws SignException {
        logger.debug("start signing");

        PrivateKey pk;
        try {
            pk = (PrivateKey) ks.getKey(alias, password);
        }
        catch (Exception e) {
            String err = "Cannot get private key: " + alias + " - "
                + e.getMessage();
            logger.warn(err);
            throw new SignException(err);
        }
        logger.debug("got private key from keystore");
        
        if (envelope == null) {
            String err = "Envelope element not set"; 
            logger.warn(err);
            throw new SignException(err);
        }

        DocumentDetail[] doc_array = new DocumentDetail[documents.size()];
        for (int i=0 ; i<doc_array.length ; i++) {
            doc_array[i] = (DocumentDetail) documents.get(i);
        }
        DocumentResolver resolver = new DocumentResolver(doc_array);
        signature.getSignedInfo().addResourceResolver(resolver);
        logger.debug("created DocumentResolver");

        Transforms transforms = new Transforms(envelope);
        try {
            transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
            Element xpath = envelope.createElementNS
                (NAMESPACE_URI_DS, ELEMENT_XPATH);
            xpath.setAttributeNS(NAMESPACE_URI_XML_NS, "xmlns:" +
                NAMESPACE_PREFIX_SOAP_ENVELOPE, NAMESPACE_URI_SOAP_ENVELOPE);
            xpath.appendChild(envelope.createTextNode(TRANSFORM_XPATH));
            xpath.setPrefix(NAMESPACE_PREFIX_DS);
            transforms.addTransform(TRANSFORM_ALGORITHM_XPATH, xpath);
            // add canonicalization transform
            transforms.addTransform(Transforms.TRANSFORM_C14N_OMIT_COMMENTS);
        }
        catch (TransformationException e) {
            String err = "Cannot add transform - " + e.getMessage(); 
            logger.warn(err);
            throw new SignException(err);
        }
        logger.debug("created Transform");

        try {
            if (digestAlgo == null) {
                signature.addDocument("", transforms, DIGEST_METHOD);
            } else {
                signature.addDocument("", transforms,
                    NAMESPACE_URI_DS + digestAlgo);
            }
        }
        catch (XMLSignatureException e) {
            String err = "Cannot add envelope document - " + e.getMessage();
            logger.warn(err);
            throw new SignException(err);
        }
        logger.debug("added main document (envelope)");

        for (int i=0 ; i<documents.size() ; i++) {
            DocumentDetail dd = (DocumentDetail) documents.get(i);
            try {
                signature.addDocument(dd.uri);
            }
            catch (XMLSignatureException e) {
                String err = "cannot add document: " + dd.uri + " - " 
                    + e.getMessage();
                logger.warn(err);
                throw new SignException(err);
            }
        }
        logger.debug("added " + documents.size() + " attachment documents");

        Certificate[] certificates;
        try {
            certificates = ks.getCertificateChain(alias);
            if (certificates == null) {
                String err = "Cannot get certificate path: " + alias;
                logger.warn(err);
                throw new SignException(err);
            }
        }
        catch (KeyStoreException e) {
            String err = "Cannot get certificate path: " + alias + " - "
                + e.getMessage();
            logger.warn(err);
            throw new SignException(err);
        }
        logger.debug("got the certificate chain from keystore");

        for (int i=0 ; i<certificates.length ; i++) {
            try {
                signature.addKeyInfo((X509Certificate) certificates[i]);
            }
            catch (XMLSecurityException e) {
                String err = "Cannot add key info - " + e.getMessage();
                logger.warn(err);
                throw new SignException(err);
            }
        }
        logger.debug("added the certificate chain to signature");

        /*
        PrivateKey pk;
        try {
            pk = (PrivateKey) ks.getKey(alias, password);
        }
        catch (Exception e) {
            String err = "Cannot get private key: " + alias + " - "
                + e.getMessage();
            logger.warn(err);
            throw new SignException(err);
        }
        logger.debug("got private key from keystore");
        */
        try {
            signature.sign(pk);
        }
        catch (Exception e) {
            String err = "Cannot sign - " + e.getMessage();
            logger.warn(err);
            throw new SignException(err);
        }
        logger.debug("message signed");
    }

    /**
     * Sets the trust anchor for verfication of certificate path.
     *
     * @param ks the keystore providing the trusted certificates
     */
    public void setTrustAnchor(CompositeKeyStore ks) {
        trusted = ks;
    }

    /**
     * Verifies the signature in the envelope passed in, which may reference
     * the documents specified using the addDocument method.
     *
     * @return true if the signature can be verified successfully, false
     *         if otherwise.
     * @throws VerifyException when there is any error in the processing of
     *                         verification
     */
    public boolean verify() throws VerifyException {
        logger.debug("start verifying");

        if (envelope == null) {
            String err = "Envelope element not set.";
            logger.warn(err);
            throw new VerifyException(err);
        }

        NodeList nodeList = envelope.getElementsByTagNameNS
            (NAMESPACE_URI_DS, ELEMENT_SIGNATURE);

        if (nodeList.getLength() == 0) {
            String err = "No <" + NAMESPACE_PREFIX_DS + ":" + ELEMENT_SIGNATURE
                + "> found";
            logger.warn(err);
            throw new VerifyException(err);
        }
        Element signatureElement = (Element) nodeList.item(0);
        // addNamespaceDeclaration(signatureElement);
        logger.debug("got the signature element");

        try {
            signature = new XMLSignature(signatureElement, NAMESPACE_URI_DS);
        }
        catch (Exception e) {
            String err = "Cannot create XMLSignature object - " 
                + e.getMessage(); 
            logger.error(err);
            throw new VerifyException(err);
        }
        logger.debug("created signature object");

        DocumentDetail[] doc_array = new DocumentDetail[documents.size()];
        for (int i=0 ; i<doc_array.length ; i++) {
            doc_array[i] = (DocumentDetail) documents.get(i);
        }
        DocumentResolver resolver = new DocumentResolver(doc_array);
        signature.addResourceResolver(resolver);
        logger.debug("created document resolver");

        Certificate[] certs = null;
        if (certResolver != null) {
            certs = certResolver.resolve(obj);
            if (certs == null || certs.length <= 0) {
                String err = "Certificates returned by certResolver is null";
                logger.warn(err);
                throw new VerifyException(err);
            }
        } else if (trusted == null) {
            String err = "Cannot verify cert path, but certResolver is null";
            logger.warn(err);
            throw new VerifyException(err);
        }
        KeyInfo keyInfo = null;
        PublicKey publicKey = null;
        if (certs != null && certs.length > 0) {
            publicKey = certs[0].getPublicKey();
            logger.debug("got certificate and public key from CertResolver");
        }
        else {
            keyInfo = signature.getKeyInfo();
        }

        if (keyInfo != null) {
            try {
                int certPathLen = keyInfo.lengthX509Data();
                if (certPathLen > 0) {
                    certs = new Certificate[certPathLen];
                    for (int i=0 ; i<certPathLen ; i++) {
                        try {
                            certs[i] = keyInfo.itemX509Data(i)
                                        .itemCertificate(0)
                                        .getX509Certificate();
                        }
                        catch (XMLSecurityException e) {
                            String err = "Cannot get X509 certficate from <" +
                                signatureElement.getPrefix() + ":" +
                                ELEMENT_KEY_INFO + ">";
                            logger.warn(err);
                            throw new VerifyException(err);
                        }
                    }
                }
                X509Certificate certificate = keyInfo.getX509Certificate();
                if (certificate != null) {
                    publicKey = certificate.getPublicKey();
                }
                logger.debug("got X509 certificate and public key from " +
                             ELEMENT_SIGNATURE + " element in message");
            }
            catch (KeyResolverException e) {
                String err = "Cannot get X509 certificate from <" +
                    signatureElement.getPrefix() + ":" + ELEMENT_KEY_INFO + ">";
                logger.warn(err);
                throw new VerifyException(err);
            }
        }
        if (publicKey == null) {
            String err = "No PublicKey found";
            logger.warn(err);
            throw new VerifyException(err);
        }

        boolean ret = false;
        try {
            ret = signature.checkSignatureValue(publicKey);
        }
        catch (Exception e) {
            String err = "Cannot check signature - " + e.getMessage();
            logger.warn(err);
            throw new VerifyException(err);
        }
        logger.debug("checked signature value, result: " + ret);


        if (ret == true && trusted != null && certs != null 
            && certs.length > 1) {

            logger.debug("start verifying cert path");
            ret = CertPathVerifier.verify(certs, trusted);
            logger.debug("verified, result: " + ret);
        } else {
            logger.debug("verification of cert path skipped");
        }

        return ret;
    }

    /**
     * Gets the DOM element of the signature generated.
     *
     * @return the DOM element of the signature
     */
    public Element getElement() {
        if (signature != null) {
            return signature.getElement();
        }
        else {
            return null;
        }
    }

    /*
    private void addNamespaceDeclaration(Element element) {
        NodeList nodeList = element.getChildNodes();
        for (int i=0 ; i<nodeList.getLength() ; i++) {
            if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element child = (Element) nodeList.item(i);
            child.setAttributeNS(NAMESPACE_URI_XML_NS, "xmlns:" +
                NAMESPACE_PREFIX_DS, NAMESPACE_URI_DS);

            if (child.getLocalName().equals(ELEMENT_XPATH)) {
                child.setAttributeNS(NAMESPACE_URI_XML_NS, "xmlns:" +
                    NAMESPACE_PREFIX_SOAP_ENVELOPE,
                    NAMESPACE_URI_SOAP_ENVELOPE);
            }

            addNamespaceDeclaration(child);
        }
    }
    */
}
