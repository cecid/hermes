/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.pkg;

import hk.hku.cecid.piazza.commons.security.SMimeMessage;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

import java.util.Enumeration;

import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * DispositionNotification represents an AS2 disposition notification.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class DispositionNotification {

    public static final String REPORTING_UA         = "Reporting-UA";

    public static final String ORIG_RECIPIENT       = "Original-Recipient";

    public static final String FINAL_RECIPIENT      = "Final-Recipient";

    public static final String ORIG_MESSAGE_ID      = "Original-Message-ID";

    public static final String DISPOSITION          = "Disposition";

    public static final String RECEIVED_CONTENT_MIC = "Received-Content-MIC";

    private static final String DISCLAIMER          = "This is not a guarantee that the message has been completely processed or understood by the receiving translator";
    
    private static final String CONTENT_SUBTYPE     = "report; report-type=disposition-notification";  
    
    private MimeMultipart      multiPart;

    private MimeBodyPart       textPart;

    private MimeBodyPart       reportPart;

    private InternetHeaders    reportValues;

    public DispositionNotification() throws AS2MessageException {
        multiPart = new MimeMultipart();
        textPart = new MimeBodyPart();
        reportPart = new MimeBodyPart();
        reportValues = new InternetHeaders();
        try {
            multiPart.setSubType(CONTENT_SUBTYPE);
            multiPart.addBodyPart(textPart);
            multiPart.addBodyPart(reportPart);
        }
        catch (MessagingException e) {
            throw new AS2MessageException("Unable to construct a new disposition notification", e);
        }
    }

    DispositionNotification(AS2Message as2Message) throws AS2MessageException {
        this();
        try {
            SMimeMessage smime = new SMimeMessage(as2Message.getBodyPart());
            if (smime.isSigned()) {
                smime = smime.unsign();
            }
            
            MimeMultipart multiPartContent;
            if (smime.getBodyPart().getContent() instanceof MimeMultipart) {
                multiPartContent = (MimeMultipart)smime.getBodyPart().getContent();
            }
            else {
                throw new AS2MessageException("Invalid message content: "
                        + smime.getBodyPart().getContentType());
            }

            if (multiPartContent.getContentType().toLowerCase().startsWith(
                    AS2Header.CONTENT_TYPE_MULTIPART_REPORT.toLowerCase())) {
                parseMDN(multiPartContent);
            }
            else {
                throw new AS2MessageException("Invalid content type: "
                        + multiPartContent.getContentType());
            }
        }
        catch (Exception e) {
            throw new AS2MessageException("Unable to parse the AS2 MDN", e);
        }
    }

    private void parseMDN(MimeMultipart report) throws AS2MessageException {
        try {
            int reportCount = report.getCount();
            for (int i = 0; i < reportCount; i++) {
                MimeBodyPart reportsPart = (MimeBodyPart) report.getBodyPart(i);
                if (reportsPart.isMimeType("text/plain")) {
                    setText(reportsPart.getContent().toString());
                }
                else if (reportsPart.isMimeType(AS2Header.CONTENT_TYPE_MESSAGE_DISPOSITION_NOTIFICATION)) {
                    InternetHeaders rptValues = new InternetHeaders(reportsPart
                            .getInputStream());
                    setReportValue(REPORTING_UA, rptValues.getHeader(
                            REPORTING_UA, ", "));
                    setOriginalMessageID(rptValues.getHeader(
                            ORIG_RECIPIENT, ", "));
                    setReportValue(FINAL_RECIPIENT, rptValues.getHeader(
                            FINAL_RECIPIENT, ", "));
                    setReportValue(ORIG_MESSAGE_ID, rptValues.getHeader(
                            ORIG_MESSAGE_ID, ", "));
                    setReportValue(DISPOSITION, rptValues.getHeader(
                            DISPOSITION, ", "));
                    setReportValue(RECEIVED_CONTENT_MIC, rptValues
                            .getHeader(RECEIVED_CONTENT_MIC, ", "));
                }
            }
        }
        catch (Exception e) {
            throw new AS2MessageException("Error in parsing MDN", e);
        }
    }

    public boolean matchOriginalContentMIC(String originalMIC) {
        String receivedMIC = getReceivedContentMIC();
        if (originalMIC == null) {
            if (receivedMIC == null) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            if (receivedMIC==null) {
                return false;
            }
            else {
                try {
                    originalMIC = StringUtilities.tokenize(originalMIC, ", ")[0];
                    receivedMIC = StringUtilities.tokenize(receivedMIC, ", ")[0];
                    if (originalMIC.equals(receivedMIC)) {
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                catch (Exception e) {
                    return false;
                }
            }
        }
    }
    
    public String getOriginalMessageID() {
        return StringUtilities.trim(getReportValue(ORIG_MESSAGE_ID), "<", ">"); 
    }
    
    public void setOriginalMessageID(String messageID) {
        setReportValue(ORIG_MESSAGE_ID, StringUtilities.wraps(messageID, "<", ">"));
    }
   
    public String getReceivedContentMIC() {
        return getReportValue(RECEIVED_CONTENT_MIC);
    }
    
    public void setReceivedContentMIC(String mic) {
        setReportValue(RECEIVED_CONTENT_MIC, mic);
    }
    
    public void setReceivedContentMIC(String mic, String alg) {
        setReceivedContentMIC(mic + ", " + alg);
    }
    
    public Disposition getDisposition() throws AS2MessageException {
        return new Disposition(getReportValue(DISPOSITION));
    }
    
    public void setDisposition(Disposition disposition) throws AS2MessageException {
        setReportValue(DISPOSITION, disposition);
    }
    
    public void setText(String text) throws AS2MessageException {
        try {
            String encodeText = MimeUtility.encodeText(text + "\r\n", "us-ascii",
                    "7bit");
            textPart.setContent(encodeText, "text/plain");
            textPart.setHeader("Content-Type", "text/plain; charset=us-ascii");
            textPart.setHeader("Content-Transfer-Encoding", "7bit");
        }
        catch (Exception e) {
            throw new AS2MessageException("Unable to set text to MDN", e);
        }
    }

    public String getText() throws AS2MessageException {
        try {
            return textPart.getContent().toString();
        }
        catch (Exception e) {
            throw new AS2MessageException("Unable to get text from MDN", e);
        }
    }

    public void setReportValue(String key, Object value) {
        if (key != null && value != null) {
            reportValues.setHeader(key, value.toString());
        }
    }

    public String getReportValue(String key) {
        if (key == null) {
            return null;
        }
        else {
            return reportValues.getHeader(key, ", ");
        }
    }

    private void saveReportValues() throws AS2MessageException {
        try {
            Enumeration reportEn = reportValues.getAllHeaderLines();
            StringBuffer reportData = new StringBuffer();
    
            while (reportEn.hasMoreElements()) {
                reportData.append((String) reportEn.nextElement()).append("\r\n");
            }
    
            reportData.append("\r\n");
    
            String reportText = MimeUtility.encodeText(reportData.toString(),
                    "us-ascii", "7bit");
            reportPart.setContent(reportText,
                    AS2Header.CONTENT_TYPE_MESSAGE_DISPOSITION_NOTIFICATION);
            reportPart.setHeader("Content-Type",
                    AS2Header.CONTENT_TYPE_MESSAGE_DISPOSITION_NOTIFICATION);
            reportPart.setHeader("Content-Transfer-Encoding", "7bit");
        }
        catch (Exception e) {
            throw new AS2MessageException("Error in saving report values", e);
        }
    }

    public MimeBodyPart getBodyPart() throws AS2MessageException {
        try {
            saveReportValues();
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setContent(multiPart);
            boolean isContentTypeFolded = new Boolean(System.getProperty("mail.mime.foldtext","true")).booleanValue();
            bodyPart.setHeader("Content-Type", isContentTypeFolded? multiPart.getContentType():multiPart.getContentType().replaceAll("\\s"," "));
            return bodyPart;
        }
        catch (Exception e) {
            throw new AS2MessageException("Unable to construct the body part", e);
        }
    }
    
    public void replyTo(AS2Message message, String reportingUA) throws AS2MessageException {
        setText(DISCLAIMER);
        setReportValue(REPORTING_UA, reportingUA);
        setReportValue(ORIG_RECIPIENT, "rfc822; \"" + message.getFromPartyID() + "\"");
        setReportValue(FINAL_RECIPIENT, "rfc822; \"" + message.getFromPartyID() + "\"");
        setOriginalMessageID(message.getMessageID());
        setDisposition(new Disposition());
    }
}