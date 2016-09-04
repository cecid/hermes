/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.spa;

import hk.hku.cecid.piazza.commons.util.PropertyTree;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;


/**
 * PluginDescriptor is a descriptor containing the configurations of the plugin
 * it represents. 
 * <p></p>
 * <pre>
 * &lt;plugin id="nmtoken" name="nmtoken"? version="nmtoken"? provider-name="nmtoken"? class="nmtoken"?&gt;
 *
 *        &lt;parameters&gt;?
 *                &lt;parameter name="nmtoken" value="nmtoken" /&gt;*
 *        &lt;/parameters&gt;
 *
 *        &lt;runtime&gt;?
 *                &lt;library name="nmtoken" /&gt;*
 *        &lt;/runtime&gt;
 *
 *        &lt;requires&gt;?
 *                &lt;import plugin="nmtoken" /&gt;*
 *        &lt;/requires&gt;
 *
 *        &lt;extension-point id="nmtoken" class="nmtoken" name="nmtoken"? /&gt;*
 *
 *        &lt;extension point="nmtoken" name="nmtoken"?&gt;*
 *                &lt;parameter name="nmtoken" value="nmtoken" /&gt;*
 *        &lt;/extension&gt;
 *
 * &lt;/plugin&gt;
 * </pre>
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class PluginDescriptor {

    /**
     * The default descriptor name (plugin.xml).
     */
    public static final String DEFAULT_DESCRIPTOR_NAME = "plugin.xml";

    private PropertyTree descriptor;
    
    private PluginComponent parent;
    
    private String id;
    private String name;
    private String version;
    private String providerName;
    private String handlerClass;
    private Properties parameters;
    
    /**
     * Creates a new instance of PluginDescriptor.
     * 
     * @param file the plugin descriptor file.
     * @throws PluginException if unable to create the plugin descriptor.
     * @throws FileNotFoundException if the descriptor file does not exist. 
     */
    public PluginDescriptor(File file) throws PluginException, FileNotFoundException {
        this(new FileInputStream(file));
    }
    
    /**
     * Creates a new instance of PluginDescriptor.
     * 
     * @param ins the plugin descriptor input stream.
     * @throws PluginException if unable to create the plugin descriptor.
     */
    public PluginDescriptor(InputStream ins) throws PluginException {
        try {
            descriptor = new PropertyTree(ins);
        }
        catch (Exception e) {
            throw new PluginException("Unable to create plugin descriptor", e);
        }
        id = descriptor.getProperty("/plugin/@id", "");
        name = descriptor.getProperty("/plugin/@name", "");
        version = descriptor.getProperty("/plugin/@version", "");
        providerName = descriptor.getProperty("/plugin/@provider-name", "");
        handlerClass = descriptor.getProperty("/plugin/@class", "");
        parameters = descriptor.createProperties("/plugin/parameters/parameter");
    }
    
    /**
     * Creates a collection of libraries according to the specified plugin descriptor.
     * 
     * @return a collection of libraries.
     */
    public Collection getLibraries() {
        String[] libraryNames = descriptor.getProperties("/plugin/runtime/library/@name");
        ArrayList list = new ArrayList();
        
        for (int i=0; i<libraryNames.length; i++) {
            Library library = new Library(parent, libraryNames[i]);
            list.add(library);
        }
        return list;
    }
    
    /**
     * Creates a collection of imports according to the specified plugin descriptor.
     * 
     * @return a collection of imports.
     */
    public Collection getImports() {
        String[] importedPlugins = descriptor.getProperties("/plugin/requires/import/@plugin");
        ArrayList list = new ArrayList();
        
        for (int i=0; i<importedPlugins.length; i++) {
            Import imp = new Import(parent, importedPlugins[i]);
            list.add(imp);
        }
        return list;
    }
    
    /**
     * Creates a collection of extension points according to the specified plugin descriptor.
     * 
     * @return a collection of extension points.
     */
    public Collection getExtensionPoints() {
        int extensionPointCount = descriptor.countProperties("/plugin/extension-point");
        ArrayList list = new ArrayList();
        
        for (int i=1; i<=extensionPointCount; i++) {
            String extensionPointId = descriptor.getProperty("/plugin/extension-point["+i+"]/@id", "");
            String extensionPointName = descriptor.getProperty("/plugin/extension-point["+i+"]/@name", "");
            String extensionPointClass = descriptor.getProperty("/plugin/extension-point["+i+"]/@class", "");
            
            ExtensionPoint extensionPoint = new ExtensionPoint(parent, extensionPointId, extensionPointName, extensionPointClass);
            list.add(extensionPoint);
        }
        return list;
    }
    
    /**
     * Creates a collection of extensions according to the specified plugin descriptor.
     * 
     * @return a collection of extensions.
     */
    public Collection getExtensions() {
        int extensionCount = descriptor.countProperties("/plugin/extension");
        ArrayList list = new ArrayList();
        
        for (int i=1; i<=extensionCount; i++) {
            String extensionPoint = descriptor.getProperty("/plugin/extension["+i+"]/@point", "");
            String extensionName = descriptor.getProperty("/plugin/extension["+i+"]/@name", "");
            Properties parameters = descriptor.createProperties("/plugin/extension["+i+"]/parameter");
            Extension extension = new Extension(parent, extensionPoint, extensionName, parameters);
            list.add(extension);
        }
        return list;
    }

    /**
     * Gets the plugin ID.
     * 
     * @return the plugin ID.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Gets the plugin name.
     * 
     * @return the plugin name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the plugin provider name.
     * 
     * @return the plugin provider name.
     */
    public String getProviderName() {
        return providerName;
    }
    
    /**
     * Gets the plugin version.
     * 
     * @return the plugin version.
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Gets the handler class of the plugin.
     * 
     * @return the handler class of the plugin.
     */
    public String getHandlerClass() {
        return handlerClass;
    }

    /**
     * Gets the plugin parameters.
     * 
     * @return the plugin parameters.
     */
    public Properties getParameters() {
        return parameters;
    }
    
    /**
     * Gets the parent plugin component of this descriptor.
     * 
     * @return the parent plugin component of this descriptor.
     */
    public PluginComponent getParent() {
        return parent;
    }
    
    /**
     * Sets the parent plugin component of this descriptor.
     * 
     * @param parent the parent plugin component of this descriptor.
     */
    public void setParent(PluginComponent parent) {
        this.parent = parent;
    }
}
