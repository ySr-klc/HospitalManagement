/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Stack;

// ... (Doctor, Patient, AppointmentSlot, AppointmentManager classes remain the same)

public class HospitalManagementSystemGUI extends JFrame {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final AppointmentManager appointmentManager;
    private final List<Doctor> doctors;
    private final List<Patient> patients;
    private final JTextArea outputArea;

    public HospitalManagementSystemGUI() {
        setTitle("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        doctors = new ArrayList<>();
        doctors.add(new Doctor("John", "Cardiology"));
        doctors.add(new Doctor("Sarah", "Pediatrics"));
        doctors.add(new Doctor("Michael", "Neurology"));
        doctors.add(new Doctor("Markus", "Neurology"));
        doctors.add(new Doctor("Sedric", "Pediatrics"));

        patients = new ArrayList<>();
        patients.add(new Patient("Alice"));
        patients.add(new Patient("Bob"));

        appointmentManager = new AppointmentManager(doctors);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton doctorButton = new JButton("Doctor");
        JButton patientButton = new JButton("Patient");
        buttonPanel.add(doctorButton);
        buttonPanel.add(patientButton);
        add(buttonPanel, BorderLayout.NORTH);

        doctorButton.addActionListener(e -> showDoctorMenu());
        patientButton.addActionListener(e -> showPatientMenu());

        setVisible(true);
    }

    private void showDoctorMenu() {
        // ... (Implement Doctor Menu using JDialog/JOptionPane)
        String[] options = {"Schedule Appointment", "View Current Appointments", "Back to Main Menu"};
        int choice = JOptionPane.showOptionDialog(this, "Doctor Menu", "Doctor Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0 -> showScheduleAppointmentMenu();
            case 1 -> viewDoctorAppointments();
            case 2 -> {} // Back to main
        }
    }
    private void showScheduleAppointmentMenu() {
        Doctor selectedDoctor = (Doctor) JOptionPane.showInputDialog(this, "Select a Doctor", "Select Doctor", JOptionPane.QUESTION_MESSAGE, null, doctors.toArray(), doctors.get(0));
        if (selectedDoctor == null) return;
        // ... (Implement Schedule Appointment Menu using JDialog/JOptionPane, similar structure as before)
         String[] options = {"Schedule New Days", "Cancel a Specific Day", "Cancel Specific Time Range", "Rearrange Hours Configuration", "Back to Previous Menu"};
        int choice = JOptionPane.showOptionDialog(this, "Schedule Appointment Menu", "Schedule Appointment Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0 -> scheduleNewDays(selectedDoctor);
            case 1 -> cancelSpecificDay(selectedDoctor);
            case 2 -> cancelTimeRange(selectedDoctor);
            case 3 -> rearrangeHours(selectedDoctor);
            case 4 -> {} // Back to main
        }
    }
    private void scheduleNewDays(Doctor doctor) {
        // Use JOptionPane for input
        String[] daysOptions = {"7 days", "15 days", "30 days"};
        int daysChoice = JOptionPane.showOptionDialog(this, "Choose number of days to schedule:", "Number of Days", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, daysOptions, daysOptions[0]);
        int days = 0;
        switch (daysChoice) {
            case 0 -> days = 7;
            case 1 -> days = 15;
            case 2 -> days = 30;
        }
        if (days > 0) {
            String[] durationOptions = {"10 minutes", "15 minutes", "30 minutes"};
            int durationChoice = JOptionPane.showOptionDialog(this, "Choose appointment duration:", "Appointment Duration", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, durationOptions, durationOptions[0]);
            int duration = 0;
            switch (durationChoice) {
                case 0 -> duration = 10;
                case 1 -> duration = 15;
                case 2 -> duration = 30;
            }
            if (duration > 0) {
                appointmentManager.createDoctorAppointments(doctor, days, duration);
                JOptionPane.showMessageDialog(this, "Appointments scheduled successfully!");
            }
        }
    }
    private void cancelSpecificDay(Doctor doctor) {
        String dateStr = JOptionPane.showInputDialog(this, "Enter date to cancel (yyyy-MM-dd):");
        if (dateStr == null || dateStr.isEmpty()) return;
        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalDateTime dateTime = date.atStartOfDay();
            appointmentManager.cancelDoctorDay(doctor, dateTime);
            JOptionPane.showMessageDialog(this, "Appointments for " + dateStr + " cancelled successfully!");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void cancelTimeRange(Doctor doctor) {
        String startStr = JOptionPane.showInputDialog(this, "Enter start date and time (yyyy-MM-dd HH:mm):");
        String endStr = JOptionPane.showInputDialog(this, "Enter end date and time (yyyy-MM-dd HH:mm):");
        if (startStr == null || startStr.isEmpty() || endStr == null || endStr.isEmpty()) return;

        try {
            LocalDateTime start = LocalDateTime.parse(startStr, formatter);
            LocalDateTime end = LocalDateTime.parse(endStr, formatter);
            List<AppointmentSlot> appointments = appointmentManager.getAllAppointmentsForDoctor(doctor);
            for (AppointmentSlot slot : appointments) {
                if (!slot.getTime().isBefore(start) && !slot.getTime().isAfter(end)) {
                    appointmentManager.cancelAppointment(doctor, slot.getTime());
                }
            }
            JOptionPane.showMessageDialog(this, "Appointments cancelled successfully!");
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void rearrangeHours(Doctor doctor) {
        String dateStr = JOptionPane.showInputDialog(this, "Enter date to rearrange (yyyy-MM-dd):");
        if (dateStr == null || dateStr.isEmpty()) return;
        String[] durationOptions = {"10 minutes", "15 minutes", "30 minutes"};
        int durationChoice = JOptionPane.showOptionDialog(this, "Choose new appointment duration:", "Appointment Duration", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, durationOptions, durationOptions[0]);
        int duration = 0;
        switch (durationChoice) {
            case 0 -> duration = 10;
            case 1 -> duration = 15;
            case 2 -> duration = 30;
        }
        if (duration > 0) {
            try {
                LocalDateTime dateTime = LocalDate.parse(dateStr).atStartOfDay();
                appointmentManager.changeAppointmentDuration(doctor, duration, dateTime);
                JOptionPane.showMessageDialog(this, "Appointments rearranged successfully!");
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void viewDoctorAppointments() {
        Doctor selectedDoctor = (Doctor) JOptionPane.showInputDialog(this, "Select a Doctor", "Select Doctor", JOptionPane.QUESTION_MESSAGE, null, doctors.toArray(), doctors.get(0));
        if (selectedDoctor != null) {
            List<AppointmentSlot> appointments = appointmentManager.getAllAppointmentsForDoctor(selectedDoctor);
            if (appointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No appointments found.");
                return;
            }
            displayAppointmentsByDay(appointments);
        }
    }

    private void showPatientMenu() {
        Patient selectedPatient = (Patient) JOptionPane.showInputDialog(this, "Select a Patient", "Select Patient", JOptionPane.QUESTION_MESSAGE, null, patients.toArray(), patients.get(0));
        if (selectedPatient == null) return;

        String[] options = {"View Appointments", "Take Appointment", "Show History", "Back to Main Menu"};
        int choice = JOptionPane.showOptionDialog(this, "Patient Menu", "Patient Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0 -> viewPatientAppointments(selectedPatient);
            case 1 -> showTakeAppointmentMenu(selectedPatient);
            case 2 -> showPatientHistory(selectedPatient);
            case 3 -> {} // Back to main
        }
    }

    private void showTakeAppointmentMenu(Patient patient) {
        // ... (Implement Take Appointment Menu using JDialog/JOptionPane)
        String[] options = {"Search for Doctor", "Take Appointment by Doctor Name", "See All Appointments for a Doctor", "Back to Previous Menu"};
        int choice = JOptionPane.showOptionDialog(this, "Take Appointment Menu", "Take Appointment Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0 -> searchAndBookDoctor(patient);
            case 1 -> takeAppointmentByDoctorName(patient);
            case 2 -> viewAllDoctorAppointments(patient);
            case 3 -> {} // Back to main
        }
    }

    private void viewPatientAppointments(Patient patient) {
        List<AppointmentSlot> appointments = patient.getPatientAppoinments();
        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments found.");
            return;
        }
        StringBuilder sb = new StringBuilder("Your appointments:\n");
        for (AppointmentSlot slot : appointments) {
            sb.append(slot).append("\n");
        }
        outputArea.setText(sb.toString()); // Display in the main output area
    }

    private void searchAndBookDoctor(Patient patient) {
        String searchTerm = JOptionPane.showInputDialog(this, "Enter doctor name to search:");
        if (searchTerm == null || searchTerm.isEmpty()) return;

        List<String> matchingDoctors = appointmentManager.findDoctor(searchTerm);
        if (matchingDoctors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matching doctors found.");
            return;
        }
        // ... (Rest of searchAndBookDoctor logic using JOptionPane)
        String[] options = matchingDoctors.toArray(new String[0]);
        int choice = JOptionPane.showOptionDialog(this, "Matching doctors:", "Select Doctor",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice >= 0 && choice < matchingDoctors.size()) {
            String doctorName = matchingDoctors.get(choice);
            Doctor selectedDoctor = findDoctorByName(doctorName);
            if (selectedDoctor != null) {
                showAndBookAppointment(patient, selectedDoctor);
            }
        }
    }
    private void showAndBookAppointment(Patient patient, Doctor doctor) {
        AppointmentSlot nearest = appointmentManager.getNearestAvailableAppointmentSlot(doctor);
        if (nearest == null) {
            JOptionPane.showMessageDialog(this, "No available appointments for this doctor.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Nearest available appointment: " + nearest + "\nBook this appointment?", "Confirm Booking", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                appointmentManager.bookAppointment(doctor, patient, nearest.getTime());
                JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Failed to book appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void takeAppointmentByDoctorName(Patient patient) {
        String doctorName = JOptionPane.showInputDialog(this, "Enter doctor name:");
        if (doctorName == null || doctorName.isEmpty()) return;

        Doctor doctor = findDoctorByName(doctorName);
        if (doctor != null) {
            showAndBookAppointment(patient, doctor);
        } else {
            JOptionPane.showMessageDialog(this, "Doctor not found.");
        }
    }

    private void viewAllDoctorAppointments(Patient patient) {
        Doctor doctor = (Doctor) JOptionPane.showInputDialog(this, "Select a Doctor", "Select Doctor", JOptionPane.QUESTION_MESSAGE, null, doctors.toArray(), doctors.get(0));
        if (doctor != null) {
            List<AppointmentSlot> appointments = appointmentManager.getAllAppointmentsForDoctor(doctor);
            if (appointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No appointments available for this doctor.");
                return;
            }
            displayAvailableAppointments(appointments);
            // ... (Booking logic after displaying appointments)
            int slotIndex = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter appointment number to book: ")) - 1;
            if (slotIndex >= 0 && slotIndex < appointments.size()) {
                AppointmentSlot selected = appointments.get(slotIndex);
                if (!selected.isBooked()) {
                    try {
                        appointmentManager.bookAppointment(doctor, patient, selected.getTime());
                        JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
                    } catch (IllegalArgumentException e) {
                        JOptionPane.showMessageDialog(this, "Failed to book appointment: " + e.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "This slot is already booked.");
                }
            }
        }
    }

    private void showPatientHistory(Patient patient) {
        Stack<String> notifications = patient.getNotifications();
        if (notifications.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No history available.");
            return;
        }

        StringBuilder sb = new StringBuilder("Notification History:\n");
        for (String notification : notifications) {
            sb.append("- ").append(notification).append("\n");
        }
        outputArea.setText(sb.toString());
    }
     private Doctor findDoctorByName(String name) {
        return doctors.stream()
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private void displayAppointmentsByDay(List<AppointmentSlot> appointments) {
        Map<LocalDate, List<AppointmentSlot>> appointmentsByDay = new TreeMap<>();

        for (AppointmentSlot slot : appointments) {
            LocalDate date = slot.getTime().toLocalDate();
            appointmentsByDay.computeIfAbsent(date, k -> new ArrayList<>()).add(slot);
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<LocalDate, List<AppointmentSlot>> entry : appointmentsByDay.entrySet()) {
            sb.append("\nDate: ").append(entry.getKey()).append("\n");
            for (AppointmentSlot slot : entry.getValue()) {
                sb.append("  ").append(slot.getTime().toLocalTime())
                        .append(" - ").append(slot.getEndTime().toLocalTime())
                        .append(slot.isBooked() ? " (Booked by " + slot.getPatientName() + ")" : " (Available)").append("\n");
            }
        }
        outputArea.setText(sb.toString());
    }

    private void displayAvailableAppointments(List<AppointmentSlot> appointments) {
        StringBuilder sb = new StringBuilder("\nAvailable appointments:\n");
        int counter = 1;
        for (AppointmentSlot slot : appointments) {
            if (!slot.isBooked()) {
                sb.append(counter).append(". ").append(slot.getTime()).append(" - ").append(slot.getEndTime()).append("\n");
                counter++;
            }
        }
        outputArea.setText(sb.toString());
    }

    private void displayNearestAppointments(List<AppointmentSlot> appointments) {
        if (appointments.isEmpty()) {
            outputArea.setText("No available appointments found.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (AppointmentSlot slot : appointments) {
            sb.append("Dr. ").append(slot.getDocName())
                    .append(" - Next available: ").append(slot.getTime())
                    .append(" - ").append(slot.getEndTime()).append("\n");
        }
        outputArea.setText(sb.toString());
    }
    // ... (Existing findDoctorByName, displayAppointmentsByDay, displayAvailableAppointments, displayNearestAppointments methods)

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HospitalManagementSystemGUI::new);
    }
}