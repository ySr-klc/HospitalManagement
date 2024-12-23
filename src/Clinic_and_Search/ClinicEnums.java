/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package Clinic_and_Search;

/**
 *
 * @author ysr
 */
public class ClinicEnums {

    public enum Department {
        UROLOGY("Deals with conditions of the urinary tract and male reproductive system."),
        RADIOLOGY("Uses imaging techniques for diagnosis."),
        CARDIOLOGY("Deals with heart and blood vessel conditions."),
        DERMATOLOGY("Focuses on skin, hair, and nail disorders."),
        EMERGENCY("Provides immediate care for acute illnesses and injuries."),
        GASTROENTEROLOGY("Treats digestive system diseases."),
        NEUROLOGY("Specializes in disorders of the nervous system."),
        OBSTETRICS_GYNECOLOGY("Provides care for women's reproductive health."),
        OPHTHALMOLOGY("Focuses on eye care."),
        ORTHOPEDICS("Deals with musculoskeletal conditions."),
        PEDIATRICS("Provides medical care for children."),
        PSYCHIATRY("Specializes in mental health disorders.");

        private final String description;

        Department(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public static Department fromString(String departmentName) {
            for (Department department : Department.values()) {
                if (department.name().equalsIgnoreCase(departmentName)) {
                    return department;
                }
            }
            return null;
        }
    }
}