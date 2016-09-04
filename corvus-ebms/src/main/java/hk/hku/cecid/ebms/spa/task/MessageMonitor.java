package hk.hku.cecid.ebms.spa.task;

import java.util.Date;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.module.ActiveModule;
import hk.hku.cecid.piazza.commons.module.ModuleException;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.handler.MessageClassifier;

/**
 * The <code>MessageMonitor</code> is a monitor which correct all 
 * 
 * Creation Date: 14/05/2007
 * 
 * @author 	Twinsen Tsang
 * @version	1.0.0
 * @since	H20 01062007 
 */
public class MessageMonitor extends ActiveModule {
	
	// Internal Message DAO object. 
	private MessageDAO msgDAO;
	
	// The flag for initializing monitor related objects.
	private boolean initialized = false;
	
	// The DEFAULT execution interval 
	private final long DEFAULT_EXEC_INTERVAL = 5000;
		
	/**
     * Creates a new instance of MessageMonitor.
     * 
     * @param descriptorLocation the module descriptor.
     * @param loader the class loader for this module.
     * @param shouldInitialize true if the module should be initialized.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
	public MessageMonitor(String descriptorLocation, ClassLoader loader, boolean shouldInitialize) {
		super(descriptorLocation, loader, shouldInitialize);
	}
	
	/**
	 * Invoke for initialization.
	 */
	public void init() {
		super.init();
		this.setExecutionInterval(DEFAULT_EXEC_INTERVAL);
	}

	/**
	 * Post/Lazy initialization. This method is invoked at the firs time only
	 * this module execute.<br/><br/>
	 * 
	 * The purpose of this can guarantee the DAO Factory has been initialized 
	 * successfully before creating it.
	 */
	public void initialize(){
		try{
			msgDAO = (MessageDAO) EbmsProcessor.core.dao.createDAO(MessageDAO.class);
			this.initialized = true;
		}catch(DAOException daoe){
			EbmsProcessor.core.log.fatal("Unable to intialize 'MessageDAO' object.");
		}
	}

	/**
	 * The method is invoked constantly with interval defined in the configuration
	 * descriptor or 60 second by default. It update the status of all timed-out message 
	 * to PENDING so that they can be re-sending by Outbox Collector.
	 * 
	 * @see hk.hku.cecid.ebms.spa.task.OutboxCollector 
	 * @see hk.hku.cecid.ebms.spa.task.OutboxTask
	 * 
     * @return true if this method should be invoked again after a defined interval.
     */
	public boolean execute() {
		// Lazy initialization. 
		if (!this.initialized)
			 this.initialize();
		
		try{ 
			int numberOfTimedoutMessage = msgDAO.updateTimedOutMessageStatus(
				MessageClassifier.INTERNAL_STATUS_PENDING, new Date());

			if (numberOfTimedoutMessage > 0)
				EbmsProcessor.core.log.info ( numberOfTimedoutMessage + " message(s) has been marked re-sending.");
			
		}catch(DAOException daoe){
			EbmsProcessor.core.log.fatal("Unable to mark re-send for timed-out message.", daoe);
		}		
		return true;
	}
}
