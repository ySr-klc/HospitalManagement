package HospitalManagmentSystem;


import java.util.List;
import java.util.Map;

/**
 *
 * @author ysr
 */
public class Search implements DoctorObserver{

    TrieWithGenericType<Integer> doctors; 
    public Search(List<Doctor> doctors){

        
        this.doctors=new TrieWithGenericType<>();
        for (Doctor doctor : doctors) {
            this.doctors.insert(doctor.getName(),doctor.getId());
        }
    }
    
    
    public  List<Map.Entry<String, Integer>> searchForDoctor(String word){
        return doctors.searchSimilarWithValues(word);
    }

    @Override
    public void update(List<Doctor> doctors) {
        for (Doctor doctor : doctors) {
            if (!this.doctors.isExist(doctor.getName())) {
                this.doctors.insert(doctor.getName(),doctor.getId());
            }
        }
    }

    @Override
    public void delete(Doctor doctor) {
       doctors.delete(doctor.getName());
    }
     
}
