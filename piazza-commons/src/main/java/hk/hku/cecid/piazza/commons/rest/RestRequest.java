package hk.hku.cecid.piazza.commons.rest;

/**
 * The RestRequest class represents a Restful service request.
 * It is independent of which access protocol it is using
 * and contains the bodies of the request message.
 * 
 * @author Patrick Yee
 *  
 */
public class RestRequest {

    private Object source;

    /**
     * Creates a new instance of RestRequest.
     */
    public RestRequest() {
    }

    /**
     * Creates a new instance of RestRequest.
     *
     * @param source the source which initiated this request.
     */
    public RestRequest(Object source) {
        this.source = source;
    }

    /**
     * Sets the source which initiated this request.
     * 
     * @param source the source which initiated this request.
     */
    public void setSource(Object source) {
        this.source = source;
    }

    /**
     * Gets the source which initiated this request.
     * 
     * @return the source which initiated this request.
     */
    public Object getSource() {
        return source;
    }
}
