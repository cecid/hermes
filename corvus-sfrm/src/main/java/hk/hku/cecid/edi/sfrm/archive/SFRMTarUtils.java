package hk.hku.cecid.edi.sfrm.archive;

import java.io.UnsupportedEncodingException;

import org.apache.tools.tar.TarUtils;

/**
 * @author Patrick Yip
 *
 */
public class SFRMTarUtils extends TarUtils {
	
	public final static String NAME_ENCODING = "UTF-8";
	/**
     * Parse an entry name from a header buffer.
     *
     * @param header The header buffer from which to parse.
     * @param offset The offset into the buffer from which to parse.
     * @param length The number of header bytes to parse.
     * @return The header's entry name.
     */
    public static StringBuffer parseName(byte[] header, int offset, int length) {

		StringBuffer result = null;
        int nameLen = length; 
		
        int          end = offset + length;

        for (int i = offset; i < end; ++i) {
            if (header[i] == 0) {
				nameLen = i - offset; 
                break;
            }

//            result.append((char) header[i]);
			
        }
		
		try {
            result = new StringBuffer(new String(header, offset, nameLen, NAME_ENCODING));
        } catch(UnsupportedEncodingException e) {
           e.printStackTrace();
        } 

        return result;
    }
    
    /**
     * Determine the number of bytes in an entry name.
     *
     * @param name The header name from which to parse.
     * @param buf The buffer from which to parse.
     * @param offset The offset into the buffer from which to parse.
     * @param length The number of header bytes to parse.
     * @return The number of bytes in a header's entry name.
     */
    public static int getNameBytes(StringBuffer name, byte[] buf, int offset, int length) {
//        int i;
		int nameLength = -1;
        try
        {
            byte nameBytes[] = name.toString().getBytes(NAME_ENCODING);
            nameLength = nameBytes.length ;
            System.arraycopy(nameBytes, 0, buf, offset, nameLength);
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        } 
		/*
        for (i = 0; i < length && i < name.length(); ++i) {
            buf[offset + i] = (byte) name.charAt(i);
        }

        for (; i < length; ++i) {
            buf[offset + i] = 0;
        }
		*/
		
		for (; nameLength < length; ++nameLength) {
            buf[offset + nameLength] = 0;
        } 
		
        return offset + length;
    }
}
