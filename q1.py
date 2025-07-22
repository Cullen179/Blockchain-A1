import hashlib
import random
import string
import time

def hashStr(input_string):
    """
    Hash a string using SHA-256 algorithm
    
    Args:
        input_string (string): Input string to be hashed
        
    Returns:
        string: Hexadecimal representation of the hash
    """
    # Create a SHA-256 hash object
    hash_object = hashlib.sha256()
    
    # Update the hash object with the string (encoded to bytes)
    hash_object.update(input_string.encode('utf-8'))
    
    # Return the hexadecimal representation of the hash
    return hash_object.hexdigest()

def calculate_hamming_distance(hash1, hash2):
    """
    Calculate the Hamming distance between two hash strings
    
    Args:
        hash1 (string): First hash
        hash2 (string): Second hash
        
    Returns:
        int: Number of different characters
    """
    if len(hash1) != len(hash2):
        return -1
    
    return sum(c1 != c2 for c1, c2 in zip(hash1, hash2))

def generate_random_string(length=8):
    """
    Generate a random string of specified length
    
    Args:
        length (int): Length of the random string
        
    Returns:
        string: Random string containing letters and digits
    """
    characters = string.ascii_letters + string.digits
    return ''.join(random.choice(characters) for _ in range(length))

def demonstrate_preimage_attack():
    """
    Demonstrates the difficulty of finding a pre-image for a given hash
    """
    print("=== PRE-IMAGE ATTACK DEMONSTRATION ===\n")
    
    # Target string and its hash (the "secret" we're trying to find)
    target_string = "Secret123"
    target_hash = hashStr(target_string)
    
    print(f"Target string: '{target_string}' (this is what we're trying to find)")
    print(f"Target hash:   {target_hash}")
    print("\nAttempting to find a pre-image (input that produces this hash)...")
    print("This demonstrates why hash functions are considered one-way functions.\n")
    
    # Attack parameters
    max_attempts = 1000000  # Maximum number of attempts
    max_time = 30  # Maximum time in seconds
    attempt_count = 0
    start_time = time.time()
    found = False
    best_match_distance = float('inf')
    best_match_string = ""
    best_match_hash = ""
    
    print("Starting brute force search...")
    print("Press Ctrl+C to stop early if needed.\n")
    
    try:
        while attempt_count < max_attempts and (time.time() - start_time) < max_time:
            # Generate random candidate string
            candidate = generate_random_string(len(target_string))
            candidate_hash = hashStr(candidate)
            attempt_count += 1
            
            # Check if we found the exact match
            if candidate_hash == target_hash:
                found = True
                print(f"🎉 INCREDIBLE! Pre-image found after {attempt_count} attempts!")
                print(f"Found string: '{candidate}'")
                print(f"Time taken: {time.time() - start_time:.2f} seconds")
                break
            
            # Track the closest match for demonstration
            distance = calculate_hamming_distance(target_hash, candidate_hash)
            if distance < best_match_distance:
                best_match_distance = distance
                best_match_string = candidate
                best_match_hash = candidate_hash
            
            # Progress reporting
            if attempt_count % 100000 == 0:
                elapsed = time.time() - start_time
                rate = attempt_count / elapsed
                print(f"Attempts: {attempt_count:,} | Rate: {rate:.0f} hashes/sec | Best match: {best_match_distance} differences")
    
    except KeyboardInterrupt:
        print("\nStopped by user.")
    
    # Final results
    elapsed_time = time.time() - start_time
    hash_rate = attempt_count / elapsed_time if elapsed_time > 0 else 0
    
    print("\n" + "=" * 70)
    print("RESULTS:")
    print("=" * 70)
    
    if found:
        print("✅ Pre-image FOUND! (This is extremely unlikely!)")
    else:
        print("❌ Pre-image NOT found (as expected)")
    
    print(f"Total attempts made: {attempt_count:,}")
    print(f"Time elapsed: {elapsed_time:.2f} seconds")
    print(f"Hash rate: {hash_rate:.0f} hashes per second")
    print(f"Probability of success: {(attempt_count / (2**256)):.2e}")
    
    print(f"\nBest match found:")
    print(f"  String: '{best_match_string}'")
    print(f"  Hash:   {best_match_hash}")
    print(f"  Hamming distance: {best_match_distance} out of 64 characters")
    print(f"  Similarity: {((64-best_match_distance)/64*100):.1f}%")
    
    # Educational explanation
    print(f"\n" + "=" * 70)
    print("EDUCATIONAL NOTES:")
    print("=" * 70)
    print(f"• SHA-256 has 2^256 ≈ 1.16 × 10^77 possible outputs")
    print(f"• Finding a specific pre-image requires on average 2^255 attempts")
    print(f"• At {hash_rate:.0f} hashes/sec, it would take approximately:")
    if hash_rate > 0:
        years_needed = (2**255) / hash_rate / (365.25 * 24 * 3600)
        print(f"  {years_needed:.2e} years to find a pre-image")
    print(f"• This demonstrates why hash functions are considered 'one-way'")

def demonstrate_avalanche_effect():
    """
    Demonstrates the avalanche effect by showing how small input changes
    result in dramatically different hashes
    """
    print("=== AVALANCHE EFFECT DEMONSTRATION ===\n")
    
    # Original input
    original = "Hello, Blockchain!"
    original_hash = hashStr(original)
    
    # Test cases with minimal changes
    test_cases = [
        ("Hello, Blockchain.", "Changed exclamation to period"),
        ("hello, Blockchain!", "Changed 'H' to lowercase 'h'"),
        ("Hello, Blockchain! ", "Added one space at the end"),
        ("Hello, Blockchain!a", "Added character 'a'"),
        ("Hallo, Blockchain!", "Changed 'e' to 'a'"),
    ]
    
    print(f"Original string: '{original}'")
    print(f"Original hash:   {original_hash}")
    print("=" * 80)
    
    for modified_string, description in test_cases:
        modified_hash = hashStr(modified_string)
        hamming_dist = calculate_hamming_distance(original_hash, modified_hash)
        
        print(f"\nModification: {description}")
        print(f"Modified string: '{modified_string}'")
        print(f"Modified hash:   {modified_hash}")
        print(f"Hamming distance: {hamming_dist} out of {len(original_hash)} characters")
        print(f"Percentage different: {(hamming_dist/len(original_hash)*100):.1f}%")
        
        # Visual comparison
        print("Visual comparison:")
        print("Original: ", end="")
        for i, (c1, c2) in enumerate(zip(original_hash, modified_hash)):
            if c1 != c2:
                print(f"\033[91m{c1}\033[0m", end="")  # Red for different
            else:
                print(c1, end="")
        print()
        print("Modified: ", end="")
        for i, (c1, c2) in enumerate(zip(original_hash, modified_hash)):
            if c1 != c2:
                print(f"\033[92m{c2}\033[0m", end="")  # Green for different
            else:
                print(c2, end="")
        print()
        print("-" * 80)

if __name__ == "__main__":
    print("Choose demonstration:")
    print("1. Avalanche Effect")
    print("2. Pre-image Attack")
    print("3. Both")
    
    choice = input("\nEnter your choice (1-3): ").strip()
    
    if choice == "1":
        demonstrate_avalanche_effect()
    elif choice == "2":
        demonstrate_preimage_attack()
    elif choice == "3":
        demonstrate_avalanche_effect()
        print("\n" + "="*100 + "\n")
        demonstrate_preimage_attack()
    else:
        print("Invalid choice. Running both demonstrations...")
        demonstrate_avalanche_effect()
        print("\n" + "="*100 + "\n")
        demonstrate_preimage_attack()