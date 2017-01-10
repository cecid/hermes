package hk.hku.cecid.corvus.ws.data;

import java.util.Map;
import java.util.HashMap;


/**
 * The <code>KVPairData</code> is a simple data structures 
 * for storing key-valued style data.<br/><br/>
 * 
 * It is implemented based on {@link java.util.Map}.<br/><br/>
 * 
 * You can define the max capacity through the constructor 
 * so that the internal data does not need to rehash when there
 * is not enough room.
 * 
 * Creation Date: 21/3/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10315
 */
public class KVPairData implements Data {
	
	/**
	 * The KVPair interval data.
	 */
	protected Map props;
	
	/**
	 * The maximum capacity that the data can hold.
	 */
	private int maxCap;
	
	/**
	 * Explicit Constructor.
	 * 
	 * @param maxCapacity
	 * 			The maximum Key value pair that the data can hold.
	 */
	public KVPairData(int maxCapacity){
		this.props  = new HashMap(maxCapacity, 1);
		this.maxCap = maxCapacity; 
	}
	
	/** 
	 * @return the properties set for this MessageStatusRequestData.
	 */
	public Map getProperties(){
		return this.props;
	}
	
	/**
	 * Set the message status request properties and overwrite 
	 * the existing one.<br/><br/>
	 * 
	 * This operation success only when the size of <code>hm</code>
	 * is smaller than it's maxCapacity defined in the constructor.
	 * 
	 * @param hm The new properties set.
	 */
	public void setProperties(Map hm){
		if (hm != null && hm.size() <= maxCap)
			this.props = hm;
	}
	
	/**
     * toString method. Simple Key-Iteration 
     */
    public String toString(){
    	String ret = "";
    	java.util.Set 		keys = this.props.keySet();
    	java.util.Iterator	itr	 = keys.iterator();
    	while(itr.hasNext()){    		
    		Object key 		= itr.next();
    		if (key != null) {
    			Object value 	= this.props.get(key.toString());    		
    			if (value != null) {
    				ret += "Key: " + key + "\t\t" + "Value: " + value.toString() + "\n";
    			}
    		}
    	}
    	return ret;    	
    }
}
