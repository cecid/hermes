/*
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Academic Free License Version 1.0
 */

package hk.hku.cecid.piazza.corvus.admin.handler;

import hk.hku.cecid.piazza.commons.pagelet.Pagelet;
import hk.hku.cecid.piazza.commons.pagelet.PageletStore;
import hk.hku.cecid.piazza.commons.spa.Extension;
import hk.hku.cecid.piazza.commons.spa.ExtensionPointIteratedHandler;
import hk.hku.cecid.piazza.commons.spa.PluginException;
import hk.hku.cecid.piazza.commons.util.StringUtilities;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import java.net.URL;

/**
 * AdminPageletRegistrar handles the registration of a pagelet with the default 
 * admin pagelet adaptor.
 *
 * @see hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor
 *  
 * @author Hugo Y. K. Lam
 *  
 */
public class AdminPageletRegistrar extends
        ExtensionPointIteratedHandler {

    /**
     * Adds a pagelet to the pagelet store that this registrar manages.
     * 
     * @param extension the extension which represents the pagelet to be added.
     * @throws PluginException if unable to add the pagelet.
     * @see hk.hku.cecid.piazza.commons.spa.ExtensionPointHandler#processExtensions(java.util.Collection)
     */
    public void processExtension(Extension extension) throws PluginException {
        String pageletId   = extension.getParameter("id");
        String pageletPath = extension.getParameter("pagelet");
        String pageletBase = extension.getParameter("base");
        Boolean noCache    = Boolean.valueOf(extension.getParameter("nocache"));
        
        String[] pageletIds   = StringUtilities.tokenize(pageletId, ", ");
        String[] pageletPaths = StringUtilities.tokenize(pageletPath, ", ");
        
        try {
            for (int i=0; i<pageletIds.length && i<pageletPaths.length; i++) {
                pageletId  = pageletIds[i];
                pageletPath = pageletPaths[i];
                
                if (pageletBase != null) {
                    pageletBase = StringUtilities.addSuffix(pageletBase, "/");
                    pageletPath = pageletBase + pageletPath;
                }
                
                URL pageletURL = extension.getPlugin().getClassLoader().findResource(pageletPath);
                if (pageletURL == null) {
                    throw new PluginException("Pagelet not found: "+pageletPath);
                }
                Pagelet pagelet = new Pagelet(pageletId, pageletURL);
                pagelet.setCacheEnabled(!noCache.booleanValue());
                getPageletStore(extension).addPagelet(pagelet);
            }
        }
        catch (Exception e) {
            throw new PluginException("Unable to add the pagelet (ID: "+pageletId+"): "+pageletPath, e);
        }
    }
    
    /**
     * Gets the pagelet store this registrar manages.
     * 
     * @param extension the extension being processed.
     * @return the pagelet store of the default admin pagelet adaptor.
     */
    protected PageletStore getPageletStore(Extension extension) {
        return AdminPageletAdaptor.store;
    }
}