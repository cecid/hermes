package hk.hku.cecid.corvus.partnership;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import hk.hku.cecid.edi.as2.dao.PartnershipDAO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DAOFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

/**
 * The <code>AS2Partnership</code> is the utilities for maintaining the partnership
 * of AS2. In current version, it support addition or deletion of the partnership.
 * 
 * @author kochiu, Twinsen Tsang (modifiers)
 * 
 * @see #createAS2Partnership(String)
 * @see #removeAS2Partnership(String)
 */
public class AS2Partnership {
	
	/**
	 * The entry point for CLI.
	 * 
	 * @param args 
	 * 			The arguments have two parametes. The first one is the partnership maintenance
	 *			which is either "-a" (add) or "-d" (delete). The second one is the 
	 *			xml file containing the partnership information. They are located 
	 *			at the "conf/as2.xml" relative to the program folders.			 			 				    			
	 */
	 public static void main(String[] args) {
		if (args.length < 2) { printUsage(); return; }

		String option = args[0];
		String xmlFile = args[1];
		try {
			if ("-a".equals(option)) {
				boolean result = createAS2Partnership(xmlFile);
				System.out.println(result ? "Added partnership successfully." : "Cannont remove partnership.");
			} else if ("-d".equals(option)) {
				boolean result = removeAS2Partnership(xmlFile);
				System.out.println(result ? "Removed partnership successfully."	: "Cannont remove partnership.");
			} else {
				printUsage();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void printUsage(){
		System.out.println("Usage : java AS2Partnership -options <xml_file_path>");
		System.out.println("Options :");
		System.out.println("\t -a \t Add partnership");
		System.out.println("\t -d \t Delete partnership");
	}
	
	/**
	 * Create a AS2 partnership with the configuration defined in the <code>XMLFile</code>.
	 * 
	 * @param xmlFile
	 * 			The partnership XML instance file. It is located at "/data/as2.xml".
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
    public static boolean createAS2Partnership(String xmlFile)
		throws DAOException, DocumentException, SAXException, IOException 
	{
        Element e = getRootElementFromFile(xmlFile);

        // Checking whether there is any field violating the AS2 specification.
        List errors = fieldChecking(e);
        if (errors.size() > 0) {
            for (int i = 0; i < errors.size(); i++)
                System.out.println(errors.get(i));
            return false;
        }

        /*
		 * Create an DAOFactory for creating AS2 partnership, you may refer
		 * to the configuration file for the setting of "as2-daofactory" at
		 * "conf/partnership.as2-dao.xml"
		 */ 
		DAOFactory as2DAOFactory = (DAOFactory) Sys.main.getComponent("as2-daofactory");
			
		PartnershipDAO partnershipDao = (PartnershipDAO) as2DAOFactory.createDAO(PartnershipDAO.class);		
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
		
		Element params = e.element("parameters");
		
		dvo.setPartnershipId(pID);
        // AS2 Specific information.
        dvo.setAs2From	(params.elementText("as2From"));
        dvo.setAs2To	(params.elementText("as2To"));
        dvo.setSubject	(params.elementText("subject"));
        
        // AS2 Devliery information.
        dvo.setRecipientAddress(params.elementText("recipientAddress"));        
        // Requires SSL for delivery ?
        dvo.setIsHostnameVerified("true".equalsIgnoreCase(params.elementText("hostnameVerifiedInSsl")));
        // Requires recipient sending back a receipt (ACK) ?  
        dvo.setIsReceiptRequired ("true".equalsIgnoreCase (params.elementText("requestMdn")));
        // Requires the digitial signatures at the receipt send by recipient.  
        dvo.setIsReceiptSignRequired("true".equalsIgnoreCase(params.elementText("signedReceipt")));
        // Requires receipt being sent using same connection or different connection.
        dvo.setIsSyncReply("false".equalsIgnoreCase(params.elementText("asynchronousReceipt")));
        // The URL that receipt is sent to when using different connection response mode. 
        dvo.setReceiptAddress(params.elementText("mdnReturnUrl"));                
        // Set whether compression is required for outgoing AS2 Message.
        dvo.setIsOutboundCompressRequired("true".equalsIgnoreCase(params
			.elementText("messageCompressionRequired")));
               
        // AS2 Security-related information
        // Set whether digital signatures is required for outgoing AS2 Message.
        dvo.setIsOutboundSignRequired("true".equalsIgnoreCase(params.elementText("messageSigningRequired")));
        
        // Set whether which signing algorithm is used. SHA1 or MD5.
        if (params.elementText("signingAlgorithm") != null)
            dvo.setSignAlgorithm(params.elementText("signingAlgorithm"));
        
        // Set whether encryption is required for outgoing AS2 Message.
        dvo.setIsOutboundEncryptRequired("true".equalsIgnoreCase(params
                .elementText("messageEncryptionRequired")));
        
        // Set wehther which encryption algorithm is used. 3DES or RC2.
        if (params.elementText("encryptionAlgorithm") != null) {
            dvo.setEncryptAlgorithm(params.elementText("encryptionAlgorithm"));
        }

        // Set the MIC (Message-Integrity-Check) algorithm. SHA1 or MD5.
        if (params.elementText("micAlgorithm") != null)
            dvo.setMicAlgorithm(params.elementText("micAlgorithm"));
        
        // Load the public certificates for encryption.
        dvo.setEncryptCert(Utilities.loadCert(params.elementText("certificateForEncryption")));
        
        // Load the public certificates for signatures verification 
        // (ONLY for incoming message or requires signed receipt ).
        dvo.setVerifyCert(Utilities.loadCert(params.elementText("certificateForVerification")));
                
        // Set whether signing / encryption MUST be applied or not when receiving an AS2 Message.
        dvo.setIsInboundSignRequired   ("true".equalsIgnoreCase(params.elementText("messageSignatureEnforced")));
        dvo.setIsInboundEncryptRequired("true".equalsIgnoreCase(params.elementText("messageEncryptionEnforced")));
        
		// General delivery information        
        dvo.setRetries(Integer.parseInt(params.elementText("maximumRetries")));
        dvo.setRetryInterval(Integer.parseInt(params.elementText("retryInterval")));       
        dvo.setIsDisabled(false);

		// Create the partnership.        
        partnershipDao.create(dvo);
        return true;
    }

    /**
	 * Checking whether there is any field in the partnership xml file 
	 * violating the AS2 specification.
	 * 
	 * @param root The root element in the partnership xml file.
	 * @return A list of string containing the error description. Empty list if no error has been found.
	 */
    private static List fieldChecking(Element root) {
        Element params = root.element("parameters");
        List errors = new ArrayList();

        // When asynchronous reciept is set, then it MUST requests for receipt and 
        // the async return URL must not NULL.
        if (Utilities.getBooleanValue(params, "asynchronousReceipt")){
            if (!Utilities.getBooleanValue(params, "requestMdn"))
                errors.add("Request MDN must be 'true' to enable the Asynchronous Receipt");
            if (params.elementText("recipientAddress") == null)
                errors.add("MDN Return URL must not be empty to enable the Asynchronous Receipt");
        }
        // When digital signatures is required, then it MUST requests for receipt and 
        // providing the public certificates for verifying the signatures.
        if (Utilities.getBooleanValue(params, "signedReceipt")){
            if (Utilities.getBooleanValue(params, "requestMdn")) 
                errors.add("Request MDN must be 'true' to enable the Signed Receipt");
            try {
                if (Utilities.loadCert(params.elementText("certificateForVerification")) == null)
                    errors.add("A certificate must be defined in 'certificateForVerification' to enable the Message Signing Required");
            } catch (IOException ioe) {
                errors.add("The certificate defined in 'certificateForVerification' cannot load to enable the Message Signing Required. Details : " + ioe.getMessage());
            }
        }
        // When encryption is required, then It MUST provide the encryption algorithm and the
        // public certificates used for encryption.
        if (Utilities.getBooleanValue(params, "messageEncryptionRequired")) {
            if (params.elementText("encryptionAlgorithm") == null)
                errors.add("An encryption algorithm must be defined in 'encryptionAlgorithm' to enable the Message Encryption Required");
            try {
                if (Utilities.loadCert(params.elementText("certificateForEncryption")) == null)
                    errors.add("A certificate must be defined in 'certificateForEncryption' to enable the Message Encryption Required");
            } catch (IOException ioe) {
                errors.add("The certificate defined in 'certificateForEncryption' cannot load to enable the Message Encryption Required. Details : " + ioe.getMessage());
            }
        }
        // When digital signatures is required for outgoing AS2 message, partnership MUST define
        // use which signing algorithm. 
        if (Utilities.getBooleanValue(params, "messageSigningRequired")){ 
            if (params.elementText("signingAlgorithm") == null)
                errors.add("A signing algorithm must be defined in 'signingAlgorithm' to enable the Message Signing Required");
        }

        // When receipt is requested, partnership MUST define the MIC algorithm for check-sum.
        if (Utilities.getBooleanValue(params, "requestMdn")){
            if (params.elementText("micAlgorithm") == null)
                errors.add("A MIC algorithm must be defined in 'micAlgorithm' to enable the Request MDN");
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
    public static boolean removeAS2Partnership(String xmlFile)
		throws DAOException, DocumentException, FileNotFoundException, SAXException 
	{
		Element root = getRootElementFromFile(xmlFile);
		/*
		 * Create an DAOFactory for creating AS2 partnership, you may refer
		 * to the configuration file for the setting of "as2-daofactory" at
		 * "conf/partnership.as2-dao.xml"
		 */ 
		DAOFactory as2DAOFactory = (DAOFactory) Sys.main.getComponent("as2-daofactory");
			
		PartnershipDAO partnershipDao = (PartnershipDAO) as2DAOFactory.createDAO(PartnershipDAO.class);		
		PartnershipDVO dvo = (PartnershipDVO) partnershipDao.createDVO();
		
		String pidToRemove = root.attributeValue("id");
		if (pidToRemove == null || "".equalsIgnoreCase(pidToRemove))
			throw new DocumentException("Missing attribute <id> for deleting the partnership.");

		// Set the id of the partnership to remove.		
		dvo.setPartnershipId(pidToRemove);
		return partnershipDao.remove(dvo);
	}

    /**
	 * Read and parse the <code>xmlFile</code> and return the root element
	 * (document).
	 * 
	 * @param xmlFile
	 *            The partnership XML instance file. It is located at
	 *            "/data/ebms.xml".
	 * @return The root element of the <code>xmlFile</code>.
	 * @throws SAXException
	 *             Error in parsing the <code>xmlFile</code>.
	 * @throws DocumentException
	 *             Error in reading the parameter in the <code>xmlFile</code>.
	 * @throws IOException
	 */
    private static Element getRootElementFromFile(String xmlFile)
			throws SAXException, DocumentException, FileNotFoundException {
		InputStream is = new FileInputStream(xmlFile);
		SAXReader reader = new SAXReader();
		// Setup the XSD validation features.		
		reader.setFeature("http://apache.org/xml/features/validation/schema",true);
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",	false);
		reader.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", "data/as2.xsd");
		Document doc = reader.read(is);
		return doc.getRootElement();
	}

   
}
