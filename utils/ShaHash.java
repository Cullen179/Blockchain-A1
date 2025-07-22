package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShaHash {

  public static String sha256Hash(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(input.getBytes());

      // Convert to hexadecimal string
      StringBuilder hexString = new StringBuilder();
      for (byte b : hashBytes) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not available", e);
    }
  }

  // display the hash in a shortened format
  public static String getShortHash(String hash) {
    if (hash == null || hash.length() < 8) {
      return hash; // Return as is if too short
    }
    return hash.substring(0, 8) + "..."; // Shorten to first 8 characters
  }
}
