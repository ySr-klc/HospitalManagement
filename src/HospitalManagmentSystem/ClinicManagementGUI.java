
package HospitalManagmentSystem;

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



class ClinicManagementGUI extends JFrame {
    private final List<Doctor> clinicDoctors;
    private JPanel doctorListPanel;
    private final Clinic clinic;
    private final DoctorObserverHandler doctors;
    
    public ClinicManagementGUI(Clinic clinic, DoctorObserverHandler doctors ) {
        this.clinic=clinic;
        clinicDoctors=clinic.getDoctors();
        initializeGUI();
        this.doctors=doctors;
    }

    private void initializeGUI() {
        setTitle("Clinic Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JLabel headerLabel = new JLabel("Clinic Management System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Doctor list panel
        doctorListPanel = new JPanel();
        doctorListPanel.setLayout(new BoxLayout(doctorListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(doctorListPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
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

    private void showAddDoctorDialog() {
        JDialog dialog = new JDialog(this, "Add New Doctor", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Name:"), gbc);
        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        // Specialization field
       
        // Contact details field
       
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

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

    private void showDoctorDetails(Doctor doctor) {
        JDialog dialog = new JDialog(this, "Doctor Details", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Display doctor details
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

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        dialog.add(new JLabel(doctor.getSpeciality().toString()), gbc);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(closeButton, gbc);

        dialog.setVisible(true);
    }

    private void refreshDoctorList() {
        doctorListPanel.removeAll();
        
        for (Doctor doctor : clinicDoctors) {
            JPanel doctorPanel = new JPanel(new BorderLayout(10, 0));
            doctorPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY)
            ));

            // Doctor info
            JLabel nameLabel = new JLabel(doctor.getName() + " - " + doctor.getSpeciality());
            doctorPanel.add(nameLabel, BorderLayout.CENTER);

            // Buttons panel
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            
            JButton detailsButton = new JButton("See Details");
            detailsButton.addActionListener(e -> showDoctorDetails(doctor));
            
            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete Dr. " + doctor.getName() + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    doctors.doctorDelete(doctor);
                    refreshDoctorList();
                }
            });

            buttonsPanel.add(detailsButton);
            buttonsPanel.add(deleteButton);
            doctorPanel.add(buttonsPanel, BorderLayout.EAST);

            doctorListPanel.add(doctorPanel);
            doctorListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        doctorListPanel.revalidate();
        doctorListPanel.repaint();
    }

   
}