package hk.hku.cecid.piazza.commons.os;

import hk.hku.cecid.piazza.commons.module.SystemComponent;

/**
 * The OSManager acts as a bridge between OSCommander and Piazza Commons framework.
 * It provides convenient way for components to execute system command.
 * <br><br>
 * 
 * Creation Date: 04/05/2009<br><br>
 * @author Philip Wong
 * @version 2.0.0
 */
public class OSManager extends SystemComponent {

	protected OSCommander osCommander;
	
	public void init() throws Exception {
		if (osCommander == null)
			osCommander = new OSCommander(this);
	}
	
	public String getName() {
		return "OSManager(" + osCommander.getOSName() + " " + osCommander.getOSVersion() + ")";
	}
	
	public OSCommander getCommander() {
		return osCommander;
	}
	
}
