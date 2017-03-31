package hk.hku.cecid.hermes.api.listener;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * HermesAbstractApiListenerTest
 * 
 * @author Patrick Yee
 *
 */
public class HermesAbstractApiListenerTest {

    private HermesAbstractApiListener listener;

    @Before
    public void setUp() {
        listener = new HermesAbstractApiListener();
    }

    @Test
    public void testFillDate() {
        HashMap<String, Object> dictionary = new HashMap<String, Object>();
        listener.fillDate(dictionary);
        Assert.assertEquals(1, dictionary.size());
        Assert.assertEquals(true, dictionary.containsKey("server_time"));
    }

    @Test
    public void testCreateError() {
        Map<String, Object> dictionary = listener.createError(123, "test message");
        Assert.assertEquals(2, dictionary.size());
        Assert.assertEquals(123, dictionary.get("code"));
        Assert.assertEquals("test message", dictionary.get("message"));
    }

    @Test
    public void testFillError() {
        Map<String, Object> error = new HashMap<String, Object>();
        listener.fillError(error, 123, "test message");
        Assert.assertEquals(123, error.get("code"));
        Assert.assertEquals("test message", error.get("message"));
    }

    @Test
    public void testCreateActionResult() {
        Map<String, Object> dictionary = listener.createActionResult("test id", true);
        Assert.assertEquals("test id", dictionary.get("id"));
        Assert.assertTrue((Boolean) dictionary.get("success"));
    }

    @Test
    public void testGetStringFromInput() {
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("key1", "value1");
        input.put("key2", "123");
        input.put("key3", "false");

        Map<String, Object> error = new HashMap<String, Object>();

        Assert.assertEquals("value1", listener.getStringFromInput(input, "key1", error));
        Assert.assertEquals(0, error.size());

        Assert.assertEquals("123", listener.getStringFromInput(input, "key2", error));
        Assert.assertEquals(0, error.size());

        Assert.assertEquals("false", listener.getStringFromInput(input, "key3", error));
        Assert.assertEquals(0, error.size());
    }
}

