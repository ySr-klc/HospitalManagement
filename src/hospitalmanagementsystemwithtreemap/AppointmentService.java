package hospitalmanagementsystemwithtreemap;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * Manages doctor appointments using a Trie-based data structure for efficient lookup.
 * Each doctor has their own TreeMap of appointment slots ordered by time.
 */
public class AppointmentService {

    /** Main data structure: Trie storing TreeMaps of appointments for each doctor */
    protected TrieWithGenericType<TreeMap<LocalDateTime, AppointmentSlot>> doctorsAppointments;

    //================================================================================
    // Constructor and Doctor List Management
    //================================================================================

    /**
     * Initializes the appointment manager with a list of doctors.
     * Creates an empty appointment map for each doctor.
     *
     * @param doctors List of doctors to initialize
     */
    public AppointmentService(List<Doctor> doctors) {
        doctorsAppointments = new TrieWithGenericType<>();
        for (Doctor doctor : doctors) {
            this.doctorsAppointments.insert(
                    doctor.name,
                    new TreeMap<>((time1, time2) -> time1.compareTo(time2))
            );
        }
    }

    /**
     * Updates the doctor list by adding new doctors if they don't exist.
     * Does not modify existing doctor appointments.
     *
     * @param doctors List of doctors to update/add
     */
    public void updateDoctorList(List<Doctor> doctors) {
        for (Doctor doctor : doctors) {
            if (!doctorsAppointments.isExist(doctor.name)) {
                doctorsAppointments.insert(
                        doctor.name,
                        new TreeMap<>((time1, time2) -> time1.compareTo(time2))
                );
            }
        }
    }

    /**
     * Removes a doctor and all their appointments from the system.
     *
     * @param doctor The doctor to remove
     */
    public void deleteDoctorFromList(Doctor doctor) {
        if (doctorsAppointments.isExist(doctor.name)) {
            doctorsAppointments.delete(doctor.name);
        }
    }

    /**
     * Clears all trie
     * when there is no pointer that keeps doctors list then it will removed by garbage collector
     */
    public void deleteAllDoctorList() {
        doctorsAppointments = new TrieWithGenericType<>();
    }

    //================================================================================
    // Appointment Retrieval
    //================================================================================

    /**
     * Gets the nearest available appointment slot for each doctor.
     * Returns a sorted list of all nearest available slots.
     *
     * @return List of nearest available slots for all doctors, sorted by time
     */

public List<AppointmentSlot> getAllDoctorsNearestAppointmentDatesByTimeOrder() {
    List<AppointmentSlot> allNearestSlots = new ArrayList<>();

    for (String doctorName : doctorsAppointments.getAll()) {
        TreeMap<LocalDateTime, AppointmentSlot> doctorSlots = doctorsAppointments.searchExact(doctorName);

        if (doctorSlots != null && !doctorSlots.isEmpty()) {
            AppointmentSlot nearestSlot = doctorSlots.entrySet().stream()
                    .filter(entry -> !entry.getValue().isBooked() && entry.getKey().isAfter(LocalDateTime.now().plusHours(1)))
                    .map(Map.Entry::getValue) 
                    .findFirst()
                    .orElse(null);

            if (nearestSlot != null) {
                allNearestSlots.add(nearestSlot);
            }
        }
    }
    allNearestSlots.sort((c,c2)-> c.time.compareTo(c2.time));
    return allNearestSlots;
}

    public List<String> findDoctor(String word) {
        return doctorsAppointments.searchSimilar(word);
    }

    public void canceledByPatient(Patient pat, AppointmentSlot apt) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.searchExact(apt.getDocName());
        if (slots != null) {
            slots.remove(apt.getTime());
        }
        // maybe doctor notification add on here
    }

    //================================================================================
    // Appointment Slot Management
    //================================================================================

    /**
     * Creates a new appointment slot for a doctor.
     * If the doctor doesn't exist, creates a new map for them.
     *
     * @param doctor      The doctor for whom to create the appointment
     * @param appointment The appointment slot to add
     */
    public void createAppoinmentSlot(Doctor doctor, AppointmentSlot appointment) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.searchExact(doctor.name);
        if (slots == null) {
            slots = new TreeMap<>((time1, time2) -> time1.compareTo(time2));
            doctorsAppointments.insert(doctor.name, slots);
        }
        slots.put(appointment.getTime(), appointment); // Use put to add to TreeMap
    }

    /**
     * Cancels an appointment at the specified time.
     *
     * @param doctor      The doctor whose appointment to cancel
     * @param apmntHour The date/time of the appointment to cancel
     */
    public void cancelAppointment(Doctor doctor, LocalDateTime apmntHour) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.searchExact(doctor.getName());
        if (slots != null) {
            AppointmentSlot removedSlot = slots.remove(apmntHour);
            if (removedSlot != null && removedSlot.getPatient() != null) {
                removedSlot.getPatient().deleteAppointment(removedSlot);
                //removedSlot.getPatient().addNotfication("Your appointment canceled by doctor:" + removedSlot.getDocName() + " Date:" + removedSlot.getTime() + " Clinic" + removedSlot.doc.getSpeciality());
            }
        }
    }

    //================================================================================
    // Appointment Booking and Retrieval
    //================================================================================

    /**
     * Books an appointment for a patient with a doctor at the specified time.
     *
     * @param doctor  The doctor to book with
     * @param patient The patient booking the appointment
     * @param time    The requested appointment time
     * @throws IllegalArgumentException if doctor not found or no available slot
     */
    public void bookAppointment(Doctor doctor, Patient patient, LocalDateTime time) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.searchExact(doctor.getName());
        if (slots == null) {
            throw new IllegalArgumentException("Doctor not found: " + doctor.getName());
        }

        AppointmentSlot targetSlot = slots.get(time); // Directly get the slot

        if (targetSlot != null && !targetSlot.isBooked()) {
            targetSlot.booked(patient, patient.getName());
            patient.addAppointment(targetSlot);
        } else {
            throw new IllegalArgumentException("No available slot at " + time);
        }
    }

    /**
     * Finds the nearest available appointment slot for a doctor.
     * Only returns slots that are not booked and are in the future.
     *
     * @param doctor The doctor to find slots for
     * @return The nearest available appointment slot, or null if none found
     */
    public AppointmentSlot getNearestAvailableAppointmentSlot(Doctor doctor) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.searchExact(doctor.getName());
        if (slots == null || slots.isEmpty()) {
            return null;
        }

        return slots.entrySet().stream()
                .filter(entry -> !entry.getValue().isBooked() && entry.getKey().isAfter(LocalDateTime.now()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    /**
     * Retrieves all appointments for a specific doctor.
     *
     * @param doctor The doctor whose appointments to retrieve
     * @return List of all appointments for the doctor
     */
    public List<AppointmentSlot> getAllAppointmentsForDoctor(Doctor doctor) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.searchExact(doctor.getName());
        if (slots == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(slots.values()); // Get values directly from TreeMap
    }
}