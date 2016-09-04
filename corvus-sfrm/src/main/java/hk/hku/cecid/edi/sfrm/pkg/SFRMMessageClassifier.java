package hk.hku.cecid.edi.sfrm.pkg;

/** 
 * The <code>SFRMMessageClassifier</code> is a classifier
 * for SFRM segments. It provide very fast and efficient 
 * access to crtiical information for it's corresponding 
 * segment.<br><br> 
 * 
 * Creation Date: 10/11/2006<br><br>
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.2
 */
public class SFRMMessageClassifier {

	/**
	 * The eight bit flag structure for classify 
	 * the SFRM message.<br><br>
	 *
	 * The first three bit (the M.S.B) is indicating 
	 * the sending method of this message.<br><br>
	 * 
	 * <PRE>
	 *  000 / 0x0X - No encryption, no signature
	 *  001 / 0x1X - No encryption, signature
	 *  010 / 0x2X - Encryption, no signature
	 *  011 / 0x3X - Encryption, signature
	 * </PRE>
	 * 
	 * The fourth bit is reserved.<br><br>
	 * 
	 * The last four bit represents the 
	 * segment type of this message.
	 * 
	 * <PRE>
	 * 0000(bin) / 0xX0(hex) - META
	 * 0001(bin) / 0xX1(hex) - PAYLOAD
	 * 0010(bin) / 0xX2(hex) - RECEIPT
	 * 0011(bin) / 0xX3(hex) - RECOVERY
	 * 0100(bin) / 0xX4(hex) - ERROR 
	 * 0101(bin) / 0xX5(hex) - Acknowledgment
	 * </PRE>
	 */
	private byte msgBits;
			
	/** 
	 * Explicit Constructor.
	 * 
	 * @param message
	 * 			The sfrm message need to be classify.
	 * @throws NullPointerException
	 * 			if the message is null.
	 * @throws SFRMMessageException
	 * 
	 * @throws Exception
	 */
	public SFRMMessageClassifier(SFRMMessage message){
		if (message == null)
			throw new NullPointerException("Message is null.");	
		
		// Resolve segment type, the order here 
		// is sorted by how frequency the message
		// appear in normal case.
		//
		// Here we use method equals only because
		// the segment type is always in UPPERCASE.
		String segmentType = message.getSegmentType();
		if (segmentType.equals(SFRMConstant.MSGT_PAYLOAD))
			msgBits |= 0x01;
		else if (segmentType.equals(SFRMConstant.MSGT_META))				 
			msgBits |= 0x00;
		else if (segmentType.equals(SFRMConstant.MSGT_ERROR))
			msgBits |= 0x04;  
		else if (segmentType.equals(SFRMConstant.MSGT_ACK_REQUEST))
			msgBits |= 0x05;
	}
	
	/**
	 * [Protected] 
	 * 
	 * @param isSign the flag indicate whether the message is signed or not.
	 */
	protected void setSigned(boolean isSign){
		msgBits |= isSign ? (0x01 << 4): (0x00 << 4);
	}
	
	/**
	 * [Protected]
	 * 
	 * @param isEncrypt the flag indicate whether the messags is encrypted or not.
	 */
	protected void setEncrypted(boolean isEncrypt){
		msgBits |= isEncrypt ? (0x02 << 4): (0x00 << 4);
	}
			
	/** 
	 * @return
	 * 			true if it is a payload message.
	 */
	public boolean isPayload(){
		return (msgBits | 0xF0) == 0xF1 ? true : false;  
	}
	
	/** 
	 * @return
	 * 			true if it is a meta message.
	 */
	public boolean isMeta(){
		return (msgBits | 0xF0) == 0xF0 ? true : false;		
	}
		
	/** 
	 * @return
	 * 			true if it is a error message.
	 */
	public boolean isError(){
		return (msgBits | 0xF0) == 0xF4 ? true : false;
	}
	
	public boolean isAcknowledgementRequest(){
		return (msgBits | 0xF0) == 0xF5 ? true : false;
	}
	
	/** 
	 * @return	
	 * 			true if the message is signed.
	 */
	public boolean isSigned(){
		return (msgBits | 0xEF) == 0xFF ? true : false; 
	}
	
	/** 
	 * @return	
	 * 			true if the message is encrypted.
	 */
	public boolean isEncrypted(){
		return (msgBits | 0xDF) == 0xFF ? true : false;
	}
	
	/**
	 * @return
	 * 			Get the internal presentation of the message 
	 * 			structure. 
	 */
	public byte getMagicNumber(){
		return this.msgBits;
	}
}
