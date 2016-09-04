/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the Apache License Version 2.0 [1]
 * 
 * [1] http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package hk.hku.cecid.piazza.corvus.core.main.admin.listener;

import hk.hku.cecid.piazza.commons.Sys;
import hk.hku.cecid.piazza.commons.module.Module;
import hk.hku.cecid.piazza.commons.module.ModuleGroup;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

/**
 * ModuleInfoPageletAdaptor is an admin pagelet adaptor which provides an admin 
 * function of the system modules.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class ModuleInfoPageletAdaptor extends AdminPageletAdaptor {

    /**
     * Generates the transformation source of the system modules.
     *  
     * @see hk.hku.cecid.piazza.corvus.admin.listener.AdminPageletAdaptor#getCenterSource(javax.servlet.http.HttpServletRequest)
     */
    protected Source getCenterSource(HttpServletRequest request) {
        PropertyTree dom = new PropertyTree();
        printModuleGroup(dom, Sys.main.getGroup(), "/module-info/module-group/");
        printModuleGroups(dom, Sys.main.getGroup(), "/module-info/all-module-groups/", 1);
        return dom.getSource(); 
    }

    private void printModuleGroup(PropertyTree dom, ModuleGroup group, String level) {
        dom.setProperty(level+"name", group.getName());
        dom.setProperty(level+"subgroups", String.valueOf(group.getChildren().size()));
        dom.setProperty(level+"sysmodule", group.getSystemModule()==null? "No system module":group.getSystemModule().getName());
        Iterator modules = group.getModules().iterator();
        for (int i=1; modules.hasNext(); i++) {
            String prefix = level + "module["+i+"]/";
            Module module = (Module)modules.next();
            dom.setProperty(prefix+"name", module.getName());
            dom.setProperty(prefix+"version", module.getVersion());
            dom.setProperty(prefix+"components", String.valueOf(module.getComponentCount()));
            dom.setProperty(prefix+"descriptor", module.getDescriptor().toString());
        }
    }

    private void printModuleGroups(PropertyTree dom, ModuleGroup group, String level, int position) {
        level += "module-group" + (position>1? "["+position+"]/":"/");
        Collection groups = group.getChildren();
        dom.setProperty(level+"name", group.getName());
        dom.setProperty(level+"sysmodule", group.getSystemModule()==null? "No system module":group.getSystemModule().getName());
        Iterator subgroups = groups.iterator();
        for (int i=1; subgroups.hasNext(); i++) {
            printModuleGroups(dom, (ModuleGroup) subgroups.next(), level, i);
        }
    }
}
