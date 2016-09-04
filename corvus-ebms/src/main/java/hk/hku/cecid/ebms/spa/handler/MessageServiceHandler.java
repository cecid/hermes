/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.ebms.spa.handler;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;
import hk.hku.cecid.ebms.pkg.SignatureException;
import hk.hku.cecid.ebms.pkg.SignatureHandler;
import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.ebms.spa.listener.EbmsRequest;
import hk.hku.cecid.ebms.spa.listener.EbmsResponse;
import hk.hku.cecid.piazza.commons.module.ActiveModule;
import hk.hku.cecid.piazza.commons.security.KeyStoreManagementException;
import hk.hku.cecid.piazza.commons.security.KeyStoreTrustManager;

import java.security.cert.Certificate;
import java.util.Properties;

/**
 * EbxmlMessageDAOConvertor Service Handler that supports asynchronous
 * communication and reliable messaging.
 * 
 * @author cyng
 * @version $Revision: 1.6 $
 */
public class MessageServiceHandler {

    private static MessageServiceHandler messageServiceHandler;

    private boolean hasSmtp = false;

    private boolean hasPop = false;

    private boolean isInboundAgreementCheck = false;

    private boolean isOutboundAgreementCheck = false;

    private boolean isSignHeaderOnly = false;

    private static String dsUsername;

    private static char[] dsPassword;

    private static String dsKeyStoreLocation;

    // private static String dsStoreType;

    // private static String dsProvider;

    public String smtpHost;

    public String smtpProtocol;

    public String smtpPort;

    public String smtpFromMailAddress;

    public String smtpUsername;

    public String smtpPassword;

    public String popHost;

    public String popProtocol;

    public String popPort;

    public String popFolder;

    public String popUsername;

    public String popPassword;

    /*
    private String XPATH_DELIVERY_MANAGER = "/ebms/delivery_manager";

    private String XPATH_OUTBOX_DELIVERY_MANAGER = "/outbox_delivery_manager";

    private String XPATH_INBOX_DELIVERY_MANAGER = "/inbox_delivery_manager";

    private String XPATH_DELIVERY_MANAGER_INTERVAL = "/delivery_manager_interval";

    private String XPATH_MAX_NUM_THREAD_LIST = "/max_thread_count";
    */

    private String XPATH_USERNAME = "/username";

    private String XPATH_PASSWORD = "/password";

    private String XPATH_MAIL = "/ebms/mail";

    private String XPATH_ENABLE = "/enable";

    private String XPATH_SMTP = "/smtp";

    private String XPATH_POP = "/pop";

    private String XPATH_HOST = "/host";

    private String XPATH_FROM_MAIL_ADDRESS = "/from_mail_address";

    private String XPATH_PROTOCOL = "/protocol";

    private String XPATH_PORT = "/port";

    private String XPATH_FOLDER = "/folder";

    /*
    private String XPATH_SMIME = "/smime";

    private String XPATH_ENCRYPTION = "/encryption";

    private String XPATH_DECRYPTION = "/decryption";

    private String XPATH_KEY_STORE_PASSWORD = "/key_store_password";

    private String XPATH_ALIAS = "/alias";

    private String XPATH_KEY_PASSWORD = "/key_password";
    */

    private String XPATH_INBOUND_AGREEMENT_CHECK = "/ebms/inbound_agreement_check";

    private String XPATH_OUTBOUND_AGREEMENT_CHECK = "/ebms/outbound_agreement_check";

    private String XPATH_SIGN_HEADER_ONLY = "/ebms/sign_header_only";

    public String INBOX = "inbox";

    public String OUTBOX = "outbox";

    public String THREAD_COUNT = "threadCount";

    public String PEEK_THREAD_COUNT = "peekThreadCount";

    private MessageServiceHandler() {

        // digital signature
        try {
            Properties signparams = EbmsProcessor.getKeyStoreManagerForSignature().getParameters();
            dsUsername = signparams.getProperty("key-alias");
            dsPassword = signparams.getProperty("keystore-password").toCharArray();
            dsKeyStoreLocation = signparams.getProperty("keystore-location");
            /*
            dsStoreType = signparams.getProperty("keystore-type");
            dsProvider = signparams.getProperty("keystore-provider");
            */
        } catch (Exception e) {
            EbmsProcessor.core.log.debug(
                    "Error in getting key store for signature", e);
        }

        // smtp server
        String smtpEnable = EbmsProcessor.core.properties
                .getProperty(XPATH_MAIL + XPATH_SMTP + XPATH_ENABLE);
        if (smtpEnable.equalsIgnoreCase("true")) {
            hasSmtp = true;
            smtpHost = EbmsProcessor.core.properties.getProperty(XPATH_MAIL
                    + XPATH_SMTP + XPATH_HOST);

            smtpProtocol = EbmsProcessor.core.properties.getProperty(XPATH_MAIL
                    + XPATH_SMTP + XPATH_PROTOCOL);

            smtpPort = EbmsProcessor.core.properties.getProperty(XPATH_MAIL
                    + XPATH_SMTP + XPATH_PORT);

            smtpFromMailAddress = EbmsProcessor.core.properties
                    .getProperty(XPATH_MAIL + XPATH_SMTP
                            + XPATH_FROM_MAIL_ADDRESS);

            smtpUsername = EbmsProcessor.core.properties.getProperty(XPATH_MAIL
                    + XPATH_SMTP + XPATH_USERNAME);

            smtpPassword = EbmsProcessor.core.properties.getProperty(XPATH_MAIL
                    + XPATH_SMTP + XPATH_PASSWORD);
            EbmsProcessor.core.log.debug("Smtp Server Setting: ");
            EbmsProcessor.core.log.debug("- Smtp Host: " + smtpHost);
            EbmsProcessor.core.log.debug("- Smtp Protocol: " + smtpProtocol);
            EbmsProcessor.core.log.debug("- Smtp Port: " + smtpPort);
            EbmsProcessor.core.log.debug("- Smtp From Mail Address: "
                    + smtpFromMailAddress);
            EbmsProcessor.core.log.debug("- Smtp Username: " + smtpUsername);
            EbmsProcessor.core.log.debug("- Smtp Password: " + smtpPassword);
        }

        // pop server
        String popEnable = EbmsProcessor.core.properties.getProperty(XPATH_MAIL
                + XPATH_POP + XPATH_ENABLE);

        if (popEnable.equalsIgnoreCase("true")) {
            hasPop = true;
            popHost = EbmsProcessor.core.properties.getProperty(XPATH_MAIL
                    + XPATH_POP + XPATH_HOST);

            popProtocol = EbmsProcessor.core.properties.getProperty(XPATH_MAIL
                    + XPATH_POP + XPATH_PROTOCOL);

            popPort = EbmsProcessor.core.properties.getProperty(XPATH_MAIL
                    + XPATH_POP + XPATH_PORT);

            popFolder = EbmsProcessor.core.properties.getProperty(XPATH_MAIL
                    + XPATH_POP + XPATH_FOLDER);

            popUsername = EbmsProcessor.core.properties.getProperty(XPATH_MAIL
                    + XPATH_POP + XPATH_USERNAME);

            popPassword = EbmsProcessor.core.properties.getProperty(XPATH_MAIL
                    + XPATH_POP + XPATH_PASSWORD);
            ((ActiveModule) EbmsProcessor.getModuleGroup().getModule(
                    "ebms.mail-collector")).start();
            EbmsProcessor.core.log.info("Mail Collector started - Host: "
                    + popHost);
            EbmsProcessor.core.log.debug("Pop Server Setting: ");
            EbmsProcessor.core.log.debug("- Pop Host: " + popHost);
            EbmsProcessor.core.log.debug("- Pop Protocol: " + popProtocol);
            EbmsProcessor.core.log.debug("- Pop Port: " + popPort);
            EbmsProcessor.core.log.debug("- Pop Folder: " + popFolder);
            EbmsProcessor.core.log.debug("- Pop Username: " + popUsername);
            EbmsProcessor.core.log.debug("- Pop Password: " + popPassword);
        }

        // optional properties for interop
        String inboundAgreementCheck = EbmsProcessor.core.properties
                .getProperty(XPATH_INBOUND_AGREEMENT_CHECK);
        if (inboundAgreementCheck != null) {
            if (inboundAgreementCheck.equalsIgnoreCase("true")) {
                isInboundAgreementCheck = true;
            }
        }
        String outboundAgreementCheck = EbmsProcessor.core.properties
                .getProperty(XPATH_OUTBOUND_AGREEMENT_CHECK);
        if (outboundAgreementCheck != null) {
            if (outboundAgreementCheck.equalsIgnoreCase("true")) {
                isOutboundAgreementCheck = true;
            }
        }
        String signHeaderOnly = EbmsProcessor.core.properties
                .getProperty(XPATH_SIGN_HEADER_ONLY);
        if (signHeaderOnly != null) {
            if (signHeaderOnly.equalsIgnoreCase("true")) {
                isSignHeaderOnly = true;
            }
        }
    }

    static boolean messageServiceHandler_initFlag = false;

    public synchronized static MessageServiceHandler getInstance() {
        if (!messageServiceHandler_initFlag) {
            EbmsProcessor.core.log.debug("Message service handler started");
            messageServiceHandler = new MessageServiceHandler();
            messageServiceHandler_initFlag = true;
        }
        return messageServiceHandler;
    }

    static boolean messageServiceHandler_destroyFlag = false;

    public synchronized void destroy() {
        if (!messageServiceHandler_destroyFlag) {
            messageServiceHandler_destroyFlag = true;
            EbmsProcessor.core.log.debug("Message service handler stopped");
        }
    }

    public void processInboundMessage(EbmsRequest request, EbmsResponse response)
            throws MessageServiceHandlerException {
        InboundMessageProcessor inboundMessageProcessor = InboundMessageProcessor
                .getInstance();
        inboundMessageProcessor.processIncomingMessage(request, response);
    }

    public void processOutboundMessage(EbmsRequest request,
            EbmsResponse response) throws MessageServiceHandlerException {
        OutboundMessageProcessor outboundMessageProcessor = 
        	OutboundMessageProcessor.getInstance();
        outboundMessageProcessor.processOutgoingMessage(request, response);
    }

    public static SignatureHandler createSignatureHandler(EbxmlMessage message)
            throws SignatureException {
        
        KeyStoreTrustManager trustman;
        try {
            trustman = new KeyStoreTrustManager(EbmsProcessor.getKeyStoreManagerForSignature());
        }
        catch (KeyStoreManagementException e) {
            throw new SignatureException("Unable to create keystore trust manager for signature", e);
        }
        return new SignatureHandler(message, dsUsername, dsPassword,
                dsKeyStoreLocation, trustman);
    }

    public static SignatureHandler createSignatureHandler(EbxmlMessage message,
            Certificate certificate) throws SignatureException {
        return new SignatureHandler(message, certificate);
    }

    /**
     * @return Returns the hasSmtp.
     */
    public boolean isHasSmtp() {
        return hasSmtp;
    }

    /**
     * @return Returns the hasPop.
     */
    public boolean isHasPop() {
        return hasPop;
    }

    /**
     * @return Returns the isInboundAgreementCheck.
     */
    public boolean isInboundAgreementCheck() {
        return isInboundAgreementCheck;
    }

    /**
     * @return Returns the isSignHeaderOnly.
     */
    public boolean isSignHeaderOnly() {
        return isSignHeaderOnly;
    }
    
    /**
     * @return Returns the isOutboundAgreementCheck.
     */
    public boolean isOutboundAgreementCheck() {
        return isOutboundAgreementCheck;
    }
}