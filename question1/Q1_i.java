package question1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Q1_i {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            System.out.println("=== Cryptographic Hash Function Demonstration ===\n");
            
            // 1. Get input from user
            System.out.print("Enter a string to hash: ");
            String input = scanner.nextLine();
            
            // 2. Compute and display original hash
            System.out.println("\n=== ORIGINAL HASH ===");
            String originalHash = computeHash(input);
            System.out.println("Input: \"" + input + "\"");
            System.out.println("SHA-256 Hash: " + originalHash);
            System.out.println("Hash Length: " + originalHash.length() + " characters");
            
            // 3. Demonstrate avalanche effect with single character change
            System.out.println("\n=== AVALANCHE EFFECT DEMONSTRATION ===");
            demonstrateAvalancheEffect(input, originalHash);
            
            // 4. Show average difference when changing bit positions
            System.out.println("\n=== BIT POSITION ANALYSIS (0-103) ===");
            analyzeBitPositionChanges(input);
            
            scanner.close();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Computes SHA-256 hash of input string
     */
    private static String computeHash(String input) throws NoSuchAlgorithmException {
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
    }
    
    /**
     * Demonstrates avalanche effect with minimal changes
     */
    private static void demonstrateAvalancheEffect(String original, String originalHash) 
        throws NoSuchAlgorithmException {

      // Test 1: Single character change
      String modified1 = modifyString(original, 0, 'X');
      String modifiedHash1 = computeHash(modified1);

      System.out.println("1. Single Character Change:");
      System.out.println("   Original: \"" + original + "\"");
      System.out.println("   Modified: \"" + modified1 + "\"");
      System.out.println("   Change: Position 0, '" + (original.length() > 0 ? original.charAt(0) : ' ') + "' → 'X'");
      System.out.println();
      displayHashComparison(originalHash, modifiedHash1);

      int differences1 = calculateHammingDistance(originalHash, modifiedHash1);
      double percentage1 = (differences1 * 100.0) / originalHash.length();
      System.out.println("   Differences: " + differences1 + "/" + originalHash.length() +
          " characters (" + String.format("%.1f", percentage1) + "%)");

      // Test 2: Single bit flip (if possible)
      if (original.length() > 0) {
        System.out.println("\n2. Single Bit Flip:");
        String modified2 = flipBit(original, 0, 0); // Flip first bit of first character
        String modifiedHash2 = computeHash(modified2);

        System.out.println("   Original: \"" + original + "\"");
        System.out.println("   Modified: \"" + modified2 + "\" (bit 0 of char 0 flipped)");
        System.out.println();
        displayHashComparison(originalHash, modifiedHash2);

        int differences2 = calculateHammingDistance(originalHash, modifiedHash2);
        double percentage2 = (differences2 * 100.0) / originalHash.length();
        System.out.println("   Differences: " + differences2 + "/" + originalHash.length() +
            " characters (" + String.format("%.1f", percentage2) + "%)");
      }

      // Test 3: Case change
      if (original.length() > 0 && Character.isLetter(original.charAt(0))) {
        System.out.println("\n3. Case Change:");
        char firstChar = original.charAt(0);
        char changedChar = Character.isUpperCase(firstChar) ? Character.toLowerCase(firstChar)
            : Character.toUpperCase(firstChar);
        String modified3 = modifyString(original, 0, changedChar);
        String modifiedHash3 = computeHash(modified3);

        System.out.println("   Original: \"" + original + "\"");
        System.out.println("   Modified: \"" + modified3 + "\"");
        System.out.println();
        displayHashComparison(originalHash, modifiedHash3);

        int differences3 = calculateHammingDistance(originalHash, modifiedHash3);
        double percentage3 = (differences3 * 100.0) / originalHash.length();
        System.out.println("   Differences: " + differences3 + "/" + originalHash.length() +
            " characters (" + String.format("%.1f", percentage3) + "%)");
      }
    }
    
    /**
     * Displays visual comparison of two hashes with connecting lines for same
     * characters
     */
    private static void displayHashComparison(String hash1, String hash2) {
      System.out.println("   Original Hash: " + hash1);
      System.out.print("                  ");

      // Create connecting line - show | only for same characters
      for (int i = 0; i < Math.min(hash1.length(), hash2.length()); i++) {
        if (hash1.charAt(i) == hash2.charAt(i)) {
          System.out.print("|");
        } else {
          System.out.print(" ");
        }
      }
      System.out.println();
      System.out.println("   Modified Hash: " + hash2);
    }
    
    /**
     * Analyzes average differences when flipping bits at positions 0-103
     */
    private static void analyzeBitPositionChanges(String input) throws NoSuchAlgorithmException {
        if (input.length() == 0) {
            System.out.println("Cannot analyze bit positions - input string is empty");
            return;
        }
        
        String originalHash = computeHash(input);
        int totalDifferences = 0;
        int validTests = 0;
        int maxBitPosition = Math.min(103, input.length() * 8 - 1);
        
        System.out.println("Testing bit flips from position 0 to " + maxBitPosition + "...");
        System.out.println("Bit Pos | Differences | Percentage | Sample Modified String");
        System.out.println("--------|-------------|------------|----------------------");
        
        for (int bitPos = 0; bitPos <= maxBitPosition; bitPos++) {
            int charIndex = bitPos / 8;
            int bitIndex = bitPos % 8;
            
            if (charIndex < input.length()) {
                String modified = flipBit(input, charIndex, bitIndex);
                String modifiedHash = computeHash(modified);
                int differences = calculateHammingDistance(originalHash, modifiedHash);
                double percentage = (differences * 100.0) / originalHash.length();
                
                totalDifferences += differences;
                validTests++;
                
                // Show every 10th result to avoid too much output
                if (bitPos % 10 == 0 || bitPos == maxBitPosition) {
                    String sampleModified = modified.length() > 20 ? 
                                           modified.substring(0, 20) + "..." : modified;
                    System.out.printf("%7d | %11d | %9.1f%% | %s%n", 
                                    bitPos, differences, percentage, sampleModified);
                }
            }
        }
        
        if (validTests > 0) {
            double averageDifferences = (double) totalDifferences / validTests;
            double averagePercentage = (averageDifferences * 100.0) / originalHash.length();
            
            System.out.println("--------|-------------|------------|----------------------");
            System.out.printf("Average | %11.1f | %9.1f%% | (across %d tests)%n", 
                            averageDifferences, averagePercentage, validTests);
            
            System.out.println("\n=== ANALYSIS SUMMARY ===");
            System.out.println("Input string length: " + input.length() + " characters (" + (input.length() * 8) + " bits)");
            System.out.println("Hash output length: " + originalHash.length() + " hex characters (256 bits)");
            System.out.println("Tests performed: " + validTests + " bit flips");
            System.out.println("Average differences: " + String.format("%.1f", averageDifferences) + " out of " + originalHash.length());
            System.out.println("Average change percentage: " + String.format("%.1f", averagePercentage) + "%");
            
            if (averagePercentage > 45) {
                System.out.println("Excellent avalanche effect - small changes cause ~50% hash differences");
            } else {
                System.out.println("⚠️  Avalanche effect: " + String.format("%.1f", averagePercentage) + "% (ideal is ~50%)");
            }
        }
    }
    
    /**
     * Modifies a string by changing character at specified position
     */
    private static String modifyString(String original, int position, char newChar) {
        if (original.length() == 0) {
            return String.valueOf(newChar);
        }
        if (position >= original.length()) {
            return original + newChar;
        }
        
        StringBuilder sb = new StringBuilder(original);
        sb.setCharAt(position, newChar);
        return sb.toString();
    }
    
    /**
     * Flips a specific bit in a string
     */
    private static String flipBit(String original, int charIndex, int bitIndex) {
        if (charIndex >= original.length()) {
            return original;
        }
        
        char[] chars = original.toCharArray();
        chars[charIndex] = (char) (chars[charIndex] ^ (1 << bitIndex));
        return new String(chars);
    }
    
    /**
     * Calculates Hamming distance between two hash strings
     */
    private static int calculateHammingDistance(String hash1, String hash2) {
        int differences = 0;
        int minLength = Math.min(hash1.length(), hash2.length());
        
        for (int i = 0; i < minLength; i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                differences++;
            }
        }
        
        // Add difference for length mismatch
        differences += Math.abs(hash1.length() - hash2.length());
        
        return differences;
    }
} 