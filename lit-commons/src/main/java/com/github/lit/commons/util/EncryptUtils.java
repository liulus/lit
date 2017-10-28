package com.github.lit.commons.util;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream;

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

        return new String(BASE64EncoderStream.encode(md5.digest()));
    }

}
