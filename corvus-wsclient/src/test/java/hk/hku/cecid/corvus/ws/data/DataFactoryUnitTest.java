/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws.data;

import java.net.URL;
import java.util.Map;
import java.io.UnsupportedEncodingException;

import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;

import junit.framework.TestCase;

import org.junit.Assert;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import hk.hku.cecid.piazza.commons.util.PropertyTree;

/** 
 * The <code>DataFactoryUnitTest</code> is unit test of <code>DataFactory</code>. 
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 */
// TODO: Add negative test-case
public class DataFactoryUnitTest extends TestCase 
{
	// Instance logger
	final Logger logger = LoggerFactory.getLogger(this.getClass()); 
	
	// Class loader for loading fixture data
	private static ClassLoader FIXTURE_LOADER = FixtureStore.createFixtureLoader(false, DataFactoryUnitTest.class);
	
	// Fixture name.
	
	public static final String AS2_ADMIN_DATA_SAMPLE0 	= "as2-admin-request.xml";
	
	public static final String AS2_MESSAGE_DATA_SAMPLE0	= "as2-request-load.xml";
	
	public static final String AS2_MESSAGE_DATA_STORE0  = "as2-request-store.xml";
	
	/**
	 * This is the fixture name for testing the reading capabilities for AS2Partnership in DataFactory. 
	 */
	public static final String AS2_PARTNERSHIP_DATA_LOAD0 	= "as2-partnership-load.xml";
	
	/**
	 * This is the fixture name for testing the storing capabilities for DataFactory. 
	 * This fixture does not contains any data initially. 
	 */
	public static final String AS2_PARTNERSHIP_DATA_STORE0 	= "as2-partnership-store.xml";
	
	
	public static final String EBMS_ADMIN_DATA_SAMPLE0	 = "ebms-admin-request.xml";
	
	public static final String EBMS_MESSAGE_DATA_SAMPLE0 = "ebms-request-load.xml";
	
	
	public static final String EBMS_MESSAGE_DATA_STORE0  = "ebms-request-store.xml";		
		
 
	/**
	 * This is the fixture name for testing the reading capabilities for EBMSPartnership in DataFactory. 
	 */
	public static final String EBMS_PARTNERSHIP_DATA_LOAD0 	= "ebms-partnership-load.xml";
	
	/**
	 * This is the fixture name for testing the storing capabilities for DataFactory. 
	 * This fixture does not contains any data initially. 
	 */
	public static final String EBMS_PARTNERSHIP_DATA_STORE0 = "ebms-partnership-store.xml";
	
	
	public static final String EBMS_CONFIG_DATA_SAMPLE0	= "ebms-config-request.xml";
	
	
	public static final String EBMS_HISTORY_QUERY_DATA_STORE0  = "ebms-history-query-request.xml";
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception 
	{
		super.setUp();		
		this.logger.info("------- {} START ------", this.getName());
	}
	

	public void testCreateAS2MessageData() throws Exception
	{
		
	}
	
	/** Test whether the DataFactory able to store AS2 Message Data to the fixture **/
	public void testStoreAS2MessageData() throws Exception
	{
		DataFactory df = DataFactory.getInstance();
		AS2MessageData d = new AS2MessageData();
		
		d.setSendEndpoint("http://localhost:8080/corvus/httpd/as2/sender");
		d.setRecvEndpoint("http://localhost:8080/corvus/httpd/as2/receiver");
		d.setRecvlistEndpoint("http://localhost:8080/corvus/httpd/as2/receiver_list");
		d.setType("xml");
		d.setMessageIdForReceive("");
		
		// Use default parameter for storing.		
		URL storeURL = FIXTURE_LOADER.getResource(AS2_MESSAGE_DATA_STORE0);
		
		// Test method.
		df.storeAS2MessageDataToXML(d, storeURL);
		
		// Assertion 
		PropertyTree assertionTree = new PropertyTree(storeURL);
		
		// All key prefix.
		final String[]   xPathPrefix = { AS2MessageData.CONFIG_PREFIX, AS2MessageData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet 	 = { AS2MessageData.CONFIG_KEY_SET,AS2MessageData.PARAM_KEY_SET };
		
		this.assertData(d, assertionTree, xPathPrefix, keySet);
	}
	
	/** Test whether the DataFactory able to store EBMS Message Data to the fixture **/
	public void testStoreEBMSMessageData() throws Exception
	{
		DataFactory df = DataFactory.getInstance();
		EBMSMessageData d = new EBMSMessageData();
		
		d.setSendEndpoint("http://localhost:8080/corvus/httpd/ebms/sender");
		d.setRecvEndpoint("http://localhost:8080/corvus/httpd/ebms/receiver");
		d.setRecvlistEndpoint("http://localhost:8080/corvus/httpd/ebms/receiver_list");
		d.setConversationId("convId");
		d.setFromPartyId("fromPartyId");
		d.setFromPartyType("fromPartyType");
		d.setToPartyId("toPartyId");
		d.setToPartyType("toPartyType");
		d.setRefToMessageId("");
		d.setServiceType("");
		d.setMessageIdForReceive("");
		
		// Use default parameter for storing.		
		URL storeURL = FIXTURE_LOADER.getResource(EBMS_MESSAGE_DATA_STORE0);
		
		// Test method.
		df.storeEBMSMessageDataToXML(d, storeURL);
		
		// Assertion 
		PropertyTree assertionTree = new PropertyTree(storeURL);
		
		// All key prefix.
		final String[]   xPathPrefix = { EBMSMessageData.CONFIG_PREFIX, EBMSMessageData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet 	 = { EBMSMessageData.CONFIG_KEY_SET,EBMSMessageData.PARAM_KEY_SET};		
		
		this.assertData(d, assertionTree, xPathPrefix, keySet);
	}
	
	/** Test whether the DataFactory able to create EBMS Partnership Data from the fixture **/
	public void testCreateAS2PartnershipData() throws Exception
	{
		DataFactory df = DataFactory.getInstance();
		PropertyTree t = this.getFixtureAsTree(AS2_PARTNERSHIP_DATA_LOAD0);
		
		AS2PartnershipData d = df.createAS2PartnershipFromXML(t);
		
		// All key prefix.
		final String[]   xPathPrefix = { AS2PartnershipData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet 	 = { AS2PartnershipData.PARAM_KEY_SET };
		
		this.assertData(d, t, xPathPrefix, keySet);
		
		// Assert data field which data-type is not String.
		String encryptCert = new String(d.getEncryptCert(), "UTF-8");
		assertEquals(encryptCert, "I am testing load cert");
		String verifyCert  = new String(d.getVerifyCert(), "UTF-8");
		assertEquals(verifyCert, "I am verifying load cert");
	}
	
	/** Test whether the DataFactory able to store AS2 Partnership Data to the fixture **/
	public void testStoreAS2PartnershipData() throws Exception
	{
		DataFactory df = DataFactory.getInstance();
		AS2PartnershipData d = new AS2PartnershipData();
		
		d.setPartnershipId("as2");
		d.setIsDisabled(false);
		d.setIsSyncReply(false);
		d.setSubject("AS2 web service client default subject");
		d.setRecipientAddress("http://127.0.0.1:8080/corvus/httpd/as2/inbound");
		d.setIsHostnameVerified(false);
		d.setReceiptAddress("http://127.0.0.1:8080/corvus/httpd/as2/inbound");
		d.setIsReceiptRequired(false);
		d.setIsOutboundSignRequired(false);		
		d.setIsOutboundEncryptRequired(false);
		d.setIsOutboundCompressRequired(false);
		d.setIsReceiptSignRequired(false);
		d.setIsInboundSignRequired(false);
		d.setIsInboundEncryptRequired(false);
		d.setRetries(3);
		d.setRetryInterval(30000);
		d.setSignAlgorithm("sha1");
		d.setEncryptAlgorithm("rc2");
		d.setMicAlgorithm("sha1");
		d.setAs2From("as2From");
		d.setAs2To("as2To");
		d.setVerifyCert(new byte[]{});
		d.setEncryptCert(new byte[]{});
		
		// Use default parameter for storing.		
		URL storeURL = FIXTURE_LOADER.getResource(AS2_PARTNERSHIP_DATA_STORE0);
		
		/*
		 * We want to test if it is able to convert data-type other than String. 
		 */  
		d.setEncryptCert("I am testing cert".getBytes());
		d.setVerifyCert	("I am verifying cert".getBytes());
		
		// Test method.
		df.storeAS2PartnershipFromXML(d, storeURL);
		
		// Assertion 
		PropertyTree assertionTree = new PropertyTree(storeURL);
		
		// All key prefix.
		final String[]   xPathPrefix = { AS2PartnershipData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet 	 = { AS2PartnershipData.PARAM_KEY_SET };
		
		this.assertData(d, assertionTree, xPathPrefix, keySet);
	}
	
	/** Test whether the DataFactory able to create EBMS Partnership Data from the fixture **/
	public void testCreateEBMSPartnershipData() throws Exception
	{
		DataFactory df = DataFactory.getInstance();
		PropertyTree t = this.getFixtureAsTree(EBMS_PARTNERSHIP_DATA_LOAD0);
		
		EBMSPartnershipData d = df.createEBMSPartnershipFromXML(t);
		
		// All key prefix.
		final String[]   xPathPrefix = { EBMSPartnershipData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet 	 = { EBMSPartnershipData.PARAM_KEY_SET };
		
		this.assertData(d, t, xPathPrefix, keySet);
		
		// Assert data field which data-type is not String.
		String cert = new String(d.getEncryptCert(), "UTF-8");
		assertEquals(cert, "I am testing load cert");
	}
	
	/** Test whether the DataFactory able to store EBMS Partnership Data to the fixture **/
	public void testStoreEBMSPartnershipData() throws Exception
	{
		DataFactory df = DataFactory.getInstance();
		EBMSPartnershipData d = new EBMSPartnershipData();
		
		d.setPartnershipId		("ebms");
		d.setCpaId				("ebmscpaid");
		d.setService			("http://127.0.0.1:8080/corvus/httpd/ebms/inbound");
		d.setAction				("action");
		d.setDisabled			("false");
		d.setSyncReplyMode		("none");
		d.setTransportEndpoint	("http://127.0.0.1:8080/corvus/httpd/ebms/inbound");
		d.setTransportProtocol	("http");
		d.setAckRequested		("never");
		d.setAckSignRequested	("never");
		d.setDupElimination		("never");
		d.setActor				("");
		d.setMessageOrder		("NotGuaranteed");
		d.setPersistDuration	("0");
		d.setRetries			(1);
		d.setRetryInterval		(30000);
		d.setSignRequested		("false");
		d.setDsAlgorithm		("");
		d.setMdAlgorithm		("");
		d.setEncryptAlgorithm	("sha1");
		d.setEncryptRequested	("false");
		d.setSignCert			(new byte[]{});
		d.setEncryptCert		(new byte[]{});
		d.setIsHostnameVerified	("false");
		
		// Use default parameter for storing.		
		URL storeURL = FIXTURE_LOADER.getResource(EBMS_PARTNERSHIP_DATA_STORE0);
		
		/*
		 * We want to test if it is able to convert data-type other than String. 
		 */  
		d.setEncryptCert("I am testing cert".getBytes());		
		
		// Test method.
		df.storeEBMSPartnershipFromXML(d, storeURL);
		
		// Assertion 
		PropertyTree assertionTree = new PropertyTree(storeURL);
		
		// All key prefix.
		final String[]   xPathPrefix = { EBMSPartnershipData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet 	 = { EBMSPartnershipData.PARAM_KEY_SET };
		
		this.assertData(d, assertionTree, xPathPrefix, keySet);
	}
	
	/** Test whether the DataFactory able to load AS2 Administrator Data from the fixture **/
	public void testCreateAS2AdminData() throws Exception 
	{	
		DataFactory df = DataFactory.getInstance();
		AS2AdminData aData = df.createAS2AdminDataFromXML(this.getFixtureAsTree(AS2_ADMIN_DATA_SAMPLE0));
		
		// Fixture dependent assertion
		assertEquals("as2Testname"		, aData.getUsername());
		assertEquals("as2Testpassword"	, new String(aData.getPassword()));
		assertEquals("http://as2Test:8080/corvus/admin/as2/partnership"	, aData.getManagePartnershipEndpoint());
		assertEquals("http://as2Test:8080/corvus/admin/as2/repository"	, aData.getEnvelopQueryEndpoint());
	}
	
	/** Test whether the DataFactory able to load EBMS Administrator Data from the fixture **/
	public void testCreateEBMSAdminData() throws Exception
	{
		DataFactory df = DataFactory.getInstance();
		EBMSAdminData aData = df.createEBMSAdminDataFromXML(this.getFixtureAsTree(EBMS_ADMIN_DATA_SAMPLE0));
		
		// Fixture dependent assertion
		assertEquals("ebmsTestname"		, aData.getUsername());
		assertEquals("ebmsTestpassword"	, new String(aData.getPassword()));
		assertEquals("http://ebmsTest:8080/corvus/admin/ebms/partnership" 	, aData.getManagePartnershipEndpoint());
		assertEquals("http://ebmsTest:8080/corvus/admin/ebms/repository"	, aData.getEnvelopQueryEndpoint());
		assertEquals(0, aData.getPartnershipOperation());
		assertEquals("test-ebms-message-id", aData.getMessageIdCriteria());
		assertEquals("INBOX", aData.getMessageBoxCriteria());
	}
	
	public void testCreateEBMSConfigData() throws Exception
	{
		DataFactory df = DataFactory.getInstance();
		EBMSAdminData aData = df.createEBMSAdminDataFromXML(this.getFixtureAsTree(EBMS_CONFIG_DATA_SAMPLE0));
		// Fixture dependent assertion.
		//TODO:
	}
	
	public void testCcreateEbmsMessageHistoryQueryDataFromXML ()throws Exception
	{
		PropertyTree props = 
			new PropertyTree(
					FIXTURE_LOADER.getResourceAsStream(EBMS_HISTORY_QUERY_DATA_STORE0));
		
		DataFactory df = DataFactory.getInstance();
		EBMSMessageHistoryRequestData actualData =
						df.createEbmsMessageHistoryQueryDataFromXML(props);
		Assert.assertTrue("%localhost%".equals(actualData.getMessageId()));
		Assert.assertTrue("*box".equals(actualData.getMessageBox()));
		Assert.assertTrue("   ".equals(actualData.getConversationId()));
		Assert.assertTrue("$-_+*^()!?.,".equals(actualData.getCpaId()));
		Assert.assertTrue("1234567890".equals(actualData.getAction()));
		Assert.assertTrue("".equals(actualData.getStatus()));
		Assert.assertNull(actualData.getService());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		this.logger.info("------- {}  END  ------", this.getName());
	}

	/**
	 * The helper method for loading the fixture from the class loader and transform 
	 * to PropertyTree.
	 */ 
	private PropertyTree getFixtureAsTree(String fixtureName) throws Exception
	{
		URL u = FIXTURE_LOADER.getResource(fixtureName);
		if (u == null)
			throw new NullPointerException("Missing resource " + fixtureName + " in the classPath.");
		return new PropertyTree(u);
	}
	
	/**
	 * Assert whether the data from <code>d</code> and the XML tree <code>expectedTree</code> contains
	 * same number of fields and content under <code>xPathPrefix</code>.
	 * 
	 * @param d				The KVPairData to assert with the <code>expectedTree</code>. 	 
	 * @param expectedTree 	The XML Tree to assert with the <code>d</code>.
	 * @param xPathPrefix	An array contains the XPath prefix used for extracting the data in the node.
	 * @param keySet		An array contains the data key for extracting the data in the KVPairData.
	 */
	private void assertData(KVPairData d, PropertyTree expectedTree, String[] xPathPrefix, String[][] keySet)
		throws UnsupportedEncodingException 
	{
		int len;
		String dataValue;
		Object expectedDataValue;
		
		Map props = d.getProperties();		
		// Iterate all key and set the properties.
		for (int i = 0; i < keySet.length; i++)
		{
			len = keySet[i].length;			
			for (int j = 0; j < len; j++)
			{
				dataValue 			= expectedTree.getProperty(xPathPrefix[i] + "/" + keySet[i][j]);
				expectedDataValue	= props.get(keySet[i][j]);
				
				if (expectedDataValue instanceof byte[])
				{
					expectedDataValue = new String((byte[])expectedDataValue, "UTF-8");
				}
				assertEquals(keySet[i][j] + " does not match ", dataValue, expectedDataValue);			
				logger.info("Data {} with value {} asserted successfully.", keySet[i][j], dataValue);
			}				
		}					
	}
}
