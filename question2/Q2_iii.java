package question2;

import java.util.List;

import question2.Q2_ii.ProofNode;
import utils.ShaHash;

public class Q2_iii {
  /**
   * Verifies a Merkle proof against a known root
   * 
   * @param targetItem   The original data item
   * @param proof        List of ProofNode objects
   * @param expectedRoot The expected Merkle root
   * @return true if proof is valid, false otherwise
   */
  public static boolean verifyMerkleProof(String targetItem, List<ProofNode> proof, String expectedRoot) {
    String currentHash = ShaHash.sha256Hash(targetItem);

    // Walk up the tree using the proof
    for (ProofNode proofNode : proof) {
      if (proofNode.isLeft()) {
        // Sibling is on the left, current hash goes on the right
        currentHash = ShaHash.sha256Hash(proofNode.getHash() + currentHash);
      } else {
        // Sibling is on the right, current hash goes on the left
        currentHash = ShaHash.sha256Hash(currentHash + proofNode.getHash());
      }
    }

    System.out.println("=== Verifying Merkle Proof ===\n");
    System.out.println("Expected Merkle Root: " + expectedRoot);
    System.out.println("Calculated Merkle Root: " + currentHash);
    return currentHash.equals(expectedRoot);
  }
}
