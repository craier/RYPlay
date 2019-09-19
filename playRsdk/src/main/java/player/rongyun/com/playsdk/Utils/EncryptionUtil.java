package player.rongyun.com.playsdk.Utils;

import android.annotation.SuppressLint;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class EncryptionUtil {
    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     * @throws InvalidAlgorithmParameterException
     * @throws Exception
     */
    @SuppressLint("TrulyRandom")
    public static String encode(String key, String data) {
        if (data == null)
            return null;
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            //key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
            byte[] bytes = cipher.doFinal(data.getBytes());
            return byte2hex(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * DES算法，解密
     *
     * @param data 待解密字符串
     * @param key  解密私钥，长度不能够小于8位
     * @return 解密后的字节数组
     * @throws Exception 异常
     */
    public static String decode(String key, String data) {
        if (data == null)
            return null;
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            //key的长度不能够小于8位字节
            Key secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
            AlgorithmParameterSpec paramSpec = iv;
            cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
            return new String(cipher.doFinal(hex2byte(data.getBytes())));
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    /**
     * 二行制转字符串
     *
     * @param b
     * @return
     */
    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

    /**
     * 十六进制转二行制
     *
     * @param b
     * @return
     */
    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException();
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    /**
     * Base64加密
     *
     * @param content
     * @return
     */
    public static String encodeBase64(String content) {
        return Base64.encode(content.getBytes());
    }

    /**
     * Base64加密
     *
     * @param bytes
     * @return
     */
    public static String encodeBase64(byte[] bytes) {
        return Base64.encode(bytes);
    }

    /**
     * Base64解密
     *
     * @param content
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] decodeBase64(String content) throws UnsupportedEncodingException {
        return Base64.decode(content);
    }

    /**
     * MD5加密
     *
     * @param content
     * @return
     */
    public static String encodeMD5(String content) {
        return encode(CryptType.MD5, content);
    }

    /**
     * SHA1加密
     *
     * @param content
     * @return
     */
    public static String encodeSHA1(String content) {
        return encode(CryptType.SHA1, content);
    }

    /**
     * SHA256加密
     *
     * @param content
     * @return
     */
    public static String encodeSHA256(String content) {
        return encode(CryptType.SHA256, content);
    }

    public static String urlEncode(String str) throws UnsupportedEncodingException {
        return URLEncoder.encode(str, "utf-8").replaceAll("\\+", "%20").replaceAll("%7E", "~")
                .replaceAll("\\*", "%2A");
    }

    public static String urlDecode(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str, "utf-8");
    }


    /**
     * MD5、SHA1、SHA256加密
     *
     * @param type
     * @param content
     * @return
     */
    public static String encode(CryptType type, String content) {
        MessageDigest instance = null;
        Object encryptMsg = null;

        byte[] var7;
        try {
            if (CryptType.MD5 == type) {
                instance = MessageDigest.getInstance("MD5");
            } else if (CryptType.SHA1 == type) {
                instance = MessageDigest.getInstance("SHA-1");
            } else if (CryptType.SHA256 == type) {
                instance = MessageDigest.getInstance("SHA-256");
            }

            if (instance == null) {
                throw new RuntimeException("instance is null");
            }

            var7 = instance.digest(content.getBytes());
        } catch (NoSuchAlgorithmException var6) {
            throw new RuntimeException("Unbelievabl! How can u passby the method ? No such " +
                    "algorithm !");
        }

        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < var7.length; ++i) {
            switch (Integer.toHexString(var7[i]).length()) {
                case 1:
                    buffer.append("0");
                    buffer.append(Integer.toHexString(var7[i]));
                    break;
                case 2:
                    buffer.append(Integer.toHexString(var7[i]));
                    break;
                case 8:
                    buffer.append(Integer.toHexString(var7[i]).substring(6, 8));
            }
        }

        return buffer.toString();
    }

    /**
     * 加密方式
     */
    public enum CryptType {
        MD5,
        SHA1,
        SHA256;
    }
}
