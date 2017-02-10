package hk.hku.cecid.ebms.spa;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;

import junit.framework.TestCase;

import hk.hku.cecid.ebms.spa.EbmsUtility;

/**
 * The <code>EbmsUtilityTest</code> is the testcase for <code>EbmsUtility</code>.
 * 
 * @author 	Twinsen Tsang
 * @since	1.0.0
 * @version	1.0.0
 */
public class EbmsUtilityTest extends TestCase{

	// Invoked for setup.
	public void setUp() throws Exception {
		System.out.println();
		System.out.println("---------" + this.getName() + " Start -------");
	}
	
	// Invoked for finalized.
	public void tearDown() throws Exception {
		System.out.println("---------" + this.getName() + " End   -------");
	}
	
	/**
	 * Get the local timezone represenetation from the UTC. The returned 
	 * format is "GMT+hh:mm".
	 * <br/><br/>
	 * For example, if you are located in Asia/Hong Kong, the returned
	 * string should be "GMT+08:00".   
	 */
	public String getLocalTimezone(){
		// Get the default timezone.		
		TimeZone tz = TimeZone.getDefault();		
		// Get the offset from UTC.
		int tzOffset = tz.getOffset(new Date().getTime());
		// Formatter.
		DecimalFormat twoDigits = new DecimalFormat("00");
				
		String sign = "+";
		if (tzOffset < 0){ sign = "-"; tzOffset = -tzOffset; }			
		
		// calculate the timezone offset.
		int hours 	= (tzOffset / 3600000);
		int minutes = (tzOffset % 3600000) / 60000;
		
		// Return the local timezone.
		return new StringBuffer("GMT")
			.append(sign)
			.append(twoDigits.format(hours))
			.append(":")
			.append(twoDigits.format(minutes)).toString();
	}
	
	/**
	 * A common method to validate cirtical field for specified <code>cal</code>
	 * 
	 * @param cal The calendar to test against. 		
	 * @param expectedYear
	 * 			The expected year.
	 * @param expectedMonth
	 * 			The expected month.
	 * @param expectedDay
	 * 			The expected day.
	 * @param expectedHour
	 * 			The expected hour.
	 * @param expectedMins
	 * 			The expected mins.
	 * @param expectedSecond
	 * 			The expected second.
	 * @param expectedMillisecond
	 * 			The expected millisecond.
	 * @param expectedTz
	 * 			The expected timezone.
	 */
	public void validateCalendar(Calendar cal, 
		int expectedYear, int expectedMonth, int expectedDay, int expectedHour,
		int expectedMins, int expectedSecond, int expectedMillisecond, String expectedTz)
	{
		// Null test
		TestCase.assertNotNull(cal);
		
		// Value test
		TestCase.assertEquals(expectedYear   		, cal.get(Calendar.YEAR));
		TestCase.assertEquals(expectedMonth  		, cal.get(Calendar.MONTH)); // Month is starting from zero.			
		TestCase.assertEquals(expectedDay    		, cal.get(Calendar.DAY_OF_MONTH));
		TestCase.assertEquals(expectedHour   		, cal.get(Calendar.HOUR_OF_DAY));
		TestCase.assertEquals(expectedMins   		, cal.get(Calendar.MINUTE));
		TestCase.assertEquals(expectedSecond 		, cal.get(Calendar.SECOND));
		TestCase.assertEquals(expectedMillisecond   , cal.get(Calendar.MILLISECOND));
		TestCase.assertEquals(expectedTz			, cal.getTimeZone().getID());

		// Now transform it to the local machine time.
		Calendar transformedCal = Calendar.getInstance();
		transformedCal.setTime(cal.getTime());
		
		// Get the timezone offset from the testing calendar from UTC.
		int clOffset = cal.getTimeZone().getOffset(new Date().getTime());
		// Get the timezone offset of this local machine from UTC.		
		int tzOffset = TimeZone.getDefault().getOffset(cal.getTimeInMillis()); 
		// Calculate the actual offset 
		tzOffset = tzOffset - clOffset;
		
		System.out.println("TimeZone Offset: " + tzOffset); 
		int hours = expectedHour +  tzOffset / 3600000;
		int mins  = expectedMins + (tzOffset % 3600000) / 60000;
		int days  = expectedDay;
		
		// If the offset is greater than 24.
		if 		(hours > 24){ hours -= 24; days += 1; }
		else if (hours < 0) { hours += 24; days -= 1; }
			
		TestCase.assertEquals(hours, transformedCal.get(Calendar.HOUR_OF_DAY));
		TestCase.assertEquals(mins , transformedCal.get(Calendar.MINUTE));
		TestCase.assertEquals(days , transformedCal.get(Calendar.DAY_OF_MONTH));
		
		System.out.println(transformedCal.getTime());
		// Done.
	}
	
	/**
	 * A common method to validate cirtical field for specified <code>target</code>
	 * by the value in the calendar <code>control</code>.
	 * 
	 * @param target
	 * 			The target calendar to test for. 
	 * @param control
	 * 			The reference calendar to test for.
	 */
	public void validateCalendar(Calendar target, Calendar control)
	{
		// Not Null test
		TestCase.assertNotNull(target);
		TestCase.assertNotNull(control);	
		// Assert whether they are representing same time.
		System.out.println("Target  Calendar (de): " + target.getTime());
		System.out.println("Control Calendar (de): " + control.getTime());
		
		System.out.println("Target  Calendar (ms): " + target.getTimeInMillis());
		System.out.println("Control Calendar (ms): " + control.getTimeInMillis());
		
		TestCase.assertEquals (target.getTimeInMillis(), control.getTimeInMillis());	
	}	
		
	/**
	 * Test the conversation from UTC to java calendar object.<br/><br/>
	 * 
	 * Testing for the scenario the dateTime is represented as UTC and
	 * under the timezone in UTC (GMT+00:00) using 'Z' designator.  
	 */
	public void testUTC2Calendar_Pos0() throws Exception {		
		String dateTime = "2007-07-16T13:24:56.789Z";
		
		Calendar cal = EbmsUtility.UTC2Calendar(dateTime);
		
		// Validate all field.
		this.validateCalendar(cal, 2007, 06, 16, 13, 24, 56, 789, "UTC");
	}	
	
	/**
	 * Test the conversation from UTC to java calendar object.<br/><br/>
	 * 
	 * Testing for the scenario the dateTime is represented as UTC and
	 * under the timezone in UTC (GMT+00:00) using +00:00
	 */
	public void testUTC2Calendar_Pos1() throws Exception {
		String dateTime = "2007-07-16T13:24:56.789+00:00";
		
		Calendar cal = EbmsUtility.UTC2Calendar(dateTime);
		
		// Validate all field.
		this.validateCalendar(cal, 2007, 06, 16, 13, 24, 56, 789, "GMT+00:00");				
	}
	
	/**
	 * Test the conversation from UTC to java calendar object.<br/><br/>
	 * 
	 * Testing for the scenario the dateTime is represented as UTC and
	 * under the timezone GMT+08:00 (non zero timezone)
	 */
	public void testUTC2Calendar_Pos2() throws Exception {
		String dateTime = "2007-07-16T13:24:56.789+08:00";
		Calendar cal = EbmsUtility.UTC2Calendar(dateTime);
		
		// Validate all field.
		this.validateCalendar(cal, 2007, 06, 16, 13, 24, 56, 789, "GMT+08:00");
	}
	
	/**
	 * Test the conversation from UTC to java calendar object.<br/><br/>
	 * 
	 * Testing for the scenario the dateTime is represented as UTC and
	 * under the timezone GMT+13:00 (boundary case)
	 */
	public void testUTC2Calendar_Pos3() throws Exception {
		String dateTime = "2007-07-16T04:24:56.789+13:00";
		Calendar cal = EbmsUtility.UTC2Calendar(dateTime);
		
		// * We have used the hour 04 instead of 13 for testing the offset from previous day.
		// Validate all field.
		this.validateCalendar(cal, 2007, 06, 16, 04, 24, 56, 789, "GMT+13:00");
	}
	
	/**
	 * Test the conversation from UTC to java calendar object.<br/><br/>
	 * 
	 * Testing for the scenario the dateTime is represented as UTC and
	 * under the timezone GMT-12:00 (boundary case)
	 */
	public void testUTC2Calendar_Pos4() throws Exception {
		String dateTime = "2007-07-16T13:24:56.789-12:00";
		Calendar cal = EbmsUtility.UTC2Calendar(dateTime);
		
		// Validate all field.
		this.validateCalendar(cal, 2007, 06, 16, 13, 24, 56, 789, "GMT-12:00");
	}
	
	/**
	 * Test the conversation from UTC to java calendar object.<br/><br/>
	 * 
	 * Testing for the scenario the dateTime is represented as UTC without millisecond
	 * and under the timezone UTC.
	 */
	public void testUTC2Calendar_Pos5() throws Exception {
		String dateTime = "2007-07-16T13:24:56Z";
		Calendar cal = EbmsUtility.UTC2Calendar(dateTime);
		
		// Validate all field.
		this.validateCalendar(cal, 2007, 06, 16, 13, 24, 56, 0, "UTC");
	}
	
	/**
	 * Test the conversation from UTC to java calendar object.<br/><br/>
	 * 
	 * Testing for the scenario the dateTime is represented as UTC without millisecond
	 * and under any timezone other than UTC.  
	 */
	public void testUTC2Calendar_Pos6() throws Exception {
		String dateTime = "2007-07-16T13:24:56-08:00";
		Calendar cal = EbmsUtility.UTC2Calendar(dateTime);
		
		// Validate all field.
		this.validateCalendar(cal, 2007, 06, 16, 13, 24, 56, 0, "GMT-08:00");
	}
	
	/**
	 * Test the conversation from UTC to java calendar object.<br/><br/>
	 * 
	 * Testing for the scenario the dateTime is represented as UTC without millisecond and 'Z'
	 * and under any timezone other than UTC.  
	 */
	public void testUTC2Calendar_Pos7() throws Exception {
		String dateTime = "2007-07-16T13:24:56";
		Calendar cal = EbmsUtility.UTC2Calendar(dateTime);
		
		// Validate all field.
		this.validateCalendar(cal, 2007, 06, 16, 13, 24, 56, 0, "UTC");
	}	
	
	/**
	 * Test the conversation from UTC to java calendar object.<br/><br/>
	 * 
	 * Testing for the scenario the dateTime is represented as UTC with 'Z'
	 * and under any timezone other than UTC.  
	 */
	public void testUTC2Calendar_Pos8() throws Exception {
		String dateTime = "2007-07-16T13:24:56.789";
		Calendar cal = EbmsUtility.UTC2Calendar(dateTime);
		
		// Validate all field.
		this.validateCalendar(cal, 2007, 06, 16, 13, 24, 56, 789, "UTC");
	}		
	
	/**
	 * Test the conversation from UTC to java calendar object negatively.<br/><br/>
	 * 
	 * Testing for the scenario all invalid UTC format representation.  
	 */
	public void testUTC2Calendar_Neg0() throws Exception {
		// Invalid regex format between year and month
		String dateTime0 = "2007**07-16T13:24:56.789-12:00";
		try{	
			EbmsUtility.UTC2Calendar(dateTime0);
			TestCase.fail("Invalid Format of UTC datetime has not throw parsing exception.");
		} catch (Exception ex) { 
			System.out.println("Invalid regex format between year and month passed.");
		}
		
		// Invalid regex format between month and days
		String dateTime1 = "2007-07!!16T13:24:56.789-12:00";
		try{	
			EbmsUtility.UTC2Calendar(dateTime1);
			TestCase.fail("Invalid Format of UTC datetime has not throw parsing exception.");
		} catch (Exception ex) { 
			System.out.println("Invalid regex format between month and days passed.");
		}
		
		// Invalid regex format between days and hour
		String dateTime2 = "2007-07-16@@@13:24:56.789-12:00";
		try{	
			EbmsUtility.UTC2Calendar(dateTime2);
			TestCase.fail("Invalid Format of UTC datetime has not throw parsing exception.");
		} catch (Exception ex) { 
			System.out.println("Invalid regex format between days and hour passed.");
		}
		
		// Invalid regex format between hours and minutes
		String dateTime3 = "2007-07-16@T13##24:56.789-12:00";
		try{	
			EbmsUtility.UTC2Calendar(dateTime3);
			TestCase.fail("Invalid Format of UTC datetime has not throw parsing exception.");
		} catch (Exception ex) { 
			System.out.println("Invalid regex format between hours and minutes passed.");
		}
		
		// Invalid regex format between minutes and second
		String dateTime4 = "2007-07-16@T13:24~~56.789-12:00";
		try{	
			EbmsUtility.UTC2Calendar(dateTime4);
			TestCase.fail("Invalid Format of UTC datetime has not throw parsing exception.");
		} catch (Exception ex) { 
			System.out.println("Invalid regex format between minutes and second passed.");
		}
		
		// Invalid regex format between second and millisecond
		String dateTime5 = "2007-07-16@T13:24:56~_~789-12:00";
		try{	
			EbmsUtility.UTC2Calendar(dateTime5);
			TestCase.fail("Invalid Format of UTC datetime has not throw parsing exception.");
		} catch (Exception ex) { 
			System.out.println("Invalid regex format between second and millisecond passed.");
		}
		
		// Invalid regex format between millisecond and timezone
		String dateTime6 = "2007-07-16@T13:24:56.78931212:00";
		try{	
			EbmsUtility.UTC2Calendar(dateTime6);
			TestCase.fail("Invalid Format of UTC datetime has not throw parsing exception.");
		} catch (Exception ex) { 
			System.out.println("Invalid regex format between millisecond and timezone passed.");
		}
		
		// Invalid regex format for millisecond
		String dateTime7 = "2007-07-16@T13:24:56.ddd-12:00";
		try{	
			EbmsUtility.UTC2Calendar(dateTime7);
			TestCase.fail("Invalid Format of UTC datetime has not throw parsing exception.");
		} catch (Exception ex) { 
			System.out.println("Invalid regex format for millisecond");
		}
	}
	
	/**
	 * Test for getting the current UTC data time.<br/><br/>
	 */
	/**
	 * @throws Exception
	 */
	public void testGetCurrentUTCDateTime_Pos0() throws Exception {
		String utc = EbmsUtility.getCurrentUTCDateTime();
		Calendar sysCal = Calendar.getInstance();
		Calendar utcCal = EbmsUtility.UTC2Calendar(utc);
		
		System.out.println("UTC converted: " + utc);
		
		// For this testcase, we should tolerate the millisecond and second due to 
		// there is sightly time different between creating the sysCal and utcCal.
		// So we set the millisecond part to 0 for this two cal.
		sysCal.set(Calendar.SECOND, 0);		
		sysCal.set(Calendar.MILLISECOND, 0);
		utcCal.set(Calendar.SECOND, 0);
		utcCal.set(Calendar.MILLISECOND, 0);

		// validate the calendar field 
		this.validateCalendar(utcCal, 			
			sysCal.get(Calendar.YEAR),
			sysCal.get(Calendar.MONTH), 			
			sysCal.get(Calendar.DAY_OF_MONTH),
			sysCal.get(Calendar.HOUR_OF_DAY),
			sysCal.get(Calendar.MINUTE),
			sysCal.get(Calendar.SECOND),
			sysCal.get(Calendar.MILLISECOND),
			this.getLocalTimezone());
	}
	
	/**
	 * Test for converting from a java date object to UTC conformed representation string.
	 * 
	 * Testing for the scenario the dateTime is represented as java calendar and
	 * under the timezone same as the machine running. 
	 */
	public void testDate2UTC_Pos0() throws Exception {
		Calendar cal = Calendar.getInstance();
		// 2007-07-16@T13:24:56.78931212:00
		cal.set(2007, 06, 16, 13, 24, 56);	
		cal.set(Calendar.MILLISECOND,  0);
		// Convert to UTC representation
		String utc = EbmsUtility.date2UTC(cal.getTime(), cal.getTimeZone());
		
		System.out.println("UTC converted: " + utc);
		
		// Convert back to java calendar and validate the field.
		Calendar utcCal = EbmsUtility.UTC2Calendar(utc);
		
		// validate the calendar field 
		this.validateCalendar(utcCal, 2007, 6, 16, 13, 24, 56, 0, this.getLocalTimezone());			
	}
	
	/**
	 * Test for converting from a java date object to UTC conformed representation string.
	 * 
	 * Testing for the scenario the dateTime is represented as java calendar and
	 * under the timezone under UTC. 
	 */
	public void testDate2UTC_Pos1() throws Exception {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		// 2007-07-16@T13:24:56.000Z
		cal.set(2007, 06, 16, 13, 24, 56);	
		cal.set(Calendar.MILLISECOND,  789);
		// Convert to UTC representation
		String utc = EbmsUtility.date2UTC(cal.getTime(), cal.getTimeZone());
		
		System.out.println("UTC converted     : " + utc);
				
		// Convert back to java calendar and validate the field.
		Calendar utcCal = EbmsUtility.UTC2Calendar(utc);
		
		// validate whether the timezone is correctly converted.
		String utcTz = utcCal.getTimeZone().getID();
		TestCase.assertTrue(utcTz.equals("UTC") || utcTz.equals("GMT+00:00"));
					
		// validate the calendar field based on cal.
		this.validateCalendar(utcCal, cal);
	} 
	
	/**
	 * Test for converting from a java date object to UTC conformed representation string.
	 * 
	 * Testing for the scenario the dateTime is represented as java calendar and
	 * under the timezone under GMT-12:00 (boundary case)
	 */
	public void testDate2UTC_Pos2() throws Exception {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-12:00"));
		// 2007-07-16@T13:24:56.000-12:00
		cal.set(2007, 06, 16, 13, 24, 56);	
		cal.set(Calendar.MILLISECOND,  789);
		// Convert to UTC representation
		String utc = EbmsUtility.date2UTC(cal.getTime(), cal.getTimeZone());
		
		System.out.println("UTC converted: " + utc);
		
		// Convert back to java calendar and validate the field.
		Calendar utcCal = EbmsUtility.UTC2Calendar(utc);	
		
		// validate whether the timezone is correctly converted.
		TestCase.assertTrue(utcCal.getTimeZone().getID().equals("GMT-12:00"));		
		
		// validate the calendar field based on cal.
		this.validateCalendar(utcCal, cal);
	}
	
	/**
	 * Test for converting from a java date object to UTC conformed representation string.
	 * 
	 * Testing for the scenario the dateTime is represented as java calendar and
	 * under the timezone under GMT+13:00 (boundary case)
	 */
	public void testDate2UTC_Pos3() throws Exception {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+13:00"));
		// 2007-07-16@T13:24:56.000-12:00
		cal.set(2007, 06, 16, 13, 24, 56);	
		cal.set(Calendar.MILLISECOND,  789);
		// Convert to UTC representation
		String utc = EbmsUtility.date2UTC(cal.getTime(), cal.getTimeZone());
		
		System.out.println("UTC converted: " + utc);
		
		// Convert back to java calendar and validate the field.
		Calendar utcCal = EbmsUtility.UTC2Calendar(utc);	
		
		// validate whether the timezone is correctly converted.
		TestCase.assertTrue(utcCal.getTimeZone().getID().equals("GMT+13:00"));
		
		// validate the calendar field based on cal.
		this.validateCalendar(utcCal, cal);
	}
	
	/**
	 * Test for converting from GMT datetime representation to java date object.  
	 */
	public void testGMT2Date_Pos0() throws Exception {
		
		Date date 	 = new Date();		
		
		String dateTime = date.toString();
		
		Date convertedDate = EbmsUtility.GMT2Date(dateTime);
		
		System.out.println("GMT converted: " + convertedDate);
		
		// Since the millisecond pecision will be lost during conversion.
		// we need to create a calendar and set the millisecond to zero. 
		Calendar cal = Calendar.getInstance();		
		cal.setTimeInMillis(date.getTime());
		cal.set(Calendar.MILLISECOND, 0);
		
		// Check whether the time is equal or not. 
		TestCase.assertEquals(convertedDate.getTime(), cal.getTimeInMillis());
	}
	
	/**
	 * Test for the scenario issued by Martin Kalen from the google group.<br/><br/>
	 * 
	 * QUOTED FROM martin said:
	 * <pre>
	 *  Hermes sends ebMS ACK messages with a non-standard timezone part of
	 *  the timestamp (timezone info is not according to ebXML-specification,
	 *  see background below)
	 *  
	 *  Background on #1:
	 *  The recent fix for Hermes UTC timestamps uses the Java
	 *  SimpleDateFormat "Z"-pattern for timezone, which is unfortunately
	 *  incompatible with the ebXML specification for time zone refering to
	 *  the XML schema data-type "dateTime" specified here:
	 *  http://www.w3.org/TR/xmlschema-2/#dateTime
	 *  
	 *  Snippets from the spec:
	 *  "dateTime consists of finite-length sequences of characters of the
	 *  form: '-'? yyyy '-' mm '-' dd 'T' hh ':' mm ':' ss ('.' s+)?
	 *  (zzzzzz)?"
	 *  "zzzzzz (if present) represents the timezone"
	 *  
	 *  From 3.2.7.3 Timezones The lexical representation of a timezone is a string of the form:
	 *  (('+' | '-') hh ':' mm) | 'Z'"
	 *  In Java the ':' character is missing between the "hh" and "mm" parts.
	 *  More details on the Java SimpleDateFormat vs XSD dateTime
	 *  incompatibility can be found here:  
	 * </pre>
	 * 
	 * Detail: http://groups.google.com/group/cecid-hermes2/browse_thread/thread/46cca8b51ca21524 
	 */	
	public void testMartinKalenIssue1() throws Exception {
		// All UTC datetime should be generated through below methods
		String utc = EbmsUtility.getCurrentUTCDateTime();
		// if the last character is not 'Z', then the character preceding last two
		// should be ":".
		System.out.println(utc);
		
		TestCase.assertTrue(
			"The converted UTC string should have ':' character between the 'hh' and 'mm' parts",
			(utc.charAt(utc.length() -1) != 'Z' && utc.charAt(utc.length() - 3) == ':')); 
	}
	
	/**
	 * Test for the scenario issued by Martin Kalen from the google group.<br/><br/>
	 * 
	 * <pre>
	 * 	I live in the UTC+02:00 timezone and today at 14:01 my time (=12:01
	 *  UTC time) an incoming ebXML message with the following TTL timestamp
	 *  was considered expired by Hermes: "2007-07-10T12:04:14Z".
	 *  Without the change in EbmsUtility above, Hermes regards the incoming
	 *  timestamp as 12:04 in my timezone which mutates the expected
	 *  12:04:14UTC to 10:04:14UTC = incorrect expiry notification to the
	 *  sender.
	 * </pre> 
	 * 
	 * Detail: http://groups.google.com/group/cecid-hermes2/browse_thread/thread/46cca8b51ca21524  
	 */
	public void testMartinKalenIssue2() throws Exception {
		// Incoming ebMS message TTL.
		String localTime   = "2007-07-10T14:01:14+02:00";
		String currentTime = "2007-07-10T12:01:14Z";
		String timeToLive  = "2007-07-10T12:04:14Z";		
		
		Calendar ttlCal  = EbmsUtility.UTC2Calendar(timeToLive);
				
		// Get the two calendar which they are representing same instant.
		Calendar curCal = EbmsUtility.UTC2Calendar(currentTime);
		Calendar locCal = EbmsUtility.UTC2Calendar(localTime);
				
		// Check whether they are representing same instant.
		this.validateCalendar(curCal, locCal);
				
		// Create one more calendar for stimulating the expiration validation flow
		// The timestamp created is 2007-07-10T12:01:14. 
		Calendar sysCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+02:00"));
		sysCal.set(2007, 06, 10, 12, 01, 14);
		
		System.out.println("System calendar : " + ttlCal.getTime());
		System.out.println("System calendar : " + sysCal.getTime());
				
		// The time to live timstamp is later than the system calendar, then it should 
		// return false.
		TestCase.assertFalse(
			"TimeToLive calendar should not eariler than system calendar", 
			ttlCal.getTime().before(sysCal.getTime()));
	}
	
}
