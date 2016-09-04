/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * A StringUtilities is a convenient class for handling some common text
 * processing.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public final class StringUtilities {

    public final static String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    
    /**
     * Creates new StringUtilities
     */
    private StringUtilities() {
    }

    /**
     * Tokenizes a given string according to the specified delimiters. The
     * characters in the delim argument are the delimiters for separating
     * tokens. Delimiter characters themselves will not be treated as tokens.
     * 
     * @param str a string to be parsed.
     * @param delim the delimiters.
     * @return the tokens in a string array.
     */
    public static String[] tokenize(String str, String delim) {
        return ArrayUtilities.toArray(str, delim);
    }

    /**
     * Tokenizes a given string according to a fixed length. If the last token's
     * length is less than the fixed length specified, it will be ignored.
     * 
     * @param str a string to be parsed.
     * @param fixedLength the fixed length.
     * @return the tokens in a string array.
     */
    public static String[] tokenize(String str, int fixedLength) {
        ArrayList tokens = new ArrayList();
        if (str != null && fixedLength > 0) {
            for (int i = 0; i < str.length(); i += fixedLength) {
                int next = i + fixedLength;
                if (next > str.length()) {
                    break;
                }
                tokens.add(str.substring(i, next));
            }
        }
        return (String[]) tokens.toArray(new String[]{});
    }

    /**
     * Concatenates a string array (string tokens) into a string with the
     * specified delimiter string.
     * 
     * @param tokens a string array to be concatenated.
     * @param delim the delimiter.
     * @return the concatenated string.
     */
    public static String concat(String[] tokens, String delim) {
        return concat(tokens, "", "", delim);
    }

    /**
     * Concatenates a string array (string tokens) into a string with the
     * specified delimiter string, token's prefix, and token's suffix.
     * 
     * @param tokens a string array to be concatenated.
     * @param tokenPrefix the token's prefix to be concatenated.
     * @param tokenSuffix the token's suffix to be concatenated.
     * @param delim the delimiter.
     * @return the concatenated string.
     */
    public static String concat(String[] tokens, String tokenPrefix,
            String tokenSuffix, String delim) {
        String s = "";
        if (tokens != null) {
            if (tokenPrefix == null) {
                tokenPrefix = "";
            }
            if (tokenSuffix == null) {
                tokenSuffix = "";
            }
            if (delim == null) {
                delim = "";
            }
            for (int i = 0; i < tokens.length; i++) {
                s += tokenPrefix + tokens[i] + tokenSuffix;
                if (i + 1 < tokens.length) {
                    s += delim;
                }
            }
        }
        return s;
    }

    /**
     * Checks if a given string array contains the specified search string.
     * 
     * @param tokens a string array to be searched.
     * @param target the target search string.
     * @return true if the given string array contains the specified search
     *         string, false otherwise.
     */
    public static boolean contains(String[] tokens, String target) {
        if (tokens != null) {
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i] == null) {
                    if (target == null) {
                        return true;
                    }
                }
                else {
                    if (tokens[i].equals(target)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    /**
     * Repeats a given string in the specified number of times, then
     * concatenates and returns it.
     * 
     * @param s a string to be repeated and concatenated.
     * @param occurs the number of times of the given string to be repeated.
     * @return the concatenated string.
     */
    public static String repeat(String s, int occurs) {
        String result = "";
        if (s != null && occurs > 0) {
            for (int i = 0; i < occurs; i++) {
                result += s;
            }
        }
        return result;
    }

    /**
     * Checks if a given string contains only digits.
     * 
     * @param s a string to be checked.
     * @return true if the given string contains only digits, false otherwise.
     */
    public static boolean isAllDigit(String s) {
        if (s == null || s.equals("")) {
            return false;
        }
        else {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Adds leading zeros to the given string to the specified length. Nothing
     * will be done if the length of the given string is equal to or greater
     * than the specified length.
     * 
     * @param s the source string.
     * @param len the length of the target string.
     * @return the string after adding leading zeros.
     */
    public static String addLeadingZero(String s, int len) {
        return addLeadingCharacter(s, '0', len);
    }

    /**
     * Adds leading spaces to the given string to the specified length. Nothing
     * will be done if the length of the given string is equal to or greater
     * than the specified length.
     * 
     * @param s the source string.
     * @param len the length of the target string.
     * @return the string after adding leading spaces.
     */
    public static String addLeadingSpace(String s, int len) {
        return addLeadingCharacter(s, ' ', len);
    }

    /**
     * Adds specified leading characters to the specified length. Nothing will
     * be done if the length of the given string is equal to or greater than the
     * specified length.
     * 
     * @param s the source string.
     * @param c the leading character(s) to be added.
     * @param len the length of the target string.
     * @return the string after adding the specified leading character(s).
     */
    public static String addLeadingCharacter(String s, char c, int len) {
        while (s != null && s.length() < len) {
            s = c + s;
        }
        return s;
    }

    /**
     * Removes leading zeros from the given string, if any.
     * 
     * @param s the source string.
     * @return the string after removing leading zeros.
     */
    public static String removeLeadingZero(String s) {
        return removeLeadingCharacter(s, '0');
    }

    /**
     * Removes leading spaces from the given string, if any.
     * 
     * @param s the source string.
     * @return the string after removing leading spaces.
     */
    public static String removeLeadingSpace(String s) {
        return removeLeadingCharacter(s, ' ');
    }

    /**
     * Removes specified leading characters from the given string, if any.
     * 
     * @param s the source string.
     * @param c the leading character(s) to be removed.
     * @return the string after removing the specified leading character(s).
     */
    public static String removeLeadingCharacter(String s, char c) {
        while (s != null && s.length() > 0 && s.charAt(0) == c) {
            s = s.substring(1, s.length());
        }
        return s;
    }

    /**
     * Appends zeros to the given string to the specified length. Nothing will
     * be done if the length of the given string is equal to or greater than the
     * specified length.
     * 
     * @param s the source string.
     * @param len the length of the target string.
     * @return the string after appending zeros.
     */
    public static String appendZero(String s, int len) {
        return appendCharacter(s, '0', len);
    }

    /**
     * Appends spaces to the given string to the specified length. Nothing will
     * be done if the length of the given string is equal to or greater than the
     * specified length.
     * 
     * @param s the source string.
     * @param len the length of the target string.
     * @return @return the string after appending spaces.
     */
    public static String appendSpace(String s, int len) {
        return appendCharacter(s, ' ', len);
    }

    /**
     * Appends specified characters to the given string to the specified length.
     * Nothing will be done if the length of the given string is equal to or
     * greater than the specified length.
     * 
     * @param s the source string.
     * @param c the character(s) to be appended.
     * @param len the length of the target string.
     * @return @return the string after appending the specified character(s).
     */
    public static String appendCharacter(String s, char c, int len) {
        while (s != null && s.length() < len) {
            s += c;
        }
        return s;
    }

    /**
     * Checks if a given string is null or empty after trimmed.
     * 
     * @param s the string to be checked.
     * @return true if the given string is null or empty after trimmed, false
     *         otherwise.
     */
    public static boolean isEmptyString(String s) {
        return (s == null || (s.trim()).equals(""));
    }

    /**
     * Returns a string representation of the given throwable object, empty string if it
     * is null. The resulted string contains a cause trace of the given throwable object.
     * 
     * @param e the throwable object for getting its string representation.
     * @return a string represenation of the given Object.
     */
    public static String toString(Throwable e) {
        if (e == null) {
            return "";
        }
        else {
            String s = getExceptionString(e);
            Throwable cause = e.getCause();
            while (cause != null) {
                s += StringUtilities.LINE_SEPARATOR + "\tby "
                  + getExceptionString(cause);
                cause = cause.getCause();
            }
            return s;
        }
    }
    
    /**
     * Gets the exception description.
     * 
     * @param e the exception.
     * @return the exception description.
     */
    private static String getExceptionString(Throwable e) {
        if (e == null) {
            return "";
        }
        else {
            String s = e.getClass().getName();
            String m = e.getMessage();
            if (m != null) {
                s += ": " + m;
            }
            return s;
        }
    }

    /**
     * Returns a string representation of the given object, empty string if it
     * is null.
     * 
     * @param obj the object for getting its string representation.
     * @return a string represenation of the given Object.
     */
    public static String toString(Object obj) {
        if (obj == null) {
            return "";
        }
        else {
            return obj.toString();
        }
    }

    /**
     * Returns a string representation of the given Date object of the form: 
     * d MMM yyyy hh:mm:ss GMT
     * 
     * @param d the date.
     * @return the GMT string representation of the given date.
     */
    public static String toGMTString(Date d) {
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy HH:mm:ss z", Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(d);
    }
    
    /**
     * Converts the given string into an array of characters.
     * 
     * @param s the string to be converted.
     * @return an array of characters representing the given string or 
     *          an empty array if the given string is null or empty.
     */
    public static char[] toCharArray(String s) {
        return s==null? new char[]{}:s.toCharArray();
    }

    /**
     * Trims a given string. An empty string will be returned if the given
     * string is null.
     * 
     * @param s the string to be trimmed.
     * @return the trimmed string.
     */
    public static String trim(String s) {
        if (s == null) {
            return "";
        }
        else {
            return s.trim();
        }
    }

    /**
     * Trims the given string. If the given string starts with the specified 
     * prefix and ends with the specified suffix, both the prefix and the suffix 
     * will be trimmed out.
     * 
     * @param s the string to be trimmed.
     * @param prefix the prefix.
     * @param suffix the suffix.
     * @return the trimmed string.
     */
    public static String trim(String s, String prefix, String suffix) {
        if (isWrappedWith(s, prefix, suffix)) {
            return s.substring(getLength(prefix), s.length()-getLength(suffix));
        }
        else {
            return s;
        }
    }

    /**
     * Trims a given string and then verifies its size against the specified
     * size. If the sizes do not match, null will be returned.
     * 
     * @param s the string to be trimmed and verified.
     * @param size the size for the verification.
     * @return the trimmed string or null if the size verification failed.
     */
    public static String trimAndVerifySize(String s, int size) {
        s = trim(s);

        if (s.length() != size) {
            return null;
        }
        else {
            return s;
        }
    }
    
    /**
     * Wraps a given string with the specified prefix and suffix, if it is not
     * alreay wrapped.
     * 
     * @param s the string to be wrapped.
     * @param prefix the prefix.
     * @param suffix the suffix.
     * @return the wrapped string.
     */
    public static String wraps(String s , String prefix, String suffix) {
        if (isWrappedWith(s, prefix, suffix)) {
            return s;
        }
        else {
            return prefix + s + suffix;
        }
    }
    
    /**
     * Adds a prefix to a given string if it does not start with.
     * 
     * @param s the string to be added.
     * @param prefix the prefix.
     * @return the string with the specified prefix.
     */
    public static String addPrefix(String s, String prefix) {
        if (s == null) {
            return prefix;
        }
        else {
            if (s.startsWith(prefix)) {
                return s;
            }
            else {
                return prefix + s;
            }
        }
    }

    /**
     * Adds a suffix to a given string if it does not end with.
     * 
     * @param s the string to be added.
     * @param suffix the suffix.
     * @return the string with the specified suffix.
     */
    public static String addSuffix(String s, String suffix) {
        if (s == null) {
            return suffix;
        }
        else {
            if (s.endsWith(suffix)) {
                return s;
            }
            else {
                return s + suffix;
            }
        }
    }

    /**
     * Checks if the given string is wrapped with the specified prefix and 
     * suffix.
     * 
     * @param s the string to be checked.
     * @param prefix the prefix.
     * @param suffix the suffix.
     * @return true if the given string is wrapped with the prefix and suffix.
     */
    public static boolean isWrappedWith(String s, String prefix, String suffix) {
        if (s == null) {
            return false;
        }
        else {
            return  s.startsWith(prefix==null? "":prefix) && 
                    s.endsWith(suffix==null? "":suffix);
        }
    }
    
    /**
     * Gets the length of the given string.
     * 
     * @param s the string.
     * @return the length of the given string or 0 if it is null.
     */
    public static int getLength(String s) {
        if (s == null) {
            return 0;
        }
        else {
            return s.length();
        }
    }
    
    /**
     * Converts the tokenized string into a string array.
     * 
     * @param s the string to be tokenized.
     * @param delim the delimiters.
     * @param size the size of the converted array.
     * @return the string array.
     */
    public static String[] toArray(String s, String delim, int size) {
        if (size < 0) {
            size = 0;
        }
        
        String[] array = new String[size];
        String[] tokens = tokenize(s, delim);
        
        for (int i=0; i<array.length && i<tokens.length; i++) {
            array[i] = tokens[i];
        }
        return array;
    }
    
    /**
     * Converts a given string into a string array.
     * 
     * @param s the string to be converted.
     * @param occurs the number of occurrences.
     * @return the string array.
     */
    public static String[] toArray(String s, int occurs) {
        if (occurs < 0) {
            occurs = 0;
        }
        String[] array = new String[occurs];
        for (int i=0; i<array.length; i++) {
            array[i] = s;
        }
        return array;
    }
    
    /**
     * Converts a given string into a string array.
     * The resulted array will contain only one element, which is the specified 
     * string parameter.
     * 
     * @param s the string to be converted.
     * @return the string array.
     */
    public static String[] toArray(String s) {
       return toArray(s, 1);
    }
    
    /**
     * Parses the given string and returns the integer value it represents.
     * 
     * @param s the string to be parsed.
     * @return the integer value the string represents or Integer.MIN_VALUE if 
     *      unable to parse the string.    
     */
    public static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (Exception e) {
            return Integer.MIN_VALUE;
        }
    }
    
    /**
     * Parses the given string and returns the integer value it represents.
     * 
     * @param s the string to be parsed.
     * @return the integer value the string represents or the default value if 
     *      unable to parse the string.    
     */
    public static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        }
        catch (Exception e) {
            return def;
        }
    }
    
    /**
     * Parses the given string and returns the long value it represents.
     * 
     * @param s the string to be parsed.
     * @return the long value the string represents or Long.MIN_VALUE if 
     *      unable to parse the string.    
     */
    public static long parseLong(String s) {
        return parseLong(s, Long.MIN_VALUE);
    }
    
    /**
     * Parses the given string and returns the long value it represents.
     * 
     * @param s the string to be parsed.
     * @return the long value the string represents or the default value if 
     *      unable to parse the string.    
     */
    public static long parseLong(String s, long def) {
        try {
            return Long.parseLong(s);
        }
        catch (Exception e) {
            return def;
        }
    }

    /**
     * Parses the given string and returns the double value it represents.
     * 
     * @param s the string to be parsed.
     * @return the double value the string represents or Double.NaN if 
     *      unable to parse the string.    
     */
    public static double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        }
        catch (Exception e) {
            return Double.NaN;
        }
    }

    /**
     * Parses the given string and returns the double value it represents.
     * 
     * @param s the string to be parsed.
     * @return the double value the string represents or the default value if 
     *      unable to parse the string.  
     */
    public static double parseDouble(String s, double def) {
        try {
            return Double.parseDouble(s);
        }
        catch (Exception e) {
            return def;
        }
    }
    
    /**
     * Parses the given string and returns the boolean value it represents.
     * 
     * @param s the string to be parsed.
     * @return true if and only if the given string equals, ignoring case, "true".
     */
    public static boolean parseBoolean(String s) {
        if (s == null) {
            return false;
        }
        else {
            return new Boolean(s).booleanValue();
        }
    }
    
    /**
     * Parses the given string and returns the boolean value it represents.
     * 
     * @param s the string to be parsed.
     * @param def the default boolean value when <code>s</code> is null. 
     * @return true if and only if the given string equals, ignoring case, "true".
     */
    public static boolean parseBoolean(String s, boolean def)
    {
    	if (s == null)
    	{
    		return def;
    	}
    	else
    	{
    		return new Boolean(s).booleanValue();
    	}
    }
    
    /**
     * To Convert the byte array to the String in Hexdecimal format
     * @param b byte array to convert
     * @return String in hexdecimal representation
     * @throws Exception
     */
    public static String toHexString(byte[] b) throws Exception {
  	  	String result = "";
  	  	for (int i=0; i < b.length; i++) {
  	  		result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
  	  	}
  	  	return result;
  	}
}