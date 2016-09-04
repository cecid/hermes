/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.test.utils;

import java.io.File;
import java.io.FilenameFilter;

import java.util.List;
import java.util.ArrayList;

import java.lang.ClassLoader;
import java.lang.reflect.Method;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * The <code>FixtureStore</code> contains a set of global method for creating class loader for loading 
 * fixture data in Test-case.
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908 
 */
public class FixtureStore 
{	
	public static final String TEST_LOG = "test.log";

	// Class Logger.
	private static final Logger clogger = LoggerFactory.getLogger("testcase.FixtureStore");
		
	// The resource base path.
	private static final String FIXTURE_BASE;

	// The internal jar filename filter.
	private static final FilenameFilter JARFILTER = new FilenameFilter()
	{
		public boolean accept(File dir, String name)
		{
			return name.toLowerCase().endsWith("jar");
		}
	};
	
	// The set of class acting as the parameters for hacking the URLClassLoader.
	private static final Class<?>[] clhackParams = new Class[]{URL.class};
		
	static
	{
		String basePath = System.getProperty("piazza.common.FIXTURE_BASE");
		// Follow the folder structure of maven
		FIXTURE_BASE = basePath == null ? "src/test/resources/" : basePath; 
	}
	
	/**
	 * Get the fixture URL from the <code>clazz</code>.
	 * <br/><br/>
	 * For example,
	 * <pre>
	 * 	package yourpackage;
	 * 
	 * 	public class foo 
	 * 	{ 	 	 
	 * 	}
	 * 
	 * 	public class fooTest
	 * 	{
	 * 		public void testFoo()
	 * 		{
	 * 			URL url = FixtureStore.getFixtureURL(foo.class); 	// By default, return <path>/res/yourpackage.foo;  			
	 * 		}
	 * 	}
	 * </pre>
	 * 
	 * @return The URL base for this fixture class.
	 */
	public static URL getFixtureURL(Class<?> clazz)
	{
		if (clazz == null)
		{
			throw new NullPointerException("Missing 'clazz' in the arguments.");
		}
		
		try 
		{
			return new File(FIXTURE_BASE + clazz.getName()).toURL();
		}
		catch(MalformedURLException muex)
		{
			return null;
		}		
	}
	
	/**
	 * Create a Fixture loader under the class <code>clazz</code>. 
	 * 
	 * @param autoJarInclude The boolean flag representing whether jar files in the fixture path are automatically included.     
	 * @param clazz The class to create the fixture loader. 
	 * @return A Fixture loader under the class <code>clazz</code>.
	 */
	public static ClassLoader createFixtureLoader(boolean autoJarInclude, Class<?> clazz)
	{
		return createFixtureLoader(autoJarInclude, new Class[]{clazz});
	}	
	
	/**
	 * Create a Fixture loader under the class <code>clazz</code>. 
	 * 
	 * @param autoJarInclude The boolean flag representing whether jar files in the fixture path are automatically included.     
	 * @param classes A set of class to create the fixture loader. 
	 * @return A Fixture loader under the class <code>clazz</code>.
	 */
	public static ClassLoader createFixtureLoader(boolean autoJarInclude, Class<?>...classes)
	{
		if (classes == null || classes.length == 0)
		{			
			return FixtureStore.class.getClassLoader();
		}

		ClassLoader combinedLoader = null;
		
		List<URL> urls = new ArrayList<URL>();		
		
		for (Class<?> clazz: classes)
		{
			try
			{
				String resFolderName = FIXTURE_BASE + clazz.getCanonicalName() + File.separator;
				File resFolder = new File(resFolderName);
				
				if (!resFolder.exists())
				{
					resFolder.mkdirs();
				}

				File logFile = new File(resFolderName + File.separator + TEST_LOG);
				if (!logFile.exists())
				{
					logFile.createNewFile();
				}

				if (autoJarInclude)
				{
					// Load all jar specified in the fixture path. 
					File[] jars = resFolder.listFiles(JARFILTER);
					
					for (File jar: jars)
					{
						urls.add(jar.toURL());
					}
				}
				
				urls.add(resFolder.toURL());
			}
			catch(Exception ex)
			{
				clogger.error("Unable to create resource loader", ex);
			}
		}
		
		// Add all URLS to the combined class loader.
		combinedLoader = new URLClassLoader
		(
			(URL[]) urls.toArray(new URL[]{}), 
			classes[0].getClassLoader()
		);
		
		return combinedLoader;
	}
		
	/**
	 * Create a class loader which has <code>old</code> as the basis, and in additional to 
	 * a set fixture path from <code>classes</code>.  
	 * 
	 * @param old
	 * @param classes
	 * @return
	 */
	public static ClassLoader createFixtureLoader(boolean autoJarInclude, ClassLoader old, Class<?>...classes)
	{
		if (old == null)
		{
			return FixtureStore.createFixtureLoader(autoJarInclude, classes);
		}
		
		if (classes == null || classes.length == 0)
		{
			return old;
		}
		
		// Create a combined loader for classes first.
		ClassLoader combinedLoader = FixtureStore.createFixtureLoader(autoJarInclude, classes);
		
		if (combinedLoader instanceof URLClassLoader)
		{
			URL [] additionClasspath = ((URLClassLoader) combinedLoader).getURLs();
			
			for (URL u : additionClasspath)
			{
				clogger.debug("Adding resource path to fixture loader {}", u.toString());
			}
			
			return new URLClassLoader(additionClasspath, old);
		}
		return old;
	}
	
	/**
	 * 
	 * @param src
	 * @param dest
	 * @return
	 * @throws Exception
	 */
	public static ClassLoader addFixtureLoaderFrom(ClassLoader src, ClassLoader dest) throws Exception 
	{
		if (src == null || dest == null)	// Null Guard.
			return null;
		if (!(src instanceof URLClassLoader) || !(dest instanceof URLClassLoader) )	// Type Guard
			return null;
		
		URLClassLoader usrc  = (URLClassLoader) src;
		URLClassLoader udest = (URLClassLoader) dest;
		
		return FixtureStore.addFixturesToLoader(udest, usrc.getURLs());
	}
	
	/**
	 * 
	 */
	public static ClassLoader addFixturesToLoader(ClassLoader loader, URL[] fixtureURL) throws Exception 
	{
		if (loader == null || fixtureURL == null)
		{
			return loader;
		}
		if (!(loader instanceof URLClassLoader))	// If it is not an instance of URLClassLoader, we can add url into it.
		{
			return loader;
		}
		
		URLClassLoader urlLoader = (URLClassLoader)loader;
		
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", clhackParams);
		method.setAccessible(true);
		for (URL u : fixtureURL)
			method.invoke(urlLoader, new Object[]{ u });		
		
		return urlLoader;
	}
}

