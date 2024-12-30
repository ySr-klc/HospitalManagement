package HospitalManagmentSystem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ClinicsManager implements DoctorObserver{

    HashMap<Department, Clinic> clinics;

    public ClinicsManager(List<Doctor> doctors) {
    clinics = (HashMap<Department, Clinic>) Arrays.stream(Department.values())
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
    
    
    public void connectAppointmentManager(AppointmentManager manager){
        for (Clinic value : clinics.values()) {
            value.setAppointmentManager(manager);
        }
    }
      
    public Clinic getClinic(Department clinic){
        return clinics.get(clinic);
    }

    public List<AppointmentSlot> getAppointmentsFromClinic(Department clinic){
     return clinics.get(clinic).getDoctorsNearestAppointment();
    }

    @Override
    public void update(List<Doctor> doctors) {
        for (Doctor doctor : doctors) {
            clinics.get(doctor.getSpeciality()).addDoctor(doctor);
        }
    }

    @Override
    public void delete(Doctor doctor) {
        clinics.get(doctor.getSpeciality()).deleteDoctor(doctor);
    }
    
    
    
    
    
}
