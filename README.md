# INTRODUCTION

## Objective**:**

In this module project, it is aimed to create a hospital patient management system where doctors schedule appointments, modify their created appointments and cancel the appointments if needed. Also, doctors can add diagnoses to their patients’ medical history when appointment is done. Patients can book appointments, cancel appointments, search for available appointments through clinics or doctors, view their current appointments, view their medical history and get notifications like cancellation of appointments by doctor. In addition, there is an administrator system named clinic management where admins can add doctors, delete doctors, and view details about doctors.

## Background

In background, this project has a lot of classes and methods. It might be a little bit complex, but all the classes and methods have a responsibility in the system. Mostly the problematic part was handling the exceptions and errors that occurred due to using an advanced system. The most noticeable handled things of system are that:

1. System uses localDateTime library to a realistic view.
2. It only counts workdays and doctors can create appointments for only future days.
3. The scheduled days can be max 15 days, the system doesn’t allow for creating more appointments.
4. Created an additional search feature which allows partial search using trie data structure.
5. Patients can’t take any new appointments from the same clinic doctors on the same day.
6. Patients can’t take appointments that have a time within a difference of 30 minutes from any clinic.

# Classes

In this part, classes and their options and contributions of the project will be illustrated.

## Scheduling Appointment Classes

These classes handle appointment creation, deletion, retrieval, and the structure of appointment slots.

### Appointment Service

This core class manages appointments and can be considered a virtual database. It handles doctor management (adding, deleting, updating, etc.) and the creation and cancellation of individual appointment slots. This class operates on single appointment objects, not collections. Retrieval operations include finding the nearest available slot for a doctor, listing a doctor's available slots (for patients), and retrieving all a doctor's appointments (for doctors). Using the nearest available appointment slot function helps us to decrease load of system since appointments stores in tree map with chronological order.

### Appointment Manager

This subclass of the Appointment Service is responsible for implementing the business logic governing appointments. It manages appointment outcomes and provides doctors with the ability to create, delete, and reschedule appointments. This class also handles patient-side operations such as booking and canceling appointments. Several key business rules are enforced here: doctors cannot schedule appointments more than 15 days in advance; patients are prevented from having conflicting appointments; and patients are limited to one appointment per day.

### Appointment Slot

Within the Appointment Slot class, provide core management operations over time and it’s includes a detailed panel is generated for doctors, and the booking status of the appointment is tracked. This class also manages appointment attendance, recording whether the patient arrived for their appointment. Additionally, it provides a mechanism for doctors to add diagnostic information related to the appointment.

## Classes are Related with Clinic

In this part we handle clinics number, clinic, and using and clinic manager we provide centralized management for clinic classes.

### Clinic

Within the Clinic class, doctors are managed in relation to their respective departments. Patients can submit requests to the Clinic class to view a chronologically ordered list of a doctor's appointments at that clinic. The class also includes a Clinic Management panel for adding and removing doctors. The Clinic class maintains a composition relationship with the Appointment Manager class, enabling it to retrieve doctor appointments based on time.

### Clinic Manager

This class serves as the central management point for all clinics. It provides functionality to send updated lists of doctors(respect to operation like deletion, addition etc.). Upon system initialization, the initial doctor lists for all clinics are distributed from this class and appointment manager connection sends from this class. Additionally, it can retrieve specific Clinic class objects using an Enum key.

### Clinic Enum

Enums are used to represent clinics due to the fixed and relatively small number of clinics. This approach is more appropriate than other options in this scenario.

## Observer Design Pattern Based Classes for Doctors List

The doctor list is utilized in several different areas within our project. Consequently, any additions or deletions of doctors necessitate updates in all affected locations. To streamline this update process and maintain consistency, we employed the observer pattern, which centralizes the management of the doctor list.

### Doctor Observer Handler

The Doctor Observer Handler is responsible for managing updates following the addition or removal of a doctor. This is achieved using the Doctor Observer interface: any class that needs to be informed of these changes implements this interface and registers itself with the handler. When a doctor is added or removed, the handler iterates through the registered observers, notifying each one. Each observer class then handles the notification in a way that is relevant to its specific function.

### Doctor Observer

This interface provides equal operations for implemented class which makes handle process easier. In other words, program be sure that implemented classes includes interface functions and directly can calls these functions without knowing specific implementations.

## History Classes

Patient history is managed by a separate History class, independent of the Patient class itself. This design decision adheres to the single responsibility principle for the Patient class, allowing it to focus only on patient-related data and operations. Separating history management also facilitates future expansion of history-related functionality and improves data security.

### History

History class includes the necessary information for a history record, such as the date and time of the appointment, the diagnosis, and the names of the clinic and doctor, enabling patients to review their medical history effectively.

### History of Patient

This class is responsible for managing patient histories, organizing them by patient ID. It provides functionality for both patients and doctors to view these histories. Additionally, this class is the designated location for adding new history records.

## Person Classes

Person Classes responsible person related classes and their abilities.

### Person

This class acts as a parent class, providing the functionality to generate unique IDs for each person and store their name. Subclasses inherit from this class and implement any additional features specific to their needs.

### Patient

The Patient class stores essential information about each patient and is also responsible for managing their notifications and list of booked appointments.

### Doctor

Doctor class as additionally includes specialty.

## Search Classes

The Search classes provide the program's search functionality by implementing the required search algorithms.

### Search

This class currently provides search capabilities for doctor entities. However, the underlying implementation, utilizing a generic Trie data structure, facilitates future extensibility to other entity types. In a prior iteration of the project, this class also provided search functionality for clinic entities; however, this functionality was subsequently deprecated due to the limited cardinality of the clinic entity set.

### Trie With Generic Type

A Trie, also known as a prefix tree, is a specialized tree-like data structure designed for efficient string storage and retrieval based on prefixes. It embodies the core logic of prefix-based searching with potential for added functionalities. In a Trie, each character of a string is stored within a Trie node, typically implemented as a map (other approach is using array). This map uses characters as keys and references to other Trie nodes as values, effectively creating a hierarchical structure. This process continues until the final character of the string is reached. At this terminal node, a flag (often called isEndOfWord) is set to true, signifying the completion of a valid word. This describes the insertion process.

Searching within a Trie follows a similar traversal approach. Starting from the root, the search algorithm checks if the current node has a child corresponding to the first character of the search prefix. If a matching child is found, the algorithm proceeds to that child node and repeats the process for the next character in the prefix. If, at any point, a character in the prefix does not have a corresponding child in the current node, the search terminates, indicating that the prefix (and therefore any word starting with that prefix) is not present in the Trie. Conversely, if the algorithm successfully traverses all characters of the prefix, the subtree rooted at the final node of the prefix represents all words stored in the Trie that begin with that prefix. This subtree can then be traversed (using Depth-First Search or Breadth-First Search) to retrieve all such words.

## Hospital Management System GUI Classes

The purpose of these classes is to provide a user-friendly interface to the underlying system. While designed to be open to future development and enhancements, the current focus is on providing a basic GUI to streamline code analysis and testing for users.

### Main GUI

The function of this class is to initialize the system by loading doctor data and patient history data. Subsequently, the user can select one of the following interfaces: the Clinic Management GUI, the Patient GUI, or the Doctor GUI.

### Clinic GUI

Within the Clinic GUI, users can perform the following actions related to doctor management: adding new doctors, deleting doctor entries, and viewing doctor details.

### Patient GUI

The patient interface provides a range of functionalities, including the ability to view medical history, book appointments, and review notifications. Any scheduled appointments are displayed centrally on the patient's main page. The appointment booking process allows patients to select a clinic or specify a doctor by name. When booking by clinic, available appointment times are displayed. Patients can cancel existing appointments directly from the main page. The medical history view presents entries in reverse chronological order, from the most recent to the oldest.

### Doctor GUI

The patient interface provides a range of functionalities, including the ability to view medical history, book appointments, and review notifications. Any scheduled appointments are displayed centrally on the patient's main page. The appointment booking process allows patients to select a clinic or specify a doctor by name. When booking by clinic, available appointment times are displayed. Patients can cancel existing appointments directly from the main page. The medical history view presents entries in reverse chronological order, from the most recent to the oldest.

## System Initializer

That class Takes necessary information (like doctor objects information and initialize patient histories then respect to their names) to initialize system objects. It Isn’t main part of project. For further usage a data-base can be preferred.

# Data Structures

Using data structures is an important part of our project. We always strive to find the optimal option in terms of time and space complexity. However, there is always a trade-off between different data structures. Therefore, we select the most appropriate one, and here are the reasons for each selection.

## Using Hashmap and TreeMap in Appointment Service Class

In our appointment service, we manage doctor's appointments. To find the most appropriate data structure, we explored various approaches, including using a priority queue for appointments. However, we ultimately found that the HashMap/TreeMap combination is the most suitable for our design.

The outer HashMap maps a doctor's ID (Integer) to their appointment schedule. Inside each doctor's schedule, there's a TreeMap that organizes appointments by time. This creates a two-level hierarchical structure where you can first locate a doctor's schedule quickly, then find specific appointments within that schedule.

Using a doctor's ID instead of a direct class reference helps reduce space complexity

### Pros

1. HashMap for Doctor Lookup:
    - Quick O(1) access to any doctor's schedule using their ID
    - Efficient memory usage since each doctor only needs one schedule
    - Easy to add or remove doctors from the system
    - Perfect for situations where you need direct access to a specific doctor's appointments
2. TreeMap for Appointments:
    - Automatically keeps appointments sorted by time (natural ordering)
    - Efficient O(log n) operations for inserting and finding appointments(In order to prevent confliction we use frequently)
    - Makes it easy to find the next available slot
    - Supports range queries, so you can easily find all appointments within a time period
    - Maintains appointments in chronological order, which is crucial for scheduling

### Cons

1. Memory Usage:
    - TreeMaps have higher memory overhead compared to arrays or simple lists
    - Each node in the TreeMap requires additional pointers for maintaining the tree structure
    - The nested structure (HashMap containing TreeMaps) uses more memory than a flat structure
2. Performance Considerations:
    - While HashMap lookups are O(1), TreeMap operations are O(log n)
    - For very large numbers of appointments, operations might be slower than a simpler array-based solution
    - Modifying the middle of the TreeMap (like canceling appointments) requires rebalancing

### Conclusion

A HashMap is ideal for doctor lookups, providing O(1) average-case time complexity for access, insertion, and deletion. A TreeMap is suitable for appointment slots because appointment changes are expected to be infrequent, and patients typically prefer the earliest available slot, resulting in near-O(1) performance due to locality of reference. While TreeMap operations are O(log n), this is generally acceptable. However, frequent appointment deletions by doctors would make a TreeMap less suitable due to rebalancing overhead, but hopefully this scenario isn’t realistic. Additionally, TreeMaps have a higher memory footprint than simpler data structures due to the need to maintain the tree structure.

## Using Set for Doctors List

To store doctors in a list, that been considered many options like LinkedList, ArrayList etc. But eventually set chosen.

The Set data structure is preferred due to its efficient element access, synchronized works principle (for further implementation create thread safety), and the guarantee of element uniqueness.

### Pros

1. When you add an element to a Set, it automatically checks if that element already exists. This is extremely valuable in many real-world scenarios.
2. operations like add(), remove(), and contains() have O(1) time complexity since it is unbalanced based set
3. Sets memory efficient compared to other data structures that might store duplicates.
4. No Additional check for prevent duplication of doctors.

### Cons

1. A performance bottleneck exists in the doctor list display: using a List instead of a Set requires a conversion for every display, incurring O(n) time complexity.

### Conclusion

Choosing to use a Set due to its inherent properties. Although a more basic implementation could have been used, a Set provides increased data safety.

## Using HashMap for Patients Histories

A special service, independent of the patient management system, was created to store patient history. This design improves data safety and allows for future enhancements, such as enabling doctors to search patient histories. The service uses HashMap to efficiently store and retrieve patient data, using the patient's ID as a unique key. Each key maps to a LinkedList, which maintains a chronological record of the patient's history. When history request send we respond with the whole list and that makes our respond time O(1). Also, since users want to see their history in a chronological order there is no performance problem with O(n) response time.

### Pros

1. HashMap lookup is O(1) on average
2. Low space complexity for History objects O(1)

### Cons

1. May occur O(n) time complexity for adding LinkList if position couldn’t find
2. Uses more memory than simpler data structures for storing patient information

### Conclusion

Although this method may have some limitations, it is deemed acceptable for managing patient information. A key advantage is that the chronological ordering of patient histories simplifies the addition of new records, regardless of whether their exact position within the sequence is immediately known as far as list keep chronological order.

## Using EnumMap for Clinics in Clinic Manager

When working with enum keys in Java, EnumMap offers significant advantages over HashMap. Its optimized internal structure provides faster access and more efficient memory utilization, making it the preferred choice for enum-based key-value mappings. That’s why EnumMap preferred.

## Using LinkedList and Stack for Patient

The implementation utilizes a LinkedList for the storage of patient appointment data, owing to the dynamic nature of appointment scheduling and the associated insertion and deletion (passed appointment handling) operations. Given the expected low cardinality of appointments per patient, the LinkedList structure is deemed appropriate. The other approach could be use ArrayList but since Array size always increase as it wants more handling code which is doesn’t necessary for LinkedList approach.

A Stack data structure is employed for the management of notifications, as long-term data retention is not a requirement, and the desired retrieval order is last-in, first-out (LIFO).

## Using TrieWithGenericType(Build-in Structure) in Search Class

For our search functionality, we opted for a generic Trie implementation. By storing only doctor IDs within the Trie, we minimize memory overhead. A Trie (or prefix tree) organizes data by representing each character as a node in a tree-like structure. This design is particularly advantageous when dealing with large datasets. In contrast, using LinkedList or HashMap for searching would necessitate storing each name entirely and would only return exact matches. The Trie's ability to share common prefixes drastically reduces memory usage, a benefit that becomes increasingly significant as the dataset grows. While the memory savings are less pronounced with small datasets, the Trie's efficiency becomes apparent with large datasets.

### Complexity

1. Insertion: O(m) where m is word length
2. Search: O(m) for exact search
3. Prefix Search: O(m + k) where k is number of matches
4. Deletion: O(m) in the average case

## Conclusion

Implementing this search approach provided a valuable learning experience. Despite the potential for memory overhead, it represented the highest technique explored and implemented in the project.

## Using List for Storing Observers

Given the low cardinality and relative stability of the observer set, a straightforward management approach is deemed sufficient. While both ArrayList and LinkedList data structures were evaluated, the LinkedList implementation was selected. The dynamic resizing strategy employed by ArrayLists in Java, which involves doubling the allocated memory upon reaching capacity (2^n), presents a potential for memory inefficiency.

## Libraries Used in This Project

Swing:

A part of Java's standard library, Swing is used to create graphical user interfaces

Provides components like buttons, text fields, tables, and more to build desktop applications.

AWT:

A Java library for creating GUI components and managing user interface events.

Provides basic building blocks for GUIs, such as windows, buttons, and layouts.

LocalDateTime:

A class in the java.time package for handling date and time without a timezone.

Represents a combination of date and time in the ISO-8601 format (yyyy-MM-ddTHH:mm:ss).

It uses machine local time as in the format given above.

DateTimeFormmatter:

A class in the java.time.format package for formatting and parsing date-time objects.

Converts LocalDateTime (or similar classes) to strings and vice versa in customizable formats.

Comparator:

An interface in java.util package for defining custom sorting logic for objects.

Allows comparison of two objects to determine their ordering.

Stream:

A class in the java.util.stream package for processing sequences of elements.

Provides functional programming capabilities for filtering, mapping, reducing, and collecting data.

Optional:

A class in java.util package that represents a value that may or may not be present

Helps avoid NullPointerException by encouraging explicit handling of absence of values.

Entry:

An inner interface of the Map interface in java.util package that represents a key-value pair.

Used to iterate over or work with key-value mappings in a Map.

# Object Oriented Programming

The subjects of OOP covered in this project are:

- - 1. Inheritance
        2.  Overriding methods
        3.  Constructors
        4.  Encapsulation
        5.  Static blocks
        6.  Usage of static and final keywords on global variables
        7.  Static methods
        8.  Implementation and Interface Usage
        9.  Exception Handling

## Inheritance and Overriding

Inheritance is used in Person class which is parent class of Patient and Doctor classes.

To use a method which is at a different class, Inheritance is needed which will allow the inheritors to access the parent class’s methods. Thus, to prevent creating the same methods at different classes, which is making a lot of code for the same job, Inheritance helps us.

Another point that is worth using inheritance is overriding. Overriding uses the method of parent class and allows you to change the definition. For instance, in example code above toString method returns name and Id of person meanwhile we can use it in doctor class and change return statement as doctor name and specialty of doctor by overriding it.

## Constructors

Constructors are the first thing a class executes when an instance of class is trying to be created. There is always a non-parametrized constructor even though it is not visible system always has it. When a need for a parametrized constructor occurs, the user should create it manually. Parametrized constructors are usually used for fast creating and assigning the variables at the class. Also, there are some scenarios that you can’t access a class variable but can access constructor of it using inheritance and super method where parametrized constructors preferred. It’s called constructor chaining. In this project it is used for the doctor and patient constructors to send name. So that assigning the name will always be inside person constructor.

## Encapsulation

Encapsulation is one of the important things in OOP which depends on access modifiers.

While public access modifiers make data accessible from anywhere in project, protected access modifiers make data accessible only from same package and its child classes. Similarly, the default access modifier only lets the same package access the data, lastly private access modifier, which is quite different from others, only lets inside of class access. The data outside from class accessing is denied.

In this project all of the private informations and methods of classes are set as it should be. For instance, the name variable of person set to be protected so that child classes can access it regardless of package difference. Also, the Id variable of person set to be private since it is not okay to access it from anywhere but person.

## Static Block, Static Keyword and Final Keyword

The important property of static blocks is that static blocks will be executed even before the main function. In this project it is used for generating the history of patients and appointment days of doctors before main executes. So that appointments will be created before everything, and patients can directly access appointments.

Static keyword used for accessing it from everywhere (by boards of access modifier) and reserve a place just once. So that for same variable it will be prevented to use a lot of memory. For instance, in this project one of the places it used is in person class to define nextId since for every new object of doctor and patients nextId will increase and it will be saved on memory.

Final keyword specifies that the data it added can’t be changed anymore. So, when you have an important data that shouldn’t be changed through the execution you should use final keyword on it to not change it by mistake. In this project, one example usage can be said as that defining Id final as in person class. So that Id of that person never changes as it should be.

## Static method

## Static methods enable access to a class's methods without requiring the instantiation of an object of that class

In this project, this approach was employed to reduce dependencies on the Patient History class. Instead of each class needing to create its own Patient History object to access patient histories, a single instance of the Patient History class is created during system initialization (e.g., in the main method). Subsequently, other classes can directly access the Patient History class's static methods. However, using a static list within the Patient History class can introduce potential concurrency issues if multiple threads access and modify the list simultaneously.

## Implementation and Interface Usage

An interface is a contract that defines a set of methods a class must implement. It specifies _what_ a class should do, not _how_ it should do it. In Java, the implements keyword indicates that a class is implementing an interface.

In this project, interfaces were used to implement the Observer design pattern. The doctor list had numerous dependencies, and updates to the list were not consistently reflected across all dependent classes. To address this issue, the Observer pattern was employed, and interfaces played a crucial role in its implementation.

By using an interface, we ensured that each implementing class would include specific methods, such as update and delete. While we didn't know the specific implementation logic within each class, we were guaranteed the existence of these methods. This allowed us to interact with the classes directly through their shared interface, abstracting away the concrete implementations.

## Exception Handling

An exception is an event that occurs during program execution, disrupting the normal flow of instructions and signaling an unexpected or erroneous condition. To signal these exceptional situations, the throw keyword is used. When a specific error condition is met, a throw statement is executed, interrupting the normal program flow and potentially preventing undesirable outcomes. For example, if a user provides invalid input that would cause an error (such as dividing by zero or attempting to access an array index out of bounds), an exception can be thrown. This typically results in an informative message explaining the cause of the error and halting execution at the point where the exception was thrown _unless_ the exception is handled.

Exceptions can be handled using try-catch blocks. The try block encloses the code that might throw an exception. If an exception occurs within the try block, execution immediately jumps to the corresponding catch block, which contains the code to handle that specific type of exception. In this project, try-catch blocks were primarily used for exception handling.  

Within a try-catch block, you can "catch" unwanted situations (exceptions) and implement appropriate recovery or error-handling logic. Crucially, after an exception is caught and handled in the catch block, the program's execution _continues_ from the code _after_ the try-catch block. This is a key benefit: it prevents the program from abruptly crashing due to a localized error. This helps prevent system collapse due to a small, isolated problem. Without using try-catch blocks, unhandled exceptions can lead to program termination, data corruption, resource leaks (like memory leaks), or other unwanted scenarios. By using try-catch blocks, we can gracefully handle these exceptions, preventing these severe consequences and improving the robustness and stability of the system.

Let’s check one example from the project:

This picture illustrates the background processes that occur when a patient clicks the "book" button. Notably, exception handling using try-catch blocks is implemented within this process. As depicted in the diagram, the process begins with a call to the bookAppointment method inside the Appointment Manager class. Let's now examine the implementation of this class and how it handles appointment bookings, including the exception handling mechanisms.

Within this class, the program checks if the user already has an appointment scheduled for the same day. If a conflict is detected (i.e., the user _does_ have an appointment on the same day), an IllegalStateException is thrown. This exception is then propagated back to the Patient GUI class, which is the calling context of this method.

In addition to the same-day appointment check, several other validation checks are performed. A check in the parent class's method verifies if the available time slots for the doctor are null. If the slots are indeed null (which should not occur as the system automatically creates a TreeMap of slots when a doctor is added), an IllegalArgumentException is thrown, indicating an invalid argument or state of the system. Another check determines if the user has an existing appointment within 30 minutes of the requested time. If such a conflict exists, an IllegalStateException is thrown. Both these exceptions are propagated up to the Patient GUI. Finally, the system checks if the doctor has an available slot at the requested time. If no slot is available, another IllegalStateException is thrown and also sent to the Patient GUI.

The Patient GUI handles these exceptions using specific catch blocks: one for IllegalArgumentException and one for IllegalStateException. A general Exception catch block is also included as a safety net to handle any other unexpected exceptions that might be thrown by other methods, preventing the application from crashing due to unforeseen errors.

# Conclusion

In conclusion, this project provided valuable learning experiences while addressing various challenges. It was a beneficial exercise in understanding how a project should be developed and the processes involved in software development. Specifically, we learned the importance of:

- **Design Pattern:** Utilizing Observer Design pattern helped us create more maintainable and sustainable class relationships by promoting loose coupling and code reusability.
- **Data Structure Selection:** We gained practical experience in choosing appropriate data structures for specific tasks, optimizing performance and efficiency.
- **Java Programming:** We significantly improved our skills and understanding of the Java programming language through hands-on application.

This project served as a practical demonstration of software development principles and best practices, enhancing our ability to design, implement, and maintain robust and efficient software systems.
