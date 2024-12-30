package hospitt;

import a.AppointmentManager;
import a.AppointmentSlot;
import a.Clinic;
import a.ClinicEnums;
import a.ClinicsManager;
import a.Doctor;
import a.HistoryOfPatient;
import a.Patient;
import a.Search;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import javax.swing.table.DefaultTableModel;

public class HospitalManagementSystemGUI extends JFrame {
    private  final CardLayout cardLayout;
    private  final JPanel mainPanel;
    private final AppointmentManager appointmentManager;
    private final List<Doctor> doctors;
    private final List<Patient> patients;
    private final ClinicsManager clinicManager;
    private final HistoryOfPatient patientsHistory;
    private final Search searchPanel;
    // Formatter for dates
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    
     public static void main(String[] args) {
        // Create and show GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            HospitalManagementSystemGUI gui = new HospitalManagementSystemGUI();
            gui.setVisible(true);
        });
    }

    public HospitalManagementSystemGUI() {
        // Initialize data structures
        doctors = new ArrayList<>();
        patients = new ArrayList<>();
        
        // Initialize business logic components
        initializeDoctorsAndPatients();
        clinicManager = new ClinicsManager(doctors);
        patientsHistory = new HistoryOfPatient(patients);
        appointmentManager = new AppointmentManager(doctors, patientsHistory, clinicManager);
        searchPanel = new Search(doctors);
        
        // Connect components
        clinicManager.connectAppointmentManager(appointmentManager);
        
        // Initialize random appointments
        initializeRandomAppointments();
        
        // Set up the main frame
        setTitle("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Initialize card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        // Create and add panels
        createMainMenu();
        createDoctorPanel();
        createPatientPanel();
        createClinicPanel();
        
        // Show the main menu initially
        cardLayout.show(mainPanel, "MainMenu");
        
        // Initialize appointment checker timer
        initializeAppointmentChecker();
    }

    private void initializeDoctorsAndPatients() {
        // Add doctors
        doctors.add(new Doctor("John", ClinicEnums.Department.CARDIOLOGY));
        doctors.add(new Doctor("Sarah", ClinicEnums.Department.PEDIATRICS));
        doctors.add(new Doctor("Michael", ClinicEnums.Department.NEUROLOGY));
        doctors.add(new Doctor("Markus", ClinicEnums.Department.NEUROLOGY));
        doctors.add(new Doctor("Sedric", ClinicEnums.Department.PEDIATRICS));
        
        // Add patients
        patients.add(new Patient("Alice"));
        patients.add(new Patient("Bob"));
    }

    private void initializeRandomAppointments() {
        Random rand = new Random();
        int[] days = {1, 2, 3, 4, 5};
        int[] duration = {10, 15, 30};
        
        for (Doctor doctor : doctors) {
            int randomDayIndex = rand.nextInt(days.length);
            int randomDurationIndex = rand.nextInt(duration.length);
            appointmentManager.createDoctorAppointments(
                doctor, 
                days[randomDayIndex], 
                duration[randomDurationIndex]
            );
        }
    }

    private void initializeAppointmentChecker() {
        java.util.Timer timer = new java.util.Timer(true); // Use daemon timer
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    appointmentManager.passedAppointmentHandler();
                });
            }
        };
        timer.schedule(task, 2000, 5000);
    }

    private void createMainMenu() {
        JPanel mainMenuPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Hospital Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainMenuPanel.add(titleLabel, gbc);

        // Doctor Button
        JButton doctorButton = new JButton("Doctor Portal");
        doctorButton.addActionListener(e -> cardLayout.show(mainPanel, "DoctorPanel"));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainMenuPanel.add(doctorButton, gbc);

        // Patient Button
        JButton patientButton = new JButton("Patient Portal");
        patientButton.addActionListener(e -> cardLayout.show(mainPanel, "PatientPanel"));
        gbc.gridx = 1;
        mainMenuPanel.add(patientButton, gbc);

        // Clinic Button
        JButton clinicButton = new JButton("Clinic Management");
        clinicButton.addActionListener(e -> cardLayout.show(mainPanel, "ClinicPanel"));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainMenuPanel.add(clinicButton, gbc);

        mainPanel.add(mainMenuPanel, "MainMenu");
    }

    private void createDoctorPanel() {
        JPanel doctorPanel = new JPanel(new BorderLayout());
        
        // Top Panel with back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));
        topPanel.add(backButton);
        
        // Doctor Selection Panel
        JPanel selectionPanel = new JPanel(new FlowLayout());
        JComboBox<Doctor> doctorComboBox = new JComboBox<>(doctors.toArray(new Doctor[0]));
        selectionPanel.add(new JLabel("Select Doctor: "));
        selectionPanel.add(doctorComboBox);
        topPanel.add(selectionPanel);
        
        doctorPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel with appointments table
        JPanel centerPanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Time", "Patient", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable appointmentsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton scheduleButton = new JButton("Schedule New Appointment");
        JButton cancelButton = new JButton("Cancel Selected Appointment");
        JButton viewButton = new JButton("View All Appointments");
        
        buttonPanel.add(scheduleButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(viewButton);

        // Add action listeners
        scheduleButton.addActionListener(e -> showScheduleDialog((Doctor)doctorComboBox.getSelectedItem()));
        cancelButton.addActionListener(e -> cancelSelectedAppointment(appointmentsTable));
        viewButton.addActionListener(e -> refreshAppointmentsTable(tableModel, (Doctor)doctorComboBox.getSelectedItem()));
        
        doctorPanel.add(centerPanel, BorderLayout.CENTER);
        doctorPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(doctorPanel, "DoctorPanel");
    }

    private void createPatientPanel() {
        JPanel patientPanel = new JPanel(new BorderLayout());
        
        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));
        topPanel.add(backButton);
        
        // Patient Selection
        JComboBox<Patient> patientComboBox = new JComboBox<>(patients.toArray(new Patient[0]));
        topPanel.add(new JLabel("Select Patient: "));
        topPanel.add(patientComboBox);
        
        patientPanel.add(topPanel, BorderLayout.NORTH);

        // Appointments Table
        String[] columnNames = {"Doctor", "Time", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable appointmentsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        patientPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton bookButton = new JButton("Book Appointment");
        JButton cancelButton = new JButton("Cancel Appointment");
        JButton historyButton = new JButton("View History");
        
        buttonPanel.add(bookButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(historyButton);

        // Add action listeners
        bookButton.addActionListener(e -> showBookAppointmentDialog((Patient)patientComboBox.getSelectedItem()));
        cancelButton.addActionListener(e -> cancelPatientAppointment(appointmentsTable, (Patient)patientComboBox.getSelectedItem()));
        historyButton.addActionListener(e -> showPatientHistory((Patient)patientComboBox.getSelectedItem()));
        
        patientPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(patientPanel, "PatientPanel");
    }

    private void createClinicPanel() {
        JPanel clinicPanel = new JPanel(new BorderLayout());
        
        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MainMenu"));
        topPanel.add(backButton);
        
        // Clinic Selection
        JComboBox<ClinicEnums.Department> clinicComboBox = new JComboBox<>(ClinicEnums.Department.values());
        topPanel.add(new JLabel("Select Clinic: "));
        topPanel.add(clinicComboBox);
        
        clinicPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        String[] columnNames = {"Doctor Name", "Specialization"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable doctorsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(doctorsTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addDoctorButton = new JButton("Add Doctor");
        JButton removeDoctorButton = new JButton("Remove Doctor");
        JButton viewHistoryButton = new JButton("View Clinic History");
        
        buttonPanel.add(addDoctorButton);
        buttonPanel.add(removeDoctorButton);
        buttonPanel.add(viewHistoryButton);

        // Add action listeners
        clinicComboBox.addActionListener(e -> refreshClinicDoctorsTable(tableModel, (ClinicEnums.Department)clinicComboBox.getSelectedItem()));
        addDoctorButton.addActionListener(e -> showAddDoctorDialog((ClinicEnums.Department)clinicComboBox.getSelectedItem()));
        removeDoctorButton.addActionListener(e -> removeSelectedDoctor(doctorsTable, (ClinicEnums.Department)clinicComboBox.getSelectedItem()));
        viewHistoryButton.addActionListener(e -> showClinicHistory((ClinicEnums.Department)clinicComboBox.getSelectedItem()));
        
        clinicPanel.add(centerPanel, BorderLayout.CENTER);
        clinicPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(clinicPanel, "ClinicPanel");
    }

    // Helper methods for Doctor functionality
    private void showScheduleDialog(Doctor doctor) {
        JDialog dialog = new JDialog(this, "Schedule Appointment", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // Add components for scheduling
        dialog.add(new JLabel("Date (YYYY-MM-DD):"));
        JTextField dateField = new JTextField();
        dialog.add(dateField);

        dialog.add(new JLabel("Time (HH:mm):"));
        JTextField timeField = new JTextField();
        dialog.add(timeField);

        dialog.add(new JLabel("Duration (minutes):"));
        JComboBox<Integer> durationBox = new JComboBox<>(new Integer[]{10, 15, 30});
        dialog.add(durationBox);

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(e -> {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateField.getText() + " " + timeField.getText(), formatter);
                appointmentManager.createDoctorAppointments(doctor, 1, (Integer)durationBox.getSelectedItem());
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Appointment scheduled successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date/time format!");
            }
        });
        dialog.add(confirmButton);

        dialog.setVisible(true);
    }

    private void cancelSelectedAppointment(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            // Implementation for canceling appointment
            JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.");
        }
    }

    private void refreshAppointmentsTable(DefaultTableModel model, Doctor doctor) {
        model.setRowCount(0);
        List<AppointmentSlot> appointments = appointmentManager.getAllAppointmentsForDoctor(doctor.getId());
        for (AppointmentSlot slot : appointments) {
            model.addRow(new Object[]{
                slot.getTime().format(formatter),
                slot.isBooked() ? slot.getPatientName() : "Available",
                slot.isBooked() ? "Booked" : "Available"
            });
        }
    }

    // Helper methods for Patient functionality
    private void showBookAppointmentDialog(Patient patient) {
        JDialog dialog = new JDialog(this, "Book Appointment", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        // Create a table model for available appointments
        String[] columnNames = {"Doctor", "Time", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable appointmentsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(appointmentsTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Add button panel
        JPanel buttonPanel = new JPanel();
        JButton bookButton = new JButton("Book Selected");
        JButton cancelButton = new JButton("Cancel");

        bookButton.addActionListener(e -> {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow >= 0) {
                // Implementation for booking
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(bookButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void cancelPatientAppointment(JTable table, Patient patient) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            // Implementation for canceling appointment
            JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.");
        }
    }

    private void showPatientHistory(Patient patient) {
        JDialog dialog = new JDialog(this, "Patient History", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);

        // Add patient history to text area
        for (String notification : patient.getNotifications()) {
            historyArea.append(notification + "\n");
        }

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // Helper methods for Clinic functionality
   private void refreshClinicDoctorsTable(DefaultTableModel model, ClinicEnums.Department department) {
        model.setRowCount(0);
        Clinic clinic = clinicManager.getClinic(department);
        List<Doctor> clinicDoctors = clinic.getDoctors();
        
        for (Doctor doctor : clinicDoctors) {
            model.addRow(new Object[]{
                doctor.getName(),
                doctor.getSpeciality()
            });
        }
    }

    private void showAddDoctorDialog(ClinicEnums.Department department) {
        JDialog dialog = new JDialog(this, "Add New Doctor", true);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        dialog.add(new JLabel("Doctor Name:"));
        JTextField nameField = new JTextField();
        dialog.add(nameField);

        JButton confirmButton = new JButton("Add");
        confirmButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                Doctor newDoctor = new Doctor(name, department);
                clinicManager.getClinic(department).addDoctor(newDoctor);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Doctor added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid name!");
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(confirmButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    private void removeSelectedDoctor(JTable table, ClinicEnums.Department department) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            String doctorName = (String) table.getValueAt(selectedRow, 0);
            Clinic clinic = clinicManager.getClinic(department);
            
            // Find and remove the doctor
            for (Doctor doctor : clinic.getDoctors()) {
                if (doctor.getName().equals(doctorName)) {
                    clinic.deleteDoctor(doctor);
                    JOptionPane.showMessageDialog(this, "Doctor removed successfully!");
                    return;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a doctor to remove.");
        }
    }

    private void showClinicHistory(ClinicEnums.Department department) {
        JDialog dialog = new JDialog(this, department + " Clinic History", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        Clinic clinic = clinicManager.getClinic(department);
        List<String> history = clinic.getHistory();

        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);

        for (String event : history) {
            historyArea.append(event + "\n");
        }

        // Add button to add new history entry
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add New Entry");
        addButton.addActionListener(e -> showAddHistoryEntryDialog(clinic));
        buttonPanel.add(addButton);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showAddHistoryEntryDialog(Clinic clinic) {
        JDialog dialog = new JDialog(this, "Add History Entry", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JTextArea entryArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(entryArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Entry");
        addButton.addActionListener(e -> {
            String entry = entryArea.getText().trim();
            if (!entry.isEmpty()) {
                clinic.addHistoryToDoctor(entry);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "History entry added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a valid entry!");
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Utility methods for dialogs
    private void showError(String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE));
    }

    private void showSuccess(String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE));
    }

    private boolean confirmDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirm", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}