/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.sfrm.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A NullOutputStream discards any bytes in 
 * <code>write</code> operations.
 *  
 * Creation Date: 11/10/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.2
 */
public class NullOutputStream extends OutputStream {

	/**
	 * DUMMY
	 */
	public void close() throws IOException {
	}

	/**
	 * DUMMY
	 */
	public void flush() throws IOException {
	}

	/**
	 * DUMMY.
	 */
	public void write(int b) throws IOException {		
	}

	/**
	 * DUMMY
	 */
	public void write(byte[] b, int off, int len) throws IOException {
	}

	/**
	 * DUMMY
	 */
	public void write(byte[] b) throws IOException {
	}
}
