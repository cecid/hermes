package hk.hku.cecid.piazza.commons.test.asserts;

import java.text.MessageFormat;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/** 
 * The <code>NetworkAssert</code> is 
 * 
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 */
public class NetworkAssert
{
	private static final String SOCKET_REACH_FAILED = 
		"sockect failed to connect <{0}:{1}>";
	
	private static final String SOCKET_TIMEOUT = 
		"socket timeout before able to connect to <{0}:{1}>";
	
	private static final String SOCKET_REACH_SUCCESS_FAILED = 
		"socket able to connect <{0}:{1}>, expected as non-reachable"; 
	
	private static NetworkAssert singleton = new NetworkAssert();
	
	/*
	 * Private constructor to prevent singleton pattern break.
	 */
	private NetworkAssert()
	{	
	}
	
	/*
	 * Get the singleton assertion object.
	 */
	private static NetworkAssert getInstance()
	{
		return singleton;
	}

	private void assertSocketReachable0(String message, String host, int port, int timeout)
	{
		if (message == null)
		{
			message = "";
		}
						
		try
		{
			Socket s = new Socket();
			s.connect(new InetSocketAddress(host, port), timeout);
			//Socket s = new Socket(host, port);
			//Socket s = new Socket(new InetSocketAddress(host, port), timeout);	
		}
		catch(SocketTimeoutException stex)
		{
			throw new AssertionError(message + MessageFormat.format(SOCKET_TIMEOUT, host, String.valueOf(port)) + " : " + stex);
		}
		catch(IOException ioex)
		{
			throw new AssertionError(message + MessageFormat.format(SOCKET_REACH_FAILED, host, String.valueOf(port)) + " : " + ioex);
		}		
		catch(Throwable t)
		{
			throw new AssertionError(message + t);
		}		
	}
	
	/**
	 * Assert whether the server socket with <code>host</code> and <code>port</code> 
	 * is reachable with the time limit <code>timeout</code>. 
	 * 
	 * @param host The host of the server socket binds to.  
	 * @param port The port of the server socket binds to .
	 * @param timeout How long does this assert think it is time out, ie failed to connect. 
	 */
	public static void assertSocketReachable(String host, int port, int timeout)
	{		
		NetworkAssert.getInstance().assertSocketReachable0(null, host, port, timeout);
	}
	
	/**
	 * Assert whether the server socket with <code>host</code> and <code>port</code> 
	 * is reachable with the time limit <code>timeout</code>. 
	 * 
	 * @param message The customized error message when the assertion failed.
	 * @param host The host of the server socket binds to.  
	 * @param port The port of the server socket binds to .
	 * @param timeout How long does this assert think it is time out, ie failed to connect. 
	 */
	public static void assertSocketReachable(String message, String host, int port, int timeout)
	{
		NetworkAssert.getInstance().assertSocketReachable0(message, host, port, timeout);
	}
	
	/**
	 * Assert whether the server socket with <code>host</code> and <code>port</code> 
	 * is reachable with the time limit <code>timeout</code>. 
	 * 
	 * @param message The customized error message when the assertion failed.
	 * @param host The host of the server socket binds to.  
	 * @param port The port of the server socket binds to .
	 * @param timeout How long does this assert think it is time out, ie failed to connect. 
	 */
	public static void assertSocketNonReachable(String host, int port, int timeout)
	{
		NetworkAssert.assertSocketNonReachable(null, host, port, timeout);
	}
	
	/**
	 * Assert whether the server socket with <code>host</code> and <code>port</code> 
	 * is reachable with the time limit <code>timeout</code>. 
	 * 
	 * @param host The host of the server socket binds to.  
	 * @param port The port of the server socket binds to .
	 * @param timeout How long does this assert think it is time out, ie failed to connect. 
	 */
	public static void assertSocketNonReachable(String message, String host, int port, int timeout)
	{
		boolean successFlag = false;
		try
		{
			NetworkAssert.getInstance().assertSocketReachable0(message, host, port, timeout);
		}
		catch(AssertionError ae)
		{
			successFlag = true;
		}
		if (!successFlag)
		{
			throw new AssertionError(MessageFormat.format(SOCKET_REACH_SUCCESS_FAILED, host, String.valueOf(port)));
		}		
	}
}
