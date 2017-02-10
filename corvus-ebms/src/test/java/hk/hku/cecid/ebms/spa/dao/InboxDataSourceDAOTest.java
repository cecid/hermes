package hk.hku.cecid.ebms.spa.dao;

import org.junit.Assert;
import org.junit.Test;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.test.DAOTest;

public class InboxDataSourceDAOTest extends DAOTest<InboxDataSourceDAO> {

	@Override
	public String getTableName() {
		return "inbox";
	}
	
	@Test
	public void testFindInboxNextOrderNo() throws DAOException {		
		InboxDataSourceDAO dao = super.getTestingTarget();

		long orderNo;
		orderNo = dao.findInboxNextOrderNo();
		Assert.assertEquals(1, orderNo);		

		InboxDVO dvo = (InboxDVO)dao.createDVO();
		dvo.setMessageId("20080311-155320-90407@192.168.0.1"); 
		dvo.setOrderNo(orderNo);
		dao.create(dvo);
		
		orderNo = dao.findInboxNextOrderNo();
		Assert.assertEquals(2, orderNo);		
	}
	
}
	