package Appointments;

import Clinics.Department;
import Persons.Doctor;
import History.History;
import History.HistoryOfPatient;
import Persons.Patient;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * This subclass of the Appointment Service is responsible for implementing the
 * business logic governing appointments. It manages appointment outcomes and
 * provides doctors with the ability to create, delete, and reschedule
 * appointments. This class also handles patient-side operations such as booking
 * and canceling appointments. Several key business rules are enforced here:
 * doctors cannot schedule appointments more than 15 days in advance; patients
 * are prevented from having conflicting appointments; and patients are limited
 * to one appointment per day.
 *
 */
public class AppointmentManager extends AppointmentService {

    public AppointmentManager(List<Doctor> doctors) {
        super(doctors);// Call the constructor of the parent class (AppointmentService)
    }

    /**
     * Handles appointments that have passed the scheduled time. - Checks if the
     * patient came for the appointment. - If the patient didn't come, adds a
     * notification and removes the appointment. - If the patient came, creates
     * a history entry and removes the appointment.
     *
     * @param appointment The AppointmentSlot object representing the passed
     * appointment
     */
    public void passedAppointmentHandler(AppointmentSlot appointment) {
        TreeMap<LocalDateTime, AppointmentSlot> existingSlots = doctorsAppointments.get(appointment.getDoc().getId());
        if (existingSlots != null) {
            if (existingSlots.containsKey(appointment.getTime()) && existingSlots.get(appointment.getTime()).equals(appointment)) {
                if (!appointment.hasPatientAttended()) {
                    appointment.getPatient().addNotification("You miss the appointment!");
                    existingSlots.remove(appointment.getTime());
                    appointment.getPatient().deleteAppointment(appointment);
                    return;
                }
                History event = new History(appointment.getTime(), appointment.getDoc(), appointment.getPatient(), appointment.getDiagnosis());
                HistoryOfPatient.addHistory(appointment.getPatient(), event);
                existingSlots.remove(appointment.getTime());
                appointment.getPatient().deleteAppointment(appointment);
                appointment.getPatient().addNotification("Thank you for coming appointment!");
            }
        }
    }

    /**
     * Creates doctor appointments for the specified number of days, excluding
     * weekends and lunch break.
     *
     * @param doctor The doctor for whom appointments are being created
     * @param days The number of days to schedule appointments (1-5)
     * @param appointmentDurationMinutes The duration of each appointment (10,
     * 15, or 30 minutes)
     * @throws IllegalArgumentException If the number of days is invalid (not
     * between 1 and 5) or the appointment duration is invalid (not 10, 15, or
     * 30 minutes)
     * @throws ArrayIndexOutOfBoundsException If the end date for appointments
     * is more than 15 days after today
     */
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
        if (endDate.isAfter(LocalDate.now().plusDays(15))) {
            throw new ArrayIndexOutOfBoundsException("You can schedule appointments up to 15 days later!");
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
                        createAppointmentSlot(doctor.getId(), appointmentSlot);
                    }
                    currentTime = currentTime.plusMinutes(appointmentDurationMinutes);
                }
            }
            startDate = startDate.plusDays(1);
        }
    }

    /**
     * Changes the appointment duration for a given doctor on a specific date.
     *
     * @param doctor The doctor whose appointment duration is being changed.
     * @param appointmentDurationMinutes The new duration of appointments (10,
     * 15, or 30 minutes).
     * @param date The date for which the appointment duration is being changed.
     * @throws IllegalArgumentException If no appointments exist on the given
     * date or the duration is invalid.
     */
    public void changeAppointmentDuration(Doctor doctor, int appointmentDurationMinutes, LocalDateTime date) {

        boolean dayExists = doctorsAppointments.get(doctor.getId()).keySet().stream()
                .anyMatch(localDateTime -> localDateTime.toLocalDate().equals(date.toLocalDate()));

        if (!dayExists) {
            throw new IllegalArgumentException("You don't set any appointment at given date, first create appointment day please!");
        }
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
                createAppointmentSlot(doctor.getId(), appointmentSlot);
            }
            currentTime = currentTime.plusMinutes(appointmentDurationMinutes);
        }
    }

    /**
     * Cancels all appointments for a given doctor on a specific date.
     *
     * @param doctorID The ID of the doctor whose appointments are being
     * cancelled.
     * @param date The date for which appointments are being cancelled.
     */
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
            cancelAppointment(doctorID, slot.getTime()); // Cancel the appointment
        }
    }

    /**
     * Books an appointment for a patient with a specific doctor at a given
     * time. Overrides the superclass method to add a same-day appointment
     * check.
     *
     * @param doctorID The ID of the doctor.
     * @param patient The patient booking the appointment.
     * @param time The desired appointment time.
     * @throws IllegalArgumentException If the patient already has an
     * appointment with the same clinic on the same day.
     */
    @Override
    public void bookAppointment(int doctorID, Patient patient, LocalDateTime time) {
        if (hasAppointmentOnSameDay(patient, doctorID, time)) {
            throw new IllegalArgumentException("\n Patient already has an appointment with same clinic on the same day.");
        }
        super.bookAppointment(doctorID, patient, time); // Use the inherited method

    }

    /**
     * Checks if a patient already has an appointment with the same clinic on
     * the same day.
     *
     * @param patient The patient to check.
     * @param doctor The doctor for the appointment.
     * @param time The appointment time.
     * @return True if the patient has an appointment on the same day with the
     * same clinic, false otherwise.
     */
    private boolean hasAppointmentOnSameDay(Patient patient, int doctor, LocalDateTime time) {
        List<AppointmentSlot> patientAppointments = patient.getPatientAppointments();
        if (patientAppointments == null || patientAppointments.isEmpty()) {
            return false; // No appointments at all
        }
        Department clinic = doctorsAppointments.get(doctor).get(time).getClinic();
        return patientAppointments.stream().anyMatch(appointment
                -> appointment.getClinic().equals(clinic)
                && appointment.getTime().toLocalDate().equals(time.toLocalDate())
        );
    }

    /**
     * Cancels a specific appointment for a patient.
     *
     * @param patient The patient whose appointment is being cancelled.
     * @param appointment The appointment to cancel.
     */
    public void cancelPatientAppointment(Patient patient, AppointmentSlot appointment) {
        TreeMap<LocalDateTime, AppointmentSlot> existingSlots = doctorsAppointments.get(appointment.getDoc().getId());
        if (existingSlots != null) {
            if (existingSlots.containsKey(appointment.getTime()) && existingSlots.get(appointment.getTime()).equals(appointment)) {
                existingSlots.get(appointment.getTime()).booked(null, null);
                existingSlots.get(appointment.getTime()).setIsbooked(false);

                patient.getPatientAppointments().remove(appointment);
                patient.addNotification("Appointment cancelled succesfully!");
                return;
            }
        }
        patient.addNotification("Appointment couldn't cancel succesfuly!");
    }

    /**
     * Views all taken (booked) appointments for a selected doctor.
     *
     * @param selectedDoctor The doctor whose taken appointments are being
     * viewed.
     * @return A list of taken AppointmentSlot objects.
     * @throws IllegalArgumentException If no appointments are found for the
     * doctor.
     */
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
