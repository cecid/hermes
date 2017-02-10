/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/DocumentDetail.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
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
/**
 * A data structure class for holding the document attachment parameters.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class DocumentDetail {

    /**
     * The URI of the document.
     */
    public String uri;

    /**
     * The input stream of the content of the document.
     */
    public InputStream stream;

    /**
     * The content type of the document.
     */
    public String contentType;
}
