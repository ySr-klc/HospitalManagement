package Persons;

import Clinics.Department;

/**
 * Represents a doctor with a name and their medical specialty (department).
 */
public class Doctor extends Person {

    // The doctor's medical specialty (e.g., Cardiology, Pediatrics).
    private final Department speciality;

    /**
     * Creates a new Doctor object with a name and their medical specialty.
     *
     * @param name        The doctor's name.
     * @param speciality  The doctor's medical specialty (department).
     */
    public Doctor(String name, Department speciality) {
        super(name); // Call the constructor of the parent class (Person)
        this.speciality = speciality;
    }

    /**
     * Gets the doctor's medical specialty.
     *
     * @return The department representing the doctor's specialty.
     */
    public Department getSpeciality() {
        return speciality;
    }

    /**
     * Overrides the default `toString` method to provide a more informative representation of the doctor.
     *
     * @return A string containing the doctor's name and specialty.
     */
    @Override
    public String toString() {
        return "Doctor Name: " + super.name + " Speciality: " + speciality;
    }
}