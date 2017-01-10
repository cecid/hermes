package hk.hku.cecid.corvus.ws.data;

/**
 * The <code>CorvusStatusQueryResponseData</code> is the data structure 
 * representing the <strong>general</strong> response data set for status 
 * query web services in corvus level. 
 *   
 * This is the sample WSDL request for the status query WS request. 
 * <PRE>
 * 	&lt;status&gt; <em>The current status of message</em> &lt;/status&gt;
 *  &lt;statusDescription&gt; <em>The current status description of message</em> &lt;/statusDescription&gt;
 *  &lt;ackMessageId&gt; <em>The message id of acknowledgment / receipt if any</em> &lt;/ackMessageId&gt;
 *  &lt;ackStatus&gt; <em>The status of acknowledgment / receipt if any</em> &lt;/ackStatus&gt;
 *  &lt;ackStatusDescription&gt; <em>The status description of acknowledgment / receipt if any</em> &lt;/ackStatusDescription&gt;  
 * </PRE>  
 * Creation Date: 10/05/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10327
 */
public class CorvusStatusQueryResponseData extends KVPairData {

	/**
	 * This is the key set for XML serialization / de-serialization.<br/><br/>
	 */
	private final String [] paramKeySet; 	
	
	/**
	 * Default Constructor.
	 */
	public CorvusStatusQueryResponseData(final String [] paramKeySet){
		super(paramKeySet.length);
		this.paramKeySet = paramKeySet;
	}
	
	/** 
	 * @return the message ID of the message being queried.
	 */
	public String getMessageId(){
		return (String) this.props.get(paramKeySet[0]);
	}
	
	/**  
	 * @return the current status of Message. 
	 */
	public String getStatus(){
		return (String) this.props.get(paramKeySet[1]);		
	}
	
	/**
	 * @return the current status description of Message.
	 */
	public String getStatusDescription(){
		return (String) this.props.get(paramKeySet[2]);
	}
	
	/** 
	 * @return the message ID of the acknowledgment corresponding to the message being queried.
	 */
	public String getACKMessageId(){
		return (String) this.props.get(paramKeySet[3]);
	}
	
	/** 
	 * @return the status of the acknowledgment corresponding to the message being queried.
	 */
	public String getACKStatus(){
		return (String) this.props.get(paramKeySet[4]);
	}
	
	/** 
	 * @return the status description of the acknowledgment corresponding to the message being queried.
	 */
	public String getACKStatusDescription(){
		return (String) this.props.get(paramKeySet[5]);
	}
}

