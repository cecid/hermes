/* =====
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/CertResolver.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * cyng [2003-04-23]
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

import java.security.cert.Certificate;
/**
 *
 * @author cyng
 * @version $Revision: 1.1 $
 */
public interface CertResolver {

    Certificate[] resolve(Object obj);
}
