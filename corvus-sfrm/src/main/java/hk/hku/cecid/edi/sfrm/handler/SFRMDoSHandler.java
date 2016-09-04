package hk.hku.cecid.edi.sfrm.handler;

import hk.hku.cecid.piazza.commons.module.Component;

import hk.hku.cecid.edi.sfrm.pkg.SFRMMessage;
import hk.hku.cecid.edi.sfrm.pkg.SFRMConstant;
import hk.hku.cecid.edi.sfrm.pkg.SFRMMessageClassifier;
import hk.hku.cecid.edi.sfrm.util.TimedOutHashTable;

/**
 * The <code>SFRMDoSHandler</code> is a simple barrier to ensure
 * there is ONLY one-thread working per segment.<br/><br/>
 * 
 * When an incoming message is received and prepare to process, the 
 * <strong>IMH</strong> invoke {@link #enter(SFRMMessage)} asking 
 * the DoSHandler to insert the working record for this segment.
 * Then if there is a duplicate message received, the DosHandler 
 * reject it due to the working record has already exist.<br/><br/> 
 * 
 * Thus it guarantees ONE THREAD WORKING per segment semantics.
 * <br/><br/>
 * 
 * <strong>CAUTION</strong>: When the thread in the working record
 * is not alive, the DoSHandler considers the working record is 
 * redundant and <strong>ALLOW</strong> message with same 
 * composite key owning a barrier for that message.
 *         
 * Creation Date: 28/6/2007
 * 
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10606
 */
public class SFRMDoSHandler extends Component {

	// The backed hash table supporting time out.
	private TimedOutHashTable ddosTable = new TimedOutHashTable();
	 
	/**
	 * Invoke for requesting a barrier for <code>message</code>.
	 * <br/><br/>
	 * For this case, the requested barrier does not expire. 
	 * 
	 * @param message 	The incoming SFRM Message.
	 * @return if the message has been entered by other thread before,
	 *         it return false. otherwise, the barrier for this 
	 *         <code>message</code> is created and the owner is 
	 *         the invocation thread.  
	 */
	public boolean enter(final SFRMMessage message){
		return this.enter(message, -1);
	}
	
	/**
	 * Invoke for requesting a barrier for <code>message</code>.   
	 * 
	 * @param message 	The incoming SFRM Message.
	 * @param lifetime	How long is the barrier expire in millisecond.
	 * 					The common use for this is managing timeout/retry for
	 * 					a message.	
	 * 
	 * @return if the message has been entered by other thread before,
	 *         it return false. otherwise, the barrier for this 
	 *         <code>message</code> is created and the owner is 
	 *         the invocation thread.  
	 */
	public boolean enter(final SFRMMessage message, long lifetime){
		
		if (message == null) return false;
		
		String key = this.getResolvedKey(message);
		
		// Check whether if there is any thread working on this message.
		// External Synchronization is required because only the hash
		// table guarantees only thread safety of single operation like #get.
		// If multiple operation like get and put, it doesn't perform
		// atomicity unless adding synchronized block.
		synchronized(this){
			Thread t = (Thread) ddosTable.get(key);
			// Create working only if the working thread is null or is done already.
			if (t == null || (t != null && !t.isAlive())){				
				if (lifetime == -1)
					ddosTable.put(key, Thread.currentThread());
				else
					ddosTable.put(key, Thread.currentThread(), lifetime);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Invoke for removing the barrier for a <code>message</code>.
	 * <br/><br/>
	 * The internal barrier for this <code>message</code> is removed 
	 * and therefore invocating {@link #enter(SFRMMessage)} for this 
	 * <code>mesasge</code> return true again.
	 * 
	 * @param message	The incoming SFRM Message.
	 * @return it returns true iff the working record exists and remove
	 *         successfully. 
	 */
	public boolean exit(final SFRMMessage message){
		return (ddosTable.remove(this.getResolvedKey(message)) != null);
	}
		
	/**
	 * Get the composite key from the <code>message</code>.
	 * <br/><br/> 
	 * What it does is generating one string indentifying the message.
	 * <br/><br/>
	 * For example: 
	 * <pre>
	 *  Input message id: test@message-id
	 *  Input segment type: PAYLOAD
	 *  Input segment number: 999
	 *   
	 *  Then the resolved key is <em>test@message-id_INBOX_PAYLOAD_999</em>
	 * </pre>  
	 * 
	 * @param message	The incoming SFRM Message.
	 * @return the composite key of thie SFRM Message.
	 */
	public String getResolvedKey(final SFRMMessage message)
	{
		SFRMMessageClassifier mc = message.getClassifier();
		if (mc.isMeta()){
			return message.getMessageID();
		} else {
			return new StringBuffer(message.getMessageID())
				.append("_")
				.append(SFRMConstant.MSGBOX_IN)
				.append("_")
				.append(message.getSegmentType())
				.append("_")
				.append(message.getSegmentNo()).toString();
		}
	}
}
