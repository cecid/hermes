/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.sfrm.util;

import java.io.File;
import java.io.IOException;

/**
 * It provides some useful method for handling the file path issue.<br><br>
 * 
 * version 1.0.1 - Added {@link #getRelativePath(String, String)}.<br>
 * 
 * @author Twinsen
 * @version 1.0.1
 */
public class PathHelper {
	
	/**
	 * Get the canonical path from the basePath and relative path.
	 * 
	 * @param basePath 
	 * @param path
	 * @return the canonical path from the "path".
	 * @throws IOException
	 */
	public static String getCanonicalPath(String basePath, String path) throws IOException{
		File found = null;
		if (!(new File(path).isAbsolute())) {
			found = new File(basePath, path);				
		} else {
			found = new File(path);
		}
		return found.getCanonicalPath();
	}
	
	/**
	 * Get the relative path from the canoical basepath and path. 
	 * 
	 * @param basePath
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String getRelativePath(String basePath, String path) throws IOException{
		if (basePath == null || 
			path == null	 ||
			basePath.equalsIgnoreCase(path))
			return "";
		else if (path.startsWith(basePath)){ 
			String relPath = path.substring(basePath.length(), path.length());							  
			return "." + relPath;
		}			
		return "";
	}
	
	/**
	 * Get the file extension from the specified path.
	 * 
	 * @param 	path
	 * 				The filepath for getting it's extension. 	
	 * @return 	return the extension of the path. 
	 * 		  	It returns empty string if the path is not directory or file without extension.
	 */
	public static String getExtension(String path) {
		if (path == null)
			return null;
		int index = path.lastIndexOf(".");
		if (index != -1)
			return path.substring(index + 1, path.length());								
		return "";
	}
	
	/**
	 * Remove the file extension from the specified path.
	 * 
	 * @param path
	 * @return return the path without extension.
	 */
	public static String removeExtension(String path) {
		if (path != null){
			int index = path.lastIndexOf("."); 
			if (index != -1){				
				return path.substring(0, index);				
			}			
		}
		return path;
	}
	
	/**
	 * Get the filename of the particular path.
	 * If the filename of the path does not exist, throws IOException.
	 * 
	 * @param path
	 * @return
	 * 
	 * @throws IOException if the file does not exist or it is a directory.
	 */
	public static String getFilename(String path) throws IOException{
		String ret = "";
		// 	Validate the path.				
		File f = new File(path);
		if (!f.exists()){
			throw new IOException("File does not exist.");
		} else if (f.exists() && f.isDirectory()){			
			throw new IOException("Path is a directory.");
		} else {
			ret = PathHelper.removeExtension(path);
			int index = path.lastIndexOf(File.separator); 
			if (index != -1){				
				ret = path.substring(index+1, ret.length());				
			}	
			return ret;
		}
	}
	
	/**
	 * Create the path specified in parameter. 
	 * This method will create any missing directory from the path.
	 * 
	 * @throws IOException 
	 */
	public static void createPath(String path) throws IOException{
		// Create sub-folder or repair the path.					
		File f = new File(path);				
		if (!f.exists()){
			// Create any parent folders if needed.
			int index = path.lastIndexOf(File.separator);
			if (index != -1){
				String dirs = path.substring(0, index);
				new File(dirs).mkdirs();
			}			
		}
	}	
	
	/**
	 * Rename the file specified to the new name specified 
	 * in parameters.<br>
	 * 
	 * @param source	The source file object.
	 * @param newName	The new name of the object.
	 * @return The new file object for the new path.
	 */
	public static File renameTo(File   source
							   ,String newName){		
		if (!source.exists())
			return null;
		String parentPath = source.getParent()==null ? "" : source.getParent();		
		File target = new File(parentPath, newName);
		boolean ret = source.renameTo(target);
		return ret ? target : null; 
	}
}
