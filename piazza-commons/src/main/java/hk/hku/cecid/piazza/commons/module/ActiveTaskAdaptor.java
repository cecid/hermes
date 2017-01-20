package hk.hku.cecid.piazza.commons.module;

/**
 * A Active Task Adaptor is a dummy class for 
 * the interface active task.<br><br>
 * 
 * Creation Date: 24/10/2006.<br><br>
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.1
 * 
 * @see	hk.hku.cecid.piazza.commons.module.ActiveTask
 */
public class ActiveTaskAdaptor implements ActiveTask {

	public void execute() throws Exception {		
	}

	public int getMaxRetries(){	
		return -1;
	}

	public long getRetryInterval() {
		return -1;
	}

	public boolean isRetryEnabled() {
		return false;
	}

	public boolean isSucceedFast() {
		return false;
	}

	public void onAwake() {
	}

	public void onFailure(Throwable e){
	}

	public void setRetried(int retried){
	}
}
