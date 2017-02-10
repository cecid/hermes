package hk.hku.cecid.edi.sfrm.archive;

import hk.hku.cecid.piazza.commons.io.FileSystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;

/**
 * An <strong>ArchiverNULL</strong> is a null packaging device 
 * that provides some useful method for the  sub-class which 
 * enhance code design and ease to read.
 * <ul>
 * 	<li>Pre-error Handling of compress,list and extract</li>
 * </ul>	  
 * 
 * Creation Date: 13/11/2006<br><br>
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.2
 */
public abstract class ArchiverNULL implements Archiver {
			
	/**
	 * The NULL archiver validates all fields 
	 * in the parameters and return false in the end.
	 * 
	 * @param src
	 * 			The source of the file(s) to be archive. 
	 * @param dest 			
	 * 			The destination of the arhived file.
	 * @param includeItself
	 * 			the source directory includes in the archive if it is
	 * 			true, vice versa.
 	 * @since	
	 * 			1.0.2   
	 * @throws IOException
	 * 			if any kind of I/O Erros
	 * @return true if the operations run successfully.
	 * @see	hk.hku.cecid.edi.sfrm.archive.Archiver#compress(File, File, boolean)
	 */
	public boolean compress(File src, File dest, boolean includeItself)
			throws IOException{
		// Error Handling
		if (src == null || dest == null)
			throw new NullPointerException(
					"Source or destination file is empty.");
		if (dest.isDirectory())
			throw new IllegalArgumentException(
					"Destination archive is not a file.");
		return false;
	}
	
	/**
	 * List out all files that need to be archive in 
	 * a {@link #compress(File, File, boolean)} call.<br><br>
	 * 
	 * <strong>NOTE</strong>: 
	 * This method should only be used inside the 
	 * <code>compress</code> method.
	 * 
	 * @param src 
	 * 			The source of the file(s) to be archive.
 	 * @since	
	 * 			1.0.2     
	 * @return    
	 * 			An iterator whichs contains a list of files object
	 * 			to archive.
	 */
	protected Iterator listFilesToArchive(File src){
		Iterator ret;
		// Handle single and multiple files case.
		if (src.isFile()){
			ArrayList tmp = new ArrayList();
			tmp.add(src);
			ret = tmp.iterator();
		} else {
			FileSystem fs = new FileSystem(src);
			ret = fs.getFiles(true).iterator();
			fs = null; // For gc
		}		
		return ret;
	}
	
	protected Iterator listAllToArchive(File src){
		Iterator ret;
		// Handle single and multiple files case.
		if (src.isFile()){
			ArrayList tmp = new ArrayList();
			tmp.add(src);
			ret = tmp.iterator();
		} else {
			FileSystem fs = new FileSystem(src);
			//ret = fs.getFiles(true).iterator();
			Collection files = fs.getFiles(true);
			Collection dirs = fs.getDirectories(true);
			
			files.addAll(dirs);
			ret = files.iterator();
			
			fs = null; // For gc
		}		
		return ret;
	}
	
	/**
	 * Get the base directory for archiving.<br>
	 *  
	 * The base directory is the path that
	 * all archive entry relative to it.<br><br>
	 * 
	 * <strong>NOTE</strong>: 
	 * This method should only be used inside the 
	 * <code>compress</code> method.
	 * 
	 * @since	
	 * 			1.0.2
	 * 
	 * @return the base directory.
	 */
	protected String getBaseArchivingDirectory(File src, boolean includeItself){	
		if ( src.getParent() != null &&
			(src.isDirectory() && includeItself) ||
			(src.isFile()))				
			return src.getParentFile().getAbsolutePath();			
		else  
			return src.getAbsolutePath();
	}
	
	/**
	 * List the files inside the <code>archive</code>. 
	 * 
	 * @param archive
	 * 			The archive to be listed.
 	 * @since	
	 * 			1.0.2    
	 * @return
	 * 			A list of java.io.File object that represents
	 * 			each entry in the archive. 
	 * @throws IOException 
	 * 			if any kind of I/O Errors. 			 	 
	 */
	public List listAsFile(File archive) 
			throws IOException{
		return null;
	}	
	
	/**
	 * List the files inside the <code>archive</code>. 
	 * 
	 * @param archive
	 * 			The archive to be listed.
	 * @return
	 * 			A list of String objects that represents
	 * 			the filename of each entry in the 
	 * 			archive.
 	 * @since	
	 * 			1.0.2     
	 * @throws IOException 
	 * 			Any kind of I/O Errors.
	 */
	public List listAsFilename(File archive) 
			throws IOException{
		return null;
	}
	
	/**
	 * The NULL archiver validates all fields  
	 * in the parameters and return false in the end.<br><br>
	 * 
	 * If the destination directory does not exist, 
	 * the NULL archiver will create one for you.
	 *  
	 * @param archive
	 * 			The archive to be extract.
	 * @param dest
	 * 			The destination directory extract to.
 	 * @since	
	 * 			1.0.2     
	 * @throws IOException
	 * 			Any kind of I/O Errors.
	 * @throws IllegalArgumentException
	 * 			If the <code>dest</code> is not a directory.
	 * @return true if the operations run successfully.	
	 */
	public boolean extract(File archive, File dest) 
			throws IOException{
		if (archive == null | dest == null) {
			throw new NullPointerException(
					"Archive or destination file is empty.");
		}
		if (dest.exists() && !dest.isDirectory()) {
			throw new IllegalArgumentException(
					"Destination file is not directory");
		}
		if (!dest.exists()) {
			dest.mkdirs();
		}         
		return false;
	}			
}
