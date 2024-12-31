
package HospitalManagmentSystem;

import SystemInitializer.FileInputTaker;
import Persons.Patient;
import Persons.Doctor;
import Search.Search;
import History.History;
import History.HistoryOfPatient;
import Clinics.Department;
import Clinics.Clinic;
import Clinics.ClinicsManager;
import Appointments.AppointmentManager;
import Appointments.AppointmentSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HospitalManagementSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final AppointmentManager appointmentManager;
    private static final List<Doctor> doctors;
    private static final List<Patient> patients;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy, dd MMM, EEEE  HH.mm", Locale.ENGLISH);
    private static final ClinicsManager clinicManager;
    private static final HistoryOfPatient patientsHistory;
    private static final Search searchPanel;

    static {
        // Initialize sample data
        doctors = FileInputTaker.readDoctorsFromFile("doctors.txt");
        doctors.sort(Comparator.comparing(Doctor::getSpeciality));
        patients = new ArrayList<>();
        patients.add(new Patient("Yasir Kilic"));
        patients.add(new Patient("Berkay Simsek"));
       
        clinicManager = new ClinicsManager(doctors);
        patientsHistory = new HistoryOfPatient(patients);
        appointmentManager = new AppointmentManager(doctors);
        searchPanel = new Search(doctors);
        clinicManager.connectAppointmentManager(appointmentManager);
        try {
            FileInputTaker.generatePatientHistories(doctors, patients, "diagnoses.txt");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }   
        Random rand = new Random();
        int[] days = {1, 2, 3, 4, 5}, duration = {10, 15, 30};
        int randomStarter, rndmStarter2;
        for (Doctor doctor : doctors) {
            randomStarter = rand.nextInt(100) % 3;
            rndmStarter2 = rand.nextInt(100) % 5;
            appointmentManager.createDoctorAppointments(doctor, days[rndmStarter2], duration[randomStarter]);
        }
    }
    public static void main(String[] args) { mainMenu();}
    private static void mainMenu() {
        while (true) {
            System.out.println("\nHello welcome to Hospital Management System");
            System.out.println("1. Doctor");
            System.out.println("2. Patient");
            System.out.println("3. Clinic");
            System.out.println("4. Exit");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 ->
                        doctorMenu(selectDoctor());
                case 2 ->
                        patientMenu(selectPatient());
                case 3 ->
                        clinicMenu();
                case 4 -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default ->
                        System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void clinicMenu() {
        List<Department> clinics = Arrays.asList(Department.values());
        while (true) {
            System.out.println("\nWelcome to the clinic page!");
            for (int i = 0; i < clinics.size(); i++) {
                System.out.println((i + 1) + ". " + clinics.get(i).name().toUpperCase(Locale.ENGLISH));
            }
            System.out.println("Select clinic that you want to visit(back menu 0)");
            int choice = getIntInput("Enter your choice: ") - 1;

            if (choice == -1) {
                return;
            }

            if (choice < 0 || choice >= clinics.size()) {
                System.out.println("Invalid clinic selection. Please try again.");
                continue;
            }

            try {
                Clinic selectedClinic = clinicManager.getClinic(clinics.get(choice));
                clinicInsideMenu(selectedClinic);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }
    }
    private static void doctorMenu(Doctor selectedDoctor) {
        while (true) {
            if (selectedDoctor == null) {
                return;
            }
            System.out.println("\nDoctor Menu");
            System.out.println("1. Schedule Appointment");
            System.out.println("2. View Current Appointments");
            System.out.println("3. View Taken Appointments");
            System.out.println("4. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 ->
                        scheduleAppointmentMenu(selectedDoctor);
                case 2 ->
                        viewDoctorAppointments(selectedDoctor);
                case 3 ->
                        viewTakenDoctorAppointments(selectedDoctor);
                case 4 -> {
                    return;
                }
                default ->
                        System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void viewTakenDoctorAppointments(Doctor selectedDoctor) {
        List<AppointmentSlot> slots = appointmentManager.viewTakenDoctorAppointments(selectedDoctor);
        if (slots.isEmpty()) {
            System.out.println("\n There is no booked appointment yet");
            return;
        }
        try {
            System.out.println("\nTaken Appointments for Dr. " + selectedDoctor.getName());
            System.out.println("-----------------------------------------");
            int i = 1;
            for (AppointmentSlot slot : slots) {

                if (slot.isBooked()) { // Check if appointment is booked
                    System.out.printf("%s. Date/Time: %s, Patient: %s%n",
                            i,
                            slot.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            slot.getPatientName());
                }
                i++;
            }
            int choice = getIntInput("If you want to configure an element enter your choice(exit=0): ") - 1;
            if (choice == -1) {
                return;
            }

            appointmentHandle(slots.get(choice));
        } catch (Exception e) {
            System.out.println("Exception found" + e.getMessage());
        }
    }
    private static void appointmentHandle(AppointmentSlot slot) {
        System.out.printf("Date/Time: %s, Patient: %s%n",
                slot.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                slot.getPatientName());
        System.out.println("Did patient come to appointment? ");
        System.out.println("1.Yes");
        System.out.println("2.No");
        int choice = getIntInput("Enter your choice: ");
        if (choice == 2) {
            slot.setPatientAttended(false);
            System.out.println("Thank for your time");
            appointmentManager.passedAppointmentHandler(slot);
        } else if (choice == 1) {
            while(true){
            slot.setPatientAttended(true);
            System.out.println("1. Add diagnose and finish");
            System.out.println("2. Return back menu");
            choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 -> {
                    System.out.println("Diagnose:");
                    String diagnose = scanner.nextLine();
                    slot.setDiagnosis(diagnose);
                    appointmentManager.passedAppointmentHandler(slot);
                }
                case 2 -> {
                    return;
                }
                default ->
                        System.out.println("Invalid choice. Please try again.");
            }
        }
        }
    }

    private static void scheduleAppointmentMenu(Doctor selectedDoctor) {
        while (true) {
            System.out.println("\nSchedule Appointment Menu");
            // Show current appointments for the selected doctor
            List<AppointmentSlot> currentAppointments = appointmentManager.getAllAppointmentsForDoctor(selectedDoctor.getId());
            if (!currentAppointments.isEmpty()) {
                System.out.println("\nCurrent appointments for " + selectedDoctor.getName() + ":");
                displayAppointmentsByDay(currentAppointments);
            }
            System.out.println("\n1. Schedule New Days");
            System.out.println("2. Cancel a Specific Day");
            System.out.println("3. Cancel Specific Time Range");
            System.out.println("4. Rearrange Hours Configuration");
            System.out.println("5. Back to Previous Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 ->
                        scheduleNewDays(selectedDoctor);
                case 2 ->
                        cancelSpecificDay(selectedDoctor);
                case 3 ->
                        cancelTimeRange(selectedDoctor);
                case 4 ->
                        rearrangeHours(selectedDoctor);
                case 5 -> {
                    return;
                }
                default ->
                        System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void patientMenu(Patient selectedPatient) {
        if (selectedPatient == null) {
            return;
        }
        while (true) {
            System.out.println("\nPatient Menu");
            System.out.println("-----------------"+selectedPatient.getName()+"----------------");
            System.out.println("1. View Appointments");
            System.out.println("2. Search Appointment");
            System.out.println("3. Show History");
            System.out.println("4. Notifications");
            System.out.println("5. Cancel Appointment");
            System.out.println("6. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 ->
                        viewPatientAppointments(selectedPatient);
                case 2 ->
                        searchAndTakeAppointment(selectedPatient);
                case 3 ->
                        showPatientHistory(selectedPatient);
                case 4 ->
                        showPatientNotifications(selectedPatient);
                case 5 ->
                        cancelAppointment(selectedPatient);
                case 6 -> {
                    return;
                }
                default ->
                        System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void searchAndTakeAppointment(Patient patient) {
        while (true) {
            System.out.println("\nSearch panel.");
            System.out.println("1. Search for doctor");
            System.out.println("2. Search for clinic");
            System.out.println("3. Back to up menu");

            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 ->
                        searchForDoctor(patient);
                case 2 ->
                        searchForClinic(patient);
                case 3 -> {
                    return;
                }
                default ->
                        System.out.println("Invalid choic. please try again.");
            }
        }
    }
    private static void searchForDoctor(Patient patient) {
        try {
            System.out.println("Doctor name:");
            String word = scanner.nextLine();
            List<Map.Entry<String, Integer>> searchResult = searchPanel.searchForDoctor(word);

            if (searchResult.isEmpty()) {
                System.out.println("No doctors found matching that name.");
                return; // Exit the method if no results
            }

            int i = 1;
            for (Map.Entry<String, Integer> entry : searchResult) {
                Doctor doctor = doctors.stream()
                        .filter(d -> d.getId()==entry.getValue())  // Filter by matching ID
                        .findFirst()                                 // Get the first matching doctor
                        .orElse(null);
                assert doctor != null;
                System.out.println(i++ + ". " + doctor.getName()+ " - Clinic: "+doctor.getSpeciality().toString().toUpperCase(Locale.ENGLISH));            }

            int choice = getIntInput("\nSelect the doctor you want to take an appointment with: ") - 1;

            if (choice < 0 || choice >= searchResult.size()) {
                throw new IndexOutOfBoundsException("Invalid doctor selection.");
            }

            int doctorId = searchResult.get(choice).getValue();
            AppointmentSlot doctorAppointments = appointmentManager.getNearestAvailableAppointmentSlot(doctorId);
            System.out.println("Nearest appointment for Dr."+doctorAppointments.getDocName()+" at Time"+doctorAppointments.getTime().format(formatter));
            System.out.println("1. Select nearst appointment");
            System.out.println("2. View avaliable appointments");
            System.out.println("3. Back to main");
            choice=getIntInput("Enter your choice:");
            switch (choice) {
                case 1 ->{
                        appointmentManager.bookAppointment(doctorId, patient, doctorAppointments.getTime());
                        System.out.println("Appointment Booked Succesfuly!");}
                case 2 ->
                        viewAllDoctorAppointments(patient, doctorId);
                case 3 -> {
                }
                default -> System.out.println("Invalid input. Please try again!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Clear the invalid input
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error booking appointment: " + e.getMessage());
        }
    }
    private static void searchForClinic(Patient patient) {
        try {
            List<Department> clinics = Arrays.asList(Department.values());
            System.out.println("\nWelcome to the clinic page!");

            for (int i = 0; i < clinics.size(); i++) {
                System.out.println((i + 1) + ". " + clinics.get(i).name().toUpperCase(Locale.ENGLISH));
            }
            int choice = getIntInput("Enter clinic number (1-" + clinics.size() + "): ");

            if (choice < 1 || choice > clinics.size()) {
                throw new IndexOutOfBoundsException("Invalid clinic selection.");
            }
            Department selectedClinic = clinics.get(choice - 1);
            List<AppointmentSlot> appointmentsFromClinic = clinicManager.getAppointmentsFromClinic(selectedClinic);

            if (appointmentsFromClinic == null || appointmentsFromClinic.isEmpty()) {
                System.out.println("No appointments found in this clinic.");
                return;
            }
            int i = 1;
            for (AppointmentSlot appointmentSlot : appointmentsFromClinic) {
                System.out.println(i++ + ". " + appointmentSlot);
            }
            choice = getIntInput("Select appointment:");

            if (choice < 1 || choice > appointmentsFromClinic.size()) {
                throw new IndexOutOfBoundsException("Invalid appointment selection.");
            }

            appointmentManager.bookAppointment(appointmentsFromClinic.get(choice - 1).getDoc().getId(), patient, appointmentsFromClinic.get(choice - 1).getTime());
            System.out.println("Appointment booked successfully!");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Clear the invalid input
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error booking appointment: " + e.getMessage());
        }
    }
    private static void cancelAppointment(Patient patient) {
        List<AppointmentSlot> patientAppointments = patient.getPatientAppointments();
        while (true) {
            try {
                if (patientAppointments.isEmpty()) {
                    System.out.println("\n There is no appointmnet");
                    return;
                }
                int i = 1;
                for (AppointmentSlot patientAppointment : patientAppointments) {
                    System.out.println(i + ". " + patientAppointment);
                    i++;
                }
                int choice = getIntInput("\nSelect appointment that you want to cancel \n for up menu zero(0)\n Selecteion: ") - 1;

                if (choice == -1) {
                    System.out.println("Returning up menu...");
                    return;
                }

                if (choice < 0 || choice > patientAppointments.size()) {
                    throw new IndexOutOfBoundsException("Invalid number");
                }

                appointmentManager.cancelPatientAppointment(patient, patient.getPatientAppointments().get(choice));
            } catch (Exception e) {
                System.out.println("Operations failed!" + e.getMessage());
                return;
            }
        }
    }
    private static void showPatientNotifications(Patient patient) {
        Stack<String> not = (Stack<String>) patient.getNotifications().clone();
        if (not.isEmpty()) {
            System.out.println("\n There is no notification yet!");
            return;
        }
        while (true) {
            for (int i = 0; i < not.size(); i++) {
                System.out.println(not.pop());
            }
            System.out.println("\n Do you want to clear Notifications?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            int choice = getIntInput("Enter your choice: ");
            switch (choice) {
                case 1 ->
                        patient.getNotifications().clear();
                case 2 -> {
                    return;
                }
                default ->
                        System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void scheduleNewDays(Doctor doctor) {
        System.out.println("\nChoose number of days to schedule:");
        System.out.println("1. 1 days");
        System.out.println("1. 2 days");
        System.out.println("1. 3 days");
        System.out.println("1. 4 days");
        System.out.println("2. 5 days");

        int daysChoice = getIntInput("Enter your choice: ");
        int days = switch (daysChoice) {
            case 1 ->
                    1;
            case 2 ->
                    2;
            case 3 ->
                    3;
            case 4 ->
                    4;
            case 5 ->
                    5;

            default -> {
                System.out.println("Invalid choice. Returning to menu.");
                yield 0;
            }
        };

        if (days > 0) {
            System.out.println("\nChoose appointment duration:");
            System.out.println("1. 10 minutes");
            System.out.println("2. 15 minutes");
            System.out.println("3. 30 minutes");

            int durationChoice = getIntInput("Enter your choice: ");
            int duration = switch (durationChoice) {
                case 1 ->
                        10;
                case 2 ->
                        15;
                case 3 ->
                        30;
                default -> {
                    System.out.println("Invalid choice. Returning to menu.");
                    yield 0;
                }
            };

            if (duration > 0) {
                appointmentManager.createDoctorAppointments(doctor, days, duration);
                System.out.println("Appointments scheduled successfully!");
            }
        }
    }
    private static void cancelSpecificDay(Doctor doctor) {
        System.out.println("Enter date to cancel (yyyy-MM-dd):");
        String dateStr = scanner.nextLine();
        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalDateTime dateTime = date.atStartOfDay();
            appointmentManager.cancelDoctorDay(doctor.getId(), dateTime);
            System.out.println("Appointments for " + dateStr + " cancelled successfully!");
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd");
        }
    }
    private static void cancelTimeRange(Doctor doctor) {
        System.out.println("Enter start date and time (yyyy-MM-dd HH:mm):");
        String startStr = scanner.nextLine();
        System.out.println("Enter end date and time (yyyy-MM-dd HH:mm):");
        String endStr = scanner.nextLine();

        try {
            LocalDateTime start = LocalDateTime.parse(startStr, formatter);
            LocalDateTime end = LocalDateTime.parse(endStr, formatter);

            // Cancel appointments within the range
            List<AppointmentSlot> appointments = appointmentManager.getAllAppointmentsForDoctor(doctor.getId());
            for (AppointmentSlot slot : appointments) {
                if (!slot.getTime().isBefore(start) && !slot.getTime().isAfter(end)) {
                    appointmentManager.cancelAppointment(doctor.getId(), slot.getTime());
                }
            }
            System.out.println("Appointments cancelled successfully!");
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm");
        }
    }
    private static void rearrangeHours(Doctor doctor) {
        System.out.println("Enter date to rearrange (yyyy-MM-dd):");
        String dateStr = scanner.nextLine();
        System.out.println("\nChoose new appointment duration:");
        System.out.println("1. 10 minutes");
        System.out.println("2. 15 minutes");
        System.out.println("3. 30 minutes");

        int durationChoice = getIntInput("Enter your choice: ");
        int duration = switch (durationChoice) {
            case 1 ->
                    10;
            case 2 ->
                    15;
            case 3 ->
                    30;
            default -> {
                System.out.println("Invalid choice. Returning to menu.");
                yield 0;
            }
        };

        if (duration > 0) {
            try {
                LocalDateTime dateTime = LocalDate.parse(dateStr).atStartOfDay();
                appointmentManager.changeAppointmentDuration(doctor, duration, dateTime);
                System.out.println("Appointments rearranged successfully!");
            } catch (IllegalArgumentException a) {
                System.out.println(a.getMessage());
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd");
            }
        }
    }
    private static void viewDoctorAppointments(Doctor doctor) {
        if (doctor != null) {
            List<AppointmentSlot> appointments = appointmentManager.getAllAppointmentsForDoctor(doctor.getId());
            if (appointments.isEmpty()) {
                System.out.println("No appointments found.");
                return;
            }
            displayAppointmentsByDay(appointments);
        }
    }
    private static void viewPatientAppointments(Patient patient) {
        List<AppointmentSlot> appointments = patient.getPatientAppointments();
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }
        System.out.println("\nYour appointments:");
        for (AppointmentSlot slot : appointments) {
            System.out.println(slot);
        }
    }
    private static Doctor selectDoctor() {
        if (doctors == null || doctors.isEmpty()) {
            System.out.println("No doctors available.");
            return null;
        }

        while (true) { // Loop until a valid choice is made or the user goes back
            System.out.println("\nAvailable doctors:");
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". " + doctors.get(i));
            }
            System.out.println("0. Back to previous menu");

            int choice = getIntInput("Select doctor: ");

            if (choice > 0 && choice <= doctors.size()) {
                return doctors.get(choice - 1);
            } else if (choice == 0) {
                return null;
            } else {
                System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }
    private static Patient selectPatient() { // Take the list as a parameter
        if (patients == null || patients.isEmpty()) {
            System.out.println("No patients available.");
            return null;
        }

        while (true) { // Loop until a valid choice is made or the user goes back
            System.out.println("\nAvailable patients:");
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i));
            }
            System.out.println((patients.size() + 1) + ". Back to previous menu");
            System.out.println("0. Add new patient");
            int choice = getIntInput("Select patient: ");

            if (choice > 0 && choice <= patients.size()) {
                return patients.get(choice - 1);
            } else if (choice == 0) {
                return addNewPatient();
            } else if (choice == patients.size() + 1) {
                return null;
            } else {
                System.out.println("\nInvalid choice. Please try again.");
                // No need to return null here; the loop continues
            }
        }
    }
    private static Patient addNewPatient() {
        try {
            System.out.println("Patient Name");
            String name = scanner.nextLine();
            System.out.println("Surname:");
            String surname = scanner.nextLine();
            Patient newPat = new Patient(name);
            patients.add(newPat);
            patientsHistory.updateList(patients);
            return newPat;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
        private static void viewAllDoctorAppointments(Patient patient, int doctorId) {

            while (true) {
                List<AppointmentSlot> appointments = appointmentManager.getAvailableAppointments(doctorId);
                if (appointments.isEmpty()) {
                    System.out.println("No appointments available for this doctor.");
                    return;
                }
                displayAvailableAppointments(appointments);
                System.out.println("\n1. Book an appointment");
                System.out.println("2. Back to menu");

                int choice = getIntInput("Enter your choice: ");
                if (choice == 1) {
                    int slotIndex = getIntInput("Enter appointment number to book: ") - 1;
                    if (slotIndex < 0 || choice >= appointments.size()) {
                        throw new IndexOutOfBoundsException("Invalid appointment selection.");
                    }
                    appointmentManager.bookAppointment(doctorId, patient, appointments.get(choice).getTime());
                    System.out.println("Appointment booked successfully!");
                }if (choice==2) {
                    return;
                }else{
                    System.out.println("Invalid Input. Please try again!");
                }
            }
        }
    private static void showPatientHistory(Patient patient) {
        List<History> histories = patientsHistory.getHistory(patient).stream().toList();
        if (histories.isEmpty()) {
            System.out.println("No history available.");
            return;
        }
        System.out.println("\nHistory:");
        for (History history : histories) {
            System.out.println("- " + history);
        }
    }
    private static void displayAppointmentsByDay(List<AppointmentSlot> appointments) {
        Map<LocalDate, List<AppointmentSlot>> appointmentsByDay = new TreeMap<>();

        // Group appointments by day
        for (AppointmentSlot slot : appointments) {
            LocalDate date = slot.getTime().toLocalDate();
            appointmentsByDay.computeIfAbsent(date, k -> new ArrayList<>()).add(slot);
        }

        // Display appointments grouped by day
        for (Map.Entry<LocalDate, List<AppointmentSlot>> entry : appointmentsByDay.entrySet()) {
            System.out.println("\nDate: " + entry.getKey());
            for (AppointmentSlot slot : entry.getValue()) {
                System.out.println("  " + slot.getTime().toLocalTime()
                        + " - " + slot.getEndTime().toLocalTime()
                        + (slot.isBooked() ? " (Booked by " + slot.getPatientName() + ")" : " (Available)"));
            }
        }
    }
    private static void displayAvailableAppointments(List<AppointmentSlot> appointments) {
        System.out.println("\nAvailable appointments:");
        int counter = 1;
        LocalDate day = LocalDate.MIN;
        for (AppointmentSlot slot : appointments) {
            if (!slot.isBooked()) {
                if (!day.isEqual(slot.getTime().toLocalDate())) {
                    day = slot.getTime().toLocalDate();
                    System.out.println(day.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy", Locale.ENGLISH)));
                }
                System.out.println(counter + ". "
                        + slot.getTime().format(DateTimeFormatter.ofPattern("HH.mm"))
                        + " - "
                        + slot.getEndTime().format(DateTimeFormatter.ofPattern("HH.mm")));
                counter++;
            }
        }
    }
       private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    private static void clinicInsideMenu(Clinic clinic) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nWelcome to " + clinic.getDepartmentKey().toString() + " clinic.");
            System.out.println("---------------------------------");
            System.out.println("1. View Doctors");
            System.out.println("2. Add Doctor");
            System.out.println("3. Delete Doctor");
            System.out.println("0. Back to Main Menu");
            System.out.println("---------------------------------");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 ->
                        viewDoctors(clinic);
                case 2 ->
                        addDoctor(clinic, scanner);
                case 3 ->
                        deleteDoctor(clinic, scanner);
                case 0 -> {
                    return; // Go back to the main menu
                }
                default ->
                        System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void viewDoctors(Clinic clinic) {
        List<Doctor> doctors = clinic.getDoctors();
        if (doctors.isEmpty()) {
            System.out.println("No doctors in this clinic.");
        } else {
            System.out.println("Doctors in " + clinic.getDepartmentKey() + " clinic:");
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". " + doctors.get(i)); // Assuming Doctor has a toString() method
            }
        }
    }
    private static void addDoctor(Clinic clinic, Scanner scanner) {
        System.out.print("Enter doctor's name: ");
        String name = scanner.nextLine();
        scanner.nextLine();
        Doctor newDoctor = new Doctor(name, clinic.getDepartmentKey()); // You'll need to adapt this

        try {
            clinic.addDoctor(newDoctor);
            doctors.add(newDoctor);
            appointmentManager.update(doctors);
            System.out.println("Doctor added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
    private static void deleteDoctor(Clinic clinic, Scanner scanner) {
        viewDoctors(clinic); // Display doctors for selection
        List<Doctor> doctors = clinic.getDoctors();
        if (doctors.isEmpty()) {
            return;
        }
        System.out.print("Enter the number of the doctor to delete: ");
        int doctorIndex = scanner.nextInt();
        scanner.nextLine();
        if (doctorIndex > 0 && doctorIndex <= doctors.size()) {
            clinic.deleteDoctor(doctors.get(doctorIndex - 1));
            System.out.println("Doctor deleted successfully.");
        } else {
            System.out.println("Invalid doctor number.");
        }
    }
}