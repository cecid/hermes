/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.http;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.CloseableHttpResponse;

import org.apache.http.entity.mime.MultipartEntityBuilder;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.KVPairData;

/**
 * The <code>PartnershipSender</code> is abstract base class for sending HTTP remote request
 * to H2O for executing partnership maintenance operation.
 * <br/><br/> 
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0 $STABLE$
 * @since   H2O 0908
 */
public abstract class PartnershipSender extends HttpSender implements PartnershipOp 
{
    // The operation for creating partnership.
    private int pOp = PartnershipOp.ADD;
    /* The result status line representing the operation result. */
    private String resultStatus = "Not yet run.";

    /**
     * Get the mapping of the partnership operation from integer to words.
     * <br/><br/>
     * By default, it is recommended to return a HashMap(Integer, String) with 3 mappings.
     * <br><br/>
     * HashMap.get(0) = A word representing the add partnership action.
     * HashMap.get(1) = A word representing the delete partnership action.
     * HashMap.get(2) = A word representing the update partnership action. 
     *  
     * @return The mapping of the partnership operation from integer to words. 
     */
    public abstract Map getPartnershipOperationMapping();
	
    /**
     * Get the mapping of the partnership data key to HTTP form parameter name.
     * <br/><br/>
     * For example, if there are 3 data (with keys) in your partnership data and
     * they are named as "dataKey0", "dataKey1" and "dataKey2", and you want the 
     * HTTP request going to execute containing multi-part parameters "formParam0", 
     * "formParam1" and "formParam2" with the value equal to the data value 
     * from "dataKey0", "dataKey1", "dataKey2" respectively, Then 
     * you should return the Map listed below: 
     * <br/><br/>
     * <pre>
     * Map m = new HashMap(); // Or LinkedHashMap() if you want to preserve the order.    
     * m.put("dataKey0", "fromParam0");
     * m.put("dataKey1", "fromParam1");
     * m.put("dataKey2", "fromParam2");
     * return m;
     * </pre> 
     * 
     * @return The mapping of the partnership data key to HTTP form parameter name.
     */
    public abstract Map getPartnershipMapping();
	
    /**
     * Explicit Constructor. Create an instance of <code>PartnershipSender</code>
     * 
     * @param logger The logger for log the sending process.
     * @param d The data used for generate HTTP multi-part request. It must be a kind of partnership data.  
     */
    protected PartnershipSender(FileLogger logger, KVPairData d) {
	super(logger, d);
    }

    /**
     * Explicit Constructor. Create an instance of <code>PartnershipSender</code>
     * 
     * @param logger The logger for log the sending process.
     * @param d The data used for generate HTTP multi-part request. It must be a kind of partnership data.  
     * @param username The username for authentication
     * @param password The password for authentication
     */
    protected PartnershipSender(FileLogger logger, KVPairData d,
				final String username, final String password) {
	super(logger, d, username, password);
    }
	
    /* (non-Javadoc)
     * @see hk.hku.cecid.corvus.http.PartnershipOperation#setExecuteOperation(int)
     */
    public void setExecuteOperation(int pOp){
	if (pOp < 0 || pOp >= PartnershipOp.OP_LEN)
	    throw new IllegalArgumentException("Expected operation value : 0, 1, 2");		
	this.pOp = pOp;
    }
	
    /* (non-Javadoc)
     * @see hk.hku.cecid.corvus.http.PartnershipOperation#getExecuteOperation()
     */
    public int getExecuteOperation(){
	return this.pOp;
    }
	
    /**
     * Get the last status result description after executing the operation.
     * <br/><br/>
     * If the sender has not been invoked by other to execute partnership operation,
     * It returns "Not yet run".
     * 
     * @return the last status result description after executing the operation.
     */
    public String getStatus(){
	return this.resultStatus;
    }
	
    /**
     * [@EVENT] The method <code>onStart</code> log all new configuration. 
     */	
    protected void onStart()
    {
	// Get the data,
	KVPairData data = (KVPairData) this.properties;
	super.onStart();
	if (this.log != null){			
	    FileLogger l = this.log;
	    // Log all information for this sender.
	    l.log("Partnership HTTP Client init at " + new Date().toString());
	    l.log("");
	    l.log("Sending Partnership HTTP Request with following configuration");				
	    l.log("------------------------------------------------------------------");
	    l.log("Partnership Operation : " + this.getPartnershipOperationMapping().get(new Integer(pOp)));
	    if (data != null)
		l.log(data.toString());			
	    l.log("------------------------------------------------------------------");
	    l.log("");		
	}		
    }	
	
    /** 
     * [@EVENT] This method is invoked when the sender is required to create a HTTP Request from configuration.
     * <br/><br/>
     * It generates a multi-part content embedded in the HTTP POST request. The multi-part content
     * contains all partnership data with the parameter name retrieved from the partnership mapping.
     * {@link #getPartnershipMapping()}. Also the type of partnership operation is appended 
     * at the end of multi-part with parameter name equal to 'request_action' and it's value 
     * is extracted thru {@link #getPartnershipOperationMapping()}.
     */
    protected HttpRequestBase onCreateRequest() throws Exception 
    {
	// Validate main parameter.
	Map data   			 = ((KVPairData)this.properties).getProperties();
	Map data2webFormName = this.getPartnershipMapping();
		
	if (data2webFormName == null)
	    throw new NullPointerException("Missing partnership mapping for creating HTTP request");		
		
	// Create the HTTP POST method targeted to service end-point.
	HttpPost post = new HttpPost(this.getServiceEndPoint().toExternalForm());
	MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();

	/* 
	 * For each data key in the partnership data, create a String multi-part
	 * with the request parameter name equal to the mapping from the data key.  
	 * 
	 * For the field like verification / encryption certificates, it creates
	 * a byte array multi-part source. 
	 */	
	Iterator itr = data2webFormName.entrySet().iterator();
	Map.Entry e; 			// an entry representing the partnership data to web form name mapping.
	String formParamName;	// a temporary pointer pointing to the value in the entry.
	Object dataValue;		// a temporary pointer pointing to the value in the partnership data.
		
	while (itr.hasNext()){
	    e = (Map.Entry) itr.next();
	    formParamName = (String) e.getValue();
	    // Add new part if the mapped key is not null.
	    if (e.getValue() != null){
		dataValue = data.get(e.getKey());	
		if (dataValue == null) // Use empty string when the key is not filled.
		    dataValue = "";				
		if (dataValue instanceof String){	// Create literal part					
		    mBuilder = mBuilder.addTextBody(formParamName, (String)dataValue);
		} else if (dataValue instanceof byte[]){ // Create streaming multi-part
		    mBuilder = mBuilder.addBinaryBody(formParamName, (byte[])dataValue);
		} else if (dataValue instanceof Boolean){
		    mBuilder = mBuilder.addTextBody(formParamName, String.valueOf((Boolean)dataValue));
		} else {
		    mBuilder = mBuilder.addTextBody(formParamName, dataValue.toString());
		}
	    }
	}
				
	Map partnershipOpMap = this.getPartnershipOperationMapping(); 		
	/* Add HTTP request action to the web form parameter. */	
	mBuilder = mBuilder.addTextBody("request_action", (String)partnershipOpMap.get(new Integer(this.pOp)));
		
	post.setEntity(mBuilder.build());
	    
	return post;
    }
	
    /**
     * [@EVENT] This method is invoked when receivedas2 the reply HTTP response from the server.
     * <br/><br/>
     * Verify the HTTP response (expected a HTML content) by PartnershipOpVerifer to check
     * whether the partnership operation execute successfully or not.
     * 
     * @throws SAXException 
     * 			When fail to verify by PartnershipOpVerifer.
     */
    protected void onResponse() throws Exception {
	try{
	    // HttpMethod method = this.getExecutedMethod();
	    // InputStream ins = method.getResponseBodyAsStream();
	    CloseableHttpResponse response = this.getResponse();
	    InputStream ins = response.getEntity().getContent();
	    	    
	    new PartnershipOpVerifer().validate(ins);		
	    ins.close();			
	    this.resultStatus = "Operation executed successfully.";
	}
	catch(Exception ex){
	    this.resultStatus = "ERROR: " + ex.getMessage();
	    throw ex; // Re-throw 
	}
    }
}
