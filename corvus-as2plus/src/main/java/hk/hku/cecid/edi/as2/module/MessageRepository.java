/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.module;

import hk.hku.cecid.edi.as2.dao.MessageDVO;
import hk.hku.cecid.edi.as2.pkg.AS2Message;

import java.io.FileOutputStream;
import java.io.InputStream;


/**
 * MessageRepository
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class MessageRepository extends PayloadRepository {

    private final String DEFAULT_AS2_MESSAGE_TYPE = "as2";
    private final String DEFAULT_AS2_RECEIPT_TYPE = "mdn";
    
    private final String PARAMETER_IS_DISABLED = "is-disabled";
    
    private boolean isDisabled = false;
    
    /**
     * 
     */
    public MessageRepository() {
        super();
    }

    protected void init() throws Exception {
        super.init();
        isDisabled = new Boolean(getParameters().getProperty(PARAMETER_IS_DISABLED)).booleanValue();
    }
    
    public void persistMessage(AS2Message message) {
        if (!isDisabled && message!=null) {
            try {
                String type = message.isDispositionNotification()? DEFAULT_AS2_RECEIPT_TYPE:DEFAULT_AS2_MESSAGE_TYPE;
        
                PayloadCache cache = createPayloadCache(
                        message.getMessageID(), 
                        message.getFromPartyID(), 
                        message.getToPartyID(), 
                        type);
                
                FileOutputStream fos = new FileOutputStream(cache.getCache());
                message.writeTo(fos);
                fos.close();
                cache.checkIn();
            }
            catch (Exception e) {
                getModule().getLogger().warn("Unable to persist message to local repository", e);
            }
        }
    }
    
    public void persistMessage(MessageDVO message, InputStream content) {
        if (!isDisabled && message!=null && content!=null) {
            try {
                String type = message.isReceipt()? DEFAULT_AS2_RECEIPT_TYPE:DEFAULT_AS2_MESSAGE_TYPE;
        
                PayloadCache cache = createPayloadCache(
                        message.getMessageId(), 
                        message.getAs2From(), 
                        message.getAs2To(), 
                        type);
        
                cache.save(content);
                cache.checkIn();
            }
            catch (Exception e) {
                getModule().getLogger().warn("Unable to persist message to local repository", e);
            }
        }
    }
}
