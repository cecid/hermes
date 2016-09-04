/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.pkg;


/**
 * AS2Header represents a set of AS2 message headers.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class AS2Header {
    
    public static final String MESSAGE_ID = "Message-Id";

    public static final String AS2_VERSION = "AS2-Version";
    
    public static final String AS2_TO = "AS2-To";

    public static final String AS2_FROM = "AS2-From";
    
    public static final String FROM = "From";
    
    public static final String DATE = "Date";
    
    public static final String SUBJECT = "Subject";

    public static final String RECIPIENT_ADDRESS = "Recipient-Address";
    
    public static final String DISPOSITION_NOTIFICATION_TO = "Disposition-Notification-To";

    public static final String DISPOSITION_NOTIFICATION_OPTIONS = "Disposition-Notification-Options";

    public static final String RECEIPT_DELIVERY_OPTION = "Receipt-delivery-option";

    public static final String CONTENT_TYPE_MULTIPART_REPORT = "multipart/report";

    public static final String CONTENT_TYPE_MULTIPART_SIGNED = "multipart/signed";

    public static final String CONTENT_TYPE_MESSAGE_DISPOSITION_NOTIFICATION = "message/disposition-notification";

    public static final String CONTENT_TYPE_APPLICATION_PKCS7_SIGNATURE = "application/PKCS7-signature";

    public static final String CONTENT_TYPE_APPLICATION_PKCS7_MIME = "application/PKCS7-mime";
}