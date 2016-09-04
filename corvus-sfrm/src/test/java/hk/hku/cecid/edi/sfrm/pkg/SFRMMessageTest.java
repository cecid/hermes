/**
 * 
 */
package hk.hku.cecid.edi.sfrm.pkg;

import java.io.File;
import java.io.IOException;

import hk.hku.cecid.edi.sfrm.activation.FileRegionDataSource;
import hk.hku.cecid.piazza.commons.os.OSCommander;
import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Patrick Yip
 *
 */
public class SFRMMessageTest extends TestCase {
	
	private ClassLoader FIXTURE_LOADER = FixtureStore.createFixtureLoader(false, this.getClass());
	public void setUp() throws Exception{
		System.out.println("------------------- Start up " + getName() + " -------------------");
	}
	
	public void tearDown() throws Exception{
		System.out.println("------------------- Shutdown " + getName() + " -------------------");
	}
	
	public void testLargeFileMD5(){
		//10GB
//		long fileSize = 10737418240L;
		//100MB
		long fileSize = 104857600L;
		//1GB
//		long fileSize = 17179869184L;
		long segmentSize = 1048576L;
		
		OSCommander commander = new OSCommander();
		File targetFile = new File(FIXTURE_LOADER.getResource(getName()).getFile(), "payload");
		boolean thrown = false;
		try {
			if(targetFile.exists())
				targetFile.delete();
			
			commander.createDummyFile(targetFile.getCanonicalPath(), fileSize);
			
			long numSegment = fileSize / segmentSize;
			if(fileSize % segmentSize != 0){
				numSegment+=1;
			}
			
			long inc = 0;
			
			for(long i=0; numSegment > i ; i++){
				long startPos = i*segmentSize;
				long endPos = 0;
				
				if(i != numSegment - 1){
					endPos = segmentSize;
				}else{
					endPos = fileSize%segmentSize;
				}
				
				String md5 = "";
				System.out.print(Long.toString(i+1) + ". Start: " + Long.toString(startPos) + ", End: " + Long.toString(endPos));
				md5 = SFRMMessage.digest((new FileRegionDataSource(targetFile.getCanonicalPath(), startPos, endPos)));
				System.out.println(", MD5: " + md5);
//				Thread.sleep(200);
//				System.gc();
//				System.runFinalization();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			thrown = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			thrown = true;
		}finally{
			if(targetFile.exists())
				targetFile.delete();
		}
		
		Assert.assertFalse("Should not throw error when evaluating the MD5 value", thrown);
		
	}
}
