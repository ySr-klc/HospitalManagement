package SystemInitializer;

import Persons.Patient;
import Persons.Doctor;
import History.History;
import History.HistoryOfPatient;
import Clinics.Department;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * That class Takes necessary information (like doctor objects information and
 * initialize patient histories then respect to their names) to initialize
 * system objects. It Isn’t main part of project. For further usage a data-base
 * can be preferred.
 */
public class FileInputTaker {

    /**
     * Reads a list of doctors from a text file.
     *
     * @param fileName The path to the doctor data file.
     * @return A list of Doctor objects representing the doctors in the file.
     */
    public static List<Doctor> readDoctorsFromFile(String fileName) {
        List<Doctor> doctors = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(","); // Split line by comma
                if (parts.length == 2) { // Ensure we have both name and department
                    String name = parts[0].trim(); // Remove leading/trailing spaces
                    String departmentStr = parts[1].trim().toUpperCase(); // Convert to uppercase for enum matching
                    try {
                        Department department = Department.valueOf(departmentStr);
                        doctors.add(new Doctor(name, department));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid department: " + departmentStr + " in line: " + line);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return doctors;
    }

    /**
     * Generates patient histories from a text file and adds them to a
     * HistoryOfPatient object.
     *
     * @param doctors A list of Doctor objects representing doctors in the
     * system.
     * @param patients A list of Patient objects representing patients in the
     * system.
     * @param historyFile The path to the patient history data file.
     * @throws IOException If there's an error reading the file.
     */
    public static void generatePatientHistories(List<Doctor> doctors, List<Patient> patients, String historyFile) throws IOException {
        Map<String, List<TempraryHistoryKeeper>> patientHistoryEntries;
        try (BufferedReader reader = new BufferedReader(new FileReader(historyFile))) {
            patientHistoryEntries = new HashMap<>(); // Store multiple histories per patient
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length != 4) {
                    System.err.println("Warning: Invalid line format in history file: " + line);
                    continue;
                }

                String formattedDate = parts[0].trim();
                String doctorName = parts[1].trim();
                String patientName = parts[2].trim();
                String diagnosis = parts[3].trim();

                Doctor doctor = findDoctorByName(doctors, doctorName);
                if (doctor == null) {
                    System.err.println("Warning: Doctor not found: " + doctorName);
                    continue;
                }
                Patient patient = findPatientByName(patients, patientName);
                if (patient == null) {
                    System.err.println("Warning: Patient not found: " + patientName);
                    continue;
                }

                LocalDateTime dateTime = LocalDateTime.parse(formattedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                // Store the history entry
                patientHistoryEntries.computeIfAbsent(patientName, k -> new ArrayList<>()).add(new TempraryHistoryKeeper(dateTime, doctor, diagnosis));
            }
        } // Store multiple histories per patient

        // Add histories for each patient
        for (Patient patient : patients) {
            List<TempraryHistoryKeeper> entries = patientHistoryEntries.get(patient.getName());
            if (entries != null) {
                for (TempraryHistoryKeeper entry : entries) {
                    HistoryOfPatient.addHistory(patient, new History(entry.dateTime, entry.doctor, patient, entry.diagnosis));
                }
            }
        }

        System.out.println("Patient histories generated from diagnosis file.");
    }

    /**
     * Creates a new HistoryEntry.
     *
     * @param dateTime The date and time of the history entry.
     * @param doctor The doctor associated with the history entry.
     * @param diagnosis The diagnosis for the history entry.
     */
    private static class TempraryHistoryKeeper {

        LocalDateTime dateTime;
        Doctor doctor;
        String diagnosis;

        public TempraryHistoryKeeper(LocalDateTime dateTime, Doctor doctor, String diagnosis) {
            this.dateTime = dateTime;
            this.doctor = doctor;
            this.diagnosis = diagnosis;
        }
    }

    /**
     * Finds a doctor by their name in a list of doctors.
     *
     * @param doctors The list of Doctor objects to search in.
     * @param name The name of the doctor to find.
     * @return The Doctor object if found, null otherwise.
     */
    private static Doctor findDoctorByName(List<Doctor> doctors, String name) {
        for (Doctor doctor : doctors) {
            if (doctor.getName().equals(name)) {
                return doctor;
            }
        }
        return null;
    }

    /**
     * Finds a patient by their name in a list of patients.
     *
     * @param patients The list of Patient objects to search in.
     * @param name The name of the patient to find.
     * @return The Patient object if found, null otherwise.
     */
    private static Patient findPatientByName(List<Patient> patients, String name) {

        for (Patient patient : patients) {
            if (patient.getName().equals(name)) {
                return patient;
            }
        }
        return null;
    }

}
