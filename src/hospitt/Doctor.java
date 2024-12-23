package hospitt;



import java.time.LocalDate;
import java.util.*;

class Doctor extends Person {
    Map<LocalDate, List<Appointment>> schedule;

    Doctor(String name) {
        super(name);
        this.schedule = new HashMap<>();
    }

    public void createAppointmentDay(LocalDate date, List<String> timeSlots) {
        List<Appointment> appointments = new ArrayList<>();
        for (String timeSlot : timeSlots) {
            appointments.add(new Appointment(date, timeSlot));
        }
        schedule.put(date, appointments);
    }

    public void viewSchedule() {
        System.out.println("Schedule for " + name + ":");
        for (LocalDate date : schedule.keySet()) {
            System.out.println(date + " (" + date.getDayOfWeek() + ")");
            for (Appointment appointment : schedule.get(date)) {
                System.out.println("  " + appointment.time + (appointment.isBooked() ? " - Booked by " + appointment.patientName : " - Available"));
            }
        }
    }

    public List<Appointment> getAvailableAppointments(LocalDate date) {
        List<Appointment> availableAppointments = new ArrayList<>();
        if (schedule.containsKey(date)) {
            for (Appointment appointment : schedule.get(date)) {
                if (!appointment.isBooked()) {
                    availableAppointments.add(appointment);
                }
            }
        }
        return availableAppointments;
    }

    public boolean addTimeSlot(LocalDate date, String time) {
        if (!schedule.containsKey(date)) {
            System.out.println("Date not found in the schedule.");
            return false;
        }
        for (Appointment appointment : schedule.get(date)) {
            if (appointment.time.equals(time)) {
                return false; // Time slot already exists
            }
        }
        schedule.get(date).add(new Appointment(date, time));
        return true;
    }

    public boolean deleteTimeSlot(LocalDate date, String time) {
        if (!schedule.containsKey(date)) {
            System.out.println("Date not found in the schedule.");
            return false;
        }
        Iterator<Appointment> iterator = schedule.get(date).iterator();
        while (iterator.hasNext()) {
            Appointment appointment = iterator.next();
            if (appointment.time.equals(time) && !appointment.isBooked()) {
                iterator.remove();
                return true;
            }
        }
        System.out.println("Time slot not found or already booked.");
        return false;
    }

    public Set<LocalDate> getUniqueAppointmentDates() {
        return schedule.keySet();
    }

    public void deleteAppointmentDay(LocalDate date) {
        if (schedule.containsKey(date)) {
            schedule.remove(date);
        }
    }

    @Override
    void displayInfo() {
        System.out.println("Doctor: " + name);
    }
}
