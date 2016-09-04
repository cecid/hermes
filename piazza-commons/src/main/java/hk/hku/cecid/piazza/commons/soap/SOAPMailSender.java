/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.soap;

import hk.hku.cecid.piazza.commons.activation.ByteArrayDataSource;
import hk.hku.cecid.piazza.commons.activation.InputStreamDataSource;
import hk.hku.cecid.piazza.commons.net.ConnectionException;
import hk.hku.cecid.piazza.commons.net.MailSender;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;


/**
 * SOAPMailSender is a mail sender responsible for sending SOAP message. 
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class SOAPMailSender extends MailSender {
    
    /**
     * Creates a new instance of SOAPMailSender. 
     * 
     * @param host the mail host.
     */
    public SOAPMailSender(String host) {
        super(host);
    }
    
    /**
     * Creates a new instance of SOAPMailSender. 
     * 
     * @param protocol the mail protocol.
     * @param host the mail host.
     */
    public SOAPMailSender(String protocol, String host) {
        super(protocol, host);
    }
    
    /**
     * Creates a new instance of SOAPMailSender. 
     * 
     * @param host the mail host.
     * @param username the user name for authentication.
     * @param password the password for authentication.
     */
    public SOAPMailSender(String host, String username, String password) {
        super(host, username, password);
    }
    
    /**
     * Creates a new instance of SOAPMailSender. 
     * 
     * @param protocol the mail protocol.
     * @param host the mail host.
     * @param username the user name for authentication.
     * @param password the password for authentication.
     */
    public SOAPMailSender(String protocol, String host, String username,
            String password) {
        super(protocol, host, username, password);
    }

    /**
     * Creates a MIME message from a SOAP message.
     * 
     * @param from the 'from' mail address.
     * @param to the 'to' mail address(es).
     * @param cc the 'cc' mail address(es).
     * @param subject the mail subject.
     * @param soapMessage the SOAP message.
     * @return a new MIME message.
     * @throws ConnectionException if error occurred in constructing the mail
     *             message.
     * @see hk.hku.cecid.piazza.commons.net.MailSender#createMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.mail.Session)
     */
    public MimeMessage createMessage(String from, String to, String cc,
            String subject, SOAPMessage soapMessage) throws ConnectionException {
        return createMessage(from, to, cc, subject, soapMessage, createSession());
    }

    /**
     * Creates a MIME message from a SOAP message.
     * 
     * @param from the 'from' mail address.
     * @param to the 'to' mail address(es).
     * @param cc the 'cc' mail address(es).
     * @param subject the mail subject.
     * @param soapMessage the SOAP message.
     * @param session the mail session.
     * @return a new MIME message.
     * @throws ConnectionException if error occurred in constructing the mail
     *             message.
     * @see hk.hku.cecid.piazza.commons.net.MailSender#createMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.mail.Session)
     */
    public MimeMessage createMessage(String from, String to, String cc,
            String subject, SOAPMessage soapMessage, Session session) throws ConnectionException {
        try {
            MimeMessage message = super.createMessage(from, to, cc, subject, session);
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            soapMessage.writeTo(bos);
            String contentType = getContentType(soapMessage);
            
            ByteArrayDataSource content = new ByteArrayDataSource(bos.toByteArray(), contentType);
            soapMessage.getAttachments();
            boolean hasAttachments = soapMessage.countAttachments() > 0;
            if (hasAttachments) {
                putHeaders(soapMessage.getMimeHeaders(), message);
                MimeMultipart mmp = new MimeMultipart(content);
                for (int i=0; i<mmp.getCount(); i++) {
                    BodyPart bp = mmp.getBodyPart(i);

                    // encoding all parts in base64 to overcome length of character line limitation in SMTP
                    InputStreamDataSource isds = new InputStreamDataSource(
                    	bp.getInputStream(), bp.getContentType(), bp.getFileName());
                    bp.setDataHandler(new DataHandler(isds));
                    bp.setHeader("Content-Transfer-Encoding", "base64");              
                }
                message.setContent(mmp);
            }
            else {
                DataHandler dh = new DataHandler(content);
                message.setDataHandler(dh);
                message.setHeader("Content-Transfer-Encoding", "base64");
            }

            message.saveChanges();
            return message;
        }
        catch (Exception e) {
            throw new ConnectionException("Unable to construct mail message from SOAP message", e);
        }
    }

    /**
     * Sends a SOAP message.
     * 
     * @param from the 'from' mail address.
     * @param to the 'to' mail address(es).
     * @param cc the 'cc' mail address(es).
     * @param subject the mail subject.
     * @param soapMessage the SOAP message.
     * @throws ConnectionException if unable to construct the mail message or to
     *             send out the message.
     * @see hk.hku.cecid.piazza.commons.net.MailSender#send(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void send(String from, String to, String cc, String subject,
            SOAPMessage soapMessage) throws ConnectionException {
        MimeMessage message = createMessage(from, to, cc, subject, soapMessage);
        send(message);
    }
    
    /**
     * Gets the content type from a SOAP message.
     * 
     * @param message the SOAP message.
     * @return the content type or "text/xml; charset=UTF-8" if there is none.
     */
    private String getContentType(SOAPMessage message) {
        String[] contentTypes = message.getMimeHeaders().getHeader("Content-Type");
        if (contentTypes != null && contentTypes.length>0 && contentTypes[0]!=null) {
            return contentTypes[0];
        }
        else {
            return "text/xml; charset=UTF-8";
        }
    }
    
    /**
     * Puts the mime headers into a mime message.
     * 
     * @param mimeHeaders the mime headers.
     * @param message the mime message.
     * @throws MessagingException if error occurred when adding the headers.
     */
    private void putHeaders(MimeHeaders mimeHeaders,
            MimeMessage message) throws MessagingException {
        for (Iterator iterator = mimeHeaders.getAllHeaders(); iterator
                .hasNext();) {
            MimeHeader mimeHeader = (MimeHeader) iterator.next();
            String[] mimeValues = mimeHeaders.getHeader(mimeHeader.getName());
            for (int i = 0; i < mimeValues.length;) {
                String value = mimeValues[i++];
                message.addHeader(mimeHeader.getName(), value);
            }
        }
    }
}