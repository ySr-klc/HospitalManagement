package Search;

import java.util.*;

/**
 * A Trie data structure that stores words and associated values of a generic type V.
 * Provides efficient prefix-based search and deletion operations.
 *
 * @param <V> The type of the value associated with each word.
 */
public class TrieWithGenericType<V> {

    // The root node of the Trie.
    private final TrieNode root;

    /**
     * Creates a new empty Trie.
     */
    public TrieWithGenericType() {
        root = new TrieNode();
    }

    /**
     * Represents a node in the Trie.
     */
    private class TrieNode {
        // Map to store child nodes (character to TrieNode).
        private Map<Character, TrieNode> children;
        // Flag indicating if this node represents the end of a word.
        private boolean isEndOfWord;
        // The complete word represented by the path to this node.
        private String word;
        // The value associated with the word.
        private V value;

        /**
         * Creates a new TrieNode.
         */
        public TrieNode() {
            children = new HashMap<>();
            isEndOfWord = false;
            word = null;
            value = null;
        }
    }

    /**
     * Inserts a word and its associated value into the Trie.
     *
     * @param word  The word to insert.
     * @param value The value associated with the word.
     */
    public void insert(String word, V value) {
        if (word == null || word.isEmpty()) return;

        TrieNode current = root;
        word = word.toLowerCase(Locale.ENGLISH); // Convert word to lowercase for case-insensitive search

        for (char ch : word.toCharArray()) {
            // Get or create the child node for the current character.
            current.children.computeIfAbsent(ch, k -> new TrieNode());
            current = current.children.get(ch);
        }
        current.isEndOfWord = true; // Mark the last node as the end of the word.
        current.word = word;
        current.value = value;
    }

    /**
     * Checks if a word exists in the Trie.
     *
     * @param word The word to search for.
     * @return True if the word exists, false otherwise.
     */
    public boolean isExist(String word) {
        if (word == null || word.isEmpty()) return false;

        TrieNode node = searchNode(word.toLowerCase()); // Convert to lowercase for search
        return node != null && node.isEndOfWord; // Check if the node exists and is the end of a word
    }

    /**
     * Searches for words with the given prefix and returns a list of word-value pairs.
     *
     * @param prefix The prefix to search for.
     * @return A list of Map.Entry objects containing matching words and their values.
     */
    public List<Map.Entry<String, V>> searchSimilarWithValues(String prefix) {
        List<Map.Entry<String, V>> result = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) return result;

        TrieNode node = searchNode(prefix.toLowerCase()); // Convert to lowercase for search
        if (node != null) {
            findAllWordsWithValues(node, result); // Find all words starting from the found node
        }
        return result;
    }

    /**
     * Helper method to find the node corresponding to a given string.
     *
     * @param str The string to search for.
     * @return The TrieNode representing the string, or null if not found.
     */
    private TrieNode searchNode(String str) {
        TrieNode current = root;

        for (char ch : str.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) return null; // If a character is not found, the word doesn't exist
        }
        return current;
    }

    /**
     * Helper method to find all words starting from a given node using Depth-First Search (DFS).
     *
     * @param node   The starting node.
     * @param result The list to store the results (word-value pairs).
     */
    private void findAllWordsWithValues(TrieNode node, List<Map.Entry<String, V>> result) {
        if (node.isEndOfWord) {
            result.add(new AbstractMap.SimpleEntry<>(node.word, node.value)); // Add the word and value to the results
        }
        for (TrieNode child : node.children.values()) {
            findAllWordsWithValues(child, result); // Recursively search child nodes
        }
    }

    /**
     * Deletes a word from the Trie.
     *
     * @param word The word to delete.
     */
    public void delete(String word) {
        if (word == null || word.isEmpty()) return;
        deleteRecursive(root, word.toLowerCase(), 0); // Start the recursive deletion
    }

    /**
     * Recursive helper method to delete a word from the Trie.
     *
     * @param current The current node being processed.
     * @param word    The word to delete.
     * @param index   The current index of the character being processed in the word.
     * @return True if the current node should be deleted, false otherwise.
     */
    private boolean deleteRecursive(TrieNode current, String word, int index) {
        if (current == null) return false;

        if (index == word.length()) {
            if (!current.isEndOfWord) return false; // Word not found
            current.isEndOfWord = false; // Unmark as end of word
            current.value = null; // Remove value
            return current.children.isEmpty(); // Delete if no children
        }

        char ch = word.charAt(index);
        if (!current.children.containsKey(ch)) return false; // Character not found

        boolean shouldDeleteCurrentNode = deleteRecursive(current.children.get(ch), word, index + 1);

        if (shouldDeleteCurrentNode) {
            current.children.remove(ch); // Remove the child node
            return current.children.isEmpty() && !current.isEndOfWord; // Delete current if empty and not end of another word
        }
        return false;
    }
}