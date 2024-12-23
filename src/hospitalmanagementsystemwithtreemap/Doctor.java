/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import java.time.LocalDateTime;
import java.util.PriorityQueue;

/**
 *
 * @author ysr
 */
public class Doctor extends Person {

    private String speciality;
    private PriorityQueue<AppointmentSlot> appointments;
   
    
    public Doctor(String name, String speciality) {
        super(name);
        this.speciality = speciality;
        appointments=new PriorityQueue<>(
     (o1, o2) -> o1.time.compareTo(o2.time)
    );
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String name) {
        this.speciality = name;
    }

    @Override
    public String toString() {
        String newString
                = "Doctor Name:" + super.name + " Speciality:" + speciality;
        return newString;
    }

    public PriorityQueue<AppointmentSlot> getAppointmentSlot() {
        return appointments;
    }

    public void setAppointmentSlot(PriorityQueue<AppointmentSlot> appointmentSlot) {
        this.appointments = appointmentSlot;
    }

    public void createAppoinmentSlot(AppointmentSlot appointment) {
        if (appointments == null) {
            appointments = new PriorityQueue<>((o1, o2) -> o1.time.compareTo(o2.time));
        }
        appointments.add(appointment);
    }

    public void cancelAppointment(LocalDateTime apmntHour) {
        if (appointments != null) {
            // Create a new queue and transfer elements, removing the cancelled appointment
            PriorityQueue<AppointmentSlot> newQueue = new PriorityQueue<>(
                    (o1, o2) -> o1.time.compareTo(o2.time));

            for (AppointmentSlot slot : appointments) {
                if (slot.time.equals(apmntHour)) {
                    if (slot.isbooked) {
                        slot.getPatient().deleteAppointment(slot);
//                 slot.getPatient().addNotfication("Your appointment canceled by doctor:" + slot.getDocName()+" Date:"+slot.getTime()+" Clinic"+slot.doc.getSpeciality());
                    }
                    continue;
                }
                newQueue.add(slot);
            }
            appointments = newQueue;
        }
    }

    

}
