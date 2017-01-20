package hk.hku.cecid.corvus.partnership;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DAOFactory;

import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

/**
 * The <code>EbmsPartnership</code> is the utilities for maintaining the partnership
 * of EbMS. In current version, it support addition or deletion of the partnership.
 * 
 * @author kochiu, Twinsen Tsang (modifiers)
 * 
 * @see #createEbmsPartnership(String)
 * @see #removeEbmsPartnership(String)
 */
public class EbmsPartnership {

	/**
	 * The entry point for CLI.
	 * 
	 * @param args 
	 * 			The arguments have two parametes. The first one is the partnership maintenance
	 *			which is either "-a" (add) or "-d" (delete). The second one is the 
	 *			xml file containing the partnership information. They are located 
	 *			at the "conf/ebms.xml" relative to the program folders.			 			 				    			
	 */
	public static void main(String[] args){
		if (args.length < 2) { printUsage(); return; }

		String option = args[0];
		String xmlFile = args[1];
		try {
			if ("-a".equals(option)) {
				boolean result = createEbmsPartnership(xmlFile);
				System.out.println( result ? "Added partnership successfully." : "Cannot add partnership.");
			} else if ("-d".equals(option)){
				boolean result = removeEbmsPartnership(xmlFile);
				System.out.println( result ? "Removed partnership successfully." : "Cannont remove partnership.");
			} else {
				printUsage();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private static void printUsage() {
		System.out.println("Usage : java EbmsPartnership -options <xml_file_path>");
		System.out.println("Options :");
		System.out.println("\t -a \t Add partnership");
		System.out.println("\t -d \t Delete partnership");
	}

	/**
	 * Create a EbMS partnership with the configuration defined in the <code>XMLFile</code>.
	 * 
	 * @param xmlFile
	 * 			The partnership XML instance file. It is located at "/data/ebms.xml".
	 * @return
	 * 			true if the removal operation ran successfully.
	 * @throws DAOException
	 * 			Error in persistence connectivity.
	 * @throws DocumentException
	 * 			Error in reading the parameter in the <code>xmlFile</code>.
	 * @throws SAXException
	 * 			Error in parsing the <code>xmlFile</code>.
	 * @throws IOException	 
	 */	
	public static boolean createEbmsPartnership(String xmlFile)
		throws DAOException, DocumentException, SAXException, IOException 
	{
		Element e = getRootElementFromFile(xmlFile);

		// Checking whether there is any field violating the EbMS specification.
		List errors = fieldChecking(e);
		if (errors.size() > 0){
			for (int i = 0; i < errors.size(); i++)
				System.out.println(errors.get(i));
			return false;
		}

		/*
		 * Create an DAOFactory for creating EbMS partnership, you may refer
		 * to the configuration file for the setting of "ebms-daofactory" at
		 * "conf/partnership.ebms-dao.xml"
		 */ 
		DAOFactory ebmsDAOFactory = (DAOFactory) Sys.main.getComponent("ebms-daofactory");
		
		// Create the EbMS partnership DAO and DVO.
		PartnershipDAO partnershipDao = (PartnershipDAO) ebmsDAOFactory.createDAO(PartnershipDAO.class);		
		PartnershipDVO dvo = (PartnershipDVO) partnershipDao.createDVO();
		
		String pID = e.attributeValue("id");		
		dvo.setPartnershipId(pID);
		
		// There is the same database record found in the persistence, warning the user for
		// overwriting the record.
		if (partnershipDao.retrieve(dvo)){	
			System.out.println("Existing partnership with id: " + pID + " found. Removing it.");
			partnershipDao.remove(dvo);
			// create again.
			dvo = (PartnershipDVO) partnershipDao.createDVO();
		}

		Element params 	= e.element("parameters");
		String endpoint = params.elementText("transportEndpoint");
		String protocol = endpoint.substring(0, endpoint.indexOf(":"));
		
		dvo.setPartnershipId(pID);		
		dvo.setCpaId	(params.elementText("cpaId"));
		dvo.setService	(params.elementText("service"));
		dvo.setAction	(params.elementText("action"));				
		dvo.setTransportProtocol	(protocol);		
		dvo.setTransportEndpoint	(endpoint);
		dvo.setIsHostnameVerified	(params.elementText("hostnameVerfiedInSsl"));
		
		// The possible value of "syncReplyMode" is 
		// 	"mshSignalsOnly" (same connection response)
		//  "none"			 (different connection response)
		dvo.setSyncReplyMode("true".equalsIgnoreCase(params
			.elementText("syncReplyMode")) ? "mshSignalsOnly" : "none");
		
		// The possible value of "acknowledgementRequested" is 
		//  "always" (ACK is requested) 
		//  "never"  (ACK is not requested)
		dvo.setAckRequested("true".equalsIgnoreCase(params
			.elementText("acknowledgementRequested")) ? "always" : "never");
		
		// The possible value of "acknowledgementSignedRequest" is
		//  "always" (ACK is required to sign before send back to sender)
		//  "never"  (ACK is not required to sign)
		dvo.setAckSignRequested("true".equalsIgnoreCase(params
			.elementText("acknowledgementSignedRequest")) ? "always" : "never");
		
		// The possible value of "duplicateElimination" is
		//  "always" (eliminate duplicate message)
		//  "never"  (does not eliminate duplicate message)
		dvo.setDupElimination("true".equalsIgnoreCase(params
			.elementText("duplicateElimination")) ? "always" : "never");
			
		// The possible value of "messageOrder" is
		//  "Guaranteed" (guaranteed the message(s) sent is in order)
		//  NotGuaranteed" (vice versa)
		dvo.setMessageOrder("true".equalsIgnoreCase(params
			.elementText("messageOrder")) ? "Guaranteed" : "NotGuaranteed");
		
		// The flag whether digital signatures is required for outgoing delivery.
		dvo.setSignRequested	(params.elementText("signingRequired"));
		
		// The flag whether encryption is required for outgoing delivery (SMTP only).		
		dvo.setEncryptRequested	(params.elementText("encryptionRequired"));
		
		dvo.setSignCert		(Utilities.loadCert(params.elementText("certificateForVerification")));		
		dvo.setEncryptCert	(Utilities.loadCert(params.elementText("certificateForEncryption")));
		
		// General delivery information
		dvo.setRetries		(Integer.parseInt(params.elementText("maximumRetries")));
		dvo.setRetryInterval(Integer.parseInt(params.elementText("retryInterval")));				
		dvo.setDisabled		("false");
		
		// Create the partnership.
		partnershipDao.create(dvo);
		return true;
	}

	/**
	 * Checking whether there is any field in the partnership xml file 
	 * violating the EbMS specification.
	 * 
	 * @param root The root element in the partnership xml file.
	 * @return A list of string containing the error description. Empty list if no error has been found.
	 */
	private static List fieldChecking(Element root) {
		Element params = root.element("parameters");
		List errors = new ArrayList();
				
		// When digital signatures is required for acknowledgment, the request of acknowledgment MUST
		// be set. Also it is required to provide the cerificate for verification the acknolwedgment. 
		if (Utilities.getBooleanValue(params, "acknowledgementSignedRequest")){
			if (!Utilities.getBooleanValue(params, "acknowledgementRequested")){
				errors.add("Acknowledgement Requested must be set to 'true' to enable the Acknowledgement Signed Requested");
			}
			try {
				if (Utilities.loadCert(params.elementText("certificateForVerification")) == null){
					errors.add("A certificate must be defined in Certificate For Verification to enable the Acknowledgement Signed Requested");
				}
			} catch (IOException e1) {
				errors.add("The certificate defined in Certificate For Verification cannot load to enable the Acknowledgement Signed Requested. Details : " + e1.getMessage());
			}
		}

		// When acknowledgment is requested for delivery, the mechanism of eleminating duplicates EbXML Message
		// MUST be set.
		if (Utilities.getBooleanValue(params, "duplicateElimination")) {
			if (!Utilities.getBooleanValue(params, "acknowledgementRequested")) {
				errors.add("Acknowledgement Requested must be set to 'true' to enable the Duplicate Elimination");
			}
		}

		// When message ordering is requested for delivery, it is implied that the transaction MUST
		// requires acknolwedgment, duplicate elimination and using asynchronous reply mode.
		if (Utilities.getBooleanValue(params, "messageOrder")) {
			if (!Utilities.getBooleanValue(params, "acknowledgementRequested")) {
				errors.add("Acknowledgement Requested must be set to 'true' to enable the Message Order");
			}
			if (!Utilities.getBooleanValue(params, "duplicateElimination")) {
				errors.add("Duplicate Elimination must be set to 'true' to enable the Message Order");
			}
			if (Utilities.getBooleanValue(params, "syncReplyMode")) {
				errors.add("Sync Reply Mode must be set to 'false' to enable the Message Order");
			}
		}
		
		// When the transaction requires data encryption (SMTP only), it MUST provides the public
		// certificate for doing such things. 
		if (Utilities.getBooleanValue(params, "encryptionRequired")) {
			try {
				if (Utilities.loadCert(params.elementText("certificateForEncryption")) == null){
					errors.add("A certificate must be defined in Certificate For Encryption to enable the Message Encryption");
				}
			} catch (IOException e1) {
				errors.add("The certificate defined in Certificate For Encryption cannot load to enable the Message Encryption. Details : "	+ e1.getMessage());
			}
		}
		return errors;
	}

	/**
	 * Remove a particular partnership defined in the <code>xmlFile</code>.<br/><br/>
	 * 
	 * Only the attributes &lt;id&gt; in the <code>xmlFile</code> will be used
	 * for removing partnership. 
	 * 
	 * @param xmlFile
	 * 			The partnership XML instance file. It is located at "/data/ebms.xml".
	 * @return
	 * 			true if the removal operation ran successfully.
	 * @throws DAOException
	 * 			Error in persistence connectivity.
	 * @throws DocumentException
	 * 			Error in reading the parameter in the <code>xmlFile</code>.
	 * @throws SAXException
	 * 			Error in parsing the <code>xmlFile</code>.
	 * @throws IOException
	 * 			
	 */
	public static boolean removeEbmsPartnership(String xmlFile)
		throws DAOException, DocumentException, SAXException, IOException 
	{
		Element root = getRootElementFromFile(xmlFile);		
		/*
		 * Create an DAOFactory for creating EbMS partnership, you may refer
		 * to the configuration file for the setting of "ebms-daofactory" at
		 * "conf/partnership.ebms-dao.xml"
		 */ 
		DAOFactory ebmsDAOFactory = (DAOFactory) Sys.main.getComponent("ebms-daofactory");
		
		// Create the EbMS partnership DAO and DVO.
		PartnershipDAO partnershipDao = (PartnershipDAO) ebmsDAOFactory.createDAO(PartnershipDAO.class);		
		PartnershipDVO dvo = (PartnershipDVO) partnershipDao.createDVO();

		String pidToRemove = root.attributeValue("id");
		if (pidToRemove == null || "".equalsIgnoreCase(pidToRemove))
			throw new DocumentException("Missing attribute <id> for deleting the partnership.");

		// Set the id of the partnership to remove.
		dvo.setPartnershipId(pidToRemove);
		return partnershipDao.remove(dvo);
	}

	/**
	 * Read and parse the <code>xmlFile</code> and return the root element (document). 
	 * 
	 * @param xmlFile
	 * 			The partnership XML instance file. It is located at "/data/ebms.xml".
	 * @return	The root element of the <code>xmlFile</code>. 			
	 * @throws SAXException
	 * 			Error in parsing the <code>xmlFile</code>.
	 * @throws DocumentException
	 * 			Error in reading the parameter in the <code>xmlFile</code>.
	 * @throws IOException
	 */
	private static Element getRootElementFromFile(String xmlFile) 
		throws SAXException, DocumentException, IOException 
	{
		InputStream is = new FileInputStream(xmlFile);		
		SAXReader reader = new SAXReader();
		// Setup the XSD validation features. 
		reader.setFeature("http://apache.org/xml/features/validation/schema",true);
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",	false);
		reader.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", "data/ebms.xsd");
		Document doc = reader.read(is);
		return doc.getRootElement();
	}
}
