/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author ysr
 */
public class Patient extends Person {

    List<AppointmentSlot> patientAppointments;
    Stack<String> notifications;
    public Patient(String name) {
        super(name);
        patientAppointments = new LinkedList();        
        notifications=new Stack<>();
    }
   public void addNotification(String notification){
        notifications.push(notification);
    }
//    
   public Stack<String> getNotifications(){
       return notifications;
   }
    public void addAppointment(AppointmentSlot apt) {
        this.patientAppointments.add(apt);
        patientAppointments.sort(Comparator.comparing(o-> o.time));
    }

    public void deleteAppointment(AppointmentSlot apt) {
        patientAppointments.remove(apt);
    }
    
    public List<AppointmentSlot> getPatientAppointments() {
        return patientAppointments;
    }
     
}
