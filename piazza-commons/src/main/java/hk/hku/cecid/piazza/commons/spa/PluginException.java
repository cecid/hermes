package hk.hku.cecid.piazza.commons.spa;

/**
 * PluginException represents all kinds of exception related to
 * the plugin framework.
 * 
 * @author Hugo Y. K. Lam
 */
public class PluginException extends
        hk.hku.cecid.piazza.commons.GenericException {

    /**
     * Creates a new instance of PluginException.
     */
    public PluginException() {
        super();
    }

    /**
     * Creates a new instance of PluginException.
     * 
     * @param message the error message.
     */
    public PluginException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of PluginException.
     * 
     * @param cause the cause of this exception.
     */
    public PluginException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of PluginException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }
}