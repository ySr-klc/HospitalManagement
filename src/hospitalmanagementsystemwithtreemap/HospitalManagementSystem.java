/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import static java.time.DayOfWeek.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HospitalManagementSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AppointmentManager appointmentManager;
    private static final List<Doctor> doctors;
    private static final List<Patient> patients;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final ClinicsManager clinicManager;
    private static final HistoryOfPatient patientsHistory;
    private static final Search searchPanel;
    static {
        // Initialize sample data
       
        doctors = new ArrayList<>();
        doctors.add(new Doctor("John", ClinicEnums.Department.CARDIOLOGY));
        doctors.add(new Doctor("Sarah", ClinicEnums.Department.PEDIATRICS));
        doctors.add(new Doctor("Michael", ClinicEnums.Department.NEUROLOGY));
        doctors.add(new Doctor("Markus", ClinicEnums.Department.NEUROLOGY));
        doctors.add(new Doctor("Sedric", ClinicEnums.Department.PEDIATRICS));
        patients = new ArrayList<>();
        patients.add(new Patient("Alice"));
        patients.add(new Patient("Bob"));
        
        clinicManager=new ClinicsManager(doctors);
        patientsHistory=new HistoryOfPatient(patients);
        appointmentManager = new AppointmentManager(doctors,patientsHistory,clinicManager);
        searchPanel=new Search(doctors);
        clinicManager.connectAppointmentManager(appointmentManager);
        
        
        Random rand = new Random();
        int[] days = {1, 2, 3,4,5}, duration = {10, 15, 30};
        int randomStarter, rndmStarter2;
        for (Doctor doctor : doctors) {
            randomStarter = rand.nextInt(100) % 3;
            rndmStarter2= rand.nextInt(100)% 5;
            appointmentManager.createDoctorAppointments(doctor, days[rndmStarter2], duration[randomStarter]);

        }
        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                appointmentManager.passedAppointmentHandler();
            }
            
            };
        timer.schedule(task,2000,5000);
    }

    public static void main(String[] args) {
        
        mainMenu();
    }

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
                    patientMenu();
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
    
    private static void clinicMenu(){
        List<ClinicEnums.Department> clinics=Arrays.asList(ClinicEnums.Department.values());
        while (true) {            
            System.out.println("\nWelcome to the clinic page!");
            for (int i = 0; i < clinics.size(); i++) { 
                System.out.println(i + ". " + clinics.get(i));
            }
            System.out.println("Select clinic that you want to visit(back menu -1)");
            int choice = getIntInput("Enter your choice: ");
            
            if (choice==-1)  return; 
            
            if (choice < 0 || choice >= clinics.size()) { 
                System.out.println("Invalid clinic selection. Please try again.");
                continue; 
            }
            
            try {
                Clinic selectedClinic=clinicManager.getClinic(clinics.get(choice));
                 clinicInsideMenu(selectedClinic);
            } catch (Exception e) {
                System.out.println("Exception: "+e.getMessage());
            }
        }
    }
    
    
    
   private static void doctorMenu(Doctor selectedDoctor) {
    while (true) {
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

// New function to display taken appointments
private static void viewTakenDoctorAppointments(Doctor selectedDoctor) {
   List<AppointmentSlot> slots=appointmentManager.viewTakenDoctorAppointments(selectedDoctor);
    if (slots.isEmpty()) {
        System.out.println("\n There is no booked appointment yet");return;
    }
    try {
         System.out.println("\nTaken Appointments for Dr. " + selectedDoctor.getName());
    System.out.println("-----------------------------------------");
    int i=1;
    for  (AppointmentSlot slot : slots){
        
        if (slot.isBooked()) { // Check if appointment is booked
            System.out.println(String.format("%s. Date/Time: %s, Patient: %s",
                    i,
                    slot.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    slot.getPatientName()));
        }
        i++;
    }
    int choice = getIntInput("If you want to configure an element enter your choice(exit=0): ")-1;
        if (choice==-1)  return;
    
    appointmentHandle(slots.get(choice));
     
        
    } catch (Exception e) {
        System.out.println("Exception found"+e.getMessage());
    }
   
}
    private static void appointmentHandle(AppointmentSlot slot){
        System.out.println(String.format("Date/Time: %s, Patient: %s",
                    slot.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    slot.getPatientName()));
        System.out.println("Did patient come to appointment? ");
        System.out.println("1.Yes");
        System.out.println("2.No");
        int choice = getIntInput("Enter your choice: ");
        if (choice==1) {
            slot.setIsPatientCome(false);
            System.out.println("Thank for your time");
            appointmentManager.passedAppointmentHandler(slot);
        }else if(choice==2){
        System.out.println("1. Add diagnose and finish");
        System.out.println("2. Return back menu");
       
        switch (choice) {
            case 1 -> {
                String diagnose=scanner.nextLine();
                slot.setDiagnosis(diagnose);
                appointmentManager.passedAppointmentHandler(slot);
            }
            case 2 -> {
                return;
            }
            default -> System.out.println("Invalid choice. Please try again.");
        }
        }
    }




    private static void scheduleAppointmentMenu(Doctor selectedDoctor) {
        while (true) {
            System.out.println("\nSchedule Appointment Menu");

            if (selectedDoctor == null) {
                return;
            }

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

    private static void patientMenu() {
        while (true) {
            System.out.println("\nPatient Menu");
            Patient selectedPatient = selectPatient();
            if (selectedPatient == null) {
                return;
            }

            System.out.println("\n1. View Appointments");
            System.out.println("2. Take Appointment");
            System.out.println("3. Search Appointment");
            System.out.println("4. Show History");
            System.out.println("5. Notifications");
            System.out.println("6. Cancel Appointment");
            System.out.println("7. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 ->
                    viewPatientAppointments(selectedPatient);
                case 2 ->
                    takeAppointmentMenu(selectedPatient);
                case 3 ->
                    searchAndTakeAppointment(selectedPatient);
                case 4 ->
                    showPatientHistory(selectedPatient);
                case 5 ->
                    showPatientNotifications(selectedPatient);
                case 6 ->
                    cancelAppointment(selectedPatient);
                case 7 -> {
                    return;
                }
                default ->
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private static void searchAndTakeAppointment(Patient patient){
        System.out.println("\nSearch panel.");
        System.out.println("1. Search for doctor");
        System.out.println("2. Search for clinic");
        System.out.println("3. Back to up menu");
        int choice=getIntInput("Enter your choice: ");
        switch (choice) {
            case 1->
                searchForDoctor(patient);
            case 2 -> {
                searchForClinic(patient);
            }
            default -> throw new AssertionError();
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
            System.out.println(i++ + ". " + entry.getKey());
        }

        int choice = getIntInput("\nSelect the doctor you want to take an appointment with: ") - 1;

        if (choice < 0 || choice >= searchResult.size()) {
            throw new IndexOutOfBoundsException("Invalid doctor selection.");
        }

        int doctorId = searchResult.get(choice).getValue();
        List<AppointmentSlot> doctorAppointments = appointmentManager.getNearestAvailableAppointmentSlots(doctorId);

        if (doctorAppointments == null || doctorAppointments.isEmpty()) {
            System.out.println("No available appointments found for this doctor.");
            return;
        }

        for (int j = 0; j < doctorAppointments.size(); j++) {
            System.out.println((j + 1) + ". " + doctorAppointments.get(j)); // Corrected toString usage
        }

        choice = getIntInput("Select the appointment you want to take: ") - 1;

        if (choice < 0 || choice >= doctorAppointments.size()) {
            throw new IndexOutOfBoundsException("Invalid appointment selection.");
        }

        appointmentManager.bookAppointment(doctorId, patient, doctorAppointments.get(choice).getTime());
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

private static void searchForClinic(Patient patient) {
    try {
        System.out.println("Clinic that you are looking for: ");
        String word = scanner.nextLine();
        List<Map.Entry<String, ClinicEnums.Department>> searchResult = searchPanel.searchForClinic(word);

        if (searchResult.isEmpty()) {
            System.out.println("No clinics found matching that name.");
            return;
        }

        int i = 1;
        for (Map.Entry<String, ClinicEnums.Department> entry : searchResult) {
            System.out.println(i++ + ". " + entry.getKey());
        }

        int choice = getIntInput("Selected clinic: ") - 1;

        if (choice < 0 || choice >= searchResult.size()) {
            throw new IndexOutOfBoundsException("Invalid clinic selection.");
        }

        List<AppointmentSlot> appointmentsFromClinic = clinicManager.getAppointmentsFromClinic(searchResult.get(choice).getValue());

        if (appointmentsFromClinic == null || appointmentsFromClinic.isEmpty()) {
            System.out.println("No appointments found in this clinic.");
            return;
        }

        i = 1;
        for (AppointmentSlot appointmentSlot : appointmentsFromClinic) {
            System.out.println(i++ + ". " + appointmentSlot);
        }

        choice = getIntInput("Select appointment: ") - 1;

        if (choice < 0 || choice >= appointmentsFromClinic.size()) {
            throw new IndexOutOfBoundsException("Invalid appointment selection.");
        }

        appointmentManager.bookAppointment(appointmentsFromClinic.get(choice).getDoc().getId(), patient, appointmentsFromClinic.get(choice).getTime());
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
    
    private static void cancelAppointment(Patient patient){
       List<AppointmentSlot> patientAppointments=patient.getPatientAppointments();
       while(true){try {
             if (patientAppointments.isEmpty()) {
            System.out.println("\n There is no appointmnet"); return;
        }
        int i=1;
        for (AppointmentSlot patientAppointment : patientAppointments) {
            System.out.println(i +". "+ patientAppointment);
            i++;
             }
        int choice = getIntInput("\nSelect appointment that you want to cancel \n for up menu zero(0)\n Selecteion: ")-1;
        
        if(choice==-1){ System.out.println("Returning up menu...");return;}
        
        if (choice<0||choice>patientAppointments.size()) {
                  throw new IndexOutOfBoundsException("Invalid number");
              }
         
        appointmentManager.cancelPatientAppointment(patient, patient.getPatientAppointments().get(choice));
        } catch (Exception e) {
            System.out.println("Operations failed!"+e.toString());
            return;
        }
       } 
    }
    
    private static void showPatientNotifications(Patient patient) {
        Stack<String> not = (Stack<String>) patient.notifications.clone();
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
                    patient.notifications.clear();
                case 2 -> {
                    return;
                }
                default ->
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void takeAppointmentMenu(Patient patient) {
        try {
            while (true) {
            System.out.println("\nTake Appointment Menu");
            System.out.println("\nAll doctors and their nearest appointments:");
            List<AppointmentSlot> nearestSlots = appointmentManager.getAllDoctorsNearestAppointmentDatesByTimeOrder();
            displayNearestAppointments(nearestSlots);
                
            System.out.println("\n1. Search for Doctor");
            System.out.println("2. Take Appointment by Doctor Name");
            System.out.println("3. See All Appointments for a Doctor");
            System.out.println("4. Take nearest Appointmet from Doctors");
            System.out.println("5. Take nearest Appointmet from Doctors by index");
            System.out.println("6. Back to Previous Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 ->
                    searchAndBookDoctor(patient);
                case 2 ->
                    takeAppointmentByDoctorName(patient);
                case 3 ->
                    viewAllDoctorAppointments(patient);
                case 4 ->
                    takeNearestAppointmentFromDoctor(nearestSlots.get(0).doc, patient, nearestSlots.get(0).getTime());
                case 5 ->
                    takeBetweenNearestAppointmentByIndex(patient, nearestSlots, getIntInput("Index:")-1);
                case 6 -> {
                    return;
                }
                default ->
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }

    private static void takeBetweenNearestAppointmentByIndex(Patient patient, List<AppointmentSlot> nearestSlots, int i) {

        if (i >= 0 && i <= nearestSlots.size()) {
            try {
                appointmentManager.bookAppointment(nearestSlots.get(i).getDoc().getId(), patient, nearestSlots.get(i).time);
                System.out.println("Succcesfuly appointment taken for the Dr."+nearestSlots.get(i).docName);
            } catch (Exception e) {
                System.out.println("Failed to book appointment: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid Number.");
        }

    }

    private static void takeNearestAppointmentFromDoctor(Doctor doc, Patient patient, LocalDateTime time) {
        try {
            appointmentManager.bookAppointment(doc.getId(), patient, time);
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("\nAppointment Taken Succesfully!");  
    }

    // Helper methods for scheduling appointments
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
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd");
            }
        }
    }

    // Helper methods for viewing and booking appointments
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

    private static void searchAndBookDoctor(Patient patient) {
        System.out.println("Not Working yet");
//        while (true) {
//            System.out.println("Enter doctor name to search (or 'back' to return):");
//            String searchTerm = scanner.nextLine();
//
//            if (searchTerm.equalsIgnoreCase("back")) {
//                return;
//            }
//
//            List<String> matchingDoctors = appointmentManager.findDoctor(searchTerm);
//            if (matchingDoctors.isEmpty()) {
//                System.out.println("No matching doctors found.");
//                continue;
//            }
//
//            System.out.println("\nMatching doctors:");
//            for (int i = 0; i < matchingDoctors.size(); i++) {
//                System.out.println((i + 1) + ". " + matchingDoctors.get(i));
//            }
//
//            System.out.println("\n1. Search again");
//            System.out.println("2. Select doctor");
//            System.out.println("3. Back to menu");
//
//            int choice = getIntInput("Enter your choice: ");
//
//            if (choice == 1) {
//                continue;
//            } else if (choice == 2) {
//                int doctorIndex = getIntInput("Enter doctor number: ") - 1;
//                if (doctorIndex >= 0 && doctorIndex < matchingDoctors.size()) {
//                    String doctorName = matchingDoctors.get(doctorIndex);
//                    Doctor selectedDoctor = findDoctorByName(doctorName);
//                    if (selectedDoctor != null) {
//                        showAndBookAppointment(patient, selectedDoctor);
//                    }
//                }
//            } else if (choice == 3) {
//                return;
//            }
//        }
    }

    private static void showAndBookAppointment(Patient patient, Doctor doctor) {
        AppointmentSlot nearest = appointmentManager.getNearestAvailableAppointmentSlot(doctor.getId());
        if (nearest == null) {
            System.out.println("No available appointments for this doctor.");
            return;
        }

        System.out.println("\nNearest available appointment: " + nearest);
        System.out.println("\n1. Book this appointment");
        System.out.println("2. See all available appointments");
        System.out.println("3. Back to menu");

        int choice = getIntInput("Enter your choice: ");

        switch (choice) {
            case 1 -> {
                try {
                    appointmentManager.bookAppointment(doctor.getId(), patient, nearest.getTime());
                    System.out.println("Appointment booked successfully!");
                } catch (IllegalArgumentException e) {
                    System.out.println("Failed to book appointment: " + e.getMessage());
                }
            }
            case 2 -> {
                List<AppointmentSlot> allSlots = appointmentManager.getAllAppointmentsForDoctor(doctor.getId());
                displayAvailableAppointments(allSlots);

                int slotIndex = getIntInput("Enter appointment number to book (0 to cancel): ") - 1;
                if (slotIndex >= 0 && slotIndex < allSlots.size()) {
                    AppointmentSlot selected = allSlots.get(slotIndex);
                    if (!selected.isBooked()) {
                        try {
                            appointmentManager.bookAppointment(doctor.getId(), patient, selected.getTime());
                            System.out.println("Appointment booked successfully!");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Failed to book appointment: " + e.getMessage());
                        }
                    } else {
                        System.out.println("This slot is already booked.");
                    }
                }
            }
        }
    }

    // Utility methods
    private static Doctor selectDoctor() {
        System.out.println("\nAvailable doctors:");
        for (int i = 0; i < doctors.size(); i++) {
            System.out.println((i + 1) + ". " + doctors.get(i));
        }
        System.out.println((doctors.size() + 1) + ". Back to previous menu");

        int choice = getIntInput("Select doctor: ");
        if (choice > 0 && choice <= doctors.size()) {
            return doctors.get(choice - 1);
        } else if (choice == doctors.size() + 1) {
            return null;
        } else {
            System.out.println("Invalid choice.");
            return null;
        }
    }

    private static Patient selectPatient() {
        System.out.println("\nAvailable patients:");
        for (int i = 0; i < patients.size(); i++) {
            System.out.println((i + 1) + ". " + patients.get(i));
        }
        System.out.println((patients.size() + 1) + ". Back to previous menu");

        int choice = getIntInput("Select patient: ");
        if (choice > 0 && choice <= patients.size()) {
            return patients.get(choice - 1);
        } else if (choice == patients.size() + 1) {
            return null;
        } else {
            System.out.println("Invalid choice.");
            return null;
        }
    }

    private static void takeAppointmentByDoctorName(Patient patient) {
        System.out.println("Enter doctor name:");
        String doctorName = scanner.nextLine();

        Doctor doctor = findDoctorByName(doctorName);
        if (doctor != null) {
            showAndBookAppointment(patient, doctor);
        } else {
            System.out.println("Doctor not found.");
        }
    }

    private static void viewAllDoctorAppointments(Patient patient) {
        Doctor doctor = selectDoctor();
        if (doctor != null) {
            List<AppointmentSlot> appointments = appointmentManager.getAllAppointmentsForDoctor(doctor.getId());
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
                if (slotIndex >= 0 && slotIndex < appointments.size()) {
                    AppointmentSlot selected = appointments.get(slotIndex);
                    if (!selected.isBooked()) {
                        try {
                            appointmentManager.bookAppointment(doctor.getId(), patient, selected.getTime());
                            System.out.println("Appointment booked successfully!");
                        } catch (IllegalArgumentException e) {
                            System.out.println("Failed to book appointment: " + e.getMessage());
                        }
                    } else {
                        System.out.println("This slot is already booked.");
                    }
                }
            }
        }
    }

    private static void showPatientHistory(Patient patient) {
        Stack<String> notifications = patientsHistory.getHistory(patient);
        if (notifications.isEmpty()) {
            System.out.println("No history available.");
            return;
        }

        System.out.println("\nNotification History:");
        for (String notification : notifications) {
            System.out.println("- " + notification);
        }
    }

    private static Doctor findDoctorByName(String name) {
        return doctors.stream()
                .filter(d -> d.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
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

    private static void displayNearestAppointments(List<AppointmentSlot> appointments) {
        if (appointments.isEmpty()) {
            throw new IllegalArgumentException("No available appointments found.");
        }
        int counter = 1;
        for (AppointmentSlot slot : appointments) {
            System.out.println(counter + " Dr. " + slot.getDocName()
                    + " - Next available: " + slot.getTime()
                    + " - " + slot.getEndTime());
            counter++;
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
            System.out.println("1. View Doctors");
            System.out.println("2. Add Doctor");
            System.out.println("3. Delete Doctor");
            System.out.println("4. View Clinic History");
            System.out.println("5. Add to Clinic History");
            System.out.println("6. View Nearest Available Appointments");
            System.out.println("0. Back to Main Menu");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> viewDoctors(clinic);
                case 2 -> addDoctor(clinic, scanner);
                case 3 -> deleteDoctor(clinic, scanner);
                case 4 -> viewClinicHistory(clinic);
                case 5 -> addToClinicHistory(clinic, scanner);
                case 6 -> viewNearestAppointments(clinic);
                case 0 -> {
                    return; // Go back to the main menu
                }
                default -> System.out.println("Invalid choice. Please try again.");
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
            System.out.println("Doctor added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteDoctor(Clinic clinic, Scanner scanner) {
        viewDoctors(clinic); // Display doctors for selection
        List<Doctor> doctors = clinic.getDoctors();
        if(doctors.isEmpty()){
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

    private static void viewClinicHistory(Clinic clinic) {
        List<String> history = clinic.getHistory();
        if (history.isEmpty()) {
            System.out.println("No history recorded for this clinic.");
        } else {
            System.out.println("Clinic History:");
            for (String event : history) {
                System.out.println("- " + event);
            }
        }
    }

    private static void addToClinicHistory(Clinic clinic, Scanner scanner) {
        System.out.print("Enter the history event to add: ");
        String event = scanner.nextLine();
        clinic.addHistoryToDoctor(event);
        System.out.println("History event added.");
    }

    private static void viewNearestAppointments(Clinic clinic) {
        if (clinic.appointmentManagerConnection == null) {
            System.out.println("Appointment Manager is not connected to this clinic.");
            return;
        }
        List<AppointmentSlot> appointments = clinic.getDoctorsNearestAppointment();
        if (appointments.isEmpty()) {
            System.out.println("No upcoming appointments found for this clinic's doctors.");
        } else {
            System.out.println("Nearest Available Appointments:");
            for (AppointmentSlot appointment : appointments) {
                System.out.println(appointment); // Assuming AppointmentSlot has a toString()
            }
        }
    }
}