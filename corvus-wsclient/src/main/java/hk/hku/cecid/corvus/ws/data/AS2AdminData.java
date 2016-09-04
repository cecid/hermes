/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws.data;

import hk.hku.cecid.corvus.http.PartnershipOp;

/** 
 * The <code>EBMSAdminData</code> is data structure for holding the administration data for EbMS plugin.
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 */
public class AS2AdminData extends AdminData 
{
	/**
	 * This is the configuration prefix for serialization / de-serialization.<br/><br/>
	 */
	public static final String CONFIG_PREFIX = "as2-admin-request/config";
	
	/**
	 * This is the parameter prefix for serialzation / de-serialization.<br/><br/>
	 */
	public static final String PARAM_PREFIX  = "as2-admin-request/param";	
	
	/**
	 * Create an instance of <code>EBMSAdminData</code> with default value.
	 * <br/><br/>
	 * <ol>
	 * 	<li>Username : corvus</li>
	 * 	<li>Password : corvus</li>
	 * 	<li>Manage Partnership End-point : http://localhost:8080/corvus/admin/as2/partnership</li>
	 * 	<li>Envelop Query End-point : http://localhost:8080/corvus/admin/as2/repository</li>
	 * 	<li>Partnership Operation : 1</li>
	 * 	<li>Message Box Criteria: INBOX</li>
	 * 	<li>Message ID Criteria: changem-messageid</li>	 
	 * </ol> 
	 */
	public AS2AdminData(){
		super(CONFIG_KEY_SET.length);
	}
}
