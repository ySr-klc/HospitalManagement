package Search;

import Persons.Doctor;
import DoctorListUpdater.DoctorObserver;

import java.util.List;
import java.util.Map;

/**
 * Implements a search functionality for doctors based on their names.
 *
 * @author ysr
 */
public class Search implements DoctorObserver {

    // A Trie data structure for efficient doctor name search with integer values (IDs).
    private final TrieWithGenericType<Integer> doctors;

    /**
     * Creates a Search object with a list of doctors.
     * Builds the Trie data structure by inserting doctor names and corresponding IDs.
     *
     * @param doctors A list of Doctor objects to build the search index from.
     */
    public Search(List<Doctor> doctors) {
        this.doctors = new TrieWithGenericType<>();  // Initialize the Trie.
        for (Doctor doctor : doctors) {
            this.doctors.insert(doctor.getName(), doctor.getId()); // Insert doctor name and ID.
        }
    }

    /**
     * Searches for doctors with names similar to the provided word.
     * Utilizes the Trie data structure to find possible matches and returns a list of
     * Map entries containing doctor names and their associated IDs.
     *
     * @param word The keyword to search for in doctor names.
     * @return A list of Map entries (key: doctor name, value: doctor ID) for similar matches.
     */
    public List<Map.Entry<String, Integer>> searchForDoctor(String word) {
        return doctors.searchSimilarWithValues(word); // Use Trie's search functionality.
    }

    /**
     * Updates the search index when the doctor list changes (implements DoctorObserver interface).
     * Iterates through the new doctor list and inserts missing doctor names and IDs into the Trie.
     *
     * @param doctors The updated list of doctors.
     */
    @Override
    public void update(List<Doctor> doctors) {
        for (Doctor doctor : doctors) {
            if (!this.doctors.isExist(doctor.getName())) { // Check if doctor already exists
                this.doctors.insert(doctor.getName(), doctor.getId()); // Insert if missing
            }
        }
    }

    /**
     * Updates the search index when a doctor is removed (implements DoctorObserver interface).
     * Removes the doctor's name and ID from the Trie data structure.
     *
     * @param doctor The doctor object to be removed from the search index.
     */
    @Override
    public void delete(Doctor doctor) {
        doctors.delete(doctor.getName()); // Use Trie's deletion functionality.
    }
}