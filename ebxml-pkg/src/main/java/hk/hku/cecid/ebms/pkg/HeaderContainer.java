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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/HeaderContainer.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPPart;
/**
 * An encapsulation of the Header Container of an <code>EbxmlMessage</code>.
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
class HeaderContainer {
    
    /* A SOAP Envelope representing an ebXML message header */
    private final SOAPEnvelope soapEnvelope;

    private MessageHeader messageHeader;

    private Manifest manifest;

    private StatusRequest statusRequest;

    private StatusResponse statusResponse;

    private final ArrayList signatures;

    private AckRequested ackRequested;

    private Acknowledgment acknowledgment;

    private ErrorList errorList;

    private MessageOrder messageOrder;

    private SyncReply syncReply;

    /** Creates a <code>HeaderContainer</code> using the specified
        <code>SOAPPart</code>
    */
    HeaderContainer(SOAPPart soapPart) throws SOAPException {
        soapEnvelope = soapPart.getEnvelope();
        final SOAPHeader soapHeader = soapEnvelope.getHeader();
        final SOAPBody soapBody = soapEnvelope.getBody();
        signatures = new ArrayList();

        Name name = soapEnvelope.createName(MessageHeader.MESSAGE_HEADER,
            ExtensionElement.NAMESPACE_PREFIX_EB,
            ExtensionElement.NAMESPACE_URI_EB);
        Iterator childElements = soapHeader.getChildElements(name);
        if (childElements.hasNext()) {
            messageHeader = new MessageHeader(soapEnvelope, (SOAPElement)
                                              childElements.next());
        }
        else {
            messageHeader = null;

            for (Iterator i=soapEnvelope.getNamespacePrefixes() ;
                 i.hasNext() ; ) {
                String prefix = (String) i.next();
                String uri = soapEnvelope.getNamespaceURI(prefix);
                if (uri.equals(ExtensionElement.NAMESPACE_URI_SOAP_ENVELOPE)) {
                    soapEnvelope.removeNamespaceDeclaration(prefix);
                }
            }
            
            soapEnvelope.addNamespaceDeclaration
                (ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE,
                 ExtensionElement.NAMESPACE_URI_SOAP_ENVELOPE);
            
            // Add soap envelope schema location
            soapEnvelope.addNamespaceDeclaration(
                ExtensionElement.NAMESPACE_PREFIX_XSI,
                ExtensionElement.NAMESPACE_URI_XSI);
            name = soapEnvelope.createName(
                ExtensionElement.ATTRIBUTE_SCHEMA_LOCATION,
                ExtensionElement.NAMESPACE_PREFIX_XSI,
                ExtensionElement.NAMESPACE_URI_XSI);
            soapEnvelope.addAttribute(name,
                ExtensionElement.SCHEMA_LOCATION_SOAP_ENVELOPE);

            // Add soap header schema location
            soapHeader.addAttribute(name,
                ExtensionElement.SCHEMA_LOCATION_SOAP_HEADER);

            // Add soap body schema location
            soapBody.addAttribute(name,
                ExtensionElement.SCHEMA_LOCATION_SOAP_BODY);
        }

        name = soapEnvelope.createName(AckRequested.ACK_REQUESTED,
            ExtensionElement.NAMESPACE_PREFIX_EB,
            ExtensionElement.NAMESPACE_URI_EB);
        childElements = soapHeader.getChildElements(name);
        if (childElements.hasNext()) {
            ackRequested = new AckRequested(soapEnvelope, (SOAPElement)
                                            childElements.next());
        }
        else {
            ackRequested = null;
        }

        name = soapEnvelope.createName(Acknowledgment.ACKNOWLEDGMENT,
            ExtensionElement.NAMESPACE_PREFIX_EB,
            ExtensionElement.NAMESPACE_URI_EB);
        childElements = soapHeader.getChildElements(name);
        if (childElements.hasNext()) {
            acknowledgment = new Acknowledgment(soapEnvelope, (SOAPElement)
                                                childElements.next());
        }
        else {
            acknowledgment = null;
        }

        name = soapEnvelope.createName(ErrorList.ERROR_LIST,
            ExtensionElement.NAMESPACE_PREFIX_EB,
            ExtensionElement.NAMESPACE_URI_EB);
        childElements = soapHeader.getChildElements(name);
        if (childElements.hasNext()) {
            errorList = new ErrorList(soapEnvelope, (SOAPElement)
                                      childElements.next());
        }
        else {
            errorList = null;
        }

        name = soapEnvelope.createName(MessageOrder.MESSAGE_ORDER,
            ExtensionElement.NAMESPACE_PREFIX_EB,
            ExtensionElement.NAMESPACE_URI_EB);
        childElements = soapHeader.getChildElements(name);
        if (childElements.hasNext()) {
            messageOrder = new MessageOrder(soapEnvelope, (SOAPElement)
                                            childElements.next());
        }
        else {
            messageOrder = null;
        }

        name = soapEnvelope.createName(SyncReply.SYNC_REPLY,
            ExtensionElement.NAMESPACE_PREFIX_EB,
            ExtensionElement.NAMESPACE_URI_EB);
        childElements = soapHeader.getChildElements(name);
        if (childElements.hasNext()) {
            syncReply = new SyncReply(soapEnvelope, (SOAPElement)
                                      childElements.next());
        }
        else {
            syncReply = null;
        }

        name = soapEnvelope.createName(Signature.ELEMENT_SIGNATURE,
            Signature.NAMESPACE_PREFIX_DS, Signature.NAMESPACE_URI_DS);
        childElements = soapHeader.getChildElements(name);
        while (childElements.hasNext()) {
            signatures.add(Signature.newInstance(soapEnvelope,
                (SOAPElement) childElements.next()));
        }

        name = soapEnvelope.createName(Manifest.MANIFEST,
            ExtensionElement.NAMESPACE_PREFIX_EB,
            ExtensionElement.NAMESPACE_URI_EB);
        childElements = soapBody.getChildElements(name);
        if (childElements.hasNext()) {
            manifest = new Manifest(soapEnvelope,
                                    (SOAPElement) childElements.next());
        }
        else {
            manifest = null;
        }

        name = soapEnvelope.createName(StatusRequest.STATUS_REQUEST,
            ExtensionElement.NAMESPACE_PREFIX_EB,
            ExtensionElement.NAMESPACE_URI_EB);
        childElements = soapBody.getChildElements(name);
        if (childElements.hasNext()) {
            statusRequest = new StatusRequest
                (soapEnvelope, (SOAPElement) childElements.next());
        }
        else {
            statusRequest = null;
        }

        name = soapEnvelope.createName(StatusResponse.STATUS_RESPONSE,
            ExtensionElement.NAMESPACE_PREFIX_EB,
            ExtensionElement.NAMESPACE_URI_EB);
        childElements = soapBody.getChildElements(name);
        if (childElements.hasNext()) {
            statusResponse = new StatusResponse
                (soapEnvelope, (SOAPElement) childElements.next());
        }
        else {
            statusResponse = null;
        }

        // Check if there are any elements that have the mustUnderstand
        // attribute set to true but are not recognized
        for (Iterator i=soapHeader.getChildElements() ; i.hasNext() ; ) {
            final Object childNode = i.next();
            if (!(childNode instanceof SOAPElement)) {
                continue;
            }
            final SOAPElement child = (SOAPElement) childNode;
            final Name mustUnderstand = soapEnvelope.createName
                (HeaderElement.ATTRIBUTE_MUST_UNDERSTAND, 
                 ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE, 
                 ExtensionElement.NAMESPACE_URI_SOAP_ENVELOPE);
            final String value = child.getAttributeValue(mustUnderstand);

            if (value != null && value.equals("1")) {
                name = child.getElementName();
                final String localName = name.getLocalName();
                final String prefix = name.getPrefix();
                final String namespace = name.getURI();
                if (namespace.equals(ExtensionElement.NAMESPACE_URI_EB)) {
                    // These element are recognized
                    if (localName.equals(MessageHeader.MESSAGE_HEADER) ||
                        localName.equals(AckRequested.ACK_REQUESTED) || 
                        localName.equals(Acknowledgment.ACKNOWLEDGMENT) || 
                        localName.equals(ErrorList.ERROR_LIST) ||
                        localName.equals(MessageOrder.MESSAGE_ORDER) ||
                        localName.equals(SyncReply.SYNC_REPLY)) {
                        continue;
                    }
                }
                throw new SOAPValidationException
                    (SOAPValidationException.SOAP_FAULT_MUST_UNDERSTAND,
                     "SOAP Element <" + prefix + ":" + localName 
                     + "> has the attribute mustUnderstand = \"1\" but it " 
                     + "cannot be recognized.");
            }
        }
    }

    /** Add an <code>ExtensionElement</code>, e.g. <code>MessageHeader</code>
        (a <code>HeaderElement</code>) or <code>Manifest</code>
        (a <code>BodyElement</code>), to this <code>HeaderContainer</code>
    */
    void addExtensionElement(ExtensionElement extensionElement) 
        throws SOAPException {
        if (extensionElement instanceof HeaderElement) {
            /*
            final SOAPHeader soapHeader;
            soapHeader = soapEnvelope.getHeader();
            soapHeader.addChildElement(extensionElement.getSOAPElement());
            extensionElement.synchronizeWithParent(soapHeader, 0);
            */
            if (extensionElement instanceof MessageHeader) {
                if (messageHeader == null) {
                    messageHeader = (MessageHeader) extensionElement;
                    /*
                    final SOAPElement parent = 
                        messageHeader.getSOAPElement();
                    messageHeader.getFrom().synchronizeWithParent(parent,0);
                    messageHeader.getTo().synchronizeWithParent(parent, 0);
                    if (messageHeader.getMessageData() != null) {
                        messageHeader.getMessageData().
                            synchronizeWithParent(parent, 0);
                    }
                    */
                }
                else {
                    throw new SOAPValidationException
                        (SOAPValidationException.SOAP_FAULT_CLIENT,
                         "<" + ExtensionElement.NAMESPACE_PREFIX_EB + ":" 
                         + MessageHeader.MESSAGE_HEADER 
                         + "> has already been added in <" 
                         + ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE 
                         + ":Header>!");
                }
            }
            else if (extensionElement instanceof Signature) {
                Signature signature = (Signature) extensionElement;
                signatures.add(signature);
                /*
                SOAPElement parent = signature.getSOAPElement();
                int count = 0;
                for (Iterator i=signature.getReferences() ; i.hasNext() ; ) {
                    ((SignatureReference) i.next()).synchronizeWithParent
                        (parent, count);
                    count++;
                }
                */
            }
            else if (extensionElement instanceof AckRequested) {
                if (ackRequested == null) {
                    ackRequested = (AckRequested) extensionElement;
                }
                else {
                    throw new SOAPValidationException
                        (SOAPValidationException.SOAP_FAULT_CLIENT,
                         "<" + ExtensionElement.NAMESPACE_PREFIX_EB + ":" 
                         + AckRequested.ACK_REQUESTED 
                         + "> has already been added in <" 
                         + ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE 
                         + ":Header>!");
                }
            }
            else if (extensionElement instanceof Acknowledgment) {
                if (acknowledgment == null) {
                    acknowledgment = (Acknowledgment) extensionElement;
                }
                else {
                    throw new SOAPValidationException
                        (SOAPValidationException.SOAP_FAULT_CLIENT,
                         "<" + ExtensionElement.NAMESPACE_PREFIX_EB + ":" 
                         + Acknowledgment.ACKNOWLEDGMENT 
                         + "> has already been added in <" 
                         + ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE 
                         + ":Header>!");
                }
            }
            else if (extensionElement instanceof ErrorList) {
                if (errorList == null) {
                    errorList = (ErrorList) extensionElement;
                }
                else {
                    throw new SOAPValidationException
                        (SOAPValidationException.SOAP_FAULT_CLIENT, 
                         "<" + ExtensionElement.NAMESPACE_PREFIX_EB + ":" 
                         + ErrorList.ERROR_LIST 
                         + "> has already been added in <" 
                         + ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE 
                         + ":Header>!");
                }
            }
            else if (extensionElement instanceof SyncReply) {
                if (syncReply == null) {
                    syncReply = (SyncReply) extensionElement;
                }
                else {
                    throw new SOAPValidationException
                        (SOAPValidationException.SOAP_FAULT_CLIENT,
                         "<" + ExtensionElement.NAMESPACE_PREFIX_EB + ":" 
                         + SyncReply.SYNC_REPLY 
                         + "> has already been added in <" 
                         + ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE 
                         + ":Header>!");
                }
            }
            else if (extensionElement instanceof MessageOrder) {
                if (messageOrder == null) {
                    messageOrder = (MessageOrder) extensionElement;
                }
                else {
                    throw new SOAPValidationException
                        (SOAPValidationException.SOAP_FAULT_CLIENT,
                         "<" + ExtensionElement.NAMESPACE_PREFIX_EB + ":" 
                         + MessageOrder.MESSAGE_ORDER 
                         + "> has already been added in <" 
                         + ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE 
                         + ":Header>!");
                }
            }
            else {
                throw new SOAPValidationException
                    (SOAPValidationException.SOAP_FAULT_CLIENT,
                     "Unknown SOAP Header extension element added to <" 
                     + ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE 
                     + ":Header>!");
            }
        }
        else if (extensionElement instanceof BodyElement) {
            /*
            final SOAPBody soapBody = soapEnvelope.getBody();
            soapBody.addChildElement(extensionElement.getSOAPElement());
            extensionElement.synchronizeWithParent(soapBody, 0);
            */
            if (extensionElement instanceof Manifest) {
                if (manifest == null) {
                    manifest = (Manifest) extensionElement;
                    /*
                    final SOAPElement parent = manifest.getSOAPElement();
                    int count = 0;
                    for (Iterator i=manifest.getReferences();i.hasNext();) {
                        ((Reference) i.next()).synchronizeWithParent(parent,
                                                                     count);
                        count++;
                    }
                    */
                }
                else {
                    throw new SOAPValidationException
                        (SOAPValidationException.SOAP_FAULT_CLIENT,
                         "<" + ExtensionElement.NAMESPACE_PREFIX_EB + ":" 
                         + Manifest.MANIFEST + "> has " 
                         + "already been added in <" 
                         + ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE 
                         + ":Body>!");
                }
            }
            else if (extensionElement instanceof StatusRequest) {
                if (statusRequest == null) {
                    statusRequest = (StatusRequest) extensionElement;
                }
                else {
                    throw new SOAPValidationException
                        (SOAPValidationException.SOAP_FAULT_CLIENT,
                         "<" + ExtensionElement.NAMESPACE_PREFIX_EB + ":" 
                         + StatusRequest.STATUS_REQUEST 
                         + "> has already been added in <" 
                         + ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE 
                         + ":Body>!");
                }
            }
            else if (extensionElement instanceof StatusResponse) {
                if (statusResponse == null) {
                    statusResponse = (StatusResponse) extensionElement;
                }
                else {
                    throw new SOAPValidationException
                        (SOAPValidationException.SOAP_FAULT_CLIENT,
                         "<" + ExtensionElement.NAMESPACE_PREFIX_EB + ":" 
                         + StatusResponse.STATUS_RESPONSE 
                         + "> has already been added in <" 
                         + ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE 
                         + ":Body>!");
                }
            }
            else {
                throw new SOAPValidationException
                    (SOAPValidationException.SOAP_FAULT_CLIENT,
                     "Unknown SOAP Body extension element added to <" 
                     + ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE 
                     + ":Body>!");
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Unknown SOAP extension element added to <"
                 + ExtensionElement.NAMESPACE_PREFIX_SOAP_ENVELOPE 
                 + ":Envelope>!");
        }
    }

    MessageHeader getMessageHeader() {
        return messageHeader;
    }

    Manifest getManifest() {
        return manifest;
    }

    StatusRequest getStatusRequest() {
        return statusRequest;
    }

    StatusResponse getStatusResponse() {
        return statusResponse;
    }

    Iterator getSignatures() {
        return signatures.iterator();
    }

    AckRequested getAckRequested() {
        return ackRequested;
    }

    Acknowledgment getAcknowledgment() {
        return acknowledgment;
    }

    ErrorList getErrorList() {
        return errorList;
    }

    SyncReply getSyncReply() {
        return syncReply;
    }

    MessageOrder getMessageOrder() {
        return messageOrder;
    }
}
