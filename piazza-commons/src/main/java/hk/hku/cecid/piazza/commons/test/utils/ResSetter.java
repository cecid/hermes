/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.test.utils;

import org.apache.commons.io.FileUtils;


import java.io.File;

/**
 * @author Patrick Yip
 * This class is to set the resource(fixture) for the test cases. Especially for the test case that need to simulate the action
 * of file system. Such as test cases for doc processor need to simulate operation of file move. So, before the test cases run.
 * Call the restore method to copy the needed resource to the tmp folder of fixture folder. After the test case finished, delete
 * the temp folder by calling the clean method. The purpose of this restore and clean is to filter the .svn file create by subversion 
 * that is not needed for the test cases to run. 
 */
public class ResSetter {
	public static String ORG_RES_FOLDER = "original_res";
	private Class clazz;
	private File resBase;
	private File fixtureBase;
	private File orgResBase;
	private File tmpDir;
	private ClassLoader loader;
	public static final String RES_TMP_DIR = "tmp";
	
	public ResSetter(Class clazz){
		this.clazz = clazz;
		fixtureBase = new File(FixtureStore.getFixtureURL(clazz).getFile());
		resBase = new File(fixtureBase.getAbsolutePath().substring(0, fixtureBase.getAbsolutePath().lastIndexOf("\\")+1));
		tmpDir = new File(fixtureBase, RES_TMP_DIR);
	}
	
	/**
	 * Restore all of the files in the res folder to the res.
	 * By moving the file from the orgResBase to the resBase. The moving operation will not move the CVS related files
	 * @throws Exception
	 */
	public void restore() throws Exception{
		tmpDir.mkdir();
		FileUtils.copyDirectory(fixtureBase, tmpDir, new ResFilter(false));
		System.out.println("Files copy from " + fixtureBase.getCanonicalPath() + " to " + tmpDir.getCanonicalPath());
	}
	
	/**
	 * Clean up the directory that related to the test cases in the res folder
	 * @throws Exception
	 */
	public void clean() throws Exception{
		FileUtils.deleteDirectory(tmpDir);
		System.out.println("All files from " + tmpDir.getCanonicalPath() + " was deleted");
	}
	
	/**
	 * Restore all of the files in the res folder to the tmp
	 * By copying all of the file from fixture for test case to the tmp folder 
	 * @throws Exception
	 */
	public void backupRes() throws Exception{
		tmpDir.mkdir();
		FileUtils.copyDirectory(fixtureBase, tmpDir, new ResFilter(true));
		System.out.println("Files copy from " + fixtureBase.getCanonicalPath() + " to " + tmpDir.getCanonicalPath());
	}
	
	/**
	 * Delete all of the file in the fixture base except tmp folder itself and copy back the original fixture
	 * from tmp to fixture, then delete the tmp folder
	 * @throws Exception
	 */
	public void storeRes() throws Exception{
		//1. Delete all of the files and folder in test case fixture folder
		File files[] = fixtureBase.listFiles(new ResFilter(true));
		for(int i=0 ; files.length > i; i++){
			if(files[i].isFile()){
				files[i].delete();
			}else{
				FileUtils.deleteDirectory(files[i]);
			}
		}
		
		//2. Copy back all of the fixture from tmp to the fixture base
		FileUtils.copyDirectory(tmpDir, fixtureBase, new ResFilter(true));
		
		//3. Delete all files in the tmp directory
		FileUtils.deleteDirectory(tmpDir);
	}

}
