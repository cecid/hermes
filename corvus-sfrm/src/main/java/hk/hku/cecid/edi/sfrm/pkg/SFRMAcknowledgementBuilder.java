package hk.hku.cecid.edi.sfrm.pkg;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import java.util.List;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * @author Patrick Yip
 * To build the SFRM Acknowledgement content body
 */
public class SFRMAcknowledgementBuilder {
	public static final String MESSAGES_TAG = "messages";
	public static final String MESSAGE_TAG = "message";
	public static final String STATUS_TAG = "status";
	public static final String SEGMENTS_TAG = "segments";
	public static final String SEGMENT_TAG = "segment";
	public static final String NUM_ATTR = "num";
	public static final String ID_ATTR = "id";
	public static final String STATUS_ATTR = "status";
	public static final String PARAM_TOKEN = "?";
	public static final String MESSAGE_XPATH = "//" + MESSAGES_TAG + "/" + MESSAGE_TAG;
	public static final String MESSAGE_SEGMENT_XPATH_PARAM = "//" + MESSAGES_TAG + "/" + MESSAGE_TAG + "[@" + ID_ATTR + "='" + PARAM_TOKEN + "']/" + SEGMENTS_TAG + "/" + SEGMENT_TAG;
	public static final String MESSAGE_NODE_XPATH_PARAM = "//" + MESSAGES_TAG + "/" + MESSAGE_TAG + "[@" + ID_ATTR + "='" + PARAM_TOKEN + "']";
	public static final String MESSAGE_SEGMENT_NODE_XPATH_PARAM = MESSAGE_SEGMENT_XPATH_PARAM + "[@" + NUM_ATTR + "='" + PARAM_TOKEN + "']";
	private Document doc;
	private Element root;
	/**
	 * Constructor for Acknowledgement Builder, create an empty acknowledgement content
	 */
	public SFRMAcknowledgementBuilder(){
		init();
	}
	
	private void init(){
		doc = DocumentHelper.createDocument();
		root = doc.addElement(MESSAGES_TAG);
	}
	
	private Element findElementByAttributeXPath(String xpath, String attrName, String attrValue){
		List results = doc.selectNodes(xpath);
		for(int i=0; results.size() > i;i++){
			Element element = (Element) results.get(i);
			String attrActualValue = element.attributeValue(attrName);
			if(!StringUtilities.isEmptyString(attrActualValue) && attrActualValue.equals(attrValue)){
				return element;
			}
		}
		return null;
	}
	
	/**
	 * Find the message element by providing the message ID
	 * @param messageId message ID
	 * @return Element contain that message ID
	 */
	private Element findMessage(String messageId){		
		return findElementByAttributeXPath(MESSAGE_XPATH, ID_ATTR, messageId);
	}
	
	private Element findSegment(String messageId, int segNum){
		String xpath = getSegmentsXPath(messageId);
		return findElementByAttributeXPath(xpath, NUM_ATTR, Integer.toString(segNum));
	}
	
	private Element createMessageElement(String messageId, String status){
		Element msgEle = root.addElement(MESSAGE_TAG);
		msgEle.addAttribute(ID_ATTR, messageId);
		msgEle.addAttribute(STATUS_ATTR, status);
		return msgEle;
	}
	
	private void modifyMessageElement(Element msgEle, String messageId, String status){
		msgEle.attribute(ID_ATTR).setValue(messageId);
		msgEle.attribute(STATUS_ATTR).setValue(status);
	}
	
	private Element addSegment(Element segmentsEle, int segNum, String status){
		Element segEle = segmentsEle.addElement(SEGMENT_TAG)
			.addAttribute(NUM_ATTR, Integer.toString(segNum))
			.addAttribute(STATUS_ATTR, status);
		return segEle;
	}
	
	private void modifySegment(Element segmentEle, int segNum, String status){
		segmentEle.attribute(NUM_ATTR).setValue(Integer.toString(segNum));
		segmentEle.attribute(STATUS_ATTR).setValue(status);
	}
	
	public static String getSegmentsXPath(String messageId){
		return replaceParam(MESSAGE_SEGMENT_XPATH_PARAM, new String[]{messageId});
	}
	
	public static String getMessageXPath(String messageId){
		return replaceParam(MESSAGE_NODE_XPATH_PARAM, new String[]{messageId});
	}
	
	public static String getMessageSegmentXPath(String messageId, int segmentNum){
		return replaceParam(MESSAGE_SEGMENT_NODE_XPATH_PARAM, new String[]{messageId, Integer.toString(segmentNum)});
	}
	
	private static String replaceParam(String xpath, String[] params){
		String temp = xpath;
		for(int i=0; params.length > i; i++){
			temp = temp.replaceFirst("\\" + PARAM_TOKEN, params[i]);
		}
		return temp;
	}
	
	/**
	 * Set the message information to the acknowledgment content, if the message is not present, it will create
	 * an entry, otherwise it will modify the entry
	 * @param messageId message id
	 * @param status message status
	 * @return the newly created or modified XML element
	 */
	public Element setMessage(String messageId, String status){
		Element msgEle = findMessage(messageId);
		if(msgEle != null){
			modifyMessageElement(msgEle, messageId, status);
		}else{
			msgEle = createMessageElement(messageId, status);
		}
		return msgEle;
	}
	
	/**
	 * Set the message segment information for a given message id, if the message segment is present, it will
	 * create one, otherwise it will modify the existing entry
	 * @param messageId message id for message segment
	 * @param segmentNum segment number
	 * @param segmentStatus segment status
	 * @return the newly created or modified XML element
	 * @throws IllegalArgumentException if the given message id is not existing in the acknowledgment content
	 */
	public Element setSegment(String messageId, int segmentNum, String segmentStatus) throws IllegalArgumentException{
		Element msgEle = findMessage(messageId);
		if(msgEle == null){
			throw new IllegalArgumentException("Message with ID '" + messageId + "' hasn't been created");
		}
		//Check if the segments tag was created for message element
		Element segmentsEle = msgEle.element(SEGMENTS_TAG); 
		if(segmentsEle == null){
			segmentsEle = msgEle.addElement(SEGMENTS_TAG);
		}
		
		Element segEle = findSegment(messageId, segmentNum);
		if(segEle == null){
			segEle = addSegment(segmentsEle, segmentNum, segmentStatus);
		}else{
			modifySegment(segEle, segmentNum, segmentStatus);
		}
		return segEle;
	}
	
	public String toString(){
		return doc.asXML();
	}
}
