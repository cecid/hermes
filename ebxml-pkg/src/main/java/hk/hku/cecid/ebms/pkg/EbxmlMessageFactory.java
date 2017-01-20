/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/EbxmlMessageFactory.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * cyng [2002-03-21]
 *
 * Code reviewed by:
 *
 * username [YYYY-MM-DD]
 *
 * Remarks:
 *
 * =====
 */

package hk.hku.cecid.ebms.pkg;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
/**
 * Implementation of <code>javax.xml.soap.MessageFactory</code>
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class EbxmlMessageFactory extends MessageFactory {

    /** 
     * Initializes EbxmlMessageFactory object.
     */
    public EbxmlMessageFactory() {
        super();
    }

    /** 
     * Creates a simple ebXML SOAP message.
     * 
     * @return <code>SOAPMessage</code> of the ebXML message created.
     * @throws SOAPException 
     */
    public SOAPMessage createMessage() throws SOAPException {
        final EbxmlMessage ebxmlMessage = new EbxmlMessage();
        return ebxmlMessage.getSOAPMessage();
    }

    /** 
     * Creates an ebXML SOAP message using given MIME headers and content in 
     * form of input stream.
     * 
     * @param headers           MIME headers to be used in the new message.
     * @param in                Content of ebXML message in the form of
     *                          input stream.
     * @return <code>SOAPMessage</code> of the ebXML message created.
     * @throws IOException 
     * @throws SOAPException 
     */
    public SOAPMessage createMessage(MimeHeaders headers, InputStream in)
        throws IOException, SOAPException {
        final EbxmlMessage ebxmlMessage = new EbxmlMessage(headers, in);
        return ebxmlMessage.getSOAPMessage();
    }

    /** 
     * Create a simple ebXML message.
     * 
     * @return A new ebXML message.
     * @throws SOAPException 
     */
    public EbxmlMessage createEbxmlMessage() throws SOAPException {
        return new EbxmlMessage();
    }

    /** 
     * Create an ebXML message with given MIME headers and content in form of
     * input stream.
     * 
     * @param headers           MIME headers to be used in the new message.
     * @param in                Content of ebXML message in the form of
     *                          input stream.
     * @return <code>EbxmlMessage</code> created.
     * @throws IOException
     * @throws SOAPException
     */
    public EbxmlMessage createEbxmlMessage(MimeHeaders headers,
                                           InputStream in)
        throws IOException, SOAPException {
        return new EbxmlMessage(headers, in);
    }
}
