package hk.hku.cecid.ebms.spa.task;

/**
 * DeliveryException represents all kinds of exception occuring in delivery.
 * 
 * @author Hugo Y. K. Lam
 */
public class DeliveryException extends
        hk.hku.cecid.piazza.commons.GenericException {

    /**
     * Creates a new instance of DeliveryException.
     */
    public DeliveryException() {
        super();
    }

    /**
     * Creates a new instance of DeliveryException.
     * 
     * @param message
     *            the error message.
     */
    public DeliveryException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of DeliveryException.
     * 
     * @param cause
     *            the cause of this exception.
     */
    public DeliveryException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of DeliveryException.
     * 
     * @param message
     *            the error message.
     * @param cause
     *            the cause of this exception.
     */
    public DeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}