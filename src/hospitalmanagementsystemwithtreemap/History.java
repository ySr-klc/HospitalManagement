/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author ysr
 */
public class History implements Comparable<History>  {

    LocalDateTime time;
    String doctorName;
    int DoctorID;
    String PatientName;
    int patientId;
    String diagnosis=null;
    String note;
    public History(LocalDateTime time, Doctor doctor, Patient pat, String diagnosis) {
        this.time = time;
        this.doctorName = doctor.getName();
        this.DoctorID = doctor.getId();
        this.PatientName = pat.getName();
        this.patientId = pat.getId();
        this.diagnosis = diagnosis;
    }
    
    
     public History(LocalDateTime time, Doctor doctor, Patient pat, String diagnosis, String note) {
        this.time = time;
        this.doctorName = doctor.getName();
        this.DoctorID = doctor.getId();
        this.PatientName = pat.getName();
        this.patientId = pat.getId();
        this.diagnosis = diagnosis;
        this.note=note;
    }
    
    @Override
      public int compareTo(History other) {
        // Implement comparison logic here
        // Example: Compare by time first
        return this.time.compareTo(other.time);
    }
     
     
    @Override
      public String toString() {
          if (note==null) {
            return String.format(
                "Time: %s%n" + 
                "Doctor: %s (ID: %d)%n" +
                "Patient: %s (ID: %d)%n" +
                "Diagnosis: %s",
                time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                doctorName, DoctorID,
                PatientName, patientId,
                diagnosis
        );
        }else {
              return String.format(
                "Time: %s%n" + // %n for newline
                "Doctor: %s (ID: %d)%n" +
                "Patient: %s (ID: %d)%n" +
                "Error: &s",
                time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                doctorName, DoctorID,
                PatientName, patientId,
                note);
          }
        
    }
    
    

}
