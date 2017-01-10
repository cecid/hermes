package hk.hku.cecid.piazza.commons.os;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.io.FileSystemUtils;

import hk.hku.cecid.piazza.commons.module.ModuleException;
import hk.hku.cecid.piazza.commons.module.SystemComponent;
import hk.hku.cecid.piazza.commons.util.ConsoleLogger;
import hk.hku.cecid.piazza.commons.util.Logger;

/**
 * The OS Manager provides interface for executing
 * platform command console so that it can 
 * execute the console command through this interface.
 * <br><br>
 * 
 * Creation Date: 04/05/2009<br><br>

 * @author Philip Wong
 * @version 2.0.0
 * @since	2.0.0
 */
public class OSCommander {

	public static final long ONE_GB = 1073741824L;
	private static final int BUFFER_SIZE = 8192;
	
	public static boolean REDIRECT_ERROR_STREAM = true;
	
	private String[] interpreter;
	private String os_name;
	private String os_ver;
	
	private Logger log;
	
	public OSCommander() {
		log = ConsoleLogger.getInstance();
		init();
	}
	
	public OSCommander(SystemComponent sys) {
		log = sys.getLogger();
		init();
	}
	
	public String getOSName() {
		return os_name;
	}
	
	public String getOSVersion() {
		return os_ver;
	}
	
	protected void init() {
		String name = System.getProperty("os.name").toUpperCase();
		os_ver = System.getProperty("os.version");
		
		if (name.indexOf("WINDOWS") >= 0) {
			os_name = "WINDOWS";			
			interpreter = new String[]{"cmd.exe", "/C"};
		} else if (name.indexOf("LINUX") >= 0) {
			os_name = "LINUX";			
			interpreter = new String[] {"/bin/sh", "-c"};
		} else if (name.indexOf("MAC") >= 0) {
			os_name = "MAC";			
			interpreter = new String[] {"/bin/sh", "-c"};
		} else if (name.indexOf("SUNOS") >= 0) {
			os_name = "SUNOS";			
			interpreter = new String[] {"/bin/sh", "-c"};
		} else {
			throw new ModuleException("OSCommander not found for " + name);
		}
	}
	
	public BufferedReader execNoWaitAsReader(String... args) throws IOException {
		return execNoWaitAsReader(null, args);
	}
	
	public BufferedReader execNoWaitAsReader(File dir, String... args) throws IOException {
		return new BufferedReader(new InputStreamReader(
				execNoWaitAsInputStream(dir, args)));
	}
	
	public BufferedInputStream execNoWaitAsInputStream(String... args) throws IOException {
		return execNoWaitAsInputStream(null, args);
	}
	
	public BufferedInputStream execNoWaitAsInputStream(File dir, String... args) throws IOException {
		Process process = execNoWaitAsProcess(dir, args);
		return new BufferedInputStream(process.getInputStream());
	}

	protected Process execNoWaitAsProcess(String... args) throws IOException {
		return execNoWaitAsProcess(null, args);
	}
	
	protected Process execNoWaitAsProcess(File dir, String... args) throws IOException {
		ArrayList<String> cmdList = new ArrayList<String>();
		Collections.addAll(cmdList, args);
		
		boolean asterisk = false;
		Iterator<String> iter = cmdList.iterator();
		while (iter.hasNext()) {
			String arg = iter.next();
			if (arg.indexOf('*') != -1) {
				asterisk = true;
				break;
			}
		}
		
		if (asterisk) {
			log.info("OSCommander use command interpreter");
			
			String cmd = "";
			for (String s:cmdList) 
				cmd += s + " "; 
			
			cmdList = new ArrayList<String>();
			Collections.addAll(cmdList, interpreter);
			cmdList.add(cmd);
		}
			
		ProcessBuilder builder = new ProcessBuilder().redirectErrorStream(REDIRECT_ERROR_STREAM).directory(dir).command(cmdList);
		
		log.info("OSCommander" + 
				((builder.directory()==null)?"":" dir:" + builder.directory()) + 
				" cmd:" + builder.command());
	
		return builder.start();
	}
	
	public void execWaitAsOutputStream(OutputStream os, String... args) throws IOException, InterruptedException {
		execWaitAsOutputStream(os, null, args);
	}
	
	public void execWaitAsOutputStream(OutputStream os, File dir, String... args) throws IOException, InterruptedException {
		Process process = execNoWaitAsProcess(dir, args);
		pipe(new BufferedInputStream(process.getInputStream()), os);
		int exitValue = process.waitFor();
		log.info("OSCommander exit(" + exitValue + ")");
	} 

	public String execWaitAsString(String... args) throws IOException, InterruptedException {
		return execWaitAsString(null, args);
	}
	
	public String execWaitAsString(File dir, String... args) throws IOException, InterruptedException {
		Process process = execNoWaitAsProcess(dir, args);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		pipe(new BufferedInputStream(process.getInputStream()), baos);
		int exitValue = process.waitFor();
		log.info("OSCommander exit(" + exitValue + ")");
		return baos.toString();
	} 
	
	private void pipe(InputStream is, OutputStream os) {
		try {
			int i = 0;
			byte[] b = new byte[BUFFER_SIZE];
			while ((i = is.read(b)) != -1) { 
				os.write(b, 0, i);
			}
		} catch (IOException e) {
			log.error("OSCommander thread copy to output stream error", e);
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				log.error("OSCommander cannot close output stream", e);
			}
			
			try {
				is.close();
			} catch (IOException e) {
				log.error("OSCommander cannot close input stream", e);
			}
		}		
	}
	
	public void writeTo(final InputStream is, final OutputStream os) {
		Thread thread = new Thread() {
			public void run(){
				pipe(is, os);
			}
		};
		
		thread.start();		
	}
	
	public long getDiskFreespace(String path) throws IOException {
		try {
			File file = new File(path);
			Method m = file.getClass().getDeclaredMethod("getUsableSpace");
			long length = (Long)m.invoke(file, new Object[] {});		
			log.info("OSCommand DiskFreespace(File 1.6): " + length);
			return length;
		} catch (Exception e) {

			long length = FileSystemUtils.freeSpaceKb(path) << 10;
			log.info("OSCommand DiskFreespace(FileSystemUtils): " + length);
			return length;
		}
	}
	
	/**
	 * Create a dummy file with the specified path and size for Linux, Mac OS X & SunOS.
	 * 
	 * @param path The absolute path of the dummy files.
	 * @param size The size of dummy files.
	 * @return true if the operation run successfully.
	 * @throws IOException 
	 */
	public void createDummyFile(String path, long size) throws Exception {
		if (new File(path).exists())
			throw new IOException("File already exist " + path);

		// Windows version as follows
		// Win2k = version 5.0
		// WinXP = version 5.1
		// Vista = version 6.0
		// Win7 = version 6.1

		// We treat windows version smaller than 5.1 as older windows
		
		// Command "fsutil" only exists from Windows XP, but it's required admin access privilege from Windows Vista
		/*
		if (getOSName().equals("WINDOWS") && (Double.valueOf(getOSVersion()) > 5.0) {
			String output = execWaitAsString(
					new String[] {"fsutil", "file", "createnew", path, Long.toString(size)});
			
			log.info("OSCommander - fsutil: " + output);
		} else {
			FileOutputStream fos = new FileOutputStream(path, true);
			FileChannel foc = fos.getChannel();
			ByteBuffer block = ByteBuffer.allocate(BUFFER_SIZE);
			
			long noOfBlock = size / BUFFER_SIZE;
			int remainder = (int)(size % BUFFER_SIZE);
			
			log.debug("Expected size:" + size + " Block size:" + BUFFER_SIZE + " No of Block:" + noOfBlock + " Remainder:" + remainder);
			
			for(long i = 0; i < noOfBlock; i++) {
				foc.write(block);
				block.rewind();
			}
			
			if (remainder > 0) {
				block.limit(remainder);
				foc.write(block);
			}
			
			foc.close();
		}
		*/
		
		RandomAccessFile acf = new RandomAccessFile(path, "rw"); 
		acf.setLength(size);
		
		log.info("OSCommander - RandomAccessFile: " + acf.length());
		
		acf.close();		
	}	
	
}
