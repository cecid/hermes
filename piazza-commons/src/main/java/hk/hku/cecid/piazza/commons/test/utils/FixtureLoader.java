package hk.hku.cecid.piazza.commons.test.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * The <code>FixtureLoader</code> is 
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 */
public class FixtureLoader extends URLClassLoader 
{	
	// Instance Logger
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Create an instance of <code>FixtureLoader</code>.
	 * 
	 * @param urls the URLs from which to load classes and resources
	 * @param parent the parent class loader for delegation
	 */
	FixtureLoader(URL[] urls, ClassLoader parent) 
	{
		super(urls, parent);
	}
	
	/**
	 * Create an instance of <code>FixtureLoader</code>.
	 * 
	 * @param urls the URLs from which to load classes and resources
	 */
	FixtureLoader(URL[] urls) 
	{
		super(urls);
	} 
	
	/* (non-Javadoc)
	 * @see java.net.URLClassLoader#findResource(java.lang.String)
	 */
	@Override public URL findResource(String name)
	{			
		return this.findResourceImpl(name);		
	}
	
	/* (non-Javadoc)
	 * @see java.net.URLClassLoader#findResources(java.lang.String)
	 */
	@Override public Enumeration<URL> findResources(String name) throws IOException 
	{
		return this.findResourcesImpl(name);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	URL findResourceImpl(String name)
	{
		try
		{	
			URL res; ClassLoader cLoader;			
			
			// #1 Find from the context class loader.
			cLoader = Thread.currentThread().getContextClassLoader();
			if (cLoader != null && (res = cLoader.getResource(name)) != null) return res;
			
			// #2 Find from super class loader
			res = super.findResource(name);			
			if (res != null) return res;
			
			// #3 Find the resource through this class loader
			cLoader = this.getClass().getClassLoader();
			if (cLoader != null && (res = cLoader.getResource(name)) != null) return res;			
		}
		catch(Throwable t)
		{
			logger.error("Unable to load resource: " + name, t);
		}		
		return null;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	Enumeration<URL> findResourcesImpl(String name) 
	{
		try{	
			Enumeration<URL> res; ClassLoader cLoader;			
			// #1 Find from the context class loader.
			cLoader = Thread.currentThread().getContextClassLoader();
			if (cLoader != null && (res = cLoader.getResources(name)) != null) return res;
			
			// #2 Find from super class loader
			res = super.findResources(name);
			if (res != null) return res;
			
			// #3 Find the resource through this class loader
			cLoader = this.getClass().getClassLoader();
			if (cLoader != null && (res = cLoader.getResources(name)) != null) return res;			
		}
		catch(Throwable t){
			logger.error("Unable to load resource: " + name, t);
		}		
		return null;
	}
}
