package hk.hku.cecid.ebms.spa.listener;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;


/**
 * @author Donahue Sze
 *
 */
public class EbmsResponse {

    private Object target = null;
    private EbxmlMessage message = null;
    
    public EbmsResponse() {
        this(null);
    }
    
    public EbmsResponse(Object target) {
        this.target = target;
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
     * @return Returns the target.
     */
    public Object getTarget() {
        return target;
    }
    
    /**
     * @param target The target to set.
     */
    public void setTarget(Object target) {
        this.target = target;
    }
}
