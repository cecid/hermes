/**
 * 
 */
package hk.hku.cecid.edi.sfrm.service;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

import org.w3c.dom.Element;

import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.handler.SFRMExternalRequestHandler;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.soap.SOAPRequestException;
import hk.hku.cecid.piazza.commons.soap.WebServicesAdaptor;
import hk.hku.cecid.piazza.commons.soap.WebServicesRequest;
import hk.hku.cecid.piazza.commons.soap.WebServicesResponse;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * @author Patrick Yip
 *
 */
public class SFRMMessageResumeService extends WebServicesAdaptor {
	
	private static String SFRM_XMLNS = "http://service.sfrm.edi.cecid.hku.hk/"; 
	
	public void serviceRequested(
			WebServicesRequest 	request,
			WebServicesResponse response) throws SOAPRequestException, DAOException, SOAPException 
	{
		Element[] bodies = request.getBodies();
		String messageID = getText(bodies, "messageId");
		
		if (messageID == null) {
			throw new SOAPRequestException("Missing Parameters - Message ID");
		}
		
		int totalSegmentNum = 0;
		int processedSegmentNum = 0;
		boolean isSuccess = true;
		String replyMessage = "Message with ID '" + messageID + "' was resumed";
		SFRMExternalRequestHandler extHandler = SFRMProcessor.getInstance().getExternalRequestHandler();
		
		try{
			extHandler.resumeMessage(messageID);
			//Get the information about the suspended mesasge
			SFRMMessageDVO mDVO = SFRMProcessor.getInstance().getMessageHandler().retrieveMessage(messageID, SFRMConstant.MSGBOX_OUT);
			totalSegmentNum = mDVO.getTotalSegment();
			processedSegmentNum = SFRMProcessor.getInstance().getMessageSegmentHandler().retrieveMessageSegmentCount(messageID, SFRMConstant.MSGBOX_OUT, SFRMConstant.MSGT_PAYLOAD, SFRMConstant.MSGS_PROCESSED);
		}catch(Exception e){
			SFRMProcessor.getInstance().getLogger().error("Error occur when calling SFRMMessageResumeService", e);
			replyMessage = e.getMessage();
			if(StringUtilities.isEmptyString(replyMessage)){
				replyMessage = "Unknown Error";
			}
			isSuccess = false;
		}
		
		generateReply(response, isSuccess, totalSegmentNum, processedSegmentNum, replyMessage);
	}
	
	/**
	 * To generate the reply message
	 * @param response 
	 * @param isSuccess whether suspending operation is success 
	 * @param totalSegmentNum total number of segement for this mesasge
	 * @param processedSegmentNum number of segment which is processed
	 * @param replyMsg message for this operation
	 * @throws SOAPException
	 */
	
	private void generateReply(
			WebServicesResponse response, 
			boolean isSuccess, int totalSegmentNum, int processedSegmentNum, String replyMsg) throws SOAPRequestException{
		try{
			SOAPElement rootElement = createElement("messageInfo", "", SFRM_XMLNS, "MessageInfo");
			rootElement.addChildElement(createText("isSuccess", Boolean.toString(isSuccess), SFRM_XMLNS));
			rootElement.addChildElement(createText("totalSegmentNum", Integer.toString(totalSegmentNum), SFRM_XMLNS));
			rootElement.addChildElement(createText("processedSegmentNum", Integer.toString(processedSegmentNum), SFRM_XMLNS));
			rootElement.addChildElement(createText("message", replyMsg, SFRM_XMLNS));
			response.setBodies(new SOAPElement[]{rootElement});
		}catch(Exception e){
			//Re - thrown the exception
			throw new SOAPRequestException("Unable to generate reply message", e);
		}
	}
	
}
