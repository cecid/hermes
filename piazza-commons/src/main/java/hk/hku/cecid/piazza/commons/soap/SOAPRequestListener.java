/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

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