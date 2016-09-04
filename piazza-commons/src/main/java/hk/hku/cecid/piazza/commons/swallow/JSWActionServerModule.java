/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.swallow;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.tanukisoftware.wrapper.WrapperActionServer;
import static org.tanukisoftware.wrapper.WrapperActionServer.*;

import hk.hku.cecid.piazza.commons.module.ActiveModule;
import hk.hku.cecid.piazza.commons.module.ModuleException;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * The JSWActionServerModule is an integrated module with JSW (Java service wrapper) to provide functionality same as WrapperActionServer defined in JSW. 
 * 
 * In simple, this module is acting as a Back-door tool enabling the operator to control the behavior of the JVM running inside JSW.
 * 
 * NOTE: The JVM must run under JSW before using the module !.
 * 
 * Quoted from the JAVADOC of WrapperActionServer:
 * 
 * If an application instantiates an instance of this class, the JVM will listen on the specified port for connections. 
 * 
 * When a connection is detected, the first byte of input will be read from the socket and then the connection will be immediately closed. 
 * 
 * An action will then be performed based on the byte read from the stream.
 * 
 * The easiest way to invoke an action manually is to Telnet to the specified port and then type the single command key. telnet localhost 9999, for example.
 * 
 * Valid commands include:
 * <ul>
 *  <li>S : Shutdown cleanly.</li>
 *  <li>H : Immediate forced shutdown.</li>
 *  <li>R : Restart</li>
 *  <li>D : Perform a Thread Dump</li>
 *  <li>U : Unexpected shutdown. (Simulate a crash for testing)</li>
 *  <li>V : Cause an access violation. (For testing)</li>
 *  <li>G : Make the JVM appear to be hung. (For testing)</li>
 * </ul> 
 *  
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since	JDK5.0 
 */
public class JSWActionServerModule extends ActiveModule
{
	/*
	 * The JSW wrapper action server instance.
	 */
	private WrapperActionServer wrappingActionServer; 
		
	/*
	 * The listening port of the action server, default is 9998.
	 */
	private int listenPort = 9998;	
	
	/*
	 * The The flag indicate whether the server accept local connection only.
	 * 
	 * Note, setting this value to false indicate everyone can access and terminate the JVM.
	 * 
	 * Take your own risk to set this value!
	 */
	private boolean localConnectionOnly = true;
	
	
	private boolean isServerStarted = false;
		
	public static final int MAX_ACTION_ALLOWED  = 255;
	
	private boolean[] actionEnabled = new boolean[MAX_ACTION_ALLOWED];
	
	/** 
	 * The <code>COMMAND</code> is the enumeration of default action server command set.   
	 */
	public static enum COMMAND 
	{		
		SHUTDOWN	    (COMMAND_SHUTDOWN		 , "shutdownEnabled"                , true , null),
		FORCE_HALT	    (COMMAND_HALT_EXPECTED	 , "forceHaltEnabled"               , true , null),
		RESTART         (COMMAND_RESTART		 , "restartEnabled"                 , true , null),
		THREADDUMP      (COMMAND_DUMP            , "threadDumpEnabled"              , true , null),
		ACCESS_VIOLATION(COMMAND_ACCESS_VIOLATION, "stimulateAccessViolationEnabled", false, null),
		JVM_HANG        (COMMAND_APPEAR_HUNG     , "stimulateJVMHangEnabled"        , false, null),
		UNEXPECT_HALT   (COMMAND_HALT_UNEXPECTED , "stimulateUnexpectedHaltEnabled" , false, null);

		private byte c;
		private String property;
		private boolean enabled;
		private Runnable r;
		
		private COMMAND(byte c, String property, boolean defaultEnabled, Runnable r)
		{
			this.c = c;
			this.property = property;
			this.enabled = defaultEnabled;
			this.r = r;
		}
		
		public boolean getEnabled()		{ return this.enabled;  }				
		public String  getPropertyKey() { return this.property; }
		public byte    getCode()        { return this.c; }
	}	
	
	/**
     * Creates a new instance of <code>JSWActionServerModule</code>.
     * 
     * @param descriptorLocation the module descriptor.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
	public JSWActionServerModule(String descriptorLocation, boolean shouldInitialize)
	{
		super(descriptorLocation, shouldInitialize);
	}

	 /**
     * Creates a new instance of <code>JSWActionServerModule</code>.
     * 
     * @param descriptorLocation the module descriptor.
     * @param shouldInitialize true if the module should be initialized.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
	public JSWActionServerModule(String descriptorLocation, ClassLoader loader, boolean shouldInitialize)
	{
		super(descriptorLocation, loader, shouldInitialize);
	}

	/**
     * Creates a new instance of <code>JSWActionServerModule</code>.
     * 
     * @param descriptorLocation the module descriptor.
     * @param loader the class loader for this module.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
	public JSWActionServerModule(String descriptorLocation, ClassLoader loader)
	{
		super(descriptorLocation, loader);
	}

	/**
     * Creates a new instance of <code>JSWActionServerModule</code>.
     * 
     * @param descriptorLocation the module descriptor.
     * @param loader the class loader for this module.
     * @param shouldInitialize true if the module should be initialized.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
	public JSWActionServerModule(String descriptorLocation)
	{
		super(descriptorLocation);
	}
	
	/**
	 * Initialize the action server configuration from the property defined in the xml.
	 */
	@Override
	public void init()
	{
		super.init();
		Properties p = super.getParameters();
		this.listenPort = StringUtilities.parseInt(p.getProperty("listenPort"), 9998);
		this.localConnectionOnly = StringUtilities.parseBoolean(p.getProperty("localConnectionOnly"), true);
		
		for (COMMAND e: COMMAND.values())
		{
			this.actionEnabled[e.getCode()] = StringUtilities.parseBoolean(
				p.getProperty(e.getPropertyKey()), e.getEnabled());
		}
		
		/*
		this.actionEnabled[COMMAND_SHUTDOWN] 	     = StringUtilities.parseBoolean(p.getProperty("shutdownEnabled"));
		this.actionEnabled[COMMAND_HALT_EXPECTED]    = StringUtilities.parseBoolean(p.getProperty("forceHaltEnabled"));
		this.actionEnabled[COMMAND_RESTART]          = StringUtilities.parseBoolean(p.getProperty("restartEnabled"));
		this.actionEnabled[COMMAND_DUMP]             = StringUtilities.parseBoolean(p.getProperty("threadDumpEnabled"));
		this.actionEnabled[COMMAND_ACCESS_VIOLATION] = StringUtilities.parseBoolean(p.getProperty("stimulateAccessViolationEnabled"));
		this.actionEnabled[COMMAND_APPEAR_HUNG]      = StringUtilities.parseBoolean(p.getProperty("stimulateJVMHangEnabled"));
		this.actionEnabled[COMMAND_HALT_UNEXPECTED]  = StringUtilities.parseBoolean(p.getProperty("stimulateUnexpectedHaltEnabled"));
		*/
		
		if (this.localConnectionOnly)
		{
			try
			{
				/*
				 * Start the server which bind the local-host address only.
				 */
				this.wrappingActionServer = new WrapperActionServer(this.listenPort, InetAddress.getByName("localhost"));	
			}
			catch(UnknownHostException uhex)
			{
				this.getLogger().error("[JSW ActServer] Unable to find localhost", uhex);
			}			
		}
		else
		{
			/*
			 * Start the server which bind any of address.
			 */
			this.wrappingActionServer = new WrapperActionServer  (this.listenPort);	
		}
		
		this.wrappingActionServer.enableShutdownAction       (this.actionEnabled[COMMAND_SHUTDOWN]);
		this.wrappingActionServer.enableHaltExpectedAction   (this.actionEnabled[COMMAND_HALT_EXPECTED]);
		this.wrappingActionServer.enableRestartAction        (this.actionEnabled[COMMAND_RESTART]);
		this.wrappingActionServer.enableThreadDumpAction     (this.actionEnabled[COMMAND_DUMP]);
		this.wrappingActionServer.enableAccessViolationAction(this.actionEnabled[COMMAND_ACCESS_VIOLATION]);
		this.wrappingActionServer.enableAppearHungAction     (this.actionEnabled[COMMAND_APPEAR_HUNG]);
		this.wrappingActionServer.enableHaltUnexpectedAction (this.actionEnabled[COMMAND_HALT_UNEXPECTED]);
	}
	
	/**
	 * Return true if the command action is enabled in this action server. false otherwise.
	 * 
	 * @param c The action command to check whether it is enabled. 
	 * @return true if the command action is enabled in this action server. false otherwise.
	 */
	public boolean isActionEnabled(COMMAND c)
	{
		return this.actionEnabled[c.getCode()];
	}

	/**
	 * Get whether the action server accepts local connection only.
	 * 
	 * @return Get whether the action server accepts local connection only.
	 */
	public boolean getIsLocalConnectionOnly()
	{
		return this.localConnectionOnly;
	}
	
	/**
	 * Get the listen port of the action server. 
	 * 
	 * @return Get the listen port of the action server.
	 */
	public int getListenPort()
	{
		return this.listenPort;
	}
	
	/**
	 * Start the JSW Action server by calling {@link WrapperActionServer#start()}. 
	 * 
	 * In this module, the ActiveModule thread will act as control thread for monitoring the wrapper
	 * action server thread. So it do nothing.
	 * 
	 * @see #execute()
	 */
	@Override
	public synchronized void start()
	{
		// Return immediate after the server has been started.
		if (this.isServerStarted)
			return;
		
		super.start();
		super.getThread().setName("JSW-Control-ActionServer@" + Integer.toHexString(this.hashCode()));
		
		if (this.wrappingActionServer == null)
		{
			throw new IllegalStateException("The 'JSW action server' has not initialized. Please call init()");
		}
		try
		{
			this.wrappingActionServer.start();
			this.isServerStarted = true;
		}
		catch(IOException ioex)
		{
			this.getLogger().error("[JSW ActServer]: Start Error" + ioex.getMessage(), ioex);
		}
	}

	/**
	 * Stop the JSW Action server by calling {@link WrapperActionServer#stop()}.
	 * 
	 * This method does not guarantee the JSW Action Server has been switched off after the invocation of 
	 * this method. It just spawn a new thread for closing the action server.
	 */
	@Override
	public synchronized void stop()
	{
		// Return immediate when the server has yet to start. 
		if (!this.isServerStarted)
			return;
				
		// Stop the control thread
		super.stop();
		
		if (this.wrappingActionServer != null)
		{
			/*
			 * The program will suffer deadlock when the action server is shutdown by the current thread under    
			 * JSW
			 *        
			 *    WrapperManager      -> lock@ WrapperStartStopApp.stop  
			 *    WrapperStartStopApp -> lock@ JSWActionServerModule.stop
			 *    WrapperActionServer -> lock@ WrapperManager.stop
			 */
			Thread stopServerHelper = new Thread("JSW-Stop-ActionServer@" + Integer.toHexString(this.hashCode()))
			{
				public void run()
				{
					try
					{
						wrappingActionServer.stop();
					}					
					catch(Exception ex)
					{
						getLogger().error("[JSW ActServer]: Stop Error" + ex.getMessage(), ex);
					}		
				}
			};
			stopServerHelper.setDaemon(true);
			stopServerHelper.start();			
			
			this.isServerStarted = false;				
		}		
	}
	 	
	/**
	 * This method does nothing, just sleep forever. This module thread is acting as the control
	 * thread for the wrapper action server.
	 */
	@Override
	public boolean execute()
	{
		try
		{
			while(true)
			{
				Thread.sleep(Integer.MAX_VALUE);					
			}			
		}	
		catch(Throwable t){}
		return true;
	}

	// ===============================================
	// Helper method 
	// ===============================================
	
	private static Field actionsMapField;
	
	static 
	{		
		try
		{
			actionsMapField = WrapperActionServer.class.getDeclaredField("m_actions");
			actionsMapField.setAccessible(true);
		}
		catch (NoSuchFieldException e)
		{
			String error = "Unable to find \"m_actions\" in the JSW ActServer class, incompatible JSW version";
			System.err.println(error);
			e.printStackTrace();
		}
	}
	
	/**
	 * Dump all action enabled in the java service wrapper action server. 
	 */
	public void dumpEnabledAction()
	{
		this.dumpEnabledAction(System.out);
	}
	
	/**
	 * Dump all action enabled in the java service wrapper action server to the specified <code>os</code>.
	 * 
	 * @param os The output stream dumping to.
	 */
	@SuppressWarnings("unchecked")
	public void dumpEnabledAction(OutputStream os)
	{
		if (os == null)
		{
			throw new NullPointerException("Missing 'os' in the arguments.");
		}
		
		Set<Entry<Byte, Runnable>> actionsMapEntries = null;
		
		try
		{
			actionsMapEntries = ((Map<Byte, Runnable>) 
				actionsMapField.get(this.wrappingActionServer)).entrySet();
			
			for (Entry<Byte, Runnable> e : actionsMapEntries)
			{				
				os.write(String.format("COMMAND '%c' : Runnable <%s>\n", e.getKey(), e.getValue().toString()).getBytes());
			}
		}
		catch(Exception ex)
		{
			this.getLogger().error("Unable to dump enabled action due to:", ex);
		}			
	}	
}
