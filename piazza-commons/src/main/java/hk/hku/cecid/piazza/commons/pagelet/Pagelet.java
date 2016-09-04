/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.pagelet;

import hk.hku.cecid.piazza.commons.io.IOHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;

/**
 * A Pagelet contains a URL representing a page or a fragment of a page.
 * By default, it caches the content of the pagelet softly. Therefore, each
 * invocation of openStream() may or may not cause an actual access to the 
 * pagelet URL, depending on the memory status and whether caching is enabled. 
 * 
 * @see PageletStore
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class Pagelet {
    
    private String id;
    
    private URL pagelet;
    
    private SoftReference content;
    
    private boolean isCacheEnabled = true;
    
    /**
     * Creates a new instance of Pagelet.
     * 
     * @param id the pagelet ID.
     * @param pagelet the pagelet URL.
     */
    public Pagelet(String id, URL pagelet) {
        this.id = id;
        this.pagelet = pagelet;
    }
    
    /**
     * Gets the pagelet ID.
     * 
     * @return the pagelet ID.
     */
    public String getId() {
        return id;
    }
    
    /**
     * Gets the URL of the pagelet.
     * 
     * @return the URL of the pagelet.
     */
    public URL getURL() {
        return pagelet;
    }
    
    /**
     * Checks if caching is enabled.
     * 
     * @return true if caching is enabled.
     */
    public boolean isCacheEnabled() {
        return isCacheEnabled;
    }
    
    /**
     * Sets whether caching should be enabled.
     * 
     * @param isCacheEnabled true if caching should be enabled.
     */
    public void setCacheEnabled(boolean isCacheEnabled) {
        this.isCacheEnabled = isCacheEnabled;
    }
    
    /**
     * Opens an input stream for reading the content of the pagelet.
     * 
     * @return the input stream of the pagelet.
     * @throws IOException if unable to create a new input stream.
     */
    public InputStream openStream() throws IOException {
        if (isCacheEnabled) {
            return openStreamFromCache();
        }
        else {
            return pagelet.openStream();
        }
    }
    
    /**
     * Returns a string representation of this pagelet.
     * 
     * @return a string representation of this pagelet.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return id + " [" + pagelet + "]";
    }
    
    /**
     * Opens an input stream of the pagelet from the cache.
     * 
     * @return the input stream of the pagelet.
     * @throws IOException if unable to create a new input stream.
     */
    private synchronized InputStream openStreamFromCache() throws IOException {
        byte[] cache = (byte[]) (content==null? null:content.get());
        if (cache == null) {
            InputStream ins = pagelet.openStream();
            byte[] bytes = IOHandler.readBytes(ins);
            content = new SoftReference(bytes);
            cache = bytes;
        }
        return new ByteArrayInputStream(cache);
    }
}