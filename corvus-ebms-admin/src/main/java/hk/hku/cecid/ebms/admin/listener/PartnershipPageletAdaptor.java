/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.ebms.admin.listener;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.commons.util.StringUtilities;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;

/**
 * @author Donahue Sze
 *  
 */
public class PartnershipPageletAdaptor extends AdminPageletAdaptor {
			
    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.pagelet.xslt.BorderLayoutPageletAdaptor#getCenterSource(javax.servlet.http.HttpServletRequest)
     */
    protected Source getCenterSource(HttpServletRequest request) {

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
                  	updatePartnership(ht, request, dom);	                    
	            }
                getSelectedPartnership(partnershipId, dom);                
            }
            getAllPartnerships(dom);
        } catch (Exception e) {
            EbmsProcessor.core.log.debug("Unable to process the partnership page request", e);
            throw new RuntimeException("Unable to process the partnership page request", e);
        }
              
        return dom.getSource();
    }

    /**
     * @param request
     * @throws DAOException
     * @throws IOException
     */
    private void updatePartnership(Hashtable ht, HttpServletRequest request,
            PropertyTree dom) throws DAOException, IOException {

        // get the parameters
        String requestAction = (String) ht.get("request_action");

        String partnershipId = (String) ht.get("partnership_id");
        String cpaId = (String) ht.get("cpa_id");
        String service = (String) ht.get("service");
        String action = (String) ht.get("action_id");

        String transportEndpoint = (String) ht.get("transport_endpoint");
        String isHostnameVerified = (String) ht.get("is_hostname_verified");
        String syncReplyMode = (String) ht.get("sync_reply_mode");
        String ackRequested = (String) ht.get("ack_requested");
        String ackSignRequested = (String) ht.get("ack_sign_requested");
        String dupElimination = (String) ht.get("dup_elimination");
        String messageOrder = (String) ht.get("message_order");

        String disabled = (String) ht.get("disabled");
        String retries = (String) ht.get("retries");
        String retryInterval = (String) ht.get("retry_interval");

        String signRequested = (String) ht.get("sign_requested");
        String encryptRequested = (String) ht.get("encrypt_requested");

        boolean hasEncryptCert = ht.get("encrypt_cert") != null;
        InputStream encryptCertInputStream = null;
        if (hasEncryptCert) {
            encryptCertInputStream = (InputStream) ht.get("encrypt_cert");
        }
        boolean hasRemoveEncryptCert = false;
        if (ht.get("encrypt_cert_remove") != null) {
            if (((String) ht.get("encrypt_cert_remove")).equalsIgnoreCase("on")) {
                hasRemoveEncryptCert = true;
            }
        }
        boolean hasVerifyCert = ht.get("verify_cert") != null;
        InputStream verifyCertInputStream = null;
        if (hasVerifyCert) {
            verifyCertInputStream = (InputStream) ht.get("verify_cert");
        }
        boolean hasRemoveVerifyCert = false;
        if (ht.get("verify_cert_remove") != null) {
            if (((String) ht.get("verify_cert_remove")).equalsIgnoreCase("on")) {
                hasRemoveVerifyCert = true;
            }
        }

        if ("add".equalsIgnoreCase(requestAction)
                || "update".equalsIgnoreCase(requestAction)
                || "delete".equalsIgnoreCase(requestAction)) {

            // validate and set to dao
            PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
                    .createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO
                    .createDVO();

            partnershipDVO.setPartnershipId(partnershipId);

            if ("update".equalsIgnoreCase(requestAction)) {
                partnershipDAO.retrieve(partnershipDVO);
            }
            
            partnershipDVO.setCpaId(cpaId);
            partnershipDVO.setService(service);
            partnershipDVO.setAction(action);
            partnershipDVO.setTransportEndpoint(transportEndpoint);
            partnershipDVO.setIsHostnameVerified(isHostnameVerified);
            partnershipDVO.setSyncReplyMode(syncReplyMode);
            partnershipDVO.setAckRequested(ackRequested);
            partnershipDVO.setAckSignRequested(ackSignRequested);
            partnershipDVO.setDupElimination(dupElimination);
            partnershipDVO.setActor(null);
            partnershipDVO.setDisabled(disabled);
            partnershipDVO.setRetries(StringUtilities.parseInt(retries));
            partnershipDVO.setRetryInterval(StringUtilities
                    .parseInt(retryInterval));
            partnershipDVO.setPersistDuration(null);
            partnershipDVO.setMessageOrder(messageOrder);
            partnershipDVO.setSignRequested(signRequested);
            partnershipDVO.setDsAlgorithm(null);
            partnershipDVO.setMdAlgorithm(null);
            partnershipDVO.setEncryptRequested(encryptRequested);
            partnershipDVO.setEncryptAlgorithm(null);

            if ("add".equalsIgnoreCase(requestAction)) {
                getPartnership(partnershipDVO, dom, "add_partnership/");
            }
            
            if (!cpaId.equals("")) {
                partnershipDVO.setCpaId(cpaId);
            } else {
                request.setAttribute(ATTR_MESSAGE, "CPA ID cannot be empty");
                return;
            }

            if (!service.equals("")) {
                partnershipDVO.setService(service);
            } else {
                request.setAttribute(ATTR_MESSAGE, "Service cannot be empty");
                return;
            }

            if (!action.equals("")) {
                partnershipDVO.setAction(action);
            } else {
                request.setAttribute(ATTR_MESSAGE, "Action cannot be empty");
                return;
            }
            
            URL transportEndpointURL = null;
            try {
                transportEndpointURL = new URL(transportEndpoint);
            } catch (Exception e) {
                request.setAttribute(ATTR_MESSAGE,
                        "Transport Endpoint is invalid");
                return;
            }
            if (transportEndpointURL.getProtocol().equalsIgnoreCase("mailto")) {
                partnershipDVO.setTransportProtocol("smtp");
            } else if (transportEndpointURL.getProtocol().equalsIgnoreCase(
                    "http")
                    || transportEndpointURL.getProtocol().equalsIgnoreCase(
                            "https")) {
                partnershipDVO.setTransportProtocol(transportEndpointURL
                        .getProtocol());
            } else {
                request.setAttribute(ATTR_MESSAGE,
                        "The endpoint protocol does not support");
                return;
            }

            if (partnershipDVO.getRetries() == Integer.MIN_VALUE) {
                request.setAttribute(ATTR_MESSAGE, "Retries must be integer");
                return;
            }
            if (partnershipDVO.getRetryInterval() == Integer.MIN_VALUE) {
                request.setAttribute(ATTR_MESSAGE,
                        "Retry Interval must be integer");
                return;
            }

            // encrypt cert
            if (hasRemoveEncryptCert) {
                partnershipDVO.setEncryptCert(null);
            }
            if (hasEncryptCert) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOHandler.pipe(encryptCertInputStream, baos);
                    CertificateFactory
                            .getInstance("X.509")
                            .generateCertificate(
                                    new ByteArrayInputStream(baos.toByteArray()));
                    partnershipDVO.setEncryptCert(baos.toByteArray());
                } catch (Exception e) {
                    request.setAttribute(ATTR_MESSAGE,
                            "Uploaded encrypt cert is not an X.509 cert");
                    return;
                }
            }

            // verify cert
            if (hasRemoveVerifyCert) {
                partnershipDVO.setSignCert(null);
            }
            if (hasVerifyCert) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOHandler.pipe(verifyCertInputStream, baos);
                    CertificateFactory
                            .getInstance("X.509")
                            .generateCertificate(
                                    new ByteArrayInputStream(baos.toByteArray()));
                    partnershipDVO.setSignCert(baos.toByteArray());
                } catch (Exception e) {
                    request.setAttribute(ATTR_MESSAGE,
                            "Uploaded verify cert is not an X.509 cert");
                    return;
                }
            }

            // check partnership conflict
            if (!Boolean.valueOf(partnershipDVO.getDisabled()).booleanValue()) {
                Iterator allConflictDAOData = partnershipDAO
                        .findPartnershipsByCPA(partnershipDVO).iterator();
                while (allConflictDAOData.hasNext()) {
                    PartnershipDVO conflictDAOData = (PartnershipDVO) allConflictDAOData
                            .next();
                    if (conflictDAOData != null
                            && !conflictDAOData.getPartnershipId().equals(partnershipDVO.getPartnershipId())
                            && !Boolean.valueOf(conflictDAOData.getDisabled()).booleanValue()) {
                        request.setAttribute(ATTR_MESSAGE,
                        		"Partnership '" +conflictDAOData.getPartnershipId()+ 
                        		"' with same combination of CPA ID, Service and Action has already been enabled");
                        return;
                    }
                }
            }

            // dao action
            if ("add".equalsIgnoreCase(requestAction)) {
            	partnershipDAO.create(partnershipDVO);                                        
                request.setAttribute(ATTR_MESSAGE, "Partnership added successfully");                       
                dom.removeProperty("/partnerships/add_partnership");
                dom.setProperty("/partnerships/add_partnership", "");
            }
            if ("update".equalsIgnoreCase(requestAction)) {
                partnershipDAO.persist(partnershipDVO);
                request.setAttribute(ATTR_MESSAGE, "Partnership updated successfully");
            }
            if ("delete".equalsIgnoreCase(requestAction)) {
            	           	            	
                partnershipDAO.remove(partnershipDVO);
                request.setAttribute(ATTR_MESSAGE, "Partnership deleted successfully");
            }

        }
    }

    private void getSelectedPartnership(String partnershipId, PropertyTree dom)
            throws DAOException, CertificateException, IOException {
        PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
                .createDAO(PartnershipDAO.class);
        PartnershipDVO partnershipDVO = (PartnershipDVO) partnershipDAO
                .createDVO();

        partnershipDVO.setPartnershipId(partnershipId);

        if (partnershipDAO.retrieve(partnershipDVO)) {
            getPartnership(partnershipDVO, dom, "selected_partnership/");
        }
    }

    /**
     * @param request
     * @throws DAOException
     * @throws CertificateException
     * @throws IOException
     */
    private void getPartnership(PartnershipDVO partnershipDVO,
            PropertyTree dom, String prefix) {

        dom.setProperty(prefix + "partnership_id", partnershipDVO
                .getPartnershipId());
        dom.setProperty(prefix + "cpa_id", partnershipDVO.getCpaId());
        dom.setProperty(prefix + "service", partnershipDVO.getService());
        dom.setProperty(prefix + "action_id", partnershipDVO.getAction());

        dom.setProperty(prefix + "transport_endpoint", partnershipDVO
                .getTransportEndpoint());
        dom.setProperty(prefix + "is_hostname_verified", partnershipDVO
                .getIsHostnameVerified());
        dom.setProperty(prefix + "sync_reply_mode", partnershipDVO
                .getSyncReplyMode());
        dom.setProperty(prefix + "ack_requested", partnershipDVO
                .getAckRequested());
        dom.setProperty(prefix + "ack_sign_requested", partnershipDVO
                .getAckSignRequested());
        dom.setProperty(prefix + "dup_elimination", partnershipDVO
                .getDupElimination());
        dom.setProperty(prefix + "message_order", partnershipDVO
                .getMessageOrder());
        dom.setProperty(prefix + "sign_requested", partnershipDVO
                .getSignRequested());
        dom.setProperty(prefix + "encrypt_requested", partnershipDVO
                .getEncryptRequested());

        getCertificateForPartnership(partnershipDVO.getEncryptCert(), dom,
                prefix + "encrypt_cert/");
        getCertificateForPartnership(partnershipDVO.getSignCert(), dom, prefix
                + "verify_cert/");

        dom.setProperty(prefix + "retries", formatInteger(partnershipDVO
                .getRetries()));
        dom.setProperty(prefix + "retry_interval", formatInteger(partnershipDVO
                .getRetryInterval()));
        dom.setProperty(prefix + "disabled", String.valueOf(partnershipDVO
                .getDisabled()));
    }

    private void getCertificateForPartnership(byte[] cert, PropertyTree dom,
            String prefix) {
        if (cert != null) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(cert);
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                X509Certificate verifyCert = (X509Certificate) cf
                        .generateCertificate(bais);
                bais.close();
                dom.setProperty(prefix + "issuer", verifyCert.getIssuerDN()
                        .getName());
                dom.setProperty(prefix + "subject", verifyCert.getSubjectDN()
                        .getName());
                dom.setProperty(prefix + "thumbprint",
                        getCertFingerPrint(verifyCert));
                dom.setProperty(prefix + "valid-from", StringUtilities
                        .toGMTString(verifyCert.getNotBefore()));
                dom.setProperty(prefix + "valid-to", StringUtilities
                        .toGMTString(verifyCert.getNotAfter()));
            } catch (Exception e) {
                dom.setProperty(prefix + "Error", e.toString());
            }
        } else {
            dom.setProperty(prefix, "");
        }
    }

    private String formatInteger(int i) {
        if (i == Integer.MIN_VALUE) {
            return "";
        } else {
            return String.valueOf(i);
        }
    }

    /**
     * @return
     * @throws DAOException
     */
    private void getAllPartnerships(PropertyTree dom) throws DAOException {

        PartnershipDAO partnershipDAO = (PartnershipDAO) EbmsProcessor.core.dao
                .createDAO(PartnershipDAO.class);

        Iterator i = partnershipDAO.findAllPartnerships().iterator();
        for (int pi = 1; i.hasNext(); pi++) {
            PartnershipDVO partnershipDVO = (PartnershipDVO) i.next();
            dom.setProperty("partnership[" + pi + "]/partnership_id",
                    partnershipDVO.getPartnershipId());
            dom.setProperty("partnership[" + pi + "]/cpa_id", partnershipDVO
                    .getCpaId());
            dom.setProperty("partnership[" + pi + "]/service", partnershipDVO
                    .getService());
            dom.setProperty("partnership[" + pi + "]/action_id", partnershipDVO
                    .getAction());
        }
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

    /*
    private boolean isInteger(String value) {
        try {
            Integer.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    */

    private String getCertFingerPrint(X509Certificate cert) {
        try {
            String mdAlg;
            if (cert.getSigAlgName().toUpperCase().startsWith("SHA")) {
                mdAlg = "SHA";
            } else {
                mdAlg = "MD5";
            }
            byte[] encCertInfo = cert.getEncoded();
            MessageDigest md = MessageDigest.getInstance(mdAlg);
            byte[] digest = md.digest(encCertInfo);
            return toHexString(digest);
        } catch (Exception e) {
            return e.toString();
        }
    }

    private String toHexString(byte[] buf) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String HEX = "0123456789abcdef";
            str.append(HEX.charAt(buf[i] >>> 4 & 0x0F));
            str.append(HEX.charAt(buf[i] & 0x0F));
        }
        return str.toString();
    }
}