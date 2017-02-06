/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws.data;

import java.sql.Timestamp;

/**
 * The <code>SFRMStatusQueryResponseData</code> is the data structure 
 * representing the response data set for SFRM Message Status Query
 * serivce.<br/><br/>
 * 
 * This is the sample WSDL response for the SFRM status query WS request. 
 * <pre>
 *  	&lt;messageInfo&gt;
 *  	&lt;status&gt; <em>The current status of message</em> &lt;/status&gt;
 *  	&lt;statusDescription&gt; <em>The current status description of message</em> &lt;/statusDescription&gt;
 *  	&lt;numberOfSegments&gt; <em>Maximum number of segments</em> &lt;/numberOfSegments&gt;
 *  	&lt;numberOfProcessedSegments&gt; <em>Number of processed segments</em> &lt;/numberOfProcessedSegments&gt;
 *  	&lt;lastUpdatedTime&gt; <em> The last updated timestamp </em> &lt;/lastUpdatedTime&gt;
 *  	&lt;/messageInfo&gt;
 * </pre>
 * 
 * Creation Date: 10/5/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10327
 */
public class SFRMStatusQueryResponseData extends KVPairData {

	/**
	 * This is the key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] PARAM_KEY_SET = 
	{
		"messageId", "status", "statusDescription", "numberOfSegments", 
		"numberOfProcessedSegments", "lastUpdatedTime"
	};
	
	/** 
	 * Default Constructor.
	 */
	public SFRMStatusQueryResponseData(){
		super(PARAM_KEY_SET.length);
	}
	
	/** 
	 * @return the message ID of the SFRM message being queried.
	 */
	public String getMessageId(){
		return (String) this.props.get(PARAM_KEY_SET[0]);
	}
	
	/**  
	 * @return the current status of SFRM Message. 
	 */
	public String getStatus(){
		return (String) this.props.get(PARAM_KEY_SET[1]);		
	}
	
	/**
	 * @return the current status description of SFRM Message.
	 */
	public String getStatusDescription(){
		return (String) this.props.get(PARAM_KEY_SET[2]);
	}
	
	/**
	 * @return the number of segments for this SFRM Message.
	 */
	public int getNumberOfSegments(){
		try{
			return Integer.parseInt((String) this.props.get(PARAM_KEY_SET[3]));						
		}catch(Exception e){
			return Integer.MIN_VALUE;
		}
	}
	
	/** 
	 * @return the number of processed segments for this SFRM Message.
	 */
	public int getNumberOfProcessedSegments(){
		try{
			return Integer.parseInt((String) this.props.get(PARAM_KEY_SET[4]));						
		}catch(Exception e){
			return Integer.MIN_VALUE;
		}
	}
	
	/**
	 * The last updated timestamp define the last time that the SFRM message
	 * was being processing. (last activity time).
	 *  
	 * @return the last active time for thie SFRM Message.
	 */
	public Timestamp getLastUpdatedTimestamp(){
		try{
			return Timestamp.valueOf((String) this.props.get(PARAM_KEY_SET[5]));
		}catch(Exception e){
			return new Timestamp(0);
		}
	}
}
