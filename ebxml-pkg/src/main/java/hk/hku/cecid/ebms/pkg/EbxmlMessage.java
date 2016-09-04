/*
 *  Copyright(c) 2002 Center for E-Commerce Infrastructure Development, The
 *  University of Hong Kong (HKU). All Rights Reserved.
 *
 *  This software is licensed under the Academic Free License Version 1.0
 *
 *  Academic Free License
 *  Version 1.0
 *
 *  This Academic Free License applies to any software and associated
 *  documentation (the "Software") whose owner (the "Licensor") has placed the
 *  statement "Licensed under the Academic Free License Version 1.0" immediately
 *  after the copyright notice that applies to the Software.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of the Software (1) to use, copy, modify, merge, publish, perform,
 *  distribute, sublicense, and/or sell copies of the Software, and to permit
 *  persons to whom the Software is furnished to do so, and (2) under patent
 *  claims owned or controlled by the Licensor that are embodied in the Software
 *  as furnished by the Licensor, to make, use, sell and offer for sale the
 *  Software and derivative works thereof, subject to the following conditions:
 *
 *  - Redistributions of the Software in source code form must retain all
 *  copyright notices in the Software as furnished by the Licensor, this list
 *  of conditions, and the following disclaimers.
 *  - Redistributions of the Software in executable form must reproduce all
 *  copyright notices in the Software as furnished by the Licensor, this list
 *  of conditions, and the following disclaimers in the documentation and/or
 *  other materials provided with the distribution.
 *  - Neither the names of Licensor, nor the names of any contributors to the
 *  Software, nor any of their trademarks or service marks, may be used to
 *  endorse or promote products derived from this Software without express
 *  prior written permission of the Licensor.
 *
 *  DISCLAIMERS: LICENSOR WARRANTS THAT THE COPYRIGHT IN AND TO THE SOFTWARE IS
 *  OWNED BY THE LICENSOR OR THAT THE SOFTWARE IS DISTRIBUTED BY LICENSOR UNDER
 *  A VALID CURRENT LICENSE. EXCEPT AS EXPRESSLY STATED IN THE IMMEDIATELY
 *  PRECEDING SENTENCE, THE SOFTWARE IS PROVIDED BY THE LICENSOR, CONTRIBUTORS
 *  AND COPYRIGHT OWNERS "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 *  LICENSOR, CONTRIBUTORS OR COPYRIGHT OWNERS BE LIABLE FOR ANY CLAIM, DAMAGES
 *  OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 *  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE.
 *
 *  This license is Copyright (C) 2002 Lawrence E. Rosen. All rights reserved.
 *  Permission is hereby granted to copy and distribute this license without
 *  modification. This license may not be modified without the express written
 *  permission of its copyright owner.
 */
/*
 *  =====
 *
 *  $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/EbxmlMessage.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 *  Code authored by:
 *
 *  cyng [2002-03-21]
 *
 *  Code reviewed by:
 *
 *  username [YYYY-MM-DD]
 *
 *  Remarks:
 *
 *  =====
 */
package hk.hku.cecid.ebms.pkg;

import hk.hku.cecid.ebms.pkg.validation.EbxmlValidationException;
import hk.hku.cecid.ebms.pkg.validation.SOAPValidationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * A representation of an ebXML message. An ebXML message conforms to the <a
 * href="http://www.w3.org/TR/2000/NOTE-SOAP-attachments-20001211"> SOAP 1.1
 * with Attachments Specification </a>. This <code>EbxmlMessage</code>
 * encapsulates a <code>javax.xml.soap.SOAPMessage</code>.
 * 
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class EbxmlMessage {

    /**
     * SOAPAction in the SOAP message MIME header
     */
    public final static String SOAP_ACTION = "SOAPAction";

    /**
     * SOAPAction value in the SOAP message MIME header
     */
    public final static String SOAP_ACTION_VALUE = "\"ebXML\"";

    /**
     * Default content id of soap part
     */
    public final static String SOAP_PART_CONTENT_ID = "soappart";

    /**
     * MIME boundary generated
     */
    public final static String mimeBoundary = "----=_BOUNDARY_01";

    /**
     * Description of the Field
     */
    public static boolean needPatch = false;
    
    /**
     * Default value of writing XML declaration in front of message
     */
    public final static boolean WRITE_XML_DECLARATION = true;
    
    /**
     * Default value XML character set
     */
    public final static String CHARACTER_SET_ENCODING = "UTF-8";

    // check if content id need to be patched with current SOAP implementation
    static {
        String s = "--MIMEBoundary\r\nContent-Type: text/xml\r\n\r\n"
            + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
            + "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/"
            + "soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body/>"
            + "</SOAP-ENV:Envelope>\r\n--MIMEBoundary\r\n"
            + "Content-Type: text/plain\r\nContent-Id: <ebxmlms>\r\n\r\n"
            + "ebxmlms\r\n--MIMEBoundary--\r\n";

    	try {
            MimeHeaders headers = new MimeHeaders();
            headers.setHeader(Constants.CONTENT_TYPE,
                    Constants.MULTIPART_RELATED_TYPE + "MIMEBoundary");
            ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
            SOAPMessage message = MessageFactory.newInstance().createMessage(
                    headers, bais);
            String contentId = ((AttachmentPart) message.getAttachments()
                    .next()).getContentId();
            needPatch = (contentId.startsWith("<") ? true : false);
        } catch (Exception e) {
        }
    }

    /**
     * A SOAP message representing the header of this <code>EbxmlMessage</code>,
     * which is composed of the <code>HeaderContainer</code> and the
     * <code>PayloadContainer</code>
     */
    private SOAPMessage soapMessage;

    /**
     * SOAP message envelope, which contains an optional header and a mandatory
     * body [SOAP 1.1 Section 4].
     */
    private SOAPEnvelope soapEnvelope;

    /**
     * SOAP header [SOAP 1.1 Section 4.2].
     */
    private HeaderContainer headerContainer;

    /**
     * Message Order element [ebMSS 9].
     */
    private MessageOrder messageOrder;

    /**
     * ebXML message payloads as SOAP attachments.
     */
    private ArrayList payloadContainers;

    private byte[] soapEnvelopeBytes = null;

    /**
     * Optional file name in which the message is persisted.
     */
    private String filename;

    /**
     * Optional persistence name in which the message is persisted
     */
    private String persistenceName;

    /**
     * Optional persistence handler in which the message is persisted
     */
    private Object persistenceHandler;

    private DataSource datasource;

    private byte[] bytes;

    /**
     * @return Returns the bytestream.
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * @param bytes
     *            The bytestream to set.
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return Returns the datasource.
     */
    public DataSource getDatasource() {
        return datasource;
    }
    /**
     * @return Returns the headerContainer.
     */
    public HeaderContainer getHeaderContainer() {
        return headerContainer;
    }
    /**
     * Constructs an <code>EbxmlMessage</code> using the default JAXM
     * <code>MessageFactory</code> implementation
     * 
     * @exception SOAPException
     *                Description of the Exception
     */

    public EbxmlMessage() throws SOAPException {
        this(MessageFactory.newInstance());
    }

    /**
     * Constructs an <code>EbxmlMessage</code> using the provided
     * <code>javax.xml.soap.MessageFactory</code>
     * 
     * @param messageFactory
     *            <code>MessageFactory</code> for creating message.
     * @exception SOAPException
     *                Description of the Exception
     */
    public EbxmlMessage(MessageFactory messageFactory) throws SOAPException {
        this(messageFactory.createMessage());
    }

    /**
     * construct an <code>EbxmlMessage</code> from File using the logic in
     * MessageServer. The file must not contain any content header like
     * Content-Type
     * 
     * @param file
     *            the file contain the messsage.
     * @exception SOAPException
     *                Description of the Exception
     * @exception IOException
     *                Description of the Exception
     */
    public EbxmlMessage(File file) throws SOAPException, IOException {
        /*
         * EbxmlMessage message = null; message = (EbxmlMessage)
         * getMessageFromDataSource( new FileDataSource(file), true);
         * init(message);
         */
        getMessageFromDataSource(new FileDataSource(file), true, this);
    }

    /**
     * constructs an <code>EbxmlMessage</code> from InputStream using the
     * logic in MessageServer. The input stream must not contain any content
     * headers like Content-type.
     * 
     * @param inputStream
     *            Description of the Parameter
     * @exception SOAPException
     *                Description of the Exception
     * @exception IOException
     *                Description of the Exception
     */
    public EbxmlMessage(InputStream inputStream) throws SOAPException,
            IOException {
        /*
         * EbxmlMessage message = null; message = (EbxmlMessage)
         * getMessageFromDataSource( new AttachmentDataSource(inputStream,
         * Constants.SERIALIZABLE_OBJECT), true); init(message);
         */        
        getMessageFromDataSource(new AttachmentDataSource(inputStream,
                Constants.SERIALIZABLE_OBJECT), true, this);
    }

    /**
     * Constructs an <code>EbxmlMessage</code> using the given
     * <code>SOAPMessage</code>
     * 
     * @param soapMessage
     * @exception SOAPException
     *                Description of the Exception
     */
    public EbxmlMessage(SOAPMessage soapMessage) throws SOAPException {
        init(soapMessage);
    }

    /**
     * initialize the object using Ebxml message.
     * 
     * @param message
     *            Description of the Parameter
     * @throws SOAPException
     *             Description of the Exception
     */
    private void init(EbxmlMessage message) throws SOAPException {
        init(message.getSOAPMessage());
        //MessageServer.getMessageFromInputStream(inputStream, true);
        this.soapMessage = message.getSOAPMessage();
        this.soapEnvelope = soapMessage.getSOAPPart().getEnvelope();

        payloadContainers = message.payloadContainers;
        soapEnvelopeBytes = message.getSoapEnvelopeBytes();
    }

    /**
     * initialize the object using SOAP message.
     * 
     * @param soapMessage
     *            Description of the Parameter
     * @throws SOAPException
     *             Description of the Exception
     */
    private void init(SOAPMessage soapMessage) throws SOAPException {
        this.soapMessage = soapMessage;

        // set default SOAP XML declaration and encoding
        this.soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, 
        		Boolean.toString(EbxmlMessage.WRITE_XML_DECLARATION));
        this.soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, 
        		EbxmlMessage.CHARACTER_SET_ENCODING);

        this.soapEnvelope = this.soapMessage.getSOAPPart().getEnvelope();
        headerContainer = new HeaderContainer(soapMessage.getSOAPPart());
        payloadContainers = new ArrayList();

        /*
         * this.soapMessage = soapMessage; this.soapEnvelope =
         * soapMessage.getSOAPPart().getEnvelope(); final SOAPHeader soapHeader =
         * soapEnvelope.getHeader(); final SOAPBody soapBody =
         * soapEnvelope.getBody(); headerContainer = new
         * HeaderContainer(soapMessage.getSOAPPart()); payloadContainers = new
         * ArrayList();
         */
        Manifest manifest = headerContainer.getManifest();
        if (manifest != null) {
            Iterator it = manifest.getReferences();
            HashMap cidMap = new HashMap();
            while (it.hasNext()) {
                Reference reference = (Reference) it.next();
                String cid = reference.getHref();
                if (cid.startsWith(Reference.HREF_PREFIX)) {
                    cid = cid.substring(Reference.HREF_PREFIX.length());
                    cidMap.put(cid, reference);
                }
            }

            for (Iterator attachments = soapMessage.getAttachments(); attachments
                    .hasNext();) {
                final AttachmentPart attachment = (AttachmentPart) attachments
                        .next();
                String contentId = attachment.getContentId();
                if (contentId.startsWith("<") && needPatch) {
                    contentId = contentId.substring(1);
                    if (contentId.endsWith(">")) {
                        contentId = contentId.substring(0,
                                contentId.length() - 1);
                    }
                }
                contentId = contentId.trim();
                if (cidMap.containsKey(contentId)) {
                    payloadContainers.add(new PayloadContainer(attachment
                            .getDataHandler(), contentId, (Reference) cidMap
                            .get(contentId)));
                }
            }
        }
        String[] soapAction = this.soapMessage.getMimeHeaders().getHeader(
                SOAP_ACTION);
        if (headerContainer.getMessageHeader() == null || soapAction == null
                || !soapAction[0].equals(SOAP_ACTION_VALUE)) {
            this.soapMessage.getMimeHeaders().setHeader(SOAP_ACTION,
                    SOAP_ACTION_VALUE);
        }
        
        String soapPartContentId = this.soapMessage.getSOAPPart()
                .getContentId();
        if (soapPartContentId == null || soapPartContentId.equals("")) {
            soapPartContentId = (
                                   needPatch ? " <" + SOAP_PART_CONTENT_ID +
                                   ">" :
                                  
            SOAP_PART_CONTENT_ID);
            this.soapMessage.getSOAPPart().setContentId(soapPartContentId);
        }

        /*
         * String[] soapAction =
         * soapMessage.getMimeHeaders().getHeader(SOAP_ACTION); if
         * (headerContainer.getMessageHeader() == null || soapAction == null ||
         * !soapAction[0].equals(SOAP_ACTION_VALUE)) {
         * soapMessage.getMimeHeaders(). setHeader(SOAP_ACTION,
         * SOAP_ACTION_VALUE); } String soapPartContentId =
         * soapMessage.getSOAPPart().getContentId(); if (soapPartContentId ==
         * null || soapPartContentId.equals("")) { soapPartContentId =
         * (needPatch ? " <" + SOAP_PART_CONTENT_ID + ">" :
         * SOAP_PART_CONTENT_ID);
         * soapMessage.getSOAPPart().setContentId(soapPartContentId); }
         */
        filename = null;
        messageOrder = null;
    }

    /**
     * Constructs a <code>EbxmlMessage</code> using the given
     * <code>InputStream</code> and default JAXM <code>MessageFactory</code>
     * implementation
     * 
     * @param headers
     *            MIME headers to be included in the message.
     * @param in
     *            Message content in form of <code>InputStream</code>.
     * @exception IOException
     *                Description of the Exception
     * @exception SOAPException
     *                Description of the Exception
     */
    public EbxmlMessage(MimeHeaders headers, InputStream in)
            throws IOException, SOAPException {
        this(MessageFactory.newInstance().createMessage(headers, in));
    }

    /**
     * Gets the <code>SOAPMessage</code> encapsulated in this
     * <code>EbxmlMessage</code>.
     * 
     * @return <code>SOAPMessage</code> representing this
     *         <code>EbxmlMessage</code>
     */
    public SOAPMessage getSOAPMessage() {
        return soapMessage;
    }

    /**
     * Adds a <code>MessageHeader</code> to this <code>EbxmlMessage</code>
     * with the given mandatory fields
     * 
     * @param fromPartyId
     *            Party ID of the sender [ebMSS 3.1.1.1]
     * @param toPartyId
     *            Party ID of the receiver [ebMSS 3.1.1.1]
     * @param cpaId
     *            CPA Id [ebMSS 3.1.2]
     * @param conversationId
     *            ID of the conversation in which this message is involved
     *            [ebMSS 3.1.3]
     * @param service
     *            Service name [ebMSS 3.1.4]
     * @param action
     *            Action name [ebMSS 3.1.5]
     * @param messageId
     *            Unique identifier of the message [ebMSS 3.1.6.1]
     * @param timestamp
     *            Date/time of the message header creation expressed as UTC
     *            [ebMSS 3.1.6.2]
     * @return the newly added <code>MessageHeader</code>
     * @throws SOAPException
     *             Description of the Exception
     */
    public MessageHeader addMessageHeader(String fromPartyId, String toPartyId,
            String cpaId, String conversationId, String service, String action,
            String messageId, String timestamp) throws SOAPException {

        return addMessageHeader(fromPartyId, null, toPartyId, null, cpaId,
                conversationId, service, action, messageId, timestamp);
    }

    /**
     * Adds a <code>MessageHeader</code> to this <code>EbxmlMessage</code>
     * with the given mandatory fields
     * 
     * @param fromPartyId
     *            Party ID of the sender [ebMSS 3.1.1.1]
     * @param fromPartyIdType
     *            PartyID type of the sender [ebMSS 3.1.1.1]
     * @param toPartyId
     *            Party ID of the receiver [ebMSS 3.1.1.1]
     * @param toPartyIdType
     *            PartyID type of the receiver [ebMSS 3.1.1.1]
     * @param cpaId
     *            CPA Id [ebMSS 3.1.2]
     * @param conversationId
     *            ID of the conversation in which this message is involved
     *            [ebMSS 3.1.3]
     * @param service
     *            Service name [ebMSS 3.1.4]
     * @param action
     *            Action name [ebMSS 3.1.5]
     * @param messageId
     *            Unique identifier of the message [ebMSS 3.1.6.1]
     * @param timestamp
     *            Date/time of the message header creation expressed as UTC
     *            [ebMSS 3.1.6.2]
     * @return the newly added <code>MessageHeader</code>
     * @throws SOAPException
     *             Description of the Exception
     */
    public MessageHeader addMessageHeader(String fromPartyId,
            String fromPartyIdType, String toPartyId, String toPartyIdType,
            String cpaId, String conversationId, String service, String action,
            String messageId, String timestamp) throws SOAPException {

        MessageHeader messageHeader = new MessageHeader(soapEnvelope,
                fromPartyId, fromPartyIdType, toPartyId, toPartyIdType, cpaId,
                conversationId, service, action, messageId, timestamp);
        headerContainer.addExtensionElement(messageHeader);

        return messageHeader;
    }

    /**
     * Adds a default <code>MessageHeader</code> to this
     * <code>EbxmlMessage</code>.
     * 
     * @return the newly added <code>MessageHeader</code>
     * @throws SOAPException
     */
    public MessageHeader addMessageHeader() throws SOAPException {
        MessageHeader messageHeader = new MessageHeader(soapEnvelope);
        headerContainer.addExtensionElement(messageHeader);
        return messageHeader;
    }

    /**
     * Get the message header of this ebXML message.
     * 
     * @return <code>MessageHeader</code> of this ebXML message.
     */
    public MessageHeader getMessageHeader() {
        return headerContainer.getMessageHeader();
    }

    /**
     * Gets the list of from party IDs. There can be multiple party IDs [ebMSS
     * 3.1.1].
     * 
     * @return Iterator pointing to a list of party IDs.
     */
    public Iterator getFromPartyIds() {
        final MessageHeader messageHeader = headerContainer.getMessageHeader();
        if (messageHeader == null) {
            return new ArrayList().iterator();
        }
        return messageHeader.getFromPartyIds();
    }

    /**
     * Gets the list of to party IDs. There can be multiple party IDs [ebMSS
     * 3.1.1].
     * 
     * @return Iterator pointing to a list of party IDs.
     */
    public Iterator getToPartyIds() {
        final MessageHeader messageHeader = headerContainer.getMessageHeader();
        if (messageHeader == null) {
            return new ArrayList().iterator();
        }
        return messageHeader.getToPartyIds();
    }

    /**
     * Gets cpaId
     * 
     * @return CPA ID
     */
    public String getCpaId() {
        final MessageHeader messageHeader = headerContainer.getMessageHeader();
        if (messageHeader == null) {
            return null;
        }
        return messageHeader.getCpaId();
    }

    /**
     * Gets conversationId
     * 
     * @return Conversation ID
     */
    public String getConversationId() {
        final MessageHeader messageHeader = headerContainer.getMessageHeader();
        if (messageHeader == null) {
            return null;
        }
        return messageHeader.getConversationId();
    }

    /**
     * Gets service name
     * 
     * @return Service name.
     */
    public String getService() {
        final MessageHeader messageHeader = headerContainer.getMessageHeader();
        if (messageHeader == null) {
            return null;
        }
        return messageHeader.getService();
    }

    /**
     * Gets service type
     * 
     * @return Service type.
     */
    public String getServiceType() {
        final MessageHeader messageHeader = headerContainer.getMessageHeader();
        if (messageHeader == null) {
            return null;
        }
        return messageHeader.getServiceType();
    }

    /**
     * Gets action name
     * 
     * @return Action name
     */
    public String getAction() {
        final MessageHeader messageHeader = headerContainer.getMessageHeader();
        if (messageHeader == null) {
            return null;
        }
        return messageHeader.getAction();
    }

    /**
     * Gets messageId
     * 
     * @return Unique message identifier
     */
    public String getMessageId() {
        final MessageHeader messageHeader = headerContainer.getMessageHeader();
        if (messageHeader == null) {
            return null;
        }
        return messageHeader.getMessageId();
    }

    /**
     * Gets timestamp
     * 
     * @return Timestamp of the message expressed in UTC format.
     */
    public String getTimestamp() {
        final MessageHeader messageHeader = headerContainer.getMessageHeader();
        if (messageHeader == null) {
            return null;
        }
        return messageHeader.getTimestamp();
    }

    /**
     * Gets TimeToLive of the message
     * 
     * @return TimeToLive expressed in UTC format.
     */
    public String getTimeToLive() {
        final MessageHeader messageHeader = headerContainer.getMessageHeader();
        if (messageHeader == null) {
            return null;
        }
        return messageHeader.getTimeToLive();
    }

    /**
     * Gets the flag stating if duplicate elimination is enabled or not.
     * 
     * @return true if duplicate elimination is required; false otherwise.
     */
    public boolean getDuplicateElimination() {
        final MessageHeader messageHeader = headerContainer.getMessageHeader();
        if (messageHeader == null) {
            return false;
        }
        return messageHeader.getDuplicateElimination();
    }

    /**
     * Add a SyncReply element to the message.
     * 
     * @throws SOAPException
     */
    public void addSyncReply() throws SOAPException {
        if (getMessageOrder() != null) {
            throw new EbxmlValidationException(
                    EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                    EbxmlValidationException.SEVERITY_ERROR, "<"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + MessageOrder.MESSAGE_ORDER
                            + "> has already been added which must not exist "
                            + "togther with <"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + SyncReply.SYNC_REPLY + ">!");
        }
        headerContainer.addExtensionElement(new SyncReply(soapEnvelope));
    }

    /**
     * Gets the flag stating if sync reply is enabled or not.
     * 
     * @return true if sync reply is enabled; false otherwise.
     */
    public boolean getSyncReply() {
        final SyncReply syncReply = headerContainer.getSyncReply();
        return (syncReply != null);
    }

    /**
     * Add acknowledgement request element to the message.
     * 
     * @param signed
     *            The feature to be added to the AckRequested attribute
     * @throws SOAPException
     */
    public void addAckRequested(boolean signed) throws SOAPException {
        headerContainer.addExtensionElement(new AckRequested(soapEnvelope,
                signed));
        if (messageOrder != null) {
            headerContainer.addExtensionElement(messageOrder);
            headerContainer.getMessageHeader().setDuplicateElimination();
        }
    }

    /**
     * Get acknowledgement request element.
     * 
     * @return <code>AckRequested</code> object representing the element.
     */
    public AckRequested getAckRequested() {
        return headerContainer.getAckRequested();
    }

    /**
     * Add acknowledgement element to the message using given timestamp,
     * refToMessageId.
     * 
     * @param timestamp
     *            Timestamp string expressed in UTC format.
     * @param refToMessage
     *            The feature to be added to the Acknowledgment attribute
     * @throws SOAPException
     *             Description of the Exception
     */
    public void addAcknowledgment(String timestamp, EbxmlMessage refToMessage)
            throws SOAPException {
        addAcknowledgment(timestamp, refToMessage, null, null);
    }

    /**
     * Add acknowledgement element to the message using given timestamp,
     * refToMessageId and fromPartyId.
     * 
     * @param timestamp
     *            Timestamp string expressed in UTC format.
     * @param fromPartyId
     *            Sender party ID.
     * @param refToMessage
     *            The feature to be added to the Acknowledgment attribute
     * @throws SOAPException
     *             Description of the Exception
     */
    public void addAcknowledgment(String timestamp, EbxmlMessage refToMessage,
            String fromPartyId) throws SOAPException {
        addAcknowledgment(timestamp, refToMessage, fromPartyId, null);
    }

    /**
     * Add acknowledgement element to the message using given timestamp,
     * refToMessageId, fromPartyId and fromPartyIdType.
     * 
     * @param timestamp
     *            Timestamp string expressed in UTC format.
     * @param fromPartyId
     *            Sender party ID.
     * @param fromPartyIdType
     *            Sender party ID type.
     * @param refToMessage
     *            The feature to be added to the Acknowledgment attribute
     * @throws SOAPException
     *             Description of the Exception
     */
    public void addAcknowledgment(String timestamp, EbxmlMessage refToMessage,
            String fromPartyId, String fromPartyIdType) throws SOAPException {
        final Acknowledgment acknowledgment = new Acknowledgment(soapEnvelope,
                timestamp, refToMessage, fromPartyId, fromPartyIdType);
        String actor = refToMessage.getAckRequested().getActor();
        if (actor != null) {
            acknowledgment.setActor(actor);
        }
        headerContainer.addExtensionElement(acknowledgment);
    }

    /**
     * Get acknowledgement element.
     * 
     * @return <code>Acknowledgement</code> object representing the element.
     */
    public Acknowledgment getAcknowledgment() {
        return headerContainer.getAcknowledgment();
    }

    /**
     * Add an error list to the message using given error code, severity and
     * description.
     * 
     * @param errorCode
     *            [ebMSS 4.2.3.4]
     * @param severity
     *            Valid values are "Error" and "Warning".
     * @param description
     *            Description of the error.
     * @throws SOAPException
     *             Description of the Exception
     */
    public void addErrorList(String errorCode, String severity,
            String description) throws SOAPException {
        addErrorList(errorCode, severity, description, null);
    }

    /**
     * Add an error list to the message using given error code, severity and
     * description.
     * 
     * @param errorCode
     *            [ebMSS 4.2.3.4]
     * @param severity
     *            Valid values are "Error" and "Warning".
     * @param description
     *            Description of the error.
     * @param location
     *            Location of the message containing the error.
     * @throws SOAPException
     */
    public void addErrorList(String errorCode, String severity,
            String description, String location) throws SOAPException {
        if (headerContainer.getStatusRequest() != null) {
            throw new EbxmlValidationException(
                    EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                    EbxmlValidationException.SEVERITY_ERROR, "<"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + StatusRequest.STATUS_REQUEST
                            + "> has already been "
                            + "added which must not exist togther with <"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + ErrorList.ERROR_LIST + ">!");
        }

        final ErrorList errorList = new ErrorList(soapEnvelope, errorCode,
                severity, description, ExtensionElement.LANG_TYPE, location);
        headerContainer.addExtensionElement(errorList);
    }

    /**
     * Get the error list in the message.
     * 
     * @return <code>ErrorList</code> object containing error list in the
     *         message. Returns null if it does not exist.
     */
    public ErrorList getErrorList() {
        return headerContainer.getErrorList();
    }

    /**
     * Add a status request element to the message.
     * 
     * @param refToMessageId
     *            Identifier of the message it is referring to.
     * @throws SOAPException
     */
    public void addStatusRequest(String refToMessageId) throws SOAPException {
        if (headerContainer.getStatusResponse() != null) {
            throw new EbxmlValidationException(
                    EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                    EbxmlValidationException.SEVERITY_ERROR, "<"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + StatusResponse.STATUS_RESPONSE
                            + "> has already been "
                            + "added which must not exist togther with <"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + StatusRequest.STATUS_REQUEST + ">!");
        }
        if (headerContainer.getManifest() != null) {
            throw new EbxmlValidationException(
                    EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                    EbxmlValidationException.SEVERITY_ERROR, "<"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + Manifest.MANIFEST
                            + "> has already been added which "
                            + "must not exist togther with <"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + StatusRequest.STATUS_REQUEST + ">!");
        }
        if (headerContainer.getErrorList() != null) {
            throw new EbxmlValidationException(
                    EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                    EbxmlValidationException.SEVERITY_ERROR, "<"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + ErrorList.ERROR_LIST
                            + "> has already been added which"
                            + " must not exist togther with <"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + StatusRequest.STATUS_REQUEST + ">!");
        }
        final StatusRequest statusRequest = new StatusRequest(soapEnvelope,
                refToMessageId);
        headerContainer.addExtensionElement(statusRequest);
    }

    /**
     * Get the status request element in the message.
     * 
     * @return Content of the status request element stored in
     *         <code>StatusRequest</code> object.
     */
    public StatusRequest getStatusRequest() {
        return headerContainer.getStatusRequest();
    }

    /**
     * Add a status response element to the message.
     * 
     * @param refToMessageId
     *            Identifier of the message it is referring to.
     * @param messageStatus
     *            Status string to be added in the response element.
     * @throws SOAPException
     */
    public void addStatusResponse(String refToMessageId, String messageStatus)
            throws SOAPException {
        addStatusResponse(refToMessageId, messageStatus, null);
    }

    /**
     * Add a status response element to the message.
     * 
     * @param refToMessageId
     *            Identifier of the message it is referring to.
     * @param messageStatus
     *            Status string to be added in the response element.
     * @param timestamp
     *            Timestamp of the status response expressed in UTC format.
     * @throws SOAPException
     */
    public void addStatusResponse(String refToMessageId, String messageStatus,
            String timestamp) throws SOAPException {
        if (headerContainer.getStatusRequest() != null) {
            throw new EbxmlValidationException(
                    EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                    EbxmlValidationException.SEVERITY_ERROR, "<"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + StatusRequest.STATUS_REQUEST
                            + "> has already been "
                            + "added which must not exist togther with <"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + StatusResponse.STATUS_RESPONSE + ">!");
        }
        if (headerContainer.getManifest() != null) {
            throw new EbxmlValidationException(
                    EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                    EbxmlValidationException.SEVERITY_ERROR, "<"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + Manifest.MANIFEST
                            + "> has already been added which "
                            + "must not exist togther with <"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + StatusResponse.STATUS_RESPONSE + ">!");
        }
        if (headerContainer.getErrorList() != null) {
            throw new EbxmlValidationException(
                    EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                    EbxmlValidationException.SEVERITY_ERROR, "<"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + ErrorList.ERROR_LIST
                            + "> has already been added which"
                            + " must not exist togther with <"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + StatusResponse.STATUS_RESPONSE + ">!");
        }

        final StatusResponse statusResponse = new StatusResponse(soapEnvelope,
                refToMessageId, messageStatus, timestamp);
        headerContainer.addExtensionElement(statusResponse);
    }

    /**
     * Get the status response element in the message.
     * 
     * @return Content of the status response element stored in
     *         <code>StatusResponse</code> object.
     */
    public StatusResponse getStatusResponse() {
        return headerContainer.getStatusResponse();
    }

    /**
     * Add a MessageOrder element to the header.
     * 
     * @param status
     *            Status of the sequence number. It should have the value of
     *            either MessageOrder.STATUS_RESET or
     *            MessageOrder.STATUS_CONTINUE.
     * @param sequenceNumber
     *            Sequence number to be assigned to this message.
     * @throws SOAPException
     *             Description of the Exception
     */
    public void addMessageOrder(int status, int sequenceNumber)
            throws SOAPException {
        if (messageOrder == null) {
            messageOrder = new MessageOrder(soapEnvelope, status,
                    sequenceNumber);
            if (headerContainer.getAckRequested() != null) {
                headerContainer.addExtensionElement(messageOrder);
                headerContainer.getMessageHeader().setDuplicateElimination();
            }
        } else {
            throw new SOAPValidationException(
                    SOAPValidationException.SOAP_FAULT_CLIENT, "<"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + MessageOrder.MESSAGE_ORDER
                            + "> has already been " + "added.");
        }
    }

    /**
     * Get the message order element in the message.
     * 
     * @return <code>MessageOrder</code> object if it exists in the ebXML
     *         message; null otherwise.
     */
    public MessageOrder getMessageOrder() {
        return headerContainer.getMessageOrder();
    }

    /**
     * Get the digital signatures in the message.
     * 
     * @return Iterator of <code>Signature</code> objects in the message.
     */
    public Iterator getSignatures() {
        return headerContainer.getSignatures();
    }

    /**
     * Add an ebXML message payload container.
     * 
     * @param dataHandler
     *            the <code>DataHandler</code> that generates the payload
     *            content.
     * @param contentId
     *            the contentId of this payload attachment. The contentId must
     *            be unique among all payload attachment.
     * @param description
     *            the description of this payload.
     * @return the <code>PayloadContainer</code> object that is created and
     *         added.
     * @throws SOAPException
     */
    public PayloadContainer addPayloadContainer(DataHandler dataHandler,
            String contentId, String description) throws SOAPException {
        if (headerContainer.getStatusRequest() != null) {
            throw new EbxmlValidationException(
                    EbxmlValidationException.EBXML_ERROR_INCONSISTENT,
                    EbxmlValidationException.SEVERITY_ERROR, "<"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + StatusRequest.STATUS_REQUEST
                            + "> has already been "
                            + "added which must not exist togther with <"
                            + ExtensionElement.NAMESPACE_PREFIX_EB + ":"
                            + Manifest.MANIFEST
                            + "> referring to added payloads!");
        }

        Manifest manifest = headerContainer.getManifest();
        if (manifest == null) {
            manifest = new Manifest(soapEnvelope);
            headerContainer.addExtensionElement(manifest);
        }
        final Reference reference = manifest.addReference(contentId,
                Reference.HREF_PREFIX + contentId);
        reference.addDescription(description);

        final PayloadContainer payload;
        if (needPatch) {
            payload = new PayloadContainer(dataHandler, "<" + contentId + ">",
                    reference);
        } else {
            payload = new PayloadContainer(dataHandler, contentId, reference);
        }
        payloadContainers.add(payload);

        // we now keep the SOAP message object away from attachment to avoid
        // it to load the payload to memory
        AttachmentPart attachment = soapMessage.createAttachmentPart();
        attachment.setContentId(needPatch ? " <" + contentId +
                ">" : contentId);
        attachment.setDataHandler(dataHandler);
        soapMessage.addAttachmentPart(attachment);

        return payload;
    }

    /**
     * Gets all <code>PayloadContainer</code>s' attached in this
     * <code>EbxmlMessage</code> object.
     * 
     * @return An iterator point to a list of payload containers.
     */
    public Iterator getPayloadContainers() {
        return payloadContainers.iterator();
    }

    /**
     * Sets the <code>PayloadContainer</code>
     * 
     * @param payloads
     *            The new payloadContainers value
     * @return The old payload container as an array list
     */
    public ArrayList setPayloadContainers(ArrayList payloads) {
        ArrayList oldPayloads = this.payloadContainers;
        this.payloadContainers = payloads;
        return oldPayloads;
    }

    /**
     * Gets the payload that is identified by the given content ID.
     * 
     * @param contentId
     *            Content ID of the payload to be retrieved.
     * @return <code>PayloadContainer</code> of the given content ID or
     *         <code>null</code> if no such <code>PayloadContainer</code>
     *         exists
     */
    public PayloadContainer getPayloadContainer(String contentId) {
        for (Iterator i = payloadContainers.iterator(); i.hasNext();) {
            final PayloadContainer payload = (PayloadContainer) i.next();
            if (payload.getContentId().equals(contentId)) {
                return payload;
            }
        }
        return null;
    }

    /**
     * Gets the number of payloads in this ebXML message
     * 
     * @return the number of payloads
     */
    public int getPayloadCount() {
        return payloadContainers.size();
    }

    /**
     * Gets the Manifest element in this ebXML message
     * 
     * @return Manifest element
     */
    public Manifest getManifest() {
        return headerContainer.getManifest();
    }

    /**
     * Updates the encapsulated <code>SOAPMessage</code> with all changes that
     * have been made to it.
     *
     * @throws SOAPException
     * @see javax.xml.soap.SOAPMessage#saveChanges() 
     */
    public void saveChanges() throws SOAPException {
    	// It may cause lose of attachment in JDK1.6.
    	soapMessage.saveChanges();
    }

    /**
     * Indicates whether the encapsulated <code>SOAPMessage</code> need to be
     * updated by calling <code>saveChanges</code> on it.
     * 
     * @return true if it is required to call saveChanges on the message; false
     *         otherwise.
     * @see javax.xml.soap.SOAPMessage#saveRequired()         
     */
    public boolean saveRequired() {
        return soapMessage.saveRequired();
    }

    /**
     * Gets the MIME headers of this ebXML message
     * 
     * @return the MIME headers of this ebXML message
     * @throws IOException
     *             Description of the Exception
     * @throws SOAPException
     *             Description of the Exception
     */
    public Map getMimeHeaders() throws IOException, SOAPException {
        return getMimeHeaders(Constants.DEFAULT_CONTENT_TRANSFER_ENCODING,
                Constants.DEFAULT_CONTENT_TRANSFER_ENCODING);
    }

    /**
     * Gets the MIME headers of this ebXML message
     * 
     * @param soapEncoding
     *            content transfer encoding to be applied to SOAP part when
     *            computing length
     * @param payloadEncoding
     *            content transfer encoding to be applied to payload when
     *            computing length
     * @return the MIME headers of this ebXML message
     * @throws IOException
     *             Description of the Exception
     * @throws SOAPException
     *             Description of the Exception
     */
    public Map getMimeHeaders(String soapEncoding, String payloadEncoding)
            throws IOException, SOAPException {
        HashMap headers = new HashMap();

        // fill in Content-Type
        String contentType;
        if (getPayloadCount() > 0) {
            if (needPatch) {
                contentType = Constants.MULTIPART_RELATED_TYPE + "\""
                        + mimeBoundary + "\"; " + Constants.CHARACTER_SET
                        + "=\"" + Constants.CHARACTER_ENCODING + "\"; "
                        + Constants.START + "=\"<"
                        + soapMessage.getSOAPPart().getContentId() + ">\"";
            } else {
                contentType = Constants.MULTIPART_RELATED_TYPE + "\""
                        + mimeBoundary + "\"; " + Constants.CHARACTER_SET
                        + "=\"" + Constants.CHARACTER_ENCODING + "\"; "
                        + Constants.START + "=\""
                        + soapMessage.getSOAPPart().getContentId() + "\"";
            }
        } else {
            contentType = Constants.TEXT_XML_TYPE + "; "
                    + Constants.CHARACTER_SET + "=\""
                    + Constants.CHARACTER_ENCODING + "\"";
        }
        headers.put(Constants.CONTENT_TYPE, contentType);

        // fill in Content-Length
        headers.put(Constants.CONTENT_LENGTH, String.valueOf(serialize(null,
                soapEncoding, payloadEncoding, true)));

        // fill in SOAPAction
        headers.put(SOAP_ACTION, SOAP_ACTION_VALUE);

        return headers;
    }

    /**
     * Walks through the serialization process to get the content length. It can
     * do the actually serialization also optionally
     * 
     * @param out
     *            <code>OutputStream</code> to write the message to.
     * @param soapEncoding
     *            content transfer encoding to be applied to SOAPPart
     * @param payloadEncoding
     *            content transfer encoding to be applied to payload
     * @param getLengthOnly
     *            get length only and do no actual serialization
     * @return the content length of the serialization
     * @throws IOException
     * @throws SOAPException
     */
    private long serialize(OutputStream out, String soapEncoding,
            String payloadEncoding, boolean getLengthOnly) throws IOException,
            SOAPException {

        /*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os;
        try {
            os = MimeUtility.encode(baos, soapEncoding);
        } catch (Exception e) {
            throw new SOAPException("Content-Transfer-Encoding encode error: "
                    + e.getMessage());
        }
        soapMessage.writeTo(os);
        long soapMessageLength = baos.toByteArray().length;
        long totalLength = 0;

        try {
            os = MimeUtility.encode(out, soapEncoding);
        } catch (Exception e) {
            throw new SOAPException("Content-Transfer-Encoding encode error: "
                    + e.getMessage());
        }

        Iterator i = getPayloadContainers();

        // not multipart
        if (!i.hasNext()) {
            if (!getLengthOnly) {
                soapMessage.writeTo(os);
                os.flush();
            }
            return soapMessageLength;
        }

        String buffer;
        byte[] bytes;

        // print SOAP part
        buffer = Constants.MIME_BOUNDARY_PREFIX + mimeBoundary + Constants.CRLF;
        buffer = buffer + Constants.CONTENT_TYPE + ": "
                + Constants.TEXT_XML_TYPE + "; " + Constants.CHARACTER_SET
                + "=\"" + Constants.CHARACTER_ENCODING + "\"" + Constants.CRLF;
        if (needPatch) {
            buffer = buffer + Constants.CONTENT_ID + ": <"
                    + soapMessage.getSOAPPart().getContentId() + ">"
                    + Constants.CRLF;
        } else {
            buffer = buffer + Constants.CONTENT_ID + ": "
                    + soapMessage.getSOAPPart().getContentId() + Constants.CRLF;
        }
        buffer = buffer + Constants.CONTENT_TRANSFER_ENCODING + ": "
                + soapEncoding + Constants.CRLF;
        buffer = buffer + Constants.CRLF;
        bytes = buffer.getBytes(Constants.CHARACTER_ENCODING);
        totalLength += bytes.length;
        totalLength += soapMessageLength;
        if (!getLengthOnly) {
            out.write(bytes);
            out.flush();
            soapMessage.writeTo(os);
            os.flush();
        }
        bytes = Constants.CRLF.getBytes(Constants.CHARACTER_ENCODING);
        totalLength += bytes.length;
        if (!getLengthOnly) {
            out.write(bytes);
            out.flush();
        }

        // print payloads
        while (i.hasNext()) {
            PayloadContainer pc = (PayloadContainer) i.next();

            buffer = Constants.MIME_BOUNDARY_PREFIX + mimeBoundary
                    + Constants.CRLF;
            buffer = buffer + Constants.CONTENT_TYPE + ": "
                    + pc.getContentType() + Constants.CRLF;
            System.out.println("Payload Container Content-id: '"+pc.getContentId());
            buffer = buffer
                    + Constants.CONTENT_ID
                    + ": "
                    + (needPatch ? "<" + pc.getContentId() + ">" : pc
                            .getContentId()) + Constants.CRLF;
            System.out.println("Buffer : " + buffer.toString());
            buffer = buffer + Constants.CONTENT_TRANSFER_ENCODING + ": "
                    + payloadEncoding + Constants.CRLF;
            buffer = buffer + Constants.CRLF;
            bytes = buffer.getBytes(Constants.CHARACTER_ENCODING);
            totalLength += bytes.length;
            if (!getLengthOnly) {
                System.out.println("can you write those bytes " + out.getClass());
                out.write(bytes);
                out.flush();
            }

            long payloadLength = pc.getContentLength();
            if (payloadLength == -1 || !getLengthOnly
                    || !payloadEncoding.equalsIgnoreCase("binary")) {
                InputStream in = pc.getDataHandler().getInputStream();
                byte[] b = new byte[4096];
                int bytesRead;
                long totalBytes = 0;
                try {
                    os = MimeUtility.encode(out, payloadEncoding);
                } catch (Exception e) {
                    throw new SOAPException(
                            "Content-Transfer-Encoding encode error: "
                                    + e.getMessage());
                }
                while ((bytesRead = in.read(b)) > 0) {
                    totalBytes += bytesRead;
                    if (!getLengthOnly) {
                        os.write(b, 0, bytesRead);
                        os.flush();
                    }
                }
                payloadLength = totalBytes;
            }
            totalLength += payloadLength;

            bytes = Constants.CRLF.getBytes(Constants.CHARACTER_ENCODING);
            totalLength += bytes.length;
            if (!getLengthOnly) {
                out.write(bytes);
            }
        }

        // print last boundary
        buffer = Constants.MIME_BOUNDARY_PREFIX + mimeBoundary + "--"
                + Constants.CRLF;
        bytes = buffer.getBytes(Constants.CHARACTER_ENCODING);
        totalLength += bytes.length;
        if (!getLengthOnly) {
            out.write(bytes);
        }
        return totalLength;
        */
        soapMessage.writeTo(out);
        return 0;
    }

    /**
     * Writes the encapsulated <code>SOAPMessage</code> to the given output
     * stream. The externalization format is as defined by the SOAP 1.1 with
     * Attachments Specification.
     * 
     * @param out
     *            <code>OutputStream</code> to write the message to.
     * @throws IOException
     *             Description of the Exception
     * @throws SOAPException
     *             Description of the Exception
     */
    public void writeTo(OutputStream out) throws IOException, SOAPException {

        // we now keep the SOAP message object away from attachment to avoid
        // it to load the payload to memory, therefore we are doing our own
        // writeTo() here
        //        soapMessage.writeTo(out);
        serialize(out, Constants.DEFAULT_CONTENT_TRANSFER_ENCODING,
                Constants.DEFAULT_CONTENT_TRANSFER_ENCODING, false);
    }

    /**
     * Writes the encapsulated <code>SOAPMessage</code> to the given output
     * stream. The externalization format is as defined by the SOAP 1.1 with
     * Attachments Specification.
     * 
     * @param out
     *            <code>OutputStream</code> to write the message to.
     * @param soapEncoding
     *            Description of the Parameter
     * @param payloadEncoding
     *            Description of the Parameter
     * @throws IOException
     *             Description of the Exception
     * @throws SOAPException
     *             Description of the Exception
     */
    public void writeTo(OutputStream out, String soapEncoding,
            String payloadEncoding) throws IOException, SOAPException {

        // we now keep the SOAP message object away from attachment to avoid
        // it to load the payload to memory, therefore we are doing our own
        // writeTo() here
        //        soapMessage.writeTo(out);
        serialize(out, soapEncoding, payloadEncoding, false);
    }

    /**
     * Checks whether the number of payloads in the SOAP message matches with
     * the Manifest element in the header or not.
     * 
     * @return true if the number of payloads is consistent, false otherwise.
     */
    /*
     * public boolean isNumPayloadConsistent() { Manifest manifest =
     * headerContainer.getManifest(); if (manifest == null) { / no manifest
     * exist.. assume ok. return true; } ArrayList contentIDs = new ArrayList();
     * Iterator i = getPayloadContainers(); while (i.hasNext()) {
     * PayloadContainer pc = (PayloadContainer) i.next();
     * contentIDs.add(pc.getContentId()); } i = manifest.getReferences(); while
     * (i.hasNext()) { Reference r = (Reference) i.next(); String cid =
     * r.getHref(); if (cid.startsWith("cid:")) { cid = cid.substring(4); } if
     * (!contentIDs.contains(cid)) { payloadInError = cid; return false; } }
     * return true; }
     */
    /**
     * Checks whether the number of payloads in the SOAP message matches with
     * the Manifest element in the header or not, and return the Content-ID of
     * the inconsistent payload.
     * 
     * @return content-id of the inconsistent payload; null if all payloads are
     *         consistent.
     */
    public String getPayloadInError() {
        Manifest manifest = headerContainer.getManifest();
        if (manifest == null) {
            // no manifest exist.. assume ok.
            return null;
        }
        ArrayList contentIDs = new ArrayList();
        Iterator i = getPayloadContainers();
        while (i.hasNext()) {
            PayloadContainer pc = (PayloadContainer) i.next();
            contentIDs.add(pc.getContentId());
        }

        i = manifest.getReferences();
        while (i.hasNext()) {
            Reference r = (Reference) i.next();
            String cid = r.getHref();
            if (cid.startsWith("cid:")) {
                cid = cid.substring(4);
            }
            if (!contentIDs.contains(cid)) {
                return cid;
            }
        }

        return null;
    }

    /**
     * Sets the fileName attribute of the EbxmlMessage object. This function
     * will only be used by the Hermes Server itself, and it is not expected for
     * the client to call it.
     * 
     * @param filename
     *            The new fileName value
     */
    public void setFileName(String filename) {
        this.filename = filename;
    }

    /**
     * Gets the fileName attribute of the EbxmlMessage object. This function
     * will only be used by the Hermes Server itself, and it is not expected for
     * the client to call it.
     * 
     * @return The fileName value
     */
    public String getFileName() {
        return filename;
    }

    /**
     * Get the soap envelope in bytes. This function will only be used by the
     * Hermes Server itself, and it is not expected for the client to call it.
     */
    byte[] getSoapEnvelopeBytes() {
        return soapEnvelopeBytes;
    }

    /**
     * set the soap envelope in bytes. Those bytes will be used to verify This
     * function will only be used by the Hermes Server itself, and it is not
     * expected for the client to call it. the signature.
     */
    public void setSoapEnvelopeBytes(byte[] soapEnvelopeBytes) {
        this.soapEnvelopeBytes = soapEnvelopeBytes;
    }

    /**
     * set the persistence info to the message This function will only be used
     * by the Hermes Server itself, and it is not expected for the client to
     * call it.
     * 
     * @param persistenceName
     *            the name on the persistence handler
     * @param handler
     *            the persistence handler
     */
    public void setPersistenceInfo(String persistenceName, Object handler,
            DataSource datasource) {
        this.persistenceName = persistenceName;
        persistenceHandler = handler;
        this.datasource = datasource;
    }

    /**
     * get the Persistence name if it is stored. This function will only be used
     * by the Hermes Server itself, and it is not expected for the client to
     * call it.
     * 
     * @return the persitence name
     */
    public String getPersistenceName() {
        return persistenceName;
    }

    /**
     * get the persistence handler if it is stored. This function will only be
     * used by the Hermes Server itself, and it is not expected for the client
     * to call it.
     * 
     * @return the persistence handler
     */
    public Object getPersistenceHandler() {
        return persistenceHandler;
    }

    public static Object getMessageFromDataSource(DataSource dataSource,
            boolean withAttachments) throws SOAPException, IOException {
        return getMessageFromDataSource(dataSource, withAttachments, null);
    }

    /**
     * Gets the messageFromDataSource attribute of the MessageServer class
     * 
     * @param dataSource
     *            Description of the Parameter
     * @param withAttachments
     *            Description of the Parameter
     * @return The messageFromDataSource value
     * @throws SOAPException
     *             Description of the Exception
     */
    private static Object getMessageFromDataSource(DataSource dataSource,
            boolean withAttachments, EbxmlMessage ebxmlMessage)
            throws SOAPException, IOException {
        InputStream fis = null;
        PushbackInputStream fileStream = null;
        try {
            fis = dataSource.getInputStream();
            MessageSemiParsedOutput parsedOutput = parseSoapEnvelopeOnly(fis);
            fileStream = parsedOutput.getInputStream();
            byte[] soapMessageBytes = parsedOutput.getSoapMessageBytes();
            soapMessageBytes = getSoapEnvelopeBytesFromParsedOutput(parsedOutput);
            int soapMessageFileOffset = 0;
            int soapMessageLength = soapMessageBytes.length;
            int lastIndex = parsedOutput.getLastIndex();
            String boundary = parsedOutput.getBoundary();
            /*
             * final MimeHeaders headers = new MimeHeaders();
             * headers.addHeader(Constants.CONTENT_TYPE,
             * Constants.TEXT_XML_TYPE);
             */
            final MimeHeaders headers = parsedOutput.getMimeHeaders();
            /**
             * added for parsing message without payload. For such case,
             */
            if (headers.getHeader(Constants.CONTENT_TYPE) == null) {
                headers.addHeader(Constants.CONTENT_TYPE,
                        Constants.TEXT_XML_TYPE);
            }
            final SOAPMessage soapMessage;
            soapMessage = MessageFactory.newInstance()
                    .createMessage(
                            headers,
                            new ByteArrayInputStream(soapMessageBytes, 0,
                                    lastIndex + 1));

            ArrayList payloads = new ArrayList();
            if (boundary != null && withAttachments) {
                byte[] line = parsedOutput.getLastLine();
                long offset = parsedOutput.getOffset();
                lastIndex = line.length - 1;
                for (; lastIndex >= 0; lastIndex--) {
                    if (line[lastIndex] != 0xA && line[lastIndex] != 0xD) {
                        break;
                    }
                }
                String s = new String(line, 0, lastIndex + 1);

                while (!s.endsWith(boundary + Constants.MIME_BOUNDARY_PREFIX)) {
                    /*
                     * Find the empty line delimiter separating the MIME header
                     * and the attachment content
                     */
                    final MimeHeaders attachmentHeaders = new MimeHeaders();
                    line = readLine(fileStream);
                    offset += line.length;
                    while (line.length > 0 && line[0] != 0xA && line[0] != 0xD) {
                        String lineString = new String(line).trim(), name;
                        int colonIndex = lineString.indexOf(':');
                        if (colonIndex >= 0) {
                            name = lineString.substring(0, colonIndex).trim();
                            String value = lineString.substring(colonIndex + 1)
                                    .trim();

                            /*
                             * final StringTokenizer t = new StringTokenizer
                             * (new String(line), "\t\n\r\f: "); if
                             * (t.hasMoreTokens()) { final String name =
                             * t.nextToken(); String value = t.nextToken();
                             */
                            if (name.equalsIgnoreCase(Constants.CONTENT_ID)) {
                                if (value.startsWith("<")
                                        && !EbxmlMessage.needPatch) {
                                    value = value.substring(1);
                                    if (value.endsWith(">")) {
                                        value = value.substring(0, value
                                                .length() - 1);
                                    }
                                }
                            }

                            attachmentHeaders.addHeader(name, value);
                        }
                        line = readLine(fileStream);
                        offset += line.length;
                    }
                    if (line.length == 0) {
                        throw new SOAPException(
                                "missing empty line delimiter of MIME header");
                    }

                    long length = 0;
                    byte[] previousLine = null;
                    line = readLine(fileStream);
                    s = new String(line);
                    while (line.length > 0
                            && !(s.startsWith(Constants.MIME_BOUNDARY_PREFIX
                                    + boundary))) {
                        length += line.length;
                        previousLine = line;
                        line = readLine(fileStream);
                        s = new String(line);
                    }

                    if (line.length == 0) {
                        throw new SOAPException("missing ending MIME boundary");
                    }

                    lastIndex = previousLine.length - 1;
                    for (; lastIndex >= 0; lastIndex--) {
                        if (previousLine[lastIndex] != 0xA
                                && previousLine[lastIndex] != 0xD) {
                            break;
                        }
                    }

                    String[] ahs = attachmentHeaders
                            .getHeader(Constants.CONTENT_TYPE);
                    String contentType = null;
                    if (ahs == null || ahs.length == 0) {
                        throw new SOAPException("missing "
                                + Constants.CONTENT_TYPE + " attachment");
                    } else if (ahs.length == 1) {
                        contentType = ahs[0];
                    } else {
                        throw new SOAPException("more than one "
                                + Constants.CONTENT_TYPE + " in attachment");
                    }
                    ahs = attachmentHeaders
                            .getHeader(Constants.CONTENT_TRANSFER_ENCODING);
                    String encoding = null;
                    if (ahs != null) {
                        if (ahs.length == 1) {
                            encoding = ahs[0];
                        } else if (ahs.length > 1) {
                            throw new SOAPException("more than one "
                                    + Constants.CONTENT_TRANSFER_ENCODING
                                    + " in attachment");
                        }
                    }

                    final AttachmentDataSource ads = new AttachmentDataSource(
                            dataSource, offset, length
                                    - (previousLine.length - 1 - lastIndex),
                            contentType, encoding, false);
                    final DataHandler dh = new DataHandler(ads);
                    String contentId = null;
                    ahs = attachmentHeaders.getHeader(Constants.CONTENT_ID);
                    if (ahs != null) {
                        contentId = ahs[0];
                    }
                    PayloadContainer attachment = new PayloadContainer(dh,
                            contentId, null);
                    for (Iterator i = attachmentHeaders.getAllHeaders(); i
                            .hasNext();) {
                        final MimeHeader header = (MimeHeader) i.next();
                        final String name = header.getName();
                        if (!name.equals(Constants.CONTENT_TYPE)
                                && !name.equals(Constants.CONTENT_ID)) {
                            attachment.setMimeHeader(name, header.getValue());
                        }
                    }
                    payloads.add(attachment);

                    // add the attachment to soap message                    
                    AttachmentPart attachmentPart = soapMessage
                            .createAttachmentPart();
                    attachmentPart.setContentId(needPatch ? " <" + contentId +
                            ">" : contentId);
                    attachmentPart.setDataHandler(dh);
                    soapMessage.addAttachmentPart(attachmentPart);

                    offset += (length + line.length);

                    lastIndex = line.length - 1;
                    for (; lastIndex >= 0; lastIndex--) {
                        if (line[lastIndex] != 0xA && line[lastIndex] != 0xD) {
                            break;
                        }
                    }
                    s = new String(line, 0, lastIndex + 1);
                }
            }

            if (withAttachments) {
                EbxmlMessage message;
                if (ebxmlMessage == null) {
                    message = new EbxmlMessage(soapMessage);
                } else {                    
                    ebxmlMessage.init(soapMessage);
                    message = ebxmlMessage;
                }
                message.setPayloadContainers(payloads);
                message.setSoapEnvelopeBytes(soapMessageBytes);
                return message;
            }

            return soapMessage;
        } catch (IOException e) {
            throw new SOAPException(e.toString());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new SOAPException(e.toString());
                }
            }
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    throw new SOAPException(e.toString());
                }
            }
        }
    }

    /**
     * The function which parse the soap envelope only from the inputstream.
     */
    private static MessageSemiParsedOutput parseSoapEnvelopeOnly(InputStream fis)
            throws SOAPException, IOException {
        PushbackInputStream fileStream = new PushbackInputStream(fis);
        String boundary = null;
        int soapMessageFileOffset = 0;
        int soapMessageLength = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] line = readLine(fileStream);
        long offset = line.length;
        String s = new String(line);
        while (line.length > 0
                && !(s.startsWith(Constants.MIME_BOUNDARY_PREFIX))) {
            out.write(line);
            line = readLine(fileStream);
            offset += line.length;
            s = new String(line);
        }
        byte[] soapMessageBytes = out.toByteArray();
        soapMessageLength = soapMessageBytes.length;
        int lastIndex = soapMessageBytes.length - 1;
        MimeHeaders soapEnvelopeHeaders = new MimeHeaders();
        if (line.length > 0) {
            lastIndex = line.length - 1;
            for (; lastIndex >= 0; lastIndex--) {
                if (line[lastIndex] != 0xA && line[lastIndex] != 0xD) {
                    break;
                }
            }
            boundary = new String(line,
                    Constants.MIME_BOUNDARY_PREFIX.length(), lastIndex
                            - Constants.MIME_BOUNDARY_PREFIX.length() + 1);

            /*
             * Find the empty line delimiter separating the MIME header and the
             * SOAPPart content
             */
            line = readLine(fileStream);
            MimeHeader header = parseMimeHeader(line);
            if (header != null) {
                soapEnvelopeHeaders.addHeader(header.getName(), header
                        .getValue());
            }
            offset += line.length;
            while (line.length > 0 && line[0] != 0xA && line[0] != 0xD) {
                line = readLine(fileStream);
                header = parseMimeHeader(line);
                if (header != null) {
                    soapEnvelopeHeaders.addHeader(header.getName(), header
                            .getValue());
                }
                offset += line.length;
            }

            if (line.length == 0) {
                throw new SOAPException(
                        "missing empty line delimiter of MIME header");
            }

            /*
             * Find the location and the length of the SOAPPart content with
             * offset being the beginning position
             */
            soapMessageFileOffset = (int) offset;
            out = new ByteArrayOutputStream();
            line = readLine(fileStream);
            offset += line.length;
            s = new String(line);
            while (line.length > 0
                    && !(s
                            .startsWith(Constants.MIME_BOUNDARY_PREFIX
                                    + boundary))) {
                out.write(line);
                line = readLine(fileStream);
                offset += line.length;
                s = new String(line);
            }

            if (line.length == 0) {
                throw new SOAPException("missing ending MIME boundary");
            }
            soapMessageBytes = out.toByteArray();
            soapMessageLength = soapMessageBytes.length;
            lastIndex = soapMessageBytes.length - 1;
            for (; lastIndex >= 0; lastIndex--) {
                if (soapMessageBytes[lastIndex] != 0xA
                        && soapMessageBytes[lastIndex] != 0xD) {
                    break;
                }
            }
        }
        return new MessageSemiParsedOutput(fileStream, lastIndex,
                soapMessageBytes, soapMessageFileOffset, boundary, line,
                offset, soapEnvelopeHeaders);
    }

    private static MimeHeader parseMimeHeader(byte[] line) {
        String lineString = new String(line).trim();
        int colonIndex = lineString.indexOf(':');
        if (colonIndex >= 0) {
            String header = lineString.substring(0, colonIndex).trim();
            String value = lineString.substring(colonIndex + 1).trim();
            return new MimeHeader(header, value);
        } else {
            return null;
        }
    }

    /**
     * parse the message from InputStream and get the byte array of SOAP
     * Envelope
     * 
     * @param stream
     *            the InputStream contains the message
     * @return the byte array of the Soap Envelope
     * @throws MessageServerException
     *             throw if there is error on parsing
     */
    public static byte[] getSoapEnvelopeBytesFromStream(InputStream stream)
            throws SOAPException {
        try {
            MessageSemiParsedOutput parsedOutput = parseSoapEnvelopeOnly(stream);
            return getSoapEnvelopeBytesFromParsedOutput(parsedOutput);
        } catch (IOException e) {
            throw new SOAPException(e.toString());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new SOAPException(e.toString());
                }
            }
        }
    }

    /**
     * parse the message from InputStream and get the byte array of SOAP
     * Envelope
     * 
     * @param parsedOutput
     *            the message semi parsed output
     * @return the byte array of the Soap Envelope
     * @throws MessageServerException
     *             throw if there is error on parsing
     */
    private static byte[] getSoapEnvelopeBytesFromParsedOutput(
            MessageSemiParsedOutput parsedOutput) throws SOAPException {
        InputStream pStream = null;
        try {
            byte[] soapEnvelopeBytes = parsedOutput.getSoapMessageBytes();
            String[] encodingHeaders = parsedOutput.getMimeHeaders().getHeader(
                    Constants.CONTENT_TRANSFER_ENCODING);
            String encoding = null;
            if (encodingHeaders != null) {
                if (encodingHeaders.length == 1) {
                    encoding = encodingHeaders[0];
                } else {
                    throw new IOException(
                            "More than one encoding on soap envelope");
                }
            }
            if (encoding != null && !encoding.equals("binary")) {
                ByteArrayInputStream bais = new ByteArrayInputStream(
                        soapEnvelopeBytes);
                pStream = MimeUtility.decode(bais, encoding);
                bais.close();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int read = pStream.read(buffer);
                while (read != -1) {
                    baos.write(buffer, 0, read);
                    read = pStream.read(buffer);
                }
                return baos.toByteArray();
            } else {
                return soapEnvelopeBytes;
            }
        } catch (MessagingException e) {
            throw new SOAPException(e.toString());
        } catch (IOException e) {
            throw new SOAPException(e.toString());
        } finally {
            if (pStream != null) {
                try {
                    pStream.close();
                } catch (IOException e) {
                    throw new SOAPException(e.toString());
                }
            }
        }
    }

    /**
     * Description of the Method
     * 
     * @param in
     *            Description of the Parameter
     * @return Description of the Return Value
     * @throws IOException
     *             Description of the Exception
     */
    private static byte[] readLine(PushbackInputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        int c = in.read();
        for (; c != 0xA && c != 0xD && c != -1; c = in.read()) {
            out.write(c);
        }
        if (c == 0xD) {
            out.write(c);
            c = in.read();
            if (c == 0xA) {
                out.write(c);
            } else if (c != -1) {
                in.unread(c);
            }
        } else if (c == 0xA) {
            out.write(c);
        }

        return out.toByteArray();
    }

    /**
     * class represent the data returned from the parseSoapEnvelopeOnly
     */
    private static class MessageSemiParsedOutput {
        private PushbackInputStream istream;

        private byte[] soapMessage;

        private int soapMessageOffset;

        private String boundary;

        private int lastIndex;

        private byte[] endLine;

        private long offset;

        private MimeHeaders headers;

        public MessageSemiParsedOutput(PushbackInputStream istream,
                int lastIndex, byte[] soapMessage, int soapMessageOffset,
                String boundary, byte[] endLine, long offset,
                MimeHeaders headers) {
            this.istream = istream;
            this.soapMessage = soapMessage;
            this.soapMessageOffset = soapMessageOffset;
            this.boundary = boundary;
            this.lastIndex = lastIndex;
            this.endLine = endLine;
            this.offset = offset;
            this.headers = headers;
        }

        /**
         * get the mime headers
         * 
         * @return the mime headers
         */
        public MimeHeaders getMimeHeaders() {
            return headers;
        }

        /**
         * get the offset up on parsing
         */
        public long getOffset() {
            return offset;
        }

        /**
         * get the last line up on parsing
         */
        public byte[] getLastLine() {
            return endLine;
        }

        /**
         * get the last index to read the soap message
         */
        public int getLastIndex() {
            return lastIndex;
        }

        /**
         * get the remain stream which is not parsed
         */
        public PushbackInputStream getInputStream() {
            return istream;
        }

        /**
         * get the byte array that contains the soap message
         */
        public byte[] getSoapMessageBytes() {
            return soapMessage;
        }

        /**
         * get the soap message offset from the started stream
         */
        public int getSoapMessageOffset() {
            return soapMessageOffset;
        }

        /**
         * get the mime boundary
         */
        public String getBoundary() {
            return boundary;
        }
    }
}