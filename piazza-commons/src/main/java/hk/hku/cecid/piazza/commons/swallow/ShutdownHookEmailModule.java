/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.swallow;

import java.net.URLClassLoader;
import java.util.Properties;

import hk.hku.cecid.piazza.commons.module.Module;
import hk.hku.cecid.piazza.commons.module.ModuleException;
import hk.hku.cecid.piazza.commons.swallow.ShutdownHookEmailThread;
import hk.hku.cecid.piazza.commons.util.Instance;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

/**
 * The ShutdownHookEmailModule is a piazza common module which send email to the specified when the JVM 
 * is terminated. It is typical used to notify the operator for the termination of long-running java process 
 * like application server (for instance, Tomcat) through email.
 * 
 * Background about this: This component is emerged when project swallow requires the detection of 
 * application crashes and therefore we want a reusable generic component for doing such.
 * <br/><br/> 
 * Sample Module Descriptor
 * <pre>
 * &lt;module id="shutdown.email.module" name="Email shutdown hook module"&gt;
 *  &lt;parameters&gt;
 *   &lt;parameter name="host" value="intraflow2.cs.hku.hk"/&gt;
 *   &lt;parameter name="protocol" value="smtp"/&gt;
 *   &lt;parameter name="username" value=""/&gt;
 *   &lt;parameter name="password" value=""/&gt;
 *   &lt;parameter name="from" value="yourDaemon@cecid.hku.hk"/&gt;
 *   &lt;parameter name="to" value="yourEmailAddress"/&gt;
 *   &lt;parameter name="cc" value="yourCCEmailAddress"/&gt;
 *   &lt;parameter name="verbose" value="false"/&gt;
 *  &lt;/parameters&gt;
 * &lt;/module&gt;
 * </pre>
 * 
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0
 */
public class ShutdownHookEmailModule extends Module
{
	private Thread shutdownThread;

	/**
     * Creates a new instance of <code>ShutdownHookEmailModule</code>.
     * 
     * @param descriptorLocation the module descriptor.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
	public ShutdownHookEmailModule(String descriptorLocation, boolean shouldInitialize)
	{
		super(descriptorLocation, shouldInitialize);	
	}

	 /**
     * Creates a new instance of <code>ShutdownHookEmailModule</code>.
     * 
     * @param descriptorLocation the module descriptor.
     * @param shouldInitialize true if the module should be initialized.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
	public ShutdownHookEmailModule(String descriptorLocation, ClassLoader loader, boolean shouldInitialize)
	{
		super(descriptorLocation, loader, shouldInitialize);
	}

	/**
     * Creates a new instance of <code>ShutdownHookEmailModule</code>.
     * 
     * @param descriptorLocation the module descriptor.
     * @param loader the class loader for this module.
     * @throws ModuleException if errors encountered when loading the module descriptor.
     */
	public ShutdownHookEmailModule(String descriptorLocation, ClassLoader loader)
	{
		super(descriptorLocation, loader);
	}

	/**
     * Creates a new instance of <code>ShutdownHookEmailModule</code>.
     * 
     * @param descriptorLocation the module descriptor.
     */
	public ShutdownHookEmailModule(String descriptorLocation)
	{
		super(descriptorLocation);
	}	
	
	/**
	 * Invoked for initialization.
	 * 
	 * Wire up all property from the XML.
	 */
	@Override
	public void init()
	{
		super.init();
		
		try
		{
			synchronized(this)
			{
				if (this.shutdownThread == null)
				{
					this.shutdownThread = this.createShutdownHookWorker();
					
					/*
					 * Add it to the runtime shutdown sequence.
					 */
					Runtime.getRuntime().addShutdownHook(this.shutdownThread);
				}
			}
			
			// Old implementation
			/*
			 * Class c = Class.forName(ShutdownHookEmailThread.class.getName(), true, threadCtxClassLoader);
			 * Constructor con = c.getConstructor(String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class);
			 * Thread shutdownThread = (Thread) con.newInstance(protocol, host, username, password, from, tos, ccs, subject);
			 * shutdownThread.setContextClassLoader(threadCtxClassLoader);
			 */
		}
		catch(Throwable t)
		{
			throw new ModuleException("Unable to initialize mail alert shutdown hook", t);
		}
	}	
	
	/**
	 * A special hack for remove the shutdown hook registered during the {@link #init()} phrase
	 * of this module. This is used for testing the module only. You rarely call this in your application. 
	 */
	public synchronized void stop() 
	{
		if (this.shutdownThread != null)
		{
			try
			{
				Runtime.getRuntime().removeShutdownHook(this.shutdownThread);
				
			}
			catch(Throwable t)
			{
				// Ignore, A hook that can't be removed is because the shutdown
				// sequence is already started.
			}	
			finally
			{
				this.shutdownThread = null;
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	protected Thread getThread()
	{
		/*
		 * TODO: Share the implementation with #init.
		 */
		try
		{
			synchronized(this)
			{
				if (this.shutdownThread == null)
				{
					this.shutdownThread = this.createShutdownHookWorker();	
				}
			}
		}
		catch(Throwable t)
		{
			throw new ModuleException("Unable to retrieve mail alert shutdown hook", t);
		}
		return this.shutdownThread;
	}
	
	/**
	 * Create the thread executed during JVM shutdown. By default, it create 
	 * an new instance of ShutdownHookEmailThread associated with the property 
	 * defined in this module descriptor. 
	 * 
	 * The thread created must conform the rule specified in the Java API. For detail, read
	 * <a href="http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Runtime.html#addShutdownHook(java.lang.Thread)">here</a>
	 * 
	 * @return The thread executed during JVM shutdown
	 * @throws Throwable any kind of execution for setup the shutdown hook worker.
	 * 
	 * @{@link java.lang.Runtime#addShutdownHook(Thread)}
	 */
	@SuppressWarnings("unchecked")
	protected Thread createShutdownHookWorker() throws Throwable
	{
		Properties p = super.getParameters();
						
		final String host  	  = super.getRequiredParameter("host");		
		final String protocol = p.getProperty("protocol", "smtp");
		final String username = p.getProperty("username");
		final String password = p.getProperty("password");
		final String from     = p.getProperty("from", "commonDaemon@cecid.hku.hk");
		final String tos      = super.getRequiredParameter("to");
		final String ccs      = p.getProperty("cc");
		final String subject  = p.getProperty("subject", ShutdownHookEmailThread.DEFAULT_SHUTDOWN_MAIL_SUBJECT);
		final boolean verbose = StringUtilities.parseBoolean(p.getProperty("verbose"), false); 
		
		/*
		 * TODO: super-class provide helper method to throw if null get property method
		 */
		
		/*if (host == null || host.equals(""))
		{
			throw new ModuleException("Missing required property \"host\" in " + this.toString());
		}
		else if (tos == null || tos.equals(""))
		{
			throw new ModuleException("Missing required property \"to\" in " + this.toString());
		}*/
		
		/*
		 * In certain circumstance (mostly in servlet container), the class loader 
		 * that create this thread may reject loading / be unloaded all classes 
		 * during the JVM is terminating.
		 *  
		 * Thus we need to clone the current class loader in advance to 
		 * guarantee this thread are able to use the JAF/JavaMail or other class 
		 * when executing. This is achieved by setting the context class loader in this thread. 
		 */
		ClassLoader threadCtxClassLoader = this.copyClassLoader();
		
		/*
		 * Okay now we have cloned the class loader, then let create the shutdown hook 
		 * through reflection API so that the hook is loaded through the class loader 
		 * cloned in the previous step.
		 */
		
		Class [] argsType 	  = {String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Boolean.TYPE};
		Object[] initargsType = {protocol, host, username, password, from, tos, ccs, subject, verbose};   
				
		Instance shutdownThreadIns = new Instance(
				ShutdownHookEmailThread.class.getName(),
				threadCtxClassLoader,
				argsType,
				initargsType);
		
		Thread shutdownThread = (Thread) shutdownThreadIns.getObject();
		
		this.getLogger().debug("[SHUTDOWN] Email Hook: Delegating classloader: " + threadCtxClassLoader);
					
		/*
		 * IMPORTANT, The context class loader must match with the class loader that load the 
		 * shutdownHookEmailThread. Otherwise it cause numerous error throwing because class
		 * loading conflict (i.e. same class-name with different class loader).
		 */
		shutdownThread.setContextClassLoader(threadCtxClassLoader);
		
		return shutdownThread;
	}
	
	/*
	 * Clone a class loader from the current class loader. Return the identical
	 * if the current class loader is not a URL class loader.
	 * 
	 * TODO: re-factor to provide utility function to copy class-loader.
	 */
	private ClassLoader copyClassLoader()
	{
		ClassLoader ret = null;
		ClassLoader cl  = this.getClass().getClassLoader();
		if (cl instanceof URLClassLoader)
		{
			URLClassLoader ucl = (URLClassLoader) cl;
			ret = URLClassLoader.newInstance(ucl.getURLs(), ucl.getParent());				
		}
		else
		{
			ret = cl;
		}
		return ret;
		
		// Old implementation
		/*StringBuilder sb = new StringBuilder();
		sb.append('.').append(File.pathSeparatorChar);
		
		ClassLoader cl  = this.getClass().getClassLoader();
		if (cl instanceof URLClassLoader)
		{
			URLClassLoader ucl   = (URLClassLoader) cl;
			URL[] urlclasspath   = ucl.getURLs();
			threadCtxClassLoader = URLClassLoader.newInstance(urlclasspath, ucl.getParent());
			
			for (URL u : urlclasspath)
			{
				sb.append(u.getFile()).append(File.pathSeparatorChar);	
			}
		}*/				
	}
}		

/*
 * Keep the POC development until the module is claimed as fully reliable.
 */

/*	
	public void run()
	{
		try
		{				
			ProcessBuilder pb = new ProcessBuilder("java", "-cp", this.processClasspath, ShutdownHookEmailThread.class.getName());
			Map<String, String> env = pb.environment();
			env.put("SHOOK_USERNAME", username);
			env.put("SHOOK_PASSWORD", password);
			env.put("SHOOK_FROM"    , from);
			env.put("SHOOK_TO"      , tos);
			env.put("SHOOK_CC"      , ccs);
			env.put("SHOOK_SUBJECT" , subject);
			env.put("SHOOK_PROTOCOL", protocol);
			env.put("SHOOK_HOST"    , host);			
			pb.redirectErrorStream(true);
			
			try
			{
				Process p = pb.start();
				byte[] bs = IOHandler.readBytes(p.getInputStream());
				System.out.println(new String(bs, "UTF-8"));
				p.waitFor();
				p.destroy();
			}
			catch(IOException ioex)
			{
				ioex.printStackTrace();
			}
			catch(InterruptedException inex)
			{
				inex.printStackTrace();
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
};*/

/*System.out.println("Installed Signal Handler..");		
SignalHandlerExample.install("TERM");
SignalHandlerExample.install("INT");
SignalHandlerExample.install("ABRT");        
System.out.println("Installed Signal Handler Done..");*/
//Runtime.getRuntime().addShutdownHook(shutdownThread);
//System.exit(0);
//SignalHandlerExample.install("QUIT");		
//System.out.println("Register shutdown hook");		
/*
try
{
	Class c2 = Class.forName("hk.hku.cecid.piazza.commons.util.DateUtil", true, ret);
	System.out.printf("c2=%s\n", c2.newInstance().getClass().getClassLoader());
	
	//System.out.println(ShutdownHookEmailThread.class.getName());
	//System.out.println(ret);
	Class c = ret.loadClass("hk.hku.cecid.piazza.commons.module.ShutdownHookEmailModule$ShutdownHookEmailThread");
	System.out.println(c.getClassLoader());
	
	Instance ins = new Instance(
			"hk.hku.cecid.piazza.commons.module.ShutdownHookEmailModule$ShutdownHookEmailThread", ret,
			new Class[]{String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class},
			new Object[]{protocol, host, username, password, from, tos, ccs, subject});
		
	Thread t = (Thread) ins.getObject();
	System.out.println(t.getClass().getClassLoader());
	
	//Runtime.getRuntime().addShutdownHook(t);
}
catch(Throwable t)
{
	t.printStackTrace();
}		
//new ShutdownHookEmailThread(protocol, host, username, password, from, tos, ccs, subject)
*/

