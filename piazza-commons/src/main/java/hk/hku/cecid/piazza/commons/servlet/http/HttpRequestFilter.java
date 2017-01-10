package hk.hku.cecid.piazza.commons.servlet.http;


/**
 * HttpRequestFilter
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public interface HttpRequestFilter {

    /**
     * Invoked when the an HTTP request is accepted.
     * 
     * @param event the HTTP request event.
     */
    public boolean requestAccepted(HttpRequestEvent event);

    /**
     * Invoked when the an HTTP request has been processed.
     * 
     * @param event the HTTP request event.
     */
    public void requestProcessed(HttpRequestEvent event);
}