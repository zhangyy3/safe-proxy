package com.zhangyangyang.util.test;

import com.zhangyangyang.proxy.EncryptException;
import com.zhangyangyang.proxy.util.AES;

import org.junit.Test;

/**
 * Created by zhangyangyang on 2018/4/8.
 * test AES
 */
public class AESTest {

    @Test
    public void testAES() throws EncryptException {
        String password = "123456";
        String text = "中车苛嘲进进； 苛工地；lasf模压aljk ljalksdfj;laskdjf asdkfj;lasdkjfl;ads fklj加；回归；虽； 中asdf 塔顶 sdaf要";
        text += text;
        text += text;

        byte[] cipher = AES.encrypt(password, text.getBytes());
        System.out.println(cipher.length);
        byte[] plain = AES.decrypt(password, cipher);
        System.out.println(new String(plain));
    }
}
