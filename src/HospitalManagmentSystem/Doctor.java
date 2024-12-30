package HospitalManagmentSystem;

public class Doctor extends Person {
    private final Department speciality;
    public Doctor(String name, Department speciality) {
        super(name);
        this.speciality = speciality;
    }
    public Department getSpeciality() {
        return speciality;
    }
    @Override
    public String toString() {
        return "Doctor Name:" + super.name + " Speciality:" + speciality;
    }

}
