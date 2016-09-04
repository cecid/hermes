/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package hk.hku.cecid.piazza.commons.activation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * InputStreamDataSource is an implementation of the javax.activation.DataSource 
 * It is designed to be used by SOAPMailSender as a bridge of InputStream and OutputStream 
 * for base64 encoding transformation.
 * 
 * @author Philip Wong
 * @see hk.hku.cecid.piazza.commons.soap.SOAPMailSender
 */
public class InputStreamDataSource implements DataSource {

	private InputStream _is;
	private String _contentType;
	private OutputStream _os;
	private String _name;
	
    /**
     * Creates a new instance of InputStreamDataSource by BodyPart.
     * 
     * @param is the input stream.
     * @param contentType the content type.
     * @param name the filename if any.
     */
	public InputStreamDataSource(InputStream is, String contentType, String name) {
		_is = is;
		_contentType = contentType;
		_name = name;
	}

    /**
     * Gets the input stream.
     * 
     * @return the input stream when this data source created.
     */
	public InputStream getInputStream() throws IOException {
		_is.reset(); // If the inputstream is not reset, it cause problem when signing Smime message
		return _is;
	}

    /**
     * Gets the content type.
     * 
     * @return the content type when this data source created.
     */
	public String getContentType() {
		return _contentType;
	}

    /**
     * Gets the filename.
     * 
     * @return the content type when this data source created.
     */
	public String getName() {
		return _name;
	}

    /**
     * Returns new byte array from a new BytesArrayOutStream always.
	 * Logically, it is required to copy binary content from the input stream to output stream.
	 * But it is found the same result without touching binary content.  
     * 
     * @return the content type when this data source created.
     */
	public OutputStream getOutputStream() throws IOException {
	    if (_os == null) {
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	/*
	    	byte[] b = new byte[8192]; 
	    	int count;
	    	while ((count = _is.read(b)) > 0) { 
	    		baos.write(b, 0, count); 
	    		baos.flush(); 
	    	} 
	    	*/	    	
	    	baos.writeTo(_os);
	    }
	    return _os;	
		
	}
	
}
