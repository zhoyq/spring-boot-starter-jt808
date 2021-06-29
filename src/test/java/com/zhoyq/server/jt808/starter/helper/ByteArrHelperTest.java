package com.zhoyq.server.jt808.starter.helper;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-06-29
 */
public class ByteArrHelperTest {
    @Test
    public void unionTest() {
        byte[] a = new byte[]{1, 2, 3};
        byte[] b = new byte[]{4, 5, 6};
        byte[] c = ByteArrHelper.union(a, b);
        Assert.assertArrayEquals(c, new byte[]{1, 2, 3, 4, 5, 6});
    }

    @Test
    public void subByteTest() {
        byte[] a = new byte[]{1, 2, 3, 4, 5, 6};
        byte[] b = ByteArrHelper.subByte(a, 1);
        byte[] c = ByteArrHelper.subByte(a, 1, 3);

        Assert.assertArrayEquals(b, new byte[]{2, 3, 4, 5, 6});
        Assert.assertArrayEquals(c, new byte[]{2, 3});
    }
}
