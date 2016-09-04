package hk.hku.cecid.piazza.corvus.core.main.admin.hc.util;

/**
 * Class to handle exceptions for the AdminProperties class.
 * @author Joel Matsumoto
 *
 */
public class AdminPropertiesException extends Exception {
	
	public AdminPropertiesException(){
		super();
	}
	
	public AdminPropertiesException(String msg){
		super(msg);
	}
	
	public AdminPropertiesException(String msg, Exception cause){
		super(msg,cause);
	}
	
}
