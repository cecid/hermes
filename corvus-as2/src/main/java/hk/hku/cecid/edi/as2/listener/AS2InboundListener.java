/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.listener;

import hk.hku.cecid.edi.as2.AS2Processor;
import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;


/**
 * AS2InboundListener
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class AS2InboundListener extends AS2RequestAdaptor {

    /**
     * @see hk.hku.cecid.edi.as2.listener.AS2RequestAdaptor#processRequest(hk.hku.cecid.edi.as2.listener.AS2Request, hk.hku.cecid.edi.as2.listener.AS2Response)
     */
    public void processRequest(AS2Request request, AS2Response response) 
            throws RequestListenerException {
        try {
            AS2Message requestMessage = request.getMessage();
            if (requestMessage.isDispositionNotification()) {
                AS2Processor.getIncomingMessageProcessor().processReceipt(requestMessage);
            }
            else {
                AS2Message responseMessage = AS2Processor.getIncomingMessageProcessor().processMessage(requestMessage);
                response.setMessage(responseMessage);
            }
        }
        catch (Exception e) {
            throw new RequestListenerException("Unable to process inbound AS2 message", e);
        }
    }
}
