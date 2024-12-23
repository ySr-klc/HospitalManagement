package hospitalmanagementsystemwithtreemap;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class AppointmentManager extends AppointmentService {

    public AppointmentManager(List<Doctor> doctors) {
        super(doctors);
    }
    public static void passedAppointmentHandler(){
        
    }
    
    public void createDoctorAppointments(Doctor doctor, int days, int appointmentDurationMinutes) {
        if (days != 3 && days != 5 && days != 7) {
            throw new IllegalArgumentException("Invalid number of days. Choose 3, 5, or 7.");
        }
        if (appointmentDurationMinutes != 10 && appointmentDurationMinutes != 15 && appointmentDurationMinutes != 30) {
            throw new IllegalArgumentException("Invalid appointment duration. Choose 10, 15, or 30 minutes.");
        }

        LocalDate startDate = LocalDate.now();

        // Find the furthest existing appointment date
        TreeMap<LocalDateTime, AppointmentSlot> existingSlots = doctorsAppointments.searchExact(doctor.getName());
        if (existingSlots != null && !existingSlots.isEmpty()) {
            Optional<LocalDate> lastAppointmentDate = existingSlots.keySet().stream()
                    .map(LocalDateTime::toLocalDate)
                    .max(Comparator.naturalOrder());

            if (lastAppointmentDate.isPresent()) {
                startDate = lastAppointmentDate.get().plusDays(1); // Start from the day AFTER the last existing appointment
            }
        }

        LocalDate endDate = startDate.plusDays(days); // Calculate the CORRECT end date

        while (startDate.isBefore(endDate)) {
            if (startDate.getDayOfWeek() != DayOfWeek.SATURDAY && startDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                LocalTime startTime = LocalTime.of(9, 0);
                LocalTime endTime = LocalTime.of(17, 0);
                LocalTime currentTime = startTime;

                while (currentTime.isBefore(endTime)) {
                    if (!(currentTime.isAfter(LocalTime.of(12, 0)) && currentTime.isBefore(LocalTime.of(13, 0)))) {
                        LocalDateTime appointmentTime = LocalDateTime.of(startDate, currentTime);
                        AppointmentSlot appointmentSlot = new AppointmentSlot(doctor, doctor.getName(), appointmentTime, appointmentTime.plusMinutes(appointmentDurationMinutes));
                        createAppoinmentSlot(doctor, appointmentSlot);
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

        cancelDoctorDay(doctor, date);

        //Recreate new appointments for that day
        LocalTime startTime = LocalTime.of(9, 0); // Start at 9:00 AM
        LocalTime endTime = LocalTime.of(17, 0);   // End at 5:00 PM
        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            if (!(currentTime.isAfter(LocalTime.of(12, 0)) && currentTime.isBefore(LocalTime.of(13, 0)))) { // Exclude break time
                LocalDateTime appointmentTime = LocalDateTime.of(date.toLocalDate(), currentTime);
                AppointmentSlot appointmentSlot = new AppointmentSlot(doctor, doctor.getName(), appointmentTime, appointmentTime.plusMinutes(appointmentDurationMinutes));
                createAppoinmentSlot(doctor, appointmentSlot);
            }
            currentTime = currentTime.plusMinutes(appointmentDurationMinutes);
        }
    }

    public void cancelDoctorDay(Doctor doctor, LocalDateTime date) {
        TreeMap<LocalDateTime, AppointmentSlot> slots = doctorsAppointments.searchExact(doctor.getName());
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
                slot.getPatient().addNotfication("Your appointment with Dr. " + slot.getDocName()
                        + " on " + slot.getTime() + " has been canceled by the doctor.");

            }
            cancelAppointment(doctor, slot.getTime()); // Cancel the appointment
        }
    }

    @Override
    public void bookAppointment(Doctor doctor, Patient patient, LocalDateTime time) {
        try {
            super.bookAppointment(doctor, patient, time); // Use the inherited method
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
        
     public boolean cancelPatientAppointment(Patient patient, AppointmentSlot appointment) {
        TreeMap<LocalDateTime, AppointmentSlot> existingSlots = doctorsAppointments.searchExact(appointment.getDocName());
        if(existingSlots!=null){
            if (existingSlots.containsKey(appointment.getTime()) && existingSlots.get(appointment.getTime()).equals(appointment)) {
                existingSlots.remove(appointment.getTime());
                patient.getPatientAppoinments().remove(appointment);
                patient.addNotfication("Appointment cancelled succesfully!");
                return true; 
            }
        }
        patient.addNotfication("Appointment couldn't cancelled succesfuly!");
        return false; 
    }

}
