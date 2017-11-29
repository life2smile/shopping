package com.shopping.hanxiao.shopping.utils;


import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by wenzhi on 2017/11/29.
 */

public final class TokenUtil {
    private TokenUtil() {

    }

    static class TokenHodler {
        static TokenUtil sTokenUtil = new TokenUtil();
    }

    public static TokenUtil instance() {
        return TokenHodler.sTokenUtil;
    }

    public String generateToken() {
        String seed = String.valueOf(System.currentTimeMillis() + new Random().nextInt());
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            byte[] digest = messageDigest.digest(seed.getBytes());
            return Base64.encodeToString(digest, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            return seed;
        }
    }
}
