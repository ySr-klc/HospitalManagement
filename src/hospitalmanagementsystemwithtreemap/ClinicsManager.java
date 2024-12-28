/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ysr
 */
public class ClinicsManager {

    HashMap<ClinicEnums.Department, Clinic> clinics;

    public ClinicsManager(List<Doctor> doctors) {
    clinics = (HashMap<ClinicEnums.Department, Clinic>) Arrays.stream(ClinicEnums.Department.values())
            .collect(Collectors.toMap(
                    clinic -> clinic,
                    clinic -> new Clinic(
                            clinic,
                            doctors.stream()
                                    .filter(doctor -> clinic.equals(doctor.getSpeciality()))
                                    .collect(Collectors.toList())
                    )
            ));
}
      //OverWritten constructur    
      public ClinicsManager(List<Doctor> doctors, AppointmentManager manager) {       
        clinics = new HashMap<>();
        List<Doctor> clinicSpecifiedDoctors=new ArrayList<>();
        for (ClinicEnums.Department clinic : Arrays.asList(ClinicEnums.Department.values())) {
            for (Doctor doctor : doctors) {
                if (clinic.equals(doctor.getSpeciality())) {
                    clinicSpecifiedDoctors.add(doctor);
                }
        }
        clinics.putIfAbsent(clinic, new Clinic(clinic,clinicSpecifiedDoctors, manager));
        }
        
    }
      
    public void connectAppointmentManager(AppointmentManager manager){
        for (Clinic value : clinics.values()) {
            value.setAppointmentManager(manager);
        }
    }
      
    public Clinic getClinic(ClinicEnums.Department clinic){
        return clinics.get(clinic);
    }
    public void addDoctor(ClinicEnums.Department clinic, Doctor doctor){
        clinics.get(clinic).addDoctor(doctor);
    }
    public void deleteDoctor(ClinicEnums.Department clinic, Doctor doc)
    {
        clinics.get(clinic).deleteDoctor(doc);
    }    
     public List<Doctor> getDoctor(ClinicEnums.Department clinic, Doctor doc){
        return clinics.get(clinic).getDoctors();
    }    
    
    
    
    public void addHistoryToClinic(ClinicEnums.Department clinic, History event ){
        clinics.get(clinic).addHistoryToDoctor(event);
    }
    public List<History> getHistoryOfClinic(ClinicEnums.Department clinic){
        return clinics.get(clinic).getHistory();
    }
    
    public List<AppointmentSlot> getAppointmentsFromClinic(ClinicEnums.Department clinic){
     return clinics.get(clinic).getDoctorsNearestAppointment();
    }
    
    
    
    
    
}
