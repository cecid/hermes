/*
 * Created on Oct 27, 2004
 *
 */
package hk.hku.cecid.ebms.admin.listener;

import hk.hku.cecid.ebms.spa.EbmsProcessor;
import hk.hku.cecid.piazza.commons.module.ActiveMonitor;
import hk.hku.cecid.piazza.commons.module.ActiveTaskModule;
import hk.hku.cecid.piazza.commons.module.ComponentException;
import hk.hku.cecid.piazza.commons.util.PropertySheet;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.commons.util.UtilitiesException;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

/**
 * @author Donahue Sze
 * 
 */
public class EbmsPropertiesPageletAdaptor extends AdminPageletAdaptor {

    private static final String REQ_PARAM_PROPERTY = "property:";

    private String XPATH_DELIVERY_MANAGER = "/ebms/delivery_manager";

    private String XPATH_OUTBOX_DELIVERY_MANAGER = "/outbox_delivery_manager";

    private String XPATH_INBOX_DELIVERY_MANAGER = "/inbox_delivery_manager";

    private String XPATH_DELIVERY_MANAGER_INTERVAL = "/delivery_manager_interval";

    private String XPATH_MAX_NUM_THREAD_LIST = "/max_thread_count";

    private String XPATH_DIGITAL_SIGNATURE = "/ebms/digital_signature";

    private String XPATH_USERNAME = "/username";

    private String XPATH_PASSWORD = "/password";

    private String XPATH_KEY_STORE_LOCATION = "/key_store_location";

    private String XPATH_STORE_TYPE = "/store_type";

    private String XPATH_PROVIDER = "/provider";

    private String XPATH_MAIL = "/ebms/mail";

    private String XPATH_SMTP = "/smtp";

    private String XPATH_POP = "/pop";

    private String XPATH_HOST = "/host";

    private String XPATH_FROM_MAIL_ADDRESS = "/from_mail_address";

    private String XPATH_PROTOCOL = "/protocol";

    private String XPATH_PORT = "/port";

    private String XPATH_FOLDER = "/folder";

    private String XPATH_SMIME = "/smime";

    private String XPATH_DECRYPTION = "/decryption";

    private String XPATH_KEY_STORE_PASSWORD = "/key_store_password";

    private String XPATH_ALIAS = "/alias";

    private String XPATH_KEY_PASSWORD = "/key_password";

    private PropertyTree outboxProperties;

    private PropertyTree inboxProperties;

    private PropertyTree mailProperties;

    private PropertyTree systemProperties;

    /*
     * (non-Javadoc)
     * 
     * @see hk.hku.cecid.piazza.commons.pagelet.xslt.BorderLayoutPageletAdaptor#getCenterSource(javax.servlet.http.HttpServletRequest)
     */
    protected Source getCenterSource(HttpServletRequest request) {

        PropertySheet props;
        PropertyTree resultDom = new PropertyTree();

        try {
            URL systemPropertiesUrl = EbmsProcessor.getModuleGroup()
                    .getSystemModule().getDescriptor();
            systemProperties = new PropertyTree(systemPropertiesUrl);
            URL outboxPropertiesUrl = EbmsProcessor.getModuleGroup().getModule(
                    EbmsProcessor.ACTIVE_MODULE_OUTBOX_COLLECTOR)
                    .getDescriptor();
            outboxProperties = new PropertyTree(outboxPropertiesUrl);
            URL inboxPropertiesUrl = EbmsProcessor.getModuleGroup().getModule(
                    EbmsProcessor.ACTIVE_MODULE_INBOX_COLLECTOR)
                    .getDescriptor();
            inboxProperties = new PropertyTree(inboxPropertiesUrl);
            URL mailPropertiesUrl = EbmsProcessor.getModuleGroup().getModule(
                    EbmsProcessor.ACTIVE_MODULE_MAIL_COLLECTOR).getDescriptor();
            mailProperties = new PropertyTree(mailPropertiesUrl);
            props = getEbmsProperties();

            updateEbmsProperties(request, props);
            resultDom = getProperties(props);
        } catch (Exception e) {
            request.setAttribute(ATTR_MESSAGE,
                    "Unable to process the request: " + e.getMessage());
            throw new RuntimeException(
                    "Error in processing ebms properties pagelet", e);
        }
        return resultDom.getSource();
    }

    /**
     * @param request
     * @throws Exception
     */
    private void updateEbmsProperties(HttpServletRequest request,
            PropertySheet props) throws Exception {

        String requestAction = request.getParameter(REQ_PARAM_ACTION);

        if (request.getMethod().equalsIgnoreCase("post")) {

            if ("update".equalsIgnoreCase(requestAction)) {

                // outbox
                String outboxInterval = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_DELIVERY_MANAGER
                        + XPATH_OUTBOX_DELIVERY_MANAGER
                        + XPATH_DELIVERY_MANAGER_INTERVAL);
                if (outboxInterval.equals("")) {
                    request.setAttribute(ATTR_MESSAGE,
                            "Outbox Collection Interval cannot be empty");
                    return;
                } else if (!isInteger(outboxInterval)) {
                    request.setAttribute(ATTR_MESSAGE,
                            "Outbox Collection Interval must be integer");
                    return;
                } else {
                    outboxProperties
                            .setProperty(
                                    "/module/parameters/parameter[@name='execution-interval']/@value",
                                    outboxInterval);
                }

                String outboxMaxThread = request
                        .getParameter(REQ_PARAM_PROPERTY
                                + XPATH_DELIVERY_MANAGER
                                + XPATH_OUTBOX_DELIVERY_MANAGER
                                + XPATH_MAX_NUM_THREAD_LIST);
                if (outboxMaxThread.equals("")) {
                    request.setAttribute(ATTR_MESSAGE,
                            "Outbox Maximum Number of Threads cannot be empty");
                    return;
                } else if (!isInteger(outboxMaxThread)) {
                    request.setAttribute(ATTR_MESSAGE,
                            "Outbox Maximum Number of Threads must be integer");
                    return;
                } else {
                    outboxProperties
                            .setProperty(
                                    "/module/component/parameter[@name='max-thread-count']/@value",
                                    outboxMaxThread);
                }

                // inbox
                String inboxInterval = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_DELIVERY_MANAGER + XPATH_INBOX_DELIVERY_MANAGER
                        + XPATH_DELIVERY_MANAGER_INTERVAL);
                if (inboxInterval.equals("")) {
                    request.setAttribute(ATTR_MESSAGE,
                            "Inbox Collection Interval cannot be empty");
                    return;
                } else if (!isInteger(inboxInterval)) {
                    request.setAttribute(ATTR_MESSAGE,
                            "Inbox Collection Interval must be integer");
                    return;
                } else {
                    inboxProperties
                            .setProperty(
                                    "/module/parameters/parameter[@name='execution-interval']/@value",
                                    inboxInterval);
                }

                // digital signature
                String digitalSignatureKeyStoreLocation = request
                        .getParameter(REQ_PARAM_PROPERTY
                                + XPATH_DIGITAL_SIGNATURE
                                + XPATH_KEY_STORE_LOCATION);
                systemProperties
                        .setProperty(
                                "/module/component[@id='keystore-manager-for-signature']/parameter[@name='keystore-location']/@value",
                                digitalSignatureKeyStoreLocation);

                String digitalSignatureUsername = request
                        .getParameter(REQ_PARAM_PROPERTY
                                + XPATH_DIGITAL_SIGNATURE + XPATH_USERNAME);
                systemProperties
                        .setProperty(
                                "/module/component[@id='keystore-manager-for-signature']/parameter[@name='key-alias']/@value",
                                digitalSignatureUsername);

                String digitalSignaturePassword = request
                        .getParameter(REQ_PARAM_PROPERTY
                                + XPATH_DIGITAL_SIGNATURE + XPATH_PASSWORD);
                systemProperties
                        .setProperty(
                                "/module/component[@id='keystore-manager-for-signature']/parameter[@name='keystore-password']/@value",
                                digitalSignaturePassword);

                String digitalSignatureStoreType = request
                        .getParameter(REQ_PARAM_PROPERTY
                                + XPATH_DIGITAL_SIGNATURE + XPATH_STORE_TYPE);
                systemProperties
                        .setProperty(
                                "/module/component[@id='keystore-manager-for-signature']/parameter[@name='keystore-type']/@value",
                                digitalSignatureStoreType);

                String digitalSignatureProvider = request
                        .getParameter(REQ_PARAM_PROPERTY
                                + XPATH_DIGITAL_SIGNATURE + XPATH_PROVIDER);
                systemProperties
                        .setProperty(
                                "/module/component[@id='keystore-manager-for-signature']/parameter[@name='keystore-provider']/@value",
                                digitalSignatureProvider);

                // smtp
                String smtpHost = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_SMTP + XPATH_HOST);
                props.setProperty(XPATH_MAIL + XPATH_SMTP + XPATH_HOST,
                        smtpHost);

                String smtpProtocol = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_SMTP + XPATH_PROTOCOL);
                props.setProperty(XPATH_MAIL + XPATH_SMTP + XPATH_PROTOCOL,
                        smtpProtocol);

                String smtpPort = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_SMTP + XPATH_PORT);

                if (!smtpPort.equals("")) {
                    if (!isInteger(smtpPort)) {
                        request.setAttribute(ATTR_MESSAGE,
                                "SMTP Port must be integer");
                        return;
                    } else {
                        props.setProperty(XPATH_MAIL + XPATH_SMTP + XPATH_PORT,
                                smtpPort);
                    }
                }

                String smtpFromMailAddress = request
                        .getParameter(REQ_PARAM_PROPERTY + XPATH_MAIL
                                + XPATH_SMTP + XPATH_FROM_MAIL_ADDRESS);
                props.setProperty(XPATH_MAIL + XPATH_SMTP
                        + XPATH_FROM_MAIL_ADDRESS, smtpFromMailAddress);

                String smtpUserName = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_SMTP + XPATH_USERNAME);
                props.setProperty(XPATH_MAIL + XPATH_SMTP + XPATH_USERNAME,
                        smtpUserName);

                String smtpPassword = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_SMTP + XPATH_PASSWORD);
                props.setProperty(XPATH_MAIL + XPATH_SMTP + XPATH_PASSWORD,
                        smtpPassword);

                // pop
                String popHost = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_POP + XPATH_HOST);
                props.setProperty(XPATH_MAIL + XPATH_POP + XPATH_HOST, popHost);

                String popProtocol = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_POP + XPATH_PROTOCOL);
                props.setProperty(XPATH_MAIL + XPATH_POP + XPATH_PROTOCOL,
                        popProtocol);

                String popPort = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_POP + XPATH_PORT);
                if (!popPort.equals("")) {
                    if (!isInteger(popPort)) {
                        request.setAttribute(ATTR_MESSAGE,
                                "POP Port must be integer");
                        return;
                    } else {
                        props.setProperty(XPATH_MAIL + XPATH_POP + XPATH_PORT,
                                popPort);
                    }
                }

                String popFolder = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_POP + XPATH_FOLDER);
                props.setProperty(XPATH_MAIL + XPATH_POP + XPATH_FOLDER,
                        popFolder);

                String popUserName = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_POP + XPATH_USERNAME);
                props.setProperty(XPATH_MAIL + XPATH_POP + XPATH_USERNAME,
                        popUserName);

                String popPassword = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_POP + XPATH_PASSWORD);
                props.setProperty(XPATH_MAIL + XPATH_POP + XPATH_PASSWORD,
                        popPassword);

                String mailInterval = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_POP
                        + XPATH_DELIVERY_MANAGER_INTERVAL);
                if (mailInterval.equals("")) {
                    request.setAttribute(ATTR_MESSAGE,
                            "POP Mail Polling Interval cannot be empty");
                    return;
                } else if (!isInteger(mailInterval)) {
                    request.setAttribute(ATTR_MESSAGE,
                            "POP Mail Polling Interval must be integer");
                    return;
                } else {
                    mailProperties
                            .setProperty(
                                    "/module/parameters/parameter[@name='execution-interval']/@value",
                                    mailInterval);
                }

                // smime decryption
                String smimeKeyStoreLocation = request
                        .getParameter(REQ_PARAM_PROPERTY + XPATH_MAIL
                                + XPATH_SMIME + XPATH_DECRYPTION
                                + XPATH_KEY_STORE_LOCATION);
                systemProperties
                        .setProperty(
                                "/module/component[@id='keystore-manager-for-decryption']/parameter[@name='keystore-location']/@value",
                                smimeKeyStoreLocation);

                String smimeKeyStorePassword = request
                        .getParameter(REQ_PARAM_PROPERTY + XPATH_MAIL
                                + XPATH_SMIME + XPATH_DECRYPTION
                                + XPATH_KEY_STORE_PASSWORD);
                systemProperties
                        .setProperty(
                                "/module/component[@id='keystore-manager-for-decryption']/parameter[@name='keystore-password']/@value",
                                smimeKeyStorePassword);

                String smimeAlias = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_SMIME + XPATH_DECRYPTION
                        + XPATH_ALIAS);
                systemProperties
                        .setProperty(
                                "/module/component[@id='keystore-manager-for-decryption']/parameter[@name='key-alias']/@value",
                                smimeAlias);

                String smimeKeyPassword = request
                        .getParameter(REQ_PARAM_PROPERTY + XPATH_MAIL
                                + XPATH_SMIME + XPATH_DECRYPTION
                                + XPATH_KEY_PASSWORD);
                systemProperties
                        .setProperty(
                                "/module/component[@id='keystore-manager-for-decryption']/parameter[@name='key-password']/@value",
                                smimeKeyPassword);

                String smimeStoreType = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_SMIME + XPATH_DECRYPTION
                        + XPATH_STORE_TYPE);
                systemProperties
                        .setProperty(
                                "/module/component[@id='keystore-manager-for-decryption']/parameter[@name='keystore-type']/@value",
                                smimeStoreType);

                String smimeProvider = request.getParameter(REQ_PARAM_PROPERTY
                        + XPATH_MAIL + XPATH_SMIME + XPATH_DECRYPTION
                        + XPATH_PROVIDER);
                systemProperties
                        .setProperty(
                                "/module/component[@id='keystore-manager-for-decryption']/parameter[@name='keystore-provider']/@value",
                                smimeProvider);

                if ("update".equalsIgnoreCase(requestAction)) {
                    outboxProperties.store();
                    inboxProperties.store();
                    mailProperties.store();
                    systemProperties.store();
                    props.store();
                    request.setAttribute(ATTR_MESSAGE,
                            "Ebms properties updated successfully");
                }

            }
        }
    }

    /**
     * @param dom
     */
    private PropertyTree getProperties(PropertySheet props) {
        // construct the return xml from ebms.properties.xml and
        // set current thread count & peek thread count to the return xml
        // this two elements will not saved on the ebms.properties.xml

        PropertyTree resultDom = new PropertyTree();

        Enumeration names = props.propertyNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement().toString();
            resultDom.setProperty(name, props.getProperty(name));
        }

        String outboxMaxThread = outboxProperties
                .getProperty("/module/component/parameter[@name='max-thread-count']/@value");
        resultDom.setProperty(XPATH_DELIVERY_MANAGER
                + XPATH_OUTBOX_DELIVERY_MANAGER + XPATH_MAX_NUM_THREAD_LIST,
                outboxMaxThread);

        String outboxInterval = outboxProperties
                .getProperty("/module/parameters/parameter[@name='execution-interval']/@value");
        resultDom.setProperty(XPATH_DELIVERY_MANAGER
                + XPATH_OUTBOX_DELIVERY_MANAGER
                + XPATH_DELIVERY_MANAGER_INTERVAL, outboxInterval);

        String inboxInterval = inboxProperties
                .getProperty("/module/parameters/parameter[@name='execution-interval']/@value");
        resultDom.setProperty(XPATH_DELIVERY_MANAGER
                + XPATH_INBOX_DELIVERY_MANAGER
                + XPATH_DELIVERY_MANAGER_INTERVAL, inboxInterval);

        String mailInterval = mailProperties
                .getProperty("/module/parameters/parameter[@name='execution-interval']/@value");
        resultDom.setProperty(XPATH_MAIL + XPATH_POP
                + XPATH_DELIVERY_MANAGER_INTERVAL, mailInterval);

        // digital signature
        String digitalSignatureKeyStoreLocation = systemProperties
                .getProperty("/module/component[@id='keystore-manager-for-signature']/parameter[@name='keystore-location']/@value");
        resultDom.setProperty(XPATH_DIGITAL_SIGNATURE
                + XPATH_KEY_STORE_LOCATION, digitalSignatureKeyStoreLocation);

        String digitalSignatureUsername = systemProperties
                .getProperty("/module/component[@id='keystore-manager-for-signature']/parameter[@name='key-alias']/@value");
        resultDom.setProperty(XPATH_DIGITAL_SIGNATURE + XPATH_USERNAME,
                digitalSignatureUsername);

        String digitalSignaturePassword = systemProperties
                .getProperty("/module/component[@id='keystore-manager-for-signature']/parameter[@name='keystore-password']/@value");
        resultDom.setProperty(XPATH_DIGITAL_SIGNATURE + XPATH_PASSWORD,
                digitalSignaturePassword);

        String digitalSignatureStoreType = systemProperties
                .getProperty("/module/component[@id='keystore-manager-for-signature']/parameter[@name='keystore-type']/@value");
        resultDom.setProperty(XPATH_DIGITAL_SIGNATURE + XPATH_STORE_TYPE,
                digitalSignatureStoreType);

        String digitalSignatureProvider = systemProperties
                .getProperty("/module/component[@id='keystore-manager-for-signature']/parameter[@name='keystore-provider']/@value");
        resultDom.setProperty(XPATH_DIGITAL_SIGNATURE + XPATH_PROVIDER,
                digitalSignatureProvider);

        // smime decryption
        String smimeKeyStoreLocation = systemProperties
                .getProperty("/module/component[@id='keystore-manager-for-decryption']/parameter[@name='keystore-location']/@value");
        resultDom.setProperty(XPATH_MAIL + XPATH_SMIME + XPATH_DECRYPTION
                + XPATH_KEY_STORE_LOCATION, smimeKeyStoreLocation);

        String smimeKeyStorePassword = systemProperties
                .getProperty("/module/component[@id='keystore-manager-for-decryption']/parameter[@name='keystore-password']/@value");
        resultDom.setProperty(XPATH_MAIL + XPATH_SMIME + XPATH_DECRYPTION
                + XPATH_KEY_STORE_PASSWORD, smimeKeyStorePassword);

        String smimeAlias = systemProperties
                .getProperty("/module/component[@id='keystore-manager-for-decryption']/parameter[@name='key-alias']/@value");
        resultDom.setProperty(XPATH_MAIL + XPATH_SMIME + XPATH_DECRYPTION
                + XPATH_ALIAS, smimeAlias);

        String smimeKeyPassword = systemProperties
                .getProperty("/module/component[@id='keystore-manager-for-decryption']/parameter[@name='key-password']/@value");
        resultDom.setProperty(XPATH_MAIL + XPATH_SMIME + XPATH_DECRYPTION
                + XPATH_KEY_PASSWORD, smimeKeyPassword);

        String smimeStoreType = systemProperties
                .getProperty("/module/component[@id='keystore-manager-for-decryption']/parameter[@name='keystore-type']/@value");
        resultDom.setProperty(XPATH_MAIL + XPATH_SMIME + XPATH_DECRYPTION
                + XPATH_STORE_TYPE, smimeStoreType);

        String smimeProvider = systemProperties
                .getProperty("/module/component[@id='keystore-manager-for-decryption']/parameter[@name='keystore-provider']/@value");
        resultDom.setProperty(XPATH_MAIL + XPATH_SMIME + XPATH_DECRYPTION
                + XPATH_PROVIDER, smimeProvider);

        // dynamic information
        ActiveMonitor outboxMonitor = ((ActiveTaskModule) EbmsProcessor
                .getModuleGroup().getModule("ebms.outbox-collector"))
                .getMonitor();

        String currentThreadCount = String.valueOf(outboxMonitor
                .getThreadCount());
        String peekThreadCount = String.valueOf(outboxMonitor
                .getPeekThreadCount());
        resultDom.setProperty(XPATH_DELIVERY_MANAGER
                + XPATH_OUTBOX_DELIVERY_MANAGER + "/current_thread_count",
                currentThreadCount);
        resultDom.setProperty(XPATH_DELIVERY_MANAGER
                + XPATH_OUTBOX_DELIVERY_MANAGER + "/peek_thread_count",
                peekThreadCount);

        return resultDom;
    }

    /**
     * @return
     * @throws UtilitiesException
     * @throws ComponentException
     * @throws MalformedURLException
     */
    private PropertySheet getEbmsProperties() throws UtilitiesException,
            ComponentException, MalformedURLException {
        String ebmsPropertiesPath = EbmsProcessor.getModuleGroup()
                .getSystemModule().getComponent("properties").getParameters()
                .getProperty("config");
        URL url = EbmsProcessor.core.getResource(ebmsPropertiesPath);
        PropertySheet propertySheet = new PropertyTree(url);
        return propertySheet;
    }

    /**
     * @param value
     * @return
     */
    private boolean isInteger(String value) {
        try {
            Integer.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}