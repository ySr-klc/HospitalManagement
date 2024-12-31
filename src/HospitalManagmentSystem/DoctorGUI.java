
package HospitalManagmentSystem;

import Persons.Doctor;
import History.History;
import History.HistoryOfPatient;
import Appointments.AppointmentManager;
import Appointments.AppointmentSlot;

import javax.swing.*;
import java.awt.*;
import java.time.DateTimeException;
import javax.swing.border.EmptyBorder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class DoctorGUI extends JFrame {
    private JTabbedPane appointmentTabs;
    private final Doctor currentDoctor;
    private final AppointmentManager appointmentManager;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    
    
    
    

    
    
    
    public DoctorGUI(Doctor doctor, AppointmentManager appointmentManager) {
        this.currentDoctor = doctor;
        this.appointmentManager = appointmentManager;
        
        setTitle("Doctor Dashboard - Dr. " + doctor.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initializeComponents();
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top panel for doctor info and main actions
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel with tabs for appointments
        appointmentTabs = new JTabbedPane();
        refreshAppointmentTabs();
        mainPanel.add(appointmentTabs, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Doctor info panel
        JPanel doctorInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        doctorInfoPanel.add(new JLabel("Dr. " + currentDoctor.getName()));
        doctorInfoPanel.add(new JLabel(" | Specialty: " + currentDoctor.getSpeciality()));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton scheduleButton = new JButton("Schedule New Appointments");
        JButton modifyButton = new JButton("Modify Appointments");
        JButton backButton = new JButton("Back to Main Menu");

        scheduleButton.addActionListener(e -> showScheduleDialog());
        modifyButton.addActionListener(e -> showModifyDialog());
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(scheduleButton);
        buttonPanel.add(modifyButton);
        buttonPanel.add(backButton);

        topPanel.add(doctorInfoPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        return topPanel;
    }
private JPanel createDayPanel(List<AppointmentSlot> appointments) {
    // Create a main container panel
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    
    // Create a panel for appointments
    JPanel appointmentsPanel = new JPanel();
    appointmentsPanel.setLayout(new BoxLayout(appointmentsPanel, BoxLayout.Y_AXIS));
    
    for (AppointmentSlot slot : appointments) {
        JPanel appointmentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String timeText = slot.getTime().format(formatter) + " - " + 
                        slot.getEndTime().format(formatter);
        String statusText = slot.isBooked() ? 
                          "Booked by " + slot.getPatientName() :
                          "Available";
        
        appointmentPanel.add(new JLabel(timeText));
        appointmentPanel.add(new JLabel(" | "));
        appointmentPanel.add(new JLabel(statusText));
        
        if (slot.isBooked()) {
            JButton detailsButton = new JButton("Details");
            detailsButton.addActionListener(e -> showEnhancedAppointmentDetails(slot));
            appointmentPanel.add(detailsButton);
        }
        
        appointmentsPanel.add(appointmentPanel);
    }
    
    // Create scroll pane and add appointments panel to it
    JScrollPane scrollPane = new JScrollPane(appointmentsPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    
    // Add the scroll pane to the main panel
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    
    return mainPanel;
}

private void refreshAppointmentTabs() {
    appointmentTabs.removeAll();
    List<AppointmentSlot> appointments = appointmentManager.getAllAppointmentsForDoctor(currentDoctor.getId());
    
    if (appointments.isEmpty()) {
        JPanel noAppointmentsPanel = new JPanel(new GridBagLayout());
        noAppointmentsPanel.add(new JLabel("No appointments found"));
        appointmentTabs.addTab("No Appointments", noAppointmentsPanel);
    } else {
        // Group appointments by day
        Map<LocalDate, List<AppointmentSlot>> appointmentsByDay = new TreeMap<>();
        for (AppointmentSlot slot : appointments) {
            LocalDate date = slot.getTime().toLocalDate();
            appointmentsByDay.computeIfAbsent(date, k -> new ArrayList<>()).add(slot);
        }
        
        // Create a tab for each day
        for (Map.Entry<LocalDate, List<AppointmentSlot>> entry : appointmentsByDay.entrySet()) {
            JPanel dayPanel = createDayPanel(entry.getValue());
            
            // Set preferred size for better scrolling behavior
            dayPanel.setPreferredSize(new Dimension(
                appointmentTabs.getWidth() - 50, 
                Math.min(500, entry.getValue().size() * 50) 
            ));
            
            appointmentTabs.addTab(
                entry.getKey().format(DateTimeFormatter.ofPattern("E, MMM d", Locale.ENGLISH)),
                dayPanel
            );
        }
    }
    appointmentTabs.revalidate();
    appointmentTabs.repaint();
}

 
    private void showScheduleDialog() {
        JDialog dialog = new JDialog(this, "Schedule New Appointments", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Components for number of days
        panel.add(new JLabel("Number of Days:"));
        JComboBox<Integer> daysCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});

        // Components for duration
        panel.add(new JLabel("Appointment Duration:"));
        JComboBox<Integer> durationCombo = new JComboBox<>(new Integer[]{10, 15, 30});

        JButton scheduleButton = new JButton("Schedule");
        scheduleButton.addActionListener(e -> {
            int days = (Integer) daysCombo.getSelectedItem();
            int duration = (Integer) durationCombo.getSelectedItem();
            try {
                appointmentManager.createDoctorAppointments(currentDoctor, days, duration);
            } catch (Exception s) {
                 JOptionPane.showMessageDialog(this, "Error: " + s.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            refreshAppointmentTabs();
            dialog.dispose();
        });

        panel.add(daysCombo);
        panel.add(durationCombo);
        panel.add(scheduleButton);

        dialog.add(panel);
        dialog.setVisible(true);
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
    private void showHistory(AppointmentSlot slot) {
        JDialog dialog = new JDialog(this, "Appointment History", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<History> histories = HistoryOfPatient.getHistory(slot.getPatient()).stream()
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


    private void showEnhancedAppointmentDetails(AppointmentSlot slot) {
        JDialog dialog = new JDialog(this, "Appointment Details", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top panel for basic information
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.add(new JLabel("Date: " + slot.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        infoPanel.add(new JLabel("Time: " + slot.getTime().format(formatter) + " - " + slot.getEndTime().format(formatter)));
        infoPanel.add(new JLabel("Patient: " + slot.getPatientName()));
        JButton seeHistory=new JButton("History");
        infoPanel.add(seeHistory,BorderLayout.EAST);
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        seeHistory.addActionListener(e->
               showHistory(slot)
        );
        
        // Center panel for attendance and diagnosis
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        // Attendance panel
        JPanel attendancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel attendanceLabel = new JLabel("Attendance: ");
        JRadioButton attendedButton = new JRadioButton("Attended");
        JRadioButton notAttendedButton = new JRadioButton("Did Not Attend");
        ButtonGroup attendanceGroup = new ButtonGroup();
        attendanceGroup.add(attendedButton);
        attendanceGroup.add(notAttendedButton);
        
        // Set initial state based on stored value
        
            if (slot.hasPatientAttended()) {
                attendedButton.setSelected(true);
            } else {
                notAttendedButton.setSelected(true);
            }
        
        
        attendancePanel.add(attendanceLabel);
        attendancePanel.add(attendedButton);
        attendancePanel.add(notAttendedButton);
        
        // Diagnosis panel
        JPanel diagnosisPanel = new JPanel(new BorderLayout(5, 5));
        diagnosisPanel.add(new JLabel("Diagnosis:"), BorderLayout.NORTH);
        JTextArea diagnosisArea = new JTextArea(5, 30);
        diagnosisArea.setLineWrap(true);
        diagnosisArea.setWrapStyleWord(true);
        if (slot.getDiagnosis() != null) {
            diagnosisArea.setText(slot.getDiagnosis());
        }
        JScrollPane scrollPane = new JScrollPane(diagnosisArea);
        diagnosisPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(attendancePanel, BorderLayout.NORTH);
        centerPanel.add(diagnosisPanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            // Save attendance
            if (attendedButton.isSelected() || notAttendedButton.isSelected()) {
                slot.setPatientAttended(attendedButton.isSelected());
            }
            
            // Save diagnosis if attended
            if (attendedButton.isSelected()) {
                slot.setDiagnosis(diagnosisArea.getText());
            }
            
            // Handle the appointment in the appointment manager
            appointmentManager.passedAppointmentHandler(slot);
            refreshAppointmentTabs();
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showModifyDialog() {
        JDialog dialog = new JDialog(this, "Modify Appointments", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JTabbedPane modifyTabs = new JTabbedPane();

        // Tab 1: Change Appointment Duration
        JPanel durationPanel = createChangeDurationPanel();
        modifyTabs.addTab("Change Duration", durationPanel);

        // Tab 2: Cancel Specific Day
        JPanel cancelDayPanel = createCancelDayPanel();
        modifyTabs.addTab("Cancel Day", cancelDayPanel);

        // Tab 3: Cancel Time Range
        JPanel cancelRangePanel = createCancelRangePanel();
        modifyTabs.addTab("Cancel Range", cancelRangePanel);

        dialog.add(modifyTabs);
        dialog.setVisible(true);
    }

    private JPanel createChangeDurationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Date selection
        panel.add(new JLabel("Select Date(YYYY-MM-DD):"), gbc);
        JTextField dateField = new JTextField();
        panel.add(dateField, gbc);

        // Duration selection
        panel.add(new JLabel("New Duration (minutes):"), gbc);
        JComboBox<Integer> durationCombo = new JComboBox<>(new Integer[]{10, 15, 30});
        panel.add(durationCombo, gbc);

        JButton applyButton = new JButton("Apply Changes");
        applyButton.addActionListener(e -> {
            try {
                LocalDateTime dateTime = LocalDate.parse(dateField.getText()).atStartOfDay();
                int duration = (Integer) durationCombo.getSelectedItem();
                appointmentManager.changeAppointmentDuration(currentDoctor, duration, dateTime);
                refreshAppointmentTabs();
                JOptionPane.showMessageDialog(this, "Duration changed successfully!");
            }catch(DateTimeException s){
                JOptionPane.showMessageDialog(this, "Error: Invalid Format please try again!(YYYY-MM-DD)", 
                                           "Error", JOptionPane.ERROR_MESSAGE);
            }  catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                                           "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(applyButton, gbc);

        return panel;
    }

    private JPanel createCancelDayPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Select Date to Cancel (YYYY-MM-DD):"), gbc);
        JTextField dateField = new JTextField();
        panel.add(dateField, gbc);

        JButton cancelButton = new JButton("Cancel Day");
        cancelButton.addActionListener(e -> {
            try {
                LocalDateTime dateTime = LocalDate.parse(dateField.getText()).atStartOfDay();
                appointmentManager.cancelDoctorDay(currentDoctor.getId(), dateTime);
                refreshAppointmentTabs();
                JOptionPane.showMessageDialog(this, "Day cancelled successfully!");
            }catch(DateTimeException s){
                JOptionPane.showMessageDialog(this, "Error: Invalid Format please try again!(YYYY-MM-DD)", 
                                           "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                                           "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(cancelButton, gbc);

        return panel;
    }

    private JPanel createCancelRangePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Start Date and Time (YYYY-MM-DD HH:mm):"), gbc);
        JTextField startField = new JTextField();
        panel.add(startField, gbc);

        panel.add(new JLabel("End Date and Time (YYYY-MM-DD HH:mm):"), gbc);
        JTextField endField = new JTextField();
        panel.add(endField, gbc);

        JButton cancelButton = new JButton("Cancel Range");
        cancelButton.addActionListener(e -> {
            try {
                LocalDateTime start = LocalDateTime.parse(startField.getText(), formatter);
                LocalDateTime end = LocalDateTime.parse(endField.getText(), formatter);
                
                // Cancel appointments within the range
                List<AppointmentSlot> appointments = appointmentManager.getAllAppointmentsForDoctor(currentDoctor.getId());
                for (AppointmentSlot slot : appointments) {
                    if (!slot.getTime().isBefore(start) && !slot.getTime().isAfter(end)) {
                        appointmentManager.cancelAppointment(currentDoctor.getId(), slot.getTime());
                    }
                }
                refreshAppointmentTabs();
                JOptionPane.showMessageDialog(this, "Time range cancelled successfully!");
            }catch(DateTimeException s){
                JOptionPane.showMessageDialog(this, "Error: Invalid Format please try again!(YYYY-MM-DD HH:mm)", 
                                           "Error", JOptionPane.ERROR_MESSAGE);
            }  catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                                           "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(cancelButton, gbc);

        return panel;
    }

}
