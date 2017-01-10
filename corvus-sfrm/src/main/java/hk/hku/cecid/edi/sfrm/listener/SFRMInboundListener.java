package hk.hku.cecid.edi.sfrm.listener;

import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;

import hk.hku.cecid.piazza.commons.servlet.RequestListenerException;

/**
 * This is the inbound listener for receiving the payload segment 
 * from the sender.
 * 
 * @author Twinsen
 * @version	1.0.0
 * @since 	1.0.0
 */
public class SFRMInboundListener extends SFRMRequestAdaptor {

	
    /**
     * @see hk.hku.cecid.edi.sfrm.listener.SFRMRequestAdaptor#processRequest(hk.hku.cecid.edi.as2.listener.SFRMRequest, hk.hku.cecid.edi.as2.listener.SFRMResponse)
     */
    public void processRequest(SFRMRequest request, SFRMResponse response) 
            throws RequestListenerException {
        try {
        	SFRMMessage requestMessage = request.getMessage();        
        	SFRMProcessor.getInstance().getLogger().debug("Request Type: " + requestMessage.getSegmentType());
        	
           	SFRMMessage responseMessage = SFRMProcessor.getInstance()          	           	
				.getIncomingMessageHandler().processIncomingMessage(
					requestMessage, null);
           	
			response.setMessage(responseMessage);
        }
        catch (Exception e) {
            throw new RequestListenerException("Unable to process inbound SFRM message", e);
        }
    }    
}
