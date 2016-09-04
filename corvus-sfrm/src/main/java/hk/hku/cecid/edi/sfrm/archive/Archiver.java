/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.sfrm.archive;

import hk.hku.cecid.piazza.commons.io.FileSystem;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * An Archiver is the interface that provide 
 * the basic archive operations like: <br>
 * <ul>
 * 	<li>Compress operations</li>
 * 	<li>Extract operations</li>
 * 	<li>List operations<li>
 * 	<li></li>
 * </ul>
 * 
 * Creation Date: 13/11/2006<br><br>
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	1.0.2
 */
public interface Archiver{

	/**
	 * Check whether the <code>archive</code> 
	 * is supported by this type of archiver.<br><br>
	 * 
	 * The execution time of this method solely 
	 * depends on how the archiver structure look like.<br><br>
	 * 
	 * For example, 
	 * 	ArchiverZIP return immediately because it checks 
	 * only with the extension of the archive file, without
	 * the actual content.
	 * 
	 * @param archive
	 * 			The archive to be tested.
	 * @since	
	 * 			1.0.2
	 * @return 
	 * 			true if the archiver support this <code>archive</code>.
	 */
	public boolean isSupportArchive(File archive);
	
	/**
	 * Compress the <code>src</code> to <code>dest</code> 
	 * in the archive form.<br><br>
	 *   
	 * If the <code>src</code> is a file, then the resulting
	 * archive contains only that file.<br><br> 
	 * 
	 * If the <code>src</code> is a directory, then the resulting
	 * archive contains all files (recursively) in the <code>
	 * src</code>. If the flag <code>includeItself</code> is true
	 * , then the <code>src</code> will also include in the archive 
	 * as the root.  
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
	 */
	public boolean compress(File src, File dest, boolean includeItself)
			throws IOException;
	
	/**
	 * Compress the <code>src</code> to <code>dest</code> 
	 * in the archive form.<br><br>
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
	 * @return true if the operations run successfully.
	 * @throws IOException
	 * 			if any kind of I/O Errors. 
	 */
	public boolean compress(FileSystem src, File dest)
			throws IOException;
	
	/**
	 * Guess how big is the compressed file without
	 * compressing actually.
	 * 
	 * @param src
	 * 			The source of the file(s) to be archive.
	 * @since	
	 * 			1.0.3
	 * @throws NullPointerException
	 * 			if the <code>src</code> is null.
	 * @throws IOException
	 * 			if one of the file in the folders 
	 * 			does not exist in some reason.
	 */
	public long guessCompressedSize(File src)
			throws IOException;
	
	/**
	 * Guess how big is the compressed file without
	 * compressing actually.
	 * 
	 * @param src
	 * 			The source of the file(s) to be archive.
	 * @since	
	 * 			1.0.3
	 * @throws NullPointerException
	 * 			if the <code>src</code> is null.
	 * @throws IOException
	 * 			if one of the file in the folders 
	 * 			does not exist in some reason.
	 */
	public long guessCompressedSize(FileSystem src)
			throws IOException;
	
	/**
	 * List the files inside the <code>archive</code>. 
	 * 
	 * @param archive
	 * 			The archive to be listed.
	 * @return
	 * 			A list of java.io.File object that represents
	 * 			each entry in the archive. 
	 * @throws IOException 
	 * 			if any kind of I/O Errors.
	 */
	public List listAsFile(File archive) 
			throws IOException;		
	
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
			throws IOException;
	
	/**
	 * Extract the <code>archive</code> to the <code>dest</code> 
	 * directory.<br><br>
	 *  
	 * @param archive
	 * 			The archive to be extract.
	 * @param dest
	 * 			The destination directory extract to. 
	 * @throws IOException
	 * 			Any kind of I/O Errors.
	 * @since	
	 * 			1.0.2 
	 * @throws IllegalArgumentException
	 * 			If the <code>dest</code> is not a directory.
	 * @return true if the operations run successfully.	
	 */
	public boolean extract(File archive, File dest) 
			throws IOException;
	
	/**
	 * Extract the <code>archive</code> to the <code>dest</code> 
	 * directory.<br><br>
	 * 
	 * @param archive
	 * 			The archive to be extract. 
	 * @param dest
	 * 			The destination directory extract to.
	 * @since	
	 * 			1.0.2 
	 * @throws IOException
	 * 			Any kind of I/O Errors.  
	 * @return true if the operations run successfully.
	 */
	public boolean extract(File archive, FileSystem dest) 
			throws IOException;
		
}
