/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.pkg;

import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.security.SMimeMessage;
import hk.hku.cecid.piazza.commons.util.Generator;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Date;
import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;

/**
 * AS2Message represents an AS2 message.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class AS2Message {

    private InternetHeaders headers;

    private MimeBodyPart bodyPart;

    /**
     * Creates a new instance of AS2Message.
     */
    public AS2Message() {
        this.headers = new InternetHeaders();
        this.bodyPart = new MimeBodyPart();
        setHeader(AS2Header.SUBJECT, "AS2 Message");
        setHeader(AS2Header.FROM, getDefaultEmailAddress());
        setHeader(AS2Header.AS2_VERSION, "1.1");
        setHeader(AS2Header.DATE, StringUtilities.toGMTString(new Date()));
        setMessageID(generateID());
    }

    /**
     * Creates a new instance of AS2Message.
     * 
     * @param message the message as input stream.
     * @throws AS2MessageException if unable to construct from the given input stream.
     */
    public AS2Message(InputStream message) throws AS2MessageException {
        try {
            BufferedInputStream bis= new BufferedInputStream(message);
            load(new InternetHeaders(bis), bis);
            bis.close();
        } catch (Exception e) {
            throw new AS2MessageException(
                    "Unable to construct AS2 message from input stream", e);
        }
    }

    /**
     * Creates a new instance of AS2Message.
     * 
     * @param headers the headers of this message.
     * @param content the content stream.
     * @throws AS2MessageException if unable to construct from the given content stream.
     */
    public AS2Message(InternetHeaders headers, InputStream content)
            throws AS2MessageException {
        try {
            load(headers, content);
        } catch (Exception e) {
            throw new AS2MessageException(
                    "Unable to construct AS2 message from content stream", e);
        }
    }
    
    /**
     * Loads the given headers and content to this message.
     * 
     * @param headers the message headers.
     * @param content the message content.
     * @throws MessagingException if unable to construct the mime body part.
     * @throws IOException unable to read the content stream.
     */
    private void load(InternetHeaders headers, InputStream content)
            throws MessagingException, IOException {
            InternetHeaders bodyHeaders = new InternetHeaders();
            copyHeaders(headers, bodyHeaders, "(?i)(?s)content-.*", true);

            this.headers = headers;
            this.bodyPart = new MimeBodyPart(bodyHeaders, IOHandler
                    .readBytes(content));
    }

    /**
     * Gets the default email address.
     * 
     * @return the default email address.
     */
    private String getDefaultEmailAddress() {
        return "as2@" + getLocalHost();
    }
    
    /**
     * Gets the textual representation of the local host.
     * 
     * @return the textual representation of the local host.
     */
    private String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (Exception e) {
            return "127.0.0.1";
        }
    }
    
    /**
     * Gets the message ID.
     * 
     * @return the message ID.
     */
    public String getMessageID() {
        return StringUtilities.trim(getHeader(AS2Header.MESSAGE_ID), "<", ">");
    }
    
    public void setMessageID(String id) {
        if (id != null) {
            setHeader(AS2Header.MESSAGE_ID, StringUtilities.wraps(id, "<", ">"));
        }
    }
    
    public void setFromPartyID(String id) {
        if (id != null) {
            if (id.indexOf(' ') != -1) {
                setHeader(AS2Header.AS2_FROM, StringUtilities.wraps(id, "\"", "\""));
            }
            else {
                setHeader(AS2Header.AS2_FROM, id);
            }
        }
    }

    public void setToPartyID(String id) {
        if (id != null) {
            if (id.indexOf(' ') != -1) {
                setHeader(AS2Header.AS2_TO, StringUtilities.wraps(id, "\"", "\""));
            }
            else {
                setHeader(AS2Header.AS2_TO, id);
            }
        }
    }

    /**
     * Gets the "from" party ID. 
     * 
     * @return the "from" party ID.
     */
    public String getFromPartyID() {
        return getPartyID(AS2Header.AS2_FROM);
    }
    
    /**
     * Gets the "to" party ID. 
     * 
     * @return the "to" party ID.
     */
    public String getToPartyID() {
        return getPartyID(AS2Header.AS2_TO);
    }
    
    /**
     * Gets the "from" party ID. 
     *
     * @param type the party type.
     * @return the party ID of the specified type.
     */
    private String getPartyID(String type) {
        return StringUtilities.trim(getHeader(type), "\"", "\"");
    }
    
    public void requestReceipt(String returnUrl, String micAlg) {
        setHeader(AS2Header.DISPOSITION_NOTIFICATION_TO, getDefaultEmailAddress());
        if (returnUrl != null) {
            setHeader(AS2Header.RECEIPT_DELIVERY_OPTION, returnUrl);
        }
        if (micAlg != null) {
            DispositionNotificationOptions dnos = new DispositionNotificationOptions();

            DispositionNotificationOption option = dnos.addOption(DispositionNotificationOptions.SIGNED_RECEIPT_PROTOCOL);
            option.addValue(DispositionNotificationOption.SIGNED_RECEIPT_PROTOCOL_PKCS7);
            
            option = dnos.addOption(DispositionNotificationOptions.SIGNED_RECEIPT_MICALG);
            option.addValue(micAlg);
            
            setHeader(AS2Header.DISPOSITION_NOTIFICATION_OPTIONS, dnos.toString());
        }
    }
    
    /**
     * Checks if receipt of message is requested.
     * 
     * @return true if receipt of message is requested.
     */
    public boolean isReceiptRequested() {
        return getHeader(AS2Header.DISPOSITION_NOTIFICATION_TO) != null;
    }

    /**
     * Checks if the receipt of message should be sent synchronously.
     * 
     * @return true if the receipt of message should be sent synchronously.
     */
    public boolean isReceiptSynchronous() {
        return getHeader(AS2Header.RECEIPT_DELIVERY_OPTION) == null;
    }
    
    /**
     * Gets the disposition notification options.
     * 
     * @return the disposition notification options.
     */
    public DispositionNotificationOptions getDispositionNotificationOptions() {
        String option = getHeader(AS2Header.DISPOSITION_NOTIFICATION_OPTIONS);
        if (option == null) {
            return null;
        }
        else {
            return new DispositionNotificationOptions(option);
        }
    }
    
    /**
     * Gets a message header of the specified name.
     * 
     * @param name the header name.
     * @return the header value.
     */
    public String getHeader(String name) {
        String[] hs = headers.getHeader(name);
        if (hs == null || hs.length < 1) {
            return null;
        } else {
            StringBuffer header = new StringBuffer();
            for (int i=0; i<hs.length; i++) {
                header.append(hs[i]);
                if (i+1<hs.length) {
                    header.append(", ");
                }
            }
            return header.toString();
        }
    }

    /**
     * Gets a message header of the specified name.
     * 
     * @param name the header name.
     * @param def the default value.
     * @return the header value.
     */
    public String getHeader(String name, String def) {
        String header = getHeader(name);
        return header == null? def : header;
    }

    /**
     * Sets a message header of the specified name.
     * 
     * @param name the header name.
     * @param value the header value.
     */
    public void setHeader(String name, String value) {
        if (name != null && value != null) {
            headers.setHeader(name, value);
        }
    }
    
    /**
     * Removes a message header of the specified name.
     * 
     * @param name the header name.
     */
    public void removeHeader(String name) {
        if (name != null) {
            headers.removeHeader(name);
        }
    }

    /**
     * Adds a message header of the specified name.
     * 
     * @param name the header name.
     * @param value the header value.
     */
    public void addHeader(String name, String value) {
        if (name != null && value != null) {
            headers.addHeader(name, value);
        }
    }

    /**
     * Gets the MIME body part of this message.
     * 
     * @return the MIME body part.
     */
    public MimeBodyPart getBodyPart() {
        return bodyPart;
    }

    /**
     * Sets the MIME body part of this message.
     *  
     */
    public void setBodyPart(MimeBodyPart bp) {
        if (bp != null) {
            bodyPart = bp;
        }
    }

    /**
     * Sets a content to this message.
     * 
     * @param content the content part.
     * @param contentType the content type.
     * @throws AS2MessageException if unable to set the content.
     */
    public void setContent(Object content, String contentType)
            throws AS2MessageException {
        try {
            bodyPart.setContent(content, contentType);
            bodyPart.setHeader("Content-Type", contentType);
            bodyPart.setHeader("Content-Transfer-Encoding", "binary");
        } catch (MessagingException e) {
            throw new AS2MessageException("Unable to set AS2 content", e);
        }
    }

    /**
     * Gets the content of this message.
     * 
     * @return the content part.
     * @throws AS2MessageException if unable to get the content.
     */
    public Object getContent() throws AS2MessageException {
        try {
            return bodyPart.getContent();
        } catch (Exception e) {
            throw new AS2MessageException("Unable to get AS2 content", e);
        }
    }

    /**
     * Gets the content type.
     * 
     * @return the content type.
     * @throws AS2MessageException if unable to get the content type.
     */
    public String getContentType() throws AS2MessageException {
        try {
            return bodyPart.getContentType();
        } catch (MessagingException e) {
            throw new AS2MessageException("Unable to get content type", e);
        }
    }

    /**
     * Gets the headers of this message.
     * 
     * @return a copy of the headers of this message.
     */
    public InternetHeaders getHeaders() {
        InternetHeaders h = new InternetHeaders();
        copyHeaders(headers, h, null, false);
        copyHeaders(bodyPart, h, null, false);
        return h;
    }

    /**
     * Copy the given headers to a specified internet headers object.
     * 
     * @param fromHeaders the headers source.
     * @param toHeaders the headers destination.
     * @param filter the filter in regular expression.
     */
    private void copyHeaders(Object fromHeaders, InternetHeaders toHeaders,
            String filter, boolean isMovingHeaders) {
        if (fromHeaders != null && toHeaders != null) {
            Enumeration enumeration;
            if (fromHeaders instanceof InternetHeaders) {
            	enumeration = ((InternetHeaders) fromHeaders).getAllHeaderLines();
            } else if (fromHeaders instanceof MimeBodyPart) {
                try {
                	enumeration = ((MimeBodyPart) fromHeaders).getAllHeaderLines();
                } catch (MessagingException e) {
                    return;
                }
            } else {
                return;
            }
            while (enumeration.hasMoreElements()) {
                String headerline = enumeration.nextElement().toString();
                if (filter == null || headerline.matches(filter)) {
                    toHeaders.addHeaderLine(headerline);
                    if (isMovingHeaders) {
                        String headerName = headerline.split(":")[0];
                        if (fromHeaders instanceof InternetHeaders) {
                            ((InternetHeaders) fromHeaders).removeHeader(headerName);
                        } else if (fromHeaders instanceof MimeBodyPart) {
                            try {
                                ((MimeBodyPart) fromHeaders).removeHeader(headerName);
                            } catch (MessagingException e) {}
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the content stream of this message.
     * 
     * @return the content stream of this message.
     * @throws AS2MessageException if unable to retrieve the stream.
     */
    public InputStream getContentStream() throws AS2MessageException {
        try {
            return bodyPart.getRawInputStream();
        } catch (MessagingException e) {
            try {
                // try getting the input stream if there is no raw stream
                // available.
                return bodyPart.getInputStream();
            } catch (Exception ex) {
                throw new AS2MessageException("Unable to get content stream", e);
            }
        }
    }
    
    /**
     *  Gets the input stream of this message's content. 
     *  Any transfer encodings will be decoded before the input stream is provided.
     * 
     * @return the input stream of this message's content.
     * @throws AS2MessageException if unable to retrieve the stream.
     */
    public InputStream getInputStream() throws AS2MessageException {
        try {
            return bodyPart.getInputStream();
        } catch (Exception e) {
            throw new AS2MessageException("Unable to get input stream of content", e);
        }
    }

    /**
     * Writes the message to the given output stream.
     * 
     * @param outs the output stream to be written.
     * @throws AS2MessageException if unable to write the message.
     */
    public void writeTo(OutputStream outs) throws AS2MessageException {
        try {
            Enumeration enumeration = headers.getAllHeaderLines();
            while (enumeration.hasMoreElements()) {
                outs.write((enumeration.nextElement() + "\r\n").getBytes());
            }
            bodyPart.writeTo(outs);
            outs.flush();
        } catch (Exception e) {
            throw new AS2MessageException("Unable to write message", e);
        }
    }

    /**
     * Checks if this message is an MDN.
     * 
     * @return true if this message is an MDN.
     */
    public boolean isDispositionNotification() {
        try {
            SMimeMessage smime = new SMimeMessage(bodyPart);
            if (smime.isSigned()) {
                smime = smime.unsign();
            }
            return smime.getBodyPart().getContentType().toLowerCase().startsWith(
                    AS2Header.CONTENT_TYPE_MULTIPART_REPORT.toLowerCase());
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the MDN of this message.
     * 
     * @return the MDN.
     * @throws AS2MessageException if unable to construct the MDN.
     */
    public DispositionNotification getDispositionNotification() throws AS2MessageException {
        return new DispositionNotification(this);
    }
    
    /**
     * Replies this message.
     * 
     * @return the reply message.
     * @throws AS2MessageException if unable to construct the message.
     */
    public AS2Message reply() throws AS2MessageException {
        try {
            AS2Message ackMessage = new AS2Message();
            ackMessage.setFromPartyID(getHeader(AS2Header.AS2_TO, "unknown"));
            ackMessage.setToPartyID(getHeader(AS2Header.AS2_FROM, "unknown"));
            ackMessage.setHeader(AS2Header.RECIPIENT_ADDRESS,
                    getHeader(AS2Header.FROM, "unknown@unknown"));
            ackMessage.setHeader(AS2Header.SUBJECT, "Message Disposition Notification");
            
            return ackMessage;
        }
        catch (Exception e) {
            throw new AS2MessageException(
                    "Error in contructing reply message", e);
        }
    }
    
    /**
     * Returns a byte array which represents this message.
     * 
     * @return a byte array which represents this message.
     * @throws AS2MessageException if unable to convert this message into bytes.
     */
    public byte[] toByteArray() throws AS2MessageException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeTo(baos);
        return baos.toByteArray();
    }
    
    /**
     * Returns a string representation of this message.
     * 
     * @return a string representation of this message.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String id = getMessageID();
        return "AS2 " + (isDispositionNotification()? "MDN":"Message") + " ["+ (id==null? "Unknown ID":id) +", From: "+getFromPartyID()+", To: "+getToPartyID()+"]";
    }
    
    /**
     * Generates a new AS2 message ID.
     * 
     * @return a new AS2 message ID.
     */
    public static String generateID() {
        return Generator.generateMessageID();
    }
}