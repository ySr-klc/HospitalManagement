/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitt;

/**
 *
 * @author ysr
 */
public class searcn {
    //    private static void searchForClinic(Patient patient) {
//        try {
//             List<ClinicEnums.Department> clinics = Arrays.asList(ClinicEnums.Department.values());
//            System.out.println("\nWelcome to the clinic page!");
//            for (int i = 0; i < clinics.size(); i++) {
//                System.out.println((i + 1) + ". " + clinics.get(i).name().toUpperCase(Locale.ENGLISH));
//            }
//            System.out.println("Clinic that you are looking for: ");
//            String word = scanner.nextLine();
//            List<Map.Entry<String, ClinicEnums.Department>> searchResult = searchPanel.searchForClinic(word);
//
//            if (searchResult.isEmpty()) {
//                System.out.println("No clinics found matching that name.");
//                return;
//            }
//
//            int i = 1;
//            for (Map.Entry<String, ClinicEnums.Department> entry : searchResult) {
//                System.out.println(i++ + ". " + entry.getKey());
//            }
//
//            int choice = getIntInput("Selected clinic: ") - 1;
//
//            if (choice < 0 || choice >= searchResult.size()) {
//                throw new IndexOutOfBoundsException("Invalid clinic selection.");
//            }
//
//            List<AppointmentSlot> appointmentsFromClinic = clinicManager.getAppointmentsFromClinic(searchResult.get(choice).getValue());
//
//            if (appointmentsFromClinic == null || appointmentsFromClinic.isEmpty()) {
//                System.out.println("No appointments found in this clinic.");
//                return;
//            }
//
//            i = 1;
//            for (AppointmentSlot appointmentSlot : appointmentsFromClinic) {
//                System.out.println(i++ + ". " + appointmentSlot);
//            }
//
//            choice = getIntInput("Select appointment: ") - 1;
//
//            if (choice < 0 || choice >= appointmentsFromClinic.size()) {
//                throw new IndexOutOfBoundsException("Invalid appointment selection.");
//            }
//
//            appointmentManager.bookAppointment(appointmentsFromClinic.get(choice).getDoc().getId(), patient, appointmentsFromClinic.get(choice).getTime());
//            System.out.println("Appointment booked successfully!");
//
//        } catch (InputMismatchException e) {
//            System.out.println("Invalid input. Please enter a number.");
//            scanner.next(); // Clear the invalid input
//        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
//            System.out.println(e.getMessage());
//        } catch (IllegalArgumentException e) {
//            System.out.println("Error booking appointment: " + e.getMessage());
//        }
//    }
}
