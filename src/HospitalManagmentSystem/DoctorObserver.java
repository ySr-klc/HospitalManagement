/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package HospitalManagmentSystem;

import java.util.List;

/**
 *
 * @author ysr
 */
public interface DoctorObserver {
    
     void update(List<Doctor> doctors);
     void delete(Doctor doctor);
    
}
