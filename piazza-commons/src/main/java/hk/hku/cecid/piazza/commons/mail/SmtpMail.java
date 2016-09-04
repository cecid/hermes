package hk.hku.cecid.piazza.commons.mail;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;

import com.sun.mail.smtp.SMTPMessage;

/**
 * A class that handles sending SMTP messages.
 * @author Administrator
 *
 */
public class SmtpMail {

	private Transport transport;

	private Properties props;

	private Session session;

	private final String SMTP = "smtp";

	private final String SMTP_SSL = "smtps";

	/**
	 * Create a new SmtpMail instance.
	 * 
	 * @param useSSL
	 * @throws SmtpMailException
	 */
	public SmtpMail(boolean useSSL) throws SmtpMailException {
		props = new Properties();
		session = createSession(props);
		if (useSSL) {
			transport = getTransport(session, SMTP_SSL);
		} else {
			transport = getTransport(session, SMTP);
		}
	}

	/**
	 * Create a new SmtpMail instance with the given SmtpMailProperties object.
	 * 
	 * @param sprops
	 * @param useSSL
	 * @throws SmtpMailException
	 */
	public SmtpMail(SmtpMailProperties sprops, boolean useSSL)
			throws SmtpMailException {
		props = sprops.getProperties();
		session = createSession(props);
		if (useSSL) {
			transport = getTransport(session, SMTP_SSL);
		} else {
			transport = getTransport(session, SMTP);
		}
	}

	/**
	 * Checks whether the transport associated with this instance is connected.
	 * 
	 * @return boolean
	 */
	public boolean isConnected() {
		return transport.isConnected();
	}

	/**
	 * Send the SMTPMessage to the address(es) using the SMTP transport
	 * 
	 * @param msg
	 * @param to
	 * @throws SmtpMailException
	 */
	public void send(SMTPMessage msg, Address[] to) throws SmtpMailException {
		try {
			transport.sendMessage(msg, to);
		} catch (SendFailedException e) {
			throw new SmtpMailException("Recipient address is invalid.", e);
		} catch (MessagingException e) {
			throw new SmtpMailException(
					"The transport is not connected or is dead.", e);
		}
	}

	/**
	 * Convenience method for composing a simple text MIME Message with the
	 * source.
	 * 
	 * @param source
	 * @param from
	 * @param to
	 * @param subject
	 * @throws SmtpMailException
	 */
	public void send(String source, Address from, Address[] to, String subject)
			throws SmtpMailException {
		send(composeMessage(source, from, to, subject), to);

	}

	/**
	 * Attempts to close the transport connection. If the transport is not
	 * connected, will not do anything.
	 * 
	 * @throws SmtpMailException
	 */
	public void transportClose() throws SmtpMailException {
		try {
			if (transport.isConnected()) {
				transport.close();
			}
		} catch (MessagingException e) {
			throw new SmtpMailException("Error closing the connection.", e);
		}
	}

	/**
	 * Attempts to connect the smtp transport object using the default values
	 * from the session.
	 * 
	 * @throws SmtpMailException
	 */
	public void transportConnect() throws SmtpMailException {
		transportConnect(null, -1, null, null);
	}

	/**
	 * Attempts to connect the smtp transport object. Use -1 for the default
	 * port, and null for the default values from the session.
	 * 
	 * @throws SmtpMailException
	 * 
	 * @throws MessagingException
	 */
	public void transportConnect(String host, int port, String password,
			String username) throws SmtpMailException {
		try {
			transport.connect(host, port, password, username);
		} catch (AuthenticationFailedException e) {
			throw new SmtpMailException(
					"Authentication failed when connecting to SMTP server.", e);
		} catch (IllegalStateException e) {
			throw new SmtpMailException("Service is alread connected.", e);
		} catch (MessagingException e) {
			throw new SmtpMailException(
					"Could not connect to the SMTP server.", e);
		}
	}

	/**
	 * Helper method for composing a simple text MIME Message with the source.
	 * If from address is null, will use the internet address of the local host.
	 * 
	 * @param source
	 * @return
	 * @throws SmtpMailException
	 */
	private SMTPMessage composeMessage(String source, Address from,
			Address[] to, String subject) throws SmtpMailException {
		SMTPMessage msg = new SMTPMessage(session);
		try {
			if (from == null) {
				msg.setFrom();
			} else {
				msg.setFrom(from);
			}
			msg.setSubject(subject);
			msg.setRecipients(Message.RecipientType.TO, to);
			msg.setText(source);
			msg.setSentDate(new Date());
			return msg;
		} catch (MessagingException e) {
			throw new SmtpMailException("Error creating the message.", e);
		}
	}

	/**
	 * Get a new session with the following properties
	 * 
	 * @param prop
	 */
	private Session createSession(Properties prop) {
		return Session.getInstance(prop);
	}

	/**
	 * Get and SMTP transport object for the session ses
	 * 
	 * @param ses
	 * @return
	 * @throws SmtpMailException
	 * @throws NoSuchProviderException
	 */
	private Transport getTransport(Session ses, String prot)
			throws SmtpMailException {
		try {
			return ses.getTransport(prot);
		} catch (NoSuchProviderException e) {
			throw new SmtpMailException("No such provider for this protocol.",
					e);
		}
	}
	
	public Session getSession(){
		return session;
	}
}
