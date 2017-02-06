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
 * The <code>CorvusStatusQueryData</code> is the data structure representing 
 * the parameters set for status query web services for SFRM protocol using 
 * Corvus level.<br/><br/>
 *   
 * This is the sample WSDL request for the SFRM status query WS request. 
 * <PRE>
 * &lt;messageId&gt; 20070418-124233-75006@147.8.177.42 &lt;/messageId&gt;  
 * </PRE>  
 * Creation Date: 2/5/2007
 * 
 * Creation Date: 02/05/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10329
 */
public class SFRMStatusQueryData extends CorvusStatusQueryData {

	/**
	 * This is the configuration prefix for serialization / de-serialization.<br/><br/>
	 */
	public static final String CONFIG_PREFIX = "sfrm-status-request/config";
	
	/**
	 * This is the param prefix for serialzation / de-serialization.<br/><br/>
	 */
	public static final String PARAM_PREFIX  = "sfrm-status-request/param";
	
	/** 
	 * Default Constructor.
	 */
	public SFRMStatusQueryData(){
		super(CONFIG_PREFIX, PARAM_PREFIX);
	}
}
