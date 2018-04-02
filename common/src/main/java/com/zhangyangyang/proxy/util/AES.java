package com.zhangyangyang.proxy.util;

import com.zhangyangyang.proxy.EncryptException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zhangyangyang on 2018/4/2.
 * AES encrypt and decrypt
 */
public final class AES {

    private AES() {

    }

    private static Cipher generateCipher(String password, int mode) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        SecretKeySpec spec = new SecretKeySpec(generateKey(password), "AES");
        //https://docs.oracle.com/javase/8/docs/api/javax/crypto/Cipher.html
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(mode, spec);
        return cipher;
    }

    public static byte[] encrypt(String password, byte[] data) throws EncryptException {
        try {
            Cipher cipher = generateCipher(password, Cipher.ENCRYPT_MODE);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new EncryptException("加密失败:" + e.getMessage(), e);
        }

    }

    public static byte[] decrypt(String password, byte[] data) throws EncryptException {
        try {
            Cipher cipher = generateCipher(password, Cipher.DECRYPT_MODE);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new EncryptException("解密失败:" + e.getMessage(), e);
        }
    }

    private static byte[] generateKey(String key) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("md5");
        return digest.digest(key.getBytes("utf-8"));
    }

}
