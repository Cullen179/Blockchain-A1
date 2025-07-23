package question2;
import java.util.List;
import utils.ShaHash;
import java.util.ArrayList;

public class Q2_i {

  public static void main(String[] args) {

    // // Test with different sizes
    System.out.println("\nTesting with different input sizes:");
    testMerkleTree(List.of("single"));
    testMerkleTree(List.of("tx1", "tx2"));
    testMerkleTree(List.of("tx1", "tx2", "tx3"));
  }

  /**
     * Builds a Merkle Tree from a list of data items and returns the root hash
     * @param dataItems List of strings representing transaction IDs or data
     * @return Merkle root hash as a string
     */
    public static String buildMerkleTree(List<String> dataItems) {
      if (dataItems == null || dataItems.isEmpty()) {
        throw new IllegalArgumentException("Data items cannot be null or empty");
      }
      
      // Create initial leaf nodes by hashing each data item
      List<String> currentLevel = new ArrayList<>();
      for (String data : dataItems) {
        currentLevel.add(ShaHash.sha256Hash(data));
      }
      
      // Build tree level by level until we reach the root
      List<List<String>> allLevels = new ArrayList<>();
      allLevels.add(new ArrayList<>(currentLevel));
      
      while (currentLevel.size() > 1) {
        currentLevel = buildNextLevel(currentLevel);
        allLevels.add(new ArrayList<>(currentLevel));
      }
     
      printTreeStructure(dataItems, allLevels);
  
      return currentLevel.get(0); // Merkle root
    }

  /**
   * Builds the next level of the Merkle Tree from the current level
   * Handles odd number of nodes by duplicating the last node
   */
  public static List<String> buildNextLevel(List<String> currentLevel) {
    List<String> nextLevel = new ArrayList<>();

    for (int i = 0; i < currentLevel.size(); i += 2) {
      String leftChild = currentLevel.get(i);
      
      String rightChild = (i + 1 < currentLevel.size()) ? currentLevel.get(i + 1) : leftChild; // Handle odd nodes

      // Combine and hash the two children
      String combinedHash = ShaHash.sha256Hash(leftChild + rightChild);
      nextLevel.add(combinedHash);
    }

    return nextLevel;
  }

  /**
   * Prints the Merkle Tree in a folder-like tree structure
   */
  public static void printTreeStructure(List<String> originalData, List<List<String>> allLevels) {
    System.out.println("\n=== Merkle Tree ===");
    
    // Start from root level
    int rootLevel = allLevels.size() - 1;
    String rootHash = allLevels.get(rootLevel).get(0);
    
    System.out.println("\nMerkle Root: " + rootHash + "\n");
    // Recursively print the tree structure
    if (allLevels.size() > 1) {
      printNode(allLevels, originalData, rootLevel, 0, "");
    }
    
    System.out.println("\n" + "=".repeat(30));
  }

  /**
   * Helper method to recursively print nodes in folder tree format
   */
  private static void printNode(List<List<String>> allLevels, List<String> originalData,
      int level, int nodeIndex, String prefix) {
    if (level <= 0)
      return;

    List<String> childLevel = allLevels.get(level - 1);
    int leftIndex = nodeIndex * 2;
    int rightIndex = nodeIndex * 2 + 1;
    boolean hasRight = rightIndex < childLevel.size();

    // Print left child
    if (leftIndex < childLevel.size()) {
      String hash = ShaHash.getShortHash(childLevel.get(leftIndex));
      String content = (level == 1) ? originalData.get(leftIndex) : "Node" + leftIndex;

      System.out.println(prefix + "├──── " + content + " (" + hash + ")");

      if (level > 1) {
        printNode(allLevels, originalData, level - 1, leftIndex, prefix + (hasRight ? "│     " : "      "));
      }
    }

    // Print right child
    if (hasRight) {
      String hash = ShaHash.getShortHash(childLevel.get(rightIndex));
      boolean isDuplicate = leftIndex < childLevel.size() &&
          childLevel.get(rightIndex).equals(childLevel.get(leftIndex));
      String dupLabel = isDuplicate ? " (dup)" : "";
      String content = (level == 1) ? originalData.get(rightIndex) : "Node" + rightIndex;

      System.out.println(prefix + "└──── " + content + dupLabel + " (" + hash + ")");

      if (level > 1) {
        printNode(allLevels, originalData, level - 1, rightIndex, prefix + "      ");
      }
    }
  }

  /**
   * Helper method to test the Merkle Tree implementation
   */
  public static void testMerkleTree(List<String> data) {
    System.out.println("Input: " + data);
    buildMerkleTree(data);
    System.out.println();
  }
}