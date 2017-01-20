package hk.hku.cecid.piazza.commons.test.asserts;

import java.text.MessageFormat;

/**
 * 
 * The <code>BasicAsserts</code> is 
 *
 * @author 	Twinsen
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 *
 */
public class BasicAsserts
{	
	private static BasicAsserts singleton = new BasicAsserts();
	
	/*
	 * Private constructor to prevent singleton pattern break.
	 */
	private BasicAsserts()
	{	
	}
	
	/*
	 * Get the singleton assertion object.
	 */
	private static BasicAsserts getInstance()
	{
		return singleton;
	}
		
	/**
	 * 
	 * @param message
	 * @param expected
	 * @param actual
	 */
	private void assertWrappedThrowableEquals0(String message, Class<? extends Throwable> expected, Throwable actual)
	{
		if (message == null)
		{
			message = "wrapped throwable does not equal ";
		}		
		if (actual == null)
		{
			throw new AssertionError(MessageFormat.format(AssertsUtilities.INPUT_REQUIRED, "throwable:actual"));
		}
		if (expected == null)
		{
			throw new AssertionError(MessageFormat.format(AssertsUtilities.INPUT_REQUIRED, "throwable:expected"));
		}
		
		Throwable wrappedThrowable = actual.getCause();
		
		if (wrappedThrowable == null)
		{
			throw new AssertionError(MessageFormat.format(AssertsUtilities.INPUT_REQUIRED, "wrapped throwable"));
		}
		
		if (!wrappedThrowable.getClass().equals(expected))
		{ 
			throw new AssertionError(AssertsUtilities.buildExpectationError(message, expected, actual));
		}
	}
	
	/**
	 * 
	 * @param expected
	 * @param actual
	 */
	public static void assertWrappedThrowableEquals(Class<? extends Throwable> expected, Throwable actual)
	{
		BasicAsserts.getInstance().assertWrappedThrowableEquals0(null, expected, actual);
	}
	
	/**
	 * 
	 * @param message
	 * @param expected
	 * @param actual
	 */
	public static void assertWrappedThrowableEquals(String message, Class<? extends Throwable> expected, Throwable actual)
	{
		BasicAsserts.getInstance().assertWrappedThrowableEquals0(message, expected, actual);
	}
}
