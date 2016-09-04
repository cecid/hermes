package hk.hku.cecid.edi.sfrm.pkg;

import hk.hku.cecid.edi.sfrm.pkg.SFRMAcknowledgementBuilder;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;

public class SFRMAcknowledgementParser {
	private String xmlContent;
	private Document doc;
		
	public SFRMAcknowledgementParser(String xmlContent) throws DocumentException{
		this.xmlContent = xmlContent;
		init();
	}
	
	private void init() throws DocumentException{
		SAXReader reader = new SAXReader();
		StringReader strReader = new StringReader(xmlContent);
		doc = reader.read(strReader);
	}
	
	/**
	 * Get the number of message in the acknowledgement
	 * @return number of messages
	 */
	public int getNumMessages(){
		List nodes = doc.selectNodes(SFRMAcknowledgementBuilder.MESSAGE_XPATH);
		return nodes.size();
	}
	
	/**
	 * Get the number of message segment for specific message
	 * @param messageId message ID
	 * @return number of message segments
	 */
	public int getNumMessageSegment(String messageId){
		String xpath = SFRMAcknowledgementBuilder.getSegmentsXPath(messageId);
		List nodes = doc.selectNodes(xpath);
		return nodes.size();
	}
	
	/**
	 * Get the list of message ID in the acknowledgement
	 * @return list of message ID
	 */
	public List<String> getMessagesIDs(){
		List nodes = doc.selectNodes(SFRMAcknowledgementBuilder.MESSAGE_XPATH);
		ArrayList<String> list = new ArrayList<String>(); 
		for(int i=0 ; nodes.size() > i ;i++){
			Element ele = (Element) nodes.get(i);
			list.add(ele.attributeValue(SFRMAcknowledgementBuilder.ID_ATTR));
		}
		return list;
	}
	
	/**
	 * Get a list of mesasge segmnet num for the specific message
	 * @param messageId message ID
	 * @return list of message segment number
	 */
	public List<Integer> getMessageSegmentNums(String messageId){
		String xpath = SFRMAcknowledgementBuilder.getSegmentsXPath(messageId);
		List nodes = doc.selectNodes(xpath);
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i=0; nodes.size() > i; i++){
			Element ele = (Element) nodes.get(i);
			list.add(new Integer(ele.attributeValue(SFRMAcknowledgementBuilder.NUM_ATTR)));
		}
		return list;
	}
	
	/**
	 * Get status for a specific message
	 * @param messageId message ID
	 * @return message status
	 */
	public String getMessageStatus(String messageId){
		String xpath = SFRMAcknowledgementBuilder.getMessageXPath(messageId);
		Element msgNode = (Element) doc.selectSingleNode(xpath);
		return msgNode.attributeValue(SFRMAcknowledgementBuilder.STATUS_ATTR);		
	}
	
	/**
	 * Get status for specific message segment
	 * @param messageId message ID
	 * @param segmentNum segment number
	 * @return message segment status
	 */
	public String getMessageSegmentStatus(String messageId, int segmentNum){
		String xpath = SFRMAcknowledgementBuilder.getMessageSegmentXPath(messageId, segmentNum);
		Element msgNode = (Element) doc.selectSingleNode(xpath);
		return msgNode.attributeValue(SFRMAcknowledgementBuilder.STATUS_ATTR);
	}
	
}