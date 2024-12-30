package HospitalManagmentSystem;

import java.time.LocalDateTime;

public class AppointmentSlot {
    Department clinic;
    Doctor doc;
    String docName;
    LocalDateTime time;
    LocalDateTime endTime;
    Patient patient;
    String patientName;
    boolean isbooked;
    boolean PatientCame;
    String diagnosis="null";
    public AppointmentSlot(Doctor doc, String docName, LocalDateTime time, LocalDateTime endTime) {
        this.doc = doc;
        this.docName = docName;
        this.time = time;
        this.endTime = endTime;
        clinic=doc.getSpeciality();
    }

    public boolean isPatientCame() {
        return PatientCame;
    }

    public void setIsPatientCame(boolean isPatientCome) {
        this.PatientCame = isPatientCome;
    }

    public Department getClinic() {
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

    public String getDocName() {
        return docName;
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getPatientName() {
        return patientName;
    }

    public boolean isBooked() {
        return isbooked;
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
