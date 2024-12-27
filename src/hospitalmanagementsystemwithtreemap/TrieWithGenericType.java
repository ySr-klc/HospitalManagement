

package hospitalmanagementsystemwithtreemap;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
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
        private Map<Character, TrieNode> children;
        private boolean isEndOfWord;
        private String word;
        private V value;
        private char character; // To store the character for this node

        public TrieNode() {
            children = new HashMap<>();
          
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
        
        TrieNode node = searchNode(word.toLowerCase());
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
    
   

    
    public List<String> searchSimilar(String prefix) {
        List<String> result = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) return result;

        TrieNode node = searchNode(prefix.toLowerCase());
        if (node != null) {
            findAllWordsWithPrefix(node, result, new StringBuilder(prefix));
        }
        return result;
    }
    
    // Get all words that has been recorded tree
    public List<String> getAll() {
        List<String> result = new ArrayList<>();
        findAllWords(root, result);
        return result;
    }

    private void findAllWords(TrieNode node, List<String> result) {
        if (node.isEndOfWord) {
            result.add(node.word);
        }

        for (TrieNode child : node.children.values()) { // More efficient iteration
            if (child != null) {
                findAllWords(child, result);
            }
        }
    }
    
    

  private void findAllWordsWithPrefix(TrieNode node, List<String> result, StringBuilder currentWord) {
        if (node.isEndOfWord) {
            result.add(currentWord.toString());
        }

        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            char ch = entry.getKey();
            TrieNode child = entry.getValue();
            currentWord.append(ch);
            findAllWordsWithPrefix(child, result, currentWord);
            currentWord.deleteCharAt(currentWord.length() - 1); // Backtrack
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
    
    
     public static void main(String[] args) {
        TrieWithGenericType<Integer> trie = new TrieWithGenericType<>();

        // Insert some words with values
        trie.insert("apple", 1);
        trie.insert("app", 2);
        trie.insert("application", 3);
        trie.insert("banana", 4);
        trie.insert("ban", 5);
        trie.insert("james", 6);
        trie.insert("jackson", 7);
        trie.insert("ajan", 8);


        // Test exact search
        System.out.println("Exact search for 'apple': " + trie.searchExact("apple")); // Output: 1
        System.out.println("Exact search for 'app': " + trie.searchExact("app"));     // Output: 2
        System.out.println("Exact search for 'grape': " + trie.searchExact("grape"));   // Output: null

        // Test similar search
        System.out.println("\nSimilar search for 'app': " + trie.searchSimilar("app"));
        // Output: [app, apple, application]
        System.out.println("\nSimilar search for 'ban': " + trie.searchSimilar("ban"));
        // Output: [ban, banana]
        System.out.println("\nSimilar search for 'j': " + trie.searchSimilar("j"));
        // Output: [james, jackson, ajan]
        System.out.println("\nSimilar search for 'a': " + trie.searchSimilar("a"));
        // Output: [apple, app, application, ajan]
        System.out.println("\nSimilar search for 'k': " + trie.searchSimilar("kson").toString());

        System.out.println("\nSimilar search with value for 'app': " + trie.searchSimilarWithValues("app"));
        //Output: [app=2, apple=1, application=3]

        // Test delete
        trie.delete("apple");
        System.out.println("\nAfter deleting 'apple', search for 'apple': " + trie.searchExact("apple")); // Output: null
        System.out.println("Similar search for 'app' after delete apple: " + trie.searchSimilar("app"));
        //Output: [app, application]

        trie.delete("app");
        System.out.println("\nAfter deleting 'app', search for 'app': " + trie.searchExact("app")); // Output: null
        System.out.println("Similar search for 'app' after delete app: " + trie.searchSimilar("app"));
        //Output: [application]

        trie.delete("application");
        System.out.println("\nAfter deleting 'application', search for 'application': " + trie.searchExact("application")); // Output: null
        System.out.println("Similar search for 'app' after delete application: " + trie.searchSimilar("app"));
        //Output: []

        // Test with empty string
        System.out.println("\nSimilar search for '': " + trie.searchSimilar("")); // Output: []

        TrieWithGenericType<String> trieString = new TrieWithGenericType<>();
        trieString.insert("hello","world");
        System.out.println("\nExact search for hello: "+trieString.searchExact("hello")); //Output: world
    }
}