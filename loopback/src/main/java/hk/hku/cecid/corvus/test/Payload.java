/*
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved. This software is licensed
 * under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1] [1]
 * http://www.gnu.org/licenses/gpl.txt
 */
package hk.hku.cecid.corvus.test;

import java.io.InputStream;

/**
 * The <code>Payload</code> representing either a file or stream from
 * hermes.
 * 
 * @author Kochiu, Twinsen Tsang (modifiers)
 */
public class Payload {
	
	// The absolute file path of the payload.
	private String filePath;
	
	// The content type of the payload
	private String contentType;
	
	
	private InputStream inputStream;
	
	public Payload(String filePath, String contentType) {
		this.filePath = filePath;
		this.contentType = contentType; 
	}
	
	public Payload(InputStream inputStream, String contentType) {
		this.inputStream = inputStream;
		this.contentType = contentType;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}
	
	public String getContentType() {
		return contentType;
	}
}