package com.github.lit.commons.util;

import com.github.lit.commons.spring.BCrypt;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * User : liulu
 * Date : 2017/10/22 17:08
 * version $Id: EncryptUtils.java, v 0.1 Exp $
 */
public class EncryptUtils {

    public static final String KEY_SHA = "SHA";
    public static final String KEY_MD5 = "MD5";

    private static Pattern BCRYPT_PATTERN = Pattern
            .compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");


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

    public static String encodeBCrypt(CharSequence rawPassword) {
        return encodeBCrypt(rawPassword, -1, null);
    }

    public static String encodeBCrypt(CharSequence rawPassword, int strength) {
        return encodeBCrypt(rawPassword, strength, null);
    }


    public static String encodeBCrypt(CharSequence rawPassword, int strength, SecureRandom random) {
        String salt;
        if (strength > 0) {
            if (random != null) {
                salt = BCrypt.gensalt(strength, random);
            }
            else {
                salt = BCrypt.gensalt(strength);
            }
        }
        else {
            salt = BCrypt.gensalt();
        }
        return BCrypt.hashpw(rawPassword.toString(), salt);
    }

    public static boolean matchesBCrypt(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.length() == 0) {
            return false;
        }

        if (!BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
            return false;
        }

        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
    }


}
