/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.http;

import java.net.MalformedURLException;
import java.net.URL;

// import org.apache.commons.httpclient.HttpClient;
// import org.apache.commons.httpclient.HttpMethod;
// import org.apache.commons.httpclient.HttpStatus;
// import org.apache.commons.httpclient.HttpException;
// import org.apache.commons.httpclient.methods.PostMethod;
// import org.apache.commons.httpclient.auth.AuthScope;
// import org.apache.commons.httpclient.UsernamePasswordCredentials;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.CloseableHttpResponse;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.HttpException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.Credentials;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.Data;

/** 
 * The <code>HttpSender</code> is top base class for sending HTTP request.
 *  
 * TODO: javadoc
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 */
public class HttpSender implements Runnable 
{			
    /* The boolean flag indicating whether the HTTP request requires Authentication. */
    private boolean isAuthRequired;
			
    /* The Apache HTTP client for doing the real HTTP request/response job. */
    // private HttpClient delegationClient;
    private CloseableHttpClient delegationClient;
	
    /* The Apache HTTP client object for configuring the HTTP request and reading the HTTP response. */
    // private HttpMethod requestMethod;
    private HttpRequestBase requestMethod;

    /* The Apache HTTP response object for storing and reading the HTTP response */
    private CloseableHttpResponse response = null;

    /* The Apache credentials object for storing username/ password for authentication */
    // private CredentialsProvider credsProvider = null;
    private Credentials creds = null;

    /*
	
      /**
      * The logger used for log message and exception
      * 
      * @see hk.hku.cecid.corvus.util.FileLogger
      */
    protected FileLogger log;
	
    /* The data properties for this sender. */
    protected Data properties;
	
    /* Number of times to sent to Hermes2. */
    private int loopTimes = 1;
	
    /* The current looping times. */
    private int curTimes  = 0;
	
    /* The custom user object for the call-back. */
    private Object userObj = null;
	
    /* The URL of service end point. */
    protected URL serviceEndPoint = null;		
	
    /**
     * SPA Constructor.<br<br>
     * 
     * It is used when the HTTP Sender is a component in the SPA.
     */
    public HttpSender(){ 
	this(null, null);
    }
	
    /**
     * Explicit Constructor.
     * 
     * @param logger The logger used for log message and exception.
     * @param d The data used for sending HTTP request.
     */
    public HttpSender(FileLogger logger, Data d){
	this.log = logger;	
	if (d == null)
	    throw new NullPointerException("Missing 'data properties' when constructing HTTP sender.");
	this.properties = d;		
	// this.delegationClient = new HttpClient();
	HttpClientBuilder builder = HttpClientBuilder.create();
	this.delegationClient = builder.build();
    }

    /**
     * Explicit Constructor.
     * 
     * @param logger The logger used for log message and exception.
     * @param d The data used for sending HTTP request.
     * @param username The username for authentication
     * @param password The password for authentication
     */
    public HttpSender(FileLogger logger, Data d,
		      final String username, final String password){
	this.log = logger;	
	if (d == null)
	    throw new NullPointerException("Missing 'data properties' when constructing HTTP sender.");
	this.properties = d;		

	if (username == null || username.equals(""))
    	    throw new NullPointerException("Missing 'username' for authentication.");
    	if (password == null)
    	    throw new NullPointerException("Missing 'password' for authentication.");

	this.creds = new UsernamePasswordCredentials(username, password);
	CredentialsProvider credProvider = new BasicCredentialsProvider();
	credProvider.setCredentials(AuthScope.ANY, this.creds);

	HttpClientBuilder builder = HttpClientBuilder.create();
	builder.setDefaultCredentialsProvider(credProvider);
	this.delegationClient = builder.build();
	this.isAuthRequired = true;
    }
	
    /**
     * Explicit Constructor.
     * 
     * @param l			The logger used for log message and exception.
     * @param d The data used for sending HTTP request. 
     * @param endpoint	The URL of service end point.
     */	
    public HttpSender(FileLogger l, Data d, String endpoint){
	this(l,d);
	try{
	    this.serviceEndPoint = new URL(endpoint);
	}catch(MalformedURLException mue){
	    this.onError(mue);
	}
    }	
	
    /**
     * Explicit Constructor.
     * 
     * @param l			The logger used for log message and exception.
     * @param d 		The data used for sending HTTP request. 
     * @param endpoint	The URL of service end point.
     */	
    public HttpSender(FileLogger l, Data d, URL endpoint){
	this(l,d);
	this.serviceEndPoint = endpoint;
    }

    /**
     * Explicit Constructor.
     * 
     * @param l			The logger used for log message and exception.
     * @param d The data used for sending HTTP request. 
     * @param endpoint	The URL of service end point.
     * @param username The username for authentication
     * @param password The password for authentication
     */	
    public HttpSender(FileLogger l, Data d, String endpoint,
		      final String username, final String password){
	this(l,d,username, password);
	try{
	    this.serviceEndPoint = new URL(endpoint);
	}catch(MalformedURLException mue){
	    this.onError(mue);
	}
    }	
	
    /**
     * Explicit Constructor.
     * 
     * @param l			The logger used for log message and exception.
     * @param d 		The data used for sending HTTP request. 
     * @param endpoint	The URL of service end point.
     * @param username The username for authentication
     * @param password The password for authentication
     */	
    public HttpSender(FileLogger l, Data d, URL endpoint,
		      final String username, final String password){
	this(l,d,username, password);
	this.serviceEndPoint = endpoint;
    }
	
    /**
     * Implements this method if you want to send messages without much different between other message to sent.   
     */
    protected void initializeMessage() throws Exception{
	// Implement by sub-class.
    }
	
    /** 
     * [@EVENT] This method is invoked when the sender begins to execute the 
     * run method.<br/>  
     */
    protected void onStart(){
	this.curTimes = 0;
    }
	
    /** 
     * [@EVENT] This method is invoked when the sender is required to 
     * create a HTTP Request from configuration.
     * <br/><br/>
     * By default, this method return a PostMethod pointing to {@link #getServiceEndPoint()}. 	
     * 
     * @throws Exception 
     * 			Sub-class implementation-specific exception
     */
    protected HttpRequestBase onCreateRequest() throws Exception{
	return new HttpPost(this.serviceEndPoint.toExternalForm());		
    }
	
    /**
     * [@EVENT] This method is invoked just before sending the request to HTTP service end-point.
     * 
     * @param client 	The HTTP Connection used for sending SOAP request.
     * @param request	The request created by {@link #onCreateRequest()}. 
     * 
     * @throws Exception Any type of exception will be processed at onError(throwable t). 
     */
    protected void onBeforeRequest(final HttpClient client, final HttpRequestBase request) throws Exception{	
	// Implement by sub-class
    }
	
    /**
     * [@EVENT] This method is invoked when received the reply HTTP  response from the server.
     * 
     * Developer can use {@link #getExecutedMethod()} to get
     * the HTTP method generated thru {@link #onCreateRequest()}
     * 
     * @throws Exception Any type of exception will be processed at onError(throwable t).
     */
    protected void onResponse() throws Exception{		
	// Implement by sub-class
    }		
	
    /**
     * [@EVENT] This method is invoked when the sending execution is ended. 
     */
    protected void onEnd(){	
	// Implement by sub-class
    }
	
    /**
     * [@EVENT] This method is invoked when there is any exception thrown during web service call.
     * <br/><br/>
     * By default, it log the throw-able <code>t</code> to the instance logger.
     */
    protected void onError(Throwable t){		
	t.printStackTrace();
	if (this.log != null){
	    this.log.logStackTrace(t);
	}
    }
	
    /**
     * [@EVENT] This method is invoked when each loop iteration start.
     * <br/><br/> 
     * @throws Exception Any type of exception will be processed at onError(throw-able t).
     */	
    protected void onEachLoopStart() throws Exception{		
	// Implement by-sub-class
    }
	
    /**
     * Set how many times should the sender to be send.
     * 
     * @param loopTimes		the new loopTimes.
     */
    public void setLoopTimes(int loopTimes){
	if (loopTimes > 0){
	    this.loopTimes = loopTimes;
	}
    }
	
    /**
     * Set a user object for call-back.
     * 
     * @param obj 		The user object.
     */
    public void setUserObject(Object obj){
	this.userObj = obj;
    }
	
    /**
     * Set the service end-point.
     * 
     * @param endpoint	The URL of the web service end-point. 
     */
    public void setServiceEndPoint(URL endpoint){
	if (endpoint != null){
	    this.serviceEndPoint = endpoint;	
	}		
    }
	
    /**
     * Set the service end-point.
     * 
     * @param endpoint	The String of the web service end-point. 
     */
    public void setServiceEndPoint(String endpoint){
	try{
	    this.serviceEndPoint = new URL(endpoint);			
	}		
	catch(MalformedURLException mue){
	    if (this.log != null){
		this.log.logStackTrace(mue);
	    }
	}
    }
	
    /**
     * Set to use the basic authentication when calling the web service.
     * 
     * @param username The user-name for basic authentication. 
     * @param password The password for basic authentication.
     * 
     * @throws NullPointerException 
     * 			When the user-name or password is null.			
     */
    // public void setBasicAuthentication(final String username, final String password)
    // {
    // 	if (username == null || username.equals(""))
    // 	    throw new NullPointerException("Missing 'username' for authentication.");
    // 	if (password == null)
    // 	    throw new NullPointerException("Missing 'password' for authentication.");
    // 		
    // 	this.delegationClient.getState().setCredentials(
    // 							new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
    // 							new UsernamePasswordCredentials(username, password));
    // 		
    // 	Use BASIC-Auth immediately.
    // 	this.delegationClient.getParams().setAuthenticationPreemptive(true);
    // 	
    // 	this.isAuthRequired = true;
    // }
	
    /** 
     * @return true if HTTP authentication is required. 
     */
    public boolean isAuthenticationRequired(){
	return this.isAuthRequired;
    }
	
    /**
     * Get how many times should the sender to be send.
     */
    public int getLoopTimes(){
	return this.loopTimes;
    }
	
    /**
     * Get what is the current loop times for looping 
     */
    public int getCurrentLoopTimes(){
	return this.curTimes;
    }
	
    /**
     * Get a user object. 
     */
    public Object getUserObject(){
	return this.userObj;
    }
	
    /**
     * Get the service end-point.
     * 
     * @return the service end-point URL.
     */
    public URL getServiceEndPoint(){
	return this.serviceEndPoint;
    }
	
    /**
     * Get the last executed HTTP method.
     * <br/><br/>
     * This method should be invoked during {@link #onResponse()}.   
     *  
     * @return the last executed HTTP method.
     */
    public HttpRequestBase getExecutedMethod(){
	return this.requestMethod;
    }

    /**
     * Get the response of the last executed HTTP method.
     *
     * This method should be invoked during {@link #onResponse()}. 
     *
     * @return the response of the last executed HTTP method
     */
    public CloseableHttpResponse getResponse(){
	return this.response;
    }

    /**
     * Close http response with exception handling
     *
     * @param response the response to close
     */
    public void closeResponse(CloseableHttpResponse response){
	if (response != null){
	    try {
		response.close();
	    }
	    catch (Exception e) {
		this.onError(e);
	    }
	}
    }
	
    /**
     * The thread execution method.
     */
    public void run()
    {
	// Signals a kick-off event.
	this.onStart();
	try
	    {
		for(int i = 0; i < this.getLoopTimes(); i++)
		    {				
			this.curTimes = i;			
			// Signals a loop start event.
			this.onEachLoopStart();									
			// Asks child class for creating the request method;				
			this.requestMethod = this.onCreateRequest();

			if (this.isAuthRequired) {
			    // this.requestMethod.setDoAuthentication(true);
			    RequestConfig config = RequestConfig.custom()
				.setAuthenticationEnabled(true).build();
			    this.requestMethod.setConfig(config);
			    this.requestMethod.addHeader(BasicScheme.authenticate(creds,"US-ASCII",false));
			}
													 
			this.onBeforeRequest(this.delegationClient, this.requestMethod);
			
			// int responseCode = this.delegationClient.executeMethod(this.requestMethod);
			this.response = this.delegationClient.execute(this.requestMethod);
			int responseCode = this.getResponse().getStatusLine().getStatusCode();
				
			if (responseCode != HttpStatus.SC_OK)
			    throw new HttpException(this.getResponse().getStatusLine().toString());					
				
			this.onResponse();
		    }					
		this.onEnd();	
	    }			
	catch(Exception e){				
	    this.onError(e);				
	}				
	finally{
	    closeResponse(this.response);
		
	    // if (this.requestMethod != null)	this.requestMethod.releaseConnection();
	}
    }
}
