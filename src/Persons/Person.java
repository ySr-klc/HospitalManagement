package Persons;

/**
 * Represents a person with a name and a unique identifier (ID).
 */
public class Person {

    // The person's name.
    protected String name; // Protected for access by subclasses (e.g., Doctor, Patient)

    // The person's unique identifier (ID).
    private final int id;

    // Tracks the next available ID for new Person objects.
    private static int nextId = 1;

    /**
     * Creates a new Person object with a given name. Assigns a unique ID 
     * based on the current `nextId` value and increments it for the next object.
     *
     * @param name The person's name.
     */
    public Person(String name) {
        this.name = name;
        this.id = nextId++;
    }

    /**
     * Gets the person's name.
     *
     * @return The person's name as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the person's name.
     *
     * @param name The new name for the person.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the person's unique identifier (ID).
     *
     * @return The person's ID as an integer.
     */
    public int getId() {
        return id;
    }

    /**
     * Overrides the default `toString` method to provide a more informative 
     * representation of the person object.
     *
     * @return A String containing the person's name and ID.
     */
    @Override
    public String toString() {
        return "Name: " + name + ", ID: " + id;
    }
}