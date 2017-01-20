package hk.hku.cecid.piazza.commons.soap;


/**
 * SOAPRequestListener is a listener for handling SOAP requests.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public interface SOAPRequestListener {

    /**
     * Processes the SOAP request.
     * 
     * @param request the SOAP request.
     * @param response the SOAP response.
     * @throws SOAPRequestException if there is any error in processing the SOAP request.
     */
    public void processRequest(SOAPRequest request, SOAPResponse response)
            throws SOAPRequestException;
}