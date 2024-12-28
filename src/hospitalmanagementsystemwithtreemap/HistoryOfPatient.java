/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 *Think we created object of this class for doctor and patient seperatly and all the other functionalties will keep in this class 
 * that's you should keep all the list of doctor and patient in here in trie 
 * 
 * 
 * @author ysr
 * @param <T>
 */
public class HistoryOfPatient{

    private TrieWithGenericType<PriorityQueue<History>> histories;

    // History Manager List Controller
    public HistoryOfPatient(List<Patient> persons) throws IllegalArgumentException {
        histories = new TrieWithGenericType<>();
        if (persons == null) {
            throw new IllegalArgumentException("List of persons cannot be null");
        }
        for (Patient person : persons) {
            if (person == null) {
                throw new IllegalArgumentException("Person object cannot be null");
            }
            histories.insert(person.getName(), new PriorityQueue<>(Comparator.comparing(o-> o.time)));
        }
    }

   
    public void updateList(List<Patient> persons) throws IllegalArgumentException {
        if (persons == null) {
            throw new IllegalArgumentException("List of persons cannot be null");
        }
        for (Person person : persons) {
            if (person == null) {
                throw new IllegalArgumentException("Person object cannot be null");
            }
            if (!histories.isExist(person.getName())) {
                histories.insert(person.getName(), new PriorityQueue<>(Comparator.comparing(o-> o.time)));
            }
        }
    }

    
    public void deleteSpecificElementFromList(Patient person) {
        if (person != null && histories.isExist(person.getName())) {
            histories.delete(person.getName());
        }
    }


    public void clearAllList() {
        histories = new TrieWithGenericType<>(); // Garbage collector handles old object
    }

  
    public void addHistory(Patient person, History event) throws IllegalArgumentException {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        PriorityQueue<History> history = histories.searchExact(person.getName());
        if (history == null) {
            history = new PriorityQueue<>(Comparator.comparing(o-> o.time));
            histories.insert(person.getName(), history);
        }
        if (event == null) { // Add check for null event if needed
            throw new IllegalArgumentException("Event cannot be null");
        }
        history.offer(event);
    }

    public PriorityQueue<History> getHistory(Patient person) throws IllegalArgumentException {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        return histories.searchExact(person.getName());
    }
}
