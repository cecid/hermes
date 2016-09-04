package hk.hku.cecid.edi.sfrm.task;

import java.util.List;
import java.util.ArrayList;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.module.LimitedActiveTaskList;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;
import hk.hku.cecid.edi.sfrm.handler.SFRMPartnershipHandler;

/**
 * @author Patrick Yip
 * @since 2.0.0
 */

/* (non-Javadoc)
 * @see hk.hku.cecid.piazza.commons.module.ActiveTaskList#getTaskList()
 */
public class AcknowledgementCollector extends LimitedActiveTaskList {

	@Override
	public List getTaskList(){		
		ArrayList taskList = new ArrayList();
		SFRMPartnershipHandler pHandler = SFRMProcessor.getInstance().getPartnershipHandler();
		try{
			List ackMessageDVOs = SFRMProcessor.getInstance().getMessageHandler().retrieveAcknowledgmentMessages(this.getMaxTasksPerList(), 0);
			for(int i=0 ; ackMessageDVOs.size() > i ; i++){
				SFRMMessageDVO mDVO = (SFRMMessageDVO) ackMessageDVOs.get(i);
				SFRMPartnershipDVO pDVO = pHandler.retreivePartnership(mDVO.getPartnershipId(), mDVO.getMessageId());
				taskList.add(new AcknowledgementTask(mDVO, pDVO));
			}
		}catch(DAOException daoe){
			SFRMProcessor.getInstance().getLogger().error("Fail on querying the SFRM database", daoe);
		}
		return taskList;
	}
}