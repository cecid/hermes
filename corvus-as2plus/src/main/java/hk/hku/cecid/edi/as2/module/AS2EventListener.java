package hk.hku.cecid.edi.as2.module;

import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.piazza.commons.module.Component;

public abstract class AS2EventListener extends Component {
	public abstract void messageSent(AS2Message requestMessage);
	public abstract void messageReceived(AS2Message requestMessage);
	public abstract void responseReceived(AS2Message response);
	public abstract void errorOccurred(AS2Message errorResponse);
}
