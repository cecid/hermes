/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.edi.as2.admin.listener;

import hk.hku.cecid.edi.as2.AS2PlusProcessor;
import hk.hku.cecid.edi.as2.dao.PartnershipDAO;
import hk.hku.cecid.edi.as2.dao.PartnershipDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.io.IOHandler;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.commons.util.StringUtilities;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
                String selectedPartnershipId = null;
                
                if (((String) ht.get("request_action"))
                        .equalsIgnoreCase("change")) {
                    selectedPartnershipId = (String) ht
                            .get("selected_partnership_id");
                } else {
                    selectedPartnershipId = (String) ht.get("partnership_id");
                   	updatePartnership(ht, request, dom);
                }
                getSelectedPartnership(selectedPartnershipId, dom);
            }
            getAllPartnerships(dom);
        } catch (Exception e) {
            AS2PlusProcessor.getInstance().getLogger().debug("Unable to process the partnership page request", e);
            throw new RuntimeException("Unable to process the partnership page request", e);
        }

        return dom.getSource();
    }

    /**
     * @param request
     * @throws DAOException
     * @throws IOException
     */
    private void updatePartnership(Hashtable ht, HttpServletRequest request, PropertyTree dom)
            throws DAOException, IOException {

        // get the parameters
        String requestAction = (String) ht.get("request_action");
        String partnershipId = (String) ht.get("partnership_id");
        String subject = (String) ht.get("subject");
        String recipientAddress = (String) ht.get("recipient_address");
        boolean isHostnameVerified = new Boolean((String) ht
                .get("is_hostname_verified")).booleanValue();
        String receiptAddress = (String) ht.get("receipt_address");
        boolean isSyncReply = new Boolean((String) ht.get("is_sync_reply"))
                .booleanValue();
        boolean isReceiptRequested = new Boolean((String) ht
                .get("is_receipt_requested")).booleanValue();
        boolean isOutboundSignRequired = new Boolean((String) ht
                .get("is_outbound_sign_required")).booleanValue();
        boolean isOutboundEncryptRequired = new Boolean((String) ht
                .get("is_outbound_encrypt_required")).booleanValue();
        boolean isOutboundCompressRequired = new Boolean((String) ht
                .get("is_outbound_compress_required")).booleanValue();
        boolean isReceiptSignRequired = new Boolean((String) ht
                .get("is_receipt_sign_required")).booleanValue();
        boolean isInboundSignRequired = new Boolean((String) ht
                .get("is_inbound_sign_required")).booleanValue();
        boolean isInbouhndEncryptRequired = new Boolean((String) ht
                .get("is_inbound_encrypt_required")).booleanValue();
        String signAlgorithm = (String) ht.get("sign_algorithm");
        String encryptAlgorithm = (String) ht.get("encrypt_algorithm");
        String micAlgorithm = (String) ht.get("mic_algorithm");
        String as2From = (String) ht.get("as2_from");
        String as2To = (String) ht.get("as2_to");
        String retries = (String) ht.get("retries");
        String retryInterval = (String) ht.get("retry_interval");
        boolean isDisabled = new Boolean((String) ht.get("disabled"))
                .booleanValue();
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
            PartnershipDAO partnershipDAO = (PartnershipDAO) AS2PlusProcessor.getInstance().getDAOFactory()
                    .createDAO(PartnershipDAO.class);
            PartnershipDVO partnershipDAOData = (PartnershipDVO) partnershipDAO
                    .createDVO();

            partnershipDAOData.setPartnershipId(partnershipId);
            if ("update".equalsIgnoreCase(requestAction)) {
                partnershipDAO.retrieve(partnershipDAOData);
            }
            partnershipDAOData.setAs2From(as2From);
            partnershipDAOData.setAs2To(as2To);
            partnershipDAOData.setSubject(subject);
            partnershipDAOData.setRecipientAddress(recipientAddress);
            partnershipDAOData.setIsHostnameVerified(isHostnameVerified);
            partnershipDAOData.setReceiptAddress(receiptAddress);
            partnershipDAOData.setIsSyncReply(isSyncReply);
            partnershipDAOData.setIsReceiptRequired(isReceiptRequested);
            partnershipDAOData.setIsOutboundSignRequired(isOutboundSignRequired);
            partnershipDAOData.setIsOutboundEncryptRequired(isOutboundEncryptRequired);
            partnershipDAOData.setIsOutboundCompressRequired(isOutboundCompressRequired);
            partnershipDAOData.setIsReceiptSignRequired(isReceiptSignRequired);
            partnershipDAOData.setIsInboundSignRequired(isInboundSignRequired);
            partnershipDAOData.setIsInboundEncryptRequired(isInbouhndEncryptRequired);
            partnershipDAOData.setSignAlgorithm(signAlgorithm);
            partnershipDAOData.setEncryptAlgorithm(encryptAlgorithm);
            partnershipDAOData.setMicAlgorithm(micAlgorithm);
            partnershipDAOData.setIsDisabled(isDisabled);
            partnershipDAOData.setRetries(StringUtilities.parseInt(retries));
            partnershipDAOData.setRetryInterval(StringUtilities.parseInt(retryInterval));

            if ("add".equalsIgnoreCase(requestAction)) {
                getPartnership(partnershipDAOData, dom, "add_partnership/");
            }

            if (partnershipId.equals("")) {
                request.setAttribute(ATTR_MESSAGE, "Partnership ID cannot be empty");
                return;
            }
            if (as2From.equals("")) {
                request.setAttribute(ATTR_MESSAGE, "AS2 From cannot be empty");
                return;
            }
            if (as2To.equals("")) {
                request.setAttribute(ATTR_MESSAGE, "AS2 To cannot be empty");
                return;
            }
            if (as2From.length() > 100) {
                request.setAttribute(ATTR_MESSAGE, "AS2 From cannot be longer than 100 characters.");
                return;
            }
            if (as2To.length() > 100) {
                request.setAttribute(ATTR_MESSAGE, "AS2 To cannot be longer than 100 characters.");
                return;
            }
            
            if (partnershipDAOData.getRetries() == Integer.MIN_VALUE) {
                request.setAttribute(ATTR_MESSAGE, "Retries must be integer");
                return;
            }
            if (partnershipDAOData.getRetryInterval() == Integer.MIN_VALUE) {
                request.setAttribute(ATTR_MESSAGE, "Retry Interval must be integer");
                return;
            }

            // encrypt cert
            if (hasRemoveEncryptCert) {
                partnershipDAOData.setEncryptCert(null);
            }
            if (hasEncryptCert) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOHandler.pipe(encryptCertInputStream, baos);
                    CertificateFactory
                            .getInstance("X.509")
                            .generateCertificate(
                                    new ByteArrayInputStream(baos.toByteArray()));
                    partnershipDAOData.setEncryptCert(baos.toByteArray());
                } catch (Exception e) {
                    request.setAttribute(ATTR_MESSAGE,
                            "Uploaded encrypt cert is not an X.509 cert");
                    return;
                }
            }
            // verify cert
            if (hasRemoveVerifyCert) {
                partnershipDAOData.setVerifyCert(null);
            }
            if (hasVerifyCert) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOHandler.pipe(verifyCertInputStream, baos);
                    CertificateFactory
                            .getInstance("X.509")
                            .generateCertificate(
                                    new ByteArrayInputStream(baos.toByteArray()));
                    partnershipDAOData.setVerifyCert(baos.toByteArray());
                } catch (Exception e) {
                    request.setAttribute(ATTR_MESSAGE,
                            "Uploaded verify cert is not an X.509 cert");
                    return;
                }
            }

            // check partnership conflict
            if (!partnershipDAOData.isDisabled()) {
                Iterator allConflictDAOData = partnershipDAO.findPartnershipsByPartyID(partnershipDAOData.getAS2From(), partnershipDAOData.getAs2To()).iterator();
                while (allConflictDAOData.hasNext()) {
                    PartnershipDVO conflictDAOData = (PartnershipDVO)allConflictDAOData.next();  
                    if (conflictDAOData != null && !conflictDAOData.getPartnershipId().equals(partnershipDAOData.getPartnershipId()) && !conflictDAOData.isDisabled()) {
                        request.setAttribute(ATTR_MESSAGE,
                                "Partnership '"+conflictDAOData.getPartnershipId()+"' with same From/To party IDs has already been enabled");
                        return;
                    }
                }
            }
            
            // dao action
            if ("add".equalsIgnoreCase(requestAction)) {
            	
                partnershipDAO.create(partnershipDAOData);
                request.setAttribute(ATTR_MESSAGE, "Partnership added successfully");
                dom.removeProperty("/partnerships/add_partnership");
                dom.setProperty("/partnerships/add_partnership", "");
            }
            if ("update".equalsIgnoreCase(requestAction)) {
                partnershipDAO.persist(partnershipDAOData);
                request.setAttribute(ATTR_MESSAGE, "Partnership updated successfully");
            }
            if ("delete".equalsIgnoreCase(requestAction)) {
                partnershipDAO.remove(partnershipDAOData);
                request.setAttribute(ATTR_MESSAGE, "Partnership deleted successfully");                
            }
        }
    }

    private void getSelectedPartnership(String selectedPartnershipId, PropertyTree dom) 
            throws DAOException, CertificateException, IOException {
        PartnershipDAO partnershipDAO = (PartnershipDAO) AS2PlusProcessor.getInstance().getDAOFactory().createDAO(PartnershipDAO.class);
        PartnershipDVO partnershipDAOData = (PartnershipDVO) partnershipDAO.createDVO();
        partnershipDAOData.setPartnershipId(selectedPartnershipId);
        if (partnershipDAO.retrieve(partnershipDAOData)) {
            getPartnership(partnershipDAOData, dom, "selected_partnership/");
        }
    }
    
    /**
     * @param request
     * @throws DAOException
     * @throws CertificateException
     * @throws IOException
     */
    private void getPartnership(PartnershipDVO partnershipDAOData, PropertyTree dom, String prefix) {

        if (partnershipDAOData!=null) {
            dom.setProperty(prefix+"partnership_id",
                    partnershipDAOData.getPartnershipId());
            String subject = partnershipDAOData.getSubject();
            dom.setProperty(prefix+"subject",
                    subject != null ? subject : "");
            String recipientAddress = partnershipDAOData.getRecipientAddress();
            dom.setProperty(prefix+"recipient_address",
                    recipientAddress != null ? recipientAddress : "");
            dom.setProperty(prefix+"is_hostname_verified", String
                    .valueOf(partnershipDAOData.isHostnameVerified()));
            String receiptAddress = partnershipDAOData.getReceiptAddress();
            dom.setProperty(prefix+"receipt_address",
                    receiptAddress != null ? receiptAddress : "");
            dom.setProperty(prefix+"is_sync_reply", String
                    .valueOf(partnershipDAOData.isSyncReply()));
            dom.setProperty(prefix+"is_receipt_requested", String
                    .valueOf(partnershipDAOData.isReceiptRequired()));
            dom
                    .setProperty(
                            prefix+"is_outbound_sign_required",
                            String.valueOf(partnershipDAOData
                                    .isOutboundSignRequired()));
            dom.setProperty(
                    prefix+"is_outbound_encrypt_required", String
                            .valueOf(partnershipDAOData
                                    .isOutboundEncryptRequired()));
            dom.setProperty(
                    prefix+"is_outbound_compress_required",
                    String.valueOf(partnershipDAOData
                            .isOutboundCompressRequired()));
            dom.setProperty(prefix+"is_receipt_sign_required",
                    String.valueOf(partnershipDAOData.isReceiptSignRequired()));
            dom.setProperty(prefix+"is_inbound_sign_required",
                    String.valueOf(partnershipDAOData.isInboundSignRequired()));
            dom.setProperty(prefix+"is_inbound_encrypt_required",
                    String.valueOf(partnershipDAOData
                            .isInboundEncryptRequired()));
            String signAlgorithm = partnershipDAOData.getSignAlgorithm();
            dom.setProperty(prefix+"sign_algorithm",
                    signAlgorithm != null ? signAlgorithm : "");
            String encryptAlgorithm = partnershipDAOData.getEncryptAlgorithm();
            dom.setProperty(prefix+"encrypt_algorithm",
                    encryptAlgorithm != null ? encryptAlgorithm : "");
            String micAlgorithm = partnershipDAOData.getMicAlgorithm();
            dom.setProperty(prefix+"mic_algorithm",
                    micAlgorithm != null ? micAlgorithm : "");
            dom.setProperty(prefix+"as2_from", partnershipDAOData
                    .getAS2From());
            dom.setProperty(prefix+"as2_to", partnershipDAOData
                    .getAs2To());
            
            getCertificateForPartnership(partnershipDAOData.getEncryptCert(), dom, prefix+"encrypt_cert/");
            getCertificateForPartnership(partnershipDAOData.getVerifyCert(), dom, prefix+"verify_cert/");

            dom.setProperty(prefix+"retries", formatInteger(partnershipDAOData.getRetries()));
            dom.setProperty(prefix+"retry_interval", formatInteger(partnershipDAOData.getRetryInterval()));
            dom.setProperty(prefix+"disabled", String.valueOf(partnershipDAOData.isDisabled()));
        }
    }

    private void getCertificateForPartnership(byte[] cert, PropertyTree dom, String prefix) {
        if (cert != null) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(cert);
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                X509Certificate verifyCert = (X509Certificate) cf.generateCertificate(bais);
                bais.close();
                dom.setProperty(prefix+"issuer", verifyCert.getIssuerDN().getName());
                dom.setProperty(prefix+"subject", verifyCert.getSubjectDN().getName());
                dom.setProperty(prefix+"thumbprint", getCertFingerPrint(verifyCert));
                dom.setProperty(prefix+"valid-from", StringUtilities.toGMTString(verifyCert.getNotBefore()));
                dom.setProperty(prefix+"valid-to", StringUtilities.toGMTString(verifyCert.getNotAfter()));
            }
            catch (Exception e) {
                dom.setProperty(prefix+"Error", e.toString());
            }
        } else {
            dom.setProperty(prefix, "");
        }
    }
    
    private String formatInteger(int i) {
        if (i == Integer.MIN_VALUE) {
            return "";
        }
        else {
            return String.valueOf(i);
        }
    }
    
    /**
     * @return
     * @throws DAOException
     */
    private void getAllPartnerships(PropertyTree dom) throws DAOException {

        PartnershipDAO partnershipDAO = (PartnershipDAO) AS2PlusProcessor.getInstance().getDAOFactory()
                .createDAO(PartnershipDAO.class);

        Iterator i = partnershipDAO.findAllPartnerships().iterator();
        for (int pi = 1; i.hasNext(); pi++) {
            PartnershipDVO partnershipDAOData = (PartnershipDVO) i
                    .next();
            dom.setProperty("partnership[" + pi + "]/partnership_id",
                    partnershipDAOData.getPartnershipId());
            dom.setProperty("partnership[" + pi + "]/as2_from",
                    partnershipDAOData.getAS2From());
            dom.setProperty("partnership[" + pi + "]/as2_to",
                    partnershipDAOData.getAs2To());
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
    
    private String getCertFingerPrint(X509Certificate cert)
    {
        try {
            String mdAlg;
            if (cert.getSigAlgName().toUpperCase().startsWith("SHA")) {
                mdAlg = "SHA";
            }
            else {
                mdAlg = "MD5";
            }
            byte[] encCertInfo = cert.getEncoded();
            MessageDigest md = MessageDigest.getInstance(mdAlg);
            byte[] digest = md.digest(encCertInfo);
            return toHexString(digest);
        }
        catch (Exception e) {
            return e.toString();
        }
    }
    
    private String toHexString(byte[] buf)
    {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < buf.length; i++)
        {
            String HEX = "0123456789abcdef";
            str.append(HEX.charAt(buf[i] >>> 4 & 0x0F));
            str.append(HEX.charAt(buf[i] & 0x0F));
        }
        return str.toString();
    }
}