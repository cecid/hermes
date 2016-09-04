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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/PKISignatureImpl.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * cyng [2002-05-22]
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

import hk.hku.cecid.ebms.pkg.pki.ApacheXMLDSigner;
import hk.hku.cecid.ebms.pkg.pki.CertResolver;
import hk.hku.cecid.ebms.pkg.pki.CompositeKeyStore;
import hk.hku.cecid.ebms.pkg.pki.ErrorMessages;
import hk.hku.cecid.ebms.pkg.pki.SignException;
import hk.hku.cecid.ebms.pkg.pki.VerifyException;
import hk.hku.cecid.ebms.pkg.validation.SOAPValidationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import javax.activation.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerFactory;

import org.apache.log4j.Logger;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
/**
 * An implementation of ebXML <code>Signature</code>, making use 
 * of Phoenix's PKI library.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
class PKISignatureImpl extends Signature {

    static Logger logger = Logger.getLogger(PKISignatureImpl.class);

    private final EbxmlMessage ebxmlMessage;

    PKISignatureImpl(EbxmlMessage ebxmlMessage) throws SOAPException {
        super(ebxmlMessage.getSOAPMessage().getSOAPPart().getEnvelope(),
              ELEMENT_SIGNATURE, NAMESPACE_PREFIX_DS, NAMESPACE_URI_DS);
        this.ebxmlMessage = ebxmlMessage;
    }

    PKISignatureImpl(EbxmlMessage ebxmlMessage, SOAPEnvelope soapEnvelope,
        SOAPElement soapElement) throws SOAPException {
        super(soapEnvelope, soapElement);
        this.ebxmlMessage = ebxmlMessage;
    }

    void addReference(String uri) {
        throw new Error("Not supported");
    }

    void sign(String alias, char[] password, String keyStoreLocation)
        throws SignatureException {
        sign(alias, password, keyStoreLocation, null);
    }

    void sign(String alias, char[] password, String keyStoreLocation,
              String algo) throws SignatureException {
        sign(alias, password, keyStoreLocation, algo, null, false);
    }

    private String getAlgorithmFromPrivateKey(PrivateKey key)
        throws SignException {
        String keyAlgo = key.getAlgorithm().toLowerCase();
        if (keyAlgo.equals("dsa")) {
            return "dsa-sha1";
        } else if (keyAlgo.equals("rsa")) {
            return "rsa-sha1";
        } else {
            throw new SignException("Unknown key algorithm : " + keyAlgo); 
        }
    }
    
    private boolean isAlgorithmMatchedWithKey(PrivateKey key, String algo) {
        String keyAlgo = key.getAlgorithm().toLowerCase();
        return algo.startsWith(keyAlgo);
    }

    void sign(String alias, char[] password, String keyStoreLocation,
            String algo, String digestAlgo, boolean signEnvelopeOnly)
                throws SignatureException {
        try {
            final SOAPPart soapPart = ebxmlMessage.getSOAPMessage().
                getSOAPPart();
            DocumentResult docResult = new DocumentResult();
            TransformerFactory.newInstance().newTransformer().
                transform(soapPart.getContent(), docResult);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            (new XMLWriter(baos)).write(docResult.getDocument());
            DocumentBuilderFactory factory = DocumentBuilderFactory.
                newInstance();
            factory.setNamespaceAware(true);
            // soapPartDocument is a DOM equivilance of soapPart
            final Document soapPartDocument = factory.newDocumentBuilder().
                parse(new ByteArrayInputStream(baos.toByteArray()));
            final String soapHeaderName = soapPart.getEnvelope().getHeader().
                getElementName().getLocalName();
            final Element soapHeader = (Element) soapPartDocument.
                getElementsByTagNameNS(NAMESPACE_URI_SOAP_ENVELOPE,
                                       soapHeaderName).item(0);
            ApacheXMLDSigner signature = new ApacheXMLDSigner();
            CompositeKeyStore ks = new CompositeKeyStore();
            ks.addKeyStoreFile(keyStoreLocation, null, password);
            PrivateKey pk = (PrivateKey) ks.getKey(alias, password);
            /* 
             * To reduce the complexity of the exception message,
             * the exception can be catch on the final catch block.
             *  
             try {
                pk = (PrivateKey) ks.getKey(alias, password);
                if(pk ==null){
                	String message ="Cannot retrieve key from keystore["+keyStoreLocation+"]" +"\n"+
                		"KeyStore Type: " + ks.getKeyStore().getType() +"\n"+
                		"Key Provider: " + ks.getKeyStore().getProvider().getInfo();
                	throw new NullPointerException(message);
                }
            }
            catch (Exception e) {
                String err = "Cannot get private key: " + alias + " - "
                    + e.getMessage();
               logger.warn(err);
                throw new SignException(err);
            }*/

            if (algo != null) {
                if (!isAlgorithmMatchedWithKey(pk, algo)) {
                    throw new SignException(
                            "Signing algorithm not matched with key algorithm, " +
                            "actual key algorithum:" + pk.getAlgorithm() +"\t" + "expect algorithum: " + algo 
                            );
                }
                if (digestAlgo == null) {
                    signature.setEnvelope(soapPartDocument, algo);
                } else {
                    signature.setEnvelope(soapPartDocument, algo, digestAlgo);
                }
            } else {
                String keyAlgo = getAlgorithmFromPrivateKey(pk);
                signature.setEnvelope(soapPartDocument, keyAlgo);
            }
            /*
            if (algo == null) {
                // use default algorithm, i.e. dsa-sha1
                signature.setEnvelope(soapPartDocument);
            }
            else {
                // use user-defined algorithm, only support dsa-sha1 and 
                // rsa-sha1
                if (digestAlgo == null) {
                    signature.setEnvelope(soapPartDocument, algo);
                } else {
                    signature.setEnvelope(soapPartDocument, algo, digestAlgo);
                }
            }
            */

            // important, if not, the transformation will fail
            soapHeader.appendChild(signature.getElement());

            if (!signEnvelopeOnly) {
                Iterator i = ebxmlMessage.getPayloadContainers();
                while (i.hasNext()) {
                    PayloadContainer pc = (PayloadContainer) i.next();
                    signature.addDocument(pc.getHref(), 
                        pc.getDataHandler().getInputStream(),
                        pc.getContentType());
                }
            }

            signature.sign(ks, alias, password);

            domToSoap(signature.getElement(), this);

            Iterator childElements = getChildElements
                (SignatureReference.SIGNATURE_REFERENCE);
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

            childElements = getChildElements(ELEMENT_SIGNATURE_VALUE);
            if (childElements.hasNext()) {
                signatureValue = ((SOAPElement) childElements.next()).
                    getValue();
            }
            else {
                throw new SOAPValidationException(SOAPValidationException.
                    SOAP_FAULT_CLIENT, "<" + NAMESPACE_PREFIX_DS + ":" +
                    ELEMENT_SIGNATURE_VALUE + "> is not found in <" +
                    NAMESPACE_PREFIX_DS + ":" + ELEMENT_SIGNATURE + ">!");
            }
        }
        catch (Exception e) {
            String err = ErrorMessages.getMessage
                (ErrorMessages.ERR_PKI_CANNOT_SIGN, e);
            err +=
            	"\n"+"Try to retreive key alias["+
            	alias+"] from keystore["+keyStoreLocation+"]";
            throw new SignatureException(err, e);
        }
    }

    void sign(String alias, char[] password) throws SignatureException {
        throw new Error("Not supported");
    }

    void sign(String alias, char[] password, String keyStoreLocation,
              String type, String provider) throws SignatureException {
        throw new Error("Not supported");
    }

    void sign(PrivateKey privateKey, X509Certificate[] certificates)
        throws SignatureException {
        throw new Error("Not supported");
    }
    
    private int loadInputStreamToOutputStream(InputStream istream,
            OutputStream ostream, int skipped, int expectedLength)
                    throws IOException {
        int bufferSize = 2048;
        byte[] buffer = new byte[bufferSize];
        int skippedBytes = 0;
        int read = 0;
        while(read != -1 && skippedBytes < skipped) {
            read = istream.read(buffer, 0,
                    calculateReadSize(skippedBytes, skipped, bufferSize));
            if (read != -1) {
                skippedBytes += read;
            }
        }
        int length = 0;
        read = 0;
        while(read != -1 && length < expectedLength) {
            read = istream.read(buffer, 0,
                    calculateReadSize(length, expectedLength, bufferSize));
            if (read != -1) {
                ostream.write(buffer, 0, read);
                length += read;
            }
       }
       return length;
    }
    
    private int calculateReadSize(int read, int expected, int bufferSize) {
        int remain = expected - read;
        if (remain > bufferSize) {
            return bufferSize;
        } else {
            return remain;
        }
    }

    boolean verify(char[] password, String keyStoreLocation,
                   CertResolver certResolver, DataSource dataSource)
        throws SignatureException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String fileName = ebxmlMessage.getFileName();
            String persistenceName = ebxmlMessage.getPersistenceName();
            byte[] soapEnvelopeBytes = ebxmlMessage.getSoapEnvelopeBytes();
            if (soapEnvelopeBytes != null) {
                baos.write(soapEnvelopeBytes);
            } else if (persistenceName != null) {
                if (dataSource == null) {
                    throw new SignatureException(
                        "Inconsistence persistence data for : "
                        + persistenceName);
                }
                InputStream istream = dataSource.getInputStream();
                try {
                    soapEnvelopeBytes
                        = EbxmlMessage.getSoapEnvelopeBytesFromStream(istream);
                } catch (SOAPException e) {
                    throw e;
                } finally {
                    istream.close();
                }
            } else if (fileName != null) {
                InputStream istream = null;
                try {
                    istream = new FileInputStream(fileName);
                    soapEnvelopeBytes
                        = EbxmlMessage.getSoapEnvelopeBytesFromStream(istream);
                } catch (IOException e) {
                    throw e;
                } catch (SOAPException e) {
                    throw e;
                } finally {
                    if (istream != null) {
                        istream.close();
                    }
                }
            } else {
                final SOAPPart soapPart = ebxmlMessage.getSOAPMessage().
                    getSOAPPart();
                DocumentResult docResult = new DocumentResult();
                TransformerFactory.newInstance().newTransformer().
                    transform(soapPart.getContent(), docResult);
                (new XMLWriter(baos)).write(docResult.getDocument());
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.
                newInstance();
            factory.setNamespaceAware(true);
            // soapPartDocument is a DOM equivilance of soapPart
            final Document soapPartDocument = factory.newDocumentBuilder().
                parse(new ByteArrayInputStream(baos.toByteArray()));

            ApacheXMLDSigner signature = new ApacheXMLDSigner();
            signature.setEnvelope(soapPartDocument);

            for (Iterator i=ebxmlMessage.getPayloadContainers(); i.hasNext();) {
                PayloadContainer pc = (PayloadContainer) i.next();
                signature.addDocument(pc.getHref(), pc.getDataHandler().
                                      getInputStream(), pc.getContentType());
            }

            if (keyStoreLocation != null) {
                CompositeKeyStore ks = new CompositeKeyStore();
                ks.addKeyStoreFile(keyStoreLocation, null, password);
                signature.setTrustAnchor(ks);
            }
            signature.addCertResolver(certResolver, ebxmlMessage);

            return signature.verify();
        }
        catch (VerifyException e) {
            throw new SignatureException(e.getMessage(), e);
        }
        catch (Exception e) {
            String err = ErrorMessages.getMessage(
                ErrorMessages.ERR_PKI_UNKNOWN_ERROR, e);
            throw new SignatureException(err , e);
        }
    }

    boolean verify(Element documentElement, PublicKey key)
        throws SignatureException {
        throw new Error("Not supported");
    }

    boolean verify(PublicKey key) throws SignatureException {
        throw new Error("Not supported");
    }

    private void domToSoap(Element domElement, ExtensionElement element)
        throws SOAPException {
        final String parentPrefix = element.getSOAPElement().getElementName().
            getPrefix();
        final String parentNamespaceUri = element.getSOAPElement().
            getElementName().getURI();
        final String nsPrefix = XML_NS_DECL_PREFIX + XML_NS_SEPARATOR;
        final NodeList nodeList = domElement.getChildNodes();
        for (int i=0 ; i<nodeList.getLength() ; i++) {
            final org.w3c.dom.Node node = nodeList.item(i);
            if (node.getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
                element.getSOAPElement().addTextNode(node.getNodeValue());
            }
            if (node.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
                continue;
            }
            final Element child = (Element) node;

            final ExtensionElement addedChild;
            if (child.getNamespaceURI().equals(parentNamespaceUri)) {
                addedChild = element.addChildElement(child.getLocalName());
            }
            else {
                addedChild = null;
            }

            final NamedNodeMap nodeMap = child.getAttributes();
            for (int j=0 ; j<nodeMap.getLength() ; j++) {
                final Attr attribute = (Attr) nodeMap.item(j);
                final String name = attribute.getName();
                final String value = attribute.getValue();

                if (!name.equals(nsPrefix + NAMESPACE_PREFIX_DS) &&
                    !name.startsWith(XML_NS_DECL_PREFIX)) {
                    addedChild.addAttribute(soapEnvelope.
                                            createName(name), value);
                }
            }
            domToSoap(child, addedChild);
        }
    }
}
