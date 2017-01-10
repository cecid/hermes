package hk.hku.cecid.piazza.commons.security;

import java.util.Collection;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;


/**
 * TrustedHostnameVerifier is a HostnameVerifier which verifies the host name in 
 * an SSL session based on a list of pre-defined hostnames. If there is no such 
 * a list in this verifier, it defaults to trust any host name.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class TrustedHostnameVerifier implements HostnameVerifier {

    private Collection trustedHostnames;
    
    /**
     * Creates a new intance of TrustedHostnameVerifier.
     * The verifier will be set to trust all hostnames on verification. 
     */
    public TrustedHostnameVerifier() {
        this(null);
    }
    
    /**
     * Creates a new intance of TrustedHostnameVerifier.
     * 
     * @param hostnames the host names to be trusted on verification.
     */
    public TrustedHostnameVerifier(Collection hostnames) {
        trustedHostnames = hostnames;
    }
    
    /**
     * Verifies that the host name is an acceptable match with the trusted 
     * host names pre-defined in this verifier.  
     * 
     * @param hostname the host name.
     * @param sslSession the SSL session used on the connection to the host.
     * @return true if the host name is acceptable.
     * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String,
     *      javax.net.ssl.SSLSession)
     */
    public boolean verify(String hostname, SSLSession sslSession) {
        if (trustedHostnames == null) {
            return true;
        }
        else {
            Iterator hostnames = trustedHostnames.iterator();
            while (hostnames.hasNext()) {
                Object name = hostnames.next();
                if (name!=null && name.toString().equals(hostname)) {
                    return true;
                }
            }
            return false;
        }
    }
}
