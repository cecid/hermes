package hk.hku.cecid.edi.sfrm.archive;

import hk.hku.cecid.edi.sfrm.util.PathHelper;
import hk.hku.cecid.piazza.commons.io.FileSystem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.apache.tools.tar.TarOutputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

/**
 * @author Patrick Yip
 *
 */
public class ArchiverTar extends ArchiverNULL {
	
	public static int TAR_ENTRY_SIZE = 512;
	
	private boolean writeDirEntry(File srcDir, String dirpath, TarOutputStream outs) throws IOException{
		String filepath;
		String entryName;
		TarEntry tarEntry;
		
		filepath 	= srcDir.getAbsolutePath();							
		entryName 	= filepath.substring(dirpath.length() + 1)
				.replace('\\', '/');
		
		if(!entryName.endsWith("/")){
			entryName += "/";
		}
		tarEntry = new TarEntry(entryName);
		outs.putNextEntry(tarEntry);
		outs.closeEntry();
		return true;
	}
	
	
	private boolean writeEntries(File srcFile, String dirpath, TarOutputStream outs, WritableByteChannel tarChannel, boolean includeItself) throws IOException{
		TarEntry tarEntry;
		String filepath;
		String entryName;	
		long size;			// the size of the files.
		long tSize;			// the transfer size for transferTo calls.
		long aSize;			// the actual transfer size for transferTo calls.			 
		long sPos;			// the start position for transferTo calls.
		filepath 	= srcFile.getAbsolutePath();							
		entryName 	= filepath.substring(dirpath.length() + 1)
				.replace('\\', '/');
		// Create zip entry
		tarEntry = new TarEntry(srcFile);
		
		tarEntry.setModTime(srcFile.lastModified());
		// Tricky fix to make the tar entry back
		// to relative path from the source 
		// directory.
		tarEntry.setName(entryName);
		// I/O Piping
		outs.putNextEntry(tarEntry);		
				
		FileChannel fc = new FileInputStream(srcFile).getChannel();
		// NIO Bugs 
		// transferTo can only transfer up to Integer.MAX_VALUE -1;
		size  = fc.size();
		tSize = size;
		sPos  = 0;
		do{					
			tSize = aSize = (size - sPos); 
			if (tSize > Integer.MAX_VALUE)
				aSize = Integer.MAX_VALUE - 1;				
			fc.transferTo(sPos, aSize, tarChannel);			
			sPos += aSize;					
		}
		while(tSize > Integer.MAX_VALUE);								
		outs.closeEntry();									
		fc.close();
		fc = null; // For gc	
		
		return true;
	}
	
	public boolean compress(List<File> src, File dest, boolean includeItself) throws IOException{	
		FileOutputStream fos  = new FileOutputStream(dest);
		TarOutputStream	 outs = new TarOutputStream(fos);
		
		WritableByteChannel tarChannel = Channels.newChannel(outs);	 		
		outs.setLongFileMode(TarOutputStream.LONGFILE_GNU);
		File srcFile;		// the file object to tar.
		
		for(int i=0; src.size() > i;i++){
			File baseSrc = src.get(i);
			super.compress(baseSrc, dest, includeItself);
			Iterator allFiles = this.listAllToArchive(baseSrc);
			String dirpath = this.getBaseArchivingDirectory(baseSrc, includeItself);
			while (allFiles.hasNext()) {
				srcFile = (File) allFiles.next();				
				if(srcFile.isFile())
					writeEntries(srcFile, dirpath, outs, tarChannel, includeItself);
				
				if(srcFile.isDirectory()){
					writeDirEntry(srcFile, dirpath, outs);
				}
			}
		}
		
		outs.close();		
		outs = null;							
		fos.close();
		fos = null;	
		return true;
	}
	
	
	/**
	 * Compress the <code>src</code> to <code>dest</code> 
	 * in the archive form.<br><br>
	 *   
	 * If the <code>src</code> is a file, then the resulting
	 * archive contains only that file.<br><br> 
	 * 
	 * If the <code>src</code> is a directory, then the resulting
	 * archive contains all files (recursively) in the <code>
	 * src</code>.
	 * 
	 * The <code>src</code> file sets will be archived to TAR
	 * format which is comes from Apache Ant Tools Tar.<br><br>
	 * 
	 * For more details, 
	 * read <a href="http://www.jajakarta.org/ant/ant-1.6.1/docs/mix/manual/api/">Apache Ant Tool Tar</a>
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
	
	public boolean compress(File src, File dest, boolean includeItself) throws IOException{
		super.compress(src, dest, includeItself);		
		
		FileOutputStream fos  = new FileOutputStream(dest);
		TarOutputStream	 outs = new TarOutputStream(fos);
		
		WritableByteChannel tarChannel = Channels.newChannel(outs);	 
		Iterator allFiles = this.listFilesToArchive(src);		
		String dirpath = this.getBaseArchivingDirectory(src, includeItself);
		outs.setLongFileMode(TarOutputStream.LONGFILE_GNU);
		File srcFile;		// the file object to tar.
		
		while (allFiles.hasNext()) {
			srcFile = (File) allFiles.next();
			writeEntries(srcFile, dirpath, outs, tarChannel, includeItself);		
		}				
		outs.close();		
		outs = null;							
		fos.close();
		fos = null;	
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see hk.hku.cecid.piazza.commons.io.Archiver#compress(hk.hku.cecid.piazza.commons.io.FileSystem, java.io.File)
	 */
	
	public boolean compress(FileSystem src, File dest) throws IOException {
		return compress(src.getRoot(), dest, true);
	}

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
	 * @throws IllegalArgumentException
	 * 			If the <code>dest</code> is not a directory.
	 * @return true if the operations run successfully.	
	 */
	
	public boolean extract(File archive, File dest) throws IOException{
		super.extract(archive, dest);
		BufferedInputStream bis = new BufferedInputStream(
			new FileInputStream(archive));
		TarInputStream tis = new TarInputStream(bis);
		
		int count = 0;
		for (;; count++) {
			TarEntry entry = tis.getNextEntry();
					
			if (entry == null) {				
				break;
			}			
			
			String name = entry.getName();
			name = name.replace('/', File.separatorChar);
			File destFile = new File(dest, name);
			if (entry.isDirectory()) {
				if (!destFile.exists()) {
					if (!destFile.mkdirs()) {
						throw new IOException(
							"Error making directory path :"
						   + destFile.getPath());
					}
				}
			} else {
				File subDir = new File(destFile.getParent());
				if (!subDir.exists()) {
					if (!subDir.mkdirs()) {
						throw new IOException(
							"Error making directory path :"
						   + subDir.getPath());
					}
				}	
				
				FileOutputStream out = new FileOutputStream(destFile);
				// FIXME: TUNE PLACE
				byte[] rdbuf = new byte[32 * 1024];
				for (;;){
					int numRead = tis.read(rdbuf);
					if (numRead == -1)
						break;						
					out.write(rdbuf, 0, numRead);
				}
				out.close();
			}
		}
		// For gc
		tis.close(); tis = null;
		bis.close(); bis = null;
		// NO FILE EXTRACTED, throw IOException
		if (count == 0)
			throw new IOException("At least one file should be a TAR.");
			
		return true;
	}
	
	/**
	 * Extract the <code>archive</code> to the <code>dest</code> directory.<br>
	 * <br>
	 * 
	 * @param archive
	 *            The archive to be extract.
	 * @param dest
	 *            The destination directory extract to.
	 * @since 1.0.2
	 * @throws IOException
	 *             Any kind of I/O Errors.
	 * @return true if the operations run successfully.
	 */
	public boolean extract(File archive, FileSystem dest) throws IOException {
		// TODO Auto-generated method stub
		return extract(archive, dest.getRoot());
	}

	/**
	 * Guess how big is the compressed file without
	 * compressing actually. The algorithm of guessing the tar size as follow:<br>
	 * For each of file Each header size is TAR_ENTRY_SIZE bytes, and for the data content block. It use TAR_ENTRY_SIZE
	 * as a block of data. If for last block of data is not TAR_ENTRY_SIZE, then the rest will padding with the empty bytes.
	 * Such that the final guessed size is ceil((file_length/TAR_ENTRY_SIZE)+1)*TAR_ENTRY_SIZE. More details of tar file format can 
	 * found from <a href="http://en.wikipedia.org/wiki/Tarball">this</a>.
	 * 
	 * @param src
	 * 			The source of the file(s) to be archive.
	 * @return guessed file size in byte
	 * @since	
	 * 			1.0.3
	 * @throws NullPointerException
	 * 			if the <code>src</code> is null.
	 * @throws IOException
	 * 			if one of the file in the folders 
	 * 			does not exist in some reason.
	 */
	public long guessCompressedSize(File src) throws IOException {
		Iterator allFiles = listFilesToArchive(src);
		long size = 0;
		while(allFiles.hasNext()){
			//Need to query about why it is the RandomAccessFile			
			RandomAccessFile file = new RandomAccessFile((File)allFiles.next(), "r");
			size += (Math.ceil((double)file.length()/TAR_ENTRY_SIZE)+1)*TAR_ENTRY_SIZE;
			file.close();
		}
		return size;
	}
	
	/* (non-Javadoc)
	 * @see hk.hku.cecid.piazza.commons.io.Archiver#guessCompressedSize(hk.hku.cecid.piazza.commons.io.FileSystem)
	 */
	
	public long guessCompressedSize(FileSystem src) throws IOException {
		return guessCompressedSize(src.getRoot());
	}

	/* (non-Javadoc)
	 * @see hk.hku.cecid.piazza.commons.io.Archiver#isSupportArchive(java.io.File)
	 */
	
	public boolean isSupportArchive(File archive) {
		return PathHelper.getExtension(archive.getAbsolutePath()).equalsIgnoreCase("TAR"); 
	}
	
	/**
	 * List the files inside the <code>archive</code>.<br>
	 * 
	 * This operation is quite slow and pending to optimize.
	 * 
	 * @param archive
	 * 			The archive to be listed.
	 * @since	
	 * 			1.0.2 
	 * @return
	 * 			A list of java.io.File object that represents
	 * 			each entry in the archive. 
	 */
	public List listAsFile(File archive) throws IOException{
		TarInputStream tarInStream = new TarInputStream(new FileInputStream(archive));
		
		TarEntry entry = null;
		ArrayList list = new ArrayList();
		while((entry = tarInStream.getNextEntry())!=null){
			list.add(entry.getFile());
		}
		tarInStream.close();
		return list;
	}
	
	/**
	 * List the files inside the <code>archive</code>. 
	 * 
	 * @param archive
	 * 			The archive to be listed.
	 * @since	
	 * 			1.0.2 
	 * @return
	 * 			A list of String objects that represents
	 * 			the filename of each entry in the 
	 * 			archive. 
	 */
	public List listAsFilename(File archive) throws IOException{
		TarInputStream tarInStream = new TarInputStream(new FileInputStream(archive));
		
		TarEntry entry = null;
		ArrayList list = new ArrayList();
		while((entry = tarInStream.getNextEntry())!=null){
			list.add(entry.getName());
		}
		tarInStream.close();
		return list;
	}
}
