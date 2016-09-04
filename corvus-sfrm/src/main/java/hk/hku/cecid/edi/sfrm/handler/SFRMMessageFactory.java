package hk.hku.cecid.edi.sfrm.handler;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import hk.hku.cecid.edi.sfrm.activation.EmptyDataSource;
import hk.hku.cecid.edi.sfrm.activation.FileRegionDataSource;
import hk.hku.cecid.edi.sfrm.dao.SFRMMessageDVO;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessageException;
import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;

import hk.hku.cecid.piazza.commons.module.Component;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;


/**
 * It is the [FACTORY] of the SFRM Message.<br><br>
 * 
 * It provides API for creating all kinds of SFRM Message
 * in the version 1.0.3 specification.<br><br>
 * 
 * Creation Date: 5/12/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.3
 * 
 * @see hk.hku.cecid.edi.sfrm.pkg.SFRMMessage
 */
public class SFRMMessageFactory extends Component {
	
	/**
	 * Singleton Handler.
	 */
	private static SFRMMessageFactory msgFactory = new SFRMMessageFactory();
	
	/**
	 * @return an instnace of SFRMMessageFactory.
	 */
	public static SFRMMessageFactory getInstance(){
		return msgFactory;
	}
	
	/**
	 * Setup the message field according to the specified parameters.  
	 * 
	 * @param ret
	 * @param messageId
	 * @param partnershipId
	 * @param segmentType
	 * @param convId
	 * @param segmentNo
	 * @since	
	 * 			1.0.3
	 */
	protected void 
	setupMessage(
			SFRMMessage ret,
			String 	messageId,
			String 	partnershipId,
			String 	segmentType,			
			String 	convId, 
			int  	segmentNo,
			File	payload,
			String 	contentType) throws SFRMMessageException
	{
		ret.setMessageID	(messageId);
		ret.setPartnershipId(partnershipId);
		ret.setSegmentNo	(segmentNo);
		ret.setSegmentType	(segmentType);
					
		if (convId != null)
			ret.setConversationId(convId);	
		if (contentType == null)
			contentType = SFRMConstant.DEFAULT_CONTENT_TYPE;	
		if (payload == null)
			ret.setContent(new EmptyDataSource(), contentType);
		
	}
								
	/**
	 * Create a handshaking request which is used for  
	 * communicate to the receiver for delivery confirmation.<br><br>
	 * 
	 * This kind of SFRMMessage segment has the type called "META".
	 * 
	 * @param messageId
	 * 			The message Id of segment.
	 * @param partnershipId
	 * 			The partnership Id of segment.
	 * @param totalSegment
	 * 			The total segment of message.
	 * @param totalSize
	 * 			The total size of message.
	 * @return
	 * 			A new handshaking SFRM Message.
	 * @since
	 * 			1.0.3
	 * @throws SFRMMessageException
	 */
	public SFRMMessage 
	createHandshakingRequest(
			String 	messageId,
			String 	partnershipId,
			int  	totalSegment,
			long	totalSize,
			String filename) throws SFRMMessageException
	{
		SFRMMessage ret = new SFRMMessage();
		this.setupMessage(
			ret, 
			messageId, 
			partnershipId, 
			SFRMConstant.MSGT_META,
			null, 
			0, 
			null,
			null);
		if (totalSegment >= 0)
			ret.setTotalSegment(totalSegment);
		if (totalSize >= 0)
			ret.setTotalSize(totalSize);
		ret.setFilename(filename);		
		return ret;
	}
		
	/**
	 * 
	 * @param messageId
	 * @param partnershipId
	 * @param segmentNo
	 * @param segmentOffset
	 * @param segmentLength
	 * @param totalSize
	 * @param payload
	 * @param contentType
	 * @return
	 * @throws SFRMMessageException
	 */
	public SFRMMessage 
	createPayloadRequest(
			String 	messageId,
			String 	partnershipId,
			int  	segmentNo,			
			long 	segmentOffset, 
			long 	segmentLength, 
			long	totalSize,
			File	payload,
			String 	contentType) throws SFRMMessageException
	{
		SFRMMessage ret = new SFRMMessage();
		this.setupMessage(
			ret, 
			messageId, 
			partnershipId, 
			SFRMConstant.MSGT_PAYLOAD,
			null, 
			segmentNo, 
			payload,
			contentType);		
		if (segmentOffset >= 0)
			ret.setSegmentOffset(segmentOffset);
		if (segmentLength >= 0)
			ret.setSegmentLength(segmentLength);
		// Set up the payload.
			ret.setContent(
				new FileRegionDataSource(
					payload, 
					segmentOffset,
					segmentLength),
				contentType);		
		return ret;
	}
	
	public SFRMMessage createAcknowledgement(SFRMMessageDVO	mDVO, SFRMPartnershipDVO pDVO, String segType, String ackContent) 
		throws SFRMException, NoSuchAlgorithmException, UnrecoverableKeyException, SFRMMessageException {
	
		// Create return message.
		SFRMMessage msg = new SFRMMessage();		
		// Construct the message header. Fill in general information
		msg.setMessageID	(mDVO.getMessageId());
		msg.setPartnershipId(mDVO.getPartnershipId());		
		msg.setSegmentType	(segType);
		msg.setSegmentNo(0);
		msg.setContent(ackContent, SFRMConstant.XML_CONTENT_TYPE);		
		msg.setMicValue(msg.digest());
		
		// No need to sign and encrypt, return immediately.
		if (pDVO.getSignAlgorithm() == null && pDVO.getEncryptAlgorithm() == null)
			return msg;
					
		// Setup up signing using MD5 or SHA1		
		if (pDVO.getSignAlgorithm() != null){
			KeyStoreManager keyman = SFRMProcessor.getInstance().getKeyStoreManager();
			msg.sign(keyman.getX509Certificate(), keyman.getPrivateKey(), pDVO.getSignAlgorithm());
		}
		
		// Setup up encrypting using RC2, DES

		if (pDVO.getEncryptAlgorithm() != null) { 
			msg.encrypt(pDVO.getEncryptX509Certificate(), pDVO.getEncryptAlgorithm());
		}
						
		return msg;
	}	
}
