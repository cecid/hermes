/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.module;

import hk.hku.cecid.edi.as2.pkg.AS2Message;
import hk.hku.cecid.piazza.commons.io.IOHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * PayloadCache
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class PayloadCache {

    private File cache;
    private String messageID;
    private String fromPartyID;
    private String toPartyID;
    private String type;
    
    private PayloadRepository repository;
    
    public PayloadCache(PayloadRepository repository, String messageID, String fromPartyID, String toPartyID, String type) {
        this.repository = repository;
        this.messageID = messageID;
        this.fromPartyID = fromPartyID;
        this.toPartyID = toPartyID;
        
        if (type == null || type.indexOf('/') != -1) {
            this.type = repository.getPayloadType(type);
        }
        else {
            this.type = type;
        }
        
        this.cache = createCacheFile(true);
    }
    
    public PayloadCache(PayloadRepository repository, File file) {
        this.repository = repository;
        this.cache = file;
        String filename = cache.getName();
        
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(filename);
        
        if (m.find()) {
            this.fromPartyID = decodeFilename(m.group(1));
        }
        if (m.find()) {
            this.toPartyID = decodeFilename(m.group(1));
        }

        boolean isAutoID = false;
        if (m.find()) {
            this.messageID = decodeFilename(m.group(1));
            if (messageID != null && messageID.startsWith("auto")) {
                isAutoID = true;
                messageID = AS2Message.generateID();
            }
        }
        
        p = Pattern.compile("\\.([^\\]]*)$");
        m = p.matcher(filename);
        if (m.find()) {
            this.type = m.group(1);
        }
        
        if (isAutoID) {
            File autoFile = createCacheFile(isCheckedOut());
            renameCacheFile(autoFile);
        }
    }
    
    private String encodeFilename(String filename) {
        StringBuffer buffer = new StringBuffer(); 
        if (filename != null) {
            Pattern p = Pattern.compile("[\\\\/:\\*\\?\"\\<\\>\\|\\[\\]%]");
            Matcher m = p.matcher(filename);
            int i = 0;
            while (m.find()) {
                buffer.append(filename.substring(i, m.start()));
                char sc = m.group().charAt(0);
                buffer.append("%").append(Integer.toHexString((int)sc).toUpperCase());
                i = m.end();
            }
            buffer.append(filename.substring(i, filename.length()));
        }
        return buffer.toString();
    }
    
    private String decodeFilename(String filename) {
        StringBuffer buffer = new StringBuffer(); 
        if (filename != null) {
            Pattern p = Pattern.compile("[\\%]([\\w][\\w])");
            Matcher m = p.matcher(filename);
            int i = 0;
            while (m.find()) {
                buffer.append(filename.substring(i, m.start()));
                String sc = m.group(1);
                buffer.append((char)Integer.parseInt(sc, 16));
                i = m.end();
            }
            buffer.append(filename.substring(i, filename.length()));
        }
        return buffer.toString();
    }
    
    private File createCacheFile(boolean isCheckedOut) {
        String filename = "[" + encodeFilename(fromPartyID) + "].[" + encodeFilename(toPartyID) + "].[" + encodeFilename(messageID) + "]." + type;
        if (isCheckedOut) {
            filename = "~" + filename;
        }
        return new File (repository.getRepository(), filename);
    }
    
    private boolean renameCacheFile(File newFile) {
        boolean renamed = newFile==null? false:cache.renameTo(newFile);
        if (renamed) {
            cache = newFile;
        }
        return renamed;
    }
    
    public boolean isCheckedOut() {
        return cache.getName().startsWith("~");
    }
    
    public boolean checkOut() {
        String cacheName = cache.getName();
        if (!isCheckedOut()) {
            File coutFile = new File(repository.getRepository(), "~"+cacheName);
            return renameCacheFile(coutFile);
        }
        else {
            return true;
        }
    }
    
    public boolean checkIn() {
        String cacheName = cache.getName();
        if (isCheckedOut()) {
            File cinFile = new File(repository.getRepository(), cacheName.substring(1));
            return renameCacheFile(cinFile);
        }
        else {
            return true;
        }
    }
    
    public InputStream load() throws FileNotFoundException {
        return new FileInputStream(cache);
    }
    
    public void save(InputStream content) throws FileNotFoundException, IOException {
        OutputStream outs = new FileOutputStream(cache);
        IOHandler.pipe(content, outs);
        outs.close();
        outs = null;
    }
    
    public String getFromPartyID() {
        return fromPartyID;
    }
    
    public String getToPartyID() {
        return toPartyID;
    }
    
    public boolean clear() {
        boolean deleted =cache.delete();
        if (!deleted) {
            cache.deleteOnExit();
        }
        return deleted;
    }
    
    public File getCache() {
        return cache;
    }
    
    public String getMessageID() {
        return messageID;
    }
    
    public String toString() {
        return cache.getAbsolutePath();
    }
    
    public String getType() {
        return type;
    }
    
    public String getContentType() {
        return repository.getPayloadContentType(type);
    }

    public boolean isValid() {
        return this.fromPartyID != null && this.toPartyID != null && this.messageID != null && type != null;
    }
}