

package hospitalmanagementsystemwithtreemap;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ysr
 */
public class TrieWithGenericType<V> {
    private V value;
    private TrieNode root;

    public TrieWithGenericType() {
        root = new TrieNode();
    }

    private class TrieNode {
        private List<TrieNode> children;
        private boolean isEndOfWord;
        private String word;
        private V value;
        private char character; // To store the character for this node

        public TrieNode() {
            children = new ArrayList<>(26);
            // Initialize the list with 26 null elements (for a-z)
            for (int i = 0; i < 26; i++) {
                children.add(null);
            }
            isEndOfWord = false;
            word = null;
            value = null;
        }

        public TrieNode(char ch) {
            this();
            this.character = ch;
        }
    }

    // Insert a word with associated value
    public void insert(String word, V value) {
        if (word == null || word.isEmpty()) return;
        
        TrieNode current = root;
        word = word.toLowerCase();
        
        for (char ch : word.toCharArray()) {
            
            int index = ch - 'a';
//            if (index<=26||index>=0) {
//                continue;
//            }
            if (current.children.get(index) == null) {
                current.children.set(index, new TrieNode(ch));
            }
            current = current.children.get(index);
        }
        current.isEndOfWord = true;
        current.word = word;
        current.value = value;
    }

    // Search for exact word match and return its associated value
    public V searchExact(String word) {
        if (word == null || word.isEmpty()) return null;
        
        TrieNode node = searchNode(word.toLowerCase());
        if (node != null && node.isEndOfWord) {
            return node.value;
        }
        return null;
    }

    public boolean isExist(String word){
         if (word == null || word.isEmpty()) return false;
        
        TrieNode node = searchNode(word.toLowerCase());
        return node != null && node.isEndOfWord;
    }
    
    // Search for similar words and return pairs of words and their values
    public List<Map.Entry<String, V>> searchSimilarr(String prefix) {
        List<Map.Entry<String, V>> result = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) return result;
        
        TrieNode node = searchNode(prefix.toLowerCase());
        if (node != null) {
            findAllWordsr(node, result);
        }
        return result;
    }

    // Helper method to find node for a given string
    private TrieNode searchNode(String str) {
        TrieNode current = root;
        
        for (char ch : str.toCharArray()) {
            int index = ch - 'a';
            if (current.children.get(index) == null) {
                return null;
            }
            current = current.children.get(index);
        }
        return current;
    }

    // Helper method to find all words starting from a node (DFS)
    private void findAllWordsr(TrieNode node, List<Map.Entry<String, V>> result) {
        if (node.isEndOfWord) {
            result.add(new AbstractMap.SimpleEntry<>(node.word, node.value));
        }

        for (int i = 0; i < 26; i++) {
            TrieNode child = node.children.get(i);
            if (child != null) {
                findAllWordsr(child, result);
            }
        }
    }
    // Get all words that has been recorded tree
    public List<String> getAll(){
        List<String> result = new ArrayList<>();
        findAllWords(root, result);
        return result;
    }
    
       // Search for similar words (returns only the words, not the values)
    public List<String> searchSimilar(String prefix) {
        List<String> result = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) return result;
        
        TrieNode node = searchNode(prefix.toLowerCase());
        if (node != null) {
            findAllWords(node, result);
        }
        return result;
    }

    // Helper method to find all words starting from a node (DFS)
    private void findAllWords(TrieNode node, List<String> result) {
        if (node.isEndOfWord) {
            result.add(node.word);
        }

        for (int i = 0; i < 26; i++) {
            TrieNode child = node.children.get(i);
            if (child != null) {
                findAllWords(child, result);
            }
        }
    }
    
     public void delete(String word) {
        if (word == null || word.isEmpty()) return;

        word = word.toLowerCase();
        deleteRecursive(root, word, 0);
    }

    private boolean deleteRecursive(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord) {
                return false; // Word not found
            }
            current.isEndOfWord = false;
            current.value = null; // Remove the value associated with the word
            return current.children.stream().allMatch(child -> child == null); // Check if node can be deleted
        }

        int charIndex = word.charAt(index) - 'a';
        if (charIndex < 0 || charIndex > 25 || current.children.get(charIndex) == null) {
            return false; // Word not found
        }

        boolean shouldDeleteCurrentNode = deleteRecursive(current.children.get(charIndex), word, index + 1);

        if (shouldDeleteCurrentNode) {
            current.children.set(charIndex, null);
            return current.children.stream().allMatch(child -> child == null) && !current.isEndOfWord;
        }
        return false;
    }
    
}