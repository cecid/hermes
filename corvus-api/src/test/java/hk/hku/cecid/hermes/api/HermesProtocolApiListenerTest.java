/* 
 * Copyright(c) 2016 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.hermes.api.listener;

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
        String protocol = this.listener.getProtocolFromPathInfo("/aa");
        Assert.assertEquals("aa", protocol);
    }

    @Test
    public void testGetProtocolFromPathInfo_two_segments() {
        String protocol = this.listener.getProtocolFromPathInfo("/aa/bb");
        Assert.assertEquals("bb", protocol);
    }

    @Test
    public void testGetProtocolFromPathInfo_three_segments() {
        String protocol = this.listener.getProtocolFromPathInfo("/aa/bb/cc");
        Assert.assertEquals("cc", protocol);
    }

    @Test
    public void testGetProtocolFromPathInfo_end_with_slash() {
        String protocol = this.listener.getProtocolFromPathInfo("/aa/bb/cc/");
        Assert.assertEquals("cc", protocol);
    }

    @Test
    public void testGetProtocolFromPathInfo_no_segment() {
        String protocol = this.listener.getProtocolFromPathInfo("");
        Assert.assertEquals("", protocol);
    }
}
