package hk.hku.cecid.piazza.commons.test.asserts;

import java.text.MessageFormat;

/** 
 * The <code>AssertsUtilities</code> is 
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 */
public class AssertsUtilities
{
	public static final String INPUT_REQUIRED  = "Missing required <{0}> arguments.";
	
	public static final String EXPECTED_ERROR  = "{0} expected <{1}>, actual <{2}>";
	
	private static AssertsUtilities singleton = new AssertsUtilities();
	
	/*
	 * Private constructor to prevent singleton pattern break.
	 */
	private AssertsUtilities()
	{	
	}
	
	/*
	 * Get the singleton assertion object.
	 */
	private static AssertsUtilities getInstance()
	{
		return singleton;
	}
	
	/**
	 * 
	 * @param message
	 * @param expected
	 * @param actual
	 * @return
	 */
	private String buildExpectationError0(String message, Object expected, Object actual)
	{
		if (message == null)
		{
			message = "";			
		}
		if (expected == null)
		{
			throw new NullPointerException(MessageFormat.format(AssertsUtilities.INPUT_REQUIRED, "message"));
		}
		if (actual == null)
		{
			throw new NullPointerException(MessageFormat.format(AssertsUtilities.INPUT_REQUIRED, "actual"));
		}
		return MessageFormat.format(EXPECTED_ERROR, message, expected, actual);		
	}
	
	/**
	 * 
	 * @param message
	 * @param expected
	 * @param actual
	 * @return
	 */
	public static String buildExpectationError(String message, Object expected, Object actual)
	{
		return AssertsUtilities.getInstance().buildExpectationError0(message, expected, actual);	
	}
}
