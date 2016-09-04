/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.ByteBuffer;

/**
 * NIOHandler provides some convenient methods for handling some basic input and 
 * output funtions which has adopted the features of java NIO package.<br><br>
 *   
 * Creation Date: 5/10/2006
 * 
 * @author Twinsen
 * @version 1.0.0
 * @since	1.0.0
 * 
 * @see hk.hku.cecid.piazza.commons.io.IOHandler
 * @see java.nio.ByteBuffer
 * @see java.nio.MappedByteBuffer
 * @see java.nio.channels.Channels
 */
public class NIOHandler {
			
	/**
	 * The max buffer size of ByteBuffer is 10mb.
	 */
	public static final int MAX_BUFFER_SIZE = 10485760;
	
	/**
	 * The default page size for memory mapped stream. 
	 */
	public static final int DEFAULT_PAGE_SIZE = 65535; 
		
	/**
     * Pipes an file input stream to an output stream.<br> 
     * 
     * @param fins the file input stream to be read.
     * @param out  the output stream to be written.
     * @throws IOException if there is IO error occurred during the operation.
     */
	public static void pipe(FileInputStream fins
						   ,OutputStream	out) throws IOException{
		WritableByteChannel outChan = Channels.newChannel(out);
		FileChannel fileChan		= fins.getChannel();		
		fins.getChannel().transferTo(0, fileChan.size(), outChan);
		outChan = null;	// For gc
	}
	
	/**
     * Pipes an file input stream to an output stream.<br> 
     * 
     * @param fins the file input stream to be read.
     * @param out  the output stream to be written.
     * @throws IOException if there is IO error occurred during the operation.
     */
	public static void pipe(FileInputStream fins
					   	   ,OutputStream	out
					   	   ,int				startPosition
					   	   ,long			size) throws IOException{
		WritableByteChannel outChan = Channels.newChannel(out);
		fins.getChannel().transferTo(startPosition, size, outChan);
		outChan = null;	// For gc
	}
		
	 /**
     * Pipes an input stream to an output stream.<br>
     * 
     * @param ins 
     * 			the input stream to be read.
     * @param out 
     * 			the output stream to be written.
     * @since	
     * 			1.0.0
     * @throws IOException if there is IO error occurred during the operation.
     */
	public static void pipe(InputStream ins, OutputStream out) throws IOException {
    	if (ins == null || out == null)
    		return;    	
    	ReadableByteChannel inChan  = Channels.newChannel(ins);
    	WritableByteChannel outChan = Channels.newChannel(out); 
    	int size = ins.available();
    	// bound the buffer value.
    	if (size > NIOHandler.MAX_BUFFER_SIZE)
    		size = NIOHandler.MAX_BUFFER_SIZE;    	
    	ByteBuffer buffers = ByteBuffer.allocateDirect(size);
    	while(inChan.read(buffers) != -1){
    		buffers.flip();
    		outChan.write(buffers);    	
        	out.flush();
        	buffers.clear();
    	}    	  	    	
    	inChan = null;
    	outChan = null;
    	buffers = null;
    }
       
    /**
     * Reads an array of bytes from an input stream.<br>
     * 
     * @param ins the input stream to be read.
     * @return an array of bytes read from the specified input stream.
     * @throws IOException if there is IO error occurred during the operation.
     * 
     */
    public static byte[] readBytes(InputStream ins) throws IOException {
    	ByteArrayOutputStream out = new ByteArrayOutputStream(ins.available());
    	NIOHandler.pipe(ins, out);    
    	byte[] bytes =  out.toByteArray();
    	out.close();
    	return bytes;
    }
    
    /**
     * Reads an array of bytes from an input stream.<br>
     * 
     * @param ins the input stream to be read.
     * @return a bytes buffer read from the specified input stream.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static ByteBuffer readByteBuffer(InputStream ins) throws IOException {
    	System.out.println(ins.available());    	
    	ReadableByteChannel inChan  = Channels.newChannel(ins);
    	ByteBuffer buffers 			= ByteBuffer.allocate(ins.available());
    	inChan.read(buffers);
    	inChan = null;
    	return buffers;        
    }
            
    /**
     * Writes an array of bytes to an output stream.<br>
     * 
     * @param bytes an array of bytes to write.
     * @param out the output stream to be written.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static void writeBytes(byte[] bytes, OutputStream out) throws IOException {
        ByteArrayInputStream ins = new ByteArrayInputStream(bytes); 
        pipe(ins, out);
    }             
    
    /**
     * Writes a byte buffer to an output stream.
     * 
     * @param src a byte buffer to write.
     * @param out the output stream to be written.
     * @throws IOException if there is IO error occurred during the operation.
     */
    public static void writeBytes(ByteBuffer src, OutputStream out) throws IOException {
    	WritableByteChannel outChan = Channels.newChannel(out);
    	outChan.write(src);
    	outChan = null;
    }             
}

