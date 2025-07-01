package History;
import Clinics.Department;
import Persons.Doctor;
import Persons.Patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class represents a medical history entry for a patient. It contains details such as the date and time of the visit,
 * the doctor who saw the patient, the patient themself, their diagnosis, and any additional notes.
 */
public class History {
    private LocalDateTime time;
    private String doctorName;
    private int doctorID;
    private String patientName;
    private int patientId;
    private String diagnosis;
    private String note;
    private Department clinic;
     /**
     * Constructor for the History class. Takes the date and time of the visit, the doctor object, the patient object,
     * and the patient's diagnosis as arguments.
     *
     * @param time The date and time of the visit
     * @param doctor The doctor who saw the patient (Doctor object)
     * @param pat The patient who was seen (Patient object)
     * @param diagnosis The doctor's diagnosis for the patient
     */
    public History(LocalDateTime time, Doctor doctor, Patient pat, String diagnosis) {
        this.time = time;
        this.doctorName = doctor.getName();
        this.doctorID = doctor.getId();
        this.patientName = pat.getName();
        this.patientId = pat.getId();
        this.diagnosis = diagnosis;
        this.clinic = doctor.getSpeciality(); // Assuming getSpeciality() exists in the Doctor class
    }

    /**
     * Getter method for the clinic (department) of the doctor who saw the patient.
     *
     * @return The department associated with the doctor
     */
    public Department getClinic() {
        return clinic;
    }
 
    
        /**
     * Overrides the default toString() method to provide a formatted string representation of the History object,
     * including date and time, doctor information, patient information, and diagnosis.
     *
     * @return A formatted string representation of the History object
     */
    @Override
    public String toString() {
        return String.format("Time: %s%n" +
                "Doctor: %s (ID: %d)%n" +
                "Patient: %s (ID: %d)%n" +
                "Diagnosis: %s%n" +
                (note != null ? "Note: %s%n" : ""), // Include note if available
                getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                getDoctorName(), getDoctorID(), getPatientName(), getPatientId(), getDiagnosis(), getNote());
    }
    
    
    
    
     //Getters and Setters
    public void setClinic(Department clinic) {
        this.clinic = clinic;
    }
     
     
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public int getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(int DoctorID) {
        this.doctorID = DoctorID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String PatientName) {
        this.patientName = PatientName;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}


