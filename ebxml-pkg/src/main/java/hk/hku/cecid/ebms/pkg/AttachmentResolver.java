/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/AttachmentResolver.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.w3c.dom.Attr;

import org.apache.log4j.Logger;
/**
 * A <code>ResourceResolver</code> implementation used by Apache Security
 * library. The URI in the <code>Reference</code> element of a digital
 * signature points to some internal or external resources. This
 * <code>AttachmentResolver</code> is used to provide the resources in
 * the <code>EbxmlMessage</code> payload attachments and also the
 * <code>SOAPPart</code> itself with Reference URI="".
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
class AttachmentResolver extends ResourceResolverSpi {

    private final EbxmlMessage ebxmlMessage;

    protected static Logger logger = Logger.getLogger(AttachmentResolver.class);

    AttachmentResolver(EbxmlMessage ebxmlMessage) {
        super();
        this.ebxmlMessage = ebxmlMessage;
    }

    public XMLSignatureInput engineResolveURI(ResourceResolverContext context)
        throws ResourceResolverException {
        final String href = context.attr.getNodeValue();

        logger.debug("href="+href+", uri="+context.uriToResolve);
        
        if (!href.startsWith(PayloadContainer.HREF_PREFIX)) {
            final Object exArgs[] = { "Reference URI does not start with "
                                      + PayloadContainer.HREF_PREFIX };
            throw new ResourceResolverException(href, exArgs, context.uriToResolve, context.baseUri);
        }

        final String contentId = href.substring(PayloadContainer.HREF_PREFIX.
                                                length());
        final PayloadContainer payload = ebxmlMessage.
            getPayloadContainer(contentId);
        if (payload == null) {
            final Object exArgs[] = { "Reference URI = " + href
                                      + " does not exist!" };
            throw new ResourceResolverException(href, exArgs, context.uriToResolve, context.baseUri);
        }
        final XMLSignatureInput input;
        try {
            input = new XMLSignatureInput(payload.getDataHandler().
                                          getInputStream());
        }
        catch (Exception e) {
            throw new ResourceResolverException(href, e, context.uriToResolve, context.baseUri);
        }
        input.setSourceURI(href);
        input.setMIMEType(payload.getContentType());

        return input;
    }

    public boolean engineCanResolveURI(ResourceResolverContext context) {
        final String href = context.attr.getNodeValue();

        if (href.startsWith(PayloadContainer.HREF_PREFIX)) {
            final String contentId = href.substring(PayloadContainer.
                                                    HREF_PREFIX.length());
            final PayloadContainer payload = ebxmlMessage.
                getPayloadContainer(contentId);
            if (payload != null) {
                return true;
            }
        }
        return false;
    }
}
