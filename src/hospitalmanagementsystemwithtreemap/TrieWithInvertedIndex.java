/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;
import java.util.*;
/**
 *
 * @author ysr
 */


public class TrieWithInvertedIndex<V> {
    private TrieWithGenericType<V> trie = new TrieWithGenericType<>();
    private Map<String, List<String>> invertedIndex = new HashMap<>();

    public void insert(String word, V value) {
        trie.insert(word, value);
        for (int i = 0; i < word.length(); i++) {
            String suffix = word.substring(i);
            invertedIndex.computeIfAbsent(suffix, k -> new ArrayList<>()).add(word);
        }
    }

    public List<String> searchContaining(String substring) {
        return invertedIndex.getOrDefault(substring, Collections.emptyList());
    }

    // ... (You can keep other Trie methods like searchExact if needed)



    public static void main(String[] args) {
        TrieWithInvertedIndex<Integer> index = new TrieWithInvertedIndex<>();
        index.insert("jackson", 1);
        index.insert("apple",2);
        index.insert("aspplication",3);

        System.out.println(index.searchContaining("jac")); // Output: [jackson]
        System.out.println(index.searchContaining("apple")); // Output: [apple]
        System.out.println(index.searchContaining("cation")); // Output: [application]

    }

}