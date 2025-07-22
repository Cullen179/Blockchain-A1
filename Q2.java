import java.util.List;

import utils.ShaHash;

import java.util.ArrayList;

public class Q2 {

  public static void main(String[] args) {
    // Example usage with transaction IDs
    List<String> transactionIds = new ArrayList<>();
    transactionIds.add("tx1");
    transactionIds.add("tx2");
    transactionIds.add("tx3");
    transactionIds.add("tx4");
    transactionIds.add("tx5"); // Odd number to test handling

    String merkleRoot = buildMerkleTree(transactionIds);
    System.out.println("Merkle Root: " + merkleRoot);

    // Test with different sizes
    System.out.println("\nTesting with different input sizes:");
    testMerkleTree(List.of("single"));
    testMerkleTree(List.of("tx1", "tx2"));
    testMerkleTree(List.of("tx1", "tx2", "tx3", "tx4", "tx5", "tx6"));
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
      printTreeWithConnections(dataItems, allLevels);
      printFolderTreeStructure(dataItems, allLevels);
  
      return currentLevel.get(0); // This is the Merkle root
    }

  /**
   * Builds the next level of the Merkle Tree from the current level
   * Handles odd number of nodes by duplicating the last node
   */
  public static List<String> buildNextLevel(List<String> currentLevel) {
    List<String> nextLevel = new ArrayList<>();

    for (int i = 0; i < currentLevel.size(); i += 2) {
      String leftChild = currentLevel.get(i);
      String rightChild;

      // Handle odd number of nodes - duplicate the last node
      if (i + 1 < currentLevel.size()) {
        rightChild = currentLevel.get(i + 1);
      } else {
        rightChild = leftChild; // Duplicate the last node
      }

      // Combine and hash the two children
      String combinedHash = ShaHash.sha256Hash(leftChild + rightChild);
      nextLevel.add(combinedHash);
    }

    return nextLevel;
  }

  /**
   * Prints the structure of the Merkle Tree
   */
  public static void printTreeStructure(List<String> originalData, List<List<String>> allLevels) {
    System.out.println("\n=== Merkle Tree Structure ===");
    
    for (int level = 0; level < allLevels.size(); level++) {
      System.out.println("Level " + level + ":");
      List<String> currentLevel = allLevels.get(level);
      
      for (int i = 0; i < currentLevel.size(); i++) {
        String hash = currentLevel.get(i);
        String shortHash = hash.substring(0, Math.min(8, hash.length())) + "...";
        
        if (level == 0) {
          // For leaf nodes, show original data
          System.out.println("  [" + i + "] " + originalData.get(i) + " -> " + shortHash);
        } else {
          System.out.println("  [" + i + "] " + shortHash);
        }
      }
      System.out.println();
    }
    
    System.out.println("Root: " + allLevels.get(allLevels.size() - 1).get(0));
    System.out.println("===============================\n");
  }

  /**
   * Prints the tree structure with parent-child connections
   */
  public static void printTreeWithConnections(List<String> originalData, List<List<String>> allLevels) {
    System.out.println("\n=== Merkle Tree with Parent-Child Connections ===");
    
    // Print from top (root) to bottom (leaves)
    for (int level = allLevels.size() - 1; level >= 0; level--) {
      List<String> currentLevel = allLevels.get(level);
      
      // Print level header
      if (level == allLevels.size() - 1) {
        System.out.println("ROOT (Level " + level + "):");
      } else if (level == 0) {
        System.out.println("LEAVES (Level " + level + "):");
      } else {
        System.out.println("Level " + level + ":");
      }
      
      // Print nodes at current level
      for (int i = 0; i < currentLevel.size(); i++) {
        String hash = currentLevel.get(i);
        String shortHash = hash.substring(0, Math.min(8, hash.length())) + "...";
        
        if (level == 0) {
          // For leaf nodes, show original data
          System.out.println("  Node[" + i + "]: " + originalData.get(i) + " -> " + shortHash);
        } else {
          System.out.println("  Node[" + i + "]: " + shortHash);
        }
        
        // Show children if not at leaf level
        if (level > 0) {
          List<String> childLevel = allLevels.get(level - 1);
          int leftChildIndex = i * 2;
          int rightChildIndex = i * 2 + 1;
          
          String leftChildHash = "";
          String rightChildHash = "";
          
          if (leftChildIndex < childLevel.size()) {
            leftChildHash = childLevel.get(leftChildIndex).substring(0, Math.min(8, childLevel.get(leftChildIndex).length())) + "...";
          }
          if (rightChildIndex < childLevel.size()) {
            rightChildHash = childLevel.get(rightChildIndex).substring(0, Math.min(8, childLevel.get(rightChildIndex).length())) + "...";
          } else {
            // Handle case where right child is duplicated
            rightChildHash = leftChildHash + " (duplicated)";
          }
          
          System.out.println("    ├── Left Child[" + leftChildIndex + "]: " + leftChildHash);
          System.out.println("    └── Right Child[" + rightChildIndex + "]: " + rightChildHash);
          System.out.println();
        }
      }
      
      if (level > 0) {
        System.out.println("    |");
        System.out.println("    ▼");
      }
    }
    
    System.out.println("================================================\n");
  }

  /**
   * Prints the Merkle Tree in a folder-like tree structure
   */
  public static void printFolderTreeStructure(List<String> originalData, List<List<String>> allLevels) {
    System.out.println("\n=== Merkle Tree (Folder Structure) ===");
    
    // Start from root level
    int rootLevel = allLevels.size() - 1;
    String rootHash = allLevels.get(rootLevel).get(0);
    String rootShort = rootHash.substring(0, Math.min(8, rootHash.length())) + "...";
    
    System.out.println("📁 Merkle Root: " + rootShort);
    
    // Recursively print the tree structure
    if (allLevels.size() > 1) {
      printFolderNode(allLevels, originalData, rootLevel, 0, "");
    }
    
    System.out.println("=======================================\n");
  }

  /**
   * Helper method to recursively print nodes in folder tree format
   */
  private static void printFolderNode(List<List<String>> allLevels, List<String> originalData, 
                                     int level, int nodeIndex, String prefix) {
    if (level <= 0) {
      return;
    }
    
    List<String> childLevel = allLevels.get(level - 1);
    int leftChildIndex = nodeIndex * 2;
    int rightChildIndex = nodeIndex * 2 + 1;
    
    // Determine if we need different prefixes for last child
    boolean hasRightChild = rightChildIndex < childLevel.size();
    boolean isRightChildDuplicate = false;
    
    // Print left child
    if (leftChildIndex < childLevel.size()) {
      String leftHash = childLevel.get(leftChildIndex);
      String leftShort = leftHash.substring(0, Math.min(8, leftHash.length())) + "...";
      
      if (level == 1) {
        // Leaf node - show original data
        String originalValue = originalData.get(leftChildIndex);
        if (hasRightChild) {
          System.out.println(prefix + "├── 📄 " + originalValue + " (" + leftShort + ")");
        } else {
          System.out.println(prefix + "└── 📄 " + originalValue + " (" + leftShort + ")");
        }
      } else {
        // Internal node
        if (hasRightChild) {
          System.out.println(prefix + "├── 📁 Node[" + leftChildIndex + "]: " + leftShort);
          printFolderNode(allLevels, originalData, level - 1, leftChildIndex, prefix + "│   ");
        } else {
          System.out.println(prefix + "└── 📁 Node[" + leftChildIndex + "]: " + leftShort);
          printFolderNode(allLevels, originalData, level - 1, leftChildIndex, prefix + "    ");
        }
      }
    }
    
    // Print right child
    if (hasRightChild) {
      String rightHash = childLevel.get(rightChildIndex);
      String rightShort = rightHash.substring(0, Math.min(8, rightHash.length())) + "...";
      
      // Check if right child is a duplicate of left child
      if (leftChildIndex < childLevel.size() && 
          rightHash.equals(childLevel.get(leftChildIndex))) {
        isRightChildDuplicate = true;
      }
      
      if (level == 1) {
        // Leaf node - show original data
        String originalValue = originalData.get(rightChildIndex);
        String duplicateLabel = isRightChildDuplicate ? " (duplicated)" : "";
        System.out.println(prefix + "└── 📄 " + originalValue + duplicateLabel + " (" + rightShort + ")");
      } else {
        // Internal node
        String duplicateLabel = isRightChildDuplicate ? " (duplicated)" : "";
        System.out.println(prefix + "└── 📁 Node[" + rightChildIndex + "]" + duplicateLabel + ": " + rightShort);
        printFolderNode(allLevels, originalData, level - 1, rightChildIndex, prefix + "    ");
      }
    }
  }

  /**
   * Helper method to test the Merkle Tree implementation
   */
  public static void testMerkleTree(List<String> data) {
    System.out.println("Input: " + data);
    System.out.println("Merkle Root: " + buildMerkleTree(data));
    System.out.println();
  }
}