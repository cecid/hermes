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
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/MessageHeader.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
/**
 * An ebXML <code>MessageHeader</code> in the SOAP Header of a
 * <code>HeaderContainer</code>
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class MessageHeader extends HeaderElement {

    /** 
     * A <code>PartyId</code> inside <code>From</code> or <code>To</code> 
     */
    public static final class PartyId {
        /** Party ID */
        private final String id;

        /** PartyID type attribute */
        private final String type;

        /** Initializes <code>PartyId</code> object */
        PartyId(String id, String type) {
            this.id = id;
            this.type = type;
        }

        /** 
         * Gets party ID.
         *
         * @return Party ID stored in this object.
         */
        public String getId() {
            return id;
        }

        /**
         * Gets value of the "type" attribute .
         *
         * @return Value of the "type" attribute.
         */
        public String getType() {
            return type;
        }
    }

    /** 
     * A class representing service element in the <code>MessageHeader</code>
     */
    public static final class Service {
        /** Service name */
        private final String service;

        /** Service type */
        private String type;

        /** Initializes service object from given service name and type */
        Service(String service, String type) {
            this.service = service;
            this.type = type;
        }

        /** 
         * Gets service name.
         *
         * @return Service name.
         */
        public String getService() {
            return service;
        }

        /** 
         * Gets service type.
         * 
         * @return Service type.
         */
        public String getType() {
            return type;
        }

        /** 
         * Sets service type.
         * 
         * @param type      Service type string.
         */
        void setType(String type) {
            this.type = type;
        }
    }

    /** <code>MessageHeader</code> element name */
    static final String MESSAGE_HEADER = "MessageHeader";

    /** <code>MessageHeader</code> From element name */
    static final String ELEMENT_FROM = "From";

    /** <code>MessageHeader</code> PartyID element name */
    static final String ELEMENT_PARTY_ID = "PartyId";

    /** <code>MessageHeader</code> To element name */
    static final String ELEMENT_TO = "To";

    /** <code>MessageHeader</code> Role element name */
    static final String ELEMENT_ROLE = "Role";

    /** <code>MessageHeader</code> CPAID element name */
    static final String ELEMENT_CPA_ID = "CPAId";

    /** <code>MessageHeader</code> ConversationID element name */
    static final String ELEMENT_CONVERSATION_ID = "ConversationId";

    /** <code>MessageHeader</code> Service element name */
    static final String ELEMENT_SERVICE = "Service";

    /** <code>MessageHeader</code> Action element name */
    static final String ELEMENT_ACTION = "Action";

    /** <code>MessageHeader</code> MessageData element name */
    static final String ELEMENT_MESSAGE_DATA = "MessageData";

    /** <code>MessageHeader</code> MessageId element name */
    static final String ELEMENT_MESSAGE_ID = "MessageId";

    /** <code>MessageHeader</code> Timestamp element name */
    static final String ELEMENT_TIMESTAMP = "Timestamp";

    /** <code>MessageHeader</code> RefToMessageID element name */
    static final String ELEMENT_REF_TO_MESSAGE_ID = "RefToMessageId";

    /** <code>MessageHeader</code> TimeToLive element name */
    static final String ELEMENT_TIME_TO_LIVE = "TimeToLive";

    /** <code>MessageHeader</code> DuplicateElimination element name */
    static final String ELEMENT_DUPLICATE_ELIMINATION = "DuplicateElimination";

    /** <code>MessageHeader</code> Type attribute name */
    static final String ATTRIBUTE_TYPE = "type";

    /** Standard time zone used in Message Service Handler */
    static final String TIME_ZONE = "GMT";

    /** 
     * From core extension element in <code>MessageHeader</code> 
     * [ebMSS 3.1.1].
     */
    private final ExtensionElement from;

    /**
     * Party ID in From core extension element. There can be one or more
     * occurrences.
     */
    private final ArrayList fromPartyIds;

    /** 
     * Role element in From core extension element. There can be zero or one
     * occurrences.
     */
    private String fromRole;

    /** 
     * To core extension element in <code>MessageHeader</code> 
     * [ebMSS 3.1.1].
     */
    private final ExtensionElement to;

    /**
     * Party ID in To core extension element. There can be one or more
     * occurrences.
     */
    private final ArrayList toPartyIds;

    /** 
     * Role element in To core extension element. There can be zero or one
     * occurrences.
     */
    private String toRole;

    /**
     * CPAId core extension element in <code>MessageHeader</code>
     * [ebMSS 3.1.2].
     */
    private String cpaId;

    /**
     * ConversationID core extension element in <code>MessageHeader</code>
     * [ebMSS 3.1.3].
     */
    private String conversationId;

    /**
     * Service core extension element in <code>MessageHeader</code>
     * [ebMSS 3.1.4].
     */
    private Service service;

    /**
     * Service core extension element in <code>MessageHeader</code>
     * [ebMSS 3.1.1] in the form of <code>ExtensionElement</code>.
     */
    private ExtensionElement serviceElement;

    /**
     * Action core extension element in <code>MessageHeader</code>
     * [ebMSS 3.1.5].
     */
    private String action;

    /**
     * MessageData core extension element in <code>MessageHeader</code>
     * [ebMSS 3.1.6].
     */
    private ExtensionElement messageData;

    /**
     * Message ID in MessageData core extension element. 
     * There can be one or more occurrences.
     */
    private String messageId;

    /**
     * Timestamp in MessageData core extension element. 
     * There can be one or more occurrences.
     */
    private String timestamp;

    /**
     * RefToMessageId in MessageData core extension element. 
     * There can be one or more occurrences.
     */
    private String refToMessageId;

    /**
     * TimeToLive in MessageData core extension element. 
     * There can be one or more occurrences.
     */
    private String timeToLive;

    /**
     * DuplicateEliminiation core extension element in 
     * <code>MessageHeader</code> [ebMSS 3.1.7].
     */
    private boolean duplicateElimination;

    /**
     * Description core extension element in <code>MessageHeader</code>
     * [ebMSS 3.1.4]. It can have zero or more occurrences.
     */
    private final ArrayList descriptions;

    /** 
     * Initializes data structures in <code>MessageHeader</code> object 
     * using the given <code>SOAPEnvelope</code>.
     * 
     * @param soapEnvelope      <code>SOAPEnvelope</code> into which the 
     *                          <code>MessageHeader</code> is added.
     * @throws SOAPException 
     */
    MessageHeader(SOAPEnvelope soapEnvelope) throws SOAPException {
        super(soapEnvelope, MESSAGE_HEADER);

        this.from = addChildElement(ELEMENT_FROM);
        this.fromPartyIds = new ArrayList();
        this.fromRole = null;

        this.to = addChildElement(ELEMENT_TO);
        this.toPartyIds = new ArrayList();
        this.toRole = null;

        this.service = null;
        this.serviceElement = null;
        this.action = null;

        this.messageData = null;
        this.messageId = null;
        this.timestamp = null;
        this.refToMessageId = null;
        this.timeToLive = null;
        this.duplicateElimination = false;

        descriptions = new ArrayList();
    }

    /** 
     * Constructs a <code>MessageHeader</code> with the given mandatory fields.
     * No further modification on the fields specified are allowed.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code> into which the 
     *                          <code>MessageHeader</code> is added.
     * @param fromPartyId       PartyID of the sender.
     * @param fromPartyIdType   PartyID type of the sender.
     * @param toPartyId         PartyID of the recipient.
     * @param toPartyIdType     PartyID type of the recipient.
     * @param cpaId             CPA ID.
     * @param conversationId    Conversation ID of the message.
     * @param service           Service name.
     * @param action            Action name.
     * @param messageId         Unique message identifier.
     * @param timestamp         Timestamp of the message header creation.
     * @throws SOAPException 
    */
    MessageHeader(SOAPEnvelope soapEnvelope, String fromPartyId,
                  String fromPartyIdType, String toPartyId,
                  String toPartyIdType, String cpaId, String conversationId,
                  String service, String action, String messageId,
                  String timestamp)
        throws SOAPException {
        super(soapEnvelope, MESSAGE_HEADER);

        this.from = addChildElement(ELEMENT_FROM);
        this.fromPartyIds = new ArrayList();
        addFromPartyId(fromPartyId, fromPartyIdType);
        this.fromRole = null;

        this.to = addChildElement(ELEMENT_TO);
        this.toPartyIds = new ArrayList();
        addToPartyId(toPartyId, toPartyIdType);
        this.toRole = null;

        addChildElement(ELEMENT_CPA_ID, cpaId);
        this.cpaId = cpaId;
        addChildElement(ELEMENT_CONVERSATION_ID, conversationId);
        this.conversationId = conversationId;
        this.serviceElement = addChildElement(ELEMENT_SERVICE, service);
        this.service = new Service(service, null);
        addChildElement(ELEMENT_ACTION, action);
        this.action = action;

        this.messageData = addChildElement(ELEMENT_MESSAGE_DATA);
        messageData.addChildElement(ELEMENT_MESSAGE_ID, messageId);
        this.messageId = messageId;
        messageData.addChildElement(ELEMENT_TIMESTAMP, timestamp);
        this.timestamp = timestamp;
        this.refToMessageId = null;
        this.timeToLive = null;
        this.duplicateElimination = false;

        descriptions = new ArrayList();
    }

    /** 
     * Constructs a <code>MessageHeader</code> with the given 
     * <code>SOAPEnvelope</code> and <code>SOAPElement</code>.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code> into which the 
     *                          <code>MessageHeader</code> is added.
     * @param soapElement       <code>SOAPElement</code> to be parsed and stored
     *                          in the MessageHeader.
     * @throws SOAPException 
    */
    MessageHeader(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
        this.fromPartyIds = new ArrayList();
        Iterator childElements = getChildElements(ELEMENT_FROM);
        if (childElements.hasNext()) {
            this.from = new ExtensionElementImpl(soapEnvelope, (SOAPElement)
                                                 childElements.next());
            Iterator partyIds = from.getChildElements(ELEMENT_PARTY_ID);
            if (partyIds.hasNext()) {
                while (partyIds.hasNext()) {
                    ExtensionElement partyId =
                        new ExtensionElementImpl(soapEnvelope, (SOAPElement)
                                                 partyIds.next());
                    fromPartyIds.add(new PartyId(partyId.getValue(),
                        partyId.getAttributeValue(ATTRIBUTE_TYPE)));
                }
            }
            else {
                throw new SOAPValidationException
                    (SOAPValidationException.SOAP_FAULT_CLIENT,
                     "Missing <" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_PARTY_ID 
                     + "> in <" + NAMESPACE_PREFIX_EB + ":" 
                     + ELEMENT_FROM + ">!");
            }
            Iterator roles = from.getChildElements(ELEMENT_ROLE);
            if (roles.hasNext()) {
                this.fromRole = ((SOAPElement) roles.next()).getValue();
            }
            else {
                this.fromRole = null;
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_FROM 
                 + "> in <" + NAMESPACE_PREFIX_EB + ":" 
                 + MESSAGE_HEADER + ">!");
        }

        this.toPartyIds = new ArrayList();
        childElements = getChildElements(ELEMENT_TO);
        if (childElements.hasNext()) {
            this.to = new ExtensionElementImpl(soapEnvelope, (SOAPElement)
                                               childElements.next());
            Iterator partyIds = to.getChildElements(ELEMENT_PARTY_ID);
            if (partyIds.hasNext()) {
                while (partyIds.hasNext()) {
                    ExtensionElement partyId =
                        new ExtensionElementImpl(soapEnvelope, (SOAPElement)
                                                 partyIds.next());
                    toPartyIds.add(new PartyId(partyId.getValue(),
                        partyId.getAttributeValue(ATTRIBUTE_TYPE)));
                }
            }
            else {
                throw new SOAPValidationException
                    (SOAPValidationException.SOAP_FAULT_CLIENT,
                     "Missing <" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_PARTY_ID 
                     + "> in <" + NAMESPACE_PREFIX_EB 
                     + ":" + ELEMENT_TO + ">!");
            }
            Iterator roles = to.getChildElements(ELEMENT_ROLE);
            if (roles.hasNext()) {
                this.toRole = ((SOAPElement) roles.next()).getValue();
            }
            else {
                this.toRole = null;
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_TO 
                 + "> in <" + NAMESPACE_PREFIX_EB + ":" 
                 + MESSAGE_HEADER + ">!");
        }

        childElements = getChildElements(ELEMENT_CPA_ID);
        if (childElements.hasNext()) {
            this.cpaId = ((SOAPElement) childElements.next()).getValue();
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_CPA_ID 
                 + "> in <" + NAMESPACE_PREFIX_EB + ":" 
                 + MESSAGE_HEADER + ">!");
        }

        childElements = getChildElements(ELEMENT_CONVERSATION_ID);
        if (childElements.hasNext()) {
            this.conversationId = ((SOAPElement) childElements.next()).
                getValue();
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":" 
                 + ELEMENT_CONVERSATION_ID + "> in <" + NAMESPACE_PREFIX_EB 
                 + ":" + MESSAGE_HEADER + ">!");
        }

        childElements = getChildElements(ELEMENT_SERVICE);
        if (childElements.hasNext()) {
            serviceElement = new ExtensionElementImpl(soapEnvelope,
                (SOAPElement) childElements.next());
            this.service = new Service(serviceElement.getValue(),
                 serviceElement.getAttributeValue(ATTRIBUTE_TYPE));

        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_SERVICE 
                 + "> in <" + NAMESPACE_PREFIX_EB + ":" 
                 + MESSAGE_HEADER + ">!");
        }

        childElements = getChildElements(ELEMENT_ACTION);
        if (childElements.hasNext()) {
            this.action = ((SOAPElement) childElements.next()).getValue();
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_ACTION 
                 + "> in <" + NAMESPACE_PREFIX_EB + ":" 
                 + MESSAGE_HEADER + ">!");
        }

        childElements = getChildElements(ELEMENT_MESSAGE_DATA);
        if (childElements.hasNext()) {
            this.messageData = new ExtensionElementImpl(soapEnvelope,
                (SOAPElement) childElements.next());
            Iterator child = messageData.getChildElements(ELEMENT_MESSAGE_ID);
            if (child.hasNext()) {
                this.messageId = ((SOAPElement) child.next()).getValue();
            }
            else {
                throw new SOAPValidationException
                    (SOAPValidationException.SOAP_FAULT_CLIENT,
                     "Missing <" + NAMESPACE_PREFIX_EB + ":" 
                     + ELEMENT_MESSAGE_ID + "> in <" + NAMESPACE_PREFIX_EB 
                     + ":" + ELEMENT_MESSAGE_DATA + ">!");
            }

            child = messageData.getChildElements(ELEMENT_TIMESTAMP);
            if (child.hasNext()) {
                this.timestamp = ((SOAPElement) child.next()).getValue();
            }
            else {
                throw new SOAPValidationException
                    (SOAPValidationException.SOAP_FAULT_CLIENT,
                     "Missing <" + NAMESPACE_PREFIX_EB + ":" 
                     + ELEMENT_TIMESTAMP + "> in <" + NAMESPACE_PREFIX_EB + ":" 
                     + ELEMENT_MESSAGE_DATA + ">!");
            }

            child = messageData.getChildElements(ELEMENT_REF_TO_MESSAGE_ID);
            if (child.hasNext()) {
                this.refToMessageId = ((SOAPElement) child.next()).getValue();
            }
            else {
                this.refToMessageId = null;
            }

            child = messageData.getChildElements(ELEMENT_TIME_TO_LIVE);
            if (child.hasNext()) {
                this.timeToLive = ((SOAPElement) child.next()).getValue();
            }
            else {
                this.timeToLive = null;
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "Missing <" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_MESSAGE_DATA 
                 + "> in <" + NAMESPACE_PREFIX_EB + ":" 
                 + MESSAGE_HEADER + ">!");
        }

        childElements = getChildElements(ELEMENT_DUPLICATE_ELIMINATION);
        if (childElements.hasNext()) {
            this.duplicateElimination = true;
        }
        else {
            duplicateElimination = false;
        }

        descriptions = new ArrayList();
        childElements = getChildElements(Description.DESCRIPTION);
        while (childElements.hasNext()) {
            final SOAPElement child = (SOAPElement) childElements.next();
            final String text = child.getValue();
            final Name name = soapEnvelope.createName(ATTRIBUTE_LANG,
                                                      NAMESPACE_PREFIX_XML,
                                                      NAMESPACE_URI_XML);
            final String lang = child.getAttributeValue(name);
            descriptions.add(new Description(text, lang));
        }
    }

    /** 
     * Add sender's PartyID into <code>MessageHeader</code>
     * 
     * @param id    Sender's PartyID string.
     * @throws SOAPException 
     */
    public void addFromPartyId(String id)
        throws SOAPException {
        addFromPartyId(id, null);
    }

    /** 
     * Add sender's PartyID and its type into <code>MessageHeader</code>
     * 
     * @param id    Sender's PartyID string.
     * @param type  PartyID type.
     * @throws SOAPException 
     */
    public void addFromPartyId(String id, String type)
        throws SOAPException {
        if (fromPartyIds.size() > 0 && fromRole != null) {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_ROLE + "> and <"
                 + NAMESPACE_PREFIX_EB + ":" + ELEMENT_PARTY_ID + "> have "
                 + "already been set in <" + NAMESPACE_PREFIX_EB + ":"
                 + ELEMENT_FROM + ">!");
        }

        final ExtensionElement fromPartyId = from.
            addChildElement(ELEMENT_PARTY_ID, id);
        if (type != null) {
            fromPartyId.addAttribute(ATTRIBUTE_TYPE, type);
        }
        fromPartyIds.add(new PartyId(id, type));

        if (fromRole != null) {
            from.addChildElement(ELEMENT_ROLE, fromRole);
        }
    }

    /** 
     * Get From core extension element.
     * 
     * @return From extension element object.
     */
    ExtensionElement getFrom() {
        return from;
    }

    /** 
     * Get Sender's PartyId in header. It can have one or more occurrences.
     *
     * @return Iterator pointing to a list of senders' Party IDs.
     */
    public Iterator getFromPartyIds() {
        return fromPartyIds.iterator();
    }

    /** 
     * Add recipient's PartyID into <code>MessageHeader</code>
     * 
     * @param id    Recipient's PartyID string.
     * @throws SOAPException 
     */
    public void addToPartyId(String id)
        throws SOAPException {
        addToPartyId(id, null);
    }

    /** 
     * Add recipient's PartyID and its type into <code>MessageHeader</code>
     * 
     * @param id    Recipient's PartyID string.
     * @param type  PartyID type.
     * @throws SOAPException 
     */
    public void addToPartyId(String id, String type)
        throws SOAPException {
        if (toPartyIds.size() > 0 && toRole != null) {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_ROLE + "> and <"
                 + NAMESPACE_PREFIX_EB + ":" + ELEMENT_PARTY_ID + "> have "
                 + "already been set in <" + NAMESPACE_PREFIX_EB + ":"
                 + ELEMENT_TO + ">!");
        }

        final ExtensionElement toPartyId = to.
            addChildElement(ELEMENT_PARTY_ID, id);
        if (type != null) {
            toPartyId.addAttribute(ATTRIBUTE_TYPE, type);
        }
        toPartyIds.add(new PartyId(id, type));

        if (toRole != null) {
            to.addChildElement(ELEMENT_ROLE, toRole);
        }
    }

    /** 
     * Get To core extension element.
     * 
     * @return To extension element object.
     */
    ExtensionElement getTo() {
        return to;
    }

    /** 
     * Get recipient's PartyId in header. It can have one or more occurrences.
     *
     * @return Iterator pointing to a list of recipients' Party IDs.
     */
    public Iterator getToPartyIds() {
        return toPartyIds.iterator();
    }

    /** 
     * Set Role element in the From core extension element.
     * 
     * @param role              Role name.
     * @throws SOAPException 
     */
    public void setFromRole(String role) throws SOAPException {
        if (fromRole == null) {
            if (fromPartyIds.size() > 0) {
                from.addChildElement(ELEMENT_ROLE, role);
            }
            fromRole = role;
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_ROLE 
                 + "> has already been " + "set in <" + NAMESPACE_PREFIX_EB 
                 + ":" + ELEMENT_FROM + ">!");
        }
    }

    /** 
     * Get Role element in the From core extension element.
     * 
     * @return Role name.
     */
    public String getFromRole() {
        return fromRole;
    }

    /** 
     * Set Role element in the To core extension element.
     * 
     * @param role              Role name.
     * @throws SOAPException 
     */
    public void setToRole(String role) throws SOAPException {
        if (toRole == null) {
            if (toPartyIds.size() > 0) {
                to.addChildElement(ELEMENT_ROLE, role);
            }
            toRole = role;
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_ROLE 
                 + "> has already been " + "set in <" + NAMESPACE_PREFIX_EB + ":" 
                 + ELEMENT_TO + ">!");
        }
    }
    /** 
     * Get Role element in the To core extension element.
     * 
     * @return Role name.
     */
 
    public String getToRole() {
        return toRole;
    }

    /**
     * Set cpaId of the message header. This property can be set only once.
     * 
     * @param cpaId             Collaborative Protocol Agreement ID.
     * @throws SOAPException 
     */
    public void setCpaId(String cpaId) throws SOAPException {
        if (this.cpaId == null) {
            this.cpaId = cpaId;
            addChildElement(ELEMENT_CPA_ID, cpaId);
            if (conversationId != null) {
                addChildElement(ELEMENT_CONVERSATION_ID, conversationId);
                if (service != null) {
                    addChildElement(ELEMENT_SERVICE, service.getService());
                    if (action != null) {
                        addChildElement(ELEMENT_ACTION, action);
                        messageData = addChildElement(ELEMENT_MESSAGE_DATA);
                        if (messageId != null) {
                            messageData.addChildElement(ELEMENT_MESSAGE_ID,
                                                        messageId);
                            if (timestamp != null) {
                                messageData.addChildElement(ELEMENT_TIMESTAMP,
                                                            timestamp);
                                addRefToMessageId();
                            }
                        }
                    }
                }
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_CPA_ID 
                 + "> has already been set in <" + NAMESPACE_PREFIX_EB 
                 + ":" + MESSAGE_HEADER + ">!");
        }
    }

    /**
     * Get cpaId. 
     * 
     * @return Collaborative Protocol Agreement ID.
     */
    public String getCpaId() {
        return cpaId;
    }

    /**
     * Set conversationId. This property can be set only once.
     *
     * @param conversationId    Conversation ID of the message.
     * @throws SOAPException
     */
    public void setConversationId(String conversationId) 
        throws SOAPException {
        if (this.conversationId == null) {
            this.conversationId = conversationId;
            if (cpaId != null) {
                addChildElement(ELEMENT_CONVERSATION_ID, conversationId);
                if (service != null) {
                    addChildElement(ELEMENT_SERVICE, service.getService());
                    if (action != null) {
                        addChildElement(ELEMENT_ACTION, action);
                        messageData = addChildElement(ELEMENT_MESSAGE_DATA);
                        if (messageId != null) {
                            messageData.addChildElement(ELEMENT_MESSAGE_ID,
                                                        messageId);
                            if (timestamp != null) {
                                messageData.addChildElement(ELEMENT_TIMESTAMP,
                                                            timestamp);
                                addRefToMessageId();
                            }
                        }
                    }
                }
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT, 
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_CONVERSATION_ID 
                 + "> has already been set in <" + NAMESPACE_PREFIX_EB + ":" 
                 + MESSAGE_HEADER + ">!");
        }
    }

    /**
     * Get conversationId 
     * 
     * @return Conversation ID of the message.
     */
    public String getConversationId() {
        return conversationId;
    }

    /**
     * Set service name. This property can be set only once.
     *
     * @param serviceName       Service name.
     * @throws SOAPException
     */
    public void setService(String serviceName) throws SOAPException {
        setService(serviceName, null);
    }

    /**
     * Set service. This property can be set only once. 
     * 
     * @param serviceName       Service name.
     * @param serviceType       Service type string.
     * @throws SOAPException 
     */
    public void setService(String serviceName, String serviceType)
        throws SOAPException {
        if (service == null) {
            service = new Service(serviceName, serviceType);
            if (cpaId != null && conversationId != null) {
                serviceElement = addChildElement(ELEMENT_SERVICE, serviceName);
                if (serviceType != null) {
                    serviceElement.addAttribute(ATTRIBUTE_TYPE, serviceType);
                }
                if (action != null) {
                    addChildElement(ELEMENT_ACTION, action);
                    messageData = addChildElement(ELEMENT_MESSAGE_DATA);
                    if (messageId != null) {
                        messageData.addChildElement(ELEMENT_MESSAGE_ID,
                                                    messageId);
                        if (timestamp != null) {
                            messageData.addChildElement(ELEMENT_TIMESTAMP,
                                                        timestamp);
                            addRefToMessageId();
                        }
                    }
                }
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT, 
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_SERVICE 
                 + "> has already been set in <" + NAMESPACE_PREFIX_EB 
                 + ":" + MESSAGE_HEADER + ">!");
        }
    }

    /**
     * Get service name 
     * 
     * @return Service name.
     */
    public String getService() {
        if (service != null) {
            return service.getService();
        }
        return null;
    }

    /**
     * Set optional "type" attribute in service element 
     *
     * @throws SOAPException
     */
    public void setServiceType(String type) throws SOAPException {
        if (service != null) {
            serviceElement.addAttribute(ATTRIBUTE_TYPE, type);
            service.setType(type);
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_SERVICE 
                 + "> has not been set in <" + NAMESPACE_PREFIX_EB + ":" 
                 + MESSAGE_HEADER + ">!");
        }
    }

    /** 
     * Get the service type string.
     * 
     * @return Service type string.
     */
    public String getServiceType() {
        if (service != null) {
            return service.getType();
        }
        return null;
    }

    /**
     * Set action name. This property can be set only once. 
     *
     * @param action            Action name.
     * @throws SOAPException
     */
    public void setAction(String action) throws SOAPException {
        if (this.action == null) {
            this.action = action;
            if (cpaId != null && conversationId != null && service != null) {
                addChildElement(ELEMENT_ACTION, action);
                messageData = addChildElement(ELEMENT_MESSAGE_DATA);
                if (messageId != null) {
                    messageData.addChildElement(ELEMENT_MESSAGE_ID, messageId);
                    if (timestamp != null) {
                        messageData.addChildElement(ELEMENT_TIMESTAMP,
                                                    timestamp);
                        addRefToMessageId();
                    }
                }
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT, 
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_ACTION 
                 + "> has already been set in <" + NAMESPACE_PREFIX_EB 
                 + ":" + MESSAGE_HEADER + ">!");
        }
    }

    /**
     * Get action name.
     *
     * @return Action name.
     */
    public String getAction() {
        return action;
    }

    /** 
     * Get MessageData core extension element.
     * 
     * @return MessageData <code>ExtensionElement</code>
     */
    ExtensionElement getMessageData() {
        return messageData;
    }

    /**
     * Set messageId. This property can be set only once. 
     * 
     * @param messageId         Unique message identifier.
     * @throws SOAPException 
     */
    public void setMessageId(String messageId) throws SOAPException {
        if (this.messageId == null) {
            this.messageId = messageId;
            if (cpaId != null && conversationId != null && service != null &&
                action != null) {
                messageData.addChildElement(ELEMENT_MESSAGE_ID, messageId);
                if (timestamp != null) {
                    messageData.addChildElement(ELEMENT_TIMESTAMP, timestamp);
                    addRefToMessageId();
                }
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT, 
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_MESSAGE_ID 
                 + "> has already been set in <" + NAMESPACE_PREFIX_EB 
                 + ":" + MESSAGE_HEADER + ">!");
        }
    }

    /**
     * Get messageId.
     * 
     * @return Unique message identifier.
     */
    public String getMessageId() {
        return messageId;
    }

    /** 
     * Get the unique identifier of the message this message refers to.
     * 
     * @return ID of the message being referred to.
     */
    public String getRefToMessageId() {
        return refToMessageId;
    }

    /**
     * Set timestamp. 
     * 
     * @param timestamp         Header creation timestamp expressed in UTC
     *                          format.
     * @throws SOAPException 
     */
    public void setTimestamp(String timestamp) throws SOAPException {
        if (this.timestamp == null) {
            this.timestamp = timestamp;
            if (cpaId != null && conversationId != null && service != null &&
                action != null && messageId != null) {
                messageData.addChildElement(ELEMENT_TIMESTAMP, timestamp);
                addRefToMessageId();
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_TIMESTAMP 
                 + "> has already been set in <" + NAMESPACE_PREFIX_EB 
                 + ":" + MESSAGE_HEADER + ">!");
        }
    }

    /**
     * Get header creation timestamp expressed in UTC format.
     * 
     * @return Timestamp expressed in UTC format.
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Get TimeToLive 
     * 
     * @return TimeToLive expressed in UTC format.
     */
    public String getTimeToLive() {
        return timeToLive;
    }

    /**
     * Set optional "RefToMessageId" element. This property can be set only
     * once.
     * 
     * @param messageId         Message ID to be set in the current message
     *                          header.
     * @throws SOAPException 
     */
    public void setRefToMessageId(String messageId)
        throws SOAPException {
        if (refToMessageId == null) {
            refToMessageId = messageId;
            if (cpaId != null && conversationId != null && service != null &&
                action != null && this.messageId != null && timestamp != null) {
                if (timeToLive != null) {
                    throw new SOAPValidationException
                        (SOAPValidationException.SOAP_FAULT_CLIENT,
                         "<" + NAMESPACE_PREFIX_EB + ":" 
                         + ELEMENT_REF_TO_MESSAGE_ID + "> must be set before <" 
                         + NAMESPACE_PREFIX_EB + ":" + ELEMENT_TIME_TO_LIVE 
                         + "> is set!");
                }
                messageData.addChildElement(ELEMENT_REF_TO_MESSAGE_ID,
                                            messageId);
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_REF_TO_MESSAGE_ID 
                 + "> has " + "already been set in <" + NAMESPACE_PREFIX_EB 
                 + ":" + ELEMENT_MESSAGE_DATA + ">!");
        }
    }

    /**
     * Set optional "TimeToLive" element. This property can be set only once. 
     * 
     * @param time          TimeToLive expressed in UTC format.
     * @throws SOAPException 
     */
    public void setTimeToLive(String time)
        throws SOAPException {
        if (timeToLive == null) {
            timeToLive = time;
            if (cpaId != null && conversationId != null && service != null &&
                action != null && messageId != null && timestamp != null) {
                messageData.addChildElement(ELEMENT_TIME_TO_LIVE, time);
            }
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_TIME_TO_LIVE 
                 + "> has " + "already been set in <" + NAMESPACE_PREFIX_EB 
                 + ":" + ELEMENT_MESSAGE_DATA + ">!");
        }
    }

    /** 
     * Convert local date/time to UTC string and set TimeToLive value. This
     * property can be set only once.
     * 
     * @param time          Local date/time to be converted to UTC format and
     *                      set as TimeToLive value.
     *
     * @throws SOAPException
     */
    public void setTimeToLive(Date time)
        throws SOAPException {
        setTimeToLive(MessageHeader.toUTCString(time));
    }

    /** 
     * Enable duplication elimination function. This property can be set only
     * once.
     * 
     * @throws SOAPException 
     */
    public void setDuplicateElimination()
        throws SOAPException {
        if (duplicateElimination == false) {
            if (messageData != null) {
                if (descriptions.size() > 0) {
                    throw new SOAPValidationException
                        (SOAPValidationException.SOAP_FAULT_CLIENT,
                         "<" + NAMESPACE_PREFIX_EB + ":" 
                         + ELEMENT_DUPLICATE_ELIMINATION + "> must be set " 
                         + "before <" + NAMESPACE_PREFIX_EB + ":" 
                         + Description.DESCRIPTION + "> is set!");
                }
                addChildElement(ELEMENT_DUPLICATE_ELIMINATION);
            }
            duplicateElimination = true;
        }
        else {
            throw new SOAPValidationException
                (SOAPValidationException.SOAP_FAULT_CLIENT,
                 "<" + NAMESPACE_PREFIX_EB + ":" + ELEMENT_DUPLICATE_ELIMINATION 
                 + "> has " + "already been set to true in <" 
                 + NAMESPACE_PREFIX_EB + ":" + MESSAGE_HEADER + ">!");
        }
    }

    /** 
     * Get current setting of duplication elimination.
     * 
     * @return true if duplicate elimination is enabled; false otherwise.
     */
    public boolean getDuplicateElimination() {
        return duplicateElimination;
    }

    /**
     * Add <Description> element with default <code>xml:lang</code> 
     * 
     * @param description       Description to be added to
     *                          <code>MessageHeader</code>
     * @throws SOAPException 
     */
    public void addDescription(String description) throws SOAPException {
        addDescription(description, LANG_TYPE);
    }

    /**
     * Add <Description> element with specified <code>xml:lang</code>. This
     * method can be called repeatedly to add several descriptions to the
     * <code>MessageHeader</code>
     * 
     * @param description       Description to be added to 
     *                          <code>MessageHeader</code>
     * @param lang              Language of the description specified in 
     *                          <a href="http://www.ietf.org/rfc/rfc1766.txt">
     *                          RFC 1766</a> and ISO639.
     * @throws SOAPException 
     */
    public void addDescription(String description, String lang)
        throws SOAPException {
        if (description != null) {
            if (lang == null) {
                lang = LANG_TYPE;
            }
            if (messageData!=null && messageId!=null && timestamp!=null) {
                addChildElement(Description.DESCRIPTION, description).
                    addAttribute(ATTRIBUTE_LANG, NAMESPACE_PREFIX_XML,
                                 NAMESPACE_URI_XML, lang);
            }
            descriptions.add(new Description(description, lang));
        }
    }

    /**
     * Get all <code>Description</code> elements in this
     * <code>MessageHeader</code>.
     *
     * @return an <code>Iterator</code> of <code>Description</code>s'.
     */
    public Iterator getDescriptions() {
        return descriptions.iterator();
    }

    /**
     * Gets the number of <code>Description</code> elements in this <code>MessageHeader</code>.
     * 
     * @return The number of <code>Description</code> elements.
     */
    public int getDescriptionCount() {
        return descriptions.size();
    }

    /** 
     * Add RefToMessageId, TimeToLive, DuplicateElimination and 
     * Description to the <code>MessageData</code> core extension element.
     * 
     * @throws SOAPException 
     */
    private void addRefToMessageId() throws SOAPException {
        if (refToMessageId != null) {
            messageData.addChildElement(ELEMENT_REF_TO_MESSAGE_ID,
                                        refToMessageId);
        }
        if (timeToLive != null) {
            messageData.addChildElement(ELEMENT_TIME_TO_LIVE, timeToLive);
        }
        if (duplicateElimination &&
            !getChildElements(ELEMENT_DUPLICATE_ELIMINATION).hasNext()) {
            addChildElement(ELEMENT_DUPLICATE_ELIMINATION);
        }
        addAllDescriptions();
    }

    /** 
     * Add all descriptions to the <code>MessageData</code> core extension
     * element.
     * 
     * @throws SOAPException 
     */
    private void addAllDescriptions() throws SOAPException {
        for (int i=0 ; i<descriptions.size() ; i++) {
            final Description description = 
                (Description) descriptions.get(i);
            addChildElement(Description.DESCRIPTION,
                            description.getDescription()).
                addAttribute(ATTRIBUTE_LANG, NAMESPACE_PREFIX_XML,
                             NAMESPACE_URI_XML, description.getLang());
        }
    }

    /** 
     * Convert {@link java.util.Date} date/time to UTC date/time string.
     * 
     * @param date          {@link java.util.Date} date/time.
     * @return Date/time expressed in UTC format.
     */
    private static String toUTCString(Date date) {
        final Calendar c =
            Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
        c.setTime(date);
        String year = String.valueOf(c.get(Calendar.YEAR));
        for ( ; year.length()<4 ; year = "0" + year);
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        for ( ; month.length()<2 ; month = "0" + month);
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        for ( ; day.length()<2 ; day = "0" + day);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        for ( ; hour.length()<2 ; hour = "0" + hour);
        String minute = String.valueOf(c.get(Calendar.MINUTE));
        for ( ; minute.length()<2 ; minute = "0" + minute);
        String second = String.valueOf(c.get(Calendar.SECOND));
        for ( ; second.length()<2 ; second = "0" + second);
        String utcString = year + "-" + month + "-" + day + "T" + hour + ":"
            + minute + ":" + second + "Z";

        return utcString;
    }
}
