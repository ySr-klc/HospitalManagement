package HospitalManagmentSystem;

import DoctorListUpdater.DoctorObserverHandler;
import Persons.Doctor;
import Clinics.Clinic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Clinic Management class for managing clinic operations including doctor management.
 * Provides interface for viewing, adding, and removing doctors from a clinic.
 */
class ClinicManagementGUI extends JFrame {
    // List of doctors associated with the clinic
    private  List<Doctor> clinicDoctors;
    // Panel to display the list of doctors
    private JPanel doctorListPanel;
    // Reference to the clinic being managed
    private final Clinic clinic;
    // Handler for doctor-related operations and updates
    private final DoctorObserverHandler doctors;
    
    /**
     * Constructor initializes the GUI with clinic and doctor handler references
     * @param clinic The clinic to be managed
     * @param doctors The handler for doctor operations
     */
    public ClinicManagementGUI(Clinic clinic, DoctorObserverHandler doctors ) {
        this.clinic=clinic;
        clinicDoctors=clinic.getDoctors();
        initializeGUI();
        this.doctors=doctors;
    }

    /**
     * Sets up the main GUI components including header, doctor list, and control buttons
     */
    private void initializeGUI() {
        setTitle("Clinic Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header section with title
        JLabel headerLabel = new JLabel("Clinic Management System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Scrollable panel for displaying doctor list
        doctorListPanel = new JPanel();
        doctorListPanel.setLayout(new BoxLayout(doctorListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(doctorListPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Control buttons panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addDoctorButton = new JButton("Add New Doctor");
        JButton backButton = new JButton("Back to Main Menu");
         
        addDoctorButton.addActionListener(e -> showAddDoctorDialog());
        backButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addDoctorButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        refreshDoctorList();
    }

    /**
     * Displays dialog for adding a new doctor to the clinic
     * Includes form fields for doctor details and validation
     */
    private void showAddDoctorDialog() {
        JDialog dialog = new JDialog(this, "Add New Doctor", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Input field for doctor's name
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);
       
        // Save and Cancel buttons with action handlers
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        // Save button handler with input validation
        saveButton.addActionListener(e -> {
            if (!nameField.getText().trim().isEmpty()) {
                doctors.doctorAdd(new Doctor(
                    nameField.getText().trim(),
                       clinic.getDepartmentKey()
                ));
                
                refreshDoctorList();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "Name field cannot be empty", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }

    /**
     * Shows detailed information about a selected doctor
     * @param doctor The doctor whose details are to be displayed
     */
    private void showDoctorDetails(Doctor doctor) {
        JDialog dialog = new JDialog(this, "Doctor Details", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Display doctor's information fields
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(new JLabel(String.valueOf(doctor.getId())), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(new JLabel(doctor.getName()), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Specialization:"), gbc);
        gbc.gridx = 1;
        dialog.add(new JLabel(doctor.getSpeciality().toString()), gbc);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(closeButton, gbc);

        dialog.setVisible(true);
    }

    /**
     * Updates the display of doctors list
     * Refreshes the list after any changes (add/delete)
     */
    private void refreshDoctorList() {
        doctorListPanel.removeAll();
        clinicDoctors=clinic.getDoctors();
        
        // Create a panel for each doctor with their info and action buttons
        for (Doctor doctor : clinicDoctors) {
            JPanel doctorPanel = new JPanel(new BorderLayout(10, 0));
            doctorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY)
            ));

            // Display doctor's name and speciality
            JLabel nameLabel = new JLabel(doctor.getName() + " - " + doctor.getSpeciality());
            doctorPanel.add(nameLabel, BorderLayout.CENTER);

            // Action buttons for each doctor entry
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            
            JButton detailsButton = new JButton("See Details");
            detailsButton.addActionListener(e -> showDoctorDetails(doctor));
            
            // Delete button with confirmation dialog
            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete Dr. " + doctor.getName() + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        doctors.doctorDelete(doctor);
                        refreshDoctorList();
                    } catch (Exception l) {
                        System.out.println(l);
                    }
                }
            });

            buttonsPanel.add(detailsButton);
            buttonsPanel.add(deleteButton);
            doctorPanel.add(buttonsPanel, BorderLayout.EAST);

            doctorListPanel.add(doctorPanel);
            doctorListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Refresh the UI
        doctorListPanel.revalidate();
        doctorListPanel.repaint();
    }
}