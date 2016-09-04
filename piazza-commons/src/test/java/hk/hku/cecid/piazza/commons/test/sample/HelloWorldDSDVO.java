/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.test.sample;

import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDVO;

public class HelloWorldDSDVO extends DataSourceDVO 
{
	private static final long serialVersionUID = -374556768030525710L;
	
	public void setID(String id)
	{
		super.setString("id", id);
	}
	
	public String getID()
	{
		return super.getString("id");
	}
	
	public void setDescription(String desc) 
	{
		super.setString("description", desc);
	}
	
	public String getDescription()
	{
		return super.getString("description");
	}
}

