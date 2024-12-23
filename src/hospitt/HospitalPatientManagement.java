package hospitt;


import java.time.*;
import java.util.*;

public class HospitalPatientManagement {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor("Dr. Smith"));
        doctors.add(new Doctor("Dr. Johnson"));
        doctors.add(new Doctor("Dr. Brown"));
        doctors.add(new Doctor("Dr. Garcia"));
        doctors.add(new Doctor("Dr. Miller"));
        doctors.add(new Doctor("Dr. Davis"));
        doctors.add(new Doctor("Dr. Wilson"));
        doctors.add(new Doctor("Dr. Moore"));
        doctors.add(new Doctor("Dr. Taylor"));
        doctors.add(new Doctor("Dr. Anderson"));

        // Automatically generate appointments for the next 7 weekdays
        createWeeklyAppointments(doctors);

        Map<String, Patient> patients = new HashMap<>();

        while (true) {
            System.out.println("\nSelect User Type:");
            System.out.println("1. Doctor");
            System.out.println("2. Patient");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int userType = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (userType == 1) {
                System.out.println("\nDoctor Menu:");
                System.out.println("1. Add/Delete Appointments or Days");
                System.out.println("2. View Full Schedule");
                System.out.println("3. Exit to Main Menu");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        manageAppointments(scanner, doctors);
                        break;
                    case 2:
                        System.out.println("Select a doctor to view the schedule:");
                        for (int i = 0; i < doctors.size(); i++) {
                            System.out.println((i + 1) + ". " + doctors.get(i).name);
                        }
                        int doctorChoice = scanner.nextInt();
                        scanner.nextLine();
                        if (doctorChoice > 0 && doctorChoice <= doctors.size()) {
                            doctors.get(doctorChoice - 1).viewSchedule();
                        } else {
                            System.out.println("Invalid doctor selection.");
                        }
                        break;
                    case 3:
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else if (userType == 2) {
                System.out.println("\nPatient Menu:");
                System.out.println("1. View Available Appointments");
                System.out.println("2. Book Appointment");
                System.out.println("3. Exit to Main Menu");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1: {
                        System.out.println("Select a doctor to view appointments:");
                        for (int i = 0; i < doctors.size(); i++) {
                            System.out.println((i + 1) + ". " + doctors.get(i).name);
                        }
                        int doctorChoice = scanner.nextInt();
                        scanner.nextLine();
                        if (doctorChoice > 0 && doctorChoice <= doctors.size()) {
                            Doctor selectedDoctor = doctors.get(doctorChoice - 1);
                            System.out.print("Enter date (yyyy-MM-dd): ");
                            LocalDate date = LocalDate.parse(scanner.nextLine());
                            List<Appointment> availableAppointments = selectedDoctor.getAvailableAppointments(date);
                            if (availableAppointments.isEmpty()) {
                                System.out.println("No available appointments on " + date);
                            } else {
                                System.out.println("Available appointments on " + date + " for Dr. " + selectedDoctor.name + ":");
                                for (Appointment appointment : availableAppointments) {
                                    System.out.println("  " + appointment.time);
                                }
                            }
                        } else {
                            System.out.println("Invalid doctor selection.");
                        }
                        break;
                    }
                    case 2: {
                        System.out.print("Enter patient name: ");
                        String patientName = scanner.nextLine();
                        patients.putIfAbsent(patientName, new Patient(patientName));
                        Patient patient = patients.get(patientName);

                        System.out.println("Select a doctor to book an appointment:");
                        for (int i = 0; i < doctors.size(); i++) {
                            System.out.println((i + 1) + ". " + doctors.get(i).name);
                        }
                        int doctorChoice = scanner.nextInt();
                        scanner.nextLine();

                        if (doctorChoice > 0 && doctorChoice <= doctors.size()) {
                            Doctor selectedDoctor = doctors.get(doctorChoice - 1);
                            System.out.print("Enter date (yyyy-MM-dd): ");
                            LocalDate date = LocalDate.parse(scanner.nextLine());
                            System.out.print("Enter time: ");
                            String time = scanner.nextLine();

                            List<Appointment> availableAppointments = selectedDoctor.getAvailableAppointments(date);
                            boolean booked = false;
                            for (Appointment appointment : availableAppointments) {
                                if (appointment.time.equals(time)) {
                                    appointment.bookAppointment(patient.name);
                                    System.out.println("Appointment booked for " + patient.name + " with Dr. " + selectedDoctor.name + " on " + date + " at " + time);
                                    booked = true;
                                    break;
                                }
                            }
                            if (!booked) {
                                System.out.println("Time slot " + time + " is not available on " + date + ".");
                            }
                        } else {
                            System.out.println("Invalid doctor selection.");
                        }
                        break;
                    }
                    case 3:
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else if (userType == 3) {
                System.out.println("Exiting system.");
                scanner.close();
                break;
            } else {
                System.out.println("Invalid user type. Please try again.");
            }
        }
    }

    private static void createWeeklyAppointments(List<Doctor> doctors) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        int daysScheduled = 0;

        while (daysScheduled < 7) {
            if (tomorrow.getDayOfWeek() != DayOfWeek.SATURDAY && tomorrow.getDayOfWeek() != DayOfWeek.SUNDAY) {
                for (Doctor doctor : doctors) {
                    List<String> timeSlots = generateTimeSlots();
                    doctor.createAppointmentDay(tomorrow, timeSlots);
                    System.out.println("Created appointments for " + doctor.name + " on " + tomorrow);
                }
                daysScheduled++;
            }
            tomorrow = tomorrow.plusDays(1);
        }
    }

    private static List<String> generateTimeSlots() {
        List<String> timeSlots = new ArrayList<>();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);

        while (startTime.isBefore(endTime)) {
            if (startTime.isBefore(LocalTime.of(12, 0)) || startTime.isAfter(LocalTime.of(13, 0))) {
                timeSlots.add(startTime.toString());
            }
            startTime = startTime.plusMinutes(30);
        }
        return timeSlots;
    }

    private static void manageAppointments(Scanner scanner, List<Doctor> doctors) {
        System.out.println("Select a doctor to manage appointments:");
        for (int i = 0; i < doctors.size(); i++) {
            System.out.println((i + 1) + ". " + doctors.get(i).name);
        }
        int doctorChoice = scanner.nextInt();
        scanner.nextLine();

        if (doctorChoice <= 0 || doctorChoice > doctors.size()) {
            System.out.println("Invalid doctor selection.");
            return;
        }

        Doctor selectedDoctor = doctors.get(doctorChoice - 1);
        System.out.println("Would you like to:");
        System.out.println("1. Add/Delete Appointment Days");
        System.out.println("2. Add/Delete Appointment Slots");
        System.out.print("Choose an option: ");

        int actionChoice = scanner.nextInt();
        scanner.nextLine();

        switch (actionChoice) {
            case 1:
                manageAppointmentDays(scanner, selectedDoctor);
                break;
            case 2:
                manageAppointmentSlots(scanner, selectedDoctor);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void manageAppointmentDays(Scanner scanner, Doctor doctor) {
        System.out.println("Existing appointment days for " + doctor.name + ":");
        Set<LocalDate> uniqueDates = doctor.getUniqueAppointmentDates();
        for (LocalDate date : uniqueDates) {
            System.out.println(" - " + date + " (" + date.getDayOfWeek() + ")");
        }

        System.out.println("Would you like to:");
        System.out.println("1. Add a new appointment day");
        System.out.println("2. Delete an existing appointment day");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Enter a date to add (yyyy-MM-dd): ");
                LocalDate dateToAdd = LocalDate.parse(scanner.nextLine());
                if (uniqueDates.contains(dateToAdd)) {
                    System.out.println("Date already exists.");
                } else {
                    doctor.createAppointmentDay(dateToAdd, generateTimeSlots());
                    System.out.println("Added appointments for " + dateToAdd + " (" + dateToAdd.getDayOfWeek() + ")");
                }
                break;
            case 2:
                System.out.print("Enter a date to delete (yyyy-MM-dd): ");
                LocalDate dateToDelete = LocalDate.parse(scanner.nextLine());
                if (!uniqueDates.contains(dateToDelete)) {
                    System.out.println("Date not found.");
                } else {
                    doctor.deleteAppointmentDay(dateToDelete);
                    System.out.println("Deleted appointments for " + dateToDelete + " (" + dateToDelete.getDayOfWeek() + ")");
                }
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void manageAppointmentSlots(Scanner scanner, Doctor doctor) {
        System.out.println("Existing appointment days for " + doctor.name + ":");
        Set<LocalDate> uniqueDates = doctor.getUniqueAppointmentDates();
        for (LocalDate date : uniqueDates) {
            System.out.println(" - " + date + " (" + date.getDayOfWeek() + ")");
        }

        System.out.print("Enter a date to manage (yyyy-MM-dd): ");
        LocalDate selectedDate = LocalDate.parse(scanner.nextLine());
        if (!uniqueDates.contains(selectedDate)) {
            System.out.println("Date not found.");
            return;
        }

        System.out.println("Available time slots for " + selectedDate + ":");
        List<Appointment> availableAppointments = doctor.getAvailableAppointments(selectedDate);
        for (Appointment appointment : availableAppointments) {
            System.out.println(" - " + appointment.time);
        }

        System.out.println("Would you like to:");
        System.out.println("1. Add a new time slot");
        System.out.println("2. Delete an existing time slot");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Enter a time slot to add (HH:mm): ");
                String timeToAdd = scanner.nextLine();
                if (doctor.addTimeSlot(selectedDate, timeToAdd)) {
                    System.out.println("Added time slot " + timeToAdd + " on " + selectedDate);
                } else {
                    System.out.println("Time slot already exists or is invalid.");
                }
                break;
            case 2:
                System.out.print("Enter a time slot to delete (HH:mm): ");
                String timeToDelete = scanner.nextLine();
                if (doctor.deleteTimeSlot(selectedDate, timeToDelete)) {
                    System.out.println("Deleted time slot " + timeToDelete + " on " + selectedDate);
                } else {
                    System.out.println("Time slot not found or is invalid.");
                }
                break;
            default:
                System.out.println("Invalid choice.");
        }

        System.out.println("Updated time slots for " + selectedDate + ":");
        availableAppointments = doctor.getAvailableAppointments(selectedDate);
        for (Appointment appointment : availableAppointments) {
            System.out.println(" - " + appointment.time);
        }
    }
}
