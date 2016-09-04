/*
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Academic Free License Version 1.0
 */

package hk.hku.cecid.piazza.corvus.admin.handler;

import hk.hku.cecid.piazza.commons.spa.Extension;
import hk.hku.cecid.piazza.commons.spa.ExtensionPointIteratedHandler;
import hk.hku.cecid.piazza.commons.spa.PluginException;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.commons.util.StringUtilities;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;
import hk.hku.cecid.piazza.corvus.admin.menu.MenuComponent;

import java.net.URL;

/**
 * AdminModuleRegistrar handles the registration of an admin module with the 
 * default admin pagelet adaptor.
 * 
 * @see hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class AdminModuleRegistrar extends
        ExtensionPointIteratedHandler {

    /**
     * Adds an admin module to the default admin pagelet adaptor.
     * 
     * @param extension the extension which represents the module to be added.
     * @throws PluginException if unable to add the module.
     * @see hk.hku.cecid.piazza.commons.spa.ExtensionPointHandler#processExtensions(java.util.Collection)
     */
    public void processExtension(Extension extension) throws PluginException {
        
        String moduleDescriptor = extension.getParameter("module");
        
        try {
            URL moduleDescriptorURL = extension.getPlugin().getClassLoader().findResource(moduleDescriptor);
            if (moduleDescriptorURL == null) {
                throw new PluginException("Module descriptor not found: "+moduleDescriptor);
            }
            
            PropertyTree props = new PropertyTree(moduleDescriptorURL);
            
            int moduleSeqno = StringUtilities.parseInt(props.getProperty("/module/@seqno"), Integer.MAX_VALUE);
            String  moduleId         = props.getProperty("/module/@id");
            String  moduleName       = props.getProperty("/module/name");
            String  moduleDesc       = props.getProperty("/module/description");
            String  moduleLink       = props.getProperty("/module/link");
            boolean moduleVisibility = !"false".equalsIgnoreCase(props.getProperty("/module/@visible"));
            
            MenuComponent module = new MenuComponent();
            module.setSeqNo(moduleSeqno);
            module.setId(moduleId);
            module.setName(moduleName);
            module.setDescription(moduleDesc);
            module.setLink(moduleLink);
            module.setVisible(moduleVisibility);
            
            int tabCount = props.countProperties("/module/tabs/tab");
            
            for (int i=0; i<tabCount; i++) {
                int tabSeqno = i+1;
                String tabId           = props.getProperty("/module/tabs/tab["+tabSeqno+"]/@id");
                String tabName         = props.getProperty("/module/tabs/tab["+tabSeqno+"]/name");
                String tabDesc         = props.getProperty("/module/tabs/tab["+tabSeqno+"]/description");
                String tabLink         = props.getProperty("/module/tabs/tab["+tabSeqno+"]/link");
                boolean tabVisibility  = !"false".equalsIgnoreCase(props.getProperty("/module/tabs/tab["+tabSeqno+"]/@visible"));
                
                MenuComponent tab = new MenuComponent();
                tab.setSeqNo(tabSeqno);
                tab.setId(tabId);
                tab.setName(tabName);
                tab.setDescription(tabDesc);
                tab.setLink(tabLink);
                tab.setVisible(tabVisibility);
                
                module.addChild(tab);
            }
            
            if (!AdminPageletAdaptor.modules.add(module)) {
                throw new PluginException("Duplicated module at position "+moduleSeqno); 
            }
        }
        catch (Exception e) {
            throw new PluginException("Unable to add the admin module: " + moduleDescriptor, e);
        }
    }
}