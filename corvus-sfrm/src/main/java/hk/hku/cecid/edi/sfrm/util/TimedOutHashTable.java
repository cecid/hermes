package hk.hku.cecid.edi.sfrm.util;


import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Enumeration;

/** 
 * The <code>TimedOutDHashTable</code> is the hashtable that provides time out features. 
 * All timed-out key and value will be cleared in the hashtable. The default interval for 
 * sweeping away all timed out record is 5 second.<br/><br/>
 * 
 * For the release of JDK5.0 , this class can inherit java.util.conncurrent.ConcurrentHashMap 
 * for better performance.<br/><br/>
 * 
 * Creation Date: 25/6/2007<br/><br/>
 * 
 * 17/7/2007<br/><br/>
 * 1. Bug Fixed that return false for invoking {@link #containsValue(Object)} because
 * of invalid implementation of {@link TimedOutEntry#equals(Object)}. <br/>
 * 2. Bug Fixed that the memory leak issue when de-referecing timed-out hashtable due 
 * to there is one reference in the internal TimedOutTask. Now you are required to
 * call {@link #complete()} when you want to gc the hashtable. 
 *   
 * @author 	Twinsen Tsang
 * @version 1.0.1
 * @since	Dwarf 10606  
 */
public class TimedOutHashTable extends Hashtable {
	
	// The serialization version ID.
	private static final long serialVersionUID = 8216590631154991663L;
	
	// The execution interval for sweeping the time out record in the hashtable.
	private long  sweepInterval = 5000;	
	
	// The internal timer for monitoring timed out record.
	private Timer monitor		= new Timer(true);
	
	// The callback listener for receiving removal event of timed-out entry.  
	private TimedOutEntryListener listener;
	
	/** The TimedOutEntry is the single data record for TimedOutHashTable containing time-out information **/
	private class TimedOutEntry
	{
		private Object obj;		
		private Date expiredDate;
				
		/** 
		 * Explict Constructor. 
		 * 
		 * @param obj The object to store.
		 * @param expiredDate The expired date for the <code>obj</code>
		 */
		protected TimedOutEntry(Object obj, Date expiredDate){
			this.obj = obj;
			this.expiredDate = expiredDate;
		}
		
		/** equals */
		public boolean equals(Object obj) {
			if (obj instanceof TimedOutEntry){
				TimedOutEntry t = (TimedOutEntry) obj;
				return (this.obj.hashCode() == t.obj.hashCode());
			}
			return false;
		}
		
		/** hashCode */
		public int hashCode(){
			return obj.hashCode();
		}
		
		/** toString method */
		public String toString(){
			return this.obj.toString() + " expire:" + this.expiredDate.toString(); 
		}
	}
	
	/** The TimerTask for sweeping out timed-out record **/
	private class TimedOutTask extends TimerTask
	{
		public void run() {			
			int len = TimedOutHashTable.this.size();
			if (len == 0) return;
			
			Object key 		  = null;
			TimedOutEntry tmp = null;				 
			Enumeration e = TimedOutHashTable.this.keys();
			while (e.hasMoreElements()){
				key = e.nextElement();
				tmp = (TimedOutEntry) TimedOutHashTable.super.get(key);
				if (tmp.expiredDate != null){
					if (System.currentTimeMillis() > tmp.expiredDate.getTime()){
						// Invoke the callback listener.
						if (listener != null) listener.timeOut(key, tmp.obj);
						// Remove the key.
						TimedOutHashTable.this.remove(key);						
					}
				}
			}			
		}

		/*protected void finalize() throws Throwable {
			super.finalize();
			System.out.println("TimedOut Task is destoryed.");
		}*/				
	}
	
	/** 
	 * Constructor. 
	 */
	public TimedOutHashTable(){
		this(5000);		
	}
	
	/** 
	 * Explicit Constructor.
	 * 
	 * @param sweepInterval The sweepInterval for sweeping away the timed-out hash-record.
	 */
	public TimedOutHashTable(long sweepInterval){
		super();
		this.sweepInterval 	= sweepInterval;	
		
		// Schedule a timer task for sweeping out the timed out record.
		this.monitor.scheduleAtFixedRate(new TimedOutTask(), this.sweepInterval, this.sweepInterval);
	}
		
	// @see java.util.Hashtable#contains(java.lang.Object)
	public boolean contains(Object value) {
		return super.contains(new TimedOutEntry(value, null));
	}

	// @see java.util.Hashtable#containsValue(java.lang.Object)
	public boolean containsValue(Object value) {
		return super.containsValue(new TimedOutEntry(value, null));
	}

	// @see java.util.Hashtable#get(java.lang.Object)
	public Object get(Object key) {
		Object obj = super.get(key);
		if (obj != null)
			return ((TimedOutEntry) obj).obj;
		return null;
	}
	
	// @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
	public Object put(Object key, Object value) {
		return super.put(key, new TimedOutEntry(value, null));
	}
	
	/**
	 * Maps the specified key to the specified value in this hashtable. Neither the key nor the value can be null.
	 * The value can be retrieved by calling the get method with a key that is equal to the original key.
	 *  
	 * @param key the hashtable key.
	 * @param value the value
	 * @param timeOutInMS how long does this record time out.
	 * @return the previous value of the specified key in this hashtable, or null if it did not have one.
	 */
	public Object put(Object key, Object value, long timeOutInMs){
		return super.put(key, new TimedOutEntry(value, new Date(System.currentTimeMillis() + timeOutInMs)));		
	}
	
	/**
	 * Maps the specified key to the specified value in this hashtable. Neither the key nor the value can be null.
	 * The value can be retrieved by calling the get method with a key that is equal to the original key.
	 *  
	 * @param key the hashtable key.
	 * @param value the value
	 * @param timeOutDate how long does this record time out in date object.
	 * @return the previous value of the specified key in this hashtable, or null if it did not have one.
	 */
	public Object put(Object key, Object value, Date timeOutDate){
		return super.put(key, new TimedOutEntry(value, timeOutDate));
	}
	
	/**  
	 * @param listener The listener for receiving removal event of timed-out entry.  
	 * 
	 * @see hk.hku.cecid.piazza.commons.util.TimedOutEntryListener.
	 */
	public void setListener(TimedOutEntryListener listener){
		this.listener = listener;
	}
	
	/** 
	 * @return the sweeping interval for the hash table.
	 */
	public long getSweepInterval(){
		return this.sweepInterval;
	}
	
	/**
	 * Invoke this method when you no longer want to use the hashtable anymore. 
	 * (it will be gc soon).  
	 */
	public void complete(){
		this.monitor.cancel();
		this.monitor = null;
	}	
	
	// For DEBUG Purpose only.
	/*protected void finalize() throws Throwable {
		super.finalize();
		System.out.println("TimedOutHashTable is destoryed.");
	}			*/
}
