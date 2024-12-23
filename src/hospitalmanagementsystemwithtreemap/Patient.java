/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author ysr
 */
public class Patient extends Person {

    List<AppointmentSlot> patientAppoinments;
    Stack<String> notifications;
    public Patient(String name) {
        super(name);
        patientAppoinments = new LinkedList();        
        notifications=new Stack<>();
    }
   public void addNotfication(String notification){
        notifications.push(notification);
    }
//    
   public Stack<String> getNotifications(){
       return notifications;
   }
    public void addAppointment(AppointmentSlot apt) {
        this.patientAppoinments.add(apt);
        patientAppoinments.sort((o1, o2) -> o1.time.compareTo(o1.time));
    }

    public void deleteAppointment(AppointmentSlot apt) {
        patientAppoinments.remove(apt);
    }
    
     public AppointmentSlot getcancelAppointment( LocalDateTime appointment) {
         for (AppointmentSlot patientAppoinment : patientAppoinments) {
             
             if(patientAppoinment.time.equals(appointment)){
                 return patientAppoinment;
             }
                 
         }
       return null;
    }

    public List<AppointmentSlot> getPatientAppoinments() {
        return patientAppoinments;
    }
     
}
