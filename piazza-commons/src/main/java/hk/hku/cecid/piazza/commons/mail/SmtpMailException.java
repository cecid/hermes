package hk.hku.cecid.piazza.commons.mail;

/**
 * General SmtpMail exception
 * 
 * @author Joel Matsumoto
 * 
 */
public class SmtpMailException extends Exception {

	/**
	 * Create a SmtpMail exception with no message.
	 * 
	 */
	public SmtpMailException() {
		super();
	}

	/**
	 * Create a SmtpMail exception with a message.
	 * 
	 * @param msg
	 */
	public SmtpMailException(String msg) {
		super(msg);
	}

	/**
	 * Create a SmtpMail exception with a message and exception e.
	 * 
	 * @param msg
	 * @param e
	 */
	public SmtpMailException(String msg, Exception e) {
		super(msg,e);
	}
	
	/**
	 * Create a SmtpMail exception with the exception e.
	 * 
	 * @param e
	 */
	public SmtpMailException(Exception e) {
		super(e);
	}

}
