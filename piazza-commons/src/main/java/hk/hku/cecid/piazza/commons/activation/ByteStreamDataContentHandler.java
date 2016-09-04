/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.activation;

import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.util.Instance;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

/**
 * ByteStreamDataContentHandler is an implementation of the javax.activation.DataContentHandler 
 * that represents a data content handler of a byte stream.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class ByteStreamDataContentHandler implements DataContentHandler {
    
    private static final DataFlavor[] flavors = new DataFlavor[] { new ActivationDataFlavor(InputStream.class, "application/octet-stream", "Byte Stream") };;

    /**
     * Creates a new instance of ByteStreamDataContentHandler.
     */
    public ByteStreamDataContentHandler() {
        super();
    }

    /**
     * Returns a byte stream, which is described by the first DataFlavor returned 
     * by the getTransferDataFlavors() method, representing the specified data. 
     *  
     * @param ds the data source representing the data to be converted.
     * @return the byte stream representing the data.
     * @throws IOException if unable to convert the data.
     * @see javax.activation.DataContentHandler#getContent(javax.activation.DataSource)
     */
    public Object getContent(DataSource ds) throws IOException {
        return ds.getInputStream();
    }

    /**
     * Returns a byte stream which represents the data to be transferred,  
     * ignoring the specified flavor. 
     * 
     * @param df the data flavor representing the requested type.
     * @param ds the DataSource representing the data to be converted.
     * @return the byte stream representing the data.
     * @throws IOException if unable to convert the data.
     * @see javax.activation.DataContentHandler#getTransferData(java.awt.datatransfer.DataFlavor, javax.activation.DataSource)
     */
    public Object getTransferData(DataFlavor df, DataSource ds) throws IOException {
            return getContent(ds);
    }

    /**
     * Returns an array of DataFlavor objects indicating the flavors the data 
     * can be provided in.
     * 
     * @return an array of DataFlavor objects.
     * @see javax.activation.DataContentHandler#getTransferDataFlavors()
     */
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    /**
     * Converts the object to a byte stream of the specified MIME type and 
     * write it to the output stream. 
     * 
     * @param obj the object to be converted.
     * @param mimeType the requested MIME type of the resulting byte stream.
     * @param os the output stream into which to write the converted byte stream. 
     * @throws IOException if unable to convert the given object.
     * @see javax.activation.DataContentHandler#writeTo(java.lang.Object, java.lang.String, java.io.OutputStream)
     */
    public void writeTo(Object obj, String mimeType, OutputStream os)
        throws IOException {
        
        byte[] bytes = null;
        if (obj instanceof InputStream) {
            IOHandler.pipe((InputStream) obj, os);
            return;
        }
        else if (obj instanceof DataSource) {
            IOHandler.pipe(((DataSource)obj).getInputStream(), os);
            return;
        }
        else if (obj instanceof byte[]) {
            bytes = (byte[])obj;
        } 
        else if (obj instanceof String) {
            bytes = ((String)obj).getBytes();
        }
        else {
            try {
                String methodName = "writeTo"; 
                Class[] outsParam = new Class[]{OutputStream.class};
                Instance instance = new Instance(obj);
                if (instance.isMethodExist(methodName, outsParam)) {
                    instance.invoke(methodName, outsParam, new Object[]{os});
                }
                return;
            }
            catch (Exception e) {
                bytes = null;
            }
        }
        
        if (bytes==null) {
            throw new IOException("Unable to convert object of type " + (obj==null? "null":obj.getClass().getName()));
        }
        else {
            os.write(bytes);
        }
    }
}
