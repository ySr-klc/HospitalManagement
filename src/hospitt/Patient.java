package hospitt;

import java.util.ArrayList;
import java.util.List;

class Patient extends Person {
    List<String> medicalHistory;

    Patient(String name) {
        super(name);
        this.medicalHistory = new ArrayList<>();
    }

    void addMedicalHistory(String record) {
        medicalHistory.add(record);
    }

    void viewMedicalHistory() {
        System.out.println("Medical History of " + name + ":");
        for (String record : medicalHistory) {
            System.out.println("  - " + record);
        }
    }

    @Override
    void displayInfo() {
        System.out.println("Patient: " + name);
    }
}
