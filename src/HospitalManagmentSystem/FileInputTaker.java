/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HospitalManagmentSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FileInputTaker {
     public static List<Doctor> readDoctorsFromFile(String fileName) {
        List<Doctor> doctors = new LinkedList<>();
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
                        System.err.println("Invalid department: " + departmentStr + " in line: " + line);
                    }
                } else {
                    System.err.println("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return doctors;
    }
     
     
     
     
    public static void generatePatientHistories(List<Doctor> doctors, List<Patient> patients, String historyFile, HistoryOfPatient patientsHistory) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(historyFile));
        Map<String, List<HistoryEntry>> patientHistoryEntries = new HashMap<>(); // Store multiple histories per patient

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
            patientHistoryEntries.computeIfAbsent(patientName, k -> new ArrayList<>()).add(new HistoryEntry(dateTime, doctor, diagnosis));
        }
        reader.close();

        // Add histories for each patient
        for (Patient patient : patients) {
            List<HistoryEntry> entries = patientHistoryEntries.get(patient.getName());
            if (entries != null) {
                for (HistoryEntry entry : entries) {
                    patientsHistory.addHistory(patient, new History(entry.dateTime, entry.doctor, patient, entry.diagnosis));
                }
            }
        }

        System.out.println("Patient histories generated from diagnosis file.");
    }

    private static class HistoryEntry {
        LocalDateTime dateTime;
        Doctor doctor;
        String diagnosis;

        public HistoryEntry(LocalDateTime dateTime, Doctor doctor, String diagnosis) {
            this.dateTime = dateTime;
            this.doctor = doctor;
            this.diagnosis = diagnosis;
        }
    }

    private static Doctor findDoctorByName(List<Doctor> doctors, String name) {
        // ... (same as before)
        for (Doctor doctor : doctors) {
            if (doctor.getName().equals(name)) {
                return doctor;
            }
        }
        return null;
    }

    private static Patient findPatientByName(List<Patient> patients, String name) {
        // ... (same as before)
        for (Patient patient : patients) {
            if (patient.getName().equals(name)) {
                return patient;
            }
        }
        return null;
    }
     
     
}
