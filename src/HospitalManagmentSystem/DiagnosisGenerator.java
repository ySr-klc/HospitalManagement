
package HospitalManagmentSystem;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DiagnosisGenerator {

    private static final Map<Department, String[]> DIAGNOSES = new HashMap<>();

    static {
        DIAGNOSES.put(Department.UROLOGY, new String[]{"Urinary Tract Infection (UTI)", "Kidney Stones", "Benign Prostatic Hyperplasia (BPH)", "Prostatitis"});
        DIAGNOSES.put(Department.RADIOLOGY, new String[]{"Fracture (X-ray)", "Pneumonia (Chest X-ray)", "Tumor (MRI)", "Arthritis (X-ray)"});
        DIAGNOSES.put(Department.CARDIOLOGY, new String[]{"Hypertension", "Coronary Artery Disease", "Arrhythmia", "Heart Failure"});
        DIAGNOSES.put(Department.DERMATOLOGY, new String[]{"Eczema", "Psoriasis", "Acne", "Skin Cancer"});
        DIAGNOSES.put(Department.GASTROENTEROLOGY, new String[]{"Gastroesophageal Reflux Disease (GERD)", "Irritable Bowel Syndrome (IBS)", "Ulcerative Colitis", "Crohn's Disease"});
        DIAGNOSES.put(Department.NEUROLOGY, new String[]{"Migraine", "Epilepsy", "Multiple Sclerosis", "Parkinson's Disease"});
        DIAGNOSES.put(Department.OBSTETRICS_GYNECOLOGY, new String[]{"Pregnancy Checkup", "Pelvic Inflammatory Disease (PID)", "Endometriosis", "Menopause"});
        DIAGNOSES.put(Department.OPHTHALMOLOGY, new String[]{"Cataracts", "Glaucoma", "Macular Degeneration", "Dry Eye Syndrome"});
        DIAGNOSES.put(Department.ORTHOPEDICS, new String[]{"Osteoarthritis", "Sprain", "Fracture", "Tendonitis"});
        DIAGNOSES.put(Department.PEDIATRICS, new String[]{"Common Cold", "Ear Infection", "Chickenpox", "Asthma"});
        DIAGNOSES.put(Department.PSYCHIATRY, new String[]{"Depression", "Anxiety", "Bipolar Disorder", "Schizophrenia"});
    }

    public static void generateDiagnoses(List<Doctor> doctors, List<Patient> patients, String outputFile, int numLines) {
        Random random = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            for (int i = 0; i < numLines; i++) {
                Doctor randomDoctor = doctors.get(random.nextInt(doctors.size()));
                Patient randomPatient = patients.get(random.nextInt(patients.size()));
                LocalDate randomDate = LocalDate.now().minusDays(random.nextInt(365));
                String formattedDate = randomDate.format(formatter);


                String[] possibleDiagnoses = DIAGNOSES.get(randomDoctor.getSpeciality());
                String diagnosis = "Unknown"; // Default value
                if (possibleDiagnoses != null && possibleDiagnoses.length > 0) {
                    diagnosis = possibleDiagnoses[random.nextInt(possibleDiagnoses.length)];
                }

                String line = String.format("%s | %s | %s | %s", formattedDate, randomDoctor.getName(), randomPatient.getName(), diagnosis);
                writer.write(line + "\n");
            }
            System.out.println("Diagnoses written to " + outputFile);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

 
}