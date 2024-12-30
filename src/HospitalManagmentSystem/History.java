package HospitalManagmentSystem;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class History implements Comparable<History>  {
    LocalDateTime time;
    String doctorName;
    int DoctorID;
    String PatientName;
    int patientId;
    String diagnosis;
    String note;
    Department clinic;
    public History(LocalDateTime time, Doctor doctor, Patient pat, String diagnosis) {
        this.time = time;
        this.doctorName = doctor.getName();
        this.DoctorID = doctor.getId();
        this.PatientName = pat.getName();
        this.patientId = pat.getId();
        this.diagnosis = diagnosis;
        this.clinic=doctor.getSpeciality();
    }

    public Department getClinic() {
        return clinic;
    }

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
        return DoctorID;
    }

    public void setDoctorID(int DoctorID) {
        this.DoctorID = DoctorID;
    }

    public String getPatientName() {
        return PatientName;
    }

    public void setPatientName(String PatientName) {
        this.PatientName = PatientName;
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
     
     
     
     
     
    @Override
      public int compareTo(History other) {
        // Implement comparison logic here
        // Example: Compare by time first
        return this.time.compareTo(other.time);
    }
    @Override
      public String toString() {
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
    }
}
