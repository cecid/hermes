package hk.hku.cecid.edi.sfrm.spa;

import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;

/**
 * The <code>SFRMLogUtil</code>
 * 
 * Creation Date: 03/07/2007
 * 
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since	
 */
public class SFRMLogUtil {

	public static final int DEFAULT_BUFFER_LENGTH = 40;
	
	/**
	 * 
	 * @param caller
	 * @param action
	 * @param messageId
	 * @param segmentNumber
	 * @return
	 */
	public static void log(String caller, String action, String messageId, int segmentNumber){			
		SFRMProcessor.getInstance().getLogger().info(
			new StringBuffer(DEFAULT_BUFFER_LENGTH + messageId.length())
			.append(caller)
			.append(action)
			.append(SFRMLog.MSGID_PREFIX)
			.append(messageId)
			.append(SFRMLog.SGTNO_PREFIX)
			.append(segmentNumber).toString());
	}
	
	/**
	 * 
	 * @param caller
	 * @param action
	 * @param segmentNumber
	 * @return
	 */
	public static void log(String caller, String action, int segmentNumber){
		SFRMProcessor.getInstance().getLogger().info(
			new StringBuffer(DEFAULT_BUFFER_LENGTH)
			.append(caller)
			.append(action)
			.append(SFRMLog.SGTNO_PREFIX)
			.append(segmentNumber).toString());
	}
	
	/**
	 *
	 */
	public static void debug(String caller, String action, String segmentType){
		SFRMProcessor.getInstance().getLogger().debug(
			new StringBuffer(DEFAULT_BUFFER_LENGTH)
			.append(caller)
			.append(action)
			.append("It is a sgt type of: ")
			.append(segmentType).toString());			
	} 
	
	/**
	 * 
	 * @param caller
	 * @param action
	 * @param message
	 * @return
	 */
	public static String logInfo(String caller, String action, SFRMMessage message){
		return new StringBuffer()
			.append(caller)
			.append(action)
			.append(message.toString()).toString();
	}
}
