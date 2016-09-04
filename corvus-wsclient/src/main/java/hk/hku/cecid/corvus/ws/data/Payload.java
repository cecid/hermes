/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws.data;

import java.io.InputStream;


/**
 * The <code>Payload<code> is a data object representing a payload
 * in the SOAP message.
 * 
 * @author 	Twinsen Tsang
 * @version	1.0.0
 * @since	Elf 0818
 */
public class Payload implements Data{
	
	/**
	 * The payload filepath.
	 */
	private String filePath;
	
	/**
	 * The content type of the payload.
	 */
	private String contentType;
	
	/**
	 * The input stream of the payload.
	 */
	private InputStream inputStream;
	
	/**
	 * Explicit Constructor.
	 * 
	 * @param filePath		The payload filepath.
	 * @param contentType	The content type of payload. 
	 */
	public Payload(String filePath, String contentType) {
		this.filePath = filePath;
		this.contentType = contentType; 
	}
	
	/**
	 * 
	 * 
	 * @param inputStream
	 * @param contentType
	 */
	public Payload(InputStream inputStream, String contentType) {
		this.inputStream = inputStream;
		this.contentType = contentType;
	}
	
	/** 
	 * @return	The filepath of the payload.
	 */
	public String getFilePath() {
		return filePath;
	}
	
	/** 
	 * 	Set filepath of the payload. 
	 * (This method is open to set the filename of payload for message receiver)
	 */
	public void setFilePath(String filepath) {
		this.filePath = filepath;
	}
	
	/** 
	 * @return  The input stream of the payload
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/** 
	 * @return The content type of payload. 
	 */
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * toString method
	 */
	public String toString(){
		String ret = "";
		ret += "Payload\n";
		ret += "Payload path: " + this.filePath;
		ret += "Payload type: " + this.contentType; 
		return ret;
	}
}
