package hk.hku.cecid.piazza.commons.module;

import hk.hku.cecid.piazza.commons.dao.DAOFactory;
import hk.hku.cecid.piazza.commons.util.Logger;
import hk.hku.cecid.piazza.commons.util.PropertySheet;

public abstract class SystemComponent extends Component {	

	public SystemModule getSystemModule() {
		if (getModule() == null)
			throw new ModuleException("System module not found.");
		return (SystemModule)getModule();
	}

	public Logger getLogger() {
		return (Logger)getSystemModule().getComponent("logger");
	}
	
	public DAOFactory getDAOFactory() {
		return (DAOFactory)getSystemModule().getComponent("daofactory");
	}
	
	public PropertySheet getProperties() {
		return (PropertySheet)getSystemModule().getComponent("properties");
	}
	
	public Component getComponent(String id) {
		return getSystemModule().getComponent(id);
	}
	
}
