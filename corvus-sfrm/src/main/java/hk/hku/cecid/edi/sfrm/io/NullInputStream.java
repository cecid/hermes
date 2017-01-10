package hk.hku.cecid.edi.sfrm.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * A NullInputStream discards any bytes and always 
 * return negative one for <code>read</code>
 * operations.   
 * 
 * Creation Date: 7/11/2006
 * 
 * @author Twinsen
 * @version 1.0.0
 * @since	1.0.2
 */
public class NullInputStream extends InputStream {
	
	/**
	 * It returns -1.
	 */
	public int available() throws IOException {
		return -1;
	}
	
	/**
	 * It returns false.
	 */
	public boolean markSupported() {
		return false;
	}

	/**
	 * It returns -1.
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		return -1;
	}

	/**
	 * It returns -1.
	 */
	public int read(byte[] b) throws IOException {
		return -1;
	}

	/**
	 * DUMMY Methods.
	 */
	public synchronized void reset() throws IOException {
	}

	/**
	 * DUMMY Methods. It returns 0.
	 */
	public long skip(long n) throws IOException {
		return 0;
	}

	/**
	 * It returns -1.
	 */
	public int read() throws IOException {
		return -1;
	}	
}
