/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * HermesApiListenerTest
 * 
 * @author Patrick Yee
 *
 */
public class HermesApiListenerTest {
    @Test
    public void testCreateStatusObject() {
        HermesApiListener listener = new HermesApiListener();
        Map<String, Object> statusObj = listener.createStatusObject();
        Assert.assertEquals(true, statusObj.containsKey("status"));
        Assert.assertEquals(true, statusObj.containsKey("server_time"));
    }
}

