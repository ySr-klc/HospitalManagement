package DoctorListUpdater;

import Persons.Doctor;

import java.util.List;

/**
 * This interface defines a contract for classes that need to be notified when
 * the list of doctors changes. These classes can then react to updates (adding
 * new doctors) or removals of existing doctors.
 */
public interface DoctorObserver {

    void update(List<Doctor> doctors);

    void delete(Doctor doctor);

}
