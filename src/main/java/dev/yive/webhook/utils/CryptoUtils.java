package dev.yive.webhook.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {
  public static String hmac(String algorithm, String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
    return bytesToHex(hmac(algorithm, data.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8)));
  }

  public static byte[] hmac(String algorithm, byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
    Mac mac = Mac.getInstance(algorithm);
    mac.init(new SecretKeySpec(key, algorithm));
    return mac.doFinal(data);
  }

  public static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte h : hash) {
      String hex = Integer.toHexString(0xff & h);
      if (hex.length() == 1)
        hexString.append('0');
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
