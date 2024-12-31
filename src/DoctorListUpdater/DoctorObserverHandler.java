package DoctorListUpdater;

import Persons.Doctor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Manages a list of doctors and notifies registered observers when the list changes.
 */
public class DoctorObserverHandler {

    // A list of observers interested in changes to the doctor list.
    private final List<DoctorObserver> observers;

    // A set to store doctors efficiently and avoid duplicates.
    private final Set<Doctor> doctors;

    /**
     * Creates a DoctorObserverHandler with an initial list of doctors.
     *
     * @param doctors A collection of doctors to start with.
     */
    public DoctorObserverHandler(Collection<Doctor> doctors) {
        this.doctors = new HashSet<>(doctors);
        this.observers = new LinkedList<>();
    }

    /**
     * Registers a new observer to be notified about doctor list changes.
     *
     * @param observer The observer to add.
     */
    public void addObserver(DoctorObserver observer) {
        observers.add(observer);
    }

    /**
     * Unregisters an observer from receiving doctor list change notifications.
     *
     * @param observer The observer to remove.
     */
    public void deleteObserver(DoctorObserver observer) {
        observers.remove(observer);
    }

    /**
     * Adds a new doctor to the list and notifies observers if the doctor was successfully added
     * (meaning it wasn't already present).
     *
     * @param doctor The doctor to add.
     */
    public void doctorAdd(Doctor doctor) {
        if (doctors.add(doctor)) { // Set.add() returns true if the doctor was added (not already present)
            // Notify observers only if the doctor was actually added.
            listUpdater(new ArrayList<>(doctors)); // Convert to List for observers
        }
    }

    /**
     * Removes a doctor from the list and notifies observers if the doctor was successfully removed.
     *
     * @param doctor The doctor to remove.
     */
    public void doctorDelete(Doctor doctor) {
        if (doctors.remove(doctor)) { // Set.remove() returns true if the doctor was removed
            deleteListUpdater(doctor);
        }
    }

    /**
     * Returns a copy of the current list of doctors (as a List for consistency).
     *
     * @return A copy of the list of doctors.
     */
    public List<Doctor> getDoctors() {
        // Return a List since observers expect a List format.
        return new ArrayList<>(doctors);
    }

    //-----------------------------------------------------------------

    /**
     * Notifies all registered observers about an update to the doctor list.
     *
     * @param doctors The new list of doctors.
     */
    private void listUpdater(List<Doctor> doctors) {
        for (DoctorObserver observer : observers) {
            observer.update(doctors);
        }
    }


    /**
     * Notifies all registered observers about the removal of a specific doctor.
     *
     * @param doctor The doctor that was removed.
     */
    private void deleteListUpdater(Doctor doctor) {
        synchronized (observers) {
            for (DoctorObserver observer : observers) {
                try {
                    observer.delete(doctor);
                } catch (Exception e) {
                    // Handle any errors but continue processing
                    System.err.println("Error notifying observer: " + e.getMessage());
                }
            }
        }
    }
}
