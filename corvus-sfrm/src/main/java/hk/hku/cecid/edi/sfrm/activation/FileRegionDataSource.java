package hk.hku.cecid.edi.sfrm.activation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.activation.FileDataSource;

import hk.hku.cecid.edi.sfrm.io.ByteBufferInputStream;

/**
 * ByteBufferDataSource is an implementation of the javax.activation.DataSource 
 * that represents a data source of a region in the file.
 * 
 * Creation Date: 2/11/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.2
 */
public class FileRegionDataSource extends FileDataSource {

	/**
	 * The start pos of the File region.
	 */
	private long position;
	
	/**
	 * The size of the File region.
	 */
	private long size;
	
	private ByteBuffer bb;
	private boolean isAllocated = false;
	
	/**
	 * Explicit Constructor.
	 * 
	 * @param file
	 * 				The file associated to this datasource.
	 * @param position 
	 * 				The start position of the region.
	 * @param size
	 * 				The size of the region.
	 * @since	
	 * 				1.0.2
	 */
	public FileRegionDataSource(File file, long position, long size) {
		super(file);
		this.position = position;
		this.size = size;
	}

	/**
	 * Explicit Constructor. 
	 * 
	 * @param filepath
	 * 				The file path associated to this datasource.
	 * @param position
	 * 				The start position of the region.
	 * @param size 
	 * 				The size of the region
	 * @since		
	 * 				1.0.2
	 */
	public FileRegionDataSource(String filepath, long position, long size) {
		super(filepath);
		this.position = position;
		this.size = size;
	}

	/**
     * This method will return an InputStream representing the the data 
     * and will throw an IOException if it can not do so. 
     * This method will return a new instance of ByteBufferInputStream 
     * with the direct byte buffer inside for each invocation.
     * 
     * @since		
     * 				1.0.2
     * 
     * @see java.nio.channels.FileChannel
     * @see hk.hku.cecid.edi.sfrm.io.ByteBufferInputStream  
     */
	
	public InputStream getInputStream() throws IOException {
		
		// FOUND IN V1.0.2 - SLOW USING FileChannel.map 
		/*MappedByteBuffer mbb =  
			fc.map(FileChannel.MapMode.READ_ONLY, this.position, this.size);
		if (!mbb.isLoaded())
			mbb.load();		*/
		
		if(isAllocated == false){
			FileInputStream fis = new FileInputStream(this.getFile()); 
			FileChannel fc 		= fis.getChannel();
			try{
				bb = ByteBuffer.allocateDirect((int)this.size);
			}catch(OutOfMemoryError ome){
				System.gc();
				throw new IOException("OutOfMemoryError: Insufficient memory for allocating direct byte buffer");
			}
			
			fc.read(bb, this.position);
			// For fast gc
			fc.close();
			fc = null;
			fis.close();
			fis = null;
			isAllocated = true;
		}
		
		bb.flip();
		bb.mark();
		
		return new ByteBufferInputStream(bb);
	}
	
	/**
	 * This method always throw IO exception.
     * 
     * @throws IOException as output stream is not supported by this data source.
     * @see javax.activation.DataSource#getOutputStream()
     * 
     * @since	
     * 				1.0.2
     */
	public OutputStream getOutputStream() throws IOException {
		return super.getOutputStream();
	}
	
	/**
     * This method will return an ByteBuffer representing the the data region
     * and will throw an IOException if it can not do so or OutOfMemory error. 
     * For OutOfMemory error in direct memory usage, it's still an issue in Java.
     * http://bugs.sun.com/view_bug.do?bug_id=5025281
     * Properly reuse of the memory and set runtime parameter may solve the problem. 
     * 
     * @ByteBuffer the ByteBuffer of the region, its cursor position has been flipped.
     * 
     * @since 2.0		 			
     * 
     * @see java.nio.channels.FileChannel  
     */	
	public ByteBuffer getByteBuffer() throws IOException {
		FileInputStream fis = new FileInputStream(this.getFile()); 
		FileChannel fc 		= fis.getChannel();

		// FOUND IN V1.0.2 - SLOW USING FileChannel.map 
		/*MappedByteBuffer mbb =  
			fc.map(FileChannel.MapMode.READ_ONLY, this.position, this.size);
		if (!mbb.isLoaded())
			mbb.load();		*/
			
		ByteBuffer bb;
		try {
			bb = ByteBuffer.allocateDirect((int)this.size);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			throw new IOException("Cannot allocate direct memory!!");
		}
			
		fc.read(bb, this.position);			
		bb.flip();
		bb.mark();
	
		fc.close();
		fis.close();

		return bb;
	}
	
	protected void finalize(){
		bb = null;
	}
}
