package hospitt;

abstract class Person {
    String name;

    Person(String name) {
        this.name = name;
    }

    abstract void displayInfo();
}

