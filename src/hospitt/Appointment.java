package hospitt;

import java.time.LocalDate;


class Appointment {
    LocalDate date;
    String time;
    String patientName;

    public Appointment(LocalDate date, String time) {
        this.date = date;
        this.time = time;
        this.patientName = "Available"; // "Available" indicates the slot is not booked
    }

    public boolean isBooked() {
        return !patientName.equals("Available"); // Slot is booked if patientName is not "Available"
    }

    public void bookAppointment(String patientName) {
        if (isBooked()) {
            System.out.println("Appointment already booked.");
        } else {
            this.patientName = patientName;
        }
    }

    public void cancelBooking() {
        this.patientName = "Available";
    }

    @Override
    public String toString() {
        return time + (isBooked() ? " - Booked by " + patientName : " - Available");
    }
}
