package hk.hku.cecid.edi.as2.module;

import hk.hku.cecid.edi.as2.AS2PlusProcessor;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.piazza.commons.module.EventModule;

public class AS2EventModule extends EventModule<AS2EventListener> {	
	public AS2EventModule(String descriptorLocation, ClassLoader loader) {
		super(descriptorLocation, loader);
	}

	public AS2EventModule(String descriptorLocation, boolean shouldInitialize) {
		super(descriptorLocation, shouldInitialize);
	}

	public AS2EventModule(String descriptorLocation, ClassLoader loader, boolean shouldInitialize) {
		super(descriptorLocation, loader, shouldInitialize);
	}

	public AS2EventModule(String descriptorLocation) {
		super(descriptorLocation);
	}
	
	public void fireMessageSent(AS2Message requestMessage) {
		int listenerCount = eventListenerList.size();
		for (int i=0; i<listenerCount; ++i) {
			try {
				AS2PlusProcessor.getInstance().getLogger().info(
						"Trigger event listener - " + eventListenerList.get(i).getClass().getName());
				eventListenerList.get(i).messageSent(requestMessage);
			} catch (Throwable e) {
				getLogger().error("Error occurs when event listener processes message sent event", e);
			}
		}
	}

	public void fireMessageReceived(AS2Message requestMessage) {
		int listenerCount = eventListenerList.size();
		for (int i=0; i<listenerCount; ++i) {
			try {
				AS2PlusProcessor.getInstance().getLogger().info(
						"Trigger event listener - " + eventListenerList.get(i).getClass().getName());
				eventListenerList.get(i).messageReceived(requestMessage);
			} catch (Throwable e) {
				getLogger().error("Error occurs when event listener processes message received event", e);
			}
		}
	}
	
	public void fireResponseReceived(AS2Message receipt) {
		int listenerCount = eventListenerList.size();
		for (int i=0; i<listenerCount; ++i) {
			try {
				AS2PlusProcessor.getInstance().getLogger().info(
						"Trigger event listener - " + eventListenerList.get(i).getClass().getName());
				eventListenerList.get(i).responseReceived(receipt);
			} catch (Throwable e) {
				getLogger().error("Error occurs when event listener processes message received event", e);
			}
		}
	}
	
	public void fireErrorOccurred(AS2Message errorResponse) {
		int listenerCount = eventListenerList.size();
		for (int i=0; i<listenerCount; ++i) {
			try {
				AS2PlusProcessor.getInstance().getLogger().info(
						"Trigger event listener - " + eventListenerList.get(i).getClass().getName());
				eventListenerList.get(i).errorOccurred(errorResponse);
			} catch (Throwable e) {
				getLogger().error("Error occurs when event listener processes message received event", e);
			}
		}
	}
}
