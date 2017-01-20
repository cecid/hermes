package hk.hku.cecid.piazza.commons.swallow;

import java.io.IOException;
import java.io.StringWriter;

import hk.hku.cecid.piazza.commons.net.ConnectionException;
import hk.hku.cecid.piazza.commons.net.MailSender;
import hk.hku.cecid.piazza.commons.swallow.DiagnosticUtilities;

/** 
 * The <code>ShutdownHookEmailThread</code> is the actual worker thread executing during the JVM shutdown.
 */
public class ShutdownHookEmailThread extends Thread
{
	public static final String DEFAULT_SHUTDOWN_MAIL_SUBJECT = "JVM has been shutdown.";
	
	protected String from;
	protected String tos;
	protected String ccs;
	protected String subject;
	protected String protocol;
	protected String host;
	protected String username;
	protected String password;
	protected boolean verbose;

	/**
	 * Create an instance of <code>ShutdownHookEmailThread</code> for delivering
	 * shutdown notification mail.
	 * 
	 * @param protocol The protocol for delivering email. default: smtp.	
	 * @param host     The host-name of email server.
	 * @param username The user-name for authenticating the email server, if necessary.
	 * @param password The password for authenticating the email server, if necessary.
	 * @param from     The source email address of the shutdown notification email. 
	 * @param tos      The to email address of recipient notification email.   
	 * @param ccs      The cc email address of recipient notification email.
	 * @param subject  The subject of the email address.
	 * @param verbose  Enable debug mode ?
	 */
	public ShutdownHookEmailThread(
		String protocol, String host, String username, String password, String from, String tos, String ccs, String subject, boolean verbose)
	{	
		this.protocol = protocol;
		this.host 	  = host;
		this.username = username;
		this.password = password;
		this.from     = from;
		this.tos      = tos;
		this.ccs      = ccs;
		this.subject  = subject;
		this.verbose  = verbose;
	}
		
	/**
	 * Get the email protocol of the shutdown alert email. 
	 * 
	 * @return Get the email protocol of the shutdown alert email.
	 */
	public String getProtocol()
	{
		return protocol;
	}

	/**
	 * Get the email host of the shutdown alert email. 
	 * 
	 * @return Get the email host of the shutdown alert email.
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 * Get the user-name (if any) for authenticating the email host for delivering the alert email. 
	 * 
	 * @return Get the user-name (if any) for authenticating the email host.
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Get the password (if any) for authenticating the email host for delivering the alert email. 
	 * 
	 * @return Get the password (if any) for authenticating the email host.
	 */
	public String getPassword()
	{
		return password;
	}
	
	/**
	 * Get the source address of the shutdown alert email. 
	 * 
	 * @return Get the source address of the shutdown alert email.
	 */
	public String getFrom()
	{
		return from;
	}

	/**
	 * Get the recipient address of the shutdown alert email. 
	 * 
	 * @return Get the recipient address of the shutdown alert email.
	 */
	public String getTos()
	{
		return tos;
	}

	/**
	 * Get the carbon copy address of the shutdown alert email. 
	 * 
	 * @return Get the carbon copy address of the shutdown alert email.
	 */
	public String getCcs()
	{
		return ccs;
	}

	/**
	 * Get the subject title of the shutdown alert email. 
	 * 
	 * @return Get the subject title of the shutdown alert email.
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * Get whether the shutdown hook output verbosely.
	 * 
	 * @return Get whether the shutdown hook output verbosely.
	 */
	public boolean getIsVerbose()
	{
		return verbose;
	}
	
	/**
	 * Return the subject of notification email.
	 * 
	 * Sub-class may override this to customize the subject of notification email. 
	 * 
	 * @return Return the subject of notification email, default is {@link #DEFAULT_SHUTDOWN_MAIL_SUBJECT}
	 */
	protected String onCreateMailNotificationSubject()
	{
		return this.subject;
	}
	
	/**
	 * Return the body of notification email.
	 * 
	 * The default return the snapshot of all thread dump information. 
	 * 
	 * Sub-class may override this to customize the subject of notification email.
	 * 
	 * @return Return the body of notification email.
	 */
	protected String onCreateMailNotificationBody()
	{		
		StringWriter sw = new StringWriter();
		
		try
		{
			/*
			 * Perform a full thread dump.
			 */
			DiagnosticUtilities.getNewInstance().dumpAllThread(sw, 3);								
		}
		catch(IOException ioex)
		{
			String error = "Unable to generate thread dump due to : ";
			sw.append(error).append(ioex.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
		finally
		{
			//	sw.close(); // No effect	
		}		
		return sw.getBuffer().toString();
	}

	/**
	 * The shutdown thread execution.  
	 */
	public void run()
	{	
		try
		{			
			String subject = this.onCreateMailNotificationSubject();
			String body    = this.onCreateMailNotificationBody();
			
			MailSender mailSender = new MailSender(protocol, host, username, password);
			
			if (this.verbose)
			{
				mailSender.setDebug(true);
			}
						
			mailSender.send(this.from, this.tos, this.ccs, subject, body);			
		}
		catch (ConnectionException ctex)
		{
			/*
			 * We think that at this moment, the logger may already be finalized. so we just 
			 * output the error in the standard error.
			 */
			ctex.printStackTrace();
		}		
	}
}

/*public static void main(String [] args) 
{
	Map<String, String> p = System.getenv();
	
	final String host  	  = p.get("SHOOK_HOST");		
	final String protocol = p.get("SHOOK_PROTOCOL");
	final String username = p.get("SHOOK_USERNAME");
	final String password = p.get("SHOOK_PASSWORD");
	final String from     = p.get("SHOOK_FROM");
	final String tos      = p.get("SHOOK_TO");
	final String ccs      = p.get("SHOOK_CC");
	final String subject  = p.get("SHOOK_SUBJECT");		
			
	new ShutdownHookEmailThread(protocol, host, username, password, from, tos, ccs, subject).run();		
}*/
