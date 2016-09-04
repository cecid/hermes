/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.io;

import java.io.IOException;
import java.io.InputStream;


/**
 * MergedInputStream is an input stream which can merge two input streams into
 * one. When the read() method is called, the first input stream will be read
 * at first. However, if it has already reached the end, the second input stream 
 * will be read.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class MergedInputStream extends InputStream {

    private InputStream ins1, ins2;
    private boolean eof;
    
    /**
     * Creates a new instance of MergedInputStream.
     *  
     * @param ins1 the first input stream to be merged.
     * @param ins2 the second input stream to be merged.
     */
    public MergedInputStream(InputStream ins1, InputStream ins2) {
        this.ins1 = ins1;
        this.ins2 = ins2;
        eof = false;
    }

    /**
     * Reads the first input stream and if it has reached the end, reads
     * the second input stream. After this input stream has positioned to 
     * the second input stream, it will never come back to the first one. 
     * 
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException {
        if (!eof && ins1 != null) {
            int i = ins1.read();
            if (i != -1) {
                return i;
            }
            else {
                eof = true;
            }
        }
        return ins2==null? -1:ins2.read();
    }
}
