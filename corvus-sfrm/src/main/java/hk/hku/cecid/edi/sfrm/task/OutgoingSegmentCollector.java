/**
 * Contains the set of active module collector for 
 * handling packaging, segmenting, sending, joining and unpacking of payloads.
 */
package hk.hku.cecid.edi.sfrm.task;

import java.io.IOException;

import java.util.Properties;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.spa.SFRMLog;
import hk.hku.cecid.edi.sfrm.util.BandWidthOptimizer;
import hk.hku.cecid.edi.sfrm.com.PackagedPayloads;
import hk.hku.cecid.edi.sfrm.com.PayloadsState;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDVO;
import hk.hku.cecid.edi.sfrm.handler.MessageStatusQueryHandler;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.module.ActiveTaskModule;
import hk.hku.cecid.piazza.commons.module.LimitedActiveTaskList;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

/** 
 * The outgoing segment payloads collector collect all segmented 
 * payload at the database which is ready for 
 * sending to receiver. (status: PS).<br><br>
 * 
 * Creation Date: 25/10/2006<br><br>
 * 
 * @author Twinsen Tsang
 * @version 1.0.2
 * @since	1.0.1
 */
public class OutgoingSegmentCollector extends LimitedActiveTaskList {
		
	private boolean isFirstLoad = true;	
	
	/**
	 * The partnership DVO that query from the last time the collector execute.  
	 */
	private SFRMPartnershipDVO lastQueryPDVO;

	/**
	 * The message DVO that query from the last time the collector execute.  
	 */
	private SFRMMessageDVO lastQueryMsgDVO;
	
	/**
	 * The packaged payload working from the last time the collector execute.  
	 */
	private PackagedPayloads lastWorkingPayloads;
	
//	private long lastTimestamp = 0;
	
	private BandWidthOptimizer optimizer;
	
	private MessageStatusQueryHandler statusQueryHandler;
	
	private boolean initialized = false;
	
	/* How long this collector is said to be active */
	private long activeDuration = 120000;
	
	private long lastActiveTimestamp = -1;
	/**
	 * 
	 * 
	 * @return
	 */
	private boolean compareNullAndKey(Object src, Object srcKey, Object destKey){
		if (src == null || !srcKey.equals(destKey)) 
			return true;
		return false;
	}
	
	/**
	 * Get the list that contains <code>OutgoingSegmentPayloadsTask</code>
	 * transformed through <code>SFRMMessageSegmentDVO</code>.  
	 *  
	 * @param taskList
	 * 			The reference task list to insert.
	 * @param status
	 * 			The segment status you want to select.
	 * @param sgtType
	 * 			The segment type you want to select.
	 * @return
	 * 			The reference <code>taskList</code>
	 */
	private List getTaskList(ArrayList taskList, String status, String sgtType){		
		try{
			// Grab all message segments that are ready to send.
			Iterator itr = SFRMProcessor.getInstance().getMessageSegmentHandler().retrieveIncompleteSegments(
				SFRMConstant.MSGBOX_OUT, status, sgtType, this.getMaxTasksPerList()).iterator();
				
			// Get message and message segment handler.			
			String sType = null;		
			
			while(itr.hasNext()){	
				SFRMMessageSegmentDVO sgtDVO = (SFRMMessageSegmentDVO) itr.next();
				try{
					// Check whether the segment type is PAYLOAD.
					sType  = sgtDVO.getSegmentType();
					boolean isPayload = sType.equals(SFRMConstant.MSGT_PAYLOAD);
					
					// Retrieve the message if the last one is null or the message id is different.
					// If it is required to change, then the remaining two part 
					// (partnership, packed_payload) should also be changed. 
					String mID = sgtDVO.getMessageId();
					if (this.compareNullAndKey(
							this.lastQueryMsgDVO
						  , this.lastQueryMsgDVO != null ? this.lastQueryMsgDVO.getMessageId(): null
						  , mID)){
						// Update the last query msg DVO depending on it's segment type.
						this.lastQueryMsgDVO = SFRMProcessor.getInstance().getMessageHandler()
							.retrieveMessage(
								sgtDVO.getMessageId(), 
								isPayload ? SFRMConstant.MSGBOX_OUT: SFRMConstant.MSGBOX_IN);
						// Check null
						if (this.lastQueryMsgDVO == null)
							throw new NullPointerException(
								"Missing Message Record for MID: " + mID);
											
						// Retrieve the new partnership.
						String pID = this.lastQueryMsgDVO.getPartnershipId();
						this.lastQueryPDVO 	= SFRMProcessor.getInstance().getPartnershipHandler()
							.retreivePartnership(pID, mID);
						
						/**
						 * @since 2.0.0 - Retrieve the single file properties, if it is single file
						 */
						String filename = lastQueryMsgDVO.getFilename();
						
						if (this.lastQueryPDVO == null)
							throw new NullPointerException(
								"Missing Partnership Record for PID: " + pID); 
						
						if (isPayload){												
							/**
							 * @since 2.0.0 added the code to retrieve filename fields also
							 */
							this.lastWorkingPayloads = (PackagedPayloads) SFRMProcessor.
							getInstance().getOutgoingRepository()
							.getPayload(new Object[]{ pID,  mID, filename }
									   ,PayloadsState.PLS_PROCESSING);
							
							if (this.lastWorkingPayloads == null)
								throw new IOException(
									" Missing Packaged Payload with partnership id: " + pID 
								   +" and message id: " + mID);
						}
						
						// Log information.
						SFRMProcessor.getInstance().getLogger().info(
							 SFRMLog.OSPTC_CALLER 
						   + "Switching working task to MSG id: " + mID 
						   +" and partnership id: " + pID);
					}											
					
					sgtDVO.setStatus(SFRMConstant.MSGS_PROCESSING);
					SFRMProcessor.getInstance().getMessageSegmentHandler().getDAOInstance().persist(sgtDVO);
					// Add a new payload task for each segmented payload.				
					taskList.add(new OutgoingSegmentTask(
						sgtDVO, this.lastQueryPDVO, this.lastQueryMsgDVO, this.lastWorkingPayloads));
				}
				// Using un-specified exception is acceptable.
				catch(Exception e){
					SFRMProcessor.getInstance().getLogger().error(SFRMLog.OSPTC_CALLER + "Error", e);
				}
			}					
		}catch(DAOException daoe){
			SFRMProcessor.getInstance().getLogger().error(SFRMLog.OSPTC_CALLER + "Unable to retrieve sgts from DB", daoe);
		// Catch any exception so that the task-list does not terminate under any condition.			
		}catch(Exception e){
			SFRMProcessor.getInstance().getLogger().error(SFRMLog.OSPTC_CALLER + "Unknown Error", e);
		}
		
		
		
		return taskList;
	}
		
	/**
	 * It get the set of payload directory from the segmented 
	 * payloads repository and pass to outgoing segmented 
	 * payload tasks for process.
	 * 
	 * @return A list of Outgoing segmented payloads task. 
	 */
	public List getTaskList() {		
		if(!initialized){
			initialize();
			initialized = true;
		}
		
		ArrayList taskList = new ArrayList();				
		if (this.isFirstLoad){
			SFRMProcessor.getInstance().getLogger().info(SFRMLog.OSPTC_CALLER + SFRMLog.FIRST_LOAD + " Resend PR and DL Segments");
			this.getTaskList(taskList, SFRMConstant.MSGS_PROCESSING, "%");
			this.getTaskList(taskList, SFRMConstant.MSGS_DELIVERED,  SFRMConstant.MSGT_PAYLOAD);
			this.isFirstLoad = false;
		}								
		
		// Get all pending segments for all segment type.  
		taskList = (ArrayList) this.getTaskList(taskList, SFRMConstant.MSGS_PENDING, "%");
		
		// For marking the execution interval
		//markTimestamp();
		
		//TODO: Remove this for testing purpose only
		
		double totalSpeed = statusQueryHandler.getTotalSpeed();
		
		if(optimizer.isFoundOptimized() && totalSpeed > 0.0){
			((ActiveTaskModule) this.getModule()).setExecutionInterval(optimizer.getOptimizedEI());			
		}
		
		if(!optimizer.isFoundOptimized()){
			optimizer.findMaxSpeed();
		}
		
		//TODO: End Remove this for testing purpose only
		
		//When there have a message segment to send, it is said to be active
		if(taskList.size() > 0){
			long currentTime = System.currentTimeMillis();
			long duration = currentTime - lastActiveTimestamp;
			//If this collector is waiting too long for sending the message segment, reset the bandwidth optimizer
			if(duration > activeDuration){
				optimizer.reset();
			}	
			lastActiveTimestamp = currentTime;
		}
		
		return taskList;
	}
	
	/*
	private void markTimestamp(){
		long currentTime = System.currentTimeMillis();
		long duration = currentTime - lastTimestamp;
		lastTimestamp = currentTime;
		SFRMProcessor.getInstance().getLogger().debug("segment collector interval:," + Long.toString(duration) + ",");
	}
	*/
	
	protected void init() throws Exception{
		super.init();
		Properties param = getParameters();
		long eiInc = StringUtilities.parseLong(param.getProperty("ei-delta"), 500);
		int maxRound = StringUtilities.parseInt(param.getProperty("max-round"), 20);
		long minEI = StringUtilities.parseLong(param.getProperty("min-ei"), 500);
		long maxEI = StringUtilities.parseLong(param.getProperty("max-ei"), 500);
		double speedGradientTolerance = StringUtilities.parseDouble(param.getProperty("speed-gradient-tolerance"), 100.0);
		double eiSpeedGradientTolerance = StringUtilities.parseDouble(param.getProperty("ei-speed-gradient-tolerance"), 50.0);
		
		activeDuration = StringUtilities.parseLong(param.getProperty("active-duration"), 120000);
		
		optimizer = new BandWidthOptimizer((ActiveTaskModule) this.getModule());
		optimizer.setEIIncrement(eiInc);
		optimizer.setMaxRound(maxRound);
		optimizer.setMinExecutionInterval(minEI);
		optimizer.setMaxExecutionInterval(maxEI);
		optimizer.setSpeedGradientTolerance(speedGradientTolerance);
		optimizer.setEISpeedGradientTolerance(eiSpeedGradientTolerance);
		 
	}
	private void initialize(){
		statusQueryHandler = SFRMProcessor.getInstance().getMessageSpeedQueryHandler();
	}
}
