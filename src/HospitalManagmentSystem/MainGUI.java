package HospitalManagmentSystem;

import SystemInitializer.FileInputTaker;
import DoctorListUpdater.DoctorObserverHandler;
import Persons.Patient;
import Persons.Doctor;
import Search.Search;
import History.HistoryOfPatient;
import Clinics.Department;
import Clinics.ClinicsManager;
import Appointments.AppointmentManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.border.EmptyBorder;
import java.util.List;
import java.util.Random;


/**
 * 
 * @author ysr
 */
public class MainGUI extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private static final List<Patient> patients;
    private static final AppointmentManager appointmentManager;
    private static final ClinicsManager clinicManager;
    private static final HistoryOfPatient patientsHistory;
    private static final Search searchPanel;
    private static final DoctorObserverHandler doctors;
    
     static {
        // Initialize sample data
        List<Doctor> tempraryDoctors= FileInputTaker.readDoctorsFromFile("doctors.txt");
        
        doctors = new DoctorObserverHandler(tempraryDoctors);
        patients = new ArrayList<>();
        patients.add(new Patient("Yasir Kilic"));
        patients.add(new Patient("Berkay Simsek"));
       
        clinicManager = new ClinicsManager(doctors.getDoctors());
        patientsHistory = new HistoryOfPatient(patients);
        appointmentManager = new AppointmentManager(doctors.getDoctors());
        searchPanel = new Search(doctors.getDoctors());
        clinicManager.connectAppointmentManager(appointmentManager);
        
        doctors.addObserver(searchPanel);
        doctors.addObserver(clinicManager);
        doctors.addObserver(appointmentManager);
        try {
            FileInputTaker.generatePatientHistories(doctors.getDoctors(), patients, "diagnoses.txt");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }   
        Random rand = new Random();
        int[] days = {1, 2, 3, 4, 5}, duration = {10, 15, 30};
        int randomStarter, rndmStarter2;
        for (Doctor doctor : doctors.getDoctors()) {
            randomStarter = rand.nextInt(100) % 3;
            rndmStarter2 = rand.nextInt(100) % 5;
            appointmentManager.createDoctorAppointments(doctor, days[rndmStarter2], duration[randomStarter]);
        }
    }
    public MainGUI() {
        setTitle("Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initializeComponents();
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add main menu panel
        mainPanel.add(createMainMenuPanel(), "MAIN_MENU");

        // Add selection panels
        mainPanel.add(createClinicSelectionPanel(), "CLINIC_SELECTION");
        mainPanel.add(createDoctorSelectionPanel(), "DOCTOR_SELECTION");
        mainPanel.add(createPatientSelectionPanel(), "PATIENT_SELECTION");

        add(mainPanel);
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Hospital Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Buttons
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;

        JButton clinicButton = createMenuButton("Clinic Management", "CLINIC_SELECTION");
        JButton doctorButton = createMenuButton("Doctor Management", "DOCTOR_SELECTION");
        JButton patientButton = createMenuButton("Patient Management", "PATIENT_SELECTION");

        panel.add(clinicButton, gbc);
        gbc.gridx = 1;
        panel.add(doctorButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(patientButton, gbc);

        return panel;
    }

    private JButton createMenuButton(String text, String destination) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50));
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.addActionListener(e -> cardLayout.show(mainPanel, destination));
        return button;
    }

    private JPanel createClinicSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Select Clinic", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Clinic selection combo box
        JComboBox<Department> clinicCombo = new JComboBox<>(Department.values());
        clinicCombo.setPreferredSize(new Dimension(200, 30));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton selectButton = new JButton("Select");
        JButton backButton = new JButton("Back to Main Menu");

        selectButton.addActionListener(e -> {
            Department selectedClinic = (Department) clinicCombo.getSelectedItem();
            ClinicManagementGUI clinicGUI = new ClinicManagementGUI(clinicManager.getClinic(selectedClinic), doctors);
            addSubGUIListener(clinicGUI);
            clinicGUI.setVisible(true);
            // Don't dispose of main window - hide it instead
            setVisible(false);
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));

        buttonPanel.add(selectButton);
        buttonPanel.add(backButton);

      
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(clinicCombo);

        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDoctorSelectionPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(20, 20, 20, 20));

    JLabel titleLabel = new JLabel("Select Doctor", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    panel.add(titleLabel, BorderLayout.NORTH);

 
   DefaultListModel<Doctor> doctorListModel = new DefaultListModel<>();
List<Doctor> sortedDoctors = new ArrayList<>(doctors.getDoctors()); 
Collections.sort(sortedDoctors, Comparator.comparing(Doctor::getSpeciality)); 
sortedDoctors.forEach(doctorListModel::addElement);

    JList<Doctor> doctorList = new JList<>(doctorListModel);
    doctorList.setCellRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Doctor doctor = (Doctor) value;
            setText(doctor.getName() + " - " + doctor.getSpeciality());
            return this;
        }
    });

    // Add refresh functionality
    JButton refreshButton = new JButton("Refresh List");
    refreshButton.addActionListener(e -> {
        doctorListModel.clear();
        doctors.getDoctors().forEach(doctorListModel::addElement);
        doctorList.revalidate();
        doctorList.repaint();
    });

    JScrollPane scrollPane = new JScrollPane(doctorList);
    panel.add(scrollPane, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    JButton selectButton = new JButton("Select");
    JButton backButton = new JButton("Back to Main Menu");

    selectButton.addActionListener(e -> {
        Doctor selectedDoctor = doctorList.getSelectedValue();
        if (selectedDoctor != null) {
            DoctorGUI doctorGUI = new DoctorGUI(selectedDoctor, appointmentManager);
            addSubGUIListener(doctorGUI);
            doctorGUI.setVisible(true);
            setVisible(false);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a doctor");
        }
    });

    backButton.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));

    // Add refresh button to button panel
    buttonPanel.add(refreshButton);
    buttonPanel.add(selectButton);
    buttonPanel.add(backButton);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
}

    private JPanel createPatientSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Select Patient", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

      
        DefaultListModel<Patient> patientListModel = new DefaultListModel<>();
        patients.forEach(patientListModel::addElement);

        JList<Patient> patientList = new JList<>(patientListModel);
        patientList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Patient patient = (Patient) value;
                setText(patient.getName());
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(patientList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton selectButton = new JButton("Select");
        JButton backButton = new JButton("Back to Main Menu");

        selectButton.addActionListener(e -> {
            Patient selectedPatient = patientList.getSelectedValue();
            if (selectedPatient != null) {
                PatientGUI patientGUI = new PatientGUI(selectedPatient, appointmentManager, 
                                                      searchPanel, patientsHistory,doctors.getDoctors(), clinicManager);
                patientGUI.setVisible(true);
                addSubGUIListener(patientGUI);
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a patient");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MAIN_MENU"));

        buttonPanel.add(selectButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

   
    private void addSubGUIListener(JFrame subGUI) {
        subGUI.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI mainGUI = new MainGUI();
            mainGUI.setVisible(true);
        });
    }
}
