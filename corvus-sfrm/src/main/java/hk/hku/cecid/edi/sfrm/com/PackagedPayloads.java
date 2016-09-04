package hk.hku.cecid.edi.sfrm.com;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//import hk.hku.cecid.piazza.commons.io.Archiver;
import hk.hku.cecid.edi.sfrm.com.PayloadException;

/**
 * A packaged payloads represent a archive file typed payloads.<br><br>
 * 
 * Creation Date: 6/10/2006
 * 
 * @author Twinsen Tsang
 * @version 1.0.2
 * @since	1.0.0
 * 
 * @version 2.0.0
 * @since	1.0.2
 */
public class PackagedPayloads extends NamedPayloads{

	/**
	 * The partnership id used by this payloads.
	 */
	private String partnershipId;

	/**
	 * The message id that this payload refer to.
	 */
	private String refMessageId;
		
	/**
	 * Indicate whether the payload is packed in tar format
	 */
	private boolean isPacked;
	
	/**
	 * Filename of the payload, (if the payload is not packed)
	 */
	private String filename;
	
	/** 
	 * Protected Explicit Constructor.
	 * 
	 * This constructor is mainly used for creating 
	 * a new payload proxy including the physical 
	 * file and the proxy object.
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
	 * @throws Exception
	 * 			Any kind of exceptions.
	 */
	protected 
	PackagedPayloads(String payloadsName, int initialState, 
			PayloadsRepository owner) throws Exception
	{
		super(payloadsName, initialState, owner);
		this.decode();
		this.clearTokens();
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
	PackagedPayloads(File payload, PayloadsRepository owner) throws IOException{
		super(payload, owner);
		if (payload.exists() && !payload.isFile())
			throw new IOException("Payloads is not a file.");
		this.decode();
		this.clearTokens();
	}		
		
	/**
	 * @return the reference to message id.
	 */
	public String 
	getRefMessageId()
	{
		return this.refMessageId;
	}
	
	/**
	 * @return the partnership id.
	 */
	public String 
	getPartnershipId()
	{
		return this.partnershipId;
	}
	
	/**
	 * Get whether the payload is packed in tar format
	 * 
	 * @return true for packed, false otherwise
	 */
	public boolean isPacked(){
		return this.isPacked;
	}
	
	/**
	 * Get the filename of the payload, if it is not packed in tar format
	 * 
	 * @return filename of the payload
	 */
	public String getFilename(){
		return this.filename;
	}
	
	/**
	 * Save the content from the input stream to this payloads.<br><br>
	 * 
	 * If the content stream is null, it save the file with empty content.<br><br>
	 *
	 * This method is rarely used in this class because it's semantics
	 * here is to copy the bytes from the inputstream to 
	 * the package payload. 
	 * 
	 * @param content
	 * 			The input content stream. 
	 * @param append 
	 * 			true if the new content is added to the existing content,
	 * 			false if the new content overwrite the existing.
	 */
	public void 
	save(InputStream content, 
		 boolean 	 append) throws IOException 
	{
		super.save(content, append);
	}
				
	/**
	 * Decode the payload root to become some useful information.
	 */
	protected void 
	decode()
	{		
		List tokens = this.getTokens();		
		if (this.getTokens().size() < 2)
			throw new ArrayIndexOutOfBoundsException(
					"Invalid Packaged Payloads Format.");		
		this.partnershipId = (String) tokens.get(0);

		//Added for decoding the packaged payload in the format of partenrship_id$message_id[original_filename].sfrm
		String suffixToken = (String) tokens.get(1);
		decodeMessageId(suffixToken);
		try {
			decodeSingleFile(suffixToken);
		} catch (PayloadException e) {
			// TODO Implement the payload exception related action
			e.printStackTrace();
		}
	}
	
	private void decodeMessageId(String token){
		int startIndex = token.indexOf(filenameStartBracket);
		if(startIndex == -1){
			this.refMessageId = token;
		}else{
			this.refMessageId = token.substring(0, startIndex);
		}
	}
	
	private void decodeSingleFile(String token) throws PayloadException{
		int startIndex = token.indexOf(filenameStartBracket);
		int endIndex = token.lastIndexOf(filenameEndBracket);
		
		//If it didn't have filename enclosing
		if(startIndex == -1 || endIndex == -1){
			isPacked = true;
			filename = "";
		}else if(startIndex >= 0 && endIndex >= 0 && endIndex > startIndex){
			isPacked = false;
			filename = token.substring(startIndex + 1, endIndex);
		}else{
			throw new PayloadException("Invalid packaged payload format");
		}
	}
	
	/**
	 * 
	 */
	protected void 
	encode() 
	{
		// TODO: Encode Packaged Payloads
	}
	
	/**
	 * To create a FoldersPayload object for this payload, create the folder in the file system when needed 
	 * @param repo owner repository of this folder payload
	 * @param state state of the payload folder @see PayloadsState
	 * @param isCreateFolder whether to create the specific folder in the file system
	 * @return FoldersPayload object
	 * @throws IOException if isCreateFolder is true and cannot create folder successfully
	 * @throws PayloadException if isCreateFolder is true and the folder already existing
	 */
	public FoldersPayload getFoldersPayload(PayloadsRepository repo, int state, boolean isCreateFolder) throws IOException, PayloadException{
		String name = this.partnershipId + NamedPayloads.decodeDelimiters + this.refMessageId;
		FoldersPayload folderPayload = new FoldersPayload(name, PayloadsState.PLS_UPLOADING, repo);
		
		if(isCreateFolder == true && folderPayload.getRoot().exists() == true){
			throw new PayloadException("Payload Folder \"" + folderPayload.getRoot().getCanonicalPath() + "\" already existed");
		}
		//Create the payload folder in file system if needed
		if(isCreateFolder == true){
			if(folderPayload.getRoot().mkdirs() == false){
				throw new IOException("Fail on creating the Payload Folder \"" + folderPayload.getRoot().getCanonicalPath() + "\"");
			}
		}
		return folderPayload;
	}
	
	/**
	 * toString method
	 */
	public String 
	toString()
	{
		StringBuffer ret = new StringBuffer(super.toString());
		ret .append("Service     : " + this.partnershipId + " \n")
			.append("RefMessageId: " + this.refMessageId +  " \n"); 
		return ret.toString();
	}
}
