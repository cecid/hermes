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

    @Test
    public void testGetOptionalStringFromInput() {
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("key1", "value1");

        Map<String, Object> error = new HashMap<String, Object>();
        String defaultValue = "default";

        Assert.assertEquals("value1", listener.getOptionalStringFromInput(input, "key1", defaultValue, error));
        Assert.assertEquals(0, error.size());

        Assert.assertEquals(defaultValue, listener.getOptionalStringFromInput(input, "key2", defaultValue, error));
        Assert.assertEquals(0, error.size());
    }

    @Test
    public void testGetLongFromInput() {
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("key1", new Long(123));
        input.put("key2", "string");

        Map<String, Object> error = new HashMap<String, Object>();

        Assert.assertEquals(new Long(123), listener.getLongFromInput(input, "key1", error));
        Assert.assertEquals(0, error.size());

        Assert.assertEquals(null, listener.getLongFromInput(input, "key2", error));
        Assert.assertEquals(2, error.size());

        error.clear();
        Assert.assertEquals(null, listener.getLongFromInput(input, "key3", error));
        Assert.assertEquals(2, error.size());
    }

    @Test
    public void testGetOptionalLongFromInput() {
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("key1", new Long(123));
        input.put("key2", "string");

        Map<String, Object> error = new HashMap<String, Object>();
        int defaultValue = 112;

        Assert.assertEquals(new Long(123), listener.getOptionalLongFromInput(input, "key1", defaultValue, error));
        Assert.assertEquals(0, error.size());

        Assert.assertEquals(null, listener.getLongFromInput(input, "key2", error));
        Assert.assertEquals(2, error.size());

        error.clear();
        Assert.assertEquals(new Long(defaultValue), listener.getOptionalLongFromInput(input, "key3", defaultValue, error));
        Assert.assertEquals(0, error.size());
    }

    @Test
    public void testGetOptionalBooleanFromInput() {
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("key1", new Boolean(false));
        input.put("key2", "string");

        Map<String, Object> error = new HashMap<String, Object>();
        boolean defaultValue = true;

        Assert.assertEquals(new Boolean(false), listener.getOptionalBooleanFromInput(input, "key1", defaultValue, error));
        Assert.assertEquals(0, error.size());

        Assert.assertEquals(null, listener.getOptionalBooleanFromInput(input, "key2", defaultValue, error));
        Assert.assertEquals(2, error.size());

        error.clear();
        Assert.assertEquals(new Boolean(defaultValue), listener.getOptionalBooleanFromInput(input, "key3", defaultValue, error));
        Assert.assertEquals(0, error.size());
    }
}
