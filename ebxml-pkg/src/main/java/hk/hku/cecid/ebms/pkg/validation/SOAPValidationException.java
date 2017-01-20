/* =====
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/validation/SOAPValidationException.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * frankielam [2002-11-14]
 *
 * Code reviewed by:
 *
 * username [YYYY-MM-DD]
 *
 * Remarks:
 *
 * =====
 */
package hk.hku.cecid.ebms.pkg.validation;

import hk.hku.cecid.ebms.pkg.EbxmlMessage;

import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
/** 
 * Exception class that can generate SOAP fault messages from the information it
 * has been given.
 * 
 * @author Frankie Lam
 * @version $Revision: 1.1 $
 */
public class SOAPValidationException extends ValidationException {

    // SOAP Fault codes
    
    /** SOAP Fault code indicating version mismatch. */
    public static final String SOAP_FAULT_VERSION_MISMATCH = "VersionMismatch";

    /** SOAP Fault code indicating that the client cannot interpret an immediate
     *  child of the header element having mustUnderstand equals to "1". */
    public static final String SOAP_FAULT_MUST_UNDERSTAND = "MustUnderstand";

    /** SOAP Fault code indicating a client fault that the message should not
     *  be resent without change. */
    public static final String SOAP_FAULT_CLIENT = "Client";

    /** SOAP Fault code indicating a server fault that the message may succeed
     *  by resending at a later time. */
    public static final String SOAP_FAULT_SERVER = "Server";


    /** SOAP Envelope namespace prefix. */
    static final String NAMESPACE_PREFIX_SOAP_ENVELOPE = "soap";

    /** SOAP Envelope namespace. */
    static final String NAMESPACE_URI_SOAP_ENVELOPE =
        "http://schemas.xmlsoap.org/soap/envelope/";

    /** CECID namespace prefix */
    static final String NAMESPACE_PREFIX_CECID = "cecid";

    /** CECID Elements namespace */
    static final String NAMESPACE_URI_CECID =
        "http://www.cecid.hku.hk";

    /** Error element to be embedded in the detail entries */
    static final String ELEMENT_ERROR = "Error";

    /** SOAP Fault Actor. */
    protected final String faultActor;

    /** SOAP Fault detail. */
    protected final String detail;

    /** 
     * Constructs a <code>SOAPValidationException</code> object given its 
     * fault code and fault string.
     * 
     * @param faultCode     Fault codes as specified in the SOAP 1.1
     *                      specification
     * @param faultString   Human readable explanation of the fault.
     */
    public SOAPValidationException(String faultCode, String faultString) {
        super(ERROR_SOAP, faultCode, faultString);
        this.faultActor = null;
        this.detail = null;
    }
    
    /** 
     * Constructs a <code>SOAPValidationException</code> object given its 
     * fault code and fault string.
     * 
     * @param faultCode     Fault codes as specified in the SOAP 1.1
     *                      specification
     * @param faultString   Human readable explanation of the fault.
     * @param detail        Specific error information related to the SOAP Body 
     *                      element. It must be present if the SOAP Body cannot
     *                      be processed successfully, and must NOT be present
     *                      if the error is caused by header entries.
     */
    public SOAPValidationException(String faultCode, String faultString, 
                                   String detail) {
        super(ERROR_SOAP, faultCode, faultString);
        this.faultActor = null;
        this.detail = detail;
    }

    /** 
     * Constructs a <code>SOAPValidationException</code> object given its 
     * fault code and fault string.
     * 
     * @param faultCode     Fault codes as specified in the SOAP 1.1
     *                      specification
     * @param faultString   Human readable explanation of the fault.
     * @param faultActor    Information on who caused the fault to happen.
     * @param detail        Specific error information related to the SOAP Body 
     *                      element. It must be present if the SOAP Body cannot
     *                      be processed successfully, and must NOT be present
     *                      if the error is caused by header entries.
     */
    public SOAPValidationException(String faultCode, String faultString,
                                   String faultActor, String detail) {
        super(ERROR_SOAP, faultCode, faultString);
        this.faultActor = faultActor;
        this.detail = detail;
    }

    /** 
     * Get SOAP Fault message from the information given.
     * 
     * @return <code>SOAPMessage</code> object containing Fault element.
     */
    public SOAPMessage getSOAPMessage() {
        try {
            final MessageFactory mf = MessageFactory.newInstance();
            final SOAPMessage message = (SOAPMessage)mf.createMessage();
            
            // set default SOAP XML declaration and encoding
            message.setProperty(SOAPMessage.WRITE_XML_DECLARATION, 
            		Boolean.toString(EbxmlMessage.WRITE_XML_DECLARATION));
            message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, 
            		EbxmlMessage.CHARACTER_SET_ENCODING);
            
            final SOAPPart part = message.getSOAPPart();
            final SOAPEnvelope envelope = part.getEnvelope();
            final SOAPBody body = envelope.getBody();
            final SOAPFault fault = body.addFault();

            fault.setFaultCode(errorCode);
            fault.setFaultString(errorString);
            if (faultActor != null) {
                fault.setFaultActor(faultActor);
            }
            if (detail != null) {
                Detail d = fault.addDetail();
                Name name = envelope.createName(ELEMENT_ERROR, 
                    NAMESPACE_PREFIX_CECID, NAMESPACE_URI_CECID);
                DetailEntry entry = d.addDetailEntry(name);
                entry.addTextNode(detail);
            }

            return message;
        }
        catch (SOAPException e) {
        }

        return null;
    }
}
