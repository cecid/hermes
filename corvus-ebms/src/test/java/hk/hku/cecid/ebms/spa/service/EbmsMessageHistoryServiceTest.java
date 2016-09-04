/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.service;

import hk.hku.cecid.ebms.spa.handler.MessageClassifier;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class EbmsMessageHistoryServiceTest
{

	EbmsMessageHistoryService historyService = new EbmsMessageHistoryService();
		
	// Invoked for setup.
	@Before
	public void setUp() throws Exception {
	}
	
	// Invoked for finalized.
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void checkMessageStatus()throws Exception{
		
		Method m = Class.forName("hk.hku.cecid.ebms.spa.service.EbmsMessageHistoryService").
		getDeclaredMethod("checkMessageStatus", new Class[] {String.class});
		m.setAccessible(true);
		
		Assert.assertEquals(null, m.invoke(historyService, new Object[] {null}));
		
		Assert.assertEquals(MessageClassifier.INTERNAL_STATUS_DELIVERED,
											m.invoke(historyService, new Object[] {"DL"}));
		Assert.assertEquals(MessageClassifier.INTERNAL_STATUS_DELIVERY_FAILURE,
											m.invoke(historyService, new Object[] {"DF"}));
		Assert.assertEquals(MessageClassifier.INTERNAL_STATUS_PENDING,
												m.invoke(historyService, new Object[] {"PD"}));
		Assert.assertEquals(MessageClassifier.INTERNAL_STATUS_RECEIVED,
										m.invoke(historyService, new Object[] {"RC"}));
		Assert.assertEquals(MessageClassifier.INTERNAL_STATUS_PROCESSING,
										m.invoke(historyService, new Object[] {"PR"}));
		Assert.assertEquals(MessageClassifier.INTERNAL_STATUS_PROCESSED,
										m.invoke(historyService, new Object[] {"PS"}));
		Assert.assertEquals(MessageClassifier.INTERNAL_STATUS_PROCESSED_ERROR,
										m.invoke(historyService, new Object[] {"PE"}));		
	}
	
	@Test
	public void checkMessageStatus_Fail() throws Exception{
		Method m = Class.forName("hk.hku.cecid.ebms.spa.service.EbmsMessageHistoryService").
		getDeclaredMethod("checkMessageStatus", new Class[] {String.class});
		m.setAccessible(true);
		
		try{
			m.invoke(historyService, new Object[] {"ALL"});
			Assert.fail("Exception expected here, as message status was an invalid input.");
		}catch(Exception exp){
			Assert.assertEquals(SOAPRequestException.class, exp.getCause().getClass());
		}	
		
		try{
			m.invoke(historyService, new Object[] {"ABC"});
			Assert.fail("Exception expected here, as message status was an invalid input.");
		}catch(Exception exp){
			Assert.assertEquals(SOAPRequestException.class, exp.getCause().getClass());
		}
		
		try{
			m.invoke(historyService, new Object[] {"%"});
			Assert.fail("Exception expected here, as message status was an invalid input.");
		}catch(Exception exp){
			Assert.assertEquals(SOAPRequestException.class, exp.getCause().getClass());
		}
		
		try{
			m.invoke(historyService, new Object[] {""});
			Assert.fail("Exception expected here, as message status was an invalid input.");
		}catch(Exception exp){
			Assert.assertEquals(SOAPRequestException.class, exp.getCause().getClass());
		}
		
		try{
			m.invoke(historyService, new Object[] {"\\&&"});
			Assert.fail("Exception expected here, as message status was an invalid input.");
		}catch(Exception exp){
			Assert.assertEquals(SOAPRequestException.class, exp.getCause().getClass());
		}
	}
	
	@Test
	public void checkMessageBox()throws Exception{
		Method m = Class.forName("hk.hku.cecid.ebms.spa.service.EbmsMessageHistoryService").
		getDeclaredMethod("checkMessageBox", new Class[] {String.class});
		m.setAccessible(true);
		
		Assert.assertEquals(null, m.invoke(historyService, new Object[] {null}));
		
		Assert.assertEquals(MessageClassifier.MESSAGE_BOX_INBOX,
											m.invoke(historyService, new Object[] {"INBOX"}));
		Assert.assertEquals(MessageClassifier.MESSAGE_BOX_OUTBOX,
											m.invoke(historyService, new Object[] {"OUTBOX"}));
		
	}	
	@Test
	public void checkMessageBox_Fail()throws Exception{
		Method m = Class.forName("hk.hku.cecid.ebms.spa.service.EbmsMessageHistoryService").
		getDeclaredMethod("checkMessageBox", new Class[] {String.class});
		m.setAccessible(true);
				
		try{
			m.invoke(historyService, new Object[] {"ALL"});
			Assert.fail("Exception expected here, as message box was an invalid input.");
		}catch(Exception exp){
			Assert.assertEquals(SOAPRequestException.class, exp.getCause().getClass());
		}
		
		try{
			m.invoke(historyService, new Object[] {""});
			Assert.fail("Exception expected here, as message box was an invalid input.");
		}catch(Exception exp){
			Assert.assertEquals(SOAPRequestException.class, exp.getCause().getClass());
		}
		
		try{
			m.invoke(historyService, new Object[] {"%"});
			Assert.fail("Exception expected here, as message box was an invalid input.");
		}catch(Exception exp){
			Assert.assertEquals(SOAPRequestException.class, exp.getCause().getClass());
		}
		
		try{
			m.invoke(historyService, new Object[] {"&&&"});
			Assert.fail("Exception expected here, as message box was an invalid input.");
		}catch(Exception exp){
			Assert.assertEquals(SOAPRequestException.class, exp.getCause().getClass());
		}
		
		try{
			m.invoke(historyService, new Object[] {"\\**"});
			Assert.fail("Exception expected here, as message box was an invalid input.");
		}catch(Exception exp){
			Assert.assertEquals(SOAPRequestException.class, exp.getCause().getClass());
		}
		
		try{
			m.invoke(historyService, new Object[] {"MailBox"});
			Assert.fail("Exception expected here, as message box was an invalid input.");
		}catch(Exception exp){
			Assert.assertEquals(SOAPRequestException.class, exp.getCause().getClass());
		}
	}
}
