/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import hk.hku.cecid.piazza.commons.module.Component;

import hk.hku.cecid.piazza.commons.util.Logger;
import hk.hku.cecid.piazza.commons.util.UtilitiesException;

/**
 * The file logger is a simple logger that log everything to 
 * the desired file.<br><br>
 * 
 * <strong>Dependency</strong> : Piazza Common<br>
 *  
 * <br>
 * The sample usage is shown on the below: <br>
 * <br>
 * <PRE>
 * 		FileLogger log = new FileLogger("../logs/testlog.txt");
 *		log.debug("Test debug statement");
 *		log.error("Test error statement");
 *		log.fatal("Test fatal statement");
 *		log.info ("Test info  statement");
 *		log.warn ("Test warn  statement");
 * </PRE>
 * <br>
 * The output in the file logger should be liked these:
 * <PRE>
 * 		[Debug] Test debug statement
 * 		[Error] Test error statement
 * 		[Fatal] Test fatal statement
 * 		[Info]  Test info statement
 * 		[Warn]  Test warn statement
 * </PRE>
 * 
 * <strong>SPA Component Guideline:</strong><br>.
 * 
 * @author Twinsen Tsang
 * @version 1.0.1
 * @since	1.0.0  
 */
public class FileLogger extends Component implements Logger {

	private File logFile;
	private OutputStream outStream;
	private PrintStream logStream;

	/*
	 * The default log file name for logging when
	 * the input file object is a directory.
	 */
	private static final String defaultLogFileName = "log.txt";
	
	/*
	 * The logging tag using.
	 */	
	private String debugTag	  = "[Debug] ";
	private String errorTag	  = "[Error] ";
	private String fatalTag	  = "[Fatal] ";
	private String warnTag	  = "[Warn]	 ";
	private String infoTag	  = "[Info]	 ";
	private String stackTraceTag = "[Stack Trace] ";
	
	/**
	 * Constructor.<br>
	 * <br> 
	 * 
	 * @param filepath				The filepath for logging.
	 * @throws UtiltiesException	Throw if any IO Operations fail like 
	 * 								can not open file or print stream.
	 */
	public FileLogger(String filepath) throws UtilitiesException { 
		this(new File(filepath));
	}
	
	/**
	 * Constructor.<br>
	 * <br>
	 * <br>
	 * The file logger open a file output stream for writing the 
	 * log to the file.<br>
	 * 
	 * It use the default log file name <em>"log.txt"</em>
	 * for logging if the file object is a directory.<br>
	 * 
	 * @param f						The file object specified.
	 * @throws UtilitiesException	Throw if any IO Operations fail like 
	 * 								can not open file or print stream.
	 */
	public FileLogger(File f) throws UtilitiesException {
		logFile = f;
		
		try {
			if (f.isDirectory()) {
				logFile = new File(f, defaultLogFileName);
			} else {
				if (!f.getParentFile().exists())
					f.getParentFile().mkdirs();
				
				logFile.createNewFile();
			}
			
			outStream = new FileOutputStream(logFile);
			logStream = new PrintStream(outStream, true);
		} catch (IOException ioe) { 
			throw new UtilitiesException("Could not open the log file \""
										+ logFile.getName() 
										+ "\""
										+ " for writing. Logger not created.", ioe);
		}
	}
	
	/**
	 * Override initialization method.<br>
	 * 
	 * It invokes when the logger is acted as Module Component.  
	 */
	protected void init(){				
		String [] tagName 	= {"debugTag","errorTag","fatalTag","warnTag","infoTag"};
		String [] tagValue 	= new String[tagName.length];
		
		// Get all the value from the parameter list.
		for (int i = 0; i< tagName.length; i++){
			tagValue[i] = this.getParameters().getProperty(tagName[i]);
		}						
		
		// Assign the new tag value to the tag element.
		if (tagValue[0] != null)
			this.debugTag = tagValue[0];
		if (tagValue[1] != null)
			this.errorTag = tagValue[1];
		if (tagValue[2] != null)
			this.fatalTag = tagValue[2];
		if (tagValue[3] != null)
			this.warnTag = tagValue[3];
		if (tagValue[4] != null)
			this.infoTag  = tagValue[4];
	}
	
	/**
	 * Override debug print method.
	 * 
	 * @param msg debugging object to be printed.
	 */
	public void 
	debug(Object msg)
	{
		this.log(debugTag + msg.toString());
	}
	
	/**
	 * Override debug with exception print method.
	 * 
	 * @param msg debugging object to be printed.
	 */
	public void 
	debug(Object msg, Throwable t)
	{
		this.debug(msg);
		this.logStackTrace(t);
	}
	
	/**
	 * Override error print method.
	 * 
	 * @param msg errorging object to be printed.
	 */
	public void 
	error(Object msg)
	{
		this.log(errorTag + msg.toString());
	}
	
	/**
	 * Override error with exception print method.
	 * 
	 * @param msg errorging object to be printed.
	 */
	public void 
	error(Object msg, Throwable t)
	{
		this.error(msg);
		this.logStackTrace(t);
	}
	
	/**
	 * Override fatal print method.
	 * 
	 * @param msg fatalging object to be printed.
	 */
	public void 
	fatal(Object msg)
	{
		this.log(fatalTag + msg.toString());
	}
	
	/**
	 * Override fatal with exception print method.
	 * 
	 * @param msg fatalging object to be printed.
	 */
	public void 
	fatal(Object msg, Throwable t)
	{
		this.fatal(msg);
		this.logStackTrace(t);
	}
	
	/**
	 * Override info print method.
	 * 
	 * @param msg infoging object to be printed.
	 */
	public void 
	info(Object msg)
	{
		this.log(infoTag + msg.toString());
	}
	
	/**
	 * Override info with exception print method.
	 * 
	 * @param msg infoging object to be printed.
	 */
	public void 
	info(Object msg, Throwable t)
	{
		this.info(msg);
		this.logStackTrace(t);
	}
	
	/**
	 * Override warn print method.
	 * 
	 * @param msg warnging object to be printed.
	 */
	public void 
	warn(Object msg)
	{
		this.log(warnTag + msg.toString());
	}
	
	/**
	 * Override warn with exception print method.
	 * 
	 * @param msg warnging object to be printed.
	 */
	public void 
	warn(Object msg, Throwable t)
	{
		this.warn(msg);
		this.logStackTrace(t);
	}
	
	/**
	 * Log a string to the file by the print stream.
	 * 
	 * @param s	The string to be logged.
	 */
	public void 
	log(String s)
	{
		if (this.logStream != null)
			logStream.println(s);
	}
		
	/**
	 * Log a exception / throwable to the file by the print stream.<br>
	 * 
	 * The exception element will begin with the tag [Stack Trace]
	 * other than debug, error, fatal, warn, info.<br>
	 * 
	 * @param e	The throwable e to be logged.
	 */
	public void 
	logStackTrace(Throwable e) 
	{
		if (e != null && this.logStream != null) {
			StackTraceElement[] list = e.getStackTrace();
			for (int index = 0; index < list.length; index++) {
				log(stackTraceTag + list[index].toString());
			}
		}
	}

	/**
	 * The method finalized the class.
	 */
	protected void 
	finalize() 
	{
		if (logStream != null)
			logStream.close();
		try {
			outStream.close();
		} catch (IOException e) {
			System.err.println("Failed to close the stream.");
		}
	}		
	
	/**
	 * toString method().
	 */
	public String 
	toString()
	{
		return super.toString();
	}
}
