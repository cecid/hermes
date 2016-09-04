package hk.hku.cecid.piazza.commons.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class EventModule<TEventListener> extends Module {
	public static final String MODULE_ID = "eventModule";
	
	protected List<TEventListener> eventListenerList;
	
	public EventModule(String descriptorLocation, ClassLoader loader) {
		super(descriptorLocation, loader);
	}

	public EventModule(String descriptorLocation, boolean shouldInitialize) {
		super(descriptorLocation, shouldInitialize);
	}

	public EventModule(String descriptorLocation, ClassLoader loader, boolean shouldInitialize) {
		super(descriptorLocation, loader, shouldInitialize);
	}

	public EventModule(String descriptorLocation) {
		super(descriptorLocation);
	}
	
	public void init() {
		super.init();
		eventListenerList = new ArrayList<TEventListener>();
		Collection<TEventListener> components = (Collection<TEventListener>) getComponents();
		Iterator<TEventListener> iter = components.iterator();
		while (iter.hasNext()) {
			eventListenerList.add(iter.next());
		}
	}
	
	public Collection<TEventListener> getListeners() {
		List<TEventListener> cloneList = new ArrayList<TEventListener>(eventListenerList);
		return cloneList;
	}
	
	public boolean hasListeners() {
		return (!eventListenerList.isEmpty());
	}
}
