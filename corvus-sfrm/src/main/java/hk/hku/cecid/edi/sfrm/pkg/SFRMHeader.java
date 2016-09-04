/**
 * Provide the core class for packing the sfrm message 
 * for messaging. 
 */
package hk.hku.cecid.edi.sfrm.pkg;

/**
 * SFRMHeader represents a set of SFRM message headers.<br><br>
 * 
 * Version 1.0.2 - <br>
 * 		Added Converation Id.
 * 
 * Version 1.0.1 - <br> 
 * 		Header schema updated for the impl of on-the-fly sending and recv mode.<br>
 * 		Remove deprecated fields.
 * 
 * Version 2.0.0 - <br>
 * 		Add Is-Packed header field
 * 		Add Filename header field
 * @author Twinsen Tsang
 * @version	1.0.2
 * @since	1.0.0
 */
public class SFRMHeader {
    
	/**
	 * The SFRM message id field.
	 */
    public static final String MESSAGE_ID 	= "Message-Id";
     
    /**
     * The SFRM version field.<br><br>
     * 
     * The current value of this field is 1.0.2.<br><br>
     */
    public static final String SFRM_VERSION = "SFRM-Version";
    
    /**
     * The SFRM converation id field.<br><br>
     * 
     * This field is RESERVED.
     */
    public static final String SFRM_CONVERATION = "Conv-Id";
    
    /**
     * The SFRM partnership id field.
     */
    public static final String SFRM_PARTNERSHIP = "Partnership-Id";     

    /**
     * The SFRM total size field for message.
     */
    public static final String SFRM_TOTAL_SIZE = "Total-Size";
    
    /**
     * The SFRM segment no field. 
     */
    public static final String SFRM_SEGMENT_NO   = "Segment-No";
    
    /**
     * The SFRM segment type field.
     */
    public static final String SFRM_SEGMENT_TYPE = "Segment-Type";
    
    /**
     * The SFRM segment offset field.
     */
    public static final String SFRM_SEGMENT_OFFSET = "Segment-Offset";
           
    /**
     * The SFRM segment length field.
     */
    public static final String SFRM_SEGMENT_LENGTH = "Segment-Length";
            
    /**
     * The SFRM total segment field for meta message. 
     */
    public static final String SFRM_META_TOTAL_SEGMENT = "Total-Segment";
          
    /**
     * The SFRM flag for representing it is a the last receipt / ack of the message.
     */
    public static final String SFRM_RECEIPT_LAST = "Last-Receipt";

    /**
     * The SFRM field to represent the file name of the payload.
     * This field is significance when the Is-Packed file is set to No. 
     */
    public static final String SFRM_FILENAME = "Filename";
    
}