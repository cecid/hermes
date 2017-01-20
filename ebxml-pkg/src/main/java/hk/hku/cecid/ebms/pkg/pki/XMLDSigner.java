/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/XMLDSigner.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 * This interface defines a standard way to have the document signed. 
 * Different classes will implement the interface using different 
 * library behind.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public interface XMLDSigner {

    /**
     * Set the envelope to host the Signature element. That is the
     * XML document where the Signature element to be added. The
     * digital signature here will always be an enveloped signature.
     * The envelope will be included in the process of signing.
     *
     * @param doc the XML document to host the Signature element
     * @throws SignException
     */
    public void setEnvelope(Document doc) throws SignException;

    /**
     * Adds a reference to a document attachment to the signature.
     *
     * @param uri the URI of the document attachment
     * @param is the input stream of the content of the document
     * @param contentType the content type of the document
     */
    public void addDocument(String uri, InputStream is, String contentType);

    /**
     * Signs the envelope and documents by using the specified key 
     * in the keystore.
     *
     * @param ks the keystore holding the key for signing
     * @param alias the alias of the key for signing
     * @param password the password for accessing the key for signing
     * @throws SignException when there is any error in the processing of
     *                       signing
     */
    public void sign(CompositeKeyStore ks, String alias, char[] password)
        throws SignException;

    /**
     * Sets the trust anchor for verfication of certificate path.
     *
     * @param ks the keystore providing the trusted certificates
     */
    public void setTrustAnchor(CompositeKeyStore ks);

    /**
     * Verifies the signature in the envelope passed in, which may reference
     * the documents specified using the addDocument method.
     *
     * @return true if the signature can be verified successfully, false
     *         if otherwise.
     * @throws VerifyException when there is any error in the processing of
     *                         verification
     */
    public boolean verify() throws VerifyException;

    /**
     * Gets the DOM element of the signature generated.
     *
     * @return the DOM element of the signature
     */
    public Element getElement();
}
