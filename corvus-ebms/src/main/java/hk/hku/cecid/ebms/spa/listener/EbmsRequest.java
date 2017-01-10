package hk.hku.cecid.ebms.spa.listener;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;


/**
 * @author Donahue Sze
 *
 */
public class EbmsRequest {

    private Object source = null;
    private EbxmlMessage message = null;
    
    public EbmsRequest() {
        this(null);
    }
    
    public EbmsRequest(Object source) {
        this.source = source;
    }
    
    
    /**
     * @return Returns the msg.
     */
    public EbxmlMessage getMessage() {
        return message;
    }
    
    /**
     * @param message The msg to set.
     */
    public void setMessage(EbxmlMessage message) {
        this.message = message;
    }
    
    /**
     * @return Returns the source.
     */
    public Object getSource() {
        return source;
    }
    
    /**
     * @param source The source to set.
     */
    public void setSource(Object source) {
        this.source = source;
    }
}
