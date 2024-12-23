/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Manages doctor appointments using a Trie-based data structure for efficient lookup.
 * Each doctor has their own priority queue of appointment slots ordered by time.
 */
public class AppointmentServiceUnusage {

    /** Main data structure: Trie storing PriorityQueues of appointments for each doctor */
    TrieWithGenericType<PriorityQueue<AppointmentSlot>> doctorsAppointments;
    
    //================================================================================
    // Constructor and Doctor List Management
    //================================================================================
    
    /**
     * Initializes the appointment manager with a list of doctors.
     * Creates an empty appointment queue for each doctor.
     * 
     * @param doctors List of doctors to initialize
     */
    public AppointmentServiceUnusage(List<Doctor> doctors) {
        doctorsAppointments = new TrieWithGenericType<>();
        for (Doctor doctor : doctors) {
            this.doctorsAppointments.insert(
                    doctor.name,
                    doctor.getAppointmentSlot()
            );
        }
        
    }

    /**
     * Updates the doctor list by adding new doctors if they don't exist.
     * Does not modify existing doctor appointments.
     * 
     * @param doctors List of doctors to update/add
     */
    public void updateDoctorList(List<Doctor> doctors) {
        for (Doctor doctor : doctors) {
            if (!doctorsAppointments.isExist(doctor.name)) {
                doctorsAppointments.insert(
                        doctor.name,
                        new PriorityQueue<>((o1, o2) -> o1.time.compareTo(o2.time))
                );
            }
        }
    }

    /**
     * Removes a doctor and all their appointments from the system.
     * 
     * @param doctor The doctor to remove
     */
    public void deleteDoctorFromList(Doctor doctor) {
        if (doctorsAppointments.isExist(doctor.name)) {
            doctorsAppointments.delete(doctor.name);
        }
    }

    /**
     * Clears all trie
     * when there is no pointer that keeps doctors list then it will removed by garbage collector
     */
    public void deleteAllDoctorList() {
        doctorsAppointments = new TrieWithGenericType<>();
    }
    
     //================================================================================
    // Appointment Retrieval
    //================================================================================
    

      /**
     * Gets the nearest available appointment slot for each doctor.
     * Returns a sorted list of all nearest available slots.
     * 
     * @return List of nearest available slots for all doctors, sorted by time
     */
    public List<AppointmentSlot> getAllDoctorsNearestAppointmentDatesByTimeOrder() {
        List<AppointmentSlot> allNearestSlots = new ArrayList<>();

        for (String doctorName : doctorsAppointments.getAll()) {
            PriorityQueue<AppointmentSlot> doctorSlots = doctorsAppointments.searchExact(doctorName);
            
            if (doctorSlots != null && !doctorSlots.isEmpty()) {
                // Find nearest available slot for this doctor without modifying the queue
                doctorSlots.stream()
                        .filter(slot -> !slot.isBooked() && 
                                      slot.getTime().isAfter(LocalDateTime.now()))
                        .min(Comparator.comparing(AppointmentSlot::getTime))
                        .ifPresent(allNearestSlots::add);
            }
        }

        // Sort all nearest slots by time
        allNearestSlots.sort(Comparator.comparing(AppointmentSlot::getTime));
        return allNearestSlots;
    }
   
    public List<String> findDoctor(String word){
        return doctorsAppointments.searchSimilar(word);
    }
    
    public void canceledByPatient(Patient pat,AppointmentSlot apt){
        doctorsAppointments.searchExact(apt.getDocName()).removeIf(slot->slot==apt);
        //maybe doctor notification add on here
    }
    
 

//    ================================================================================
//     Appointment Slot Management
//    ================================================================================
//    
    /**
     * Creates a new appointment slot for a doctor.
     * If the doctor doesn't exist, creates a new queue for them.
     * 
     * @param doctor The doctor for whom to create the appointment
     * @param appointment The appointment slot to add
     */
    public void createAppoinmentSlot(Doctor doctor, AppointmentSlot appointment) {
        PriorityQueue<AppointmentSlot> slots = doctorsAppointments.searchExact(doctor.name);
        if (slots == null) {
            slots = new PriorityQueue<>((o1, o2) -> o1.time.compareTo(o2.time));
            doctorsAppointments.insert(doctor.name, slots);
        }
        slots.add(appointment);
    }

    /**
     * Cancels an appointment at the specified time.
     * Creates a new queue without the cancelled appointment to avoid data loss.
     * 
     * @param doctor The doctor whose appointment to cancel
     * @param apmntHour The date/time of the appointment to cancel
     */
    public void cancelAppointment(Doctor doctor, LocalDateTime apmntHour) {
        PriorityQueue<AppointmentSlot> slots = doctorsAppointments.searchExact(doctor.getName());
        if (slots != null) {
            // Create a new queue and transfer elements, removing the cancelled appointment
            PriorityQueue<AppointmentSlot> newQueue = new PriorityQueue<>(
                    (o1, o2) -> o1.time.compareTo(o2.time));
            
            for (AppointmentSlot slot : slots) {
                if (slot.time.equals(apmntHour)) {
                    slot.getPatient().deleteAppointment(slot);
//                    slot.getPatient().addNotfication("Your appointment canceled by doctor:" + slot.getDocName()+" Date:"+slot.getTime()+" Clinic"+slot.doc.getSpeciality());
                    continue;
                } 
                newQueue.add(slot);
            }
            
            doctorsAppointments.insert(doctor.getName(), newQueue);
        }
    }

//    ================================================================================
//     Appointment Booking and Retrieval
//    ================================================================================
    
    /**
     * Books an appointment for a patient with a doctor at the specified time.
     * Preserves all appointments while updating the booked slot.
     * 
     * @param doctor The doctor to book with
     * @param patient The patient booking the appointment
     * @param time The requested appointment time
     * @throws IllegalArgumentException if doctor not found or no available slot
     */
    public void bookAppointment(Doctor doctor, Patient patient, LocalDateTime time) {
        PriorityQueue<AppointmentSlot> slots = doctorsAppointments.searchExact(doctor.getName());
        if (slots == null) {
            throw new IllegalArgumentException("Doctor not found: " + doctor.getName());
        }

        // Create a temporary queue to preserve all appointments
        PriorityQueue<AppointmentSlot> tempQueue = new PriorityQueue<>(
                (o1, o2) -> o1.time.compareTo(o2.time));
        
        AppointmentSlot targetSlot = null;
        
        // Find the matching slot while preserving all appointments
        for (AppointmentSlot slot : slots) {
            if (slot.getTime().equals(time) && !slot.isBooked()) {
                targetSlot = slot;
                slot.booked(patient, patient.getName());
                patient.addAppointment(slot);
            }
            tempQueue.add(slot);
        }

        if (targetSlot == null) {
            throw new IllegalArgumentException("No available slot at " + time);
        }

        // Replace the original queue with our updated one
        doctorsAppointments.insert(doctor.getName(), tempQueue);
    }

    /**
     * Finds the nearest available appointment slot for a doctor.
     * Only returns slots that are not booked and are in the future.
     * 
     * @param doctor The doctor to find slots for
     * @return The nearest available appointment slot, or null if none found
     */
    public AppointmentSlot getNearestAvailableAppointmentSlot(Doctor doctor) {
        PriorityQueue<AppointmentSlot> slots = doctorsAppointments.searchExact(doctor.getName());
        if (slots == null || slots.isEmpty()) {
            return null;
        }

        // Use stream to find nearest available slot without modifying the queue
        return slots.stream()
                .filter(slot -> !slot.isBooked() && slot.getTime().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(AppointmentSlot::getTime))
                .orElse(null);
    }

  
    /**
     * Retrieves all appointments for a specific doctor.
     * 
     * @param doctor The doctor whose appointments to retrieve
     * @return List of all appointments for the doctor
     */
    public List<AppointmentSlot> getAllAppointmentsForDoctor(Doctor doctor) {
        PriorityQueue<AppointmentSlot> slots = doctorsAppointments.searchExact(doctor.getName());
        if (slots == null) {
            return new ArrayList<>();
        }
        
        // Convert queue to list without modifying original queue
        return new ArrayList<>(slots);
    }
    
   
}    




//    public List<AppointmentSlot> getAllAppointmentsOfDoctor(String word){
//        if (null==doctorsAppointments.searchExact(word)) {
//            return new ArrayList<>();
//        }
//       return new ArrayList(doctorsAppointments.searchExact(word));
//    }
//    
//    
//        public AppointmentSlot getNearestAvailableAppointmentSlot(String word) {
//           PriorityQueue<AppointmentSlot> appointments=doctorsAppointments.searchExact(word);
//        if (appointments == null || appointments.isEmpty()) {
//            return null;
//        }
//
//        // Use stream to find nearest available slot without modifying the queue
//        return appointments.stream()
//                .filter(slot -> !slot.isBooked() && slot.getTime().isAfter(LocalDateTime.now()))
//                .min(Comparator.comparing(AppointmentSlot::getTime))
//                .orElse(null);
//    }
//    
//        
//     public void bookAppointment(String word,Patient patient, LocalDateTime time) {
//        PriorityQueue<AppointmentSlot> appointments=doctorsAppointments.searchExact(word);
//        if (appointments == null) {
//            throw new IllegalArgumentException("Doctor not found: " + word);
//        }
//
//        AppointmentSlot targetSlot = null;
//        // Find the matching slot while preserving all appointments
//        for (AppointmentSlot slot : appointments) {
//            if (slot.getTime().equals(time) && !slot.isBooked()) {
//                targetSlot = slot;
//                slot.booked(patient, patient.getName());
//                patient.addAppointment(slot);
//            }
//        }
//
//        if (targetSlot == null) {
//            throw new IllegalArgumentException("No available slot at " + time);
//        }
//
//    }
