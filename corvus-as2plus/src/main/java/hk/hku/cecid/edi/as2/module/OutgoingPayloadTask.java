/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.module;

import hk.hku.cecid.edi.as2.AS2Exception;
import hk.hku.cecid.edi.as2.AS2PlusProcessor;
import hk.hku.cecid.edi.as2.dao.PartnershipDAO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.piazza.commons.module.ActiveTask;
//import hk.hku.cecid.edi.as2.pkg.DispositionNotificationOption;
//import hk.hku.cecid.piazza.commons.security.SMimeException;
//import hk.hku.cecid.piazza.commons.security.SMimeMessage;

import javax.activation.FileDataSource;



/**
 * OutgoingPayloadTask
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class OutgoingPayloadTask implements ActiveTask {

    // private int retried;
    private PayloadCache payload;
    
    /**
     * @throws AS2Exception
     * 
     */
    public OutgoingPayloadTask(PayloadCache payload) throws AS2Exception {
        try {
            if (payload == null) {
                throw new AS2Exception("No payload data");
            }
            
            this.payload = payload;
            
            if (!this.payload.checkOut()) {
                throw new AS2Exception("Unable to check out payload: "+payload);
            }
        }
        catch (Exception e) {
            throw new AS2Exception("Unable to construct outgoing payload task", e);
        }
    }

    /**
     * execute
     * @throws Exception
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#execute()
     */
    public void execute() throws Exception {
    	
    	//Retreive Message Header Value
        String msgId = payload.getMessageID();
        String as2From = payload.getFromPartyID();
        String as2To = payload.getToPartyID();
        
        //Prepare to read message file
        FileDataSource cacheSource = new FileDataSource(payload.getCache());
        
        PartnershipDAO dao = (PartnershipDAO)AS2PlusProcessor.getInstance().getDAOFactory().createDAO(PartnershipDAO.class);
        PartnershipDVO partnership = dao.findByParty(as2From, as2To); 
        
        try{
        	OutgoingMessageProcessor outProcessor = AS2PlusProcessor.getInstance().getOutgoingMessageProcessor();
        	AS2Message as2Msg =
    		outProcessor.storeOutgoingMessage(
    				msgId, payload.getContentType(),
    				partnership, cacheSource, null);
    	
        	if(as2Msg == null){
        		throw new NullPointerException("AS2 Message ["+payload.getMessageID()+"] is null when loading to database." +
        				"Partnership id:" +partnership.getPartnershipId()  );
        	}
        }catch(Exception exp){
        	throw exp;
        }
        
    }
    
    /* Refactor to perform on OutgoingMessageProcessor
     
    private String calculateMIC(SMimeMessage smime, PartnershipDVO partnership) throws SMimeException {
        String mic = null;
        if (partnership.isReceiptSignRequired()) {
            boolean isSMime = partnership.isOutboundCompressRequired() ||
                              partnership.isOutboundSignRequired() ||
                              partnership.isOutboundEncryptRequired();
            
            String micAlg = partnership.getMicAlgorithm();
            if (micAlg !=null && micAlg.equalsIgnoreCase(PartnershipDVO.ALG_MIC_MD5)) {
                mic = smime.digest(SMimeMessage.DIGEST_ALG_MD5, isSMime);
                micAlg = DispositionNotificationOption.SIGNED_RECEIPT_MICALG_MD5;
            }
            else {
                mic = smime.digest(SMimeMessage.DIGEST_ALG_SHA1, isSMime);
                micAlg = DispositionNotificationOption.SIGNED_RECEIPT_MICALG_SHA1;
            }
            mic =  mic + ", " + micAlg;
        }
        return mic;
    }
    */

    /**
     * onFailure
     * @param e
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#onFailure(java.lang.Throwable)
     */
    public void onFailure(Throwable e) {
        AS2PlusProcessor.getInstance().getLogger().error("Outgoing payload task failure", e);
    }

    /**
     * isRetryEnabled
     * @return boolean
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#isRetryEnabled()
     */
    public boolean isRetryEnabled() {
        return false;
    }

    /**
     * getRetryInterval
     * @return long
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#getRetryInterval()
     */
    public long getRetryInterval() {
        return -1;
    }

    /**
     * getMaxRetries
     * @return int
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#getMaxRetries()
     */
    public int getMaxRetries() {
        return 0;
    }

    /**
     * setRetried
     * @param retried
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#setRetried(int)
     */
    public void setRetried(int retried) {
        // this.retried = retried;
    }

    /**
     * onAwake
     * 
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#onAwake()
     */
    public void onAwake() {
    }

    /**
     * isSucceedFast
     * @return boolean
     * @see hk.hku.cecid.piazza.commons.module.ActiveTask#isSucceedFast()
     */
    public boolean isSucceedFast() {
        return true;
    }
}
