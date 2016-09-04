/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.listener;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.handler.MessageServiceHandler;
import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;

/**
 * @author Donahue Sze
 */
public class EbmsInboundListener extends EbmsAdaptor {

    private MessageServiceHandler msh;

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.servlet.RequestListener#listenerCreated()
     */
    /** Handles event for servlet start up */
    public void listenerCreated() throws RequestListenerException {
        super.listenerCreated();
        try {
            msh = MessageServiceHandler.getInstance();
        } catch (Throwable e) {
            throw new RequestListenerException(
                    "Error in creating MSH in EbmsInboundListener", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.servlet.RequestListener#listenerDestroyed()
     */
    public void listenerDestroyed() throws RequestListenerException {
        super.listenerDestroyed();        
        msh.destroy();
        msh = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.ebms.spa.listener.EbmsAdaptor#processRequest(hk.hku.cecid.ebms.spa.listener.EbmsRequest,
     *      hk.hku.cecid.ebms.spa.listener.EbmsResponse)
     */
    public void processRequest(EbmsRequest request, EbmsResponse response)
            throws RequestListenerException {
        try {
            msh.processInboundMessage(request, response);
        } catch (Exception e) {
            EbmsProcessor.core.log.debug("Error in processing incoming message", e);
            throw new RequestListenerException(
                    "Error in processing incoming message", e);
        }
    }
}