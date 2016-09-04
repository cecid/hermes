package hk.hku.cecid.edi.sfrm.com;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

import java.nio.channels.ReadableByteChannel;

import java.util.StringTokenizer;
import java.util.List;
import java.util.Vector;

import hk.hku.cecid.edi.sfrm.util.PathHelper;
import hk.hku.cecid.piazza.commons.io.NIOHandler;

/** 
 * A Named payloads is a kind of payload (file attachment) that
 * use it's filename to provide some informations for the system.<br><br>
 * 
 * It is a proxy design pattern that control the behavior of the 
 * actual files. 
 * 
 * In SFRM plugin, the default style of named payload is shown on the below:<br>
 * <ul>
 * 	<li>  Outgoing Payload Repository - &lt;service&gt;$&lt;message_id&gt;$timestamp&gt; </li>
 * 	<li>  Packaged Payload Repository - &lt;service&gt;$&lt;message_id&gt; </li> 
 * 	<li>  Incoming Payload Repository - &lt;service&gt;$&lt;message_id&gt; </li>
 * </ul>	  
 * 
 * Creation Date: 6/10/2006<br>.
 * 
 * @author Twinsen Tsang
 * @version 1.0.2
 * @since	1.0.0
 * 
 * @version 2.0.0
 * @since	1.0.2 
 */
public abstract class NamedPayloads {

	/**
	 * The delimitier used for decoding.
	 */
	protected static final String decodeDelimiters = "$";
	
	/**
	 * The prefix of uploading payload.
	 */
	protected static final String uploadingPrefix  = "~";
	
	/**
	 * The prefix of processing payload.
	 */
	protected static final String processingPrefix = "##";
	
	/**
	 * The prefix of uploading payload.
	 */
	protected static final String processedPrefix  = "%%";
	
	/**
	 * The start bracket to enclose the filename of the single file, not packed payload
	 */
	protected static final String filenameStartBracket = "[";
	
	/**
	 * The end bracket to enclose the filename of the single file, not packed payload
	 */
	protected static final String filenameEndBracket = "]";
	
	/**
	 * The state to prefix hash map 
	 */
	private static String[] stateToPrefix = new String[4]; 
	
	/**
	 * The root of payloads.
	 */
	private File root;		
	
	/**
	 * The orginal root name of the payloads
	 */
	private String originalRootName;
	
	/**
	 * The extension of the payload.
	 */
	private String extension;
	
	/**
	 * The content type of the payload.
	 */
	private String contentType;
	
	/**
	 * The name token of this payload.
	 */
	private List tokens = null;
	
	/**
	 * The owner of this payload.
	 */
	private PayloadsRepository owner;
		
	static{
		stateToPrefix[PayloadsState.PLS_UPLOADING]  = uploadingPrefix;
		stateToPrefix[PayloadsState.PLS_PROCESSING] = processingPrefix;
		stateToPrefix[PayloadsState.PLS_PROCESSED]  = processedPrefix;
		stateToPrefix[PayloadsState.PLS_PENDING]	= "";
	}

	/**
	 * Explicit Constructor.<br><br> 
	 * 
	 * This constructor is mainly used  for creating 
	 * a new payload proxy including the physical 
	 * file and the proxy object.<br><br>
	 * 
	 * <strong>NOTE:</strong>
	 * The physical file is not created until it is 
	 * necessary. 
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
	 *  
	 * @throws NullPointerException
	 * 			if the <code>owner</code> is null
	 * @throws IllegalArgumentException
	 * 			if the <code>payloadsName</code> is null or 
	 * 			the <code>initialState</code> is not invalid.  
	 * 
	 * @see hk.hku.cecid.edi.sfrm.com.PayloadsRepostory 
	 * @see hk.hku.cecid.edi.sfrm.com.PayloadsState
	 */
	public NamedPayloads(String payloadsName, int initialState,
			PayloadsRepository owner) throws IOException {
		if (owner == null)
			throw new NullPointerException("Missing repository owner.");		
		if (payloadsName == null || payloadsName.equalsIgnoreCase(""))
			throw new IllegalArgumentException("Invalid Payload Name.");				
		// Setup general Stuff.
		this.extension 	= PathHelper.getExtension(payloadsName);
		this.owner 		= owner;
		this.originalRootName = payloadsName;
		
		payloadsName = getStateForm(initialState) + payloadsName;
		// Create the acutal root.
		this.root = new File(owner.getRepositoryPath(), payloadsName);
	}
	
	/** 
	 * Explicit Constructor.<br><br>
	 * 
	 * The constructor is mainly used for creating 
	 * the new payload proxy object only.
	 * 
	 * @param payloads
	 * 			The file payload object.
	 * @param owner 
	 * 			The owner of the payloads.
	 * @see hk.hku.cecid.edi.sfrm.PayloadsRepostory
	 */
	public NamedPayloads(File payloads, PayloadsRepository owner){
		this.root 		= payloads;		
		this.extension 	= PathHelper.getExtension(payloads.getName());
		this.owner 		= owner;
		this.originalRootName = this.parseOriginalFilename();	
	}		
	
	/**
	 * Rename the root to the specified name. (The file path
	 * has not been changed after each invocation.
	 * 
	 * @param newName
	 * 			The name of the root. 
	 * @return true if the operation run successfully.
	 * @throws IOException 
	 * 			if any kinds of I/O Exception.
	 */
	public boolean renameRoot(String newName) throws IOException {
		if (this.root.getName().equalsIgnoreCase(newName))
			return true;				
		File ret = PathHelper.renameTo(this.root, newName);
		if (ret != null){
			this.root = ret;
			return true;
		}
		return false;					
	}
	
	/**
	 * Move the root to the specified path.
	 * 
	 * @param newPath
	 * 			The absolute new path 
	 * @param force 
	 * 			force to move the file to specified path if 
	 * 			there is a file with same name already exist 
	 * 			in that path.
	 * @return 
	 * 			true if the moving operation successfully.
	 * @since	
	 * 			1.0.0 
	 */
	public boolean moveRoot(String newPath, boolean force){
		if (this.root.getAbsolutePath().equalsIgnoreCase(newPath))
			return true;
		File target = new File(newPath);
		if (force)
			if (target.exists())
				if (!target.delete())
					return false;						
		if (!this.root.renameTo(target)){
			target = null; // for gc
			return false;
		}
		this.root = target;
		return true;
	}
	
	/**
	 * Move the root to another payloads repository.<br><br>
	 * 
	 * The owner of this payloads will changes to 
	 * <code>newOwner</code> after invocation of 
	 * this method.
	 * 
	 * @param newOwner
	 * 			The new owner of the payload repository.
	 * @return 
	 * 			true if the moving operation successfully, 
	 * 			false if the <code>newOwner</code> is null 
	 * 			or moving operation fail.
	 * @since	
	 * 			1.0.2 			
	 */
	public boolean moveToRepository(PayloadsRepository newOwner){
		if (newOwner == null)
			return false;
		if (this.moveRoot(newOwner.getRepositoryPath()
						 +File.separator
						 +this.originalRootName, false)){
			this.owner = newOwner; 
			return true;
		}
		return false;
	}
	
	/**
	 * Move the root to another payloads repository
	 * force.
	 * 
	 * The owner of this payloads will changes to 
	 * <code>newOwner</code> after invocation of 
	 * this method.
	 * 
	 * @param newOwner
	 * 			The new owner of the payload repository.
	 * @return 
	 * 			true if the moving operation successfully, 
	 * 			false if the <code>newOwner</code> is null 
	 * 			or moving operation fail.
	 * @since	
	 * 			1.0.3 			
	 */
	public boolean moveToRepositoryForce(PayloadsRepository newOwner){
		if (newOwner == null)
			return false;
		if (this.moveRoot(newOwner.getRepositoryPath()
						 +File.separator
						 +this.originalRootName, true)){
			this.owner = newOwner; 
			return true;
		}
		return false;
	}
	
	/**
	 * Set the status of payload to pending.
	 * 
	 * @return true if the operation run sucessfully. 
	 */
	public boolean setToPending() throws IOException{
		return this.renameRoot(this.originalRootName);
	}
	
	/**
	 * Set the status of payload to uploading.
	 *
	 * @return true if the operation run sucessfully. 
	 */
	public boolean setToUploading() throws IOException{
		return this.renameRoot(NamedPayloads.uploadingPrefix + this.originalRootName);
	}
	
	/**
	 * Set the status of payload to processing.
	 * 
	 * @return true if the operation run sucessfully. 
	 */
	public boolean setToProcessing() throws IOException{
		return this.renameRoot(NamedPayloads.processingPrefix + this.originalRootName);
	}
	
	/**
	 * Set the status of payload to processed.
	 * 
	 * @return true if the operation run sucessfully.
	 */
	public boolean setToProcessed() throws IOException{
		return this.renameRoot(NamedPayloads.processedPrefix + this.originalRootName);			
	}
			
	/** 
	 * @return true if the payload's name is startting with processing prefix.
	 */ 
	public boolean isUploading(){
		return this.root.getName().startsWith(NamedPayloads.uploadingPrefix);
	}
	
	/**
	 * @return true if the payload's name is startting with processing prefix.
	 */
	public boolean isProcessing(){
		return this.root.getName().startsWith(NamedPayloads.processingPrefix);
	}
	
	/** 
	 * @return true if the payload's name is starting with processed prefix.
	 */
	public boolean isProcessed(){
		return this.root.getName().startsWith(NamedPayloads.processedPrefix);
	}

	/**
	 * @return the directory of this payload set.
	 */
	public File getRoot() {
		return this.root;
	}			
	
	/**
	 * @return the original file name of the payload root.
	 */
	public String getOriginalRootname(){
		return this.originalRootName;
	}
		
	/**
	 * @return a list of token under the decode delimiters.
	 */
	protected List getTokens(){
		if (this.tokens == null){
			this.tokens = new Vector();
			// Bug Fix: Directory 
			String name = this.root.isDirectory() ? 
					this.originalRootName :
					PathHelper.removeExtension(this.originalRootName);
			StringTokenizer st = new StringTokenizer(name,
					NamedPayloads.decodeDelimiters);
			while(st.hasMoreElements())
				this.tokens.add(st.nextElement());	
		}				
		return this.tokens;
	}
	
	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}
	
	/**
	 * @return the contentType
	 */
	public String getContentType() {
		// TODO: Use back the original content type.
		return "application/octet-stream";
	}
	
	/** 
	 * @return get the owner of this payload.
	 */
	public PayloadsRepository getOwner() {
		return owner;
	}	
	
	/**
	 * @return get the size of the payload, return 0 if the 
	 *  	   payload does not exist.
	 */
	public long getSize(){
		if (this.root != null)
			try{
				FileInputStream fis = new FileInputStream(this.root);
				long size = fis.getChannel().size();
				fis.close();
				return size;
			}catch(IOException ioe){
				return 0;
			}
		return 0;
	}
	
	/**
	 * Get the state form string according to the 
	 * specified state.<br><br>
	 * 
	 * @param state
	 * 			The state you want to retrieve. 
	 * @return
	 * @since		
	 * 			1.0.2
	 * @throws IllegalArgumentException
	 * 			if the state is invalid. For all state
	 * 			see PayloadsState.
	 */
	public static String getStateForm(int state){
		if (state > stateToPrefix.length)
			throw new IllegalArgumentException("Invalid State.");
		return stateToPrefix[state];
	}

	/**
	 * Parse the payload filename and get back the original filename from inside.
	 *  
	 * @return the original file name.  		
	 */	
	private String parseOriginalFilename(){ 	
		if (this.isUploading())
			return this.root.getName().substring(NamedPayloads.uploadingPrefix.length());
		else if (this.isProcessing())
			return this.root.getName().substring(NamedPayloads.processingPrefix.length());
		else if (this.isProcessed())
			return this.root.getName().substring(NamedPayloads.processedPrefix.length());
		return this.root.getName();
	}
	
	/**
	 * Clear the tokens to free some memory.
	 */
	public void clearTokens(){		
		this.tokens.clear();
		this.tokens = null;
	}
	
	/**
	 * Clear / Delete the payload cache to free some space.
	 */
	public void clearPayloadCache(){
		if (this.root != null){
			if (!this.root.delete())
				this.root.deleteOnExit();
			this.root = null; // for gc
		}		
	}	 
	
	/**
	 * Load the payload content from the input stream.<br><br>
	 * 
	 * NOTE: This method returns a new instance of input stream.
	 * 
	 * @return the content input stream.
	 * @throws IOException
	 * 			Throws if the payload file does not exist.
	 */
	public InputStream load() throws IOException {
        return new BufferedInputStream(new FileInputStream(this.root));
    }		
	
	/**
	 * Load the payload content from the input stream channel.<br><br>
	 * 
	 * @return the content input channel.
	 * @throws IOException
	 * 			Throws if the payload file does not exist.
	 */
	public ReadableByteChannel loadChannel() throws IOException {
		return new FileInputStream(this.root).getChannel();
	}
			
	/**
	 * Save the content from the input stream to this payloads.<br><br>
	 * 
	 * If the content stream is null, it save the file with empty content.<br>
	 * 
	 * @param content
	 * 			The input content stream. 
	 * @param append 
	 * 			true if the new content is added to the existing content,
	 * 			false if the new content overwrite the existing.
	 */
	public void save(InputStream content, boolean append) throws IOException {
		// Create buffered ouput stream.
		OutputStream outs = new BufferedOutputStream(
				new FileOutputStream(this.root, append));
		if (content != null){			
			if (content instanceof FileInputStream)
				// Performance gain a lot if using FileInputSteam.
				NIOHandler.pipe((FileInputStream)content, outs);
			else
				NIOHandler.pipe(content, outs);
		}
		outs.close();
		outs = null;	// For gc
    } 
	
	/**
	 * Decode the payload root to become some useful information.
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 * 			if the decoding fails due to the filename is in wrong format. 
	 */
	protected abstract void decode() throws ArrayIndexOutOfBoundsException;
	
	/**
	 * Encode the payload root back to a filename.
	 */
	protected abstract void encode();
			
	/**
	 * toString method
	 */
	public String 
	toString()
	{
		StringBuffer ret = new StringBuffer();
		ret	.append("\n")
			.append(this.getClass().getName() + "\n")
			.append("Payload Root: " + this.originalRootName + " \n")
			.append("Extension   : " + this.extension	  	 + " \n")
			.append("Content-type: " + this.contentType	  	 + " \n")
			.append("Owner       : " + this.owner.getId()	 + " \n");
		return ret.toString();
	}
}
