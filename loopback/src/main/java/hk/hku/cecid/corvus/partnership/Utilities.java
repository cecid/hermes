package hk.hku.cecid.corvus.partnership;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateFactory;

import hk.hku.cecid.piazza.commons.io.IOHandler;

import org.dom4j.Element;

/**
 * Comment for Utilities.java.
 * 
 * @author kochiu
 * @version $Revision$
 */
public class Utilities {
	
    public static boolean isValidCert(byte[] cert) {
        if (cert != null) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(cert);
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                cf.generateCertificate(bais);
                bais.close();
                return true;
            } catch (Exception e) {
                // do nothing
            }
        }
        return false;
    }

    public static byte[] loadCert(String filepath) throws IOException {
        if (filepath == null || "".equals(filepath)) {
            return null;
        }
        FileInputStream fis = new FileInputStream(filepath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOHandler.pipe(fis, baos);
        byte[] cert = baos.toByteArray();
        if (!isValidCert(cert)) {
            throw new RuntimeException("Invalid cert.");
        }
        return cert;
    }

    public static boolean getBooleanValue(Element params, String paramName) {
        return (params.elementText(paramName) != null && "true"
                .equalsIgnoreCase(params.elementText(paramName)));
    }
}
