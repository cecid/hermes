package hk.hku.cecid.ebms.spa.task;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.piazza.commons.module.EventModule;

public class EbmsEventModule extends EventModule<EbmsEventListener> {
	public EbmsEventModule(String descriptorLocation, ClassLoader loader) {
		super(descriptorLocation, loader);
	}

	public EbmsEventModule(String descriptorLocation, boolean shouldInitialize) {
		super(descriptorLocation, shouldInitialize);
	}

	public EbmsEventModule(String descriptorLocation, ClassLoader loader, boolean shouldInitialize) {
		super(descriptorLocation, loader, shouldInitialize);
	}

	public EbmsEventModule(String descriptorLocation) {
		super(descriptorLocation);
	}
	
	public void fireMessageSent(EbxmlMessage requestMessage) {
		int listenerCount = eventListenerList.size();
		for (int i=0; i<listenerCount; ++i) {
			try {
				EbmsProcessor.core.log.info(
						"Trigger event listener - " + eventListenerList.get(i).getClass().getName());
				eventListenerList.get(i).messageSent(requestMessage);
			} catch (Throwable e) {
				getLogger().error("Error occurs when event listener processes message sent event", e);
			}
		}
	}
	
	public void fireMessageReceived(EbxmlMessage requestMessage) {
		int listenerCount = eventListenerList.size();
		for (int i=0; i<listenerCount; ++i) {
			try {
				EbmsProcessor.core.log.info(
						"Trigger event listener - " + eventListenerList.get(i).getClass().getName());
				eventListenerList.get(i).messageReceived(requestMessage);
			} catch (Exception e) {
				EbmsProcessor.core.log.error("Error occurs when event listener processes message received event", e);
			}
		}
	}
	
	public void fireResponseReceived(EbxmlMessage acknowledgement) {
		int listenerCount = eventListenerList.size();
		for (int i=0; i<listenerCount; ++i) {
			try {
				EbmsProcessor.core.log.info(
						"Trigger event listener - " + eventListenerList.get(i).getClass().getName());
				eventListenerList.get(i).responseReceived(acknowledgement);
			} catch (Exception e) {
				EbmsProcessor.core.log.error("Error occurs when event listener processes message received event", e);
			}
		}
	}
	
	public void fireErrorOccurred(EbxmlMessage errorMessage) {
		int listenerCount = eventListenerList.size();
		for (int i=0; i<listenerCount; ++i) {
			try {
				EbmsProcessor.core.log.info(
						"Trigger event listener - " + eventListenerList.get(i).getClass().getName());
				eventListenerList.get(i).errorOccurred(errorMessage);
			} catch (Exception e) {
				EbmsProcessor.core.log.error("Error occurs when event listener processes error occurred event", e);
			}
		}
	}
}
