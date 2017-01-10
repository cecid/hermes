package hk.hku.cecid.corvus.ws.data;

/**
 * The <code>EBMSStatusQueryResponseData</code> is the data structure 
 * representing the response data set for EBMS status query web services. 
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
public class EBMSStatusQueryResponseData extends CorvusStatusQueryResponseData {
	
	/**
	 * This is the key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] PARAM_KEY_SET = 
	{
		"messageId", "status", "statusDescription", "ackMessageId", "ackStatus", 
		"ackStatusDescription"
	};  
	
	/** 
	 * Default Constructor.
	 */
	public EBMSStatusQueryResponseData(){	
		super(PARAM_KEY_SET);
	}
}
