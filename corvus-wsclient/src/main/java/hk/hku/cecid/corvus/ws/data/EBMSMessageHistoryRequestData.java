package hk.hku.cecid.corvus.ws.data;

public class EBMSMessageHistoryRequestData extends MessageHistoryRequestData{

	public static final String [] PARAM_EBMS_KEY_SET = 
	{
		"messageId", "conversationId", "cpaId", "service", "action"
	};
	
	public static final String CONFIG_PREFIX = "ebms-msg-history-request/config";
	
	public static final String CRITERIA_PARAM_PREFIX  = "ebms-msg-history-request/param/criteria";
	
	public void setMessageId(String value){
		props.put(PARAM_EBMS_KEY_SET[0], value);
	}
	
	public String getMessageId(){
		return (String)props.get(PARAM_EBMS_KEY_SET[0]);
	}
	
	public void setConversationId(String value){
		props.put(PARAM_EBMS_KEY_SET[1], value);
	}
	
	public String getConversationId(){
		return (String)props.get(PARAM_EBMS_KEY_SET[1]);
	}
	
	public void setCpaId(String value){
		props.put(PARAM_EBMS_KEY_SET[2], value);
	}
	
	public String getCpaId(){
		return (String)props.get(PARAM_EBMS_KEY_SET[2]);
	}
	
	public void setService(String value){
		props.put(PARAM_EBMS_KEY_SET[3], value);
	}
	
	public String getService(){
		return (String)props.get(PARAM_EBMS_KEY_SET[3]);
	}
	
	public void setAction(String value){
		props.put(PARAM_EBMS_KEY_SET[4], value);
	}
	
	public String getAction(){
		return (String)props.get(PARAM_EBMS_KEY_SET[4]);
	}
	
	public EBMSMessageHistoryRequestData() {
		super((PARAM_EBMS_KEY_SET.length +
					PARAM_KEY_SET.length +
					CONFIG_KEY_SET.length ));
	}
}
