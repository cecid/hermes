/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.pkg;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;


/**
 * DispositionNotificationOption represents an AS2 disposition notification
 * option.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class DispositionNotificationOption {

    public static final String IMPORTANCE_REQURIED = "required";
    
    public static final String IMPORTANCE_OPTIONAL = "optional";
    
    public static final String SIGNED_RECEIPT_PROTOCOL_PKCS7 = "pkcs7-signature";
    
    public static final String SIGNED_RECEIPT_MICALG_SHA1 = "sha1";
    
    public static final String SIGNED_RECEIPT_MICALG_MD5 = "md5";
    
    private String name;
    
    private String importance;
    
    private List values = new Vector();
    
    public DispositionNotificationOption() {
    }
    
    public DispositionNotificationOption(String option) {
        String optName    = null;
        String optImpt    = null;
        String[] optVals  = null;
        
        if (option != null) {
            int sep = option.indexOf('=');
            optName    = option.substring(0, sep == -1? option.length() : sep).trim();
            if (sep != -1) { 
                String optValue = option.substring(sep+1).trim();
                sep = optValue.indexOf(',');
                optImpt = optValue.substring(0, sep == -1? optValue.length() : sep).trim();
                if (sep != -1) {
                    optVals = optValue.substring(sep+1, optValue.length()).split(",");
                }
            }
        }
        initOption(optName, optImpt, optVals);
    }
    
    public DispositionNotificationOption(String name, String importance, String[] values) {
        initOption(name, importance, values);
    }
    
    private void initOption(String name, String importance, String[] values) {
        this.name = name == null? "unknown":name;
        this.importance = importance == null? IMPORTANCE_OPTIONAL:importance;
        for (int i=0; values!=null && i<values.length; i++) {
            this.values.add(values[i].trim());
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getImportance() {
        return importance;
    }
    
    public void setImportance(String importance) {
        this.importance = importance;
    }
    
    public boolean isRequired() {
        return IMPORTANCE_REQURIED.equalsIgnoreCase(getImportance());
    }
    
    public String getValue() {
        return getValue(0);
    }
    
    public String getValue(int pos) {
        if (pos <0 || pos >= values.size()) {
            return null;
        }
        else {
            return (String)values.get(pos);
        }
    }

    public Iterator getValues() {
        return values.iterator();
    }
    
    public void addValue(String value) {
        if (value != null) {
            values.add(value);
        }
    }
    
    public int countValues() {
        return values.size();
    }

    public void removeValue(int pos) {
        if (pos >=0 && pos < values.size()) {
            values.remove(pos);
        }
    }
    
    public void removeValues() {
        values.clear();
    }
    
    public boolean containsValue(String value) {
        if (value == null) {
            return false;
        }
        else {
            return values.contains(value.trim());
        }
    }

    public String toString() {
        StringBuffer option = new StringBuffer();
        option.append(name).append("=").append(importance);
        Iterator vals = getValues();
        while (vals.hasNext()) {
            option.append(", ").append(vals.next());
        }
        return option.toString();
    }
}
