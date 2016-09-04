/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.handler;

import hk.hku.cecid.ebms.spa.EbmsUtility;
import hk.hku.cecid.ebms.pkg.Acknowledgment;
import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.MessageHeader;
import hk.hku.cecid.ebms.pkg.Signature;
import hk.hku.cecid.ebms.pkg.SignatureReference;
import hk.hku.cecid.piazza.commons.util.DataFormatter;
import hk.hku.cecid.piazza.commons.util.Generator;

import java.util.Date;
import java.util.Iterator;

import javax.xml.soap.SOAPException;

/**
 * <code>SignalMessageGenerator</code> is an utility api for the user to
 * generate some signal message
 * 
 * @author pykoon
 * @version $Revision: 1.2 $
 */
public class SignalMessageGenerator {

    /**
     * Generate a simple response message containing required elements in
     * message header. They include:
     * <ul>
     * <li>From/PartyId [ebMSS 3.1.1]</li>
     * <li>To/PartyId [ebMSS 3.1.1]</li>
     * <li>CPAId [ebMSS 3.1.2].</li>
     * <li>ConversationId [ebMSS 3.1.3].</li>
     * <li>Service [ebMSS 3.1.4].</li>
     * <li>Action [ebMSS 3.1.5].</li>
     * <li>MessageId [ebMSS 3.1.6.1].</li>
     * <li>Timestamp [ebMSS 3.1.6.2].</li>
     * </ul>
     * 
     * @param requestMessage
     *            Request message for which a response message shall be
     *            generated.
     * @param action
     *            Name of the action.
     * 
     * @return An {@link EbxmlMessage}that contains the fields mentioned above.
     * 
     * @throws SOAPException
     */
    private static EbxmlMessage generateResponseMessage(
            EbxmlMessage requestMessage, String action) throws SOAPException {
        final EbxmlMessage responseMessage;
        responseMessage = new EbxmlMessage();
        final MessageHeader.PartyId fromParty = (MessageHeader.PartyId) requestMessage
                .getToPartyIds().next();
        final String fromPartyId = fromParty.getId();
        final String fromPartyIdType = fromParty.getType();
        final MessageHeader.PartyId toParty = (MessageHeader.PartyId) requestMessage
                .getFromPartyIds().next();
        final String toPartyId = toParty.getId();
        final String toPartyIdType = toParty.getType();
        final String timeStamp = EbmsUtility.getCurrentUTCDateTime();
        final String messageId = Generator.generateMessageID();
        responseMessage.addMessageHeader(fromPartyId, fromPartyIdType,
                toPartyId, toPartyIdType, requestMessage.getCpaId(),
                requestMessage.getConversationId(), MessageClassifier.SERVICE,
                action, messageId, timeStamp);
        return responseMessage;
    }

    private static EbxmlMessage generateResponseMessageBySender(
            EbxmlMessage requestMessage, String action) throws SOAPException {
        final EbxmlMessage responseMessage;
        responseMessage = new EbxmlMessage();
        final MessageHeader.PartyId fromParty = (MessageHeader.PartyId) requestMessage
                .getFromPartyIds().next();
        final String fromPartyId = fromParty.getId();
        final String fromPartyIdType = fromParty.getType();
        final MessageHeader.PartyId toParty = (MessageHeader.PartyId) requestMessage
                .getToPartyIds().next();
        final String toPartyId = toParty.getId();
        final String toPartyIdType = toParty.getType();
        final Date date = new Date();
        final String timeStamp = EbmsUtility.getCurrentUTCDateTime();
        final String messageId = Generator.generateMessageID();
        responseMessage.addMessageHeader(fromPartyId, fromPartyIdType,
                toPartyId, toPartyIdType, requestMessage.getCpaId(),
                requestMessage.getConversationId(), MessageClassifier.SERVICE,
                action, messageId, timeStamp);
        return responseMessage;
    }

    /**
     * Generates acknowledgement message from the given acknowledgement request
     * message and the refToMessageId. Note that the acknowledgment message is
     * not signed.
     * 
     * @param ackRequestedMessage
     *            Acknowledgement request message.
     * @param refToMessageId
     *            MessageId of the message to which the acknowledgement response
     *            should be referred.
     * 
     * @return Acknowledgement message.
     * 
     * @throws SOAPException
     */
    public static EbxmlMessage generateAcknowledgment(
            EbxmlMessage ackRequestedMessage, String refToMessageId)
            throws SOAPException {
        final EbxmlMessage ackMessage = generateResponseMessage(
                ackRequestedMessage, MessageClassifier.ACTION_ACKNOWLEDGMENT);

        final MessageHeader messageHeader = ackMessage.getMessageHeader();
        messageHeader.setRefToMessageId(refToMessageId);
        if (ackRequestedMessage.getDuplicateElimination()) {
            messageHeader.setDuplicateElimination();
        }
        Iterator toParties = ackRequestedMessage.getToPartyIds();
        if (toParties.hasNext()) {
            MessageHeader.PartyId party = (MessageHeader.PartyId) toParties
                    .next();
            ackMessage.addAcknowledgment(messageHeader.getTimestamp(),
                    ackRequestedMessage, party.getId(), party.getType());
        } else {
            /*
             * ackMessage.addAcknowledgment(messageHeader.getTimestamp(),
             * ackRequestedMessage, mshUrl);
             */
            throw new SOAPException(
                    "Missing To party Id on ack request message");
        }

        Iterator signatures = ackRequestedMessage.getSignatures();
        if (signatures.hasNext()) {
            Acknowledgment ack = ackMessage.getAcknowledgment();
            for (Iterator i = ((Signature) signatures.next()).getReferences(); i
                    .hasNext();) {
                ack.addSignatureReference((SignatureReference) i.next());
            }
            ackMessage.getSOAPMessage().getSOAPPart().getEnvelope()
                    .addNamespaceDeclaration(Signature.NAMESPACE_PREFIX_DS,
                            Signature.NAMESPACE_URI_DS);
        }
        return ackMessage;
    }

    /**
     * Generates acknowledgement message from the given acknowledgement request
     * message and the refToMessageId. Note that the acknowledgment message is
     * not signed.
     * 
     * @param ackRequestedMessage
     *            Acknowledgement request message.
     * 
     * @return Acknowledgement message.
     * 
     * @throws SOAPException
     */
    public static EbxmlMessage generateAcknowledgment(
            EbxmlMessage ackRequestedMessage) throws SOAPException {
        return generateAcknowledgment(ackRequestedMessage, ackRequestedMessage
                .getMessageId());
    }

    /**
     * Generates response message from the given status request message and the
     * status string [ebMSS 7.1.2].
     * 
     * @param statusRequestMessage
     *            Status request message.
     * @param status
     *            Current status of the message service handler.
     * 
     * @return Status response message.
     * 
     * @throws SOAPException
     */
    public static EbxmlMessage generateStatusResponseMessage(
            EbxmlMessage statusRequestMessage, String status, Date timestamp)
            throws SOAPException {
        final EbxmlMessage statusResponseMessage = generateResponseMessage(
                statusRequestMessage, MessageClassifier.ACTION_STATUS_RESPONSE);
        statusResponseMessage.getMessageHeader().setRefToMessageId(
                statusRequestMessage.getMessageId());
        final String refToMessageId = statusRequestMessage.getStatusRequest()
                .getRefToMessageId();
        if (status.equals(MessageClassifier.STATUS_UN_AUTHORIZED)
                || status.equals(MessageClassifier.STATUS_NOT_RECOGNIZED)) {
            statusResponseMessage.addStatusResponse(refToMessageId, status);
        } else {
            String utcTime = EbmsUtility.getCurrentUTCDateTime();
            statusResponseMessage.addStatusResponse(refToMessageId, status,
                    utcTime);
        }
        return statusResponseMessage;
    }

    /**
     * Generates an error message containing the specfied error code [ebMSS
     * 4.2.3.4.1].
     * 
     * @param ebxmlMessage
     *            ebXML message to which error list should be attached.
     * @param errorCode
     *            Error code of the message.
     * @param severity
     *            Error severity, either ERROR or WARNING.
     * @param description
     *            Human-readable description of the error message.
     * @param location
     *            Source of the error.
     * 
     * @return ebXML message containing error code.
     * 
     * @throws SOAPException
     */
    public static EbxmlMessage generateErrorMessage(EbxmlMessage ebxmlMessage,
            String errorCode, String severity, String description,
            String location) throws SOAPException {
        final EbxmlMessage errorMessage = generateResponseMessage(ebxmlMessage,
                MessageClassifier.ACTION_MESSAGE_ERROR);
        errorMessage.getMessageHeader().setRefToMessageId(
                ebxmlMessage.getMessageId());
        errorMessage.addErrorList(errorCode, severity, description, location);
        return errorMessage;
    }

    public static EbxmlMessage generateErrorMessageBySender(
            EbxmlMessage ebxmlMessage, String errorCode, String severity,
            String description, String location) throws SOAPException {
        final EbxmlMessage errorMessage = generateResponseMessageBySender(
                ebxmlMessage, MessageClassifier.ACTION_MESSAGE_ERROR);
        errorMessage.getMessageHeader().setRefToMessageId(
                ebxmlMessage.getMessageId());
        errorMessage.addErrorList(errorCode, severity, description, location);
        return errorMessage;
    }

    /**
     * Generates pong message from the given ping message [ebMSS 8.2].
     * 
     * @param pingMessage
     *            Incoming ping message.
     * 
     * @return Pong message in response of the incoming ping message.
     * 
     * @throws SOAPException
     */
    public static EbxmlMessage generatePongMessage(EbxmlMessage pingMessage)
            throws SOAPException {
        final EbxmlMessage pongMessage = generateResponseMessage(pingMessage,
                MessageClassifier.ACTION_PONG);
        pongMessage.getMessageHeader().setRefToMessageId(
                pingMessage.getMessageId());
        return pongMessage;
    }   
}