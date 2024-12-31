
package History;

import Persons.Patient;
import java.util.*;

/*
 * This class manages the medical history of patients.
 * It maintains a mapping of patient IDs to linked lists of their historical events (History objects).
 */
public class HistoryOfPatient {

    // A map to store patient IDs as keys and their corresponding linked lists of history events as values
    private static final Map<Integer, LinkedList<History>> histories = new HashMap<>();

    /**
     * Constructor that initializes the history map based on a provided list of patients.
     *
     * @param persons A list of Patient objects.
     * @throws IllegalArgumentException If the list of patients is null or contains null elements.
     */
    public HistoryOfPatient(List<Patient> persons) throws IllegalArgumentException {
        if (persons == null) {
            throw new IllegalArgumentException("List of persons cannot be null");
        }

        for (Patient person : persons) {
            if (person == null) {
                throw new IllegalArgumentException("Person object cannot be null");
            }
            histories.put(person.getId(), new LinkedList<>());
        }
    }

    /**
     * Updates the history map based on a new list of patients, ensuring each patient has a corresponding history list.
     *
     * @param persons A list of Patient objects.
     * @throws IllegalArgumentException If the list of patients is null or contains null elements.
     */
    public void updateList(List<Patient> persons) throws IllegalArgumentException {
        if (persons == null) {
            throw new IllegalArgumentException("List of persons cannot be null");
        }

        for (Patient person : persons) {
            if (person == null) {
                throw new IllegalArgumentException("Person object cannot be null");
            }
            histories.computeIfAbsent(person.getId(), k -> new LinkedList<>());
        }
    }

    /**
     * Deletes the medical history of a specific patient from the map.
     *
     * @param person The Patient object whose history needs to be removed.
     */
    public void deleteSpecificElementFromList(Patient person) {
        if (person != null) {
            histories.remove(person.getId());
        }
    }

    /**
     * Adds a new historical event (History object) to a patient's medical history, maintaining chronological order.
     *
     * @param person The Patient object to add the history to.
     * @param event The History object representing the new event.
     * @throws IllegalArgumentException If either the patient or the event object is null.
     */
    public static void addHistory(Patient person, History event) throws IllegalArgumentException {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }

        LinkedList<History> history = histories.get(person.getId());
        if (history == null) {
            history = new LinkedList<>();
            histories.put(person.getId(), history);
        }

        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        int index = 0;
        for (History h : history) {
            if (event.getTime().compareTo(h.getTime()) < 0) {
                break;
            }
            index++;
        }
        history.add(index, event);
    }

    /**
     * Retrieves the complete medical history of a patient as a linked list of History objects.
     *
     * @param person The Patient object whose history is requested.
     * @throws IllegalArgumentException If the patient object is null.
     * @return A linked list containing the patient's historical events, or null if the patient doesn't exist.
     */
    public static LinkedList<History> getHistory(Patient person) throws IllegalArgumentException {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        return histories.get(person.getId());
    }
}