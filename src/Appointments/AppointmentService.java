package Appointments;

import Persons.Doctor;
import DoctorListUpdater.DoctorObserver;
import Persons.Patient;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Manages doctor appointments using a Trie-based data structure (not used in
 * this implementation) for efficient lookup. Each doctor has their own TreeMap
 * of appointment slots ordered by time.
 */
public class AppointmentService implements DoctorObserver {

    /**
     * Main data structure: HashMap storing TreeMaps of appointments for each
     * doctor. - HashMap key: Doctor ID (unique identifier for each doctor) -
     * HashMap value: TreeMap of appointments for the specific doctor - TreeMap
     * key: Appointment time (LocalDateTime object) - TreeMap value:
     * AppointmentSlot object containing details about the appointment
     */
    protected HashMap<Integer, TreeMap<LocalDateTime, AppointmentSlot>> doctorsAppointments;

    //================================================================================
    // Constructor and Doctor List Management
    //================================================================================
    /**
     * Initializes the appointment manager with a list of doctors. Creates an
     * empty appointment map for each doctor.
     *
     * @param doctors List of doctors to initialize
     */
    public AppointmentService(List<Doctor> doctors) {
        doctorsAppointments = new HashMap<>();

        for (Doctor doctor : doctors) {
            this.doctorsAppointments.put(
                    doctor.getId(),
                    new TreeMap<>(LocalDateTime::compareTo)
            );
        }
    }

    /**
     * Updates the doctor list by adding new doctors if they don't exist. Does
     * not modify existing doctor appointments.
     *
     * @param doctors List of doctors to update/add
     */
    @Override
    public void update(List<Doctor> doctors) {
        for (Doctor doctor : doctors) {
            doctorsAppointments.putIfAbsent(
                    doctor.getId(),
                    new TreeMap<>(LocalDateTime::compareTo)
            );
        }
    }
/**
 * Deletes doctor if it is exist but first checks if doctor have appointment or not
 * if doctor have appointments first cancel appointment then send notification to user
 * 
 * @param doctor Takes doctor that want to delete
 */
   @Override
public void delete(Doctor doctor) {
    //  Check if appointments exist for the given doctor.
    if (doctorsAppointments.containsKey(doctor.getId())) {
        //  Retrieve the TreeMap of appointments for the doctor.
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.get(doctor.getId());

        //  Check if the retrieved TreeMap is not empty AND not null (redundant check, see explanation below).
        if (!slots.isEmpty()&& null != slots) {
            // Iterate through all AppointmentSlot values in the TreeMap.
            for (AppointmentSlot slot : slots.values()) {
                //  Check if the current slot is booked.
                if (slot.isBooked()) {
                    //  If the slot is booked, cancel the appointment.
                    cancelAppointment(slot.getDoc().getId(), slot.getTime());
                }
            }
        }

        //  Remove the doctor's appointments from the main map.
        doctorsAppointments.remove(doctor.getId());
    }
}

    //================================================================================
    // Appointment Management Methods
    //================================================================================
    /**
     * Creates a new appointment slot for a doctor at a specific time.
     *
     * @param doctorID The ID of the doctor for whom the appointment is being
     * created.
     * @param appointment AppointmentSlot object containing details about the
     * appointment.
     */
    public void createAppointmentSlot(int doctorID, AppointmentSlot appointment) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.computeIfAbsent(doctorID, k -> new TreeMap<>(LocalDateTime::compareTo));
        slots.put(appointment.getTime(), appointment); // Use put to add to TreeMap
    }

    /**
     * Cancels an appointment for a specific doctor at a specific time.
     *
     * @param doctorID The ID of the doctor for whom the appointment is being
     * cancelled.
     * @param aptHour The LocalDateTime object representing the appointment time
     * to cancel.
     */
    public void cancelAppointment(int doctorID, LocalDateTime aptHour) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.get(doctorID);
        if (slots != null) {
            AppointmentSlot removedSlot = slots.remove(aptHour);
            if (removedSlot != null && removedSlot.getPatient() != null) {
                removedSlot.getPatient().deleteAppointment(removedSlot);
                removedSlot.getPatient().addNotification("Your appointment canceled by doctor:" + removedSlot.getDocName() + " Date:" + removedSlot.getTime() + " Clinic" + removedSlot.getDoc().getSpeciality());
            }
        }
    }

    /**
     * Books an appointment for a patient with a specific doctor at a given
     * time.
     *
     * @param doctorID The ID of the doctor for whom the appointment is being
     * booked.
     * @param patient The Patient object for whom the appointment is booked.
     *
     * @param time The LocalDateTime object representing the desired appointment
     * time.
     * @throws IllegalArgumentException If the doctor ID is not found, the
     * patient already has an appointment within 30 minutes of the requested
     * time, or the slot is not available.
     */
   public void bookAppointment(int doctorID, Patient patient, LocalDateTime time) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.get(doctorID);

        if (slots == null) {
            throw new IllegalArgumentException("\n Doctor ID not found: " + doctorID);
        }
        if (patient.getPatientAppointments().stream()
            .anyMatch(o -> (o.getTime().isEqual(time) || (o.getTime().isAfter(time.minusMinutes(30)) && o.getTime().isBefore(time.plusMinutes(30)))))) {
            throw new IllegalArgumentException("\n You already have an appointment within 30 minutes of this time!");
        }

        AppointmentSlot targetSlot = slots.get(time); // Directly get the slot

        if (targetSlot != null && !targetSlot.isBooked()) {
            targetSlot.booked(patient, patient.getName());
            patient.addAppointment(targetSlot);
        } else {
            throw new IllegalArgumentException("\nNo available slot at " + time);
        }
    }

    //================================================================================
    // Appointment Retrieve Methods
    //================================================================================
     /**
     * Gets the nearest available appointment slot for a given doctor.
     *
     * @param doctorID The ID of the doctor.
     * @return The nearest available AppointmentSlot, or null if none are found.
     */
    public AppointmentSlot getNearestAvailableAppointmentSlot(int doctorID) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.get(doctorID);
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
     * Gets all available (not booked and in the future) appointments for a given doctor.
     *
     * @param doctorID The ID of the doctor.
     * @return A list of available AppointmentSlot objects, or null if no appointments exist for the doctor.
     */
    public List<AppointmentSlot> getAvailableAppointments(int doctorID) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.get(doctorID);
        if (slots == null || slots.isEmpty()) {
            return null;
        }

        return slots.entrySet().stream()
                .filter(entry -> !entry.getValue().isBooked() && entry.getKey().isAfter(LocalDateTime.now()))
                .map(Map.Entry::getValue).toList();
    }
    
     /**
     * Gets all appointments (booked and available) for a given doctor.
     *
     * @param doctorID The ID of the doctor.
     * @return A list of all AppointmentSlot objects for the doctor. Returns an empty list if the doctor has no appointments.
     */
    public List<AppointmentSlot> getAllAppointmentsForDoctor(int doctorID) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.get(doctorID);
        if (slots == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(slots.values()); // Get values directly from TreeMap
    }
}
