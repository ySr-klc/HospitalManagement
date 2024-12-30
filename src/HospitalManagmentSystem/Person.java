/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HospitalManagmentSystem;

/**
 *
 * @author ysr
 */
public class Person {
    protected String name; // Protected so subclasses can access it
    private final int id;
    private static int nextId = 1;

    public Person(String name) {
        this.name = name;
        this.id = nextId++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", ID: " + id;
    }
}
    
    

