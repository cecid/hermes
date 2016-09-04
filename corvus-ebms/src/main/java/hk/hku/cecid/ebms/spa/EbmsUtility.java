package hk.hku.cecid.ebms.spa;

import java.sql.Timestamp;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Locale;

import hk.hku.cecid.piazza.commons.util.DataFormatter;
import hk.hku.cecid.piazza.commons.util.UtilitiesException;

/**
 * An utility for converting from java datetime to UTC conformed datetime and vice versa.<br/><br/>
 * 
 * <ol>
 * 	<li>Fixed a bug that it parses UTC datetime with 'Z' timezone incorrectly becasue it used 
 * 		wrong timezone (GMT, but not UTC). (Thank Martin Kalen for figure out this)</li>
 * 	<li>Added datetime / calendar two UTC convertion.</li>  
 * </ol>
 * 
 * @author 	Twinsen Tsang, (modifier Philip Wong)
 * @since  	1/6/2007
 * @version	1.0.2   
 */
public class EbmsUtility 
{
	// The internal regex for ISO8601 UTC conformed datetime.
	private static final String UTC_REGEX_PATTERN = 
		"-?(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})(?:\\.(\\d+))?(Z|([\\+-]\\d{2}:\\d{2}))?";
	 
	// The number of group matching UTC regular expression pattern (8) including the whole string.
	private static final int UTC_CAPTURE_GROUP 	= 9; 
	
	// The UTC regex pattern matcher.
	private static final Pattern UTC_PATTERNER 	= Pattern.compile(UTC_REGEX_PATTERN);
				
	/**
	 * Convert an UTC representation of string <code>dateTime</code> to java Timestamp object. 
	 *  
	 * Followings are UTC conformed patterns:  
	 * <ul>
	 * <li>2007-07-16T13:24:56<li>
	 * <li>2007-07-16T13:24:56Z<li>
	 * <li>2007-07-16T13:24:56.789<li>
	 * <li>2007-07-16T13:24:56.789Z<li>
	 * <li>2007-07-16T13:24:56+02:00<li>
	 * <li>2007-07-16T13:24:56-02:00<li>
	 * <li>2007-07-16T13:24:56.789+02:00<li>
	 * <li>2007-07-16T13:24:56.789-02:00<li>
	 * </ul>
	 *  
	 * @param dateTime A string representing a UTC datetime. 
	 * @return A java timestamp object representing the <code>dateTime</code>.
	 * @throws UtilitiesException	When unable to convert the dateTime format.
	 */
	public static Timestamp 
	UTC2Timestamp(String dateTime) throws UtilitiesException
	{
		return new Timestamp(UTC2MS(dateTime));
	}
	
	/**
	 * Convert an UTC representation of string <code>dateTime</code> to java Date object. 
	 *  
	 * Followings are UTC conformed patterns:  
	 * <ul>
	 * <li>2007-07-16T13:24:56<li>
	 * <li>2007-07-16T13:24:56Z<li>
	 * <li>2007-07-16T13:24:56.789<li>
	 * <li>2007-07-16T13:24:56.789Z<li>
	 * <li>2007-07-16T13:24:56+02:00<li>
	 * <li>2007-07-16T13:24:56-02:00<li>
	 * <li>2007-07-16T13:24:56.789+02:00<li>
	 * <li>2007-07-16T13:24:56.789-02:00<li>
	 * </ul>
	 *  
	 * @param dateTime A string representing a UTC datetime. 
	 * @return A java date object representing the <code>dateTime</code>.
	 * @throws UtilitiesException	When unable to convert the dateTime format.
	 */
	public static Date 
	UTC2Date(String dateTime) throws UtilitiesException
	{
		return new Date(UTC2MS(dateTime));
	}
	
	/**
	 * Convert an UTC representation of string <code>dateTime</code> to millisecond.
	 * 
	 * Followings are UTC conformed patterns:  
	 * <ul>
	 * <li>2007-07-16T13:24:56<li>
	 * <li>2007-07-16T13:24:56Z<li>
	 * <li>2007-07-16T13:24:56.789<li>
	 * <li>2007-07-16T13:24:56.789Z<li>
	 * <li>2007-07-16T13:24:56+02:00<li>
	 * <li>2007-07-16T13:24:56-02:00<li>
	 * <li>2007-07-16T13:24:56.789+02:00<li>
	 * <li>2007-07-16T13:24:56.789-02:00<li>
	 * </ul>
	 *   
	 * @param dateTime A string representing a UTC datetime. 
	 * @return The millisecond representing by <code>dateTime</code>.
	 * @throws UtilitiesException	When unable to convert the dateTime format.
	 */
	public static long
	UTC2MS(String dateTime) throws UtilitiesException
	{
		return UTC2Calendar(dateTime).getTimeInMillis();
	}
		
	/**
	 * Convert an UTC representation of string <code>dateTime</code> to java calendar object. 
	 * 
	 * Followings are UTC conformed patterns:  
	 * <ul>
	 * <li>2007-07-16T13:24:56<li>
	 * <li>2007-07-16T13:24:56Z<li>
	 * <li>2007-07-16T13:24:56.789<li>
	 * <li>2007-07-16T13:24:56.789Z<li>
	 * <li>2007-07-16T13:24:56+02:00<li>
	 * <li>2007-07-16T13:24:56-02:00<li>
	 * <li>2007-07-16T13:24:56.789+02:00<li>
	 * <li>2007-07-16T13:24:56.789-02:00<li>
	 * </ul>
	 * 
	 * @param dateTime A string representing a UTC datetime. 
	 * @return A java clendar object representing the <code>dateTime</code>
	 * @throws UtilitiesException	When unable to convert the dateTime format.
	 */
	public static Calendar UTC2Calendar(String dateTime) throws UtilitiesException {
		Matcher m = UTC_PATTERNER.matcher(dateTime);

		if (m.matches() && m.groupCount() == UTC_CAPTURE_GROUP) {
			
			int[] parts = new int[UTC_CAPTURE_GROUP - 1];
			
			// parsing year, month, days, hours, minutes and second.
			int i;
			// i = 0 is group of whole pattern
			for (i = 0; i < 6; i++)
				parts[i] = Integer.parseInt(m.group(i + 1));
			
			// parsing millesecond (OPTIONAL)
			String ms = m.group(i + 1);
			if (ms == null) {
				parts[i++] = 0;
			} else {
				if (ms.length() > 3)
					ms = ms.substring(0, 3); // chop the sub second part to millesecond.
				parts[i++] = Integer.parseInt(ms);
			}
			
			// parsing Timezone (Z OR [+-]hh:mm)
			String tzStr = m.group(i + 1);
			TimeZone tz = null;
			
			if (tzStr == null || tzStr.equals("Z")) // Case with or without Z
				tz = TimeZone.getTimeZone("UTC");
			else
				tz = TimeZone.getTimeZone("GMT" + tzStr);

			
			Calendar c = Calendar.getInstance();
			c.clear();
			c.setTimeZone(tz);
			c.set(parts[0], parts[1]-1, parts[2], parts[3], parts[4], parts[5]);
			c.add(Calendar.MILLISECOND, parts[6]); // parts[6] = millescond
			return c; // parts[6] = millescond

		} else {
			throw new UtilitiesException("Unable to convert datetime to UTC format:" + dateTime);
		}						
	}

	/**
	 * Convert a date <code>dateTime</code> to UTC conformed representation of string. (e.g. 2007-07-16T13:24:56.789+13:00)
	 * 
	 * @param dateTime 
	 * 			A java date representing the time you want to convert.
	 * @param timeZone 
	 * 			The timezone of the <code>dateTime</code>. If it is null, it use the default 
	 * 			timezone in the machine.       
	 * @return 
	 * 			an UTC conformed representation with time specified by <code>dateTime</code> 
	 * 			
	 * @throws UtilitiesException	When unable to convert the <code>dateTime</code> to UTC.
	 */
	public static String
	date2UTC(Date dateTime, TimeZone timeZone) throws UtilitiesException  
	{
		try{
			// Create the formatter.
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");	                
			dateFormatter.setLenient(false);
			dateFormatter.setTimeZone(timeZone);			
			DecimalFormat twoDigits = new DecimalFormat("00");
			// Format the date to UTC representation with out timezone information.		
			String utc = dateFormatter.format(dateTime);
			
			// Calculate the timezone offset from the date to convert.			
			int tzOffset = timeZone.getOffset(dateTime.getTime());
			String sign  = "+"; // default using plus sign
			if (tzOffset < 0){	
				sign = "-"; 
				tzOffset = -tzOffset;	
			}		
			int hours = tzOffset / 3600000;
			int minutes = (tzOffset % 3600000) / 60000;
			
			// Create return string using buffer
			return new StringBuffer(utc.length() + 7)
				.append(utc)
				.append(sign)
				.append(twoDigits.format(hours))
				.append(":")
				.append(twoDigits.format(minutes))
				.toString();
			
		} catch (Exception ex){
			throw new UtilitiesException(ex);
		}				
	}
	
	/**
	 * Convert a calendar <code>dateTime</code> to UTC conformed representation of string. 
	 * 
	 * @param dateTime A java calendar representing the time you want to convert. 
	 * @return 
	 * 		an UTC conformed representation with time specified by <code>dateTime</code> 
	 * @throws UtilitiesException	
	 * 		When unable to convert the <code>dateTime</code> to UTC.
	 */
	public static String
	calendar2UTC(Calendar dateTime) throws UtilitiesException
	{
		return EbmsUtility.date2UTC(dateTime.getTime(), dateTime.getTimeZone());
	}
	
	/** 
	 * @return 	Get the current datetime with respect to the default timezone in UTC 
	 * 			conformed representation.
	 */
	public static String
	getCurrentUTCDateTime() 
	{
		// Suppress exception.
		try { 
			return EbmsUtility.date2UTC(new Date(), TimeZone.getDefault()); 
		} catch (UtilitiesException ex){ }
		return null;
	}
	
	/**
	 * Convert an GMT representation of string <code>dateTime</code> to java Timestamp object. 
	 *  
	 * @param dateTime A string representing a GMT datetime. 
	 * @return A java timestamp object representing the <code>dateTime</code>
	 * @throws UtilitiesException	When unable to convert the dateTime format.
	 */
	public static Timestamp
	GMT2Timestamp(String dateTime) throws UtilitiesException 
	{	
		return new Timestamp(EbmsUtility.GMT2Date(dateTime).getTime());
	}
	
	/**
	 * Convert an GMT representation of string <code>dateTime</code> to millisecond. 
	 *  
	 * @param dateTime A string representing a GMT datetime. 
	 * @return The millisecond representing the <code>dateTime</code>
	 * @throws UtilitiesException	When unable to convert the dateTime format.
	 */
	public static long
	GMT2MS(String dateTime) throws UtilitiesException 
	{	
		return EbmsUtility.GMT2Date(dateTime).getTime();
	}
	
	/**
	 * Convert an GMT representation of string <code>dateTime</code> to java calendar object. 
	 *  
	 * @param dateTime A string representing a GMT datetime. 
	 * @return The millisecond representing the <code>dateTime</code>
	 * @throws UtilitiesException	When unable to convert the dateTime format.
	 */
	public static Calendar
	GMT2Calender(String dateTime) throws UtilitiesException
	{
		Calendar cal = Calendar.getInstance();		
		cal.setTimeInMillis(EbmsUtility.GMT2Date(dateTime).getTime());
		return cal;
	}
	
	/**
	 * Convert an GMT representation of string <code>dateTime</code> to java Date object. 
	 *  
	 * @param dateTime A string representing a GMT datetime. 
	 * @return A java date object representing the <code>dateTime</code>
	 * @throws UtilitiesException	When unable to convert the dateTime format.
	 */
	public static Date
	GMT2Date(String dateTime) throws UtilitiesException
	{
		// GMT Example: Wed Jul 25 09:59:00 GMT+08:00 2007
		// Reminder: We need to use locale.US for parsing the above dateTime.
		Date GMTdate = DataFormatter.getInstance().parseDate(dateTime, "EEE MMM dd HH:mm:ss zz yyyy", Locale.US);
		
		if (GMTdate == null)
			throw new UtilitiesException("Unable to convert datetime to GMT format:" + dateTime);
		
		return GMTdate;
	}
}
