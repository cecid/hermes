package hk.hku.cecid.piazza.commons.test;

import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import org.junit.Before;
import org.junit.After;

/** 
 * The <code>UnitTest</code> is top level class for performing unit-test. 
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 */
public abstract class UnitTest<T> 
{
	// Instance logger.
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// The resource loader for this class, For detail, read 
	protected ClassLoader FIXTURE_LOADER;
	
	// Mock context
	protected Mockery mockContext;
	
	// 
	private boolean mockRequired;
	
	// The testing target.
	protected T target;
		
	private List<DependencyEntry> dependencies = new ArrayList<DependencyEntry>();
	
	private static class DependencyEntry
	{
		/**
		 * @param fixtureOnly
		 * @param testClass
		 */
		public DependencyEntry(UnitTest<?> testClass, boolean fixtureOnly)
		{
			this.fixtureOnly = fixtureOnly;
			this.testClass = testClass;
		}
		
		private boolean fixtureOnly;
		private UnitTest<?> testClass;
		
		public boolean isFixtureOnly()
		{
			return fixtureOnly;
		}
		
		public void setFixtureOnly(boolean fixtureOnly)
		{
			this.fixtureOnly = fixtureOnly;
		}
		
		public UnitTest<?> getTestClass()
		{
			return testClass;
		}
		
		public void setTestClass(UnitTest<?> testClass)
		{
			if (testClass == null)
			{
				throw new NullPointerException("Missing 'testClass' in the arugments.");
			}
			this.testClass = testClass;
		}

		
	}
	
	/**
	 * Create an instance of <code>UnitTest</code>.
	 */
	public UnitTest()
	{
		this(true);
	}
	
	/**
	 * Create an instance of <code>UnitTest</code>.
	 * 
	 * @param noMocking
	 */
	public UnitTest(boolean noMocking)
	{
		this.mockRequired = noMocking;
	}
			
	/** 
	 * @return Get the testing target.
	 */
	public T getTestingTarget()
	{
		return this.target;
	}
	
	/**
	 * Compatible to JUnit3-style.
	 */
	@Before public void setUp() throws Exception
	{		
		this.initTestDependency();
		
		for (DependencyEntry e: dependencies)
		{
			if (!e.isFixtureOnly())
			{
				e.getTestClass().setUp();	// Setup the test dependency.
			}
		}
		
		this.initFixtureLoader();		
		
		if (this.mockRequired)
		{
			this.initTestMockObjects();			
		}
				
		this.initTestTarget();				
		this.initTestDependencyInjection();
		
		
	}
	
	/**
	 * Initialize all test-class dependency used for testing.
	 */
	public void initTestDependency() throws Exception 
	{
		this.logger.debug("Initialize Test-case dependency");		
	}
	
	/**
	 * Initialize the resource class loader for loading resource at test-case class folders under res/.
	 */
	public void initFixtureLoader() throws Exception 
	{		
		this.logger.debug("Initialize Fixture Loader");
		
		Class<?>[] dependenciesClass = new Class[dependencies.size()+1];
		Class<?> testClass = null;
		int i = 0;
		
		for (DependencyEntry e: dependencies)
		{		
			testClass = e.getTestClass().getClass();
			dependenciesClass[i++] = testClass;
			this.logger.debug("Loaded external fixture class : {}", testClass.getName());
		}
		
		Class<?> thisClass = this.getClass();		
		
		if (thisClass.isMemberClass() || thisClass.isLocalClass())
		{			
			thisClass = thisClass.getEnclosingClass();
		}
		
		dependenciesClass[i] = thisClass;
		this.logger.debug("Loaded self fixture class : {}", thisClass);
		
		FIXTURE_LOADER = FixtureStore.createFixtureLoader(false, dependenciesClass);
	}
		
	/**
	 * Initialize the mock object used in the test-case.
	 */		
	public void initTestMockObjects() throws Exception 
	{
		this.logger.debug("Initialize Mock objects");
		
		mockContext = new Mockery()
		{
			{
				// So that test-case can mock abstract class.
				setImposteriser(ClassImposteriser.INSTANCE);
			}			
		};
	}	
	
	/**
	 * Initialize the test target for this test-case.
	 */
	public void initTestTarget() throws Exception 
	{
		this.logger.debug("Initialize Testing Target");
	}

	/**
	 * Initialize the testing dependency injection for this test-case
	 */
	public void initTestDependencyInjection() throws Exception {};		
	
	/**
	 * Add a testClass dependency to this UnitTest.
	 * 
	 * @param testClass 
	 * @param fixtureOnly
	 */
	public void addTestDependency(UnitTest<?> testClass, boolean fixtureOnly)
	{
		this.logger.debug("Adding Test-case dependency {}", testClass.getClass());				
		this.dependencies.add(new DependencyEntry(testClass, fixtureOnly));
	}
	
	public <C extends UnitTest<?>> void addTestDependency(Class<C> testClass, boolean fixtureOnly)
	{
		if (testClass == null)
		{
			throw new NullPointerException("Missing 'testClass' in the arguments");
		}
		
		this.logger.debug("Adding Test-case dependency {}", testClass);
		
		try
		{
			this.dependencies.add(new DependencyEntry(testClass.newInstance(), fixtureOnly));
		}
		catch(Throwable t)
		{
			this.logger.error("Unable to create test dependency", t);
		}
	}
	
	/**
	 * 
	 * 
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends UnitTest<?>> T getTestDependency(Class<T> testClass) 
	{
		if (testClass == null)
		{
			throw new NullPointerException("Missing 'testClass' in the arguments");
		}
		
		for (DependencyEntry e: dependencies)
		{
			if (testClass.isAssignableFrom(e.getTestClass().getClass()))
			{			
				return (T) e.getTestClass(); 
			}		
		}		
		return null;
	}
	
	/**
	 * Check the mock whether the expectations is correct or not. 
	 */
	@After public void checkMonk()
	{
		if (mockContext != null) 
		{
			mockContext.assertIsSatisfied();
		}
	}
	
	/**
	 * Tear-down all resource loaded.
	 */
	@After public void tearDown() throws Exception
	{
		for (DependencyEntry e: dependencies)
		{
			if (!e.isFixtureOnly())
			{
				e.getTestClass().tearDown();
			}
		}
	}
}
