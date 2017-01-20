package hk.hku.cecid.corvus.ws.data;

/**
 * The <code>EBMSConfigData</code> is the data structure representing the
 * parameters set for EbMS runtime configuration.
 * 
 * This is the WSDL schema for the message status WS request.
 * 
 * <pre>  
 *  &lt;active-module-status&gt; true | false &lt;/active-module-status&gt;
 *  &lt;incollector-interval&gt;15000&lt;/incollector-interval&gt;
 *  &lt;incollector-maxthread&gt;0&lt;/incollector-maxthread&gt;
 *  &lt;outcollector-interval&gt;15000&lt;/outcollector-interval&gt;
 *  &lt;outcollector-maxthread&gt;0&lt;/outcollector-maxthread&gt;
 *  &lt;mailcollector-interval&gt;15000&lt;/mailcollector-interval&gt;
 *  &lt;mailcollector-maxthread&gt;0&lt;/mailcollector-maxthread&gt;
 * </pre>
 *   
 * @author 	Twinsen
 * @version 1.0.1
 * @since	Elf 0818 
 */
public class EBMSConfigData extends KVPairData {

	/**
	 * This is the key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] PARAM_KEY_SET = 
	{
		"active-module-status",
		"incollector-interval"  , "incollector-maxthread",
		"outcollector-interval" , "outcollector-maxthread",
		"mailcollector-interval", "mailcollector-maxthread"
	};
	
	/**
	 * This is the configuration key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] CONFIG_KEY_SET = 
	{
		"endpoint"       
	};
	
	/**
	 * This is the configuration prefix for serialization / de-serialization.<br/><br/>
	 */
	public static final String CONFIG_PREFIX = "ebms-config-request/config";
	
	/**
	 * This is the param prefix for serialzation / de-serialization.<br/><br/>
	 */
	public static final String PARAM_PREFIX  = "ebms-config-request/param";	
	
	/** 
	 * Default Constructor.
	 */
	public EBMSConfigData(){
		super(PARAM_KEY_SET.length + CONFIG_KEY_SET.length);
	}
	
	/**
	 * @return Get the web service endpoint for sending as2 config message to corvus.
	 */
	public String getSendEndpoint(){
		return (String)this.props.get(CONFIG_KEY_SET[0]);
	}
	
	/** 
	 * Set the web service endpoint for sending as2 config message to corvus.
	 * 
	 * @param endpoint
	 * 			The web service endpoint for sending as2 config message to corvus.
	 */
	public void setSendEndpoint(String endpoint){
		this.props.put(CONFIG_KEY_SET[0], endpoint);
	}
	
	/** 
	 * @return 	Get the boolean value for ths active module status wanted to set for this
	 *			data. 		 
	 */
	public boolean getActiveModuleStatusBn(){
		return Boolean.valueOf((String) this.props.get(PARAM_KEY_SET[0])).booleanValue();
	}
	
	/** 
	 * @return 	Get the active module status wanted to set for this data.
	 */
	public String getActiveModuleStatus(){
		return (String) this.props.get(PARAM_KEY_SET[0]);
	}
	
	/**
	 * Set the new active module status wanted for this data.
	 * 
	 * @param newStatus the new active module status wanted for this data. 
	 */
	public void setActiveModuleStatus(boolean newStatus){
		this.props.put(PARAM_KEY_SET[0], String.valueOf(newStatus));
	}	
	
	/** 
	 * Get the execution interval for incoming collector in this data.
	 * 
	 * @return the execution interval for incoming collector in this data.
	 */
	public String getInCollectorExecInterval(){
		return (String) this.props.get(PARAM_KEY_SET[1]);
	}
	
	/**
	 * @param newInterval
	 * 			the new execution interval for incoming collector for this data.
	 */
	public void setInCollectorExecInterval(long newInterval){
		this.props.put(PARAM_KEY_SET[1], String.valueOf(newInterval));
	}
	
	/** 
	 * Get the maximum thread for incoming message in this data.
	 * 
	 * @return the maximum thread for incoming message in this data.
	 */
	public String getInCollectorMaxThread(){
		return (String) this.props.get(PARAM_KEY_SET[2]);	
	}
	
	/**
	 * @param maxThread
	 * 			the maximum thread for incoming message for this data.
	 */
	public void setInCollectorMaxThread(long maxThread){
		this.props.put(PARAM_KEY_SET[2], String.valueOf(maxThread));
	}
	
	/** 
	 * Get the execution interval for outgoing collector in this data.
	 * 
	 * @return the execution interval for outgoing collector in this data.
	 */
	public String getOutCollectorExecInterval(){
		return (String) this.props.get(PARAM_KEY_SET[3]);
	}
	
	/**
	 * @param newInterval
	 * 			the new execution interval for outgoing collector for this data.
	 */
	public void setOutCollectorExecInterval(long newInterval){
		this.props.put(PARAM_KEY_SET[3], String.valueOf(newInterval));
	}
	
	/** 
	 * Get the maximum thread for outgoing message in this data.
	 * 
	 * @return the maximum thread for outgoing message in this data.
	 */
	public String getOutCollectorMaxThread(){
		return (String) this.props.get(PARAM_KEY_SET[4]);	
	}
	
	/**
	 * @param maxThread
	 * 			the maximum thread for outgoing message for this data.
	 */
	public void setOutCollectorMaxThread(long maxThread){
		this.props.put(PARAM_KEY_SET[4], String.valueOf(maxThread));
	}
	
	/** 
	 * Get the execution interval for mail collector in this data.
	 * 
	 * @return the execution interval for mail collector in this data.
	 */
	public String getMailCollectorExecInterval(){
		return (String) this.props.get(PARAM_KEY_SET[5]);
	}
	
	/**
	 * @param newInterval
	 * 			the new execution interval for mail collector for this data.
	 */
	public void setMailCollectorExecInterval(long newInterval){
		this.props.put(PARAM_KEY_SET[5], String.valueOf(newInterval));
	}
	
	/** 
	 * Get the maximum thread for mail message in this data.
	 * 
	 * @return the maximum thread for mail message in this data.
	 */
	public String getMailCollectorMaxThread(){
		return (String) this.props.get(PARAM_KEY_SET[6]);	
	}
	
	/**
	 * @param maxThread
	 * 			the maximum thread for mail message for this data.
	 */
	public void setMailCollectorMaxThread(long maxThread){
		this.props.put(PARAM_KEY_SET[6], String.valueOf(maxThread));
	}
}
