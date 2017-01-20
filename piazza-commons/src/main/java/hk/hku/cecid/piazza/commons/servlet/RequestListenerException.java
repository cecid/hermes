package hk.hku.cecid.piazza.commons.servlet;

/**
 * RequestListenerException represents all kinds of exception related to
 * RequestListener.
 * 
 * @author Hugo Y. K. Lam
 */
public class RequestListenerException extends
        hk.hku.cecid.piazza.commons.GenericException {

    /**
     * Creates a new instance of RequestListenerException.
     */
    public RequestListenerException() {
        super();
    }

    /**
     * Creates a new instance of RequestListenerException.
     * 
     * @param message the error message.
     */
    public RequestListenerException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of RequestListenerException.
     * 
     * @param cause the cause of this exception.
     */
    public RequestListenerException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of RequestListenerException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public RequestListenerException(String message, Throwable cause) {
        super(message, cause);
    }
}