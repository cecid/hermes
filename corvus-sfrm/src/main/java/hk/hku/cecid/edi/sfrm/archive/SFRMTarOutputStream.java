package hk.hku.cecid.edi.sfrm.archive;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.tools.tar.TarConstants;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;

/**
 * @author Patrick Yip
 *
 */
public class SFRMTarOutputStream extends TarOutputStream {

	public SFRMTarOutputStream(OutputStream os) {
		super(os);
	}
	
	public SFRMTarOutputStream(OutputStream os, int blockSize){
		super(os, blockSize);
	}
	
	public SFRMTarOutputStream(OutputStream os, int blockSize, int recordSize){
		super(os, blockSize, recordSize);
	}
	
	/**
     * Put an entry on the output stream. This writes the entry's
     * header record and positions the output stream for writing
     * the contents of the entry. Once this method is called, the
     * stream is ready for calls to write() to write the entry's
     * contents. Once the contents are written, closeEntry()
     * <B>MUST</B> be called to ensure that all buffered data
     * is completely written to the output stream.
     *
     * @param entry The TarEntry to be written to the archive.
     * @throws IOException on error
     */
//	@Override
	public void putNextEntry(TarEntry entry) throws IOException {
        if (entry.getName().length() >= TarConstants.NAMELEN) {

            if (longFileMode == LONGFILE_GNU) {
                // create a TarEntry for the LongLink, the contents
                // of which are the entry's name
            	TarEntry longLinkEntry = new SFRMTarEntry(TarConstants.GNU_LONGLINK,
                      TarConstants.LF_GNUTYPE_LONGNAME);

//                longLinkEntry.setSize(entry.getName().length() + 1);
				byte[] nameBytes = entry.getName().getBytes(SFRMTarUtils.NAME_ENCODING);
				longLinkEntry.setSize(nameBytes.length + 1); 
				
                putNextEntry(longLinkEntry);
//                write(entry.getName().getBytes());
				write(nameBytes); 
                write(0);
                closeEntry();
            } else if (longFileMode != LONGFILE_TRUNCATE) {
                throw new RuntimeException("file name '" + entry.getName()
                                             + "' is too long ( > "
                                             + TarConstants.NAMELEN + " bytes)");
            }
        }

        entry.writeEntryHeader(this.recordBuf);
        this.buffer.writeRecord(this.recordBuf);

        this.currBytes = 0;

        if (entry.isDirectory()) {
            this.currSize = 0;
        } else {
            this.currSize = entry.getSize();
        }
        currName = entry.getName();
    }
	
}
