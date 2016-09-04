/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.pkg;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;


/**
 * DispositionNotificationOption represents the AS2 disposition notification
 * options.
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class DispositionNotificationOptions {

    public static final String SIGNED_RECEIPT_PROTOCOL = "signed-receipt-protocol";
    
    public static final String SIGNED_RECEIPT_MICALG = "signed-receipt-micalg";
    
    private Map options = new Hashtable();
    
    public DispositionNotificationOptions() {
    }
    
    public DispositionNotificationOptions(String options) {
        if (options != null) {
            String[] opts = options.split(";");
            for (int i=0; i<opts.length; i++) {
                DispositionNotificationOption option = new DispositionNotificationOption(opts[i]);
                this.options.put(option.getName(), option);
            }
        }
    }
    
    public DispositionNotificationOption getOption(String name) {
        if (name == null) {
            return null;
        }
        else {
            return (DispositionNotificationOption) options.get(name);
        }
    }
    
    public void addOption(DispositionNotificationOption option) {
        if (option != null) {
            options.put(option.getName(), option);
        }
    }
    
    public DispositionNotificationOption addOption(String name) {
        DispositionNotificationOption option = new DispositionNotificationOption();
        option.setName(name);
        option.setImportance(DispositionNotificationOption.IMPORTANCE_OPTIONAL);
        addOption(option);
        return option;
    }
    
    public String toString() {
        String s = "";
        Iterator opts = options.values().iterator();
        while (opts.hasNext()) {
            DispositionNotificationOption opt = (DispositionNotificationOption)opts.next();
            s += opt.toString();
            if (opts.hasNext()) {
                s += "; ";
            }
        }
        return s;
    }
}
