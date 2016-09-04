/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.sfrm.archive;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

/**
 * @author Patrick Yip
 *
 */
public class SFRMTarInputStream extends TarInputStream {

	public SFRMTarInputStream(InputStream arg0) {
		super(arg0);
	}
	
	public SFRMTarInputStream(InputStream is, int blockSize){
		super(is, blockSize);
	}
	
	public SFRMTarInputStream(InputStream is, int blockSize, int recordSize){
		super(is, blockSize, recordSize);
	}
	
	/**
     * Get the next entry in this tar archive. This will skip
     * over any remaining data in the current entry, if there
     * is one, and place the input stream at the header of the
     * next entry, and read the header and instantiate a new
     * TarEntry from the header bytes and return that entry.
     * If there are no more entries in the archive, null will
     * be returned to indicate that the end of the archive has
     * been reached.
     *
     * @return The next TarEntry in the archive, or null.
     * @throws IOException on error
     */
//	@Override
	public TarEntry getNextEntry() throws IOException {
        if (this.hasHitEOF) {
            return null;
        }

        if (this.currEntry != null) {
            long numToSkip = this.entrySize - this.entryOffset;

            if (this.debug) {
                System.err.println("TarInputStream: SKIP currENTRY '"
                        + this.currEntry.getName() + "' SZ "
                        + this.entrySize + " OFF "
                        + this.entryOffset + "  skipping "
                        + numToSkip + " bytes");
            }

            if (numToSkip > 0) {
                this.skip(numToSkip);
            }

            this.readBuf = null;
        }

        byte[] headerBuf = this.buffer.readRecord();

        if (headerBuf == null) {
            if (this.debug) {
                System.err.println("READ NULL RECORD");
            }
            this.hasHitEOF = true;
        } else if (this.buffer.isEOFRecord(headerBuf)) {
            if (this.debug) {
                System.err.println("READ EOF RECORD");
            }
            this.hasHitEOF = true;
        }

        if (this.hasHitEOF) {
            this.currEntry = null;
        } else {
//            this.currEntry = new TarEntry(headerBuf);
        	this.currEntry = new SFRMTarEntry(headerBuf);

            if (this.debug) {
                System.err.println("TarInputStream: SET CURRENTRY '"
                        + this.currEntry.getName()
                        + "' size = "
                        + this.currEntry.getSize());
            }

            this.entryOffset = 0;

            this.entrySize = this.currEntry.getSize();
        }

        if (this.currEntry != null && this.currEntry.isGNULongNameEntry()) {
            // read in the name
            StringBuffer longName = new StringBuffer();
//            byte[] buf = new byte[SMALL_BUFFER_SIZE];
			byte[] buf = new byte[(int)currEntry.getSize()]; 
            int length = 0;
            while ((length = read(buf)) >= 0) {
//                longName.append(new String(buf, 0, length));
				longName.append(new String(buf, 0, length, SFRMTarUtils.NAME_ENCODING)); 
            }
            getNextEntry();
            if (this.currEntry == null) {
                // Bugzilla: 40334
                // Malformed tar file - long entry name not followed by entry
                return null;
            }
            // remove trailing null terminator
            if (longName.length() > 0
                && longName.charAt(longName.length() - 1) == 0) {
                longName.deleteCharAt(longName.length() - 1);
            }
            this.currEntry.setName(longName.toString());
        }

        return this.currEntry;
    }
	
}
