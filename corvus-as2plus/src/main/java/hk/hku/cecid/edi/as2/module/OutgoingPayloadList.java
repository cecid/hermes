/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.edi.as2.module;

import hk.hku.cecid.edi.as2.AS2PlusProcessor;
import hk.hku.cecid.piazza.commons.module.ActiveTaskList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * OutgoingPayloadList
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class OutgoingPayloadList extends ActiveTaskList {

    public List getTaskList() {
        try {
            Iterator payloads = AS2PlusProcessor.getInstance().getOutgoingPayloadRepository().getPayloadCaches().iterator();
            
            List tasks = new ArrayList();
            while (payloads.hasNext()) {
                try {
                    PayloadCache cache = (PayloadCache)payloads.next(); 
                    if (cache.isValid()) {
                        OutgoingPayloadTask task = new OutgoingPayloadTask(cache);
                        tasks.add(task);
                    }
                    else {
                        AS2PlusProcessor.getInstance().getLogger().error("Invalid payload cache: "+cache);
                    }
                }
                catch (Exception e) {
                    AS2PlusProcessor.getInstance().getLogger().error("Error in creating outgoing payload task", e);
                }
            }
            return tasks;
        }
        catch (Exception e) {
            AS2PlusProcessor.getInstance().getLogger().error("Error in retrieving outgoing payloads", e);
            return Collections.EMPTY_LIST;
        }
    }

}
