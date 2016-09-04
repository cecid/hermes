package hk.hku.cecid.ebms.spa.task;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.piazza.commons.module.Component;

public abstract class EbmsEventListener extends Component {	
	public abstract void messageSent(EbxmlMessage requestMessage);
	public abstract void messageReceived(EbxmlMessage requestMessage);
	public abstract void responseReceived(EbxmlMessage acknowledgement);
	public abstract void errorOccurred(EbxmlMessage errorMessage);
}
