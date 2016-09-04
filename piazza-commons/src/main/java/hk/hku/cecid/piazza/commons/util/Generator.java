/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;


/**
 * Generator is a convenient class for generating various standard objects. For
 * instance, a message ID which conforms to RFC2822.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class Generator {

    private static int messageIdCounter = 0;

    private static Object messageIdCounterLock = new Object();
    
    /**
     * Creates a new instance of Generator.
     */
    private Generator() {
    }
    
    /**
     * Generates a message ID which conforms to 
     * <a href="http://www.ietf.org/rfc/rfc2822.txt">RFC2822</a>
     * 
     * @return the message ID.
     */
    public static String generateMessageID() {
        // this message id conforms to MessageId [RFC2822]
        DataFormatter messageIdFormatter = DataFormatter.getInstance();
        Date timestamp = new Date();
        int localCounter;
        synchronized (messageIdCounterLock) {
            messageIdCounter %= 100;
            localCounter = messageIdCounter;
            messageIdCounter++;
        }

        final StringBuffer messageId = new StringBuffer();
        messageId.append(messageIdFormatter.formatDate(timestamp,
                "yyyyMMdd-HHmmss-SSS"));
        messageId.append(StringUtilities.addLeadingZero(String
                .valueOf(localCounter), 2));

        String domain;
        try {
            InetAddress localAddr = InetAddress.getLocalHost();
            domain = localAddr.getHostAddress();
        } catch (Exception e) {
            domain = "unknown-domain";
        }

        messageId.append("@").append(domain);

        return messageId.toString();
    }
    
    /**
     * Generates a UUID which conforms to 
     * <a href="http://www.iso.ch/cate/d2229.html">ISO/IEC 11578:1996</a>
     * 
     * @return the UUID.
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
