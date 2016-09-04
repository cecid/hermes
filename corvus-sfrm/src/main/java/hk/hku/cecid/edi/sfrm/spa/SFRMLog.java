package hk.hku.cecid.edi.sfrm.spa;

/**
 * The <code>SFRMLog</code> is the log code for the logging 
 * statement in the sfrm.log files.<br><br> 
 * 
 * For detail, please read "doc/logcode.txt".
 * 
 * @author 	Twinsen Tsang
 * @version	1.0.0
 * @since	1.0.4	
 */
public interface SFRMLog {

	/**
	 * The constant field for the prefix of the 
	 * <code>Incoming Message Handler</code> log.
	 */		
	public static final String IMH_CALLER 	 = " In MH : ";	
	
	/**
	 * The constant field for the prefix of the
	 * <code>Outgoing Message Handler</code> log. 
	 */		
	public static final String OMH_CALLER 	 = " outMH : ";
	
	/**
	 * The constant field for the prefix of the
	 * <code>Incoming Payloads Task</code> log. 
	 */
	public static final String IPT_CALLER 	 = " IPT   : ";
	
	/**
	 * The constant field for the prefix of the log.
	 * <code>Outgoing Packaged Payload Task</code> log.
	 */
	public static final String OPPT_CALLER	 = " OPPT  : ";
	
	/**
	 * The constant field for the prefix of the log.
	 * <code>Outgoing Payload Task</code> log.
	 */
	public static final String OPT_CALLER 	 = " OPT   : ";
	
	/**
	 * The constant field for the prefix of the log.
	 * <code>Outgoing Segment Payload Task</code> log.
	 */
	public static final String OSPT_CALLER	 = " OSPT  : ";
	
	/**
	 * The constant field for indicating the log prefix
	 * for <code>OutgoingPayloadCollector</code> log.
	 */
	public static final String OPTC_CALLER	 = " OPTC  : ";
	
	/**
	 * The constant field for indicating the log prefix
	 * for <code>OutgoingPackagedPayloadCollector</code> log.
	 */
	public static final String OPPTC_CALLER	 = " OPPTC : ";
	
	/**
	 * The constant field for indicating the log prefix
	 * for <code>OutgoingSegmentPayloadsCollector</code> log.
	 */
	public static final String OSPTC_CALLER  = " OSPTC : "; 
	
	/**
	 * The constant field for indicating the log prefix
	 * for <code>IncomingPayloadCollector</code> log.
	 */
	public static final String IPTC_CALLER	 = " IPTC  : ";
	
	/**
	 * The constant field for indicating the log prefix
	 * for <code>AcknowledgementCollector</code> log.
	 */
	public static final String ATC_CALLER	 = " ATC   : ";
	
	/**
	 * The constant field for indicating the log prefix
	 * for <code>AcknowledgementTask</code> log.
	 */
	public static final String AT_CALLER	 = " AT	   : "; 
	
	/**
	 * The constant field for indicating the log prefix
	 * for <code>MessageStatusCollecotr</code> log.
	 */
	public static final String MSC_CALLER 	 = " MSC   : ";
	/**
	 * The constant field for indicating the log prefix
	 * for <code>SFRMMessageSegmentHandler</code> log.
	 */ 
	public static final String MSHDAO_CALLER = " MSHDAO: ";
	
	/**
	 * The constant field for indicating the log prefix
	 * for <code>SFRMMessageStatusQueryService</code> log.
	 */
	public static final String SQS_CALLER 	= " SQS   : ";
	
	/**
	 * The constant field for indicating the signing segment 
	 * in the log.
	 */
	public static final String SIGNING_SGT  = " [SIGN SGT ]";
	
	/**
	 * The constant field for indicating the verifiying segment 
	 * in the log. 
	 */
	public static final String VERIFY_SGT  	= " [VRFY SGT ]";
	
	/**
	 * The constant field for indicating the encrypting segment 
	 * in the log.
	 */
	public static final String ENCRYPT_SGT	= " [ECYT SGT ]";
	
	/**
	 * The constant field for indicating the de-crypting segment 
	 * in the log.
	 */
	public static final String DECRYPT_SGT	= " [DCYT SGT ]";
	
	/**
	 * The constant field for indicating the un-packing segment 
	 * in the log.
	 */
	public static final String UNPACK_SGT	= " [UNPK SGT ]";
	
	/**
	 * The constant field for indicating packing message
	 * in the log.
	 */
	public static final String PACK_MSG		= " [PACK MSG ]";
	
	/**
	 * The constant field for indicating the un-packing message
	 * in the log.
	 */
	public static final String UNPACK_MSG	= " [UNPK MSG ]";
	
	/**
	 * The constant field for receiving a illegal segment.
	 */
	public static final String ILLEGAL_SGT  = " [ILEG SGT ]";
	
	/**
	 * The constant field for failure to resolve the 
	 * segment barrier. 
	 */
	public static final String RESOLVE_FAIL = " [RSLE FAIL]"; 
	
	/**
	 * The constant field for receiving msg in the log.
	 */
	public static final String RECEIVE_SGT	= " [RECV SGT ]";
	
	/**
	 * The constant field for receiving duplication in the log. 
	 */
	public static final String RECEIVE_DUP	= " [RECV DUP ]";	
	
	/**
	 * The constant field for indicating a new thread is 
	 * spanning in the <code>Incoming Message Handler</code>
	 * for handling the segment request.
	 */
	public static final String SPANNED_THRD	= " [SPAN THRD]";
	
	/**
	 * The constant field for receiving handshaking request in
	 * the log.
	 */
	public static final String RECEIVE_HDSK	= " [RECV HDSK]";
	
	/**
	 * The constant field for indicating failure in processing
	 * handshaking request in the log.
	 */
	public static final String FAIL_HDSK	= " [FAIL HDSK]";
	
	/**
	 * The constant field for indicating received meta in
	 * the log.
	 */
	public static final String RECEIVE_META	= " [RECV META]";
	
	/**
	 * The constant field for receiving all in the log.
	 */
	public static final String RECEIVE_ALL	= " [RECV ALL ]";	
	
	/**
	 * The constant field for indicating fail to receive
	 * in incoming message handler.
	 */
	public static final String RECEIVE_FAIL = " [RECV FAIL]";
	
	/**
	 * The constant field for indicating creating segment
	 * in the log. 
	 */
	public static final String CREATE_SGT	= " [CRTE SGT ]";
	
	/**
	 * The constant field for indicating CRC successfully
	 * in the log.
	 */
	public static final String SUCCESS_CRC	= " [PASS CRC ]";
	
	/**
	 * The constant field for indicating CRC fail in the log. 
	 */
	public static final String FAIL_CRC		= " [FAIL CRC ]";
	
	/**
	 * The constant field for indicating a new outgoing
	 * task is executing in the log.
	 */
	public static final String OUTG_TASK	= " [OUTG TASK]";
	
	/**
	 * The constant field for indicating sending outgoing
	 * message in the log.
	 */
	public static final String SEND_SGT		= " [SEND SGT ]";
	
	/**
	 * The constant field for indicating sending handshaking
	 * message in the log.
	 */
	public static final String SEND_HDSK	= " [SEND HDSK]";
	
	/**
	 * The constant field for indicating sending all 
	 * segment with reliable receipt. 
	 */
	public static final String SEND_ALL		= " [SEND ALL ]";
	
	/**
	 * The constant field for indicating insertion segments
	 * in the log.
	 */
	public static final String INSERT_SGTS	= " [INST SGTS]";
	
	/**
	 * The constant field for indicating the first load 
	 * in the collector.
	 */
	public static final String FIRST_LOAD	= " [INIT LOAD]";
	
	/**
	 * The constant field for indicating the roll back action
	 * in outgoing payload task.
	 */
	public static final String ROLL_BACK	= " [RLBK MSG ]";
	
	/**
	 * The constant field for indicating the need to wait
	 * for all receipt to be done.
	 */
	public static final String WAIT_REPT	= " [WAIT REPT]";
	
	/**
	 * The constant field for indicating the last receipt has
	 * been sent and notify to all thread waiting in
	 * the global lock.
	 */
	public static final String NOTIFY_REPT  = " [NTFY REPT]";
	
	/**
	 * The constant field for indicating there is web services
	 * client query the status of particular SFRM message.
	 */
	public static final String QUERY_STATUS = " [QERY STAT]";
	
	/**
	 * The constant field for logging message id prefix.
	 */
	public static final String MSGID_PREFIX	= " msg id: ";
	
	/**
	 * The constant field for logging segment number prefix.
	 */
	public static final String SGTNO_PREFIX = " sgt no: ";
}
