package hk.hku.cecid.edi.sfrm.com;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Iterator;
import java.util.Collection;
import hk.hku.cecid.piazza.commons.io.FileSystem;

/** 
 * A folders payload represent a folder hierarchical with 
 * the set of payloads.<br>  
 * 
 * Creation Date: 5/10/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.1
 * @since	1.0.0
 */
public class FoldersPayload extends NamedPayloads{
	
	/**
	 * The partnershipId provider used by this payloads.
	 */
	private String partnershipId;
	
	/**
	 * The messageId used by this payloads.
	 */
	private String messageId;
	
	/**
	 * The total size of all payloads within the folders.
	 */
	private long totalSize = -1;
	
	/**
	 * The number of files within the folders.
	 */
	private int	 numOfFiles = -1;
				
	/** 
	 * Protected Explicit Constructor.
	 * 
	 * This constructor is mainly used for creating 
	 * a new payload proxy including the physical 
	 * file and the proxy object.
	 * 
	 * @param payloadsName
	 * 			The name of the newly created payload.
	 * @param initialState 
	 * 			The initialState of the payloads, 
	 * 			see {@link PayloadsState} for details.
	 * @param owner 
	 * 			The owner of the payloads. 
	 * @since	
	 * 			1.0.2
	 * @throws Exception
	 * 			Any kind of exceptions.
	 */
	protected
	FoldersPayload(
			String 				payloadsName,
			int    				initialState,
			PayloadsRepository 	owner) throws
			IOException
	{
		super(payloadsName, initialState, owner);
		this.decode();
	}
	
	/** 
	 * Protected Explicit Constructor.
	 * 
	 * @param payloads
	 * 			The payloads directory.
	 * @param owner
	 * 			The owner of this payload.
	 * @since	
	 * 			1.0.0
	 * @throws IOException
	 * 			If the payload is not directory.  
	 */
	protected 
	FoldersPayload(
			File payloads, 
			PayloadsRepository owner) throws 
			IOException 
	{
		super(payloads, owner);
		if (!payloads.isDirectory())
			throw new IOException("Payloads is not a folder.");
		this.decode();
	}
	
	/** 
	 * @return the partnership id of the payloads.
	 */
	public String 
	getPartnershipId()
	{
		return partnershipId;
	}
	
	/**
	 * @return the message of the payloads.
	 */
	public String 
	getMessageId()
	{
		return this.messageId;
	}

	/**
	 * @return the total size within the folders.
	 */
	public long 
	getSize() 
	{
		if (this.totalSize == -1){
			this.totalSize = 0;
			Collection c = new FileSystem(this.getRoot())
				.getFiles(true);
			Iterator itr = c.iterator();
			FileChannel fc;
			while(itr.hasNext()){
				try{
					fc = new FileInputStream
						((File)itr.next()).getChannel();
					this.totalSize +=  fc.size();
					fc.close();
				}
				catch(IOException e){
					// Continue.
				}
			}
			this.numOfFiles = c.size(); 
		}
		return this.totalSize;
	}
	
	/**
	 * @return the number of files within the folders. 
	 */
	public int 
	getNumOfFiles()
	{		
		if (this.numOfFiles == -1){
			this.numOfFiles = new FileSystem(this.getRoot()).getFiles(true).size();
		}
		return this.numOfFiles;
	}
	
	/**
	 * Clear all the content and the folder for this payload.
	 */			
	public void 
	clearPayloadCache() 
	{
		FileSystem fs = new FileSystem(this.getRoot());
		fs.purge();
	}

	/**
	 * The outgoing payload does not support <code>load</code> method.
	 */
	public InputStream 
	load() throws IOException 
	{
		throw new IOException("Unable to load content from directory.");
	}

	/**
	 * The outgoing payload does not support <code>loadChannel</code> method.
	 */
	public ReadableByteChannel 
	loadChannel() throws IOException 
	{
		throw new IOException("Unable to load channel from directory.");
	}

	/**
	 * The outgoing payload does not support <code>save</code> method.  
	 */
	public void 
	save(InputStream content, 
		 boolean 	 append) throws 
		 IOException 
	{
		throw new IOException("Unable to write content to directory.");
	}

	/**
	 * Decode the payload root to become some useful information.<br><br>
	 * 
	 * Only the partnershipId (the first token) is assigned.
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 * 			if the decoding fails due to the filename is in wrong format.   
	 */
	protected void 
	decode() throws ArrayIndexOutOfBoundsException
	{
		if (this.getTokens().size() < 2)
			throw new ArrayIndexOutOfBoundsException(
					"Invalid Folders Payload Format.");
		this.partnershipId = (String) this.getTokens().get(0);
		this.messageId = (String) this.getTokens().get(1);
	}

	/**
	 * 
	 */
	protected void 
	encode() 
	{
		// TODO: Encode Folders Payload
	}
	
	/**
	 * toString method
	 */
	public String 
	toString()
	{
		StringBuffer ret = new StringBuffer(super.toString());
		ret .append("PartnershipId:" + this.partnershipId + " \n")
			.append("MessageId    :" + this.messageId	  + " \n");
		return ret.toString();
	}
}