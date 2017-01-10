/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/PayloadContainer.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
/**
 * <P>An encapsulation of the payload container in an <code>EbxmlMessage</code>
 * [ebMSS 2.1.4].</P>
 *
 * <P>A Payload Container contains an ebXML MIME header as well as application
 * payload, as illustrated in the following diagram:</P>
 *
 * <TT>
 * <BR/>1. Content-ID: <unique-id@cecid.hku.hk>    
 * <BR/>2. Content-type: application/xml           
 * <BR/>3.
 * <BR/>4.&lt;PurchaseOrder&gt;                  
 * <BR/>5.  &lt;Product&gt;...&lt;/Product&gt;
 * <BR/>6.  ...
 * <BR/>7.&lt;/PurchaseOrder&gt;
 * </TT>
 *
 * <BR/>
 * <BR/>Line 1-2: ebXML MIME headers.
 * <BR/>Line 4-7: Application payload
 * <P>This class encapsulates the structure of payload container in an ebXML 
 * message.</P>
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public class PayloadContainer {

    /** 
     * The prefix of "href" attribute of this <code>PayloadContainer</code>.
     */
    public final static String HREF_PREFIX = Reference.HREF_PREFIX;

    /**
     * <code>DataHandler</code> representing this
     * <code>PayloadContainer</code>.
     */
    private final DataHandler dataHandler;

    /**
     * <code>Reference</code> inside the <code>Manifest</code> associated
     * with this <code>PayloadContainer</code>.
     */
    private final Reference reference;

    /*
     * MIME headers of this <code>PayloadContainer</code>.
     */
    private final HashMap mimeHeaders;

    /** 
     * Create a <code>PayloadContainer</code> from the specified
     * <code>DataHandler</code>.
     */
    public PayloadContainer(DataHandler dataHandler, String contentId,
                            Reference reference) {
        this.dataHandler = dataHandler;
        this.reference = reference;
        this.mimeHeaders = new HashMap();
        String id = contentId;
        if (id != null && id.startsWith("<") && EbxmlMessage.needPatch) {
            id = id.substring(1);
            if (id.endsWith(">")) {
                id = id.substring(0, id.length() - 1);
            }
        }
        mimeHeaders.put(Constants.CONTENT_TYPE, dataHandler.getContentType());
        mimeHeaders.put(Constants.CONTENT_ID, id);
    }

    /** 
     * Get contentId. 
     */
    public String getContentId() {
        return (String) mimeHeaders.get(Constants.CONTENT_ID);
    }

    /** 
     * Get "href" attribute which is equal to the prefixed contentId.
     */
    public String getHref() {
        return HREF_PREFIX + getContentId();
    }

    /** 
     * Get content type of this attachment. 
     */
    public String getContentType() {
        return dataHandler.getContentType();
    }

    /*
     * Get all MIME headers of this attachment.
     */
    public Map getMimeHeaders() {
        return mimeHeaders;
    }

    /*
     * Set a MIME header of this attachment.
     */
    public void setMimeHeader(String name, String value) {
        if (name != null) {
            mimeHeaders.put(name, value);
        }
    }

    /** 
     * Get <code>javax.activation.DataHandler</code> of this attachment.
     */
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * Get <code>Reference</code> inside the <code>Manifest</code> associated
     * with this <code>PayloadContainer</code>.
     */
    public Reference getReference() {
        return reference;
    }
    
    /**
     * Get the content length of this payload. Note that the content length 
     * returned will be -1 if this container is not created from 
     * AttachmentDataSource. Also, the length returned does not take 
     * Content-Transfer-Encoding into account, if any.
     * 
     * @return content length of this payload
     */
    public long getContentLength() {
        DataSource source = dataHandler.getDataSource();
        if (source instanceof AttachmentDataSource) {
            AttachmentDataSource ads = (AttachmentDataSource) source;
            return ads.getLength();
        }
        else {
            return -1;
        }
    }
}
