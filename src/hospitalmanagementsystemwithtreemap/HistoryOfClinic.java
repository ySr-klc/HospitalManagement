/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import java.util.List;
import java.util.Stack;

/**
 *
 * @author ysr
 */
public class HistoryOfClinic {
    
    private Stack<String> historiesOfClinic;

    // History Manager List Controller
    public HistoryOfClinic() {
      
        historiesOfClinic=new Stack<>();
      
    }

 
    public void addHistory(String event) {
        if (event == null) {
            throw new IllegalArgumentException("Appointment information is null");
        }
            historiesOfClinic.push(event);
    }

    public Stack<String> getHistory() {
        return historiesOfClinic;
    }
}

