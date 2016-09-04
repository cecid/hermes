package hk.hku.cecid.edi.sfrm.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageSegmentDAO;
import hk.hku.cecid.edi.sfrm.handler.SFRMMessageHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMMessageSegmentHandler;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessageException;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.piazza.commons.dao.DAOException;

/**
 * @author Patrick Yip
 * @version 2.0.0
 * @since 2.0.0
 * Estimate the network speed of sending message by querying how many number of segment was sent to the client
 */
public class StatusQuery{
	
	final private String messageId;
	final private SFRMMessageSegmentDAO dao;
	private boolean isRunning = false;
	private List<String> statusToCheck;
	private long startTime;
	private String statusDesc;
	private String status;
	private int numOfSegments = -1;
	private int numOfProcessedSegments = -1; 
	private Timestamp lastUpdatedTime;
	private long segmentSize = -1;
	private double currentSpeed = 0.0;
	
	//Estimated time in second when the sending payload can finished
	private int estimatedTime = -1;
	
	public StatusQuery(String messageId, SFRMMessageSegmentDAO dao){
		this.messageId = messageId;
		this.dao = dao;
	}
	
	public void init() throws Exception{
		statusToCheck = new ArrayList<String>();
		statusToCheck.add(SFRMConstant.MSGS_DELIVERED);
		statusToCheck.add(SFRMConstant.MSGS_PROCESSED);
		
		//Init the value related to this message ID
		SFRMMessageHandler mHandle = SFRMProcessor.getInstance().getMessageHandler();
		SFRMMessageDVO mDVO = mHandle.retrieveMessage(messageId, SFRMConstant.MSGBOX_OUT);
		if(mDVO != null){
			numOfSegments = mDVO.getTotalSegment();
			segmentSize = mDVO.getTotalSize() / numOfSegments;
		}else{
			throw new SFRMMessageException("Message ID: " + messageId + " not found");
		}
	}

	/* 
	 * @see hk.hku.cecid.edi.sfrm.com.SpeedQuery#start()
	 */
	
	public void start() {
		isRunning = true;
		startTime = System.currentTimeMillis();
	}

	/* 
	 * @see hk.hku.cecid.edi.sfrm.com.SpeedQuery#stop()
	 */
	
	public void stop() {
		isRunning = false;
	}
	
	public String getMessageId(){
		return messageId;
	}
	
	public void tick() throws DAOException{
		//Tick the speed if the speed query was started to run
		if(isRunning){
			updateCurrentSpeed();
			updateProgress();
			updateEstimatedTime();
		}
	}
	
	public void updateEstimatedTime(){
		int es = (int)(((double) (segmentSize *  (numOfSegments - numOfProcessedSegments))/1024) / currentSpeed);
		estimatedTime = es;
	}
	
	public void updateCurrentSpeed(){
		try {
			long sentBytes = dao.findNumOfBytesSentByMessageIdAndBoxAndTypeAndStatues(messageId, SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, startTime, statusToCheck);
			long ctm = System.currentTimeMillis();
			long elapsed = ctm - startTime;
			//To prevent the divide by zero error
			if(elapsed != 0 && sentBytes != 0){
				currentSpeed = (sentBytes/1024) / (elapsed/1000);
				SFRMProcessor.getInstance().getLogger().debug("Speed:, "+ Long.toString(ctm) +","+ Long.toString(elapsed) +"," + Double.toString(currentSpeed) + ", kb/s");
			}
		} catch (DAOException e) {
			SFRMProcessor.getInstance().getLogger().error("Failure on querying the network speed", e);
		}
	}
	
	public void updateProgress() throws DAOException{
		SFRMMessageHandler mHandle = SFRMProcessor.getInstance().getMessageHandler();
		SFRMMessageSegmentHandler msHandle = SFRMProcessor.getInstance().getMessageSegmentHandler();
		
		// Extract the SFRM message by the web services parameter
		SFRMMessageDVO mDVO = mHandle.retrieveMessage(messageId, SFRMConstant.MSGBOX_OUT);		
				
		if (mDVO != null){
			status 		 = mDVO.getStatus();
			statusDesc 	 = mDVO.getStatusDescription();			

			// Extract number of processed segments.			
			numOfProcessedSegments = msHandle.retrieveMessageSegmentCount(
					messageId, SFRMConstant.MSGBOX_OUT,	SFRMConstant.MSGT_PAYLOAD, SFRMConstant.MSGS_PROCESSED);
			
			// Extract the last update timestamp.
			lastUpdatedTime  = msHandle.retrieveLastUpdatedTimestamp(
					messageId, SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD);
			
			/*
			 * The column of number of segments present in the SFRM message 
			 * is not entered until the status changes to MSGS_PACKAGING.
			 * so it will use Integer.MAX_VALUE as undetermined.
			 */
//			if (status.equals(SFRMConstant.MSGS_HANDSHAKING))
//				numOfSegments = Integer.MAX_VALUE;
						
			/*
			 * The last updated timestamp may be NULL for message segment if the 
			 * SFRM message is in the stage of HS, PK, PKD and ST. So it uses the 
			 * SFRM message proceeding timestamp instead of last segment timestamp.
			 * If the proceeding timestamp is NULL also, it uses the creation 
			 * timestamp of the message.      
			 */  
			if (lastUpdatedTime == null){
				lastUpdatedTime = mDVO.getProceedTimestamp();
				if (lastUpdatedTime == null)
					lastUpdatedTime = mDVO.getCreatedTimestamp();
					// Unknown situation, mark it timeout immediately.
					if (lastUpdatedTime == null)
						lastUpdatedTime = new Timestamp(System.currentTimeMillis());
			}
		}
	}
	
	public void updateCurrentSpeedFromMsg(){
		SFRMMessageHandler mHandle = SFRMProcessor.getInstance().getMessageHandler();
		try{
			SFRMMessageDVO mDVO = mHandle.retrieveMessage(messageId, SFRMConstant.MSGBOX_OUT);
			long elapsed = mDVO.getCompletedTimestamp().getTime() - mDVO.getCreatedTimestamp().getTime();
			long size = mDVO.getTotalSize();
			currentSpeed = (double)((size/1024) / (elapsed/1000));
		}catch (DAOException e) {
			SFRMProcessor.getInstance().getLogger().error("Failure on querying the network speed", e);
		}
	}
	
	public String getStatus(){
		return status;
	}
	
	public String getStatusDesc(){
		return statusDesc;
	}

	public int getNumOfSegments() {
		return numOfSegments;
	}

	public int getNumOfProcessedSegments() {
		return numOfProcessedSegments;
	}
	
	public Timestamp getLastUpdatedTime(){
		return lastUpdatedTime;
	}
	
	/* 
	 * @see hk.hku.cecid.edi.sfrm.com.SpeedQuery#getCurrentSpeed()
	 */
	
	public double getCurrentSpeed() {
		return currentSpeed;
	}
	
	public long getSegmentSize(){
		return segmentSize;
	}
	
	public String toString(){
		String msg = "Message ID: " + messageId + "\n" +
						"Status: " + status + "\n" +
						"Status Desc: " + statusDesc + "\n" +
						"numOfSegments: " + Integer.toString(numOfSegments) + "\n" + 
						"Num PS segments: " + Integer.toString(numOfProcessedSegments) + "\n" +
						"Current speed: " + Double.toString(currentSpeed) + "\n" +
						"Estimated Time: " + Long.toString(estimatedTime) + "\n" + 
						"Last Update time: " + lastUpdatedTime;
		return msg;
	}

	public int getEstimatedTime() {
		return estimatedTime;
	}
}
