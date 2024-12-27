/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;

import hospitalmanagementsystemwithtreemap.Doctor;
import hospitalmanagementsystemwithtreemap.TrieWithGenericType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ysr
 */
public class Search {
    TrieWithGenericType<ClinicEnums.Department> clinicSearcher;
    TrieWithGenericType<Integer> doctors; 
    public Search(List<Doctor> doctors){
        this.clinicSearcher=new TrieWithGenericType<>();
         for (ClinicEnums.Department clinic : Arrays.asList(ClinicEnums.Department.values())) {
            clinicSearcher.insert(clinic.toString(), clinic);
        }
        
        this.doctors=new TrieWithGenericType<>();
        for (Doctor doctor : doctors) {
            this.doctors.insert(doctor.getName(),doctor.getId());
        }
    }
    
    
    public  List<Map.Entry<String, Integer>> searchForDoctor(String word){
        return doctors.searchSimilarWithValues(word);
    }
    
    public List<Map.Entry<String, ClinicEnums.Department>> searchForClinic(String word){
        return clinicSearcher.searchSimilarWithValues(word);
    }
     
}
