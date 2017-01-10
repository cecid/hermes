package hk.hku.cecid.hermes.api.listener;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * HermesAbstractApiListenerTest
 * 
 * @author Patrick Yee
 *
 */
public class HermesAbstractApiListenerTest {
    @Test
    public void testFillDate() {
        HermesAbstractApiListener listener = new HermesAbstractApiListener();
        HashMap<String, Object> dictionary = new HashMap<String, Object>();
        listener.fillDate(dictionary);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(true, dictionary.containsKey("server_time"));
    }

    @Test
    public void testCreaetError() {
        HermesAbstractApiListener listener = new HermesAbstractApiListener();
        Map<String, Object> dictionary = listener.createError(123, "test message");
        Assert.assertEquals(2, dictionary.size());
        Assert.assertEquals(123, dictionary.get("code"));
        Assert.assertEquals("test message", dictionary.get("message"));
    }
}

