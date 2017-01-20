package hk.hku.cecid.edi.sfrm.io;

import java.io.IOException;
import java.io.InputStream;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;

/**
 * A ByteBufferInputStream contains an internal buffer that contains direct
 * or non-direct byte buffer that may be read from the stream.<br><br>
 *   
 * Creation Date: 24/10/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since 	1.0.1
 */
public class ByteBufferInputStream extends InputStream {

	/**
	 * The internal byte buffer.
	 */
	protected ByteBuffer buf;
	
	/**
	 * The internal file channel.
	 */
	private ReadableByteChannel channel;

	 /**
     * Creates a <code>ByteBufferInputStream</code>
     * so that it uses <code>buf</code> as its
     * byte buffer. 
     * The buffer array is not copied. 
     *
     * @param buf 
     * 			the input buffer.
     */
	public ByteBufferInputStream(ByteBuffer buffer) {
		this.buf = buffer;
	}

	/**
     * Returns the number of bytes that can be read from this input 
     * stream without blocking. 
     * The value returned is limit of byte buffer. 
     * which is the number of bytes remaining to be read from the input buffer.
     *
     * @return  the number of bytes that can be read from the input stream
     *          without blocking.
     */
	public int available() throws IOException {
		return buf.remaining();
	}
	
	/**
	 * Returns the readable byte channel object associated with this byte 
	 * buffer input stream. 
	 * 
	 * @return a readable byte channel for this byte buffer.
	 * 
	 * @see java.nio.channels.Channels
	 */	 
	public ReadableByteChannel getChannel(){
		synchronized (this) {
		    if (channel == null)
		    	channel = new ByteBufferChannelImpl(this);
		    return channel;
		}
	}
	
	/**
	 * Close the stream.
	 */
	public void close() throws IOException {
		buf.clear();
		buf.position(0);
		buf.flip();
		buf = null;
		// Close the channel if exists.
		if (this.channel != null)
			if (this.channel.isOpen())
				this.channel.close();
	}
	
	/**
	 * Mark this stream.
	 */
	public synchronized void mark(int readlimit) {
		buf.mark();
	}

	/**
	 * Mark is support by this InputStream.  
	 * 
	 * @return always true
	 */
	public boolean markSupported() {
		return true;
	}
	
	/**
	 * Reads the next byte of data from this input stream. The value byte is
	 * returned as an int in the range 0 to 255. If no byte is available because
	 * the end of the stream has been reached, the value -1 is returned.
	 */
	public int read() throws IOException {
		if (buf.position() > buf.limit())
			return -1;
		return this.buf.get();
	}

	/**
	 * 
	 */
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	/**
	 * 
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0)
				|| ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		}
		int pos = buf.position();
		int count = buf.limit();
		if (pos >= count)
			return -1;
		if (pos + len > count) {
			len = count - pos;
		}
		if (len <= 0) {
			return 0;
		}
		buf.get(b, off, len);
		return len;
	}

	/**
     * Resets the buffer to the marked position.  The marked position
     * is 0 unless another position was marked or an offset was specified
     * in the constructor.
     */
	public synchronized void reset() throws IOException {
		this.buf.reset();
	}

	/**
	 * 
	 */
	public long skip(long n) throws IOException {
		super.skip(n);
		int pos = buf.position();
		int newPos = buf.position() + (int) n;
		int count = buf.limit();
		if (newPos > count)
			newPos = count;
		buf.position(newPos);
		return newPos - pos;
	}
	
	/**
	 * 
	 * Creation Date: 24/10/2006
	 * 
	 * @author Twinsen Tsang
	 * @version 1.0.0
	 * @since	1.0.1
	 */
	private static class ByteBufferChannelImpl extends
			AbstractInterruptibleChannel // Not really interruptible
			implements ReadableByteChannel {

		ByteBufferInputStream in;

		private static final int TRANSFER_SIZE = 8192;
		private byte buf[] = new byte[0];		
		private Object readLock = new Object();

		ByteBufferChannelImpl(ByteBufferInputStream in) {
			this.in = in;
		}

		public int read(ByteBuffer dst) throws IOException {
			if (dst == null)
				throw new NullPointerException();
			int len = dst.remaining();
			int totalRead = 0;
	        int bytesRead = 0;
			synchronized (readLock) {
				if (len >= in.buf.remaining() && in.buf.remaining() > 0){
					totalRead = in.buf.remaining();  
					dst.put(in.buf);					
				}
				while (totalRead < len){
                    int bytesToRead = Math.min((len - totalRead), TRANSFER_SIZE);
                    if (buf.length < bytesToRead)
                        buf = new byte[bytesToRead];
                    if ((totalRead > 0) && !(in.available() > 0))
                        break; // block at most once
                    try {
                        begin();
                        bytesRead = in.read(buf, 0, bytesToRead);
                    } finally {
                        end(bytesRead > 0);
                    }
                    if (bytesRead < 0)
                        break;
                    else
                        totalRead += bytesRead;
                    dst.put(buf, 0, bytesRead);
                }
                if ((bytesRead < 0) && (totalRead == 0))
                    return -1;
			}
            return totalRead;			
		}

		protected void implCloseChannel() throws IOException {
			in.close();
		}
	}
}
;
