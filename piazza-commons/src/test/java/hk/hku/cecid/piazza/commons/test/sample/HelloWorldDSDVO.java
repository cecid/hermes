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

