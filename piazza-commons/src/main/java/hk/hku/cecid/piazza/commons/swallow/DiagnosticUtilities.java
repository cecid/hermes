/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.swallow;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/** 
 * The <code>DiagnosticUtilities</code> is is a utility for providing useful information for monitoring and 
 * debugging the JVM. 
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 */
public class DiagnosticUtilities
{
	// The private singleton object.
	private static DiagnosticUtilities singleton;
	
	/**
	 * Get the single instance of <code>DiagnosticUtilities</code>.
	 * 
	 * @return the single instance of <code>DiagnosticUtilities</code>.
	 */
	public static DiagnosticUtilities getInstance()
	{
		synchronized(DiagnosticUtilities.class)
		{
			if (singleton == null) 
			{				
				/*
				 * RESERVED FOR DISCOVERY PATTERN
				 */
				singleton = new DiagnosticUtilities();
			}
		}		
		return singleton;
	}
	
	/**
	 * Get the new instance of <code>DiagnosticUtilities</code>.
	 * 
	 * @return the new instance of <code>DiagnosticUtilities</code>.
	 */
	public static DiagnosticUtilities getNewInstance()
	{		
		return new DiagnosticUtilities();
	}

	/**
	 * Dump all threads information from the current JVM.
	 */
	public void dumpAllThread() throws IOException
	{
		this.dumpAllThread(System.out, 3);
	}
	
	/**
	 * Dump all threads information from the current JVM to the specified OutputStream
	 * <code>os</code>.
	 * 
	 * @param os The output stream to dump the threads information. 
	 * @param stackTraceDepth The depth of stack trace to dump, default is 3. 
	 * @throws NullPointerException when <code>os</code> is null.
	 * @throws IllegalArgumentException when <code>stackTraceDepth</code> less than zero.
	 */
	public void dumpAllThread(OutputStream os, int stackTraceDepth) throws IOException
	{
		/*
		 * TODO: Support of dumping thread priority, is daemon? 
		 */
		if (os == null)
		{
			throw new NullPointerException("Missing 'os' in the arugments.");
		}
		
		os.write(this.dumpAllThread0(stackTraceDepth).getBytes());	
		os.flush();
	}
	
	/**
	 * Dump all threads information from the current JVM to the specified OutputStream
	 * <code>w</code>.
	 * 
	 * @param w The writer used to dump the threads information. 
	 * @param stackTraceDepth The depth of stack trace to dump, default is 3. 
	 * @throws NullPointerException when <code>os</code> is null.
	 * @throws IllegalArgumentException when <code>stackTraceDepth</code> less than zero.
	 */
	public void dumpAllThread(Writer w, int stackTraceDepth) throws IOException
	{
		/*
		 * TODO: Support of dumping thread priority, is daemon? 
		 */
		if (w == null)
		{
			throw new NullPointerException("Missing 'writer' in the arugments.");			
		}
		
		w.write(this.dumpAllThread0(stackTraceDepth));
		w.flush();	
	}
	
	/*
	 * Dump all threads information from the current JVM and return as String.
	 * 
	 * The threads information is on the basis of the JMX thread bean in the default 
	 * MBean server.  
	 * 
	 * @return a string containing all threads information.
	 */
	private String dumpAllThread0(int stackTraceDepth)
	{
		if (stackTraceDepth < 0)
		{
			throw new IllegalArgumentException("'stackTraceDepth' must greater than zero.");
		}
		
		StringBuilder whole = new StringBuilder();
		
		ThreadMXBean threadBeans = ManagementFactory.getThreadMXBean();
		
		long [] tids = threadBeans.getAllThreadIds();
		for (int i = 0; i < tids.length; i++)
		{
			// Collect threadInfo from the MXBean.
			ThreadInfo tinfo = threadBeans.getThreadInfo(tids[i], stackTraceDepth);
			
			/*
			 * Translate each information with prefix.
			 */
			String lock 		 = this.replaceEmpty("lk="      , tinfo.getLockName());		
			String lockOwnerName = this.replaceEmpty("lkowner=" , tinfo.getLockOwnerName());
			String lockId        = tinfo.getLockOwnerId() == -1 ? "" : String.valueOf(tinfo.getLockOwnerId());			
			String blockCount    = this.replaceEmpty("bkCount=" , Long.valueOf(tinfo.getBlockedCount()));
			String waitCount     = this.replaceEmpty("wtCount=" , Long.valueOf(tinfo.getWaitedCount()));			
			String state 		 = this.getResolvedThreadState(tinfo);			
			
			StringBuilder sb = new StringBuilder()
				.append("\""   + tinfo.getThreadName() + "\" ")
				.append("tid=")
				.append(tinfo.getThreadId())
				.append(" ")
				.append(state)
				.append(" ")
				.append(lock)
				.append(lockOwnerName)
				.append(lockId)
				.append(blockCount)				
				.append(waitCount)
				.append("\n");
						
			/*
			 * Output stack track element if exist.
			 */
			StackTraceElement[] sts = tinfo.getStackTrace();
			for (int j = 0; j < sts.length; j++)
			{
				sb.append(" [" + j + "] ")
				  .append(sts[j])
				  .append("\n");
			}		
			sb.append("\n");
			
			whole.append(sb);
		}
		return whole.toString();
	}
	
	
	private String replaceEmpty(String prefix, Object obj)
	{
		if (obj == null) return "";
		if (obj instanceof String)
		{
			return prefix + (String)obj + " ";
		}
		return prefix + obj.toString() + " ";		
	}
	
	/*
	 * Get a resolved thread state from a entry of ThreadInfo according 
	 * to the following syntax.
	 * 
	 * resolved state  = is thread in native code ? IS_IN_NATIVE(thread's state)
	 * or 
	 * resolved state  = is thread suspended ? SUSPENDED(thread's state)
	 * else
	 * resolved state  = thread state. 
	 */
	private String getResolvedThreadState(ThreadInfo tinfo)
	{
		String state = tinfo.getThreadState().toString();
		if (tinfo.isInNative()) 
		{
			state = "IS_IN_NATIVE(" + state + ")";
		}
		else if (tinfo.isSuspended())
		{
			state = "SUSPENDED(" + state + ")";
		}
		return state;
	}
	
	/*
	 * (FOR TESTING ONLY)
	 */
	public static void main(String[] args) throws IOException
	{
		DiagnosticUtilities.getInstance().dumpAllThread();
	}
}
