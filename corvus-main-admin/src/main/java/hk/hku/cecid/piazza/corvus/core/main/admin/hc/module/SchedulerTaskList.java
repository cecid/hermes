package hk.hku.cecid.piazza.corvus.core.main.admin.hc.module;

import java.util.ArrayList;
import java.util.List;

import hk.hku.cecid.piazza.commons.module.ActiveTaskList;

public class SchedulerTaskList extends ActiveTaskList {

	public List getTaskList() {
		ArrayList list = new ArrayList();
		list.add(new SchedulerTask());
		return list;
	}

}
