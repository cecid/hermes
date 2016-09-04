package hk.hku.cecid.edi.sfrm.admin.listener;

import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;
import java.sql.Timestamp;
import java.util.Iterator;
import hk.hku.cecid.piazza.commons.util.PropertyTree;

/**
 * @author Patrick Yip
 * @version 1.0.0
 */
public class MessageHistoryPageletAdaptor extends AdminPageletAdaptor {
	protected Source getCenterSource(HttpServletRequest request){
		PropertyTree dom = null;
		try{
			dom = getMessageHistory(request);
		} catch (DAOException e) {
            SFRMProcessor.getInstance().getLogger().debug(
                    "Unable to process the message search page request", e);
            throw new RuntimeException(
                    "Unable to process the message search page request", e);
        }
		
		SFRMProcessor.getInstance().getLogger().info("Processed the message history search request");
		return dom.getSource();
	}
	
	private PropertyTree getMessageHistory(HttpServletRequest request) throws DAOException{
		PropertyTree dom = new PropertyTree();
		dom.setProperty("/message_history", "");
		
		int numberOfMessagesInt = 20; // default value
        int offsetInt = 0;
        boolean isDetail = false;
        boolean isTime = false;
        int displayLastInt = 0;
        
        Iterator messageIterator = null;
        if(request.getParameter("original_message_id")!=null){
        	
        }else{
        	String messageId = checkStarAndConvertToPercent(request
                    .getParameter("message_id"));
            
            // radio button and menu
            String messageBox = checkEmptyAndReturnNull(request
                    .getParameter("message_box"));
            String status = checkEmptyAndReturnNull(request
                    .getParameter("status"));
            
            //get the message_time value
            String displayLast = request.getParameter("message_time");
            if(displayLast != null){
            	if(!(displayLast.equals(""))){
            		displayLastInt = Integer.valueOf(displayLast).intValue();
            		isTime = true;
            	}
            }

            String numOfMessages = request.getParameter("num_of_messages");
            if (numOfMessages != null) {
                numberOfMessagesInt = Integer.valueOf(numOfMessages).intValue();
            }

            String offset = request.getParameter("offset");
            if (offset != null) {
                offsetInt = Integer.valueOf(offset).intValue();
            }

            String isDetailStr = request.getParameter("is_detail");
            if (isDetailStr != null) {
                if (isDetailStr.equalsIgnoreCase("true")) {
                    isDetail = true;
                }
            }

            // search the corresponding messages
            SFRMMessageDAO messageDAO = (SFRMMessageDAO) SFRMProcessor.getInstance().getDAOFactory()
                    .createDAO(SFRMMessageDAO.class);
            SFRMMessageDVO messageDVO = (SFRMMessageDVO) messageDAO.createDVO();

            messageDVO.setMessageId(messageId);
            messageDVO.setMessageBox(messageBox);
            messageDVO.setStatus(status);

			messageIterator = findMessageWithPagination(messageDVO, messageDAO, numberOfMessagesInt, offsetInt,displayLastInt, isTime);
            dom.setProperty("total_no_of_messages", String.valueOf(messageDAO
                    .findNumberOfMessagesByHistory(messageDVO)));
        }
        
     // pass the search criteria
        dom.setProperty("search_criteria/message_id", request
                .getParameter("message_id"));
        dom.setProperty("search_criteria/message_box", request
                .getParameter("message_box"));
        dom.setProperty("search_criteria/conv_id", request
                .getParameter("conv_id"));
        dom.setProperty("search_criteria/status", request
                .getParameter("status"));
        dom.setProperty("search_criteria/num_of_messages", String
                .valueOf(numberOfMessagesInt));
        dom.setProperty("search_criteria/message_time",String.valueOf(displayLastInt));
        dom.setProperty("search_criteria/offset", String.valueOf(offsetInt));
        dom.setProperty("search_criteria/is_detail", String.valueOf(isDetail));
		
        buildMessageHistoryList(messageIterator, isDetail, dom);
		return dom;
	}
	
	/*
	 * Build the message history list to the DOM object 
	 * @param messageIterator Iterator contain a list of SFRMMessageDVO for the search result
	 * @param isDetail Boolean value for whether to show the detail information for the message, true for show detail, false otherwise
	 * @param dom the DOM object to be build
	 */
	private void buildMessageHistoryList(Iterator messageIterator, boolean isDetail, PropertyTree dom){
		if(messageIterator == null)
			return;
		 for (int pi = 1; messageIterator.hasNext(); pi++) {
	            SFRMMessageDVO returnData = (SFRMMessageDVO) messageIterator.next();

	            dom.setProperty("message[" + pi + "]/message_id",
	                    checkNullAndReturnEmpty(returnData.getMessageId()));
	            dom.setProperty("message[" + pi + "]/message_box",
	                    checkNullAndReturnEmpty(returnData.getMessageBox()));
	            String timeStr = null;
	            timeStr = (returnData.getCompletedTimestamp()==null ? "N/A" : returnData.getCompletedTimestamp().toString());
	            dom.setProperty("message[" + pi + "]/completed_time_stamp", timeStr);
	            dom.setProperty("message[" + pi + "]/status", checkNullAndReturnEmpty(returnData.getStatus()));
	            
	            if (isDetail) {            	
	            	timeStr = (returnData.getCreatedTimestamp()==null ? "N/A" : returnData.getCreatedTimestamp().toString());
		            dom.setProperty("message[" + pi + "]/created_time_stamp", timeStr);
		            timeStr = (returnData.getProceedTimestamp()==null ? "N/A" : returnData.getProceedTimestamp().toString());
		            dom.setProperty("message[" + pi + "]/proceed_time_stamp", timeStr);
		            
		            if(returnData.getProceedTimestamp()!=null && returnData.getCompletedTimestamp()!=null){
		            	TimeDiff elapsedTime = timeDiff(returnData.getCreatedTimestamp(), returnData.getCompletedTimestamp());
		            	dom.setProperty("message[" + pi + "]/elapsed_time",Integer.toString(elapsedTime.hour) + " hour " + Integer.toString(elapsedTime.minute) + " minute " + Integer.toString(elapsedTime.second) + " second");
		            }else{
		            	dom.setProperty("message[" + pi + "]/elapsed_time", "N/A");
		            }
		            
	                dom.setProperty("message[" + pi + "]/status_description",
	                        checkNullAndReturnEmpty(returnData
	                                .getStatusDescription()));
	                
	                
	                String totalSegmentStr = null;
	                if(returnData.getTotalSegment()==Integer.MIN_VALUE)
	                	totalSegmentStr = "N/A";
	                else
	                	totalSegmentStr = String.valueOf(returnData.getTotalSegment());
	                	
	                dom.setProperty("message[" + pi + "]/total_segment", totalSegmentStr);
	                	
	                String totalSizeStr = null;
	                
	               if(returnData.getTotalSize()==Long.MIN_VALUE)
	            	   totalSizeStr = "N/A";
	               else
	            	   totalSizeStr = String.valueOf(returnData.getTotalSize());
	                
	                dom.setProperty("message[" + pi + "]/total_size", totalSizeStr);

	            }
	        }
	}
	
	
	/**
     * @param parameter
     * @return
     */
    private String checkStarAndConvertToPercent(String parameter) {
    	if (parameter == null || parameter.equals("")) {
            return "%";
        }
        return parameter.replace("_", "\\_").replace("%", "\\%").replace('*', '%');
    }
    
    private String checkEmptyAndReturnNull(String parameter) {
        if (parameter == null || parameter.equals("")) {
            return null;
        }
        return parameter;
    }

    private String checkNullAndReturnEmpty(String parameter) {
        if (parameter == null) {
            return new String("");
        }
        return parameter;
    }
    
	private Iterator findMessageWithPagination(SFRMMessageDVO data, SFRMMessageDAO messageDAO, int numberOfMessage, int offset,int displayLastInt, boolean isTime) throws DAOException{
		if(!isTime){
			return messageDAO.findMessagesByHistory(data,
					numberOfMessage, offset).iterator();
		}else{
			return messageDAO.findMessagesByTime(displayLastInt,data,
					numberOfMessage, offset).iterator();			
		}
	}
	
	/**
	 * Get the time different between 2 timestamp provided 
	 * @param startTime Starting time
	 * @param endTime Ending time
	 * @return Different between 2 timestamp
	 */
	
	private TimeDiff timeDiff(Timestamp startTime, Timestamp endTime){
		long diff = endTime.getTime() - startTime.getTime();
		TimeDiff diffObj = new TimeDiff();
		diffObj.hour = (int)(diff / 3600000);
		diffObj.minute = (int)(diff - diffObj.hour*3600000)/60000;
		diffObj.second = (int)((diff - diffObj.hour*3600000 - diffObj.minute*60000)/1000);
		return diffObj;
	}
	
	private class TimeDiff{
		public int hour;
		public int minute;
		public int second;
	}
}




