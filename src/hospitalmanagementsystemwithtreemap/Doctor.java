/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitalmanagementsystemwithtreemap;
/**
 *
 * @author ysr
 */
public class Doctor extends Person {

    private ClinicEnums.Department speciality;
   
    
    public Doctor(String name, ClinicEnums.Department speciality) {
        super(name);
        this.speciality = speciality;
    }

    public ClinicEnums.Department getSpeciality() {
        return speciality;
    }

    public void setSpeciality(ClinicEnums.Department speciality) {
        this.speciality = speciality;
    }

    @Override
    public String toString() {
        String newString
                = "Doctor Name:" + super.name + " Speciality:" + speciality;
        return newString;
    }

}
