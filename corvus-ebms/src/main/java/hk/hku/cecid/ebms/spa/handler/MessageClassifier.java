/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.handler;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;


/**
 * @author Donahue Sze
 * 
 */
public class MessageClassifier {
    
    // message box
    public static String MESSAGE_BOX_INBOX = "inbox";

    public static String MESSAGE_BOX_OUTBOX = "outbox";
    
    // internal status    
    public static String INTERNAL_STATUS_RECEIVED         = "RC";
    
    public static String INTERNAL_STATUS_PENDING          = "PD";

    public static String INTERNAL_STATUS_PROCESSING       = "PR";

    public static String INTERNAL_STATUS_PROCESSED        = "PS";

    public static String INTERNAL_STATUS_PROCESSED_ERROR  = "PE";

    public static String INTERNAL_STATUS_DELIVERED        = "DL";

    public static String INTERNAL_STATUS_DELIVERY_FAILURE = "DF";
    
    // ebxml spec status
    public static String STATUS_UN_AUTHORIZED = "UnAuthorized";

    public static String STATUS_NOT_RECOGNIZED = "NotRecognized";

    public static String STATUS_RECEIVED = "Received";

    public static String STATUS_PROCESSED = "Processed";

    public static String STATUS_FORWARDED = "Forwarded";

    // message type
    public static String MESSAGE_TYPE_STATUS_REQUEST = "StatusRequest";
    
    public static String MESSAGE_TYPE_STATUS_RESPONSE = "StatusResponse";
    
    public static String MESSAGE_TYPE_PING = "Ping";
    
    public static String MESSAGE_TYPE_PONG = "Pong";
    
    public static String MESSAGE_TYPE_ERROR = "Error";
    
    public static String MESSAGE_TYPE_ACKNOWLEDGEMENT = "Acknowledgement";
    
    public static String MESSAGE_TYPE_ORDER = "Order";
    
    public static String MESSAGE_TYPE_PROCESSED_ERROR = "ProcessedError";
    
    // message service
    public static String SERVICE = "urn:oasis:names:tc:ebxml-msg:service";

    // message action
    public static String ACTION_ACKNOWLEDGMENT = "Acknowledgment";

    public static String ACTION_MESSAGE_ERROR = "MessageError";

    public static String ACTION_PING = "Ping";

    public static String ACTION_PONG = "Pong";
    
    public static String ACTION_STATUS_REQUEST = "StatusRequest";
    
    public static String ACTION_STATUS_RESPONSE = "StatusResponse";

    private boolean isSync = false;

    private boolean isDupElimination = false;

    private boolean isMessageOrder = false;

    private boolean isAckRequested = false;
    
    private boolean isSeqeunceStatusReset = false;
    
    private String messageType = MESSAGE_TYPE_ORDER;

    public MessageClassifier(EbxmlMessage ebxmlMessage) {
        classifyMessageType(ebxmlMessage);
    }

    private void classifyMessageType(EbxmlMessage ebxmlMessage) {

        String service = ebxmlMessage.getService();
        String action = ebxmlMessage.getAction();

        boolean isPing = service.equals(SERVICE) && action.equals(ACTION_PING);
        boolean isPong = service.equals(SERVICE) && action.equals(ACTION_PONG);
        /*
        boolean isError = service.equals(SERVICE)
                && action.equals(ACTION_MESSAGE_ERROR);
        boolean isAcknowledgment = service.equals(SERVICE)
                && action.equals(ACTION_ACKNOWLEDGMENT);
		*/
        
        if (ebxmlMessage.getStatusRequest() != null) {
            messageType = MESSAGE_TYPE_STATUS_REQUEST;
        }

        if (ebxmlMessage.getStatusResponse() != null) {
            messageType = MESSAGE_TYPE_STATUS_RESPONSE;
        }

        if (isPing) {
            messageType = MESSAGE_TYPE_PING;
        }

        if (isPong) {
            messageType = MESSAGE_TYPE_PONG;
        }

        if (ebxmlMessage.getErrorList() != null) {
            messageType = MESSAGE_TYPE_ERROR;
        }

        if (ebxmlMessage.getSyncReply()) {
            isSync = true;
        }

        if (ebxmlMessage.getMessageOrder() != null) {
            isMessageOrder = true;
            if (ebxmlMessage.getMessageOrder().getStatus() == 0){
                isSeqeunceStatusReset = true;
            }
        }

        if (ebxmlMessage.getDuplicateElimination()) {
            isDupElimination = true;
        }

        if (ebxmlMessage.getAckRequested() != null) {
            isAckRequested = true;
        }

        if (ebxmlMessage.getAcknowledgment() != null) {
            messageType = MESSAGE_TYPE_ACKNOWLEDGEMENT;
        }
    }

    /**
     * @return Returns the isAckRequested.
     */
    public boolean isAckRequested() {
        return isAckRequested;
    }

    /**
     * @return Returns the isDupElimination.
     */
    public boolean isDupElimination() {
        return isDupElimination;
    }

    /**
     * @return Returns the isMessageOrder.
     */
    public boolean isMessageOrder() {
        return isMessageOrder;
    }

    /**
     * @return Returns the isSync.
     */
    public boolean isSync() {
        return isSync;
    }

    /**
     * @return Returns the messageType.
     */
    public String getMessageType() {
        return messageType;
    }
        
    /**
     * @return Returns the isSeqeunceStatusReset.
     */
    public boolean isSeqeunceStatusReset() {
        return isSeqeunceStatusReset;
    }
}