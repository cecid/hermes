package hk.hku.cecid.corvus.ws.data;

/**
 * The <code>AS2ConfigData</code> is the data structure representing 
 * the parameters set for as2 runtime configuration.<br/><br/>
 * 
 * This is the sample WSDL request for the message status WS request. 
 * <PRE>
 * &lt;active-module-status> true | false &lt;/active-module-status>
 * &lt;inmessage-interval>15000&lt;/inmessage-interval>
 * &lt;inmessage-maxthread>0&lt;/inmessage-maxthread>
 * &lt;outmessage-interval>15000&lt;/outmessage-interval>
 * &lt;outmessage-maxthread>0&lt;/outmessage-maxthread>
 * &lt;outpayload-interval>15000&lt;/outpayload-interval>
 * &lt;outpayload-maxthread>0&lt;/outpayload-maxthread>
 * </PRE>
 *  
 * @author Twinsen Tsang
 * @version 1.0.1
 * @since	Elf 0818
 */
public class AS2ConfigData extends KVPairData {
	
	/**
	 * This is the key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] PARAM_KEY_SET = 
	{
		"active-module-status", 
		"inmessage-interval",   "inmessage-maxthread", 
		"outmessage-interval",	"outmessage-maxthread",
		"outpayload-interval",  "outpayload-maxthread"
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
	public static final String CONFIG_PREFIX = "as2-config-request/config";
	
	/**
	 * This is the param prefix for serialzation / de-serialization.<br/><br/>
	 */
	public static final String PARAM_PREFIX  = "as2-config-request/param";
	
	/**
	 * This is special flag for stress test mode.
	 * In stress test mode, messages are bumped into Hermes2 
	 * before start processing. so we need to switch off
	 * the active module before bumping the message.
	 * and restart after bumping. At the result, 
	 * we need a flag for switch on / off active task module. 
	 */
	public boolean stressMode = false;
	
	/** 
	 * Default Constructor.
	 */
	public AS2ConfigData(){
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
	 * Get the execution interval for incoming message in this data.
	 * 
	 * @return the execution interval for incoming message in this data.
	 */
	public String getInMessageExecInterval(){
		return (String) this.props.get(PARAM_KEY_SET[1]);
	}
	
	/**
	 * @param newInterval
	 * 			the new execution interval for incoming message for this data.
	 */
	public void setInMessageExecInterval(long newInterval){
		this.props.put(PARAM_KEY_SET[1], String.valueOf(newInterval));
	}
	
	/** 
	 * Get the maximum thread for incoming message in this data.
	 * 
	 * @return the maximum thread for incoming message in this data.
	 */
	public String getInMessageMaxThread(){
		return (String) this.props.get(PARAM_KEY_SET[2]);	
	}
	
	/**
	 * @param maxThread
	 * 			the maximum thread for incoming message for this data.
	 */
	public void setInMessageMaxThread(long maxThread){
		this.props.put(PARAM_KEY_SET[2], String.valueOf(maxThread));
	}
	
	/** 
	 * Get the execution interval for outgoing message in this data.
	 * 
	 * @return the execution interval for outgoing message in this data.
	 */
	public String getOutMessageExecInterval(){
		return (String) this.props.get(PARAM_KEY_SET[3]);	
	}
	
	/**
	 * @param newInterval
	 * 			the new execution interval for outgoing message for this data.
	 */
	public void setOutMessageExecInterval(long newInterval){
		this.props.put(PARAM_KEY_SET[3], String.valueOf(newInterval));
	}
	
	/** 
	 * Get the maximum thread for outgoing message in this data.
	 * 
	 * @return the maximum thread for outgoing message in this data.
	 */
	public String getOutMessageMaxThread(){
		return (String) this.props.get(PARAM_KEY_SET[4]);	
	}
	
	/**
	 * @param maxThread
	 * 			the maximum thread for outgoing message for this data.
	 */
	public void setOutMessageMaxThread(long maxThread){
		this.props.put(PARAM_KEY_SET[4], String.valueOf(maxThread));
	}
	
	/** 
	 * Get the execution interval for outgoing payload in this data.
	 * 
	 * @return the execution interval for outgoing payload in this data.
	 */
	public String getOutPayloadExecInterval(){
		return (String) this.props.get(PARAM_KEY_SET[5]);	
	}
	
	/**
	 * @param newInterval
	 * 			the new execution interval for outgoing payload for this data.
	 */
	public void setOutPayloadExecInterval(long newInterval){
		this.props.put(PARAM_KEY_SET[5], String.valueOf(newInterval));
	}
	
	/** 
	 * Get the maximum thread for outgoing payload in this data.
	 * 
	 * @return the maximum thread for outgoing payload in this data.
	 */
	public String getOutPayloadMaxThread(){
		return (String) this.props.get(PARAM_KEY_SET[6]);	
	}
	
	/**
	 * @param maxThread
	 * 			the maximum thread for outgoing payload for this data.
	 */
	public void setOutPayloadMaxThread(long maxThread){
		this.props.put(PARAM_KEY_SET[6], String.valueOf(maxThread));
	}
}
