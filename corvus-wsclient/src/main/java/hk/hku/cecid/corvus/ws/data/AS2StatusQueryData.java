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
 * The <code>AS2StatusQueryData</code> is the data structure
 * representing the request parameter set for AS2 Status Query
 * web services.<br/><br/>
 * 
 * This is the sample WSDL request for the status query WS request. 
 * <PRE>
 * &lt;messageId&gt; 20070418-124233-75006@147.8.177.42 &lt;/messageId&gt;  
 * </PRE>    
 * Creation Date: 10/05/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10327
 */
public class AS2StatusQueryData extends CorvusStatusQueryData {

	/**
	 * This is the configuration prefix for serialization / de-serialization.<br/><br/>
	 */
	public static final String CONFIG_PREFIX = "as2-status-request/config";
	
	/**
	 * This is the param prefix for serialzation / de-serialization.<br/><br/>
	 */
	public static final String PARAM_PREFIX  = "as2-status-request/param";
	
	/** 
	 * Default Constructor.
	 */
	public AS2StatusQueryData(){
		super(CONFIG_PREFIX, PARAM_PREFIX);
	}
}
