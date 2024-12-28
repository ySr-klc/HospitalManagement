/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import java.util.PriorityQueue;

/**
 *
 * @author ysr
 */
public class HistoryOfClinic {
    
    private PriorityQueue<History> historiesOfClinic;

    // History Manager List Controller
    public HistoryOfClinic() {
      
        historiesOfClinic=new PriorityQueue<>();
      
    }

 
    public void addHistory(History event) {
        if (event == null) {
            throw new IllegalArgumentException("Appointment information is null");
        }
            historiesOfClinic.offer(event);
    }

    public PriorityQueue<History> getHistory() {
        return historiesOfClinic;
    }
}

