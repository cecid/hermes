/**
 * 
 */
package hk.hku.cecid.edi.sfrm.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hk.hku.cecid.edi.sfrm.handler.MessageStatusQueryHandler;
import hk.hku.cecid.edi.sfrm.spa.SFRMLog;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.util.StatusQuery;
import hk.hku.cecid.piazza.commons.module.ActiveTaskModule;
import hk.hku.cecid.piazza.commons.module.LimitedActiveTaskList;

/**
 * @author Patrick Yip
 * Collector to query the status of active message periodically. The status of message includes
 * <ul>
 * <li>status code</li>
 * <li>status description</li>
 * <li>number of processed segment</li>
 * <li>message speed (KB/s)</li>
 * </ul>
 */
public class MessageStatusCollector extends LimitedActiveTaskList {
	
	private long currentExecutionInterval;
	
	private ActiveTaskModule segmentCollector;
	
	private boolean initialized = false;
	
	private void initialize(){
		segmentCollector = (ActiveTaskModule) SFRMProcessor.getInstance().getModuleGroup().getModule("sfrm.outgoing.segment.collector");
		currentExecutionInterval = Long.parseLong(segmentCollector.getParameters().getProperty("execution-interval"));
	}
		
	/* (non-Javadoc)
	 * @see hk.hku.cecid.piazza.commons.module.ActiveTaskList#getTaskList()
	 */
	@Override
	public List getTaskList() {
		if(!initialized){
			initialize();
			initialized = true;
		}
		
		MessageStatusQueryHandler statusHandler = SFRMProcessor.getInstance().getMessageSpeedQueryHandler();
		Iterator<String> statusIter = statusHandler.getMessageList();
		
		double totalSpeed = 0.0;
		long maxSegmentSize = 0;
		
		int count = 0;
		
		while(statusIter.hasNext()){
			
			String msgId = statusIter.next();
			StatusQuery query = (StatusQuery) statusHandler.getMessageSpeedQuery(msgId);	
			
			try {
				query.tick();
				totalSpeed += query.getCurrentSpeed();
				
				if(query.getSegmentSize() > maxSegmentSize)
					maxSegmentSize = query.getSegmentSize();
				
			} catch (Exception e) {
				SFRMProcessor.getInstance().getLogger().error(SFRMLog.MSC_CALLER + "Failure on marking the message status", e);
			}
			
			count++;
		}
		
		return new ArrayList();
	}
}
