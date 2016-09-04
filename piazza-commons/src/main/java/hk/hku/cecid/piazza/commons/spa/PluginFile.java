/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.spa;

import hk.hku.cecid.piazza.commons.io.IOHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A PluginFile represents an archive file containing all the plugin required 
 * files, including the plugin descriptor. 
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public class PluginFile {

    /**
     * The default file extension (spa) of the plugin file.
     */
    public static final String DEFAULT_FILE_EXT = "spa";
    
    private byte[] pluginContent;
    private String descriptorName;
    private PluginDescriptor descriptor;
    
    /**
     * Create a new instance of PluginFile.
     * 
     * @param pluginFile the plugin file.
     * @throws PluginException if unable to create the plugin file instance.
     */
    public PluginFile(File pluginFile) throws PluginException {
        this.init(pluginFile, null);
    }
    
    /**
     * Create a new instance of PluginFile.
     * 
     * @param pluginFile the plugin file.
     * @param descriptorName the plugin descriptor name.
     * @throws PluginException if unable to create the plugin file instance.
     */
    public PluginFile(File pluginFile, String descriptorName) throws PluginException {
        this.init(pluginFile, descriptorName);
    }

    /**
     * Create a new instance of PluginFile.
     * 
     * @param pluginStream the plugin stream.
     * @throws PluginException if unable to create the plugin file instance.
     */
    public PluginFile(InputStream pluginStream) throws PluginException {
        this.init(pluginStream, null);
    }

    /**
     * Create a new instance of PluginFile.
     * 
     * @param pluginStream the plugin stream.
     * @param descriptorName the plugin descriptor name.
     * @throws PluginException if unable to create the plugin file instance.
     */
    public PluginFile(InputStream pluginStream, String descriptorName) throws PluginException {
        this.init(pluginStream, descriptorName);
    }

    /**
     * Initializes the plugin file instance.
     * 
     * @throws PluginException if unable to initialize the plugin file instance.
     */
    private void init(Object input, String dname) throws PluginException {
        ZipInputStream zins = null;
        try {
            if (input instanceof File) {
                zins = new ZipInputStream(new FileInputStream((File)input));
            }
            else if (input instanceof InputStream) {
                zins = new ZipInputStream((InputStream)input);
            }
            else {
                throw new PluginException("Unsupported plugin file type: "+input==null? "null":input.getClass().getName());
            }
            if (dname == null) {
                this.descriptorName = PluginDescriptor.DEFAULT_DESCRIPTOR_NAME; 
            }
            else {
                this.descriptorName = dname.trim(); 
            }
            
            ZipEntry zipEntry = null;
            while ((zipEntry=zins.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    if (zipEntry.getName().equalsIgnoreCase(descriptorName)) {
                        ByteArrayOutputStream outs = new ByteArrayOutputStream(); 
                        IOHandler.pipe(zins, outs);
                        pluginContent = outs.toByteArray();
                        descriptor = new PluginDescriptor(new ByteArrayInputStream(pluginContent));
                        break;
                    }
                }
                zins.closeEntry();
            }
            if (descriptor == null) {
                throw new PluginException("No plugin descriptor ("+ descriptorName +") found.");
            }
        }
        catch (Exception e) {
            throw new PluginException("Unable to extract plugin descriptor from: "
                    + input, e);
        }
        finally {
            if (zins != null) {
                try {
                    zins.close();
                }
                catch (Exception e) {}
            }
        }
    }

    /**
     * Gets the plugin content.
     * 
     * @return the plugin content.
     */
    public byte[] getPluginContent() {
        return pluginContent;
    }
    
    /**
     * Gets the plugin descriptor.
     * 
     * @return the plugin descriptor.
     */
    public PluginDescriptor getDescriptor() {
        return descriptor;
    }
}