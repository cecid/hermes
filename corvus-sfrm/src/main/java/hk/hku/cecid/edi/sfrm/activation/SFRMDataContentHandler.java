package hk.hku.cecid.edi.sfrm.activation;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;
 
// DataContentHandler class to work with activation framework.
// It is called when calling SFRMMessage.getContentStream() 
public class SFRMDataContentHandler implements DataContentHandler {

	private static final int BUFFER_SIZE = 8192;
	
	public Object getContent(DataSource ds) throws IOException {
		return ds.getInputStream();
	}

	public Object getTransferData(DataFlavor df, DataSource ds)
			throws UnsupportedFlavorException, IOException {
		return getContent(ds);
	}

	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {
				new ActivationDataFlavor(InputStream.class, "application/octet-stream", "SFRM Data Stream"),
				new ActivationDataFlavor(FileRegionDataSource.class, "application/octet-stream", "SFRM Data Stream"),
				new ActivationDataFlavor(EmptyDataSource.class, "application/octet-stream", "SFRM Data Stream"),
				new ActivationDataFlavor(String.class, "application/octet-stream", "SFRM Data Stream")};
	}

	public void writeTo(Object obj, String mime, OutputStream os) 
			throws IOException {

		if (obj instanceof InputStream) {
			InputStream is = (InputStream)obj;
	        byte[] buffer = new byte[BUFFER_SIZE];
	        int len;
	        while((len = is.read(buffer)) != -1)
	        	os.write(buffer, 0, len);
	        os.flush();
		} else if (obj instanceof FileRegionDataSource) {	
			FileRegionDataSource frds = (FileRegionDataSource)obj;
	    	WritableByteChannel wbc = Channels.newChannel(os);
	    	wbc.write(frds.getByteBuffer());
	        os.flush();	
		} else if (obj instanceof EmptyDataSource) {
			/*
			EmptyDataSource eds = (EmptyDataSource)obj;
			InputStream is = (InputStream) eds.getInputStream();
			
	        byte[] buffer = new byte[BUFFER_SIZE];
	        int len;
	        while ((len = is.read(buffer)) != -1) 
	            os.write(buffer, 0, len);

	        os.flush();
	        */
		} else if (obj instanceof String) {
			byte[] bytes = ((String)obj).getBytes();
			os.write(bytes);
			os.flush();
		} 
		else {
			throw new IOException("Unsupported class type - " + obj.getClass().getName());
		}
	}

}
