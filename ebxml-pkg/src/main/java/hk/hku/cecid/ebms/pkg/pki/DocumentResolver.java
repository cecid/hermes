/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/DocumentResolver.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2002-05-16]
 *
 * Code reviewed by:
 *
 * username [YYYY-MM-DD]
 *
 * Remarks:
 *
 * =====
 */

package hk.hku.cecid.ebms.pkg.pki;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.w3c.dom.Attr;

import org.apache.log4j.Logger;
/**
 * This class is needed by the Apache XML Security library for locating
 * and loading the document attachments.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class DocumentResolver extends ResourceResolverSpi {

    /** 
     * Internal variable for holding the document parameters.
     */
    protected DocumentDetail[] docs;

    protected static Logger logger = Logger.getLogger(DocumentResolver.class);

    /** 
     * Construct with an array of document parameters.
     *
     * @param docs array of document parameters
     */
    public DocumentResolver(DocumentDetail[] docs) {
        super();
        this.docs = docs;
    }

    /**
     * Gets the document (encapsulated in the XMLSignatureInput object)
     * by specifying the URI.
     *
     * @param uri
     * @param baseUri
     * @return the document encapsulated in the XMLSignatureInput object
     */
    // public XMLSignatureInput engineResolve(Attr uri, String baseUri)
    public XMLSignatureInput engineResolveURI(ResourceResolverContext context)
        throws ResourceResolverException {
    
        // String href = uri.getNodeValue();
        String href = context.attr.getNodeValue();

        logger.debug("href="+href+", uri="+context.uriToResolve);
        
        if (!href.startsWith("cid:")) {
            Object exArgs[] = {"Reference URI does not start with 'cid:'"};
            // throw new ResourceResolverException(href, exArgs, uri, baseUri);
            throw new ResourceResolverException(href, exArgs, context.uriToResolve, context.baseUri);
        }

        int found = -1;
        for (int i=0 ; i<docs.length ; i++) {
            if (docs[i].uri != null && docs[i].uri.equals(href)) {
                found = i;
                break;
            }
        }

        if (found < 0) {
            Object exArgs[] = {"Reference URI = " + href + " does not exist!"};
            throw new ResourceResolverException(href, exArgs, context.uriToResolve, context.baseUri);
        }

        XMLSignatureInput input;
        try {
            final InputStream in = docs[found].stream;
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final byte[] buffer = new byte[4096];
            for (int c=in.read(buffer) ; c!=-1 ; c=in.read(buffer))
                out.write(buffer, 0, c);
            input = new XMLSignatureInput(out.toByteArray());
        }
        catch (Exception e) {
            throw new ResourceResolverException(href, e, context.uriToResolve, context.baseUri);
        }
        input.setSourceURI(href);
        input.setMIMEType(docs[found].contentType);

        return input;
    }

    /**
     * Sees whether the resolver can resolve the document specified by
     * the URI or not.
     *
     * @param uri
     * @param baseUri
     * @return true if the resolver can locate the document specified, false
     *         if otherwise.
     */
    // public boolean engineCanResolveURI(Attr uri, String baseUri) {
        public boolean engineCanResolveURI(ResourceResolverContext context) {

        String href = context.attr.getNodeValue();
        logger.debug("DocumentResolver.engineCanResolveURI(): href="+href);
        if (href.startsWith("cid:")) {
            for (int i=0 ; i<docs.length ; i++) {
                if (docs[i].uri != null && docs[i].uri.equals(href)) {
                    return true;
                }
            }
        }
        return false;
    }
}
