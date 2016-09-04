/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.piazza.corvus.core.main.admin.listener;

import hk.hku.cecid.piazza.commons.spa.Plugin;
import hk.hku.cecid.piazza.commons.spa.PluginRegistry;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;
import hk.hku.cecid.piazza.corvus.core.Kernel;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;


/**
 * PluginRegistryPageletAdaptor is an admin pagelet adaptor which provides an
 * admin function of the plugin registry.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class PluginRegistryPageletAdaptor extends AdminPageletAdaptor {

    /**
     * Generates the transformation source of the plugin regsitry.
     * 
     * @see hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor#getCenterSource(javax.servlet.http.HttpServletRequest)
     */
    protected Source getCenterSource(HttpServletRequest request) {

        PropertyTree dom = new PropertyTree();
        dom.setProperty("/registry", "");

        Kernel kernel = Kernel.getInstance();
        PluginRegistry registry = kernel.getPluginRegistry();
        Collection plugins = registry.getPlugins();
        
        String status = (registry.isActivated()? "Activated "+(registry.hasErrors() ? "with": "without") + " errors" : "Not activated");

        dom.setProperty("location", registry.getLocation());
        dom.setProperty("activation", status);

        Iterator allPlugins = plugins.iterator();
        for (int i=1; allPlugins.hasNext(); i++) {
            Plugin plugin = (Plugin) allPlugins.next();
            dom.setProperty("plugins/plugin["+i+"]/id", plugin.getId());
            dom.setProperty("plugins/plugin["+i+"]/name", plugin.getName());
            dom.setProperty("plugins/plugin["+i+"]/version", plugin.getVersion());
            dom.setProperty("plugins/plugin["+i+"]/points", String.valueOf(plugin.getExtensionPoints().size()));
            dom.setProperty("plugins/plugin["+i+"]/extensions", String.valueOf(plugin.getExtensions().size()));
        }
        
        return dom.getSource(); 
    }
}
