package hk.hku.cecid.corvus.ws.data;

import java.security.InvalidParameterException;

public class PermitRedownloadData extends KVPairData {

	public static final String PROTOCOL_AS2 = "AS2";
	public static final String PROTOCOL_EBMS = "EBMS";
	
	public static final String [] PARAM_KEY_SET = 
	{
		"messageId"
	};
	
	public static final String [] CONFIG_KEY_SET = 
	{
		"permitDlEndpoint"      
	};
	
	public static final String AS2_CONFIG_PREFIX = "/as2-request/config";
	public static final String AS2_PARAM_PREFIX  = "/as2-request/param";
	
	public static final String EBMS_CONFIG_PREFIX = "/ebms-request/config";
	public static final String EBMS_PARAM_PREFIX  = "/ebms-request/param";
	
	public PermitRedownloadData(String protocol){
		super(PARAM_KEY_SET.length);
		
		if(protocol == null && protocol.trim().equalsIgnoreCase("")||
				!(protocol.equalsIgnoreCase(PROTOCOL_AS2) ||protocol.equalsIgnoreCase(PROTOCOL_EBMS))){
			throw new InvalidParameterException("Message Protocol did not specified.");
		}
	}
	
	public String getEndpoint(){
		return (String) this.props.get(CONFIG_KEY_SET[0]);
	}
	
	public String getTargetMessageId(){
		return (String) this.props.get(PARAM_KEY_SET[0]);
	}
	
	public String toString(){
		return "Endpoint: " 	+ this.getEndpoint() 			+ 	"\n" +
				"Message ID: " 	+ this.getTargetMessageId();
	}

}
