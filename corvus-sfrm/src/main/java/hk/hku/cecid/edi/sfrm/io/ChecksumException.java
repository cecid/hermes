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

/**
 * An exception that should be thrown when the check sum 
 * value for a file is invalid.
 * 
 * Creation Date: 21/12/2006
 * 
 * @author Twinsen	
 * @version 1.0.0
 * @since	1.0.3
 */
public class ChecksumException extends IOException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5349557440775151533L;

	/**
     * Creates a new instance of ChecksumException.
     */
    public ChecksumException() {
        super();
    }

    /**
     * Creates a new instance of ChecksumException.
     * 
     * @param message the error message.
     */
    public ChecksumException(String message) {
        super(message);
    }
}
