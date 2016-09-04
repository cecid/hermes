/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.admin.listener;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import hk.hku.cecid.ebms.admin.listener.MessageHistoryPageletAdaptorTest;;

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
			Method m = Class.forName("hk.hku.cecid.ebms.admin.listener.MessageHistoryPageletAdaptor").
				getDeclaredMethod("checkEmptyAndReturnNull", new Class[] {String.class});
			m.setAccessible(true);
			TestCase.assertEquals("abcdefg", (String)m.invoke(adaptor, new Object[] {"abcdefg"}));
			TestCase.assertEquals(null, (String)m.invoke(adaptor, new Object[] {""}));
		}	
		
		public void testCheckNullAndReturnEmpty() throws Exception {
			Method m = Class.forName("hk.hku.cecid.ebms.admin.listener.MessageHistoryPageletAdaptor").
				getDeclaredMethod("checkNullAndReturnEmpty", new Class[] {String.class});
			m.setAccessible(true);
			TestCase.assertEquals("abcdefg", (String)m.invoke(adaptor, new Object[] {"abcdefg"}));
			TestCase.assertEquals("", (String)m.invoke(adaptor, new Object[] {null}));			
		}
		
		public void testCheckStarAndConvertToPercent() throws Exception {
			Method m = Class.forName("hk.hku.cecid.ebms.admin.listener.MessageHistoryPageletAdaptor").
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
}
