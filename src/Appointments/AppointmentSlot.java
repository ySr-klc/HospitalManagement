package Appointments;

import Clinics.Department;
import Persons.Doctor;
import Persons.Patient;

import java.time.LocalDateTime;

/**
 * Represents a time based appointment slot for an appointment with a doctor in a specific clinic.
 */
public class AppointmentSlot {
    private Department clinic;
    private Doctor doc;
    private String docName;
    private LocalDateTime time;
    private LocalDateTime endTime;
    private Patient patient;
    private String patientName;
    private boolean isbooked;
    private boolean patientAttended;
    private String diagnosis;


  
    /**
     * Creates an appointment slot with the given doctor information, appointment time, and end time.
     * The clinic is automatically set based on the doctor's speciality.
     *
     * @param doc       The doctor associated with the appointment.
     * @param docName   The doctor's name (for display).
     * @param time      The start time of the appointment.
     * @param endTime  The end time of the appointment.
     */
    public AppointmentSlot(Doctor doc, String docName, LocalDateTime time, LocalDateTime endTime) {
        this.doc = doc;
        this.docName = docName;
        this.time = time;
        this.endTime = endTime;
        clinic=doc.getSpeciality();
    }
    
    /**
     * Checks if the patient attended the appointment.
     *
     * @return True if the patient attended, false otherwise.
     */
    public boolean hasPatientAttended() {
        return patientAttended;
    }
    
    /**
     * Sets the flag indicating if the patient attended the appointment.
     *
     * @param patientAttended True if the patient attended, false otherwise.
     */
    public void setPatientAttended(boolean patientAttended) {
        this.patientAttended = patientAttended;
    }

    public Department getClinic() {
        return clinic;
    }
    
     /**
     * Gets the doctor's diagnosis for the appointment.
     *
     * @return The doctor's diagnosis (null if not set).
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    
    /**
     * Sets the doctor's diagnosis for the appointment.
     *
     * @param diagnosis The doctor's diagnosis.
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
   
    
     /**
     * Books the appointment with a patient and sets the patient's name.
     *
     * @param patient  The patient to book the appointment for.
     * @param name     The patient's name (for display).
     */
    public void booked(Patient patient, String name) {
        setIsbooked(true);
        this.setPatient(patient);
        this.setPatientName(name);
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
        return isIsbooked();
    }

    @Override
public String toString() {
    return String.format("Appointment with %s at %s - %s%s", 
        getDocName(), 
        getTime().toString(), getEndTime().toString(),
        isIsbooked() ? " (Booked by " + getPatientName() + ")" : " (Available)");
    }

    /**
     * @param clinic the clinic to set
     */
    public void setClinic(Department clinic) {
        this.clinic = clinic;
    }

    /**
     * @param doc the doc to set
     */
    public void setDoc(Doctor doc) {
        this.doc = doc;
    }

    /**
     * @param docName the docName to set
     */
    public void setDocName(String docName) {
        this.docName = docName;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * @param patientName the patientName to set
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * @return the isbooked
     */
    public boolean isIsbooked() {
        return isbooked;
    }

    /**
     * @param isbooked the isbooked to set
     */
    public void setIsbooked(boolean isbooked) {
        this.isbooked = isbooked;
    }

    /**
     * @param PatientCame the PatientCame to set
     */
    public void setPatientCame(boolean PatientCame) {
        this.patientAttended = PatientCame;
    }
}
