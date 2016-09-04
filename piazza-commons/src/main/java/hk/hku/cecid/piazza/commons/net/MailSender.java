/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.net;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * MailSender is a mail connector for making connections to outgoing mail servers.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class MailSender extends MailConnector {

    private static final String DEFAULT_PROTOCOL = "smtp"; 
    
    /**
     * Creates a new instance of MailSender. 
     * The default protocol is SMTP.
     * 
     * @param host the mail host.
     */
    public MailSender(String host) {
        super(DEFAULT_PROTOCOL, host);
    }

    /**
     * Creates a new instance of MailSender.
     * The default protocol is SMTP.
     * 
     * @param host the mail host.
     * @param username the user name for authentication.
     * @param password the password for authentication.
     */
    public MailSender(String host, String username, String password) {
        super(DEFAULT_PROTOCOL, host, username, password);
    }

    /**
     * Creates a new instance of MailSender.
     * 
     * @param protocol the mail protocol.
     * @param host the mail host.
     */
    public MailSender(String protocol, String host) {
        super(protocol, host);
    }

    /**
     * Creates a new instance of MailSender.
     * 
     * @param protocol the mail protocol.
     * @param host the mail host.
     * @param username the user name for authentication.
     * @param password the password for authentication.
     */
    public MailSender(String protocol, String host, String username, String password) {
        super(protocol, host, username, password);
    }

    /**
     * Creates a MIME message from the underlying mail properties.
     * 
     * @return a new MIME message.
     */
    public MimeMessage createMessage() {
        return new MimeMessage(createSession());
    }

    /**
     * Creates a MIME message from the given mail session.
     * 
     * @param session the mail session.
     * @return a new MIME message.
     */
    public MimeMessage createMessage(Session session) {
        return new MimeMessage(session);
    }

    /**
     * Creates a simple MIME message with some basic headers.
     * 
     * @param from the 'from' mail address.
     * @param to the 'to' mail address(es).
     * @param cc the 'cc' mail address(es).
     * @param subject the mail subject.
     * @return a new MIME message.
     * @throws ConnectionException if error occurred in constructing the mail
     *             message.
     */
    public MimeMessage createMessage(String from, String to, String cc,
            String subject) throws ConnectionException {
        return createMessage(from, to, cc, subject, createSession());
    }

    /**
     * Creates a simple MIME message with some basic headers.
     * 
     * @param from the 'from' mail address.
     * @param to the 'to' mail address(es).
     * @param cc the 'cc' mail address(es).
     * @param subject the mail subject.
     * @param session the mail session.
     * @return a new MIME message.
     * @throws ConnectionException if error occurred in constructing the mail
     *             message.
     */
    public MimeMessage createMessage(String from, String to, String cc,
            String subject, Session session) throws ConnectionException {
        MimeMessage msg = createMessage(session);

        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(
                    to, false));
            if (cc != null) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress
                        .parse(cc, false));
            }
            if (subject != null) {
                msg.setSubject(subject);
            }
            return msg;
        }
        catch (Exception e) {
            throw new ConnectionException("Error in constructing mail message",
                    e);
        }

    }

    /**
     * Sends a simple mail message.
     * 
     * @param from the 'from' mail address.
     * @param to the 'to' mail address(es).
     * @param cc the 'cc' mail address(es).
     * @param subject the mail subject.
     * @param body the message content.
     * @throws ConnectionException if unable to construct the mail message or to
     *             send out the message.
     */
    public void send(String from, String to, String cc, String subject,
            String body) throws ConnectionException {
        Message msg = createMessage(to, from, cc, subject);
        if (body != null) {
            try {
                msg.setText(body);
                msg.saveChanges();
            }
            catch (Exception e) {
                throw new ConnectionException(
                        "Unable to construct the body part of the mail message",
                        e);
            }
        }
        send(msg);
    }

    /**
     * Sends a mail message.
     * 
     * @param msg the mail message to be sent.
     * @throws ConnectionException if unable to send the mail message
     */
    public void send(Message msg) throws ConnectionException {
        try {
            Transport.send(msg);
        }
        catch (Exception e) {
            throw new ConnectionException("Unable to send SMTP message", e);
        }
    }
}