/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.module;

import hk.hku.cecid.piazza.commons.io.FileSystem;
import hk.hku.cecid.piazza.commons.module.Component;
import hk.hku.cecid.piazza.commons.util.PropertyMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * PayloadRepository
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class PayloadRepository extends Component {
    
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final String DEFAULT_PAYLOAD_TYPE = "bin";

    private Hashtable payloadTypes = new Hashtable();
    private File repository;

    public PayloadRepository() {
        super();
    }
    
    protected void init() throws Exception {
        super.init();
        Properties params = getParameters();
        String location = params.getProperty("location");
        initRepository(location);
        
        PropertyMap map = new PropertyMap(params);
        Properties types = map.createProperties("type-");
        setPayloadTypes(types);       
    }

    protected void initRepository(String repository) {
        initRepository(
                repository==null? new File(System.getProperty("user.dir"), "as2-repository"):
                                  new File(repository)
        );
    }
    
    protected void initRepository(File repository) {
        if (!repository.exists()) {
            repository.mkdirs();
        }
        this.repository = repository;
    }

    public PayloadCache createPayloadCache(String messageID, String fromPartyID, String toPartyID, String type) {
        return new PayloadCache(this, messageID, fromPartyID, toPartyID, type);
    }
    
    public List getPayloadCaches() {
        ArrayList payloads = new ArrayList(); 
        FileSystem fs = new FileSystem(repository);
        Iterator files = fs.getFiles(false, "[^\\~\\.].*").iterator();
        while (files.hasNext()) {
            File f = (File)files.next();
            PayloadCache payload = new PayloadCache(this, f);
            payloads.add(payload);
        }
        return payloads;
    }
    
    public String getPayloadContentType(String type) {
        if (type == null) {
            return DEFAULT_CONTENT_TYPE;
        }
        else {
            Object t = payloadTypes.get(type);
            if (t == null) {
                return DEFAULT_CONTENT_TYPE;
            }
            else {
                return t.toString();
            }
        }
    }
    
    public String getPayloadType(String contentType) {
        if (contentType == null) {
            return DEFAULT_PAYLOAD_TYPE;
        }
        else {
            String[] contentTypes = contentType.split(";");
            if (contentTypes.length > 0) {
                String mainContentType = contentTypes[0].trim(); 
                Iterator entries = payloadTypes.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry entry = (Map.Entry)entries.next();
                    String entryContentType = ((String)entry.getValue()).trim();
                    if (mainContentType.equalsIgnoreCase(entryContentType)) {
                        return (String)entry.getKey();
                    }
                }
            }
            return DEFAULT_PAYLOAD_TYPE;
        }
    }
    
    public void setPayloadTypes(Map m) {
        if (m != null) {
            payloadTypes.clear();
            payloadTypes.putAll(m);
        }
    }
    
    public Map getPayloadTypes() {
        return payloadTypes;
    }
    
    public File getRepository() {
        return repository;
    }
}
