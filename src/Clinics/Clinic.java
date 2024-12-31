package Clinics;

import Appointments.AppointmentManager;
import Appointments.AppointmentSlot;
import Persons.Doctor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Clinic {

    // A set to store all doctors associated with this clinic.
    private final Set<Doctor> doctors;

    // Connection to the AppointmentManager for appointment-related operations.
    private AppointmentManager appointmentManagerConnection;

    // The department this clinic belongs to (e.g., Cardiology, Pediatrics).
    private final Department departmentKey;

    /**
     * Creates a new Clinic with the given department and a collection of doctors.
     *
     * @param departmentKey The department this clinic belongs to.
     * @param doctor         A collection of doctors to associate with the clinic.
     */
    public Clinic(Department departmentKey, Collection<Doctor> doctor) {

        this.departmentKey = departmentKey;
        // Use HashSet for efficient lookups and to avoid duplicate doctors.
        this.doctors = new HashSet<>(doctor);

    }

    /**
     * Adds a doctor to the clinic if their specialty matches the clinic's department.
     *
     * @param doctor The doctor to add.
     * @throws IllegalArgumentException If the doctor's specialty doesn't match the clinic's department.
     */
    public void addDoctor(Doctor doctor) {
        if (doctor.getSpeciality().equals(getDepartmentKey())) {
            // Use Set's add method to check if the doctor was added (not already present).
            if (doctors.add(doctor)) {
                // Doctor was added successfully (wasn't already present).
                // You can optionally add a message here.
            } else {
                // Doctor was already in the set, no action needed. You can optionally log a message.
            }
        } else {
            throw new IllegalArgumentException("Cannot add doctor. Specialty does not match the clinic's department.");
        }
    }
    
    //===========================================================
    //Doctor List Management Methods
    //===========================================================


    /**
     * Removes a doctor from the clinic.
     *
     * @param doctor The doctor to remove.
     */
    public void deleteDoctor(Doctor doctor) {
        this.doctors.remove(doctor);
    }

    /**
     * Returns a copy of the list of doctors associated with this clinic.
     *
     * @return An unmodifiable list of doctors.
     */
    public List<Doctor> getDoctors() {
        return new ArrayList<>(this.doctors); // Return a copy to avoid modifying the original set.
    }

    /**
     * Gets the department this clinic belongs to.
     *
     * @return The department of the clinic.
     */
    public Department getDepartmentKey() {
        return departmentKey;
    }

    //===========================================================
    //Appoinment Manager Related Methods
    //===========================================================

    /**
     * Finds the nearest available appointment slot for each doctor in the clinic.
     *
     * @return A list of AppointmentSlot objects, sorted by appointment time (nearest first).
     *         Returns an empty list if no appointments are available for any doctors.
     */
    public List<AppointmentSlot> getDoctorsNearestAppointment() {
        List<AppointmentSlot> aptList = new ArrayList<>();
        for (Doctor doctor : getDoctors()) {
            // Use AppointmentManager to find the nearest available slot for this doctor.
            AppointmentSlot slot = getAppointmentManagerConnection().getNearestAvailableAppointmentSlot(doctor.getId());
            if (slot != null) {
                aptList.add(slot);
            }
        }
        // Sort the list of appointments by time (nearest first).
        aptList.sort(Comparator.comparing(AppointmentSlot::getTime));
        return aptList;
    }

    /**
     * Sets the connection to the AppointmentManager for appointment-related operations.
     *
     * @param managerObejct The AppointmentManager object to connect to.
     */
    public void setAppointmentManager(AppointmentManager managerObejct) {
        appointmentManagerConnection = managerObejct;
    }

    /**
     * In order to show doctors appointments from clinic we need make connection to appoinment service
     * @return the appointmentManagerConnection
     */
    public AppointmentManager getAppointmentManagerConnection() {
        return appointmentManagerConnection;
    }

}