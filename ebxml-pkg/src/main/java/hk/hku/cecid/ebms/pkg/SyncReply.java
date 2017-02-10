/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/SyncReply.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * tslam [2002-08-16]
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

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;

/**
 * An ebXML <code>SyncReply</code> in the SOAP Header of a
 * <code>HeaderContainer</code> [ebMSS 4.3].
 *
 * @author tslam 
 * @version $Revision: 1.1 $
 */
public class SyncReply extends HeaderElement {

    /** <code>SyncReply</code> element name */
    static final String SYNC_REPLY = "SyncReply";
    
    /** 
     * Constructs a <code>SyncReply</code> with the given mandatory
     * fields.
     *
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header container
     *                          of which the <code>SyncReply</code> is 
     *                          attached to.
     * @throws SOAPException 
    */
    SyncReply(SOAPEnvelope soapEnvelope) 
        throws SOAPException {
        super(soapEnvelope, SYNC_REPLY);
        setActor(ACTOR_NEXT_MSH_SCHEMAS);
    }

    /** 
     * Constructs an <code>SyncReply</code> object by parsing the given
     * <code>SOAPElement</code>.
     * 
     * @param soapEnvelope      <code>SOAPEnvelope</code>, the header container
     *                          of which the <code>SyncReply</code> is 
     *                          attached to.
     * @param soapElement       Empty <code>SOAPElement</code>.
     *
     * @exception SOAPException
     */
    SyncReply(SOAPEnvelope soapEnvelope, SOAPElement soapElement)
        throws SOAPException {
        super(soapEnvelope, soapElement);
    }
}
