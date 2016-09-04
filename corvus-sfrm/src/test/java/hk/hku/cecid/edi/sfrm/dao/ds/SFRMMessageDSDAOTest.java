package hk.hku.cecid.edi.sfrm.dao.ds;

import hk.hku.cecid.piazza.commons.test.DAOTest;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import org.junit.Assert;
import org.junit.Test;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Test cases for SFRMMessageDAO
 * @author Patrick Yip
 * @since 2.0.0
 */
public class SFRMMessageDSDAOTest extends DAOTest<SFRMMessageDSDAO> {
	
	/* (non-Javadoc)
	 * @see hk.hku.cecid.piazza.commons.test.DAOTest#getTableName()
	 */
	@Override
	public String getTableName() {
		return "message";
	}
	
	@Test
	public void testFindMessageForAcknowledgement() throws DAOException{
		SFRMMessageDSDAO dao = super.getTestingTarget();
		List results = dao.findMessageForAcknowledgement(10, 0);
		
		ArrayList msgs = new ArrayList();
		for(int i=0; results.size() > i; i++){
			msgs.add(((SFRMMessageDVO) results.get(i)).getMessageId());
		}
		
		assertMessageinList(results, "A", true);
		assertMessageinList(results, "B", true);
		assertMessageinList(results, "C", true);
		assertMessageinList(results, "D", true);
		assertMessageinList(results, "E", true);
		assertMessageinList(results, "F", false);				
	}
	
	private void assertMessageinList(List results, String messageId, boolean isInList){
		boolean found = false;
		for(int i=0 ; results.size()>i ; i++){
			SFRMMessageDVO mDVO = (SFRMMessageDVO) results.get(i);
			if(mDVO.getMessageId().equals(messageId)){
				found = true;
				break;
			}
		}
		Assert.assertEquals("Message exist in list should " + Boolean.toString(isInList), isInList, found);
	}

}
