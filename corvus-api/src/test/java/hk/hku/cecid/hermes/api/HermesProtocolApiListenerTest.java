package hk.hku.cecid.hermes.api.listener;

import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * HermesProtocolApiListenerTest
 * 
 * @author Patrick Yee
 *
 */
public class HermesProtocolApiListenerTest {
    protected HermesProtocolApiListener listener;

    @Before
    public void setup() {
        this.listener = new HermesProtocolApiListener();
    }

    @Test
    public void testGetProtocolFromPathInfo_one_segment() {
        ArrayList<String> parts = this.listener.parseFromPathInfo("/aa");
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals("aa", parts.get(0));
        Assert.assertEquals("", parts.get(1));
        Assert.assertEquals("", parts.get(2));
    }

    @Test
    public void testGetProtocolFromPathInfo_two_segments() {
        ArrayList<String> parts = this.listener.parseFromPathInfo("/aa/bb");
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals("aa", parts.get(0));
        Assert.assertEquals("bb", parts.get(1));
        Assert.assertEquals("", parts.get(2));
    }

    @Test
    public void testGetProtocolFromPathInfo_three_segments() {
        ArrayList<String> parts = this.listener.parseFromPathInfo("/aa/bb/cc");
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals("aa", parts.get(0));
        Assert.assertEquals("bb", parts.get(1));
        Assert.assertEquals("cc", parts.get(2));
    }

    @Test
    public void testGetProtocolFromPathInfo_four_segments() {
        ArrayList<String> parts = this.listener.parseFromPathInfo("/aa/bb/cc/dd");
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals("aa", parts.get(0));
        Assert.assertEquals("bb", parts.get(1));
        Assert.assertEquals("cc/dd", parts.get(2));
    }

    @Test
    public void testGetProtocolFromPathInfo_four_segments_no_prefix() {
        ArrayList<String> parts = this.listener.parseFromPathInfo("aa/bb/cc/dd");
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals("aa", parts.get(0));
        Assert.assertEquals("bb", parts.get(1));
        Assert.assertEquals("cc/dd", parts.get(2));
    }

    @Test
    public void testGetProtocolFromPathInfo_end_with_slash() {
        ArrayList<String> parts = this.listener.parseFromPathInfo("/aa/bb/cc/");
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals("aa", parts.get(0));
        Assert.assertEquals("bb", parts.get(1));
        Assert.assertEquals("cc", parts.get(2));
    }

    @Test
    public void testGetProtocolFromPathInfo_no_segment() {
        ArrayList<String> parts = this.listener.parseFromPathInfo("");
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals("", parts.get(0));
        Assert.assertEquals("", parts.get(1));
        Assert.assertEquals("", parts.get(2));
    }

    @Test
    public void testGetProtocolFromPathInfo_some_empty_segment() {
        ArrayList<String> parts = this.listener.parseFromPathInfo("aa//cc/dd");
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals("aa", parts.get(0));
        Assert.assertEquals("", parts.get(1));
        Assert.assertEquals("cc/dd", parts.get(2));
    }

    @Test
    public void testGetProtocolFromPathInfo_multiple_action_parts() {
        ArrayList<String> parts = this.listener.parseFromPathInfo("aa/bb/cc/dd", 2);
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals("aa/bb", parts.get(0));
        Assert.assertEquals("cc", parts.get(1));
        Assert.assertEquals("dd", parts.get(2));
    }

    @Test
    public void testGetProtocolFromPathInfo_multiple_action_parts_with_empty_segment() {
        ArrayList<String> parts = this.listener.parseFromPathInfo("aa/bb/cc/dd", 3);
        Assert.assertEquals(3, parts.size());
        Assert.assertEquals("aa/bb/cc", parts.get(0));
        Assert.assertEquals("dd", parts.get(1));
        Assert.assertEquals("", parts.get(2));
    }
}
