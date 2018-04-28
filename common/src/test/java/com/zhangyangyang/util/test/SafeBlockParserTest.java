package com.zhangyangyang.util.test;

import com.zhangyangyang.proxy.common.SafeBlockWrapper;
import com.zhangyangyang.proxy.proto.SafeBlock;
import com.zhangyangyang.proxy.util.SafeBlockParser;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by zhangyangyang on 2018/4/28.
 * test SafeBlock protocol
 */
public class SafeBlockParserTest {

    @Test
    public void testCorrect() throws IOException {
        String block = "safeBlock 1\r\n1\r\nthis is a test file haha \r\n";
        ByteArrayInputStream in = new ByteArrayInputStream(block.getBytes());
        SafeBlockWrapper wrapper = SafeBlockParser.parse(in);
        System.out.println(wrapper.isSuccess());
        SafeBlock safeBlock = wrapper.getSafeBlock();
        System.out.println(new String(safeBlock.getHeader()));
        System.out.println(new String(safeBlock.getType()));
        System.out.println(new String(safeBlock.getData()));
    }

    @Test
    public void testFailed() throws IOException {
        String block = "\rsa\n\rfeBlock 1\r\n1\r\nthi\r\ns is a test file haha \r\n";
        ByteArrayInputStream in = new ByteArrayInputStream(block.getBytes());
        SafeBlockWrapper wrapper = SafeBlockParser.parse(in);
        System.out.println(wrapper.isSuccess());
    }
}
