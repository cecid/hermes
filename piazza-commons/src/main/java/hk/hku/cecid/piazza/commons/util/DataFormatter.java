/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;

/**
 * DataFormatter is a convenient class for formatting objects into or from
 * string. Objects like Date, Time, and Decimal will be formatted according to
 * the following default patterns if none is specified or configured beforehand:
 * <p>
 * <ul>
 *   <li>date-format: <code>yyyyMMdd</code></li>
 *   <li>time-format: <code>HHmmss</code></li>
 *   <li>datetime-format: <code>yyyyMMddHHmmss</code></li>
 *   <li>timestamp-format: <code>yyyyMMddHHmmssSSS</code></li>
 *   <li>timestamp-suffix: <code>000</code></li>
 *   <li>decimal-format: <code>0.00</code></li>
 * </ul>
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public final class DataFormatter {

    private static final DataFormatter instance = new DataFormatter();
    
    private String dateFormat      = "yyyyMMdd";

    private String timeFormat      = "HHmmss";

    private String datetimeFormat  = "yyyyMMddHHmmss";

    private String timestampFormat = "yyyyMMddHHmmssSSS";

    private String timestampSuffix = "000";

    private String decimalFormat   = "0.00";

    /**
     * Creates a new instance of DataFormatter.
     */
    private DataFormatter() {
    }

    /**
     * Creates a new instance of DataFormatter. 
     * 
     * @param config configuration properties of the data format.
     */
    public DataFormatter(Properties config) {
        if (config != null) {
            dateFormat = config.getProperty("date-format", dateFormat);
            timeFormat = config.getProperty("time-format", timeFormat);
            datetimeFormat = config.getProperty("datetime-format",
                    datetimeFormat);
            timestampFormat = config.getProperty("timestamp-format",
                    timestampFormat);
            timestampSuffix = config.getProperty("timestamp-suffix",
                    timestampSuffix);
            decimalFormat = config.getProperty("decimal-format",
                    decimalFormat);
        }
    }
    
    /**
     * Gets a default instance of DataFormatter.
     * 
     * @return a default instance of DataFormatter.
     */
    public static DataFormatter getInstance() {
        return instance;
    }

    /**
     * Formats a double value into a String.
     * 
     * @param d The value to be formatted.
     * @return The String represenation of the value.
     */
    public String formatDecimal(double d) {
        return formatDecimal(d, decimalFormat);
    }

    /**
     * Formats a given double into a String according to the specified pattern.
     * 
     * @param d The double to be formatted.
     * @param pattern The pattern to be followed in formatting.
     * @return The formatted String of the double.
     */
    public String formatDecimal(double d, String pattern) {
        return new DecimalFormat(pattern).format(d);
    }

    /**
     * Formats a Double value into a String.
     * 
     * @param d The value to be formatted.
     * @return The String represenation of the value.
     */
    public String formatDecimal(Double d) {
        return formatDecimal(d.doubleValue());
    }

    /**
     * Formats a java.util.Date value into a date String.
     * 
     * @param d The value to be formatted.
     * @return The String represenation of the value.
     */
    public String formatDate(java.util.Date d) {
        return formatDate(d, dateFormat);
    }

    /**
     * Formats a java.util.Date value into a time String.
     * 
     * @param d The value to be formatted.
     * @return The String represenation of the value.
     */
    public String formatTime(java.util.Date d) {
        return formatDate(d, timeFormat);
    }

    /**
     * Formats a java.util.Date value into a date-time String.
     * 
     * @param d The value to be formatted.
     * @return The String represenation of the value.
     */
    public String formatDateTime(java.util.Date d) {
        return formatDate(d, datetimeFormat);
    }

    /**
     * Formats a java.util.Date value into a timestamp String.
     * 
     * @param d The value to be formatted.
     * @return The String represenation of the value.
     */
    public String formatTimestamp(java.util.Date d) {
        return formatDate(d, timestampFormat); // + TIMESTAMP_SUFFIX;
    }

    /**
     * Formats a given java.util.Date into a String according to the specified
     * pattern.
     * 
     * @param date The date to be formatted.
     * @param pattern The pattern to be followed in formatting.
     * @return The formatted String of the date.
     */
    public String formatDate(java.util.Date date, String pattern) {
        return formatDate(date, pattern, null);
    }

    /**
     * Formats a given java.util.Date into a String according to the specified
     * pattern.
     * 
     * @return The formatted String of the date.
     * @param locale The locale used in formatting.
     * @param date The date to be formatted.
     * @param pattern The pattern to be followed in formatting.
     */
    public String formatDate(java.util.Date date, String pattern,
            Locale locale) {
        try {
            if (locale == null) {
                return new SimpleDateFormat(pattern).format(date);
            }
            else {
                return new SimpleDateFormat(pattern, locale).format(date);
            }
        }
        catch (Exception e) {
            return date == null ? null : date.toString();
        }
    }

    /**
     * Parses a date String and formats it into a java.util.Date object.
     * 
     * @param s The value to be parsed.
     * @return The Object represenation of the value.
     */
    public java.util.Date parseDate(String s) {
        return parseDate(s, dateFormat);
    }

    /**
     * Parses a time String and formats it into a java.util.Date object.
     * 
     * @param s The value to be parsed.
     * @return The Object represenation of the value.
     */
    public java.util.Date parseTime(String s) {

        return parseDate(s, timeFormat);
    }

    /**
     * Parses a date-time String and formats it into a java.util.Date object.
     * 
     * @param s The value to be parsed.
     * @return The Object represenation of the value.
     */
    public java.util.Date parseDateTime(String s) {
        return parseDate(s, datetimeFormat);
    }

    /**
     * Parses a timestamp String and formats it into a java.util.Date object.
     * 
     * @param s The value to be parsed.
     * @return The Object represenation of the value.
     */
    public java.util.Date parseTimestamp(String s) {
        if (s == null
                || s.length() != (timestampFormat.length() + timestampSuffix
                        .length())) {
            return null;
        }
        return parseDate(s.substring(0, timestampFormat.length()),
                timestampFormat);
    }

    /**
     * Parses a date string and returns a java.util.Date object.
     * 
     * @param date The date string to be parsed.
     * @param pattern The pattern of the date string.
     * @return A java.util.Date object that represents the given date string.
     */
    public java.util.Date parseDate(String date, String pattern) {
        return parseDate(date, pattern, null);
    }

    /**
     * Parses a date string and returns a java.util.Date object.
     * 
     * @param date The date string to be parsed.
     * @param pattern The pattern of the date string.
     * @param locale The locale used in parsing the date string.
     * @return A java.util.Date object that represents the given date string.
     */
    public java.util.Date parseDate(String date, String pattern,
            Locale locale) {
        try {
            SimpleDateFormat dateFormatter;
            if (locale == null) {
                dateFormatter = new SimpleDateFormat(pattern);
            }
            else {
                dateFormatter = new SimpleDateFormat(pattern, locale);
            }
            dateFormatter.setLenient(false);
            return dateFormatter.parse(date);
        }
        catch (Exception e) {
            return null;
        }
    }
}