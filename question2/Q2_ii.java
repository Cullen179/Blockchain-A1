package question2;

import java.util.ArrayList;
import java.util.List;

import utils.ShaHash;

public class Q2_ii {
  /**
   * Generates a Merkle proof for a specific data item
   * 
   * @param dataItems  Original list of data items
   * @param targetItem The specific item to generate proof for
   * @return List of ProofNode objects representing the proof path
   */
  public static List<ProofNode> generateMerkleProof(List<String> dataItems, String targetItem) {
    if (dataItems == null || dataItems.isEmpty()) {
      throw new IllegalArgumentException("Data items cannot be null or empty");
    }

    // Find the index of the target item
    int targetIndex = dataItems.indexOf(targetItem);
    if (targetIndex == -1) {
      throw new IllegalArgumentException("Target item '" + targetItem + "' not found in data items");
    }

    List<ProofNode> proof = new ArrayList<>();

    // Create initial leaf nodes by hashing each data item
    List<String> currentLevel = new ArrayList<>();
    for (String data : dataItems) {
      currentLevel.add(ShaHash.sha256Hash(data));
    }

    int currentIndex = targetIndex;

    // Build proof path by moving up the tree
    while (currentLevel.size() > 1) {
      List<String> nextLevel = new ArrayList<>();

      for (int i = 0; i < currentLevel.size(); i += 2) {
        String leftHash = currentLevel.get(i);
        String rightHash;

        // Handle odd number of nodes (duplicate the last node)
        if (i + 1 < currentLevel.size()) {
          rightHash = currentLevel.get(i + 1);
        } else {
          rightHash = leftHash; // Duplicate for odd number
        }

        // If current index is at this pair, collect sibling for proof
        if (i == (currentIndex / 2) * 2) {
          if (currentIndex % 2 == 0) {
            // Current node is left child, sibling is right
            if (i + 1 < currentLevel.size()) {
              proof.add(new ProofNode(rightHash, false)); // Right sibling
            } else {
              proof.add(new ProofNode(leftHash, false)); // Right sibling is duplicate
            }
          } else {
            // Current node is right child, sibling is left
            proof.add(new ProofNode(leftHash, true)); // Left sibling
          }
        }

        // Combine hashes for next level
        nextLevel.add(ShaHash.sha256Hash(leftHash + rightHash));
      }

      currentLevel = nextLevel;
      currentIndex = currentIndex / 2;
    }

    return proof;
  }

  /**
   * Displays a Merkle proof in a readable format
   * 
   * @param targetItem The item being proved
   * @param proof      The proof path
   */
  public static void displayMerkleProof(String targetItem, List<ProofNode> proof) {
    System.out.println("\n=== Merkle Proof for '" + targetItem + "' ===");
    System.out.println("Leaf hash: " + ShaHash.getShortHash(ShaHash.sha256Hash(targetItem)));
    System.out.println("Proof path (" + proof.size() + " steps):");

    for (int i = 0; i < proof.size(); i++) {
      ProofNode node = proof.get(i);
      String position = node.isLeft() ? "LEFT" : "RIGHT";
      String hashPreview = ShaHash.getShortHash(node.getHash());
      System.out.println("  Step " + (i + 1) + ": " + hashPreview + " (" + position + ")");
    }

    System.out.println("-".repeat(30) + "\n");
  }

  /**
   * Test method to demonstrate Merkle proof generation
   */
  public static void main(String[] args) {
    System.out.println("\n=== Testing Merkle Proof Generation ===");

    // Test with different data sets
    List<String> testData1 = List.of("tx1", "tx2", "tx3", "tx4");
    List<String> testData2 = List.of("tx1", "tx2", "tx3", "tx4", "tx5");

    testMerkleProof(testData1, "tx3");
    testMerkleProof(testData2, "tx5");
  }

  public static void testMerkleProof(List<String> dataItems, String targetItem) {
    System.out.println("\n\n\nTesting Merkle Proof for item: " + targetItem);

    // Build the Merkle tree to get the root
    String merkleRoot = Q2_i.buildMerkleTree(dataItems);

    // Generate the proof for the target item
    List<ProofNode> proof = generateMerkleProof(dataItems, targetItem);

    // Display the proof
    displayMerkleProof(targetItem, proof);

    // Verify the proof
    boolean isValid = Q2_iii.verifyMerkleProof(targetItem, proof, merkleRoot);
    System.out.println("Proof verification: " + (isValid ? "VALID" : "INVALID"));

    System.out.println("=".repeat(30));
  }

  /**
   * Class representing a single node in the Merkle proof path
   */
  static class ProofNode {
    private final String hash;
    private final boolean isLeft;

    public ProofNode(String hash, boolean isLeft) {
      this.hash = hash;
      this.isLeft = isLeft;
    }

    public String getHash() {
      return hash;
    }

    public boolean isLeft() {
      return isLeft;
    }

    @Override
    public String toString() {
      return "ProofNode{hash=" + ShaHash.getShortHash(hash) + ", position=" + (isLeft ? "LEFT" : "RIGHT") + "}";
    }
  }
}
