package hospitalmanagementsystemwithtreemap;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class AppointmentManager extends AppointmentService {

    ClinicsManager clinicManager;
    HistoryOfPatient patientsHistory;

    public AppointmentManager(List<Doctor> doctors, HistoryOfPatient patientsHistory, hospitalmanagementsystemwithtreemap.ClinicsManager manager) {
        super(doctors);
        clinicManager = manager;
        this.patientsHistory = patientsHistory;
    }

    public void passedAppointmentHandler(AppointmentSlot appointment) {
        TreeMap<LocalDateTime, AppointmentSlot> existingSlots = doctorsAppointments.get(appointment.getDoc().getId());
        if (existingSlots != null) {
            if (existingSlots.containsKey(appointment.getTime()) && existingSlots.get(appointment.getTime()).equals(appointment)) {
                if (!appointment.isPatientCome()) {
                    appointment.getPatient().addNotification("You miss the appointment!");
                    existingSlots.remove(appointment.getTime());
                    appointment.getPatient().deleteAppointment(appointment);
                    return;
                }

                History event=new History(appointment.getTime(),appointment.getDoc(),appointment.getPatient(),appointment.getDiagnosis());
                patientsHistory.addHistory(appointment.getPatient(), event);
                
                clinicManager.getClinic(appointment.clinic).addHistoryToDoctor(event);
                existingSlots.remove(appointment.getTime());
                appointment.getPatient().deleteAppointment(appointment);
                appointment.getPatient().addNotification("Thank you for coming appointment!");

            }
        }
    }

    public void passedAppointmentHandler() {
        LocalDateTime currentTime = LocalDateTime.now().minusDays(1);

        // Iterate through each doctor's appointments
        for (Map.Entry<Integer, TreeMap<LocalDateTime, AppointmentSlot>> doctorEntry : doctorsAppointments.entrySet()) {
            TreeMap<LocalDateTime, AppointmentSlot> appointments = doctorEntry.getValue();

            // Create a list to store appointments that need to be removed
            List<LocalDateTime> toRemove = new ArrayList<>();

            // Check each appointment
            for (Map.Entry<LocalDateTime, AppointmentSlot> appointmentEntry : appointments.entrySet()) {
                LocalDateTime appointmentTime = appointmentEntry.getKey();
                AppointmentSlot slot = appointmentEntry.getValue();

                // If the appointment end time has passed
                if (slot.getEndTime().isBefore(currentTime.minusDays(1))) {
                    // Add to removal list
                    toRemove.add(appointmentTime);

                    // If the appointment was booked, add it to patient's history
                    if (slot.isBooked() && slot.getPatient() != null) {
                        String note="Undecleared Appointmet!";
                        History event=new History(slot.getTime(),slot.getDoc(),slot.getPatient(),slot.getDiagnosis(),note);
                        patientsHistory.addHistory(slot.getPatient(), event);
                      
                        clinicManager.getClinic(slot.clinic).addHistoryToDoctor(event);
                        // Remove from patient's active appointments
                        slot.getPatient().deleteAppointment(slot);

                    }
                }
            }

            // Remove all passed appointments
            for (LocalDateTime time : toRemove) {
                appointments.remove(time);
            }

        }
    }

    public void createDoctorAppointments(Doctor doctor, int days, int appointmentDurationMinutes) {

        if (days <= 0 || days > 5) {
            throw new IllegalArgumentException("Invalid number of days. Choose between 1-5.");
        }
        if (appointmentDurationMinutes != 10 && appointmentDurationMinutes != 15 && appointmentDurationMinutes != 30) {
            throw new IllegalArgumentException("Invalid appointment duration. Choose 10, 15, or 30 minutes.");
        }

        LocalDate startDate = LocalDate.now().plusDays(1);

        // Find the furthest existing appointment date
        TreeMap<LocalDateTime, AppointmentSlot> existingSlots = doctorsAppointments.get(doctor.getId());
        if (existingSlots != null && !existingSlots.isEmpty()) {
            Optional<LocalDate> lastAppointmentDate = existingSlots.keySet().stream()
                    .map(LocalDateTime::toLocalDate)
                    .max(Comparator.naturalOrder());

            if (lastAppointmentDate.isPresent()) {
                startDate = lastAppointmentDate.get().plusDays(1); // Start from the day AFTER the last existing appointment
            }
        }
        LocalDate endDate = startDate;

        for (int i = 1; i <= days; i++) {
            if (endDate.getDayOfWeek() == DayOfWeek.SATURDAY || endDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                i--;
            }
            endDate = endDate.plusDays(1); // Calculate the CORRECT end date
        }

        while (startDate.isBefore(endDate)) {
            if (startDate.getDayOfWeek() != DayOfWeek.SATURDAY && startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                LocalTime startTime = LocalTime.of(9, 0);
                LocalTime endTime = LocalTime.of(17, 0);
                LocalTime currentTime = startTime;

                while (currentTime.isBefore(endTime)) {
                    if (!(currentTime.isAfter(LocalTime.of(12, 0)) && currentTime.isBefore(LocalTime.of(13, 0)))) {
                        LocalDateTime appointmentTime = LocalDateTime.of(startDate, currentTime);
                        AppointmentSlot appointmentSlot = new AppointmentSlot(doctor, doctor.getName(), appointmentTime, appointmentTime.plusMinutes(appointmentDurationMinutes));
                        createAppoinmentSlot(doctor.getId(), appointmentSlot);
                    }
                    currentTime = currentTime.plusMinutes(appointmentDurationMinutes);
                }
            }
            startDate = startDate.plusDays(1);
        }
    }

    public void changeAppointmentDuration(Doctor doctor, int appointmentDurationMinutes, LocalDateTime date) {
        if (appointmentDurationMinutes != 10 && appointmentDurationMinutes != 15 && appointmentDurationMinutes != 30) {
            throw new IllegalArgumentException("Invalid appointment duration. Choose 10, 15, or 30 minutes.");
        }

        cancelDoctorDay(doctor.getId(), date);

        //Recreate new appointments for that day
        LocalTime startTime = LocalTime.of(9, 0); // Start at 9:00 AM
        LocalTime endTime = LocalTime.of(17, 0);   // End at 5:00 PM
        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            if (!(currentTime.isAfter(LocalTime.of(12, 0)) && currentTime.isBefore(LocalTime.of(13, 0)))) { // Exclude break time
                LocalDateTime appointmentTime = LocalDateTime.of(date.toLocalDate(), currentTime);
                AppointmentSlot appointmentSlot = new AppointmentSlot(doctor, doctor.getName(), appointmentTime, appointmentTime.plusMinutes(appointmentDurationMinutes));
                createAppoinmentSlot(doctor.getId(), appointmentSlot);
            }
            currentTime = currentTime.plusMinutes(appointmentDurationMinutes);
        }
    }

    public void cancelDoctorDay(int doctorID, LocalDateTime date) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.get(doctorID);
        if (slots == null) {
            return; // No appointments for this doctor
        }

        // Find appointments for the specific date
        List<AppointmentSlot> appointmentsToCancel = slots.entrySet().stream()
                .filter(entry -> entry.getKey().toLocalDate().equals(date.toLocalDate()))
                .map(Map.Entry::getValue)
                .toList();

        // Cancel old appointments and notify patients
        for (AppointmentSlot slot : appointmentsToCancel) {
            if (slot.isBooked()) {
                slot.getPatient().addNotification("Your appointment with Dr. " + slot.getDocName()
                        + " on " + slot.getTime() + " has been canceled by the doctor.");
                slot.getPatient().getPatientAppointments().removeIf(apt->apt.getTime().toLocalDate().isEqual(date.toLocalDate()));

            }
            cancelAppointment(doctorID, slot.getTime()); // Cancel the appointment
        }
    }

    /**
     *
     * @param doctorID
     * @param patient
     * @param time
     */
    @Override
    public void bookAppointment(int doctorID, Patient patient, LocalDateTime time) {
        try {
            if (hasAppointmentOnSameDay(patient, doctorID, time)) {
                throw new IllegalArgumentException("\n Patient already has an appointment with this doctor on the same day.");
            }
            super.bookAppointment(doctorID, patient, time); // Use the inherited method
        } catch (IllegalArgumentException e) {
            throw(e);
        }
    }

    private boolean hasAppointmentOnSameDay(Patient patient, int doctor, LocalDateTime time) {
        List<AppointmentSlot> patientAppointments = patient.getPatientAppointments();
        if (patientAppointments == null || patientAppointments.isEmpty()) {
            return false; // No appointments at all
        }

        return patientAppointments.stream().anyMatch(appointment
                -> appointment.getDoc().getId() == doctor
                && appointment.getTime().toLocalDate().equals(time.toLocalDate())
        );
    }

    public boolean cancelPatientAppointment(Patient patient, AppointmentSlot appointment) {
        TreeMap<LocalDateTime, AppointmentSlot> existingSlots = doctorsAppointments.get(appointment.getDoc().getId());
        if (existingSlots != null) {
            if (existingSlots.containsKey(appointment.getTime()) && existingSlots.get(appointment.getTime()).equals(appointment)) {
                existingSlots.get(appointment.getTime()).booked(null, null);
                existingSlots.get(appointment.getTime()).isbooked = false;

                patient.getPatientAppointments().remove(appointment);
                patient.addNotification("Appointment cancelled succesfully!");
                return true;
            }
        }
        patient.addNotification("Appointment couldn't cancel succesfuly!");
        return false;
    }

    public List<AppointmentSlot> viewTakenDoctorAppointments(Doctor selectedDoctor) {
        TreeMap<LocalDateTime, AppointmentSlot> doctorSlots = doctorsAppointments.get(selectedDoctor.getId());
        if (doctorSlots == null || doctorSlots.isEmpty()) {
            throw new IllegalArgumentException("No appointments found for this doctor.");
        }
        List<AppointmentSlot> takenAppointments = new ArrayList<>();
        for (Map.Entry<LocalDateTime, AppointmentSlot> entry : doctorSlots.entrySet()) {
            AppointmentSlot slot = entry.getValue();
            if (slot.isBooked()) { // Check if appointment is booked
                takenAppointments.add(slot);
            }
        }
        return takenAppointments;
    }

}
