/*
 *  =====
 *
 *  $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/PartialInputStream.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 *  Code authored by:
 *
 *  cyng [2002-07-29]
 *
 *  Code reviewed by:
 *
 *  username [YYYY-MM-DD]
 *
 *  Remarks:
 *
 *  =====
 */
package hk.hku.cecid.ebms.pkg;
import java.io.IOException;
import java.io.InputStream;
/**
 * This is an implementation of <code>java.io.InputStream</code> that
 * represents part of data read from an <code>InputStream</code>.
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
class PartialInputStream extends InputStream {
    private final InputStream in;

    private final long length;

    private long currentRead;

    /**
     * Constructor for the PartialInputStream object
     *
     * @param in the origninal input stream
     * @param offset the offset the read data from the stream.
     * @param length the length of the data to be read
     * @exception IOException Description of the Exception
     */
    PartialInputStream(InputStream in, long offset, long length)
             throws IOException {
        this.in = in;
        this.length = length;
        long skip = in.skip(offset);
        long actualSkip = skip;
        while (actualSkip >= 0 && skip < offset) {
            actualSkip = in.skip(offset - skip);
            if (actualSkip > 0) {
                skip += actualSkip;
            }
        }
        currentRead = 0;
    }

    /**
     * close the stream
     *
     * @exception IOException thrown when IO error occur during closing
     */
    public void close() throws IOException {
        in.close();
    }

    /**
     * read a byte from the stream
     *
     * @return the byte in int value from the stream.
     * @exception IOException thrown when IO error occur during reading.
     */
    public int read() throws IOException {
        if (currentRead < length) {
            int r = in.read();
            if (r != -1) {
                currentRead++;
            }
            return r;
        } else {
            return -1;
        }
    }
}

