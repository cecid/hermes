package hk.hku.cecid.piazza.commons.test.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.PushbackInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.HashMap;
import java.net.ServerSocket;
import java.net.Socket;

/** 
 * The <code>SimpleHttpMonitor</code> is a very simple raw HTTP monitor used for testing.
 * 
 * It is usually required to override some method in this class to provide handle
 * the HTTP request / response event such as generating proper response for the requester.
 * 
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since	JDK5.0    
 */
public class SimpleSoapMonitor implements Runnable 
{
	/* The stream used for capturing data */
	private InputStream fullHTTPInStream;	
	private InputStream	contentHTTPInStream;	
	private ByteArrayOutputStream fullHTTPOutStream = new ByteArrayOutputStream();
	
	/* The HTTP header from the last HTTP request */
	private Map headers	= new HashMap();		
	/* The content length of last HTTP request */
	private int contentLength 					= -1;
	/* The start offset of HTTP entity body from the captured HTTP request. */ 
	private int contentStartOffset				= -1;
	/* The content type of last HTTP request */
	private String contentType;	
	/* The content length of the HTTP response */
	private int responseContentLength			= 0;
	/* The content type of the HTTP response */
	private String responseContentType          = "text/plain";	
	/* The port used for listening data */
	private int port;
	/* The server thread for executing the HTTP monitor */
	private Thread serverThread	= new Thread(this);
	/* The server socket for listening socket connection */
	private ServerSocket sSocket;
	
	/* Whether the monitor has been stopped ? */
	private volatile boolean stopped = true;
		
	public static final String CONTENT_LENGTH 	= "Content-Length";
	public static final String CONTENT_TYPE		= "Content-Type";
	
	protected static final byte[] STATUS_200 	= "HTTP/1.1 200 OK ".getBytes();
	protected static final byte[] HD_SERVIER 	= "Server: WSC_HTTP_monitor".getBytes();
	protected static final byte[] HD_CT_LEN 	= "Content-Length: ".getBytes();
	protected static final byte[] HD_CT_TYPE	= "Content-type: ".getBytes();
	protected static final byte[] CRLF			= "\r\n".getBytes();
	
	/**
	 * Explicit constructor for <code>SimpleHttpMonitor</code>
	 *    
	 * @param port The port number listening HTTP request. 
	 */
	public SimpleSoapMonitor(int port)
	{
		this.port = port;
		
		/*
		 * The socket should be initialized so that it is safe to invoke #stop without
		 * actually starting the HTTP monitor.  
		 */
		try
		{
			this.sSocket = new ServerSocket(this.port);
		}
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
	}
	
	/**
	 * Start the HTTP monitor.
	 */
	public synchronized void start()
	{
		if (this.stopped)
		{
			this.stopped = false;
			this.serverThread.start();	
		}
	}
	
	/**
	 * Stop the HTTP monitor.
	 */
	public synchronized void stop()
	{
		if (this.serverThread.isAlive() && !this.serverThread.isInterrupted())
		{
			this.stopped = true;
			this.serverThread.interrupt();
			
			/*
			 * attempt to shutdown the server socket (if necessary) to guarantee
			 * the server socket has been closed.  
			 */
			try
			{
				if (!this.sSocket.isClosed())
				{
					this.sSocket.close();	
				}							
			}
			catch(IOException ioex)
			{
				ioex.printStackTrace(); // log information 
			}
		}		
		
		/*
		 * Prepare for the next server.start() invocation.
		 */
		this.serverThread = new Thread(this);
	}

	/**
	 * [@EVENT] This method is invoked when a socket connection is accepted.
	 * 
	 * Do nothing by default.
	 */
	protected void onAccept(final Socket s)
	{
		
	}
	
	/**
	 * [@EVENT] This method is invoked after the socket is accepted. 
	 */
	protected void onRequest(final InputStream ins) throws IOException 
	{
		// Parse the HTTP Header and capture to fullHTTPOutStream
		this.parseHttpHeader(ins, fullHTTPOutStream);		
		
		// Okay, it is safe to get the content length after parsing the HTTP header
		int len = this.getContentLength();
		
		// Mark the index where the HTTP content start with.
		this.contentStartOffset = fullHTTPOutStream.size();		
		// Parse the HTTP body
		this.parseHttpBody	(ins, fullHTTPOutStream, len);
		
		// Redirect the content to input content stream.
		byte[] request = fullHTTPOutStream.toByteArray();
		
		if (this.contentStartOffset != -1)
		{
			// Capture only the content.
			this.contentHTTPInStream = new ByteArrayInputStream(
				request, this.contentStartOffset, request.length);						
		}
		
		// Capture the whole stream.
		fullHTTPInStream = new ByteArrayInputStream(request);					
		fullHTTPOutStream.close();			
	}
	
	/**
	 * [@EVENT] This method is invoked before calling onResponse. It ask the 
	 * sub-class implementation for the content length of this response. Default 
	 * return 0.    
	 */
	protected int  onResponseLength()
	{
		return 0;
	}
	
	protected String onResponseContentType()
	{
		return "text/plain";
	}
	
	/**
	 * [@EVENT] This method is invoked when the HTTP content has been parsed, and now ready 
	 * to write content to socket.
	 */
	protected void onResponse(final OutputStream os) throws IOException 
	{
		os.write(STATUS_200);
		os.write(CRLF);
		os.write(HD_SERVIER);
		os.write(CRLF);
		os.write(HD_CT_LEN);		
		os.write(String.valueOf(this.responseContentLength).getBytes());
		os.write(CRLF);		
		os.write(HD_CT_TYPE);
		os.write(String.valueOf(this.responseContentType).getBytes());
		os.write(CRLF);
		os.write(CRLF);
	}
	
	/**
	 * The thread execution method.
	 */
	public void run()
	{	
		try
		{
			while(!stopped)
			{
				Socket s = null;
				try
				{
					// Accept a new connection.
					s = this.sSocket.accept();
					// Invoke event.
					this.onAccept(s);
					
					// Create a buffered socket input stream.					
					InputStream bsis = new BufferedInputStream(s.getInputStream());
					
					// INVOKE event 
					this.onRequest(bsis);
					
					// INVOKE event for determine the response content-length.
					this.responseContentLength = this.onResponseLength(); 
					this.responseContentType   = this.onResponseContentType();
					
					// Create a buffered socket output stream. 
					OutputStream bsos = new BufferedOutputStream(s.getOutputStream());
					
					// INVOKE event
					this.onResponse(bsos);			
					
					bsos.flush();
					bsos.close();
				}
				catch(IOException ioex)
				{
					if (!this.stopped) 
					{
						ioex.printStackTrace();
					}
				}				
				finally
				{
					if (s != null && s.isClosed())
					{
						s.close();
					}
				}
			}						
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		finally
		{
			try
			{
				/*
				 * Close the server socket to prevent further binding.
				 */
				if (sSocket != null && !sSocket.isClosed())
				{
					sSocket.close();
				}
			}
			catch(IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
	}		
	
	/** 
	 * @return The content length of last HTTP request.
	 */
	public synchronized int getContentLength()
	{		
		if (contentLength == -1)
		{
			String contentLen = (String) this.headers.get(CONTENT_LENGTH);
			if (contentLen != null)
			{ 
				try
				{
					this.contentLength = Integer.parseInt(contentLen);		
					return this.contentLength;
				}
				catch(NumberFormatException nfe){} // TODO: 
			}
		}
		return this.contentLength;
	}
	
	/** 
	 * @return The content type of last HTTP request
	 */
	public String getContentType()
	{
		if (contentType == null)
		{
			this.contentType = (String) this.headers.get(CONTENT_TYPE);
		}
		return contentType;
	}
	
	/** 
	 * @return Get the last HTTP request header monitored. 
	 * */
	public Map getHeaders()
	{
		return this.headers;
	}
	
	/** 
	 * Get the input stream containing the raw last HTTP request (header + content).
	 * 
	 * @return Get the last HTTP request content.
	 * */
	public InputStream getInputStream()
	{
		return this.fullHTTPInStream;
	}
	
	/** 
	 * Get the input stream containing the HTTP body content from the last HTTP request.
	 * It is different from {@link #getInputStream()} because that returns the stream
	 * containing (HTTP header + HTTP body content). 
	 * 
	 * @return the input stream containing the HTTP body content from the last HTTP request.
	 */
	public InputStream getContentStream()
	{
		return this.contentHTTPInStream;
	}
	
	/*
	 * Reset the data 
	 */
	private void resetData()
	{
		// Reset the configuration and header field.
		fullHTTPOutStream.reset(); 
		this.headers.clear();
		this.contentLength 		= -1;
		this.contentStartOffset	= -1;
		this.contentType 		= null;
	}
	
	/*
	 * 
	 */
	private void parseHttpHeader(InputStream sins, OutputStream capouts) 
		throws IOException 
	{
		if (sins == null)
		{
			throw new NullPointerException("Missing 'SocketInputStream' for parsing Http line.");
		}
		if (capouts == null)
		{
			throw new NullPointerException("Missing 'CapturedOutputStream' for captugin Http line.");
		}
		
		PushbackInputStream pbis = new PushbackInputStream(sins);
		
		/*
		 * Parse the whole HTTP header. 
		 * 
		 * 
		 */
		char pc = (char) -1; char c;		
		while ((c = (char)pbis.read()) != -1)
		{
			if (c != '\r'){				
				pbis.unread(c);
				this.parseHttpLine(pbis, capouts);
			}
			else {
				pc = c;
				c = (char) pbis.read();			
				if (c == '\n' && pc == '\r'){
					capouts.write((int)pc);
					capouts.write((int)c);
					break; // Parse end-line
				}
			}			
		}		
	}
	
	/*
	 * 
	 */
	private void parseHttpLine(InputStream sins, OutputStream capouts) 
		throws IOException 
	{			
		char pc = (char) -1; 
		char c;		
		boolean colonized = false;
		String name = null; String value = null;
		
		int len   = 128;
		int count = 0;
		char[] cs = new char[len];
		
		while ((c = (char)sins.read()) != -1){			
			capouts.write(c);			
			if (c == '\n' && pc == '\r'){
				// Get the value
				if (colonized){
					// Ignore the captured colon and CR
					value = new String(cs, 1, count - 1).trim();
					this.headers.put(name, value);
				}
				break;
			} 
			if (c == ':' && !colonized){
				// Get the header
				name = new String(cs, 0, count).trim();
				colonized = true;
				count = 0;				
			}
			// Update the previous char pointer.
			pc = c;
			cs[count++] = c;
			// Re-allocate if the buffer is not big enough.
			if (count == len){
				char[] ncs = new char[len * 2];
				System.arraycopy(cs, 0, ncs, 0, count);
				cs = ncs;
			}
		}		
	}
	
	/*
	 * 
	 */
	private void parseHttpBody(InputStream sins, OutputStream capouts, int contentLength) 
		throws IOException 
	{
		int read = 0;
		char c;
		while ( read++ < contentLength )
		{
			c = (char)sins.read();
			capouts.write(c);				
		}
	}		
}
