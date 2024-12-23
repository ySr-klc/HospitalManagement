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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Random;


public class HospitalManagementSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AppointmentManager appointmentManager;
    private static final List<Doctor> doctors;
    private static final List<Patient> patients;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    static {
        // Initialize sample data
        doctors = new ArrayList<>();
        doctors.add(new Doctor("John", "Cardiology"));
        doctors.add(new Doctor("Sarah", "Pediatrics"));
        doctors.add(new Doctor("Michael", "Neurology"));
        doctors.add(new Doctor("Markus", "Neurology"));
        doctors.add(new Doctor("Sedric", "Pediatrics"));
        patients = new ArrayList<>();
        patients.add(new Patient("Alice"));
        patients.add(new Patient("Bob"));

        appointmentManager = new AppointmentManager(doctors);
        Random rand=new Random();
        int[] days={3,5,7},duration={10,15,30};
        int randomStarter;
        for (Doctor doctor : doctors) {
            randomStarter=rand.nextInt(100)%3;
            appointmentManager.createDoctorAppointments(doctor, days[randomStarter], duration[randomStarter]);
            
        }

    }
    public static void main(String[] args) {
        mainMenu();
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\nHello welcome to Hospital Management System");
            System.out.println("1. Doctor");
            System.out.println("2. Patient");
            System.out.println("3. Exit");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 ->
                    
                    doctorMenu(selectDoctor());
                case 2 ->
                    patientMenu();
                case 3 -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default ->
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void doctorMenu(Doctor selectedDoctor) {
        while (true) {
            System.out.println("\nDoctor Menu");
            System.out.println("1. Schedule Appointment");
            System.out.println("2. View Current Appointments");
            System.out.println("3. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 ->
                    scheduleAppointmentMenu(selectedDoctor);
                case 2 ->
                    viewDoctorAppointments(selectedDoctor);
                case 3 -> {
                    return;
                }
                default ->
                    System.out.println("Invalid choice. Please try again.");
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
            List<AppointmentSlot> currentAppointments = appointmentManager.getAllAppointmentsForDoctor(selectedDoctor);
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
            System.out.println("3. Show History");
            System.out.println("4. Notifications");
            System.out.println("5. Back to Main Menu");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 ->
                    viewPatientAppointments(selectedPatient);
                case 2 ->
                    takeAppointmentMenu(selectedPatient);
                case 3 ->
                    showPatientHistory(selectedPatient);
                case 4 ->
                    showPatientNotifications(selectedPatient);
                case 5 -> {
                    return;
                }
                default ->
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private static void showPatientNotifications(Patient patient){
       Stack<String> not= (Stack<String>) patient.notifications.clone();
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
            case 1 -> patient.notifications.clear();
            case 2 ->{
                return;
            }
            default -> 
                System.out.println("Invalid choice. Please try again.");
        }
       }
    }
    
    private static void takeAppointmentMenu(Patient patient) {
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
                    takeBetweenNearestAppointmentByIndex(patient,nearestSlots, getIntInput("Index:"));
                case 6->{
                    return;
                }
                default ->
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void takeBetweenNearestAppointmentByIndex(Patient patient,List<AppointmentSlot> nearestSlots,int i){
                    
            if (i>=0&&i<=nearestSlots.size()) {
                  try{
                 appointmentManager.bookAppointment(nearestSlots.get(i).getDoc(), patient,nearestSlots.get(i).time);
        }catch(Exception e){
                  System.out.println("Failed to book appointment: "+e.getMessage());
        }
        }else System.out.println("Invalid Number.");
          
        
      
    }
    private static void takeNearestAppointmentFromDoctor(Doctor doc,Patient patient, LocalDateTime time){
        appointmentManager.bookAppointment(doc, patient, time);
        System.out.println("\nAppointment Taken Succesfully!");
    }
    
    // Helper methods for scheduling appointments
    private static void scheduleNewDays(Doctor doctor) {
        System.out.println("\nChoose number of days to schedule:");
        System.out.println("1. 3 days");
        System.out.println("2. 5 days");
        System.out.println("3. 7 days");

        int daysChoice = getIntInput("Enter your choice: ");
        int days = switch (daysChoice) {
            case 1 ->
                3;
            case 2 ->
                5;
            case 3 ->
                7;
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
            appointmentManager.cancelDoctorDay(doctor, dateTime);
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
            List<AppointmentSlot> appointments = appointmentManager.getAllAppointmentsForDoctor(doctor);
            for (AppointmentSlot slot : appointments) {
                if (!slot.getTime().isBefore(start) && !slot.getTime().isAfter(end)) {
                    appointmentManager.cancelAppointment(doctor, slot.getTime());
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
            List<AppointmentSlot> appointments = appointmentManager.getAllAppointmentsForDoctor(doctor);
            if (appointments.isEmpty()) {
                System.out.println("No appointments found.");
                return;
            }
            displayAppointmentsByDay(appointments);
        }
    }

    private static void viewPatientAppointments(Patient patient) {
        List<AppointmentSlot> appointments = patient.getPatientAppoinments();
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
        while (true) {
            System.out.println("Enter doctor name to search (or 'back' to return):");
            String searchTerm = scanner.nextLine();

            if (searchTerm.equalsIgnoreCase("back")) {
                return;
            }

            List<String> matchingDoctors = appointmentManager.findDoctor(searchTerm);
            if (matchingDoctors.isEmpty()) {
                System.out.println("No matching doctors found.");
                continue;
            }

            System.out.println("\nMatching doctors:");
            for (int i = 0; i < matchingDoctors.size(); i++) {
                System.out.println((i + 1) + ". " + matchingDoctors.get(i));
            }

            System.out.println("\n1. Search again");
            System.out.println("2. Select doctor");
            System.out.println("3. Back to menu");

            int choice = getIntInput("Enter your choice: ");

            if (choice == 1) {
                continue;
            } else if (choice == 2) {
                int doctorIndex = getIntInput("Enter doctor number: ") - 1;
                if (doctorIndex >= 0 && doctorIndex < matchingDoctors.size()) {
                    String doctorName = matchingDoctors.get(doctorIndex);
                    Doctor selectedDoctor = findDoctorByName(doctorName);
                    if (selectedDoctor != null) {
                        showAndBookAppointment(patient, selectedDoctor);
                    }
                }
            } else if (choice == 3) {
                return;
            }
        }
    }

    private static void showAndBookAppointment(Patient patient, Doctor doctor) {
        AppointmentSlot nearest = appointmentManager.getNearestAvailableAppointmentSlot(doctor);
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
                    appointmentManager.bookAppointment(doctor, patient, nearest.getTime());
                    System.out.println("Appointment booked successfully!");
                } catch (IllegalArgumentException e) {
                    System.out.println("Failed to book appointment: " + e.getMessage());
                }
            }
            case 2 -> {
                List<AppointmentSlot> allSlots = appointmentManager.getAllAppointmentsForDoctor(doctor);
                displayAvailableAppointments(allSlots);

                int slotIndex = getIntInput("Enter appointment number to book (0 to cancel): ") - 1;
                if (slotIndex >= 0 && slotIndex < allSlots.size()) {
                    AppointmentSlot selected = allSlots.get(slotIndex);
                    if (!selected.isBooked()) {
                        try {
                            appointmentManager.bookAppointment(doctor, patient, selected.getTime());
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
            List<AppointmentSlot> appointments = appointmentManager.getAllAppointmentsForDoctor(doctor);
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
                            appointmentManager.bookAppointment(doctor, patient, selected.getTime());
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
        Stack<String> notifications = patient.getNotifications();
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
        LocalDate day=LocalDate.MIN;
        for (AppointmentSlot slot : appointments) {
            if (!slot.isBooked()) {
                if (!day.isEqual(slot.getTime().toLocalDate())) {
                    day=slot.getTime().toLocalDate();
                    System.out.println(day.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy", Locale.ENGLISH)));
                }
                System.out.println(counter + ". " + 
                        slot.getTime().format(DateTimeFormatter.ofPattern("HH.mm")) 
                        + " - " 
                        + slot.getEndTime().format(DateTimeFormatter.ofPattern("HH.mm")));
                counter++;
            }
        }
    }

    private static void displayNearestAppointments(List<AppointmentSlot> appointments) {
        if (appointments.isEmpty()) {
            System.out.println("No available appointments found.");
            return;
        }
        int counter=1;
        for (AppointmentSlot slot : appointments) {
            System.out.println(counter+" Dr. " + slot.getDocName()
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
}
