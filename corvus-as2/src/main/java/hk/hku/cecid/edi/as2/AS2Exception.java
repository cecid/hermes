package hk.hku.cecid.edi.as2;

/**
 * AS2Exception represents all kinds of exception related to
 * AS2 processing.
 * 
 * @author Hugo Y. K. Lam
 */
public class AS2Exception extends
        hk.hku.cecid.piazza.commons.GenericException {

    /**
     * Creates a new instance of AS2Exception.
     */
    public AS2Exception() {
        super();
    }

    /**
     * Creates a new instance of AS2Exception.
     * 
     * @param message the error message.
     */
    public AS2Exception(String message) {
        super(message);
    }

    /**
     * Creates a new instance of AS2Exception.
     * 
     * @param cause the cause of this exception.
     */
    public AS2Exception(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of AS2Exception.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public AS2Exception(String message, Throwable cause) {
        super(message, cause);
    }
}