package hk.hku.cecid.piazza.commons.ejb;

/**
 * EjbConnectionException represents all kinds of exception occurred in an EJB
 * connection.
 * 
 * @author Hugo Y. K. Lam
 */
public class EjbConnectionException extends Exception {

    /**
     * Default constructor of EjbConnectionException.
     */
    public EjbConnectionException() {
        super();
    }

    /**
     * Constructor of EjbConnectionException.
     * 
     * @param message the error message
     */
    public EjbConnectionException(String message) {
        super(message);
    }

    /**
     * Constructor of EjbConnectionException.
     * 
     * @param cause the cause of this exception
     */
    public EjbConnectionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor of EjbConnectionException.
     * 
     * @param message the error message
     * @param cause the cause of this exception
     */
    public EjbConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}