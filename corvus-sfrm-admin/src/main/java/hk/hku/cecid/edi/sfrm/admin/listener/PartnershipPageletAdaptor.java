package hk.hku.cecid.edi.sfrm.admin.listener;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.security.cert.CertificateFactory;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.security.cert.X509Certificate;
import java.security.MessageDigest;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.Enumeration;

import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.util.StringUtilities;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.commons.dao.DAOException;

import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDAO;
import hk.hku.cecid.edi.sfrm.dao.SFRMPartnershipDVO;
import hk.hku.cecid.edi.sfrm.spa.SFRMException;
import hk.hku.cecid.edi.sfrm.spa.SFRMProcessor;
import hk.hku.cecid.edi.sfrm.spa.SFRMProperties;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;

/**
 * @author Patrick Yip
 * @version 1.0.0
 */
public class PartnershipPageletAdaptor extends AdminPageletAdaptor {
	protected Source getCenterSource(HttpServletRequest request){
		
		PropertyTree dom = new PropertyTree();
		dom.setProperty("/partnerships", "");
        dom.setProperty("add_partnership/", "");
        
        try {
            boolean isMultipart = FileUpload.isMultipartContent(request);
            if (isMultipart) {
                Hashtable ht = getHashtable(request);
                String partnershipId = null;

	            if (((String) ht.get("request_action")).equalsIgnoreCase("change")) {
	                partnershipId = (String) ht.get("selected_partnership_id");
	            } else {
	                partnershipId = (String) ht.get("partnership_id");	   
	            	modifyPartnership(ht, request, dom);
	            }
                getSelectedPartnership(partnershipId, dom);                
            }
            getAllPartnerships(dom);
        } catch (Exception e) {
           	SFRMProcessor.getInstance().getLogger().debug("Unable to process the partnership page request", e);
            throw new RuntimeException("Unable to process the partnership page request", e);
        }
        return dom.getSource();
	}
	
	private void getAllPartnerships(PropertyTree dom) throws DAOException{
		SFRMPartnershipDAO partnershipDAO = (SFRMPartnershipDAO) SFRMProcessor.getInstance().getDAOFactory().createDAO(SFRMPartnershipDAO.class);
		List partnerships = partnershipDAO.findAllPartnerships();
		Iterator iter = partnerships.iterator();
		for(int i = 1; iter.hasNext(); i++){
			SFRMPartnershipDVO partnershipDVO = (SFRMPartnershipDVO) iter.next();
			dom.setProperty("partnership[" + i + "]/partnership_id", partnershipDVO.getPartnershipId());
		}
	}
	
	/*
	 * Get all partnership information from DAO and set the selected partnership to the dom object, if the given partnership ID exist 
	 * @param parntershipId Partnership ID being selected
	 * @param dom The DOM object to insert the partnership information to
	 * @throws DAOException 
	 */
	private void getSelectedPartnership(String partnershipId, PropertyTree dom) throws DAOException, SFRMException{
		SFRMPartnershipDAO partnershipDAO = (SFRMPartnershipDAO) SFRMProcessor.getInstance().getDAOFactory().createDAO(SFRMPartnershipDAO.class);
		SFRMPartnershipDVO partnershipDVO = (SFRMPartnershipDVO) partnershipDAO.createDVO();
		partnershipDVO.setPartnershipId(partnershipId);
		SFRMProcessor.getInstance().getLogger().info("Partnership ID: " + partnershipId);
		if(partnershipDAO.retrieve(partnershipDVO)){
			getPartnership(partnershipDVO, dom, "selected_partnership/");
		}
	}
	
	/*
	 * Insert the information of given partnership DVO to the DOM object
	 * @param partnershipDVO The partnership DVO object contains information of the partnership to insert to the DOM object
	 * @param dom DOM object being insert the partnership information
	 * @param prefix The xpath location for inserting the partnership information
	 */
	private void getPartnership(SFRMPartnershipDVO partnershipDVO, PropertyTree dom, String prefix) throws SFRMException{
		dom.setProperty(prefix + "partnership_id", partnershipDVO.getPartnershipId());
		dom.setProperty(prefix + "description", partnershipDVO.getDescription());
		dom.setProperty(prefix + "partner_endpoint", partnershipDVO.getOrgPartnerEndpoint());
		dom.setProperty(prefix + "partner_cert_fingerprint", partnershipDVO.getPartnerCertFingerprint());
		dom.setProperty(prefix + "is_hostname_verified", Boolean.toString(partnershipDVO.isHostnameVerified()));
		dom.setProperty(prefix + "sign_algorithm", partnershipDVO.getSignAlgorithm());
		dom.setProperty(prefix + "encrypt_algorithm", partnershipDVO.getEncryptAlgorithm());
		dom.setProperty(prefix + "retry_max", Integer.toString(partnershipDVO.getRetryMax()));
		dom.setProperty(prefix + "retry_interval", Integer.toString(partnershipDVO.getRetryInterval()));
		dom.setProperty(prefix + "is_disabled", Boolean.toString(partnershipDVO.isDisabled()));
		
		String certFingerPrint = partnershipDVO.getPartnerCertFingerprint();
		
		if(certFingerPrint!=null && !certFingerPrint.equals("")){
			//Check if the certificate is existing
			if(!checkCertificateExist(certFingerPrint)){
				dom.setProperty(prefix + "encrypt_cert_warn", "The certificate file with fingerprint (" + certFingerPrint + ") doesn't exist.");
			}else{
				X509Certificate cert = partnershipDVO.getVerifyX509Certificate();
				if(cert!=null){
					dom.setProperty(prefix + "encrypt_cert/issuer", cert.getIssuerDN()
			                .getName());
			        dom.setProperty(prefix + "encrypt_cert/subject", cert.getSubjectDN()
			                .getName());
			        dom.setProperty(prefix + "encrypt_cert/thumbprint", partnershipDVO.getPartnerCertFingerprint());
			        dom.setProperty(prefix + "encrypt_cert/valid-from", StringUtilities
			                .toGMTString(cert.getNotBefore()));
			        dom.setProperty(prefix + "encrypt_cert/valid-to", StringUtilities
			                .toGMTString(cert.getNotAfter()));
				}
			}
		}
	}
	
	private boolean checkCertificateExist(String fingerPrint){
		File certFile = new File(SFRMProperties.getTrustedCertStore(), fingerPrint);
		return certFile.exists();
	}
	
	public Hashtable getHashtable(HttpServletRequest request)
	    throws FileUploadException, IOException {
		Hashtable ht = new Hashtable();
		DiskFileUpload upload = new DiskFileUpload();
		List fileItems = upload.parseRequest(request);
		Iterator iter = fileItems.iterator();
		while (iter.hasNext()) {
		    FileItem item = (FileItem) iter.next();
		    if (item.isFormField()) {
		        ht.put(item.getFieldName(), item.getString());
		    } else {
		        if (item.getName().equals("")) {
		            //ht.put(item.getFieldName(), null);
		        } else if (item.getSize() == 0) {
		            //ht.put(item.getFieldName(), null);
		        } else {
		            ht.put(item.getFieldName(), item.getInputStream());
		        }
		    }
		}
		return ht;
	}
	
	private void modifyPartnership(Hashtable ht, HttpServletRequest request,
            PropertyTree dom) throws DAOException, SFRMException{
		String requestAction = (String) ht.get("request_action");
		boolean success = false;
		if(requestAction.equalsIgnoreCase("update")){
			success = updatePartnership(ht, dom, request);
		}else if(requestAction.equalsIgnoreCase("add")){
			success = addPartnership(ht, dom, request);
		}else if(requestAction.equalsIgnoreCase("delete")){
			String partnershipId = (String)ht.get("partnership_id");
			success = deletePartnership(partnershipId, request);
		}
	}
	
	/**
	 * Add a new partnership
	 * @param ht Hastable that contain the information of partnership
	 * @param dom DOM object of the page
	 * @param request Servlet requset object
	 * @return whether add action is success, true for success, false otherwise
	 * @throws SFRMException 
	 */
	private boolean addPartnership(Hashtable ht, PropertyTree dom, HttpServletRequest request) throws DAOException, SFRMException{
		SFRMPartnershipDAO partnershipDAO = (SFRMPartnershipDAO) SFRMProcessor.getInstance().getDAOFactory().createDAO(SFRMPartnershipDAO.class);
		SFRMPartnershipDVO partnershipDVO = (SFRMPartnershipDVO) partnershipDAO.createDVO();
		boolean success = setPartnershipDVO(partnershipDVO, ht, request);
		//Check whether partnership ID already exist
		if(partnershipDAO.findPartnershipById((String)ht.get("partnership_id")) != null){
			request.setAttribute(ATTR_MESSAGE, "Partnership ID already exist");
			success = false;
		}
		if(success){
			partnershipDAO.create(partnershipDVO);			
			//If the insertion of the new partnership to database is successful, then upload the user provided partnership to the system specified location
			try{
				if(ht.get("partner_cert") != null){
					uploadCertificate(partnershipDVO.getPartnerCertFingerprint(), (InputStream)ht.get("partner_cert"));
				}
			}catch(Exception e){
				
				SFRMProcessor.getInstance().getLogger().error("Error when uploading the partnership certificate file", e);
			}
			
			request.setAttribute(ATTR_MESSAGE, "Partnership added successfully");
			dom.removeProperty("/partnerships/add_partnership");
		}else{
			getPartnership(partnershipDVO, dom, "add_partnership/");
		}
		return success;
	}
	
	/*
	 * To update the given partnerhsip that user selected
	 * @param ht Hastable that contain the information of partnership
	 * @param dom DOM object of the page
	 * @param request Servlet requset object
	 * @return Whether the update is success, true for success, false otherwise
	 */
	private boolean updatePartnership(Hashtable ht, PropertyTree dom, HttpServletRequest request) throws DAOException{
		SFRMPartnershipDAO partnershipDAO = (SFRMPartnershipDAO) SFRMProcessor.getInstance().getDAOFactory().createDAO(SFRMPartnershipDAO.class);
		SFRMPartnershipDVO partnershipDVO = (SFRMPartnershipDVO) partnershipDAO.createDVO();
		String partnershipId = (String)ht.get("partnership_id");
		partnershipDVO.setPartnershipId(partnershipId);
		partnershipDAO.retrieve(partnershipDVO);
		boolean success = setPartnershipDVO(partnershipDVO, ht, request);
//		try{
			if(success){
				partnershipDAO.persist(partnershipDVO);
				if(ht.get("partner_cert") != null){
					uploadCertificate(partnershipDVO.getPartnerCertFingerprint(), (InputStream)ht.get("partner_cert"));
				}
				request.setAttribute(ATTR_MESSAGE, "Partnership updated successfully");
			}
//		}catch(Exception e){
//			SFRMProcessor.getInstance().getLogger().error("Upload Error", e);
//		}
		return success;
	}
	
	/**
	 * To delete the partnernship
	 * @param partnershipId Partnership ID to delete
	 * @param request Servlet request object
	 * @return Whether the delete operation is success, true for success, false otherwise 
	 */
	private boolean deletePartnership(String partnershipId, HttpServletRequest request) throws DAOException{
		SFRMPartnershipDAO partnershipDAO = (SFRMPartnershipDAO) SFRMProcessor.getInstance().getDAOFactory().createDAO(SFRMPartnershipDAO.class);
		SFRMPartnershipDVO partnershipDVO = (SFRMPartnershipDVO) partnershipDAO.createDVO();
		partnershipDVO.setPartnershipId(partnershipId);
		//partnershipDAO.retrieve(partnershipDVO);
		boolean success = partnershipDAO.remove(partnershipDVO);
		if(!success){
			request.setAttribute(ATTR_MESSAGE, "Fail on deleting the Partnership with ID: " + partnershipId);
		}else{
			request.setAttribute(ATTR_MESSAGE, "Partnership deleted successfully");
		}
		return success;
	}
	
	/**
	 * Validate the partnership data
	 * @param ht Hashtable containing the partnership data
	 * @return Hashtable containing the errors detected
	 */
	private Hashtable validatePartnership(Hashtable ht){
		Hashtable errors = new Hashtable();
		//Validate the partnership ID
		String partnership_id = (String) ht.get("partnership_id");
		String endpoint = (String)ht.get("partner_endpoint");
		String retry_max = (String)ht.get("retry_max");
		String retry_interval = (String)ht.get("retry_interval");
		
		boolean partnership_valid = true;
		if(partnership_id==null || partnership_id.trim().equals("")){
			errors.put("partnership_id", "Partnership ID cannot be empty");
			partnership_valid = false;
		}
		
		if(partnership_valid != false && !partnership_id.matches(SFRMPartnershipDVO.PARTNERSHIPID_REGEXP)){
			errors.put("partnership_id", "Partnership ID should contains the alphanumeric characters and @ _ + - only");
		}
		
		//Validate the partnership Endpoint
		if(endpoint==null || endpoint.trim().equals("")){
			errors.put("partner_endpoint", "Transport Endpoint cannot be empty");
		}else{
			URL endPointURL = null;
			try{
				endPointURL = new URL(endpoint);
			}catch(Exception e){
				errors.put("partner_endpoint", "Transport Endpoint is invalid");
			}
			if(endPointURL!=null){
				String protocol = endPointURL.getProtocol();
				if(protocol!=null && !(protocol.equals("http") || protocol.equals("https"))){
					errors.put("partner_endpoint", "Transport Endpoint protocol is invalid");
				}
			}
		}
		
		//Validate retry max
		try{
			boolean valid = true;
			if(retry_max.trim().equals("")){
				errors.put("retry_max", "Maximum Retries should have a value");
				valid = false;
			}
			if(valid == true){
				long temp = Long.parseLong(retry_max);
				if(temp < Integer.MIN_VALUE || temp > Integer.MAX_VALUE){
					errors.put("retry_max", "Maximum Retries out of range");
				}
			}
		}catch(NumberFormatException nfe){
			errors.put("retry_max", "Maximum Retries must be an integer");
		}
				
		//Validate retry interval
		try{
			boolean valid = true;
			if(retry_interval.trim().equals("")){
				errors.put("retry_interval", "Retry Interval should have a value");
				valid = false;
			}
			if(valid == true){
				long temp = Long.parseLong(retry_interval);
				if(temp < Integer.MIN_VALUE || temp > Integer.MAX_VALUE){
					errors.put("retry_interval", "Retry Interval out of range");
				}
			}
		}catch(NumberFormatException nfe){
			errors.put("retry_interval", "Retry Interval must be an integer");
		}
		
		//Validate the uploaded certificate
		boolean isUploadedCert = ht.get("partner_cert") != null;
		InputStream certInStream = null;
		if(isUploadedCert){
			certInStream = (InputStream) ht.get("partner_cert");
			try {
//				certInStream.reset();
				String fingerprint = generateX509CertificateFingerprint(certInStream);
            }catch(IOException ioe){
            	errors.put("partner_cert", "Error when reading the partnership certificate");
            } catch (Exception e) {
                errors.put("partner_cert", "Uploaded cert is not an X.509 cert");
            }
		}
		return errors;
	}
	
	/**
	 * To set the Partnership DVO to from the given hashtable
	 * @param partnershipDVO The PartnershipDVO object to set the value
	 * @param ht The given hashtable that contins the partnership information which need to set to DVO
	 * @param request Servlet request for setting the response message in text, if needed
	 * @return Whether it passes the validation of the data 
	 */
	private boolean setPartnershipDVO(SFRMPartnershipDVO partnershipDVO, Hashtable ht, HttpServletRequest request){
		boolean success = true;
		try{
			Hashtable errors = validatePartnership(ht);
			if(errors.size()!=0){
				Enumeration keys = errors.keys();
				request.setAttribute(ATTR_MESSAGE, errors.get((String)keys.nextElement()));
				
				if(errors.get("retry_max")!=null){
					partnershipDVO.setRetryMax(3);
				}
				if(errors.get("retry_interval")!=null){
					partnershipDVO.setRetryInterval(60000);
				}			
				success = false;
			}
			
			if(errors.get("retry_max") == null)
				partnershipDVO.setRetryMax(Integer.parseInt((String) ht.get("retry_max")));
			if(errors.get("retry_interval") == null)
				partnershipDVO.setRetryInterval(Integer.parseInt((String) ht.get("retry_interval")));
			
			partnershipDVO.setPartnershipId((String)ht.get("partnership_id"));
			partnershipDVO.setPartnerEndPoint((String)ht.get("partner_endpoint"));		
			partnershipDVO.setDescription((String)ht.get("description"));
					
			String signAlg = null;
			String encryptAlg = null;
			if(!((String)ht.get("sign_algorithm")).equalsIgnoreCase("none")){
				signAlg = (String)ht.get("sign_algorithm");
			}
			
			if(!((String)ht.get("encrypt_algorithm")).equalsIgnoreCase("none")){
				encryptAlg = (String)ht.get("encrypt_algorithm");
			}
			
			partnershipDVO.setSignAlgorithm(signAlg);
			partnershipDVO.setEncryptAlgorithm(encryptAlg);
			
			partnershipDVO.setIsHostnameVerified(Boolean.valueOf((String)ht.get("is_hostname_verified")).booleanValue());
			partnershipDVO.setIsDisabled(Boolean.valueOf((String)ht.get("is_disabled")).booleanValue());
			
			//Check if user request to delete the partnership certificate
			if (ht.get("encrypt_cert_remove") != null) {
	            if (((String) ht.get("encrypt_cert_remove")).equalsIgnoreCase("on")) {
	                partnershipDVO.setPartnerCertFingerprint(null);
	            }
	        }
			
			//Get the certificate uploaded by user
			if(errors.get("partner_cert") == null){
				boolean isUploadedCert = ht.get("partner_cert") != null;
				InputStream certInStream = null;
				if(isUploadedCert){
					certInStream = (InputStream) ht.get("partner_cert");
					
					try {
						certInStream.reset();
						String fingerprint = generateX509CertificateFingerprint(certInStream);
						partnershipDVO.setPartnerCertFingerprint(fingerprint.toUpperCase());
		            } catch (IOException ioe) {
		            	request.setAttribute(ATTR_MESSAGE, "Error when reading the partnership certificate");
		            	success = false;
		            }catch(Exception e){
		            	request.setAttribute(ATTR_MESSAGE, "Partnership Certificate Error");
		            }
				}
			}
		}catch(Exception e){
			SFRMProcessor.getInstance().getLogger().error("Error which uplaoding the cert", e);
		}

		return success;
	}
	
	/**
	 * Generate the fingerprint for the provided certificate. This method also validate the certificate provided.
	 * @param inStream the file stream for the certificate to generate the fingerprint
	 * @return Fingerprint in the hexdecimal String representation
	 * @throws Exception if the certificate is invalid and it is not a X.509 format
	 */
	private String generateX509CertificateFingerprint(InputStream inStream) throws Exception{
		BufferedInputStream fis = new BufferedInputStream(inStream);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate) cf.generateCertificate(fis);
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte fingerprint[] = md.digest(cert.getEncoded());
		return toHexString(fingerprint);		
	}
	
	private String toHexString(byte[] b) throws Exception {
  	  	String result = "";
  	  	for (int i=0; i < b.length; i++) {
  	  		result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
  	  	}
  	  	return result;
  	}
	
	/**
	 * Upload the certificate to the application defined location at [PLUGIN_FOLDER]/hk.hku.cecid.edi.sfrm/conf/hk/hku/cecid/edi/sfrm/conf/sfrm.properties.xml, trusted-certificates element
	 * @param filename Filename to uplaod to the specified location
	 * @param certInStream InputStream that contains the certificate uploaded from client
	 */
	private void uploadCertificate(String filename, InputStream certInStream){
		try{
			File outFile = new File(SFRMProperties.getTrustedCertStore(), filename);
			FileOutputStream certOutStream = new FileOutputStream(outFile);
			certInStream.reset();
			IOHandler.pipe(certInStream, certOutStream);
			certOutStream.close();
		}catch(Exception e){
			SFRMProcessor.getInstance().getLogger().error("Error when uploading the partnership certificate file", e);
		}
	}
}
