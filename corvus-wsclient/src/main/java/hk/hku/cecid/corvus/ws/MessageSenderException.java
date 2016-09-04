/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws;

/**
 * The <code>MessageSenderException</code> is a custom exception 
 * for the MessageSender class.
 * 
 * @author 	Joel Matsumoto
 * @version	1.0.0
 * @since  	Elf 0818
 */
public class MessageSenderException extends Exception {

	/**
	 * Default serial UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */	
	public MessageSenderException(){
		super();
	}
	
	/**
	 * Explicit Constructor.
	 * 
	 * @param msg	The exception message.
	 */
	public MessageSenderException(String msg){
		super(msg);
	}

	/**
	 * Explicit Constructor.
	 * 
	 * @param msg	The exception message.
	 * @param e		The root cause.
	 */
	public MessageSenderException(String msg, Exception e){
		super(msg,e);
	}	
}
