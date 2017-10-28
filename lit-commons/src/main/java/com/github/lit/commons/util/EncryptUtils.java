package com.github.lit.commons.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User : liulu
 * Date : 2017/10/22 17:08
 * version $Id: EncryptUtils.java, v 0.1 Exp $
 */
public class EncryptUtils {

    public static final String KEY_SHA = "SHA";
    public static final String KEY_MD5 = "MD5";


    public static String md5(String data){

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance(KEY_MD5);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("加密失败 !", e);
        }

        md5.update(data.getBytes());

        return new BigInteger(1, md5.digest()).toString(16);
    }

}
