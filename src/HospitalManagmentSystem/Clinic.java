package HospitalManagmentSystem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Clinic {
    List<Doctor> doctors;
    AppointmentManager appointmentManagerConnection;
    
    private final Department departmentKey;

    public Clinic(Department departmentKey,List<Doctor> doctor) {
        
        this.departmentKey=departmentKey;
        this.doctors = doctor;
        
       
    }
   public void addDoctor(Doctor doctor) {
        if (doctor.getSpeciality().equals(departmentKey)) {
            if (!doctors.contains(doctor)) {
                 this.doctors.add(doctor);
            }
           
        } else {
            throw new IllegalArgumentException("Cannot add doctor. Specialty does not match the clinic's department.");
        }
    }

    public void deleteDoctor(Doctor doctor) {
        
        for (AppointmentSlot appoinmtent : appointmentManagerConnection.getAllAppointmentsForDoctor(doctor.getId())) {
            appointmentManagerConnection.cancelAppointment(doctor.getId(), appoinmtent.getEndTime());
        }
        this.doctors.remove(doctor);
    }

    public List<Doctor> getDoctors() {
        return new ArrayList<>(this.doctors); // Return a copy to prevent external modification
    }

    public Department getDepartmentKey() {
        return departmentKey;
    }
    
    
    //-----------------------------------------------------------------
    
    public void setAppointmentManager(AppointmentManager managerObejct){
        appointmentManagerConnection =managerObejct;
    }
    
    public List<AppointmentSlot> getDoctorsNearestAppointment(){
        List<AppointmentSlot> aptList=new ArrayList<>();
        for (Doctor doctor : doctors) {
            AppointmentSlot slot =appointmentManagerConnection.getNearestAvailableAppointmentSlot(doctor.getId());
            if (slot!=null) {
                aptList.add(slot);
            }
        }
        aptList.sort(Comparator.comparing(AppointmentSlot::getTime));
        return aptList;
    }
        
    
}
