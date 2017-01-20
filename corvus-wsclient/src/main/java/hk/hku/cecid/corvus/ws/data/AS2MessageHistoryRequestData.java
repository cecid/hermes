package hk.hku.cecid.corvus.ws.data;

public class AS2MessageHistoryRequestData extends MessageHistoryRequestData {

public static final String CONFIG_PREFIX = "as2-msg-history-request/config";
	
	public static final String PARAM_PREFIX  = "as2-msg-history-request/param/criteria";

	public static final String [] PARAM_AS2_KEY_SET = 
	{
		"messageId", "as2From", "as2To"
	};
		
	public void setAS2FromParty (String value){
		props.put(PARAM_AS2_KEY_SET[1], value);
	}
	
	public String getAS2FromParty(){
		return (String)props.get(PARAM_AS2_KEY_SET[1]);
	}
	
	public void setAS2ToParty (String value){
		props.put(PARAM_AS2_KEY_SET[2], value);
	}
	
	public String getAS2ToParty(){
		return (String)props.get(PARAM_AS2_KEY_SET[2]);
	}
	
	public void setMessageId (String value){
		props.put(PARAM_AS2_KEY_SET[0], value);
	}
	
	public String getMessageId(){
		return (String)props.get(PARAM_AS2_KEY_SET[0]);
	}
	
	public AS2MessageHistoryRequestData() {
		super((PARAM_AS2_KEY_SET.length +
				PARAM_KEY_SET.length +
				CONFIG_KEY_SET.length));
	}

}
