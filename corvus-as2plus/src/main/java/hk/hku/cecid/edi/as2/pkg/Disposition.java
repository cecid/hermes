/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.pkg;

import java.util.StringTokenizer;

/**
 * Disposition represents an AS2 disposition.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class Disposition {
    
    public static final String ACTION_MODE_AUTOMATIC = "automatic-action";

    public static final String ACTION_MODE_MANUAL = "manual-action";

    public static final String SENDING_MODE_AUTOMATIC = "MDN-sent-automatically";

    public static final String SENDING_MODE_MANUAL = "MDN-sent-manually";

    public static final String TYPE_PROCESSED = "processed";
    
    public static final String TYPE_FAILED = "failed";
    
    public static final String MODIFIER_ERROR = "error";
    
    public static final String MODIFIER_WARNING = "warning";
    
    public static final String MODIFIER_FAILURE = "failure";
    
    public static final String DESC_AUTHENTICATION_FAILED = "authentication-failed";
    
    public static final String DESC_DECOMPRESSION_FAILED = "decompression-failed";
    
    public static final String DESC_DECRYPTION_FAILED = "decryption-failed";
    
    public static final String DESC_INSUFFICIENT_MESSAGE_SECURITY = "insufficient-message-security";
    
    public static final String DESC_INTEGRITY_CHECK_FAILED = "integrity-check-failed";
    
    public static final String DESC_UNEXPECTED_PROCESSING_ERROR = "unexpected-processing-error";
    
    public static final String DESC_UNSUPPORTED_FORMAT = "unsupported format";
    
    public static final String DESC_UNSUPPORTED_MIC_ALGORITHM = "unsupported MIC-algorithm";
    
    private String actionMode;

    private String sendingMode;

    private String type;

    private String modifier;

    private String description;

    public Disposition() {
        this(ACTION_MODE_AUTOMATIC, SENDING_MODE_AUTOMATIC, TYPE_PROCESSED);
    }
    
    public Disposition(String actionMode, String sendingMode, String type) {
        this(actionMode, sendingMode, type, null, null);
    }

    public Disposition(String actionMode, String sendingMode, String type,
            String modifier, String statusDescription) {
        this.initDesposition(actionMode, sendingMode, type, modifier, description);
    }

    public Disposition(String disposition) throws AS2MessageException {
        this.parseDisposition(disposition);
    }
    
    private void parseDisposition(String disposition) throws AS2MessageException {
        if (disposition != null) {
            try {
                StringTokenizer tokens = new StringTokenizer(disposition, "/;:", false);
    
                setActionMode(tokens.nextToken().toLowerCase());
                setSendingMode(tokens.nextToken().toLowerCase());
                setType(tokens.nextToken().trim().toLowerCase());
    
                if (tokens.hasMoreTokens()) {
                    setModifier(tokens.nextToken().toLowerCase());
    
                    if (tokens.hasMoreTokens()) {
                        setDescription(tokens.nextToken().trim()
                                .toLowerCase());
                    }
                }
            } catch (Exception nsee) {
                throw new AS2MessageException("Invalid disposition: " + disposition);
            }
        }
    }

    private void initDesposition(String actionMode, String sendingMode, String type,
            String modifier, String description) {
        this.actionMode = actionMode;
        this.sendingMode = sendingMode;
        this.type = type;
        this.modifier = modifier;
        this.description = description;
    }

    public String getDispositionMode() {
        return getActionMode() + "/" + getSendingMode();
    }
    
    public void setActionMode(String action) {
        this.actionMode = action;
    }

    public String getActionMode() {
        return actionMode;
    }

    public void setSendingMode(String mdnAction) {
        this.sendingMode = mdnAction;
    }

    public String getSendingMode() {
        return sendingMode;
    }

    public void setType(String status) {
        this.type = status;
    }

    public String getType() {
        return type;
    }

    public void setDescription(String statusDescription) {
        this.description = statusDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setModifier(String statusModifier) {
        this.modifier = statusModifier;
    }

    public String getModifier() {
        return modifier;
    }

    public boolean isWarning() {
        return MODIFIER_WARNING.equalsIgnoreCase(modifier);
    }
    
    public boolean isError() {
        return MODIFIER_ERROR.equalsIgnoreCase(modifier);
    }

    public void validate() throws DispositionException {
        if (getType() == null) {
            throw new DispositionException(this, null);
        } else if (!type.equalsIgnoreCase(TYPE_PROCESSED)) {
            throw new DispositionException(this, null);
        }

        String modifier = getModifier();
        if (modifier != null) {
            if (    modifier.equalsIgnoreCase(MODIFIER_ERROR) ||
                    modifier.equalsIgnoreCase(MODIFIER_WARNING) ||
                    modifier.equalsIgnoreCase(MODIFIER_FAILURE)
                ) {
                throw new DispositionException(this, null);
            }
        }
    }

    public String toString() {
        StringBuffer disposition = new StringBuffer();
        disposition.append(getDispositionMode()).append("; ").append(getType());

        if (getModifier() != null) {
            disposition.append("/").append(getModifier());

            if (getDescription() != null) {
                disposition.append(": ").append(getDescription());
            }
        }
        return disposition.toString();
    }
}