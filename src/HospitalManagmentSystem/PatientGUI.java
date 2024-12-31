package HospitalManagmentSystem;

import Persons.Patient;
import Persons.Doctor;
import Search.Search;
import History.History;
import History.HistoryOfPatient;
import Clinics.Department;
import Clinics.ClinicsManager;
import Appointments.AppointmentManager;
import Appointments.AppointmentSlot;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;


public class PatientGUI extends JFrame {

    private final Patient currentPatient;
    private JPanel mainPanel;
    private JLabel notificationBadge;
    private static AppointmentManager appointmentManager;
    private static List<Doctor> doctors;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy, dd MMM, EEEE  HH.mm", Locale.ENGLISH);
    private static ClinicsManager clinicManager;
    private static Search searchPanel;
    private JPanel centerPanel;

    public PatientGUI(Patient patient, AppointmentManager appointmentManager,
            Search searchPanel, HistoryOfPatient patientHistory, List<Doctor> doctors, ClinicsManager manager) {
        this.currentPatient = patient;
        this.appointmentManager = appointmentManager;
        this.searchPanel = searchPanel;
        this.doctors = doctors;
        clinicManager = manager;

        setTitle("Patient Dashboard - " + patient.getName());
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeComponents();
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top panel with patient info and notification button
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);

        // Center panel with appointments
        centerPanel = createCenterPanel(); // Initialize centerPanel
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Side panel with main menu buttons
        mainPanel.add(createSidePanel(), BorderLayout.WEST);

        add(mainPanel);
        updateNotificationBadge();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());

        // Patient info
        JPanel patientInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        patientInfo.add(new JLabel("Welcome, " + currentPatient.getName()));

        // Notification button with badge
        JPanel notificationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton notificationButton = new JButton("Notifications");
        notificationBadge = new JLabel("");
        notificationBadge.setForeground(Color.RED);
        notificationPanel.add(notificationBadge);
        notificationPanel.add(notificationButton);

        notificationButton.addActionListener(e -> showNotifications());

        topPanel.add(patientInfo, BorderLayout.WEST);
        topPanel.add(notificationPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Your Appointments"));

        List<AppointmentSlot> appointments = currentPatient.getPatientAppointments();

        // Use a CardLayout for flexibility
        JPanel appointmentsContainer = new JPanel(new CardLayout());
        centerPanel.add(appointmentsContainer, BorderLayout.CENTER);

        // Empty Appointment Message
        JPanel noAppointmentsPanel = new JPanel();
        noAppointmentsPanel.add(new JLabel("You do not have any appointments yet", SwingConstants.CENTER));
        appointmentsContainer.add(noAppointmentsPanel, "NO_APPOINTMENTS");

        // Appointments Panel
        if (!appointments.isEmpty()) {
            JPanel appointmentsPanel = new JPanel();
            appointmentsPanel.setLayout(new BoxLayout(appointmentsPanel, BoxLayout.Y_AXIS));

            for (AppointmentSlot slot : appointments) {
                JPanel appointmentPanel = createAppointmentPanel(slot);

                JButton cancelAppointment = new JButton("Cancel Appointment");
                cancelAppointment.addActionListener(e -> {
                    // Confirmation Dialog
                    int confirmation = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to cancel this appointment?\n"
                           ,
                            "Cancel Appointment", JOptionPane.YES_NO_OPTION);
                            
                    if (confirmation == JOptionPane.YES_OPTION) {
                        // Cancel appointment without reschedule
                        appointmentManager.cancelPatientAppointment(currentPatient, slot);
                        refreshCenterPanel();
                    }
                });

                appointmentPanel.add(cancelAppointment);
                appointmentPanel.setPreferredSize(new Dimension(appointmentPanel.getPreferredSize().width, 50)); // Adjust 50 to your desired height
                appointmentsPanel.add(appointmentPanel);
                appointmentsPanel.add(Box.createVerticalStrut(5));
            }

            appointmentsContainer.add(appointmentsPanel, "WITH_APPOINTMENTS");
            ((CardLayout) appointmentsContainer.getLayout()).show(appointmentsContainer, "WITH_APPOINTMENTS");
        }

        return centerPanel;
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(new EmptyBorder(0, 0, 0, 10));

        JButton bookAppointmentButton = new JButton("Book Appointment");
        JButton historyButton = new JButton("View History");
        JButton backButton = new JButton("Back to Main Menu");

        // Set preferred size for consistent button width
        Dimension buttonSize = new Dimension(150, 30);
        bookAppointmentButton.setPreferredSize(buttonSize);
        historyButton.setPreferredSize(buttonSize);
        backButton.setPreferredSize(buttonSize);

        // Add action listeners
        bookAppointmentButton.addActionListener(e -> showBookAppointmentDialog());
        historyButton.addActionListener(e -> showHistory());
        backButton.addActionListener(e -> dispose());

        // Add buttons to panel with spacing
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(bookAppointmentButton);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(historyButton);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(backButton);

        return sidePanel;
    }

    private void showNotifications() {
        JDialog dialog = new JDialog(this, "Notifications", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Clone the notifications stack to display
        Stack<String> notifications = (Stack<String>) currentPatient.getNotifications().clone();

        if (notifications.isEmpty()) {
            panel.add(new JLabel("No notifications", SwingConstants.CENTER), BorderLayout.CENTER);
        } else {
            JPanel notificationsPanel = new JPanel();
            notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));

            while (!notifications.isEmpty()) {
                JLabel notificationLabel = new JLabel(notifications.pop());
                notificationsPanel.add(notificationLabel);
                notificationsPanel.add(Box.createVerticalStrut(5));
            }

            JScrollPane scrollPane = new JScrollPane(notificationsPanel);
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clearButton = new JButton("Clear All");
        JButton closeButton = new JButton("Close");

        clearButton.addActionListener(e -> {
            currentPatient.getNotifications().clear();
            updateNotificationBadge();
            dialog.dispose();
        });

        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(clearButton);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showBookAppointmentDialog() {
        JDialog dialog = new JDialog(this, "Book Appointment", true);
        dialog.setSize(1000, 400);
        dialog.setLocationRelativeTo(this);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Search by Clinic tab
        tabbedPane.addTab("Search by Clinic", createSearchByClinicPanel());

        // Search by Doctor tab
        tabbedPane.addTab("Search by Doctor", createSearchByDoctorPanel());

        dialog.add(tabbedPane);
        dialog.setVisible(true);
    }

    private JPanel createSearchByDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Search field and button
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Results panel
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultsPanel);

        searchButton.addActionListener(e -> {
            resultsPanel.removeAll();
            List<Map.Entry<String, Integer>> searchResults = PatientGUI.searchPanel.searchForDoctor(searchField.getText());

            if (searchResults.isEmpty()) {
                resultsPanel.add(new JLabel("No doctors found"));
            } else {
                for (Map.Entry<String, Integer> result : searchResults) {
                    JPanel doctorPanel = createDoctorResultPanel(result.getValue());
                    resultsPanel.add(doctorPanel);
                    resultsPanel.add(Box.createVerticalStrut(5));
                }
            }
            resultsPanel.revalidate();
            resultsPanel.repaint();
        });

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchByClinicPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Clinic selection
        JComboBox<Department> clinicCombo
                = new JComboBox<>(Department.values());

        // Results panel
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultsPanel);

        clinicCombo.addActionListener(e -> {
            resultsPanel.removeAll();
            Department selectedClinic
                    = (Department) clinicCombo.getSelectedItem();

            List<AppointmentSlot> appointments
                    = clinicManager.getAppointmentsFromClinic(selectedClinic);

            if (appointments.isEmpty()) {
                resultsPanel.add(new JLabel("No appointments available"));
            } else {
                for (AppointmentSlot slot : appointments) {
                    JPanel appointmentPanel = createClinicAppointmentPanel(slot);
                    resultsPanel.add(appointmentPanel);
                    resultsPanel.add(Box.createVerticalStrut(5));

                }
            }
            resultsPanel.revalidate();
            resultsPanel.repaint();
        });

        panel.add(clinicCombo, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createClinicAppointmentPanel(AppointmentSlot slot) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5)); // GridLayout with multiple rows
        panel.setBorder(BorderFactory.createEtchedBorder());

        panel.add(new JLabel("Time: " + slot.getTime().format(formatter)));
        panel.add(new JLabel("Doctor: " + slot.getDocName()));

        JButton takeAppointment = new JButton("Book");
        takeAppointment.addActionListener(s -> {
            try {
                appointmentManager.bookAppointment(slot.getDoc().getId(), currentPatient, slot.getTime()); // No boolean return
                JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                refreshCenterPanel();
                
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace(); // Very important for debugging
            }
        });
        panel.add(takeAppointment);
        JButton viewButton = new JButton("View Appointments");
        viewButton.addActionListener(s -> {
            JDialog dialog = new JDialog(this, "Doctor Appointments", true);
            dialog.add(seeAllAppointmentsForADoctor(slot.getDoc().getId()));
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });
        panel.add(viewButton);
        return panel;
    }

    private void showHistory() {
        JDialog dialog = new JDialog(this, "Appointment History", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<History> histories = HistoryOfPatient.getHistory(currentPatient).stream()
                .sorted(Comparator.comparing(History::getTime).reversed())
                .toList();

        if (histories.isEmpty()) {
            panel.add(new JLabel("No appointment history available", SwingConstants.CENTER));
        } else {
            JPanel historyPanel = new JPanel();
            historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));

            for (History history : histories) {
                JPanel historyItemPanel = createHistoryPanel(history);
                historyPanel.add(historyItemPanel);
                historyPanel.add(Box.createVerticalStrut(10));
            }

            JScrollPane scrollPane = new JScrollPane(historyPanel);
            panel.add(scrollPane, BorderLayout.CENTER);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        panel.add(closeButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateNotificationBadge() {
        int count = currentPatient.getNotifications().size();
        notificationBadge.setText(count > 0 ? "(" + count + ")" : "");
    }

    // Helper methods for creating panels...
    private JPanel createAppointmentPanel(AppointmentSlot slot) {
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 5));
        panel.setBorder(BorderFactory.createEtchedBorder());

        panel.add(new JLabel(slot.getTime().format(formatter)));
        panel.add(new JLabel(slot.getDocName()));
        panel.add(new JLabel(slot.getDoc().getSpeciality().toString()));

        return panel;
    }

    private JPanel createDoctorResultPanel(int doctorId) {
        Doctor doctor = findDoctorById(doctorId);
        if (doctor == null) {
            return (JPanel) new JPanel().add(new JLabel("Doctor not found")); // Handle null doctor
        }

        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 5));
        panel.setBorder(BorderFactory.createEtchedBorder());

        panel.add(new JLabel(doctor.getName()));
        panel.add(new JLabel(doctor.getSpeciality().toString()));

        AppointmentSlot nextSlot = appointmentManager.getNearestAvailableAppointmentSlot(doctorId);
        if (nextSlot != null) {
            panel.add(new JLabel(nextSlot.getTime().format(formatter)));
            JButton takeAppointment = new JButton("Book");
            takeAppointment.addActionListener(e -> {
                try {
                    appointmentManager.bookAppointment(doctorId, currentPatient, nextSlot.getTime()); // No boolean return
                    JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshCenterPanel();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace(); // Very important for debugging
                }
            });
            panel.add(takeAppointment);
        } else {
            panel.add(new JLabel("No available slots"));
        }

        JButton viewButton = new JButton("View Appointments");
        viewButton.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Doctor Appointments", true);
            dialog.add(seeAllAppointmentsForADoctor(doctorId));
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });
        panel.add(viewButton);

        return panel;
    }

    private JPanel seeAllAppointmentsForADoctor(int doctorId) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Doctor doctor = findDoctorById(doctorId);
        if (doctor == null) {
            mainPanel.add(new JLabel("Doctor not found."), BorderLayout.CENTER);
            return mainPanel;
        }

        JPanel doctorInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        doctorInfoPanel.add(new JLabel("Appointments for Dr. " + doctor.getName() + " (" + doctor.getSpeciality() + ")"));
        mainPanel.add(doctorInfoPanel, BorderLayout.NORTH);

        JPanel appointmentsPanel = new JPanel();
        appointmentsPanel.setLayout(new BoxLayout(appointmentsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(appointmentsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        refreshAppointments(appointmentsPanel, doctorId); // Initial population

        return mainPanel;
    }

    private void refreshAppointments(JPanel appointmentsPanel, int doctorId) {
        appointmentsPanel.removeAll();
        List<AppointmentSlot> allAppointments = appointmentManager.getAvailableAppointments(doctorId);

        if (allAppointments == null || allAppointments.isEmpty()) {
            appointmentsPanel.add(new JLabel("No appointments found for this doctor."));
        } else {
            for (AppointmentSlot slot : allAppointments) {
                JPanel appointmentPanel = createAppointmentPanelForDisplay(slot);
                appointmentsPanel.add(appointmentPanel);
                appointmentsPanel.add(Box.createVerticalStrut(5));
                JButton bookAppointment = new JButton("Book");
                bookAppointment.addActionListener(e -> {
                    try {
                        appointmentManager.bookAppointment(doctorId, currentPatient, slot.getTime()); // No boolean return
                        JOptionPane.showMessageDialog(this, "Appointment booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        refreshCenterPanel();
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    } catch (IllegalStateException ex) {
                        JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    refreshAppointments(appointmentsPanel, doctorId);
                });
                appointmentsPanel.add(bookAppointment);
            }
        }

        appointmentsPanel.revalidate();
        appointmentsPanel.repaint();
    }

    private JPanel createAppointmentPanelForDisplay(AppointmentSlot slot) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEtchedBorder());

        panel.add(new JLabel("Time: " + slot.getTime().format(formatter)));

        return panel;
    }

    private void refreshCenterPanel() {
        mainPanel.remove(centerPanel); // Remove the actual centerPanel object
        centerPanel = createCenterPanel(); // Create a new centerPanel
        mainPanel.add(centerPanel, BorderLayout.CENTER); // Add the new one
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private Doctor findDoctorById(int doctorId) {
        for (Doctor doctor : doctors) { // Assuming 'doctors' is a List<Doctor> field
            if (doctor.getId() == doctorId) { // Assuming Doctor has an getId() method
                return doctor;
            }
        }
        return null; // Return null if no doctor is found
    }

    private JPanel createHistoryPanel(History history) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEtchedBorder());

        panel.add(new JLabel("Date: " + history.getTime().format(formatter)));
        panel.add(new JLabel("Doctor: " + history.getDoctorName()));
        panel.add(new JLabel("Clinic: " + history.getClinic()));
        panel.add(new JLabel("Diagnosis: " + history.getDiagnosis()));

        return panel;
    }

}
