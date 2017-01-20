package hk.hku.cecid.edi.sfrm.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import hk.hku.cecid.edi.sfrm.io.NullInputStream;
import hk.hku.cecid.edi.sfrm.io.NullOutputStream;

/**
 * NullDataSource is an implementation of the javax.activation.DataSource 
 * that represents a empty, but not null. datasource 
 * 
 * Creation Date: 7/11/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.2
 */
public class EmptyDataSource implements DataSource {
			
	/**
	 * The name of data source.
	 */
	private String name;
	
	/**
	 * The content type of data source.
	 */
	private String contentType;	
	
	/**
	 * Default Constructor.<br><br>
	 * 
	 * The name is set to empty and the content 
	 * type is set to <code>application/octect-stream</code>.  
	 * 
	 * @since	
	 * 			1.0.2
	 */
	public EmptyDataSource(){
		this.name = "";
		// TODO: Use constant table
		this.contentType = "application/octet-stream";
	}
	
	/** 
	 * Explicit Constructor.
	 * 
	 * @param name
	 * 			The name of the datasource.
	 * @param contentType
	 * 			The content type of the datasource.
	 * @since	
	 * 			1.0.2
	 */	
	public EmptyDataSource(String name, String contentType) {
		super();
		this.name = name;
		this.contentType = contentType;
	}

	/**
     * This method will return an InputStream representing the the data 
     * and will throw an IOException if it can not do so.  
     * This method will return a new instance of NullInputStream 
	 * with each invocation
     * 
     * @since		
     * 			1.0.2
     * 
     * @see hk.hku.cecid.edi.sfrm.io.NullInputStream  
     */
	public InputStream getInputStream() throws IOException {
		return new NullInputStream();
	}
	
	/**
	 * This method always throw IO exception.
     * 
     * @since	
     * 			1.0.2
     * 
     * @throws IOException as output stream is not supported by this data source.
     * @see javax.activation.DataSource#getOutputStream()
     */
	public OutputStream getOutputStream() throws IOException {
		return new NullOutputStream();
	}

	/**
	 * Get the name of content type.
	 * 
	 * @since	
	 * 			1.0.2
	 */
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * Get the name of the datasource.
	 * 
	 * @since	
	 * 			1.0.2
	 */
	public String getName() {
		return this.name;
	}
}
