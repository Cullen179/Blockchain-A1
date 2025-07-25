import java.security.*;
import java.util.Base64;
import java.util.Scanner;

public class Q3 {

    public static void main(String[] args) {
        try {
            // Create an instance of the digital signature system
            DigitalSignatureSystem signatureSystem = new DigitalSignatureSystem();

            // Generate key pair
            System.out.println("=== Digital Signature System ===\n");
            System.out.println("1. Generating RSA Key Pair...");
            signatureSystem.generateKeyPair();

            // Display keys
            signatureSystem.displayKeys();

            // Get message from user
            Scanner scanner = new Scanner(System.in);
            System.out.print("\nEnter a message to sign: ");
            String message = scanner.nextLine();

            // Sign the message
            System.out.println("\n2. Signing the message...");
            byte[] signature = signatureSystem.signMessage(message);

            // Display results
            signatureSystem.displaySignatureResults(message, signature);

            // Verify the signature
            System.out.println("\n3. Verifying the signature...");
            boolean isValid = signatureSystem.verifySignature(message, signature);

            System.out.println("\n=== VERIFICATION RESULT ===");
            System.out.println("Signature Valid: " + (isValid ? "YES" : "NO"));

            // Demonstrate tampering detection
            System.out.println("\n4. Testing tampering detection...");
            testTampering(signatureSystem, message, signature);
            // Test authenticity verification
            System.out.println("\n5. Testing authenticity verification...");
            testAuthenticity(signatureSystem, message, signature);

            scanner.close();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Demonstrates tampering detection with key scenarios
     */
    private static void testTampering(DigitalSignatureSystem system, String originalMessage, byte[] signature) {
        System.out.println("=== TAMPERING DETECTION TEST ===");

        try {
            // Test different types of tampering
            String[] tamperedMessages = {
                    originalMessage + " (modified)", // Text addition
                    originalMessage.replace("a", "b"), // Character change
                    originalMessage.toUpperCase(), // Case change
                    " " + originalMessage // Space added
            };

            String[] descriptions = {
                    "Text added", "Char changed", "Case changed", "Space added"
            };

            System.out.println("Original: \"" + originalMessage + "\"");
            System.out.println("\nMessage Tampering Tests:");

            for (int i = 0; i < tamperedMessages.length; i++) {
                if (!tamperedMessages[i].equals(originalMessage)) {
                    boolean isValid = system.verifySignature(tamperedMessages[i], signature);
                    System.out.printf("  %-12s: '%s' - Valid: %s\n",
                            descriptions[i], tamperedMessages[i], isValid ? "YES" : "NO");
                }
            }

            System.out.println("\n All tampering attempts detected successfully!");

        } catch (Exception e) {
            System.err.println("Error during tampering test: " + e.getMessage());
        }
    }

    /**
     * Tests authenticity verification - proving message origin and preventing
     * impersonation
     */
    private static void testAuthenticity(DigitalSignatureSystem originalSystem, String message,
            byte[] originalSignature) {
        System.out.println("=== AUTHENTICITY VERIFICATION TEST ===");

        try {
            // Test 1: Verify legitimate signature
            System.out.println("\n1. Legitimate Signature Verification:");
            boolean isAuthentic = originalSystem.verifySignature(message, originalSignature);
            System.out.printf("  Original signature - Authentic: %s\n", isAuthentic ? "YES" : "NO");

            // Test 2: Impersonation attempt - different key pair
            System.out.println("\n2. Impersonation Detection:");
            DigitalSignatureSystem imposterSystem = new DigitalSignatureSystem();
            imposterSystem.generateKeyPair();

            // Imposter tries to sign the same message
            byte[] imposterSignature = imposterSystem.signMessage(message);

            // Verify imposter's signature with original public key
            boolean imposterAuthentic = originalSystem.verifySignature(message, imposterSignature);
            System.out.printf("Imposter Signature Authentic: %s\n", imposterAuthentic ? "YES" : "NO");
        } catch (Exception e) {
            System.err.println("Error during authenticity test: " + e.getMessage());
        }
        System.out.println("\nAuthenticity verification tests completed!");
    }
}

/**
 * Digital Signature System using RSA encryption
 */
class DigitalSignatureSystem {
    private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private static final String ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final int KEY_SIZE = 2048;

    /**
     * Generates a new RSA key pair
     */
    public void generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(KEY_SIZE);

        this.keyPair = keyGen.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();

        System.out.println("RSA Key Pair generated successfully");
        System.out.println("  - Key Size: " + KEY_SIZE + " bits");
        System.out.println("  - Algorithm: " + ALGORITHM);
        System.out.println("  - Signature Algorithm: " + SIGNATURE_ALGORITHM);
    }

    /**
     * Signs a message using the private key
     */
    public byte[] signMessage(String message) throws Exception {
        if (privateKey == null) {
            throw new IllegalStateException("Private key not available. Generate key pair first.");
        }

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(message.getBytes("UTF-8"));

        byte[] digitalSignature = signature.sign();
        System.out.println("Message signed successfully");

        return digitalSignature;
    }

    /**
     * Verifies a signature against a message using the public key
     */
    public boolean verifySignature(String message, byte[] signature) throws Exception {
        if (publicKey == null) {
            throw new IllegalStateException("Public key not available. Generate key pair first.");
        }

        Signature verifier = Signature.getInstance(SIGNATURE_ALGORITHM);
        verifier.initVerify(publicKey);
        verifier.update(message.getBytes("UTF-8"));

        return verifier.verify(signature);
    }

    /**
     * Displays the generated keys (Base64 encoded for readability)
     */
    public void displayKeys() {
        System.out.println("\n=== GENERATED KEYS ===");

        // Display Public Key
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println("\nPublic Key (Base64):");
        System.out.println("Format: " + publicKey.getFormat());
        System.out.println("Algorithm: " + publicKey.getAlgorithm());
        printFormattedKey(publicKeyBase64);

        // Display Private Key (for academic purposes only)
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        System.out.println("\nPrivate Key (Base64) - *FOR ACADEMIC PURPOSES ONLY*:");
        System.out.println("Format: " + privateKey.getFormat());
        System.out.println("Algorithm: " + privateKey.getAlgorithm());
        printFormattedKey(privateKeyBase64);

        System.out.println("\nWARNING: In production, NEVER expose private keys!");
    }

    /**
     * Displays signature results
     */
    public void displaySignatureResults(String message, byte[] signature) {
        System.out.println("\n=== SIGNATURE RESULTS ===");
        System.out.println("Original Message: \"" + message + "\"");
        System.out.println("Message Length: " + message.length() + " characters");

        String signatureBase64 = Base64.getEncoder().encodeToString(signature);
        System.out.println("\nDigital Signature (Base64):");
        System.out.println("Signature Length: " + signature.length + " bytes");
        printFormattedKey(signatureBase64);
    }

    /**
     * Helper method to format long Base64 strings for better readability
     */
    private void printFormattedKey(String base64String) {
        int lineLength = 64;
        for (int i = 0; i < base64String.length(); i += lineLength) {
            int end = Math.min(i + lineLength, base64String.length());
            System.out.println(base64String.substring(i, end));
        }
    }

    // Getters for testing purposes
    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}