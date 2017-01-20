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

