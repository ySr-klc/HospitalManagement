/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import hospitalmanagementsystemwithtreemap.ClinicEnums;
import hospitalmanagementsystemwithtreemap.AppointmentManager;
import hospitalmanagementsystemwithtreemap.AppointmentSlot;
import hospitalmanagementsystemwithtreemap.Doctor;
import hospitalmanagementsystemwithtreemap.HistoryOfClinic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ysr
 */
public class Clinic {
    
    List<Doctor> doctors;
    HistoryOfClinic history;
    AppointmentManager appointmentManagerConnection;
    
    private final ClinicEnums.Department departmentKey;
    
    
    
    public Clinic(ClinicEnums.Department departmentKey,List<Doctor> doctor) {
        
        this.departmentKey=departmentKey;
        this.doctors = doctor;
         
        history=new HistoryOfClinic(); 
    }
    
 
    
     public Clinic(ClinicEnums.Department departmentKey,List<Doctor> doctor, AppointmentManager myManager) {
        this.appointmentManagerConnection=myManager;
        this.departmentKey=departmentKey;
        this.doctors = doctor;
         
        history=new HistoryOfClinic(); 
    }
    
    //--------------------------------------------
    
    public void addHistoryToDoctor(String event){
        history.addHistory(event);
    }
    
    public List<String> getHistory(){
        return  history.getHistory().stream().toList();
    }
    
    
    //-----------------------------------------
    
   public void addDoctor(Doctor doctor) {
        if (doctor.getSpeciality().equals(departmentKey)) {
            this.doctors.add(doctor);
        } else {
            throw new IllegalArgumentException("Cannot add doctor. Specialty does not match the clinic's department.");
        }
    }

    public void deleteDoctor(Doctor doctor) {
        this.doctors.remove(doctor);
    }

    public List<Doctor> getDoctors() {
        return new ArrayList<>(this.doctors); // Return a copy to prevent external modification
    }

    public ClinicEnums.Department getDepartmentKey() {
        return departmentKey;
    }
    
    
    //-----------------------------------------------------------------
    
    public void setAppointmentManager(AppointmentManager managerObejct){
        appointmentManagerConnection =managerObejct;
    }
    
    public List<AppointmentSlot> getDoctorsNearestAppointment(){
        List<AppointmentSlot> aptList=new ArrayList<>();
        for (Doctor doctor : doctors) {
             aptList.add(appointmentManagerConnection.getNearestAvailableAppointmentSlot(doctor.getId()));
        }
        aptList.sort((c1,c2)->c1.getTime().compareTo(c2.getTime()));
        return aptList;
    }
    
    
}
