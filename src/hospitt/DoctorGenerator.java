/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hospitt;




import hospitalmanagementsystemwithtreemap.ClinicEnums;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
/**
 *
 * @author ysr
 */
public class DoctorGenerator {

    public static void generateDoctorsFile(String fileName, int numDoctors) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            Random random = new Random();
            String[] firstNames = {"John", "Jane", "David", "Sarah", "Michael", "Emily", "William", "Jessica", "James", "Ashley","Robert","Linda","Christopher","Elizabeth","Daniel","Jennifer","Matthew","Maria","Joseph","Susan","Andrew","Margaret","Steven","Dorothy","Anthony","Lisa","Joshua","Nancy","Kevin","Karen","Brian","Betty","Donald","Helen","George","Sandra","Edward","Donna","Ronald","Carol","Timothy","Ruth","Jason","Sharon","Jeffrey","Michelle","Ryan","Laura","Jacob","Sarah","Gary","Kimberly","Nicholas","Jessica","Eric","Angela","Jonathan","Melissa","Stephen","Deborah","Larry","Stephanie","Justin","Rebecca","Scott","Cynthia","Brandon","Kathleen","Benjamin","Amy","Samuel","Shirley","Gregory","Anna","Raymond","Brenda","Frank","Pamela","Patrick","Nicole","Jose","Emma","Dennis","Kelly","Jerry","Heather","Tyler","Abigail"}; // Expanded list
            String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez","Hernandez","Lopez","Gonzalez","Wilson","Anderson","Thomas","Taylor","Moore","Jackson","Martin","Lee","Perez","Thompson","White","Harris","Sanchez","Clark","Lewis","Robinson","Walker","Perez","Hall","Young","Allen","King","Wright","Scott","Green","Baker","Adams","Nelson","Carter","Mitchell","Perez","Roberts","Turner","Phillips","Campbell","Parker","Evans","Edwards","Collins","Stewart","Sanchez","Morris","Rogers","Reed","Cook","Morgan","Bell","Murphy","Bailey","Rivera","Cooper","Richardson","Cox","Howard","Ward","Torres","Peterson","Gray","Ramirez","James","Watson","Brooks","Kelly","Sanders","Price","Bennett","Wood","Barnes","Ross","Henderson","Coleman","Jenkins","Perry","Powell","Long","Patterson","Hughes","Flores","Washington","Butler"}; // Expanded list


            for (int i = 0; i < numDoctors; i++) {
                String firstName = firstNames[random.nextInt(firstNames.length)];
                String lastName = lastNames[random.nextInt(lastNames.length)];

                String name = firstName + " " + lastName;

                ClinicEnums.Department[] departments = ClinicEnums.Department.values();
                ClinicEnums.Department randomDepartment = departments[random.nextInt(departments.length)];

                writer.write(name + "," + randomDepartment.toString() + "\n");
            }
            System.out.println("Generated " + numDoctors + " doctors in " + fileName);

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        generateDoctorsFile("doctors.txt", 100); // Generate 100 doctors
    }
}