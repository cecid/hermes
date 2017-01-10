package hk.hku.cecid.corvus.ws.data;

/**
 * The <code>AS2PartnershipData</code> is 
 * 
 * @author 	Twinsen Tsang
 * @version	1.0.0
 * @since	Elf 0818 
 */
public class AS2PartnershipData extends KVPairData {

	/** The constant field for RC2 Encryption. */
	public static final String ALG_ENCRYPT_RC2 = "rc2";

	/** The constant field for 3DES Encryption. */
	public static final String ALG_ENCRYPT_3DES = "3des";

	/** The constant field for SHA1 Message-Integrity-Check. */	
	public static final String ALG_MIC_SHA1 = "sha1";

	/** The constant field for MD5 Message-Integrity-Check. */
	public static final String ALG_MIC_MD5 = "md5";

	/** The constant field for SHA1 Signing. */
	public static final String ALG_SIGN_SHA1 = "sha1";

	/** The constant field for MD5 Signing. */
	public static final String ALG_SIGN_MD5 = "md5";

	/**
	 * This is the key set for XML serialization / de-serialization.<br/><br/>
	 */
	public static final String [] PARAM_KEY_SET = 
	{
		"id"                , "disabled"			  , "isSyncReply", 
		"subject"			, "recipientAddress"      , "isHostnameVerified",
		"receiptAddress"    , "isReceiptRequired"     , "isOutboundSignRequired",
		"isOutboundEncryptRequired"	, "isOutboundCompressRequired", 
		"isReceiptSignRequired"     , "isInboundSignRequired",
		"isInboundEncryptRequired"  , "retries",
		"retryInterval"     , "signAlgorithm"		  , "encryptAlgorithm",
		"micAlgorithm"      , "as2From"				  ,	"as2To", 
		"encryptCert"       , "verifyCert"		
	};
	
	static final Class [] PARAM_CLASS_SET = 
	{
		String.class		, String.class,		String.class,
		String.class		, String.class,		String.class,
		String.class		, String.class,		String.class,
		String.class		, String.class,		
		String.class		, String.class,		
		String.class		, String.class,		
		String.class		, String.class,		String.class,		
		String.class		, String.class,		String.class,			
		byte[].class		, byte[].class
	};
	
			
	/**
	 * This is the parameters prefix for serialization / de-serialization.<br/><br/>
	 */
	public static final String PARAM_PREFIX  = "/partnership/as2";
			
	/**
	 * Default Constructor.
	 */
	public AS2PartnershipData(){
		super(PARAM_KEY_SET.length);
	}

	/** 
	 * @return The partnership id for this partnership.
	 */
	public String getPartnershipId(){
		return (String)props.get("id");
	}		
	
	/**
     * @param partnershipId The partnership id for this partnership.
     */
	public void setPartnershipId(String partnershipId){
		props.put("id", partnershipId);
	}	

	/** 
	 * @return the partnership is disabled ? 
	 */
	public boolean isDisabled(){
		return Boolean.valueOf((String)props.get("disabled")).booleanValue();
	}	
	
	/**
	 * Set the partnership is enabled or not.
	 * 
	 * @param isDisabled the flag whether the partnership is enabled or not.	
	 */
	public void setIsDisabled(boolean isDisabled){
		props.put("disabled",String.valueOf(isDisabled));
	}

	/** 
	 * @return the message required sync reply
	 */
	public boolean isSyncReply(){
		return Boolean.valueOf((String)props.get("isSyncReply")).booleanValue();
	}	
	
	/**
	 * Set the message require sync reply
	 * 
	 * @param isSyncReply
	 */
	public void setIsSyncReply(boolean isSyncReply){
		props.put("isSyncReply", String.valueOf(isSyncReply));
	}
	
	/** 
	 * @return Get the subject of the message.
	 */
	public String getSubject(){
		return (String) props.get("subject");
	}	

	/** 
	 * @param subject the subject of the message.
	 */
	public void setSubject(String subject){
		props.put("subject", subject);
	}

	/** 
	 * @return Get recipient address of the message.
	 */
    public String getRecipientAddress() {
        return (String) props.get("recipientAddress");
    }    

	/** 
	 * @param recipientAddress the recipient address of the message.
	 */
	public void setRecipientAddress(String recipientAddress) {
        props.put("recipientAddress", recipientAddress);
    }
	
	/**
	 * @return True if hostname is verified.
	 */	
	public boolean isHostnameVerified() {
        return Boolean.valueOf((String)props.get("isHostnameVerified")).booleanValue();
    }

	/**
	 * @param isHostnameVerified true if the hostname is verified.
	 */	
    public void setIsHostnameVerified(boolean isHostnameVerified) {
        props.put("isHostnameVerified", String.valueOf(isHostnameVerified));
    }

	/**
	 * @return Get receipt address.
	 */	
    public String getReceiptAddress() {
        return (String)props.get("receiptAddress");
    }

	/**
	 * @param receiptAddress the receipt address
	 */	
    public void setReceiptAddress(String receiptAddress) {
        props.put("receiptAddress", receiptAddress);
    }
    
	/**
	 * @return True if receipt required.
	 */	
    public boolean isReceiptRequired() {
        return Boolean.valueOf((String)props.get("isReceiptRequired")).booleanValue();
    }

	/**
	 * @param isReceiptRequired true if receipt required.
	 */	
    public void setIsReceiptRequired(boolean isReceiptRequired) {
        props.put("isReceiptRequired", String.valueOf(isReceiptRequired));
    }
    
	/**
	 * @return True if outbound signing is required.
	 */	
    public boolean isOutboundSignRequired() {
        return Boolean.valueOf((String)props.get("isOutboundSignRequired")).booleanValue();
    }

	/**
	 * @param isOutboundSignRequired true if outbound signing is required.
	 */	
    public void setIsOutboundSignRequired(boolean isOutboundSignRequired) {
        props.put("isOutboundSignRequired", String
                .valueOf(isOutboundSignRequired));
    }
    
	/**
	 * @return true if outbound encryption is required.
	 */	
    public boolean isOutboundEncryptRequired() {
        return Boolean.valueOf((String)props.get("isOutboundEncryptRequired")).booleanValue();
    }

	/**
	 * @param isOutboundEncryptRequired true if outbound encryption is required.
	 */	
    public void setIsOutboundEncryptRequired(boolean isOutboundEncryptRequired) {
        props.put("isOutboundEncryptRequired", String
                .valueOf(isOutboundEncryptRequired));
    }
    
	/**
	 * @return true if outbound compression is required.
	 */	
    public boolean isOutboundCompressRequired() {
        return Boolean.valueOf((String)props.get("isOutboundCompressRequired")).booleanValue();
    }

	/**
	 * @param isOutboundCompressRequired true if outbound compression is required.
	 */	
    public void setIsOutboundCompressRequired(boolean isOutboundCompressRequired) {
        props.put("isOutboundCompressRequired", String
                .valueOf(isOutboundCompressRequired));
    }
    
	/**
	 * @return true if receipt signing is required.
	 */	
    public boolean isReceiptSignRequired() {
        return Boolean.valueOf((String)props.get("isReceiptSignRequired")).booleanValue();
    }
  
	/**
	 * @param isReceiptSignRequired true if receipt signing is required.
	 */	
    public void setIsReceiptSignRequired(boolean isReceiptSignRequired) {
        props.put("isReceiptSignRequired", String
                .valueOf(isReceiptSignRequired));
    }
    
	/**
	 * @return true if inbound signing is required.
	 */	
    public boolean isInboundSignRequired() {
        return Boolean.valueOf((String)props.get("isInboundSignRequired")).booleanValue();
    }

	/**
	 * @param isInboundSignRequired true if inbound signing is required.
	 */	
    public void setIsInboundSignRequired(boolean isInboundSignRequired) {
        props.put("isInboundSignRequired", String
                .valueOf(isInboundSignRequired));
    }
    
	/**
	 * @return true if inbound encryption is required.
	 */	
    public boolean isInboundEncryptRequired() {
        return Boolean.valueOf((String)props.get("isInboundEncryptRequired")).booleanValue();
    }

	/**
	 * @param isInboundEncryptRequired true if inbound encrpytion is required.
	 */	
    public void setIsInboundEncryptRequired(boolean isInboundEncryptRequired) {
        props.put("isInboundEncryptRequired", String
                .valueOf(isInboundEncryptRequired));
    }
   
	/**
	 * @return retries.
	 */	
    public int getRetries() {
        return Integer.parseInt((String)props.get("retries"));
    }

	/**
	 * @param retries Retries.
	 */	    
    public void setRetries(int retries) {
        props.put("retries", String.valueOf(retries));
    }

	/**
	 * @return retry interval.
	 */	
    public int getRetryInterval() {
        return Integer.parseInt((String)props.get("retryInterval"));
    }
   
	/**
	 * @param retryInterval retries interval.
	 */	
    public void setRetryInterval(int retryInterval) {
        props.put("retryInterval", String.valueOf(retryInterval));
    } 

	/**
	 * @return signing algorithm.
	 */	    
    public String getSignAlgorithm() {
        return (String)props.get("signAlgorithm");
    }
   
	/**
	 * @param signAlgorithm signing algorithm.
	 */	
    public void setSignAlgorithm(String signAlgorithm) {
        props.put("signAlgorithm", signAlgorithm);
    }
    
	/**
	 * @return encrpytion algorithm.
	 */	
    public String getEncryptAlgorithm() {
        return (String) props.get("encryptAlgorithm");
    }

	/**
	 * @param encryptAlgorithm encryption algorithm.
	 */	    
    public void setEncryptAlgorithm(String encryptAlgorithm) {
        props.put("encryptAlgorithm", encryptAlgorithm);
    }
    
	/**
	 * @return checksum algorithm.
	 */	
    public String getMicAlgorithm() {
        return (String) props.get("micAlgorithm");
    }

	/**
	 * @param micAlgorithm checksum algorithm.
	 */	
    public void setMicAlgorithm(String micAlgorithm) {
        props.put("micAlgorithm", micAlgorithm);
    }
    
	/**
	 * @return AS2 From.
	 */	
    public String getAS2From() {
        return (String) props.get("as2From");
    }
    
	/**
	 * @param as2From AS2 From.
	 */	
    public void setAs2From(String as2From) {
        props.put("as2From", as2From);
    }

	/**
	 * @return AS2 To.
	 */	
    public String getAs2To() {
        return (String) props.get("as2To");
    }

	/**
	 * @param as2To AS2 To.
	 */	
    public void setAs2To(String as2To) {
        props.put("as2To", as2To);
    }

	/**
	 * @return encryption certificate in byte array.
	 */	
    public byte[] getEncryptCert() {
        return (byte[]) props.get("encryptCert");
    } 
    
	/**
	 * @param encryptCert encryption certificate in byte array.
	 */	
    public void setEncryptCert(byte[] encryptCert) {
        props.put("encryptCert", encryptCert);
    }
    
	/**
	 * @return verification certificate in byte array.
	 */	
    public byte[] getVerifyCert() {
        return (byte[]) props.get("verifyCert");
    }     
    
	/**
	 * @param verifyCert verification certificate in byte array.
	 */	
    public void setVerifyCert(byte[] verifyCert) {
        props.put("verifyCert", verifyCert);
    }
}
