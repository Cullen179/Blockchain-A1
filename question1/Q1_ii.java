package question1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Q1_ii {
    
    private static final int MAX_ITERATIONS = 1_000_000;
    private static final int TIME_LIMIT_SECONDS = 30;
    private static MessageDigest digest;
    
    public static void main(String[] args) {
      Scanner scanner = new Scanner(System.in);
        try {
            digest = MessageDigest.getInstance("SHA-256");
            
            System.out.println("=== Hash Function Security Demonstration ===\n");

            // Get user input
            System.out.print("Enter a string to test hash function security properties: ");
            String userInput = scanner.nextLine();
            
            // 1. Pre-image Resistance
            System.out.println("1. PRE-IMAGE RESISTANCE TEST");
            System.out.println("Finding input that produces a specific hash output");
            testPreImageResistance(userInput);
            
            // 2. Second Pre-image Resistance  
            System.out.println("\n" + "=".repeat(60) + "\n");
            System.out.println("2. SECOND PRE-IMAGE RESISTANCE TEST");
            System.out.println("Finding different input that produces same hash as known input");
            testSecondPreImageResistance(userInput);

            // 3. Collision Resistance
            System.out.println("\n" + "=".repeat(60) + "\n");
            System.out.println("3. COLLISION RESISTANCE TEST");
            System.out.println("Finding any two different inputs that produce the same hash");
            testCollisionResistance(userInput);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Demonstrates pre-image resistance by trying to find input for target hash
     */
    private static void testPreImageResistance(String userInput) throws NoSuchAlgorithmException {
        // Define target - hash of a known short string
        String targetHash = computeHash(userInput);

        System.out.println("Target hash (hash of \"" + userInput + "\"):");
        System.out.println(targetHash);
        System.out.println();
        
        System.out.println("Attempting to find pre-image...");
        System.out.println("Testing random strings to find input that produces this hash:");
        
        long startTime = System.currentTimeMillis();
        int attempts = 0;
        boolean found = false;
        String foundInput = null;
        
        while (attempts < MAX_ITERATIONS && !found && 
               (System.currentTimeMillis() - startTime) < TIME_LIMIT_SECONDS * 1000) {
            
            // Generate random input string
            String testInput = generateSequentialString(attempts);

            String testHash = computeHash(testInput);
            
            attempts++;
            
            // Show progress every 50,000 attempts
            if (attempts % 50000 == 0) {
                System.out.println("  Attempt " + attempts + ": \"" + testInput + "\" → " + 
                                 testHash.substring(0, 16) + "...");
            }
            
            if (testHash.equals(targetHash)) {
                found = true;
                foundInput = testInput;
            }
        }
        
        long endTime = System.currentTimeMillis();
        double timeElapsed = (endTime - startTime) / 1000.0;
        
        System.out.println("\n--- PRE-IMAGE RESISTANCE RESULTS ---");
        System.out.println("Attempts made: " + String.format("%,d", attempts));
        System.out.println("Time elapsed: " + String.format("%.2f", timeElapsed) + " seconds");
        System.out.println("Rate: " + String.format("%,.0f", attempts / timeElapsed) + " hashes/second");
        
        if (found) {
            System.out.println("PRE-IMAGE FOUND: \"" + foundInput + "\"");
            System.out.println("   This is extremely unlikely and suggests a problem!");
        } else {
            System.out.println("NO PRE-IMAGE FOUND");
            System.out.println("   This demonstrates strong pre-image resistance");
        }
    }
    
    /**
     * Demonstrates second pre-image resistance
     */
    private static void testSecondPreImageResistance(String userInput) throws NoSuchAlgorithmException {
        // Use a known input
        String targetHash = computeHash(userInput);

        System.out.println("Original input: \"" + userInput + "\"");
        System.out.println("Target hash: " + targetHash);
        System.out.println();
        
        System.out.println("Attempting to find second pre-image...");
        System.out.println("Looking for different input that produces the same hash:");
        
        long startTime = System.currentTimeMillis();
        int attempts = 0;
        boolean found = false;
        String foundInput = null;
        
        while (attempts < MAX_ITERATIONS && !found && 
               (System.currentTimeMillis() - startTime) < TIME_LIMIT_SECONDS * 1000) {
            
            // Generate different input string
            String testInput = generateSequentialString(attempts);
            
            // Skip if same as original
            if (testInput.equals(userInput)) {
                continue;
            }
            
            String testHash = computeHash(testInput);
            attempts++;
            
            // Show progress every 50,000 attempts
            if (attempts % 50000 == 0) {
                System.out.println("  Attempt " + attempts + ": \"" + testInput + "\" → " + 
                                 testHash.substring(0, 16) + "...");
            }
            
            if (testHash.equals(targetHash)) {
                found = true;
                foundInput = testInput;
            }
        }
        
        long endTime = System.currentTimeMillis();
        double timeElapsed = (endTime - startTime) / 1000.0;
        
        System.out.println("\n--- SECOND PRE-IMAGE RESISTANCE RESULTS ---");
        System.out.println("Original input: \"" + userInput + "\"");
        System.out.println("Attempts made: " + String.format("%,d", attempts));
        System.out.println("Time elapsed: " + String.format("%.2f", timeElapsed) + " seconds");
        System.out.println("Rate: " + String.format("%,.0f", attempts / timeElapsed) + " hashes/second");
        
        if (found) {
            System.out.println("SECOND PRE-IMAGE FOUND: \"" + foundInput + "\"");
            System.out.println("   Both \"" + userInput + "\" and \"" + foundInput + "\" hash to:");
            System.out.println("   " + targetHash);
        } else {
            System.out.println("NO SECOND PRE-IMAGE FOUND");
            System.out.println("   This demonstrates strong second pre-image resistance");
        }
    }
    
    /**
     * Demonstrates collision resistance using birthday paradox approach
     */
    private static void testCollisionResistance(String userInput) throws NoSuchAlgorithmException {
        System.out.println("Using birthday paradox approach to find collisions...");
        System.out.println("Storing hashes and looking for duplicates:");
        
        Map<String, String> hashToInput = new HashMap<>();
        long startTime = System.currentTimeMillis();
        int attempts = 0;
        boolean collisionFound = false;
        String input1 = userInput, input2 = null, collisionHash = null;
        
        while (attempts < MAX_ITERATIONS && !collisionFound && 
               (System.currentTimeMillis() - startTime) < TIME_LIMIT_SECONDS * 1000) {
            
            // Generate input string
            String testInput = generateSequentialString(attempts);
            String testHash = computeHash(testInput);
            attempts++;
            
            // Show progress every 50,000 attempts
            if (attempts % 50000 == 0) {
                System.out.println("  Attempt " + attempts + ": \"" + testInput + "\" → " + 
                                 testHash.substring(0, 16) + "...");
            }
            
            // Check for collision
            if (hashToInput.containsKey(testHash)) {
                collisionFound = true;
                input2 = testInput;
                collisionHash = testHash;
            } else {
                hashToInput.put(testHash, testInput);
            }
        }
        
        long endTime = System.currentTimeMillis();
        double timeElapsed = (endTime - startTime) / 1000.0;
        
        System.out.println("\n--- COLLISION RESISTANCE RESULTS ---");
        System.out.println("Attempts made: " + String.format("%,d", attempts));
        System.out.println("Unique hashes stored: " + String.format("%,d", hashToInput.size()));
        System.out.println("Time elapsed: " + String.format("%.2f", timeElapsed) + " seconds");
        System.out.println("Rate: " + String.format("%,.0f", attempts / timeElapsed) + " hashes/second");
        
        if (collisionFound) {
            System.out.println("COLLISION FOUND!");
            System.out.println("   Input 1: \"" + input1 + "\"");
            System.out.println("   Input 2: \"" + input2 + "\"");
            System.out.println("   Same hash: " + collisionHash);
            System.out.println("   This is extremely unlikely for SHA-256!");
        } else {
            System.out.println("NO COLLISIONS FOUND");
            System.out.println("   This demonstrates strong collision resistance");
        }
        
        // Birthday paradox analysis
        System.out.println("\nBirthday Paradox Analysis:");
        System.out.println("For SHA-256 (256-bit output):");
        System.out.println("Expected attempts for 50% collision probability: 2^128 ≈ 3.4 × 10^38");
        System.out.println("Our attempts: " + String.format("%,d", attempts));
        System.out.println("Probability of finding collision: ~0% (negligible)");
    }
    
    /**
     * Generates a sequential string based on attempt number using all ASCII
     * characters
     * Pattern: covers ASCII 32-126 (printable characters including space, symbols,
     * letters, numbers)
     * Order: " ", "!", "\"", "#", ..., "~", " ", " !", etc.
     */
    private static String generateSequentialString(int index) {
      if (index < 0)
        return " "; // Start with space (ASCII 32)

      // Define ASCII printable character range (32-126 = 95 characters)
      int ASCII_START = 32; // Space character
      int ASCII_END = 126; // Tilde character ~
      int ASCII_RANGE = ASCII_END - ASCII_START + 1; // 95 characters

      StringBuilder result = new StringBuilder();

      // For single characters (first 95 attempts)
      if (index < ASCII_RANGE) {
        return String.valueOf((char) (ASCII_START + index));
      }

      // For multi-character strings, use base-95 representation
      int remaining = index - ASCII_RANGE; // Offset for single char strings
      int length = 2; // Start with 2-character strings
      long rangeSize = (long) ASCII_RANGE * ASCII_RANGE; // 95^2 for 2-char strings

      // Find the appropriate string length
      while (remaining >= rangeSize) {
        remaining -= rangeSize;
        length++;
        rangeSize *= ASCII_RANGE;
      }

      // Convert to base-95 representation
      for (int i = 0; i < length; i++) {
        result.insert(0, (char) (ASCII_START + (remaining % ASCII_RANGE)));
        remaining /= ASCII_RANGE;
      }

      return result.toString();
    }
    
    /**
     * Computes SHA-256 hash of input string
     */
    private static String computeHash(String input) throws NoSuchAlgorithmException {
        byte[] hashBytes = digest.digest(input.getBytes());
        
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
}
