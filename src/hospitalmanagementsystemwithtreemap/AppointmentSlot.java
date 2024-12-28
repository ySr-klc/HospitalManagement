/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import java.time.LocalDateTime;

/**
 *
 * @author ysr
 */
public class AppointmentSlot {
    hospitalmanagementsystemwithtreemap.ClinicEnums.Department clinic;
    Doctor doc;
    String docName;
    LocalDateTime time;
    LocalDateTime endTime;
    Patient patient;
    String patientName;
    boolean isbooked;
    boolean PatientCome;
    String diagnosis="null";
    public AppointmentSlot(Doctor doc, String docName, LocalDateTime time, LocalDateTime endTime) {
        this.doc = doc;
        this.docName = docName;
        this.time = time;
        this.endTime = endTime;
        clinic=doc.getSpeciality();
    }

    public boolean isPatientCome() {
        return PatientCome;
    }

    public void setIsPatientCome(boolean isPatientCome) {
        this.PatientCome = isPatientCome;
    }

    public ClinicEnums.Department getClinic() {
        return clinic;
    }
    
    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    public void booked(Patient patient,String name){
        isbooked=true;
        this.patient=patient;
        this.patientName=name;
    }

    public Doctor getDoc() {
        return doc;
    }

    public void setDoc(Doctor doc) {
        this.doc = doc;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public boolean isBooked() {
        return isbooked;
    }

    public void setIsbooked(boolean isbooked) {
        this.isbooked = isbooked;
    }
    
 
    
    @Override
public String toString() {
    return String.format("Appointment with %s at %s - %s%s", 
        docName, 
        time.toString(), 
        endTime.toString(),
        isbooked ? " (Booked by " + patientName + ")" : " (Available)");
}
    
    
}
