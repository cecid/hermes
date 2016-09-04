package hk.hku.cecid.piazza.corvus.core.main.admin.hc.module;

import java.util.ArrayList;
import java.util.List;

public class NeoSchedulerTaskList extends SchedulerTaskList{

	@Override
	public List getTaskList() {
		ArrayList list = new ArrayList();
		list.add(new NeoSchedulerTask());
		return list;
	}
}
