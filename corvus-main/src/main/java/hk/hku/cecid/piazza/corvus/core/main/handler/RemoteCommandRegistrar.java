/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.corvus.core.main.handler;

import java.util.Properties;

import hk.hku.cecid.piazza.commons.ejb.util.RemoteCommandHandler;
import hk.hku.cecid.piazza.commons.spa.Extension;
import hk.hku.cecid.piazza.commons.spa.ExtensionPointIteratedHandler;
import hk.hku.cecid.piazza.commons.spa.PluginException;

/**
 * RemoteCommandRegistrar handles the registration of any remote command 
 * with the central remote command handler.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class RemoteCommandRegistrar extends ExtensionPointIteratedHandler {

    /**
     * Adds a remote command to the central remote command handler.
     * 
     * @param extension the extension which represents the remote command.
     * @throws PluginException if unable to add the remote command.
     * @see hk.hku.cecid.piazza.commons.spa.ExtensionPointHandler#processExtensions(java.util.Collection)
     */
    public void processExtension(Extension extension) throws PluginException {
        
        String commandName = extension.getParameter("command");
        String className  = extension.getParameter("class");

        try {
            Properties command = new Properties();
            command.putAll(extension.getParameters());

            if (className != null) { 
                Class theClass = extension.getPlugin().loadClass(className);
                command.put("class", theClass);
            }
            
            RemoteCommandHandler.register(commandName, command);
        }
        catch (Exception e) {
            throw new PluginException("Unable to register the remote command: "+commandName, e);
        }
    }
}
