/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import java.util.List;
import java.util.Stack;

/**
 *Think we created object of this class for doctor and patient seperatly and all the other functionalties will keep in this class 
 * that's you should keep all the list of doctor and patient in here in trie 
 * 
 * 
 * @author ysr
 * @param <T>
 */
public class HistoryManager<T extends Person> {
    TrieWithGenericType<Stack<String>> histories;

    
    //History Manager List Controller
    public HistoryManager(List<T> persons) {
        histories=new TrieWithGenericType<>();
        for (T person : persons) {
            histories.insert(person.name, new Stack<>());
        }
    }
    
    
    public void updateDoctorList(List<Doctor> persons){
        for (Doctor person : persons) {
            if (!histories.isExist(person.name)) {
                histories.insert(person.name, new Stack());
            }
        }
    }
    
    public void deleteSpecificDoctorFromList(Doctor person){
        if (histories.isExist(person.name)) {
            histories.delete(person.name);
        }
    }
    
    public void clearAllList(){
        histories=new TrieWithGenericType<>(); //Garbage collector will handle old history object
    }
    
    public void addHistory(Doctor person,String event){
       Stack<String> history= histories.searchExact(person.getName());
       history.push(event);
    }
    
    public Stack<String> getHistory(Doctor person){
       return histories.searchExact(person.getName());
    }
    
    
}
