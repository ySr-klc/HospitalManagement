package HospitalManagmentSystem;
import java.time.LocalDateTime;
import java.util.*;
/**
 * Manages doctor appointments using a Trie-based data structure for efficient
 * lookup. Each doctor has their own TreeMap of appointment slots ordered by
 * time.
 */
public class AppointmentService implements DoctorObserver{
    /**
     * Main data structure: Trie storing TreeMaps of appointments for each
     * doctor
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

    @Override
 public void delete(Doctor doctor) {
  if (doctorsAppointments.containsKey(doctor.getId())) {
    TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.get(doctor.getId());

    // Cancel all appointments before removing the doctor
    for (AppointmentSlot slot : slots.values()) {
      if (slot.isBooked()) {
          cancelAppointment(slot.getDoc().getId(), slot.getTime());
      }
    }

    doctorsAppointments.remove(doctor.getId());
  }
}
    public void createAppointmentSlot(int doctorID, AppointmentSlot appointment) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.computeIfAbsent(doctorID, k -> new TreeMap<>(LocalDateTime::compareTo));
        slots.put(appointment.getTime(), appointment); // Use put to add to TreeMap
    }
    public void cancelAppointment(int doctorID, LocalDateTime aptHour) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.get(doctorID);
        if (slots != null) {
            AppointmentSlot removedSlot = slots.remove(aptHour);
            if (removedSlot != null && removedSlot.getPatient() != null) {
                removedSlot.getPatient().deleteAppointment(removedSlot);
                removedSlot.getPatient().addNotification("Your appointment canceled by doctor:" + removedSlot.getDocName() + " Date:" + removedSlot.getTime() + " Clinic" + removedSlot.doc.getSpeciality());
            }
        }
    }
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
    public List<AppointmentSlot> getAvailableAppointments(int doctorID) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.get(doctorID);
        if (slots == null || slots.isEmpty()) {
            return null;
        }

        return slots.entrySet().stream()
                .filter(entry -> !entry.getValue().isBooked() && entry.getKey().isAfter(LocalDateTime.now()))
                .map(Map.Entry::getValue).toList();
    }
    public List<AppointmentSlot> getAllAppointmentsForDoctor(int doctorID) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.get(doctorID);
        if (slots == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(slots.values()); // Get values directly from TreeMap
    }
}
