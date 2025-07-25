# Blockchain Assignment A1

This repository contains implementations demonstrating fundamental blockchain and cryptographic concepts including hash functions, digital signatures, and security properties.
## 👨‍💻 Author

**Student**: Do Tung Lam - s3963286  
**Course**: Blockchain Technology  

## 📋 Requirements

### System Requirements
- **Java Development Kit (JDK) 8 or higher**
- **Operating System**: Windows, macOS, or Linux
- **Memory**: Minimum 4GB RAM (8GB recommended for performance tests)
- **Storage**: 100MB free space

### Java Libraries Used
- `java.security.MessageDigest` - For SHA-256 hash functions
- `java.security.KeyPairGenerator` - For RSA key generation
- `java.security.Signature` - For digital signature operations
- `java.util.Scanner` - For user input
- `java.util.HashMap` - For collision detection storage

### Development Environment
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code) or command line
- No external dependencies required - uses only standard Java libraries

## 🚀 How to Run the Code

### Prerequisites
1. Ensure Java is installed and configured:
   ```bash
   java --version
   javac --version
   ```

2. Clone or download this repository:
   ```bash
   git clone https://github.com/Cullen179/Blockchain-A1.git
   cd Blockchain-A1
   ```

### Running Each Question

#### Question 1: Hash Functions and Avalanche Effect

**Q1_i.java - Avalanche Effect Demonstration**
```bash
# Compile
javac -d . question1/Q1_i.java

# Run
java question1.Q1_i
```
**What it does:**
- Takes user input string
- Computes SHA-256 hash
- Demonstrates avalanche effect with minimal changes
- Shows visual comparison of hash differences
- Analyzes bit-level changes across positions 0-103

**Sample Input:** `Hello World`

---

**Q1_ii.java - Hash Function Security Properties**
```bash
# Compile
javac -d . question1/Q1_ii.java

# Run
java question1.Q1_ii
```
**What it does:**
- Tests pre-image resistance
- Tests second pre-image resistance  
- Tests collision resistance
- Analyzes input vulnerability to sequential attacks
- Provides security recommendations

**Sample Input:** `Blockchain`

---

#### Question 2: Merkle Trees (if implemented)

**Q2_i.java - Basic Merkle Tree**
```bash
# Compile
javac -d . question2/Q2_i.java

# Run
java question2.Q2_i
```

**Q2_ii.java - Merkle Tree Verification**
```bash
# Compile
javac -d . question2/Q2_ii.java

# Run
java question2.Q2_ii
```

**Q2_iii.java - Merkle Tree Efficiency**
```bash
# Compile
javac -d . question2/Q2_iii.java

# Run
java question2.Q2_iii
```

---

#### Question 3: Digital Signatures

**Q3.java - Digital Signature System**
```bash
# Compile
javac -d . question3/Q3.java utils/ShaHash.java

# Run
java question3.Q3
```
**What it does:**
- Generates RSA-2048 key pairs
- Signs user messages with SHA256withRSA
- Verifies signature authenticity
- Tests tampering detection
- Tests authenticity verification
- Demonstrates key pair isolation
- Performance analysis

**Sample Input:** `This is my digital signature test`

## 📊 Expected Output Examples

### Question 1i - Avalanche Effect
```
Enter a string to hash: Hello

=== ORIGINAL HASH ===
Input: "Hello"
SHA-256 Hash: 2cf24dba4f21d4288f4cb7b85c0e7c1d5c96e6a6e9c04a86eefd6c4f8c2c2c21

=== AVALANCHE EFFECT DEMONSTRATION ===
1. Single Character Change:
   Original: "Hello"
   Modified: "Xello"
   
   Original Hash: 2cf24dba4f21d4288f4cb7b85c0e7c1d5c96e6a6e9c04a86eefd6c4f8c2c2c21
                  |    |   |     |  |     |    |     |     |  |      |  |      |
   Modified Hash: a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e
   Differences: 58/64 characters (90.6%)
```

### Question 1ii - Security Properties
```
Enter a string to test: hello

=== USER INPUT ANALYSIS ===
Input: "hello"
⚠️  VULNERABILITY WARNING:
   Sequential position: 372,946
   This input will likely be found during sequential search!

1. PRE-IMAGE RESISTANCE TEST
Attempts made: 1,000,000
Time elapsed: 30.00 seconds
Rate: 33,333 hashes/second
✅ NO PRE-IMAGE FOUND
```

### Question 3 - Digital Signatures
```
=== Digital Signature System ===

1. Generating RSA Key Pair...
2. Signing the message...
3. Verifying the signature...

=== VERIFICATION RESULT ===
Signature Valid: YES

4. Testing tampering detection...
   Text added: "Hello (modified)" → Valid: ✅ NO
   Char changed: "Hbllo" → Valid: ✅ NO

5. Testing authenticity verification...
   Original signature → Authentic: ✅ YES
   Imposter signature → Authentic: ✅ NO
```

## 🛠 Troubleshooting

### Common Issues

**1. "java: command not found"**
- Install Java JDK 8 or higher
- Add Java to your system PATH

**2. "Exception in thread NoSuchAlgorithmException"**
- Ensure you're using a standard Java installation
- SHA-256 should be available in all modern Java versions

**3. "OutOfMemoryError" during collision tests**
- Increase JVM heap size: `java -Xmx2g question1.Q1_ii`
- Reduce MAX_ITERATIONS in source code

**4. Compilation errors**
- Ensure all files are in correct package directories
- Compile dependencies first (utils/ShaHash.java)

### Performance Notes

- **Hash operations**: ~30,000-50,000 per second typical
- **RSA key generation**: 2-5 seconds for 2048-bit keys  
- **Digital signatures**: ~1,000-5,000 per second
- **Large tests**: May take 30 seconds to complete

## 📚 Educational Objectives

This assignment demonstrates:

1. **Cryptographic Hash Properties**
   - Avalanche effect sensitivity
   - Pre-image resistance
   - Collision resistance
   - Security analysis

2. **Digital Signature Security**
   - RSA cryptography
   - Message authentication
   - Integrity verification
   - Non-repudiation

3. **Practical Cryptography**
   - Real-world attack scenarios
   - Performance considerations
   - Security recommendations

## 🔒 Security Considerations

- **Input Length**: Use 4+ character inputs for security tests
- **Random vs Sequential**: Random inputs provide better security
- **Key Management**: Private keys should be stored securely
- **Performance**: Cryptographic operations are computationally intensive

## 📄 File Structure

```
Blockchain-A1/
├── README.md
├── A1.pdf                    # Assignment requirements
├── question1/
│   ├── Q1_i.java            # Avalanche effect demo
│   └── Q1_ii.java           # Hash security properties
├── question2/               # Merkle tree implementations
│   ├── Q2_i.java
│   ├── Q2_ii.java
│   └── Q2_iii.java
├── question3/
│   └── Q3.java              # Digital signature system
└── utils/
    ├── ShaHash.java         # Hash utility class
    └── ShaHash.class
```

