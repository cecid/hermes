package hk.hku.cecid.edi.sfrm.handler;

import junit.framework.TestCase;

import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;

/**
 * The test-case of <code>SFRMDoSHandler</code>.
 * 
 * Creation Date: 03/07/2007
 * 
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10606
 */
public class SFRMDoSHandlerTest extends TestCase {
	
	// The set of sample message. 
	private SFRMMessage input0;	
	private SFRMMessage input1;
	
	// The set of sample message id;
	private String sampleMessageId0 = "test0@message-id";
	private String sampleMessageId1 = "test1@message-id";
	
	// The test target.
	private SFRMDoSHandler doSHandler;
	
	/**
	 * Invoke for setup.
	 */
	protected void setUp() throws Exception {
		super.setUp();
		doSHandler = new SFRMDoSHandler();
		
		input0 = new SFRMMessage();
		input0.setMessageID(sampleMessageId0);
		input0.setSegmentNo(0);
		input0.setSegmentType("PAYLOAD");	
		
		input1 = new SFRMMessage();
		input1.setMessageID(sampleMessageId1);
		input1.setSegmentNo(1);
		input1.setSegmentType("RECEIPT");
	}

	/**
	 * Simplest Testcase for a message entering the DoS Handler.
	 */
	public void testEnter_Pos_0(){		
		TestCase.assertEquals(true, doSHandler.enter(input0));
	}		
	
	/**
	 * Testcase for multiple message entering the DoS Handler.  
	 */
	public void testEnter_Pos_1(){
		TestCase.assertEquals(true, doSHandler.enter(input0));
		TestCase.assertEquals(true, doSHandler.enter(input1));
	}
	
	/**
	 * Testcase for duplicate message entering the DosHandler.
	 */
	public void testEnter_Neg_0(){
		TestCase.assertEquals(true , doSHandler.enter(input0));
		// Duplicate message record should return false.
		TestCase.assertEquals(false, doSHandler.enter(input0));
	}

	/**
	 * Testcase for a message entering the DoSHandler with a
	 * specified lifetime. The record should be removed 
	 * after at least (1 + sweeping_interval) seconds. 
	 */
	public void testEnterWithLifetime_Pos_0(){
		// Expire after 1 second.
		TestCase.assertEquals(true , doSHandler.enter(input0, 1000));
		// At least one timed-out sweeping has been done after 6 seconds.
		try{ Thread.sleep(6000); }catch(Exception ex){}
		
		TestCase.assertEquals(false, doSHandler.exit (input0));
	}
	
	/**
	 * Testcase for a message entering the DosHandler with a specified
	 * lifetime. The record should exist within the lifetime.
	 */
	public void testEnterWithLifetime_Pos_1(){
		// Expire after 1 second.
		TestCase.assertEquals(true , doSHandler.enter(input0, 1000));
		// At least one timed-out sweeping has been done after 6 seconds.
		try{ Thread.sleep(500); }catch(Exception ex){}
		
		TestCase.assertEquals(true, doSHandler.exit (input0));
	}
	
	/**
	 * Testcase for entering the message after previous thread owner
	 * has been died. 
	 */
	public void testEnterAfterThreadDie_Pos_0(){
		// Mark self reference.
		final SFRMDoSHandlerTest owner = this;
		// Create a dummy thread for enter a message into DoSHandler.		
		Thread testThread = new Thread(new Runnable(){
			public void run(){
				try{
					TestCase.assertEquals(true, owner.doSHandler.enter(owner.input0));
					Thread.sleep(1000);
				}catch(Exception ex){}
			}
		});
		// Start the testing thread.
		testThread.start();
		
		// Wait the test thread die.
		try{ testThread.join();	}catch(Exception ex){}
		
		// The testing thread has died, it should allow input0 enter again.
		TestCase.assertEquals(true, doSHandler.enter(input0));
	}
	
	/**
	 * Testcase for entering the message being owner by other 
	 * active thread.
	 */
	public void testEnterAfterThreadDie_Pos_1(){
		// Mark self reference.
		final SFRMDoSHandlerTest owner = this;
		// Create a dummy thread for enter a message into DoSHandler.		
		Thread testThread = new Thread(new Runnable(){
			public void run(){
				try{
					TestCase.assertEquals(true, owner.doSHandler.enter(owner.input0));
					Thread.sleep(1000);
				}catch(Exception ex){}
			}
		});
		// Start the testing thread.
		testThread.start();
		
		try{ Thread.sleep(500);	}catch(Exception ex){}
		
		// The testing thread has not died, it should not allow input0 enter again.
		TestCase.assertEquals(false, doSHandler.enter(input0));
	}
	
	/**
	 * Testcase for entering the message after previous thread owner
	 * has been interrupted. 
	 */
	// Commented testEnterAfterThreadDie_Pos_2 because it's hard to predict alive time after the thread is interrupted, it's also environment dependent.
	/*
	public void testEnterAfterThreadDie_Pos_2(){
		// Mark self reference.
		final SFRMDoSHandlerTest owner = this;
		// Create a dummy thread for enter a message into DoSHandler.		
		Thread testThread = new Thread(new Runnable(){
			public void run(){
				try{
					TestCase.assertEquals(true, owner.doSHandler.enter(owner.input0));
					// Guarantee the thread is alive when being interrupted by main thread.
					Thread.sleep(10000);
				}catch(Exception ex){}
			}
		});
		// Start the testing thread.
		testThread.start();
		
		try{ Thread.sleep(500); testThread.interrupt(); }catch(Exception ex){}
		//Since the thread cannot be interrupt, when the thread is sleeping, so it still alive
		//and cannot enter the barrier
		TestCase.assertEquals(false, doSHandler.enter(input0));
	}
	*/
	
	/**
	 * Testcase for entering and exiting for one record.
	 */
	public void testExit_Pos_0(){
		TestCase.assertEquals(true, doSHandler.enter(input0));
		TestCase.assertEquals(true, doSHandler.exit (input0));
	}
	
	/**
	 * Testcase for entering one record, but with a illegal message
	 * exiting.
	 */
	public void testExit_Neg_0(){
		TestCase.assertEquals(true , doSHandler.enter(input0));
		TestCase.assertEquals(false, doSHandler.exit (input1));		
	}		
}
