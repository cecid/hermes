package hk.hku.cecid.edi.sfrm.pkg;
import java.util.Hashtable;
/**
 * The constant field for SFRM Message.<br><br>
 * 
 * Creation Date: 13/11/2006<br><br>
 * 
 * Version 1.0.1 - Added Message status Description Constant.<br><br>
 * 
 * @author Twinsen Tsang
 * @version 1.0.1
 * @since	1.0.2
 */
public final class SFRMConstant {

	public static final String WILDCARD		= "%";
	
	/*
	 * The constant fields related to the message box. 
	 * It can either be "outbox" and "inbox". 
	 */
	public static final String MSGBOX_IN		= "INBOX";	
	public static final String MSGBOX_OUT		= "OUTBOX";
	
	/*
	 * The constant fields related to the segment type.
	 */
	public static final String MSGT_META		 	 = "META";
	public static final String MSGT_PAYLOAD		 = "PAYLOAD";
	public static final String MSGT_ERROR		 = "ERROR";
	public static final String MSGT_ACK_REQUEST	 = "ACKNOWLEDGEMENT_REQUEST";
	public static final String MSGT_ACK_RESPONSE	 = "ACKNOWLEDGEMENT_RESPONSE";
	
	/*
	 * The constant fields related to message status. 
	 */
	public static final String MSGS_HANDSHAKING		= "HS";
	public static final String MSGS_PACKAGING  		= "PK";
	public static final String MSGS_PACKAGED		= "PKD";
	public static final String MSGS_SEGMENTING 		= "ST";
	public static final String MSGS_PENDING	 		= "PD";
	public static final String MSGS_PROCESSING 		= "PR";
	public static final String MSGS_DELIVERED  		= "DL";
	public static final String MSGS_UNPACKAGING		= "UK";
	public static final String MSGS_PROCESSED 		= "PS";			
	public static final String MSGS_PROCESSING_ERROR	= "PE";
	public static final String MSGS_DELIVERY_FAILURE	= "DF";
	
	/**
	 * New status for SFRM V2
	 */
	public static final String MSGS_SUSPENDED			= "SD";
	public static final String MSGS_PRE_SUSPENDED		= "PSD";
	public static final String MSGS_PRE_RESUME			= "PRS";
	public static final String MSGS_PRE_PROCESSED		= "PPS";
	public static final String MSGS_PRE_DELIVERY_FAILED 	= "PDF";
	
	/*
	 * The constant fields related to message status desc. 
	 */
	public static final String MSGSDESC_HANDSHAKING	= "Connecting to partner.";
	public static final String MSGSDESC_PACKAGING	= "Message is packaging.";
	public static final String MSGSDESC_PACAKGED		= "Message is packaged.";
	public static final String MSGSDESC_PROCESSING	= "Message is processing.";
	public static final String MSGSDESC_PROCESSED	= "Message is processed.";
	public static final String MSGSDESC_SEGMENTING	= "Message is segmenting.";
	public static final String MSGSDESC_UNPACKAGING	= "Message is un-packaging.";	
	public static final String MSGSDESC_NODISKSPACE	= "Not enough disk space";
	
	/*
	 * Hash algorithm for MessageDigest
	 */
	public static final String MESSAGE_DIGEST_SHA1 = "sha1";
	public static final String MESSAGE_DIGEST_MD5 = "md5";
	
	/**
	 * New description for SFRM V2
	 */
//	public static final String MSGSDESC_PENDING				= "Message is pending";
	public static final String MSGSDESC_SUSPENDED			= "Messsage is suspended";
	public static final String MSGSDESC_PRE_SUSPENDED		= "Message is pre-suspending";
	public static final String MSGSDESC_PRE_RESUME			= "Message is pre-resuming";
	public static final String MSGSDESC_PRE_PROCESSED		= "Message is pre-processed";
	public static final String MSGSDESC_PRE_DELIVERY_FAILED	= "Message is pre delivery failed";
	
	/*
	 * 
	 */
	public static final String DEFAULT_CONTENT_TYPE 	= "application/octet-stream";
	public static final String XML_CONTENT_TYPE			= "text/xml";
	
	private static Hashtable<String, String> statusDescriptionMap; 
	
	static {
		statusDescriptionMap = new Hashtable<String, String>();
		statusDescriptionMap.put(MSGS_HANDSHAKING, MSGSDESC_HANDSHAKING);
		statusDescriptionMap.put(MSGS_PACKAGING, MSGSDESC_PACKAGING);
		statusDescriptionMap.put(MSGS_PACKAGED, MSGSDESC_PACAKGED);
		statusDescriptionMap.put(MSGS_SEGMENTING, MSGSDESC_SEGMENTING);
		statusDescriptionMap.put(MSGS_PROCESSING, MSGSDESC_PROCESSING);
		statusDescriptionMap.put(MSGS_UNPACKAGING, MSGSDESC_UNPACKAGING);
		statusDescriptionMap.put(MSGS_PROCESSED, MSGSDESC_PROCESSED);
		statusDescriptionMap.put(MSGS_SUSPENDED, MSGSDESC_SUSPENDED);
		statusDescriptionMap.put(MSGS_PRE_SUSPENDED, MSGS_PRE_SUSPENDED);
		statusDescriptionMap.put(MSGS_PRE_RESUME, MSGSDESC_PRE_RESUME);
		statusDescriptionMap.put(MSGS_PRE_PROCESSED, MSGSDESC_PRE_PROCESSED);
		statusDescriptionMap.put(MSGS_PRE_DELIVERY_FAILED, MSGSDESC_PRE_DELIVERY_FAILED);
	}
	
	public static String getStatusDescription(String status){
		return statusDescriptionMap.get(status);
	}
}
