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
 * SOAPRequestException represents all kinds of exception related to a SOAP
 * request or its process.
 * 
 * @author Hugo Y. K. Lam
 */
public class SOAPRequestException extends
        hk.hku.cecid.piazza.commons.GenericException {

    /**
     * Creates a new instance of SOAPRequestException.
     */
    public SOAPRequestException() {
        super();
    }

    /**
     * Creates a new instance of SOAPRequestException.
     * 
     * @param message the error message.
     */
    public SOAPRequestException(String message) {
        super(message);
    }

    /**
     * Creates a new instance of SOAPRequestException.
     * 
     * @param cause the cause of this exception.
     */
    public SOAPRequestException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance of SOAPRequestException.
     * 
     * @param message the error message.
     * @param cause the cause of this exception.
     */
    public SOAPRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Gets the SOAP fault exception which caused this exception.
     * 
     * @return the SOAP fault exception which caused this exception or null if
     *         there is none.
     */
    public SOAPFaultException getSOAPFault() {
        Throwable e = this;
        do {
            if (e instanceof SOAPFaultException) {
                return (SOAPFaultException) e;
            }
            else {
                e = e.getCause();
            }
        }
        while (e != null);
        return null;
    }

    /**
     * Checks if this exception is caused by a SOAP fault exception.
     * 
     * @return true if this exception is caused by a SOAP fault exception.
     */
    public boolean isSOAPFault() {
        return getSOAPFault() != null;
    }
}