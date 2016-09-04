/**
 * 
 */
package hk.hku.cecid.edi.sfrm.com;

import java.io.File;
import java.io.IOException;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;


/**
 * The outgoing payloads repository retrieves the 
 * payload which is in the form of directory. 
 * 
 * Creation Date: 5/10/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.1
 * @since	1.0.0
 */
public class OutgoingPayloadsRepository extends PayloadsRepository {
	public OutgoingPayloadsRepository(String path) throws Exception{
		super(path);
	}
	
	private Collection getPayloads(String regex){
		getLogger().debug("Inside the OutgoingPayloadsRepository#getPayloads");
		// Get all directories without "~" symbol.
		Collection dirs = this.getRepositorySystem().getDirectories(false,
				regex);
		Collection payloads = new ArrayList();
		
		Iterator   itr		= dirs.iterator();
		while(itr.hasNext()){
			try{
				payloads.add(new FoldersPayload((File)itr.next(), this));
			}catch(IOException ioe){
				getLogger().error(
						"IO Error in Outgoing payloads Repository", ioe);
			}
		}
		itr = null;	// For gc
		return payloads;
	}
	
	/**
	 * @return A set of directories which contains the payloads set.
	 */
	public Collection getPayloads() {
		return this.getPayloads("[^\\~\\.|^\\#\\#|\\%\\%].*");
	}			
	
	/**
     * @return Get the list of processing payloads in the payload repositoy;
     */
    public Collection getProcessingPayloads(){
    	return this.getPayloads("[^\\~\\.|^\\%\\%].*");
    }
	
	/**
     * Create a customizing payloads for the specified 
     * parameter.<br><br>
     * 
     * Since the outgoing payloads is in the form of 
     * &lt;partnership_id&gt;$&lt;message_id&gt;, 
     * so the size of parameters size should have at least 
     * 2. 
     * 
     * @param params
     * 			An array object parameters set for creating the 
     * 			payload.
     * @throws IllegalArgumentException
     * 			if the size of parameters is smaller than 2. 
     */
    public NamedPayloads createPayloads(Object[] params, int initialState) throws Exception {
    	if (params.length < 2)
    		throw new IllegalArgumentException(
					"Not enough parameters for creating payload.");
    	String payloadName = params[0].toString() + 
    						 NamedPayloads.decodeDelimiters + 
    						 params[1].toString();    	    	
    	return new FoldersPayload(payloadName, initialState, this);
    }
    
    /**
     * Get a particular payload in the payload repository 
     * by the specified parameters.
     *   
     * Since the outgoing payloads is in the form of 
     * &lt;partnership_id&gt;$&lt;message_id&gt;, 
     * so the size of parameters size should have at least 
     * 2.    
     *   
     * @param params
     * 			An array object parameters set for creating the 
     * 			payload.      		
     * @param state
     * 			The current state of that payload.	
     * @return the payload with the specified params or null
     * 		   if it does not exist.
     */
    public NamedPayloads getPayload(Object[] params, int state){
    	if (params.length < 2)
    		throw new IllegalArgumentException(
					"Not enough parameters for getting payload.");
    	String payloadName = NamedPayloads.getStateForm(state) + 
    						 params[0].toString() + 
    						 NamedPayloads.decodeDelimiters + 
    						 params[1].toString();
    	File f = new File(this.getRepositoryPath(), payloadName);
    	if (f.exists())
    		return this.createPayloadsProxy(f);
    	f = null;	// For gc
    	return null;
    }
	
	/**
     * Create a customizing payloads for this repository.
     * 
     * @param proxyObj
     * 			The file object for the payloads.
     *  
     * @return a customizing payloads.
     */
    protected NamedPayloads createPayloadsProxy(File proxyObj){
    	try{
    		return new FoldersPayload(proxyObj, this);
    	}catch(IOException ioe){
    		return null;
    	}
    }
}
