/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws.data;

/**
 * The <code>AS2StatusQueryResponseData</code> is the data structure 
 * representing the response data set for EBMS status query web services. 
 *   
 * This is the sample WSDL request for the status query WS request. 
 * <PRE>
 * 	&lt;status&gt; <em>The current status of message</em> &lt;/status&gt;
 *  &lt;statusDescription&gt; <em>The current status description of message</em> &lt;/statusDescription&gt;
 *  &lt;mdnMessageId&gt; <em>The message id of acknowledgment / receipt if any</em> &lt;/ackMessageId&gt;
 *  &lt;mdnStatus&gt; <em>The status of acknowledgment / receipt if any</em> &lt;/ackStatus&gt;
 *  &lt;mdnStatusDescription&gt; <em>The status description of acknowledgment / receipt if any</em> &lt;/ackStatusDescription&gt;  
 * </PRE>  
 * Creation Date: 10/05/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10327
 */
public class AS2StatusQueryResponseData extends CorvusStatusQueryResponseData {

	/**
	 * This is the key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] PARAM_KEY_SET = 
	{
		"messageId", "status", "statusDescription", "mdnMessageId", "mdnStatus", 
		"mdnStatusDescription"
	};  
	
	/** 
	 * Default Constructor.
	 */
	public AS2StatusQueryResponseData(){	
		super(PARAM_KEY_SET);
	}
}
