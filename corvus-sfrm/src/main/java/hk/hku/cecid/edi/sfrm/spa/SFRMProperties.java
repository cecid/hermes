/**
 * Contains the SPA plugin hook point for the piazza corvus
 * and the constant table.
 */
package hk.hku.cecid.edi.sfrm.spa;

import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * Provide fast access of constant field defined in the properties files.<br>
 * It is originally come from POC1.<br><br>
 * 
 * Creation Date: 3/10/2006<br><br>
 * 
 * V1.0.2 - Added <code>Mailcaps</code> properties.
 * 
 * @author Twinsen
 * @version 1.0.2
 * @since	1.0.0
 */
public class SFRMProperties {
			
	/**
	 * The sfrm XML namespace name. 
	 */
	public static final String XPATH_SFRM 		= "sfrm";
		
	/**
	 * The constant field for default XML separator.
	 */
	public static final String XPATH_SEPARATOR 	= "/";	
	
	/**
	 * The XML Path for the sfrm segment size. The typical size is 1 mb.
	 */
	public static final String XPATH_SEGMENT_SIZE 		= 
		XPATH_SFRM + XPATH_SEPARATOR + "segment-size";
	
	/**
	 * The XML Path for the trusted certificates location.
	 */
	public static final String XPATH_TRUSTED_CERTS		= 
		XPATH_SFRM + XPATH_SEPARATOR + "trusted-certificates";
	
	/**
	 * The XML Path for the maximum payload size that can be sent.<br><br>
	 * The typical value is 5GB.
	 */
	public static final String XPATH_MAX_PAYLOAD_SIZE 	= 
		XPATH_SFRM + XPATH_SEPARATOR + "max-payload-size";	 	
	
	/**
	 * The XML Path for the data content handler (DCH) for the 
	 * activiation framework.
	 */
	public static final String XPATH_MAILCAPS			=
		XPATH_SFRM + XPATH_SEPARATOR + "mailcaps" + XPATH_SEPARATOR + "cap";
	
	
	
	/**
	 * The XML Path for the interval for ticking the speed of the sending message
	 */
	public static final String XPATH_SPEED_TICK_INTERVAL=
		XPATH_SFRM + XPATH_SEPARATOR + "speed-tick-interval";
	
	/**
	 * The XML Path for the maximum speed that user specified (Bandwidth Optimizer)
	 */
	public static final String XPATH_SPEED_MAXIMUM=
		XPATH_SFRM + XPATH_SEPARATOR + "speed-maximum";
	
	
	/* -------------------------------------------------------------------
	 * Private variable
	 * -------------------------------------------------------------------
	 */		
	private static final int segmentSize 		 = 
		StringUtilities.parseInt(SFRMProcessor.getInstance().getSystemModule().properties.getProperty(XPATH_SEGMENT_SIZE));
	
	private static final String trustedCertStore = 
		SFRMProcessor.getInstance().getSystemModule().properties.getProperty(XPATH_TRUSTED_CERTS);
	private static final long maxPayloadSize 	 = 
		StringUtilities.parseLong(
				SFRMProcessor.getInstance().getSystemModule().properties.getProperty(XPATH_MAX_PAYLOAD_SIZE),
				50465865723L);
	
	private static final String [] mailcaps		 =
		SFRMProcessor.getInstance().getSystemModule().properties.getProperties(XPATH_MAILCAPS);
	
	
	private static final long speedTickInterval =
		StringUtilities.parseLong(SFRMProcessor.getInstance().getSystemModule().properties.getProperty(XPATH_SPEED_TICK_INTERVAL), 1000L);
	
	private static final long speedMaximum = 
		StringUtilities.parseLong(SFRMProcessor.getInstance().getSystemModule().properties.getProperty(XPATH_SPEED_MAXIMUM), 2048L);
	/** 
	 * Get the fixed size of each payload segment.
	 * 
	 * @return the size of each segment.
	 */
	public static int getPayloadSegmentSize(){
		return SFRMProperties.segmentSize;
	}
	
	/**
	 * Get the location of trusted certificate store.
	 * 
	 * @return a absolute path storing the trusted certificate store.
	 */
	public static String getTrustedCertStore(){
		return SFRMProperties.trustedCertStore;
	}
	
	/**
	 * @return 
	 * 		the max payload size allowed.
	 */
	public static long getMaxPayloadSize(){
		return SFRMProperties.maxPayloadSize;
	}
	
	/**
	 * @return 
	 * 		the available mail caps for the activiation framework available 
	 * 		in the SFRM plugin.
	 */
	public static String[] getAvailableMailCaps(){
		return SFRMProperties.mailcaps;
	}
	
	public static long getSpeedTickInterval(){
		return SFRMProperties.speedTickInterval;
	}
		
	/**
	 * toString method.
	 */
	public String 
	toString()
	{
		return new StringBuffer()
		.append(this.getClass().getName() + "\n")
		.append("Segment Size:           " + SFRMProperties.segmentSize 	 + "\n")
		.append("Trusted Certs Location: " + SFRMProperties.trustedCertStore + "\n")
		.append("Max Payload Size:       " + SFRMProperties.maxPayloadSize	 + "\n")
		.append("Speed Maximum:          " + SFRMProperties.speedMaximum     + "\n")
		.toString();
	}
}
