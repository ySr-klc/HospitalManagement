package Clinics;

import Appointments.AppointmentManager;
import Appointments.AppointmentSlot;
import Persons.Doctor;
import DoctorListUpdater.DoctorObserver;

import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages a collection of clinics, each specializing in a particular department
 * (e.g., Cardiology, Pediatrics).
 */
public class ClinicsManager implements DoctorObserver {

    // A Specified Map from Java to store clinics respect to Enums, where the key is the department and the value is the corresponding Clinic object. 
    private final EnumMap<Department, Clinic> clinics;

    /**
     * Creates a ClinicsManager instance with a list of doctors.
     *
     * @param doctors A list of doctors to associate with clinics based on their
     * specialties.
     */
    public ClinicsManager(List<Doctor> doctors) {
        clinics = new EnumMap<>(Department.class); // Initialize EnumMap for efficient department lookups.

        // Group doctors by department and create clinics with them.
        for (Department department : Department.values()) {
            List<Doctor> departmentDoctors = doctors.stream()
                    .filter(doctor -> doctor.getSpeciality().equals(department)) // Filter doctors by department
                    .collect(Collectors.toList()); // Collect filtered doctors into a list

            clinics.put(department, new Clinic(department, departmentDoctors)); // Create clinic for each department
        }
    }

    /**
     * Establishes a connection between all clinics and the provided
     * AppointmentManager. This allows clinics to access appointment-related
     * functionalities.
     *
     * @param manager The AppointmentManager object to connect to.
     */
    public void connectAppointmentManager(AppointmentManager manager) {
        for (Clinic value : clinics.values()) {
            value.setAppointmentManager(manager); // Connect each clinic to the AppointmentManager.
        }
    }

    /**
     * Gets the clinic associated with the given department.
     *
     * @param clinic The department of the desired clinic.
     * @return The Clinic object for the department, or null if not found.
     */
    public Clinic getClinic(Department clinic) {
        return clinics.get(clinic);
    }

    /**
     * Retrieves a list of available appointment slots for all doctors in the
     * specified department.
     *
     * @param clinic The department of the clinic.
     * @return A list of AppointmentSlot objects for the nearest available
     * appointments, or an empty list if no appointments are available for any
     * doctors in the department.
     */
    public List<AppointmentSlot> getAppointmentsFromClinic(Department clinic) {
        return clinics.get(clinic).getDoctorsNearestAppointment();
    }

    //===========================================================
    //Observer Methods
    //===========================================================
    /**
     * Updates clinic information when the list of doctors changes (implements
     * DoctorObserver interface). Adds new doctors to their respective clinics
     * based on their specialties.
     *
     * @param doctors The updated list of doctors.
     */
    @Override
    public void update(List<Doctor> doctors) {
        for (Doctor doctor : doctors) {
            clinics.get(doctor.getSpeciality()).addDoctor(doctor);
        }
    }

    /**
     * Updates clinic information when a doctor is removed (implements
     * DoctorObserver interface). Removes the doctor from their respective
     * clinic based on their specialty.
     *
     * @param doctor The doctor to be removed.
     */
    @Override
    public void delete(Doctor doctor) {
        clinics.get(doctor.getSpeciality()).deleteDoctor(doctor);
    }

}
