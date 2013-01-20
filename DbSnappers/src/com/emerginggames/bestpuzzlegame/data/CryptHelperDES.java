package com.emerginggames.bestpuzzlegame.data;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 05.04.12
 * Time: 9:31
 */
public class CryptHelperDES {

    private static final byte[] salt = {
            (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
            (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
    };

    // Iteration count
    private static final int count = 20;

    private static SecretKey pbeKey;
    private static PBEParameterSpec pbeParamSpec;
    private static String storedPassword;

    private static PBEParameterSpec getSpec(){
        if (pbeParamSpec == null)
            pbeParamSpec = new PBEParameterSpec(salt, count);
        return pbeParamSpec;
    }
    
    private static SecretKey getKey(String password) throws Exception{
        if (pbeKey == null || !storedPassword.equals(password)){
            storedPassword = password;
            PBEKeySpec pbeKeySpec;
            SecretKeyFactory keyFac;

            pbeKeySpec = new PBEKeySpec(password.toCharArray());
            keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            pbeKey = keyFac.generateSecret(pbeKeySpec);
        }
        return pbeKey;
    }

    public static String encrypt(String seed, String cleartext) throws Exception {
        return toHex(encryptToBytes(seed, cleartext));
    }

    public static byte[] encryptToBytes(String seed, String cleartext) throws Exception {
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

        // Initialize PBE Cipher with key and parameters
        pbeCipher.init(Cipher.ENCRYPT_MODE, getKey(seed), getSpec());

        // Encrypt the cleartext
        return  pbeCipher.doFinal(cleartext.getBytes());
    }

    public static String decrypt(String seed, String encrypted) throws Exception {
        return decrypt(seed, toByte(encrypted));
    }

    public static String decrypt(String seed, byte[] encrypted) throws Exception {
        // Create PBE Cipher
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

        // Initialize PBE Cipher with key and parameters
        pbeCipher.init(Cipher.DECRYPT_MODE, getKey(seed), getSpec());

        // Encrypt the cleartext
        byte[] decrypted = pbeCipher.doFinal(encrypted);
        return new String(decrypted);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }


    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }
    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }
    private final static String HEX = "0123456789ABCDEF";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }

}
