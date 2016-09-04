/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.activation;

/**
 * Mailcap represents a single mailcap in the MailcapCommandMap. 
 * It is a convenient class for constructing a mailcap to be added to the 
 * MailcapCommandMap and for easy comparison if necessary.
 * 
 * @author Kevin Tsang
 *  
 * @see javax.activation.MailcapCommandMap
 */
public class Mailcap {

    private String mimeType      = null;

    private String commandName   = null;

    private String className     = null;

    private String mailCapString = null;

    /**
     * Creates a new instance of Mailcap. 
     * 
     * @param mimeType the MIME type.
     * @param commandName the command name.
     * @param className the handling class name.
     */
    public Mailcap(String mimeType, String commandName, String className) {
        this.mimeType = mimeType;
        this.commandName = commandName;
        this.className = className;
        this.mailCapString = mimeType + ";; x-java-" + commandName + "="
                + className;
    }

    /**
     * Gets the MIME type.
     * 
     * @return the MIME type.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Gets the command name.
     * 
     * @return the command name.
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Gets the handling class name.
     * 
     * @return the handling class name.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Returns a string representation of this mailcap. 
     * 
     * @return a mailcap formatted string.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return mailCapString;
    }
}