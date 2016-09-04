/**
 * Contains the set of active module collector for 
 * handling packaging, segmenting, sending, joining and unpacking of payloads.
 */
package hk.hku.cecid.edi.sfrm.task;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant; 
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.spa.SFRMLog;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.module.LimitedActiveTaskList;

import hk.hku.cecid.edi.sfrm.com.PackagedPayloads;
import hk.hku.cecid.edi.sfrm.com.PackagedPayloadsRepository;
import hk.hku.cecid.edi.sfrm.com.PayloadsState;

import hk.hku.cecid.edi.sfrm.handler.SFRMMessageHandler;
import hk.hku.cecid.edi.sfrm.handler.SFRMPartnershipHandler;
/**
 * The outgoing message payloads collector collects all packaged
 * message from the DB with the associated payload which then
 * create DB segments for send.<br><br>
 * 
 * The looking query for packaged message.
 * <PRE><code>
 * 		select * from sfrm_message where message_box = ? and status = ?
 * </code></PRE>
 * 
 * Creation Date: 5/10/2006.<br><br>
 * 
 * @author Twinsen Tsang
 * @version 1.0.4
 * @since	1.0.1
 * 
 * @version 2.0.0
 * @since	1.0.4 
 */
public class OutgoingPayloadsCollector extends LimitedActiveTaskList {
	   
	private boolean isFirstLoad = true;
	
	/**
	 * Get the list that contains <code>OutgoingPackagedPayloadTask</code>
	 * transformed through <code>SFRMMessageDVO</code>.  
	 *  
	 * @param taskList
	 * 			The reference task list to insert.
	 * @param status
	 * 			The status you want to query.
	 * @return
	 * 			The reference <code>taskList</code>
	 * @throws IOException 
	 * @throws DAOException 
	 */	
	private List getTaskListByStatus(ArrayList taskList, String status) throws IOException, DAOException{
		int maxTasks = this.getMaxTasksPerList();				
		int packedCount = 0;
		SFRMPartnershipHandler pHandler = SFRMProcessor.getInstance().getPartnershipHandler();
		//Scan the packaged payload repository to find the packaged payload which is ready to send 
		if(status == SFRMConstant.MSGS_PACKAGED){
			Iterator packadPayloadIter = ((PackagedPayloadsRepository)SFRMProcessor.getInstance().getOutgoingRepository()).getReadyPayloads().iterator();
			SFRMPartnershipDVO pDVO = null;
			while(packadPayloadIter.hasNext() && packedCount < maxTasks){
				PackagedPayloads packedPayload = (PackagedPayloads) packadPayloadIter.next();
				String pId = packedPayload.getPartnershipId();
				String mId = packedPayload.getRefMessageId();
				try{
					if(pDVO == null || !pDVO.getPartnershipId().equals(pId))
						pDVO = pHandler.retreivePartnership(pId, mId);
					
					if(packedPayload.setToProcessing() == false){
						SFRMProcessor.getInstance().getLogger().error(SFRMLog.OPTC_CALLER + "Cannot rename the packaged payload " + packedPayload.getOriginalRootname() + " to processing");
						continue;
					}
					
					taskList.add(new OutgoingPayloadsTask(packedPayload, pDVO, status));
				}catch(DAOException daoe){
					SFRMProcessor.getInstance().getLogger().error(SFRMLog.OPTC_CALLER + "Missing Partnership", daoe);
					continue;
				}catch(Exception e){
					SFRMProcessor.getInstance().getLogger().error(SFRMLog.OPTC_CALLER + "Unknown Error", e);
				}
				
				packedCount++ ;
			}
		}
		
		//Find the message that is segmenting in the previous tomcat session, but not finished
		if(status == SFRMConstant.MSGS_SEGMENTING){
			SFRMMessageHandler mHandler = null;
			Iterator segmentingMsgIterator = null;
			try{
				mHandler = SFRMProcessor.getInstance().getMessageHandler();
				segmentingMsgIterator = mHandler.retrieveMessages(SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGS_SEGMENTING).iterator();
			}catch(Exception e){
				e.printStackTrace();
			}
			SFRMPartnershipDVO pDVO = null;
			while(segmentingMsgIterator.hasNext()){
				SFRMMessageDVO mDVO = (SFRMMessageDVO)segmentingMsgIterator.next();
				try{
					if(pDVO == null || !pDVO.getPartnershipId().equals(mDVO.getPartnershipId()))
						pDVO = pHandler.retreivePartnership(mDVO.getPartnershipId(), mDVO.getMessageId());
					PackagedPayloads pp = (PackagedPayloads) SFRMProcessor.getInstance().getOutgoingRepository().getPayload(
							new Object[]{mDVO.getPartnershipId(), mDVO.getMessageId(), mDVO.getFilename()},PayloadsState.PLS_PROCESSING);
					taskList.add(new OutgoingPayloadsTask(pp, pDVO, status));
				}catch(DAOException daoe){
					SFRMProcessor.getInstance().getLogger().error(SFRMLog.OPTC_CALLER + "Missing Partnership", daoe);
				}catch(Exception e){
					SFRMProcessor.getInstance().getLogger().error(SFRMLog.OPTC_CALLER + "Unknown Error", e);
				}
			}
		}
		
		return taskList;
	}
	
    /**
	 * It get the set of payload directory from the packaged 
	 * payloads repository and pass to outgoing message 
	 * payload tasks for process.
	 * 
	 * @return A list of Outgoing message payload task. 
	 */
	public List getTaskList() {
		ArrayList taskList = new ArrayList();
		try{
			if (isFirstLoad){ 
				SFRMProcessor.getInstance().getLogger().info(SFRMLog.OPTC_CALLER + SFRMLog.FIRST_LOAD + " Redo ST message");
				this.getTaskListByStatus(taskList, SFRMConstant.MSGS_SEGMENTING);
				isFirstLoad = false; 
			}
			getTaskListByStatus(taskList, SFRMConstant.MSGS_PACKAGED);
		}catch(IOException ioe){
			SFRMProcessor.getInstance().getLogger().error(SFRMLog.OPTC_CALLER + "Unable to retrieve packaged payloads", ioe);
		}catch(DAOException daoe){
			SFRMProcessor.getInstance().getLogger().error(SFRMLog.OPTC_CALLER + "Unable to retrieve DVO", daoe);
		}
				
		return taskList;
	}
}
