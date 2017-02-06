/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.ConnectException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.w3c.tidy.Tidy;

/** 
 * The <code>PartnershipOpVerifer</code> is an helper class for verifying whether the partnership
 * operation has been executed successfully. Since H2O does not have any build-in SOAP-based 
 * web service for managing the partnerships under remote behavior, The PartnershipOpVerifer 
 * acts as a validator for validating the <b>HTML content</b> returning from the H2O
 * administration web page.
 * <br/><br>
 * The main method of this class is {@link #validate(InputStream)} and the arugment 
 * is the input stream. The input stream SHOULD contains the HTML content after you 
 * executed add/delete/update partnership in either AS2/EBMS partnership administration page.
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0 $CHANGE FREQUENTLY$
 * @since   H2O 28/11
 */
public class PartnershipOpVerifer 
{
	// Instance logger.
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/* The constant HTML content representing the partnership is added successfully */
	public static final String OP_ADD_SUCCESS 	 = "Partnership added successfully";
	
	/* The constant HTML content representing the partnership is updated successfully */
	public static final String OP_UPDATE_SUCCESS = "Partnership updated successfully";
	
	/* The constant HTML content representing the partnership is deleted successfully */
	public static final String OP_DELETE_SUCCESS = "Partnership deleted successfully";
	
	/* The constant HTML content representing the partnership page is ready, no operation executed. */ 
	public static final String OP_NO			 = "Ready";
	
	/* The constant ERROR message notifying the user to view the log for the root cause why the 
	 * partnership failed to execute.
	 */
	private static final String CHECK_LOG_RESULTSTR = 
		"Please check the H2O log to see why the partnership operation fails";
	
	// The factory for creating SAX reader.
	private final SAXParserFactory spf = SAXParserFactory.newInstance();
	
	// The external library - Tidy for reformat HTML input to well-formed XHTML. 
	// private Tidy tidy = new Tidy();
		
	/** 
	 * The <code>PageletContentVerifer</code> is SAX Default handler for extracting the
	 * result content after executing a partnership operation. 	 
	 */
	/*
	 * The format of HTML we want to capture : 
	 * 
	 * <html> 
	 * 		.
	 * 		.
	 * 		.
	 * <td>
	 * 	<a name="message"></a>
	 * 	<b>Message: </b><font color="blue">Partnership added successfully</font>
	 * </td>
	 */
	private static class PageletContentVerifer extends DefaultHandler 
	{
		private String result = "";		
		private boolean opRanWithNoError = false;		
		private boolean capturedEnabled = false;
				
		public void startElement(String uri, String localName, String name,	Attributes attributes) 
			throws SAXException 
		{
			if (!name.equals("a") || attributes.getLength() < 1) return;
			String lname = attributes.getLocalName(0);
			String value = attributes.getValue(0);
			
			if (lname.equals("id") && value.equals("message"))
				this.capturedEnabled = true;
		}
		
		public void characters(char[] ch, int start, int length) 
			throws SAXException 
		{
			if (capturedEnabled){
				String s = new String(ch, start, length).replaceAll("\\A\\s|\\s\\z|\\n", " ");
				result += s;
			}
		}
		
		public void endElement(String uri, String localName, String name)
			throws SAXException 
		{
			if (name.equals("td") && this.capturedEnabled){			
				this.capturedEnabled = false;											
			}	
			
			// Now we process the result, first we split the str by :			
			String [] token = result.split(":");			
			if (token.length > 0 && token.length != 2){
				// Exception occurred from the HTML or some any strange error
				if (token[0].equalsIgnoreCase(OP_NO)){
					// Replace with meaning error string.
					this.result = CHECK_LOG_RESULTSTR;
				}					 
			}
			else if (token.length == 2){
				token[1] = token[1].trim(); // trim itself
				opRanWithNoError = (
					token[1].equalsIgnoreCase(OP_ADD_SUCCESS) 	 ||
					token[1].equalsIgnoreCase(OP_UPDATE_SUCCESS) ||
					token[1].equalsIgnoreCase(OP_DELETE_SUCCESS)
				);
			}
			else {
				throw new SAXException("Unknown result content: " + result);
			}
		}
		
		public boolean 	getIsVerifiedWithNoError()	{ return this.opRanWithNoError; }		
		public String 	getVerifiedMessage()		{ return this.result; }
	}
	
	/**  
	 * Default constructor.
	 */
	public PartnershipOpVerifer(){
		this.spf.setNamespaceAware(true);
	}
	
	/**
	 * Validate the HTML content received after executed partnership operation. The content
	 * is passed as a input stream <code>ins</code>.
	 * <br/><br/>
	 * This operation is quite expensive because it first transform the whole HTML content
	 * received to a well-formed XHTML before parsing by the SAX Parser.  
	 * 
	 * @param ins The HTML content to validate the result of partnership operation  
	 * @throws SAXException 
	 * 			<ol>
	 * 				<li>When unable to down-load the HTML DTD from the web. Check your Internet connectivity</li>
	 * 				<li>When IO related problems occur</li>
	 * 			</ol>
	 * @throws ParserConfigurationException
	 * 			When SAX parser mis-configures. 
	 */
	public void validate(InputStream ins) throws SAXException, ParserConfigurationException
	{ 		
		if (ins == null)
			throw new NullPointerException("Missing 'input stream' for validation");
		try{
			// TODO: SLOW, It requires two full-scan transformation to find the result of the partnership operation. 			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			/* Transforms to well-formed XHTML */	
			Tidy t = new Tidy();
			t.setXHTML(true); t.setQuiet(true); t.setShowWarnings(false);
			t.parse(ins, baos);
			
			// For debug purpose
			// System.out.println(hk.hku.cecid.piazza.commons.io.IOHandler.readString(ins, null));
			// System.out.println("Test: " + new String(baos.toByteArray(), "UTF-8"));			 
			
			/* Pipe to another input stream */
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			// Create a custom SAX handler for parsing the partnership op result from the HTML.
			PageletContentVerifer verifer = new PageletContentVerifer();
			// Create SAX parser for parsing the HTML coming back after executing partnership operation.
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			SAXParser parser = spf.newSAXParser();
			parser.parse(bais, verifer);				
						
			boolean result = verifer.getIsVerifiedWithNoError();
			if (!result) throw new SAXException("Fail to execute partnership operation as : " + verifer.getVerifiedMessage());
		}
		catch(ConnectException cex){			
			cex.printStackTrace();
			throw new SAXException("Seems unable to download correct DTD from the web, behind proxy/firewall?", cex);			
		}				
		catch(IOException ioex){
			throw new SAXException("IO Error during SAX parsing.", ioex);
		}
	}
}
