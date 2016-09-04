package hk.hku.cecid.edi.sfrm.handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import hk.hku.cecid.edi.sfrm.net.FastHttpConnector;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessageClassifier;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessageException;

import hk.hku.cecid.edi.sfrm.spa.SFRMComponent;
import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.edi.sfrm.spa.SFRMLog;

import hk.hku.cecid.edi.sfrm.activation.FileRegionDataSource;

import hk.hku.cecid.piazza.commons.net.ConnectionException;
import hk.hku.cecid.piazza.commons.security.KeyStoreManager;
import hk.hku.cecid.piazza.commons.security.TrustedHostnameVerifier;

/**
 * The outgoing message handler is a singleton classes
 * that provides service for processing outgoing SFRM 
 * message.<br><br>
 * 
 * Creation Date: 5/12/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.3
 */
public class OutgoingMessageHandler extends SFRMComponent{	
	static{
		System.setProperty("sun.net.client.defaultConnectTimeout", "60000");
		System.setProperty("sun.net.client.defaultReadTimeout"	 , "60000");
	}
		
	private static OutgoingMessageHandler omh;
	
	/**
	 * @return an instance of OutgoingMessageHandler.
	 */
	public static OutgoingMessageHandler getInstance(){
		return omh;
	}
		
	/**
	 * Initialization of this Component
	 */
	protected void init() throws Exception{
		super.init();
		omh = this;
	}
	
	
	/**
	 * Pack the SMIME (secure MIME) message to become 
	 * secured SFRM Message.
	 * <br><br>
	 * 
	 * Currently, the packing mechanisms support: <br>
	 * <ol>
	 * 	<li> Digitial Signing using MD5 or SHA-1 </li>
	 *  <li> Encryption using RC2_CBC or DES_EDE3_CBC </li>
	 * </ol>
	 *   
	 * @param message
	 * 			The outgoing SFRM Message.
	 * @param msgDVO
	 * 			The message record associated to this SFRM message. 
	 * @param pDVO
	 * 			
	 * @return
	 * 			The secured SFRM message.
	 * @throws UnrecoverableKeyException 
	 * @throws NoSuchAlgorithmException 
	 * @throws SFRMException 
	 * 
	 * @since	
	 * 			1.0.3 
	 */
	protected SFRMMessage packOutgoingMessage(
			SFRMMessage message, String signAlgorithm, String encryptAlgorithm, X509Certificate encryptCert) 
		throws SFRMException, NoSuchAlgorithmException, UnrecoverableKeyException {
	
		// No need to sign and encrypt, return immediately.
		if (signAlgorithm == null && encryptAlgorithm == null)
			return message;
		
		// Create SMIME Header.
		KeyStoreManager keyman = getKeyStoreManager();
		
		String logInfo = " msg id: " + message.getMessageID()
						+" and sgt no: " + message.getSegmentNo();
		
		// Setup up signing using MD5 or SHA1		
		if(signAlgorithm != null){
			getLogger().info(SFRMLog.OMH_CALLER + SFRMLog.SIGNING_SGT + logInfo);  
			message.sign(keyman.getX509Certificate(), keyman.getPrivateKey(), signAlgorithm);
		}
		
		// Setup up encrypting using RC2, DES
		if(encryptAlgorithm != null){
			getLogger().info(SFRMLog.OMH_CALLER + SFRMLog.ENCRYPT_SGT + logInfo);
			message.encrypt(encryptCert, encryptAlgorithm);
		}
						
		return message;
	}
	
	/**
	 * Send SFRM message.
	 * <br><br>
	 * 
	 * @param message The original SFRM Message.
	 * @param isSign Digital signature is required
	 * @param isEncryptReq Encryption is required
	 * @param signAlg Signing algorithm
	 * @param encryptAlg Encryption algorithm  
	 * @param encrypt Partner public certificate for encryption
	 * 			
	 * @return HTTP response
	 * 
	 * @throws SFRMMessageException 			
	 * @throws ConnectionException
	 * 
	 * @since  2.0.0	 
	 */
	// FIXME: other segment type is signed or encrypted in task
	public FastHttpConnector sendMessage (
			SFRMMessage message, String endpoint, boolean isHostVerified,
			String signAlg, String encryptAlg, X509Certificate encryptCert) 
		throws SFRMMessageException, ConnectionException {
		
		if (message == null)
			throw new SFRMMessageException("Missing SFRM Message");
		
		// Pack the SFRM Message
		// TODO: All segment should use this method to pack, re-design interface.

		if (message.getSegmentType().equals(SFRMConstant.MSGT_META)){	
			try {
				this.packOutgoingMessage(message, signAlg, encryptAlg, encryptCert);
			} catch (Exception e) {
				throw new SFRMMessageException("Failed to sign/encrypt message", e);
			}
		}			
		// Create the HTTP Connection.
		// TODO: Use connection pool.
		// TODO: refactor and more test on FastHttpConnector	
			
		FastHttpConnector httpConn;
		try {
			httpConn = new FastHttpConnector(endpoint);
		} catch (MalformedURLException e) {
			throw new ConnectionException("Failed to create FastHttpConnector", e);
		}
		
		// Add SSL Verification if switched on.
		if (isHostVerified)
			httpConn.setHostnameVerifier(new TrustedHostnameVerifier());
		
		// Log sending information.
		getLogger().info(
			  SFRMLog.OMH_CALLER
		   +  SFRMLog.SEND_SGT
		   +" To " + endpoint
		   +" with msg info"
		   +  message);
		
		int responseCode; 
		try{						
			SFRMMessageClassifier classifier = message.getClassifier();
			
			if(message.getSegmentType().equals(SFRMConstant.MSGT_PAYLOAD) && !classifier.isEncrypted() && !classifier.isSigned()){
				FileRegionDataSource fSrc = (FileRegionDataSource) message.getContent();
				httpConn.send(fSrc.getInputStream(), message.getHeaders());
			}else{
				httpConn.send(message.getContentStream(), message.getHeaders());
			}
			
			responseCode = httpConn.getResponseCode();
			if (responseCode < 200 || responseCode > 300)
				throw new ConnectionException("Invalid Response Code.");
			
			return httpConn;
		} catch (Exception e){
			throw new ConnectionException("Failed to make FastHttpConnector connection", e);
		}		
	}
	
		
	public SFRMMessage sendMessageWithMessageResponse (
			SFRMMessage message, String endpoint, boolean isHostVerified,
			String signAlg, String encryptAlg, X509Certificate encryptCert) 
		throws SFRMMessageException, ConnectionException, IllegalStateException, IOException {
		
		FastHttpConnector conn = sendMessage(message, endpoint, isHostVerified,
				signAlg, encryptAlg, encryptCert);
		
		SFRMMessage retMessage = new SFRMMessage(conn.getResponseHeaders(), conn.getResponseContentStream());
		return retMessage;								
	}

}
