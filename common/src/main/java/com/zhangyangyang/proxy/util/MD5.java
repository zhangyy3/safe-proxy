package com.zhangyangyang.proxy.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhangyangyang on 2018/4/3.
 * md5 util
 */
public class MD5 {

    private MD5() {

    }

    public static String md5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("md5");
        byte[] bytes = digest.digest(str.getBytes("utf8"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++){
            int val = ((int) bytes[i]) & 0xff;
            if (val < 16)
                sb.append("0");
            sb.append(Integer.toHexString(val));
        }
        return sb.toString();
    }

}
