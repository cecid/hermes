/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.http;

import java.io.File;
import java.net.URL;

import hk.hku.cecid.piazza.commons.test.utils.FixtureStore;

import hk.hku.cecid.corvus.util.FileLogger;
import hk.hku.cecid.corvus.ws.data.EBMSAdminData;
import hk.hku.cecid.corvus.ws.data.EBMSPartnershipData;

/** 
 * The <code>EBMSPartnershipSenderUnitTest</code> is unit test of <code> EBMSPartnershipSender</code>.
 *
 * @author 	Twinsen Tsang
 * @version 1.0.0 $STABLE$
 * @since  	H2O 0908
 * 
 * I fall back to Junit3 because this project may use under J2SE 1.4.2. 
 */
public class EBMSPartnershipSenderUnitTest extends PartnershipSenderUnitTest
{	
	// Fixture loader
	private static ClassLoader 	FIXTURE_LOADER	= FixtureStore.createFixtureLoader(false, EBMSPartnershipSenderUnitTest.class);
				
	/** 
	 * The testing target which is an EBMSPartnershipSender and the associated data. 
	 * The testing target variable is available in the super class.
	 */
	/* private EBMSPartnershipSender 	target */ 	
	private EBMSAdminData 			adata;
	private EBMSPartnershipData 	pdata;		
			
	/** Initialize the test data **/
	public void initTestData()
	{
		super.initTestData();		
		// Create a EBMS administration data.
		this.adata = new EBMSAdminData();
		this.adata.setUsername(USER_NAME);
		this.adata.setPassword(PASSWORD);
		this.adata.setManagePartnershipEndpoint(TEST_ENDPOINT);
		this.adata.setPartnershipOperation(PartnershipOp.ADD);		
		
		// Use the default partnership configuration.			
		this.pdata 		= new EBMSPartnershipData();
		this.pdata.setPartnershipId		("ebms");		
		this.pdata.setCpaId				("ebmscpaid");
		this.pdata.setService			("http://127.0.0.1:8080/corvus/httpd/ebms/inbound");
		this.pdata.setAction			("action");
		this.pdata.setDisabled			("false");
		this.pdata.setSyncReplyMode		("none");
		this.pdata.setTransportEndpoint	("http://127.0.0.1:8080/corvus/httpd/ebms/inbound");
		this.pdata.setTransportProtocol	("http");
		this.pdata.setAckRequested		("never");
		this.pdata.setAckSignRequested	("never");
		this.pdata.setDupElimination	("never");
		this.pdata.setActor				("");
		this.pdata.setMessageOrder		("NotGuaranteed");
		this.pdata.setPersistDuration	("0");
		this.pdata.setRetries			(1);
		this.pdata.setRetryInterval		(30000);
		this.pdata.setSignRequested		("false");
		this.pdata.setDsAlgorithm		("");
		this.pdata.setMdAlgorithm		("");
		this.pdata.setEncryptAlgorithm	("sha1");
		this.pdata.setEncryptRequested	("false");
		this.pdata.setSignCert			(new byte[]{});
		this.pdata.setEncryptCert		(new byte[]{});
		this.pdata.setIsHostnameVerified	("false");		
		
	}
	
	/** Initialize the test target which is a HTTP Sender. */
	public void initTestTarget() throws Exception 
	{
		URL logURL = FIXTURE_LOADER.getResource(FixtureStore.TEST_LOG);
		if (logURL == null)
			throw new NullPointerException("Missing fixture " + FixtureStore.TEST_LOG + " in the fixture path");
		
		File log = new File(logURL.getFile());
		this.testClassLogger = new FileLogger(log);		
		this.target = new EBMSPartnershipSender(this.testClassLogger, adata, pdata);		
	}
}

