/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.sfrm.util;

import hk.hku.cecid.edi.sfrm.util.TimedOutEntryListener;
import hk.hku.cecid.edi.sfrm.util.TimedOutHashTable;
import junit.framework.TestCase;

/**
 * The test case for <code>TimedOutHashTable</code>.
 *  
 * Creation Date: 25/6/2007
 * 
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10606
 */
public class TimedOutHashTableTest extends TestCase {
	
	// The testing target.
	private TimedOutHashTable tht;
	
	// The default timeout.
	private static long DEFAULT_TIMEOUT = 5000;
			
	/**
	 * The simple timed out entry listener which print the timed out  
	 * entry to system console.  
	 */
	public static class SimpleTimedOutEntryListener implements TimedOutEntryListener {

		// @see hk.hku.cecid.piazza.commons.util.TimedOutEntryListener#timeOut (java.lang.Object, java.lang.Object)
		public void timeOut(Object key, Object value) {			
			System.out.println("Key: " + key + " value: " + value + " has been timed-out.");
		}		
	}

	/**
	 * Setup for testing.
	 */
	public void setUp() throws Exception {
		// Create the timeout ht.
		this.tht = new TimedOutHashTable(DEFAULT_TIMEOUT);
		// Add a console output timeout listener
		this.tht.setListener(new TimedOutHashTableTest.SimpleTimedOutEntryListener());
		
		// Console output for enclosing each testcase.
		System.out.println();
		System.out.println("-----" + this.getName() + " Start -----");		
	}
		
	/**
	 * Invoked for completing one testcase 
	 */
	public void tearDown() throws Exception {
		// Finalize the Hashtable.
		this.tht.complete();
		
		System.out.println("-----" + this.getName() + " End -----");
	}


	/**
	 * Test for simple and normal scenario for putting object. 
	 */
	public void testPut() throws Exception {
		this.tht.put("test", new Object());
	}
	
	/**
	 * Test for existence of the putting object using method <code>contain</code>.   
	 */
	public void testContain() throws Exception {
		Object obj = new Object();
		this.tht.put("test", obj);
		// Assertion		
		TestCase.assertTrue("'contain' Test failure", this.tht.contains(obj));		
	}
	
	/**
	 * Test for existence of the putting object using method <code>containValue</code>. 
	 */
	public void testContainValue() throws Exception {
		Object obj = new Object();
		this.tht.put("test", obj);
		// Assertion
		TestCase.assertTrue("'containValue' Test failure", this.tht.containsValue(obj));
	}
	
	/**
	 * Test for getting object from the hashtable using <code>get</code> 
	 */
	public void testGet() throws Exception {		
		this.tht.put("test", "obj");
		String ret = (String) this.tht.get("test");
		// Assertion		
		TestCase.assertTrue("'get' Test failure", ret.equals("obj")); 
	}
	
	/**
	 * Test for putting a object with time out specific and see whether 
	 * it will timeout after maximum time [sweeping_interval + timeout value].
	 */
	public void testPutWithTimeout() throws Exception {
		this.tht.put("test", new Object(), 2500);
		
		int stime = (int)this.tht.getSweepInterval() + 2500;
		
		try{ Thread.sleep(stime); }catch (Exception e){} // At-least sweeping out one round.
		
		Object obj = this.tht.get("test");		
		// Assertion
		TestCase.assertNull(obj);
	}
	
	/**
	 * Test for putting multiple object with time out specified and see whether
	 * all records are swept out after [sweeping_interval + timeout value].
	 */
	public void testMultiplePutWithTimeout() throws Exception {
		this.tht.put("test0", new Object(), 1000);
		this.tht.put("test1", new Object(), 2000);
		this.tht.put("test2", new Object(), 3000);
		
		int stime = (int)this.tht.getSweepInterval() + 3500;
		
		try{ Thread.sleep(stime); }catch (Exception e){} // At-least sweeping out one round.
		
		TestCase.assertTrue("'multiplePutWithTimeout' Test failure", (this.tht.size() == 0));		
	}
			
	/**
	 * Test for putting multiple object with some time out specified while 
	 * some don't. It then check whether some records are swept out after
	 * [sweeping_interval + timeout value]. 
	 */
	public void testMultiplePutWithMixed() throws Exception {
		this.tht.put("test0", new Object(), 1000);
		this.tht.put("test1", new Object(), 2000);
		this.tht.put("test2", new Object(), 8000);
		this.tht.put("test3", new Object());
		
		int stime = (int)this.tht.getSweepInterval() + 2500; 
		
		try{ Thread.sleep(stime); }catch (Exception e){} // At-least sweeping out one round.
		
		TestCase.assertTrue("'multiplePutWithMixed' Test failure", (this.tht.size() == 2));				
	}
	
	/**
	 * Test for setting the customized listener for handling timeout entry.
	 */
	public void testSetListener() throws Exception {
		final int [] arrRes = {0}; 
		// Setup the customized listener
		this.tht.setListener(new TimedOutEntryListener(){
			public void timeOut(Object key, Object value) {
				// dirty successful flag.
				System.out.println("Key: " + key + " value: " + value + " has been timed-out.");
				arrRes[0] = 1;
			}		 
		});		
		this.tht.put("test0", new Object(), 1000);
		
		int stime = (int)this.tht.getSweepInterval() + 1500; 
		
		try{ Thread.sleep(stime); }catch (Exception e){} // At-least sweeping out one round.
		
		TestCase.assertTrue("'setListener' Test failure", (arrRes[0] == 1));						
	}
}
