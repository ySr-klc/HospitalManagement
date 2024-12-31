package Persons;

import Appointments.AppointmentSlot;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Represents a patient with a name, a list of appointments, and a stack of notifications.
 */
public class Patient extends Person {

    // List of appointments scheduled for the patient.
    private final List<AppointmentSlot> patientAppointments;

    // Stack to store notifications for the patient (e.g., appointment reminders, cancellations).
    private final Stack<String> notifications;

    /**
     * Creates a new Patient object with the given name. Initializes an empty list of appointments and an empty notification stack.
     *
     * @param name The patient's name.
     */
    public Patient(String name) {
        super(name); // Call the constructor of the parent class (Person).
        patientAppointments = new LinkedList<>(); // Use LinkedList for efficient insertions/deletions.
        notifications = new Stack<>();
    }

    /**
     * Adds a notification to the patient's notification stack.
     *
     * @param notification The notification message to add.
     */
    public void addNotification(String notification) {
        getNotifications().push(notification); // Push the notification onto the stack.
    }

    /**
     * Gets the patient's notification stack.
     *
     * @return The stack of notification messages.
     */
    public Stack<String> getNotifications() {
        return notifications;
    }

    /**
     * Adds an appointment to the patient's appointment list and sorts the list by appointment time.
     *
     * @param apt The AppointmentSlot object to add.
     */
    public void addAppointment(AppointmentSlot apt) {
        this.getPatientAppointments().add(apt);
        getPatientAppointments().sort(Comparator.comparing(AppointmentSlot::getTime)); // Sort appointments by time.
    }

    /**
     * Removes an appointment from the patient's appointment list.
     *
     * @param apt The AppointmentSlot object to remove.
     */
    public void deleteAppointment(AppointmentSlot apt) {
        getPatientAppointments().remove(apt);
    }

    /**
     * Gets the patient's list of appointments.
     *
     * @return The list of AppointmentSlot objects for this patient.
     */
    public List<AppointmentSlot> getPatientAppointments() {
        return patientAppointments;
    }
}