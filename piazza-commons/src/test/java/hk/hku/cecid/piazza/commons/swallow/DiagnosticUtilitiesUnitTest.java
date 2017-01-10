package hk.hku.cecid.piazza.commons.swallow;

import hk.hku.cecid.piazza.commons.swallow.DiagnosticUtilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.junit.Assert;

/** 
 * The <code>DiagnoisticUtilitiesUnitTest</code> is MANUAL unit test of <code>DiagnoisticUtilities</code>. 
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 */
public class DiagnosticUtilitiesUnitTest
{
	/**
	 * Test whether the singleton instance is instantiated successfully through {@link DiagnosticUtilities#getInstance()}
	 */
	@Test
	public void testGetInstance()
	{
		Assert.assertNotNull("The singleton of diagnostic utilities MUST not null", DiagnosticUtilities.getInstance());		
	}
	
	/**
	 * Test whether operation dumpAllThread throws NullPointerException when output stream is null.
	 */
	@Test(expected=NullPointerException.class)
	public void testDumpAllThreadWithNullStream() throws IOException
	{
		DiagnosticUtilities.getInstance().dumpAllThread((OutputStream)null, 0);
	}
	
	/**
	 * Test whether operation dumpAllThread throws IllegalArgumentException when stack trace depth is less than zero.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testDumpAllThreadWithIllegalStackTraceDepth() throws IOException
	{ 
		DiagnosticUtilities.getInstance().dumpAllThread(new ByteArrayOutputStream(), -1);
	}
	
	/**
	 * It is not an automated test-case. Just for manual diagnostic method.
	 */
	@Test
	public void testDumpAllThread()	
	{
		try
		{
			DiagnosticUtilities.getInstance().dumpAllThread();	
		}
		catch(IOException ioex)
		{
			ioex.printStackTrace();
		}
	}	
}
