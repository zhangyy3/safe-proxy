package com.zhangyangyang.proxy.client.test;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by zhangyangyang on 2018/3/27.
 * test \r\n
 */
public class CharTest {

    @Test
    public void testChar2byte() {
        //[13, 10]
        String rn = "\r\n";
        System.out.println(Arrays.toString(rn.getBytes()));
    }
}
