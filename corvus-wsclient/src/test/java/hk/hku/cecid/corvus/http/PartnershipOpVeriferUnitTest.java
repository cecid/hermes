package hk.hku.cecid.corvus.http;

import java.io.InputStream;

import junit.framework.TestCase;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;

/** 
 * The <code>PartnershipOpVeriferUnitTest</code> is unit test of <code>PartnershipOpVerifer</code>. 
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0 $STABLE
 * @since   H2O 0908
 */
public class PartnershipOpVeriferUnitTest extends TestCase 
{
	// Instance logger
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/*
	 * Since the partnership operation verifer requires Internet connectivity 
	 * and therefore i have added this dirty proxy settings here. Changed if needed.
	 */
	static {
		System.setProperty("http.proxyHost", "proxy.cs.hku.hk");
		System.setProperty("http.proxyPort", "8282");
	}
	
	// Fixture set.
	public static final String 	ADDOP_SUCCESS_SAMPLE 	= "add.success.sample.html";
	public static final String 	DELETEOP_SUCCESS_SAMPLE = "delete.success.sample.html"; 
	public static final String 	UPDATEOP_SUCCESS_SAMPLE = "update.success.sample.html";
	public static final String 	ERROR_SAMPLE_0			= "fail.sample.0.html";
	public static final String  ERROR_SAMPLE_1			= "fail.sample.1.html";
	
	// Fixture loader
	private static ClassLoader 	FIXTURE_LOADER	= FixtureStore.createFixtureLoader(false, PartnershipOpVeriferUnitTest.class);
	
	/** The testing target which is an PartnershipOpVerifer **/
	private PartnershipOpVerifer target;
	
	/** Setup the fixture. */
	public void setUp() throws Exception {
		this.initTestTarget();
		logger = LoggerFactory.getLogger(this.getName());
		logger.info(this.getName() + " Start ");
	}
		
	/** Initialize the test target which is a Partnership Operation Verifier. */
	public void initTestTarget() throws Exception {
		this.target = new PartnershipOpVerifer();
	}
	
	/** Test whether the validation process execute property for add operation executed properly. **/
	public void testValidateAddSuccess() throws Exception {
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(ADDOP_SUCCESS_SAMPLE);
		this.target.validate(ins);
	}
	
	/** Test whether the validation process execute property for delete operation executed properly. **/
	public void testValidateDeleteSuccess() throws Exception {
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(DELETEOP_SUCCESS_SAMPLE);
		this.target.validate(ins);
	}
	
	/** Test whether the validation process execute property for update operation executed properly. **/
	public void testValidateUpdateSuccess() throws Exception {
		InputStream ins = FIXTURE_LOADER.getResourceAsStream(UPDATEOP_SUCCESS_SAMPLE);
		this.target.validate(ins);
	}
	
	/** Test whether it throws exception when null stream is passed as arugment **/
	public void testValidateWithNullStream() throws Exception {
		boolean failed = false;
		try{
			this.target.validate(null);	
		}catch(Exception ex){
			failed = true;
		}
		assertTrue(failed);
	}
	
	/**  
	 * Test whether the validation process throw exception when the HTML content
	 * violate the field constraint (this case, 'Retry Interval must be integer');
	 */
	public void testValidateWithErrorSample0() throws Exception {
		this.testValidateWithError(ERROR_SAMPLE_0);		
	}
	
	/**
	 * Test whether the validation process throw exception when the HTML content
	 * violate the field constraint (this case, 'CPA ID cannot be empty');  
	 */
	public void testValidateWithErrorSample1() throws Exception {
		this.testValidateWithError(ERROR_SAMPLE_1);
	}
	
	/** Setup the fixture. */
	public void tearDown() throws Exception {			
		logger.info(this.getName() + " End ");
	}
	
	/**
	 * A helper implementation for testing error path in the validation process.
	 * 
	 * @param fixtureName The name of fixture to load  
	 */
	private void testValidateWithError(String fixtureName){
		boolean failed = false;
		try{
			InputStream ins = FIXTURE_LOADER.getResourceAsStream(fixtureName);
			this.target.validate(ins);		
		}catch(Exception ex){
			logger.info("Expected: " + ex.getMessage());
			failed = true;
		}
		assertTrue(failed);		
	}
}
