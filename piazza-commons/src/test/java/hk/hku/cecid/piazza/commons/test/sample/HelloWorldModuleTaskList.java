package hk.hku.cecid.piazza.commons.test.sample;

import java.util.ArrayList;
import java.util.List;

import hk.hku.cecid.piazza.commons.module.ActiveTask;
import hk.hku.cecid.piazza.commons.module.ActiveTaskList;

public class HelloWorldModuleTaskList extends ActiveTaskList
{
	public class HelloWorldModuleTask implements ActiveTask
	{

		public void execute() throws Exception
		{
			// TODO Auto-generated method stub
			System.out.println("Hello world!");
		}

		public int getMaxRetries()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		public long getRetryInterval()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean isRetryEnabled()
		{
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isSucceedFast()
		{
			// TODO Auto-generated method stub
			return false;
		}

		public void onAwake()
		{
			// TODO Auto-generated method stub
			
		}

		public void onFailure(Throwable e)
		{
			// TODO Auto-generated method stub
			
		}

		public void setRetried(int retried)
		{
			// TODO Auto-generated method stub
			
		}
		
	}
	
	@Override
	public List getTaskList()
	{
		List<HelloWorldModuleTask> helloWorldList = new ArrayList<HelloWorldModuleTask>();
		for (int i = 0; i < 10; i++)
		{
			helloWorldList.add(new HelloWorldModuleTask());
		}
		return helloWorldList;
	}	
}	