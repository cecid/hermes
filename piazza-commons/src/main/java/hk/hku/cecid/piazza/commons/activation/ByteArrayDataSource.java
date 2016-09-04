/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.activation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;


/**
 * ByteArrayDataSource is an implementation of the javax.activation.DataSource 
 * that represents a data source of a byte array.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class ByteArrayDataSource implements DataSource {

    private String name;
    
    private String contentType;
    
    private byte[] bytes;
    
    /**
     * Creates a new instance of ByteArrayDataSource.
     * 
     * @param bytes the bytes source.
     */
    public ByteArrayDataSource(byte[] bytes) {
        this(bytes, null, null);
    }

    /**
     * Creates a new instance of ByteArrayDataSource.
     * 
     * @param bytes the bytes source.
     * @param contentType the content type.
     */
    public ByteArrayDataSource(byte[] bytes, String contentType) {
        this(bytes, contentType, null);
    }

    /**
     * Creates a new instance of ByteArrayDataSource.
     * 
     * @param bytes the bytes source.
     * @param contentType the content type.
     * @param name the name of the underlying content.
     */
    public ByteArrayDataSource(byte[] bytes, String contentType, String name) {
        this.bytes = bytes==null? new byte[]{}:bytes;
        this.contentType = contentType==null? "application/octet-stream":contentType;
        this.name = name==null? toString():name;
    }
    
    /**
     * Returns a new input stream representing the bytes source.
     * 
     * @return a new byte array input stream.
     * @see javax.activation.DataSource#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    /**
     * This method always throw IO exception.
     * 
     * @throws IOException as output stream is not supported by this data source.
     * @see javax.activation.DataSource#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("Output stream not supported");
    }

    /**
     * Gets the content type.
     * 
     * @return the content type.
     * @see javax.activation.DataSource#getContentType()
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Gets the name of the underlying content. 
     * 
     * @return the name of the underlying content.
     * @see javax.activation.DataSource#getName()
     */
    public String getName() {
        return name;
    }
}