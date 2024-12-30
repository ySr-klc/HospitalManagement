package HospitalManagmentSystem;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
public class DoctorObserverHandler {
        List<DoctorObserver> observers;
        List<Doctor> doctors;
    
      public DoctorObserverHandler( List<Doctor> doctors) {
      doctors.sort(Comparator.comparing(Doctor::getSpeciality));
      this.doctors = doctors;
      observers=new LinkedList<>();
    }
      
    
    public void addObserver(DoctorObserver observer){
        observers.add(observer);
    }

    public void deleteObserver(DoctorObserver observer){
        observers.remove(observer);
    }


public void doctorAdd(Doctor doctor){
    doctors.add(doctor);
    doctors.sort(Comparator.comparing(Doctor::getSpeciality));
    listUpdater(doctors);
}    

public void doctorDelete(Doctor doctor){
    doctors.remove(doctor);
    deleteListUpdater(doctor);
}  

public  List<Doctor>  getDoctors(){
    return doctors;
}


//  ---------------
    
    
    private void listUpdater(List<Doctor> doctors){
        for (DoctorObserver observer : observers) {
            observer.update(doctors);
        }
    }
    
    private void deleteListUpdater(Doctor doctor){
        for (DoctorObserver observer : observers) {
            observer.delete(doctor);
        }
    }
}
