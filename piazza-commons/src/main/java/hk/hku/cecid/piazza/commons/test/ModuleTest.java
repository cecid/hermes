package hk.hku.cecid.piazza.commons.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.MessageFormat;

import org.junit.Assert;

import hk.hku.cecid.piazza.commons.module.Module;
import hk.hku.cecid.piazza.commons.util.Instance;

public abstract class ModuleTest<T extends Module> extends UnitTest<T> 
{
	public static final String NO_MODULE_DESCRIPTOR = 
		"No module descriptor defined (null string), Please return the module descriptor URL in #getModuleDescriptor().";
	
	public static final String MISSING_MODULE_DESCRITOR = 
		"Missing module descriptor \"{0}\" in the classpath, Please put the module descriptor inside FIXTURE directory.";
	
	/**
	 * Create an instance of <code>ModuleTest</code>. By default, it disable JMOCK features for reducing dependency. 
	 */
	public ModuleTest()
	{
		super(false);
	}
	
	/**
	 * Create an instance of <code>DAOTest</code>.
	 * 
	 * @param noMocking the flag representing the test requires object mocking or not ?
	 */
	public ModuleTest(boolean noMocking)
	{
		super(noMocking);
	}
	
	public abstract String getModuleDescription();
	
	public abstract boolean initAtOnce();
	
	/**
	 * Initialize the test target for this test-case.
	 * <br/><br/>
	 */	
	@SuppressWarnings("unchecked")
	public synchronized void initTestTarget() throws Exception
	{
		// Get the parameter type.
		Class searchClass	= this.getClass();
		Class moduleClass	= null;
		
		/*
		 * TODO: re-factor (extract as a util function)
		 */
		while (true)
		{
			Type  type = searchClass.getGenericSuperclass();
			Class typeClass = type.getClass();
			
			if (ParameterizedType.class.isAssignableFrom(typeClass))
			{
				Type [] allTypes = ((ParameterizedType)type).getActualTypeArguments();
				
				if (allTypes.length < 1)
				{
					throw new IllegalArgumentException("Missing module type in the generic parameter type.");
				}			
				else
				{
					moduleClass = (Class)allTypes[0];
					break;
				}
			}
			else if (Class.class.isAssignableFrom(typeClass))
			{
				searchClass = (Class) type;
			}			
		}
		
		logger.info("Using Module-class : {}", moduleClass);
		
		String mdesc = this.getModuleDescription();

		Assert.assertNotNull(NO_MODULE_DESCRIPTOR, mdesc);
				
		URL mdescURL = this.FIXTURE_LOADER.getResource(mdesc);
		
		Assert.assertNotNull(MessageFormat.format(MISSING_MODULE_DESCRITOR, mdesc), mdescURL);
				
		Instance instance = new Instance( 
			moduleClass.getName(), 
			this.FIXTURE_LOADER, 
			new Class[]  { String.class, ClassLoader.class, Boolean.TYPE }, 
			new Object[] { mdescURL.getFile(), this.FIXTURE_LOADER, Boolean.FALSE }
		);            
		
		this.target			 = (T) instance.getObject();
		
		if (this.initAtOnce())
		{
			this.target.init();
		}
	}
}
