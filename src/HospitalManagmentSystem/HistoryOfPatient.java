/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HospitalManagmentSystem;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 *Think we created object of this class for doctor and patient seperatly and all the other functionalties will keep in this class 
 *
 */
import java.util.*;

public class HistoryOfPatient {

    private static Map<Integer, LinkedList<History>> histories;

    public HistoryOfPatient(List<Patient> persons) throws IllegalArgumentException {
        histories = new HashMap<>();
        if (persons == null) {
            throw new IllegalArgumentException("List of persons cannot be null");
        }
        for (Patient person : persons) {
            if (person == null) {
                throw new IllegalArgumentException("Person object cannot be null");
            }
            histories.put(person.getId(), new LinkedList<>());
        }
    }

    public void updateList(List<Patient> persons) throws IllegalArgumentException {
        if (persons == null) {
            throw new IllegalArgumentException("List of persons cannot be null");
        }
        for (Patient person : persons) {
            if (person == null) {
                throw new IllegalArgumentException("Person object cannot be null");
            }
            histories.computeIfAbsent(person.getId(), k -> new LinkedList<>());
        }
    }

    public void deleteSpecificElementFromList(Patient person) {
        if (person != null) {
            histories.remove(person.getId());
        }
    }

    public void addHistory(Patient person, History event) throws IllegalArgumentException {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        LinkedList<History> history = histories.get(person.getId());
        if (history == null) {
            history = new LinkedList<>();
            histories.put(person.getId(), history);
        }
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        int index = 0;
        for (History h : history) {
            if (event.time.compareTo(h.time) < 0) {
                break;
            }
            index++;
        }
        history.add(index, event);
    }

    public static LinkedList<History> getHistory(Patient person) throws IllegalArgumentException {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        return histories.get(person.getId());
    }

   
    
}