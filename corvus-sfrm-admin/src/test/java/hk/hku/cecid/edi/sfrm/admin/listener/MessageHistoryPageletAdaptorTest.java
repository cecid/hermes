package hk.hku.cecid.edi.sfrm.admin.listener;

import hk.hku.cecid.edi.sfrm.admin.listener.MessageHistoryPageletAdaptor;
import java.lang.reflect.Method;

import junit.framework.TestCase;

public class MessageHistoryPageletAdaptorTest extends TestCase {
	private MessageHistoryPageletAdaptor adaptor = new MessageHistoryPageletAdaptor();
	
	// Invoked for setup.
	public void setUp() throws Exception {
		System.out.println();
		System.out.println("---------" + this.getName() + " Start -------");
	}
	
	// Invoked for finalized.
	public void tearDown() throws Exception {
		System.out.println("---------" + this.getName() + " End   -------");
	}	
	
	public void testCheckEmptyAndReturnNull() throws Exception {
		Method m = Class.forName("hk.hku.cecid.edi.sfrm.admin.listener.MessageHistoryPageletAdaptor").
			getDeclaredMethod("checkEmptyAndReturnNull", new Class[] {String.class});
		m.setAccessible(true);
		TestCase.assertEquals("abcdefg", (String)m.invoke(adaptor, new Object[] {"abcdefg"}));
		TestCase.assertEquals(null, (String)m.invoke(adaptor, new Object[] {""}));
	}	
	
	public void testCheckNullAndReturnEmpty() throws Exception {
		Method m = Class.forName("hk.hku.cecid.edi.sfrm.admin.listener.MessageHistoryPageletAdaptor").
			getDeclaredMethod("checkNullAndReturnEmpty", new Class[] {String.class});
		m.setAccessible(true);
		TestCase.assertEquals("abcdefg", (String)m.invoke(adaptor, new Object[] {"abcdefg"}));
		TestCase.assertEquals("", (String)m.invoke(adaptor, new Object[] {null}));			
	}
	
	public void testCheckStarAndConvertToPercent() throws Exception {
		Method m = Class.forName("hk.hku.cecid.edi.sfrm.admin.listener.MessageHistoryPageletAdaptor").
		getDeclaredMethod("checkStarAndConvertToPercent", new Class[] {String.class});
		m.setAccessible(true);
		TestCase.assertEquals("abcdefg", (String)m.invoke(adaptor, new Object[] {"abcdefg"}));
		TestCase.assertEquals("%", (String)m.invoke(adaptor, new Object[] {null}));
		TestCase.assertEquals("%", (String)m.invoke(adaptor, new Object[] {""}));
		TestCase.assertEquals("%", (String)m.invoke(adaptor, new Object[] {"*"}));
		TestCase.assertEquals("abc\\_def", (String)m.invoke(adaptor, new Object[] {"abc_def"}));
		TestCase.assertEquals("abc\\%def", (String)m.invoke(adaptor, new Object[] {"abc%def"}));
		TestCase.assertEquals("abc%def", (String)m.invoke(adaptor, new Object[] {"abc*def"}));
		TestCase.assertEquals("\\%%\\_\\_%\\%", (String)m.invoke(adaptor, new Object[] {"%*__*%"}));
	}
	
//	public void testBuildMessageHistoryListNotDetail() throws Exception{
//		Method m = Class.forName("hk.hku.cecid.edi.sfrm.admin.listener.MessageHistoryPageletAdaptor").getDeclaredMethod("buildMessageHistoryList"
//				, new Class[]{Iterator.class, boolean.class, PropertyTree.class});
//		m.setAccessible(true);
//		SFRMMessageDAO dao = (SFRMMessageDSDAO)SFRMProcessor.core.dao.createDAO(SFRMMessageDSDAO.class);
//		SFRMMessageDVO queryDAO = (SFRMMessageDVO) dao.createDVO();
//		List dvos = dao.findMessagesByHistory(queryDAO, 20, 0);
//		PropertyTree dom = new PropertyTree();
//		m.invoke(adaptor, new Object[] {dvos.iterator(), false, dom});
//		
//		//Check the DOM object
//		TestCase.assertEquals();
//	}
}
