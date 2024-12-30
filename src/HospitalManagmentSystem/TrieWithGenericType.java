

package HospitalManagmentSystem;

import java.util.*;


public class TrieWithGenericType<V> {

    private final TrieNode root;

    public TrieWithGenericType() {
        root = new TrieNode();
    }

    private class TrieNode {
        private Map<Character, TrieNode> children;
        private boolean isEndOfWord;
        private String word;
        private V value;
        public TrieNode() {
            children = new HashMap<>();
          
            isEndOfWord = false;
            word = null;
            value = null;
        }
    }

    // Insert a word with associated value
    public void insert(String word, V value) {
        if (word == null || word.isEmpty()) return;
        
        TrieNode current = root;
        word = word.toLowerCase(Locale.ENGLISH);
        
        for (char ch : word.toCharArray()) {
           
            current.children.computeIfAbsent(ch, k-> new TrieNode());
            current = current.children.get(ch);
        }
        current.isEndOfWord = true;
        current.word = word;
        current.value = value;
    }

    // Search for exact word match and return its associated value
    public V searchExact(String word) {
        if (word == null || word.isEmpty()) return null;
        
        TrieNode node = searchNode(word.toLowerCase(Locale.ENGLISH));
       return (node != null && node.isEndOfWord) ? node.value : null;
    }

    public boolean isExist(String word){
         if (word == null || word.isEmpty()) return false;
        
        TrieNode node = searchNode(word.toLowerCase());
        return node != null && node.isEndOfWord;
    }
    
    // Search for similar words and return pairs of words and their values
   
    public List<Map.Entry<String, V>> searchSimilarWithValues(String prefix) {
        List<Map.Entry<String, V>> result = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) return result;

        TrieNode node = searchNode(prefix.toLowerCase());
        if (node != null) {
            findAllWordsWithValues(node, result);
        }
        return result;
    }

    // Helper method to find node for a given string
    private TrieNode searchNode(String str) {
        TrieNode current = root;
    
        for (char ch : str.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) return null;
        }
        return current;
    }

    // Helper method to find all words starting from a node (DFS)
   private void findAllWordsWithValues(TrieNode node, List<Map.Entry<String, V>> result) {
        if (node.isEndOfWord) {
            result.add(new AbstractMap.SimpleEntry<>(node.word, node.value));
        }
        for (TrieNode child : node.children.values()) {
            findAllWordsWithValues(child, result);
        }
    }
     public void delete(String word) {
        if (word == null || word.isEmpty()) return;
        deleteRecursive(root, word.toLowerCase(), 0);
    }

    private boolean deleteRecursive(TrieNode current, String word, int index) {
        if (current == null) return false;

        if (index == word.length()) {
            if (!current.isEndOfWord) return false;
            current.isEndOfWord = false;
            current.value = null;
            return current.children.isEmpty();
        }

        char ch = word.charAt(index);
        if (!current.children.containsKey(ch)) return false;

        boolean shouldDeleteCurrentNode = deleteRecursive(current.children.get(ch), word, index + 1);

        if (shouldDeleteCurrentNode) {
            current.children.remove(ch);
            return current.children.isEmpty() && !current.isEndOfWord;
        }
        return false;
    }
    
}