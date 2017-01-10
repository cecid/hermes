/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/CRLFileSource.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2002-04-30]
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
/**
 * This class extends CRLSource to add initialization procedure for loading a 
 * file-based CRL.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class CRLFileSource extends CRLSource {

    /**
     * The file holding the CRL
     */
    protected File crlFile;

    /**
     * Default constructor. It initializes the object. But the object 
     * is still unusable until init() is called.
     */
    public CRLFileSource() {
        super();
        crlFile = null;
    }

    /**
     * Constructor with the file name of the CRL passed in. It initializes 
     * the object. But the object is still unusable until init() is called.
     *
     * @param crlFile the file name of the CRL
     */
    public CRLFileSource(String crlFile) {
        this(new File(crlFile));
    }

    /**
     * Constructor with the file object holding the CRL passed in. It 
     * initializes the object. But the object is still unusable until init() 
     * is called.
     *
     * @param crlFile the file object of the file holding the CRL
     */
    public CRLFileSource(File crlFile) {
        super();
        this.crlFile = crlFile;
    }

    /**
     * Initializes the object. The CRL file is being loaded into the 
     * internal CRL object.
     *
     * @throws CRLException Initialization error occurs
     */
    public void init() throws CRLException {
        if (crlFile == null || !crlFile.exists() || !crlFile.isFile()) {
            throw new CRLException("Error loading file: " + crlFile + ".\n");
        }

        try {
            InputStream inStream = new FileInputStream(crlFile);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            crl = (X509CRL) cf.generateCRL(inStream);
            inStream.close();
        }
        catch (IOException e) {
            throw new CRLException("IO exception when loading crl file.\n"
                + e.getMessage());
        }
        catch (CertificateException e) {
            throw new CRLException(
                "Certificate exception when loading crl file.\n" 
                + e.getMessage());
        }

        ready = true;
    }
}
