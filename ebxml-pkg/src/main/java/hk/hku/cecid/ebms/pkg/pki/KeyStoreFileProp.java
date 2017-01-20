/* ===== 
 *
 * $Header: /home/cvsroot/ebxml-pkg/src/hk/hku/cecid/ebms/pkg/pki/KeyStoreFileProp.java,v 1.1 2005/07/28 09:36:24 dcmsze Exp $
 *
 * Code authored by:
 *
 * kcyee [2002-05-03]
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

import java.io.Serializable;
/**
 * This class represents the data structure for holding parameters for 
 * accessing a keystore file. Since the parameters will be serialized 
 * to the file system, some fields (particularly password will be scrambled.
 *
 * @author kcyee
 * @version $Revision: 1.1 $
 */
public class KeyStoreFileProp implements Serializable {

    /**
     * Internal storage for the keystore type
     */
    protected String type;

    /**
     * Internal storage for the keystore password
     */
    protected char[] password;

    /**
     * Constructor that initializes the object with keystore parameters
     *
     * @param type the keystore type
     * @param password the keystore password
     */
    public KeyStoreFileProp(String type, char[] password) {
        this.type = type;
        this.password = encrypt(password);
    }

    /**
     * Gets the keystore type
     *
     * @return the keystore type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the keystore password
     *
     * @return the keystore password
     */
    public char[] getPassword() {
        return decrypt(password);
    }

    /**
     * Encrypts the password. This is a helper function called internally
     * to scramble the password for storage.
     *
     * @param password the password to be encrypted
     * @return the encrypted text
     */
    protected char[] encrypt(char[] password) {
        return password;
    }

    /**
     * Decrypts the password. This is a helper function called internally
     * to unscramble the password from storage.
     *
     * @param password the text to be decrypted
     * @return the decrypted password
     */
    protected char[] decrypt(char[] password) {
        return password;
    }
}
