package hk.hku.cecid.corvus.http;

import java.io.File;
import java.net.URL;

import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;
import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.AS2AdminData;
import hk.hku.cecid.corvus.ws.data.AS2PartnershipData;

/** 
 * The <code>AS2PartnershipSenderUnitTest</code> is unit test of <code> AS2PartnershipSender</code>.
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0 $STABLE$
 * @since  	H2O 0908
 * 
 * I fall back to Junit3 because this project may use under J2SE 1.4.2. 
 */
public class AS2PartnershipSenderUnitTest extends PartnershipSenderUnitTest
{	
	// Fixture loader
	private static ClassLoader 	FIXTURE_LOADER	= FixtureStore.createFixtureLoader(false, EBMSPartnershipSenderUnitTest.class);
				
	/** 
	 * The testing target which is an AS2PartnershipSender and the associated data. 
	 * The testing target variable is available in the super class.
	 */
	/* private AS2PartnershipSender 	target */ 	
	private AS2AdminData 		adata;
	private AS2PartnershipData 	pdata;		
			
	/** Initialize the test data **/
	public void initTestData()
	{
		super.initTestData();		
		// Create a EBMS administration data.
		this.adata = new AS2AdminData();
		this.adata.setUsername(USER_NAME);
		this.adata.setPassword(PASSWORD);
		//this.adata.setManagePartnershipEndpoint("http://localhost:8080/corvus/admin/as2/partnership");
		this.adata.setManagePartnershipEndpoint(TEST_ENDPOINT);	
		this.adata.setPartnershipOperation(PartnershipOp.ADD);		
		
		// Use the default partnership configuration.		
		this.pdata 	= new AS2PartnershipData();
		this.pdata.setPartnershipId("as2");
		this.pdata.setIsDisabled(false);
		this.pdata.setIsSyncReply(false);
		this.pdata.setSubject("AS2 web service client default subject");
		this.pdata.setRecipientAddress("http://127.0.0.1:8080/corvus/httpd/as2/inbound");
		this.pdata.setIsHostnameVerified(false);
		this.pdata.setReceiptAddress("http://127.0.0.1:8080/corvus/httpd/as2/inbound");
		this.pdata.setIsReceiptRequired(false);
		this.pdata.setIsOutboundSignRequired(false);		
		this.pdata.setIsOutboundEncryptRequired(false);
		this.pdata.setIsOutboundCompressRequired(false);
		this.pdata.setIsReceiptSignRequired(false);
		this.pdata.setIsInboundSignRequired(false);
		this.pdata.setIsInboundEncryptRequired(false);
		this.pdata.setRetries(3);
		this.pdata.setRetryInterval(30000);
		this.pdata.setSignAlgorithm("sha1");
		this.pdata.setEncryptAlgorithm("rc2");
		this.pdata.setMicAlgorithm("sha1");
		this.pdata.setAs2From("as2From");
		this.pdata.setAs2To("as2To");
		this.pdata.setVerifyCert(new byte[]{});
		this.pdata.setEncryptCert(new byte[]{});		
		
	}
	
	/** Initialize the test target which is a HTTP Sender. */
	public void initTestTarget() throws Exception 
	{
		URL logURL = FIXTURE_LOADER.getResource(FixtureStore.TEST_LOG);
		if (logURL == null)
			throw new NullPointerException("Missing fixture " + FixtureStore.TEST_LOG + " in the fixture path");
		
		File log = new File(logURL.getFile());
		this.testClassLogger = new FileLogger(log);		
		this.target = new AS2PartnershipSender(this.testClassLogger, adata, pdata);		
	}
}

