package hk.hku.cecid.edi.sfrm.archive;

import java.io.File;

import hk.hku.cecid.piazza.commons.os.OSCommander;
import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Patrick Yip
 *
 */
public class ArchiverTarTest extends TestCase {
	private ClassLoader FIXTURE_LOADER = FixtureStore.createFixtureLoader(false, this.getClass());
	public void setUp(){
		System.out.println("Starting " + this.getName());
	}
	
	public void tearDown(){
		System.out.println("Shutdown " + this.getName());
	}
	
	/**
	 * Test for untar the file
	 * Notice: Run this test should have at least 4GB disk space
	 * @throws Exception
	 */
	public void testExtractFile() throws Exception{
		OSCommander os = new OSCommander();
		
		//10MB
		long payloadSize = 10485760L;		
		
		String payloadName = "10MB";
		
		File dummyFile = new File(FIXTURE_LOADER.getResource("Src").getFile(), payloadName) ;
		File tarFile = new File(FIXTURE_LOADER.getResource("Compressed").getFile(), payloadName + ".tar");
		File extractDir = new File(FIXTURE_LOADER.getResource("Extracted").getFile());
		File extractedFile = null;
		
		try{
			os.createDummyFile(dummyFile.getAbsolutePath(), payloadSize);
			Assert.assertTrue("dummyFile didn't created", dummyFile.exists());
			ArchiverTar tar = new ArchiverTar();
			//Compress the file firstly
			tar.compress(dummyFile, tarFile, true);
			dummyFile.delete();
			//Extract the file
			tar.extract(tarFile, extractDir);
			extractedFile = new File(extractDir, payloadName);
			//Check that whether the extracted file size is the same as orginial 
			Assert.assertEquals("Extracted payload size should be " + Long.toString(payloadSize), payloadSize, extractedFile.length());
			Assert.assertEquals("Extracted file name should same as original", dummyFile.getName(), extractedFile.getName());
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(tarFile.exists())
				tarFile.delete();
			if(extractedFile.exists())
				extractedFile.delete();
		}
	}
	
	
	/**
	 * Test for tar the file with long filename, for traditional tar format
	 * , it only support the tar entry name <= 100 characters
	 * @throws Exception
	 */
	public void testCompressLongFileName() throws Exception{
		OSCommander os = new OSCommander();
		
		long payloadSize = 10485760L;
		String payloadName = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
		
		File dummyFile = new File(FIXTURE_LOADER.getResource("Src").getFile(), payloadName);
		File tarFile = new File(FIXTURE_LOADER.getResource("Compressed").getFile(), payloadName + ".tar");
		File extractDir = new File(FIXTURE_LOADER.getResource("Extracted").getFile());
		File extractedFile = null;
		
		try{
			os.createDummyFile(dummyFile.getAbsolutePath(), payloadSize);
			Assert.assertTrue("dummyFile didn't created", dummyFile.exists());
			ArchiverTar tar = new ArchiverTar();
			//Compress the file firstly
			tar.compress(dummyFile, tarFile, true);
			dummyFile.delete();
			//Extract the file
			tar.extract(tarFile, extractDir);
			extractedFile = new File(extractDir, payloadName);
			Assert.assertTrue("Untar file should exist", extractedFile.exists());
			//Check that whether the extracted file size is the same as orginial
			Assert.assertEquals("Extracted payload size should be " + Long.toString(payloadSize), payloadSize, extractedFile.length());
			Assert.assertEquals("Extracted file name should same as original", dummyFile.getName(), extractedFile.getName());
		}catch(Exception e){
			throw e;
		}finally{
			if(tarFile.exists())
				tarFile.delete();
			if(extractedFile.exists())
				extractedFile.delete();
		}
	}
	
	/**
	 * Test for tar a file
	 * @throws Exception
	 */
	public void testCompressFile() throws Exception{
		OSCommander os = new OSCommander();
		
		//10 MB payload size
		long payloadSize = 10485760L;
		String payloadName = "10MB";
		File dummyFile = new File(FIXTURE_LOADER.getResource("Src").getFile(), payloadName);
		File tarFile = new File(FIXTURE_LOADER.getResource("Compressed").getFile(), payloadName + ".tar");
		
		try{
			os.createDummyFile(dummyFile.getAbsolutePath(), payloadSize);
			Assert.assertTrue("dummyFile didn't created", dummyFile.exists());
			ArchiverTar tar = new ArchiverTar();
			//Compress the file firstly
			tar.compress(dummyFile, tarFile, true);
			Assert.assertTrue("Compressed file size should greater than orginial file size", tarFile.length() > dummyFile.length());
		}catch(Exception e){
			throw e;
		}finally{
			if(tarFile.exists())
				tarFile.delete();
			if(dummyFile.exists())
				dummyFile.delete();
		}
	}
	
//	public void testCompressChineseCharFilename() throws Exception{
//		File srcFile = new File(FIXTURE_LOADER.getResource(getName()).getFile(), "Src");
//		File tarFile = new File(FIXTURE_LOADER.getResource(getName()).getFile(), "payload.tar");
//		
//		ArchiverTar tar = new ArchiverTar();
//		//Tar the file with the file name using chinese character
//		Assert.assertTrue("Failure on archiving files using tar", tar.compress(srcFile, tarFile, false));
//		
//		//untar the file
//		File destFile = new File(FIXTURE_LOADER.getResource(getName()).getFile(), "Extracted");
//
//		tar.extract(tarFile, destFile);
//		
//		File extractedFile = new File(destFile, "[chinese filename].txt");
//		
//		Assert.assertTrue("File didn't existed after extracted", extractedFile.exists());
//		
//		if(tarFile.exists())
//			tarFile.delete();
//		
//		if(extractedFile.exists())
//			extractedFile.delete();
//	}
	
}
