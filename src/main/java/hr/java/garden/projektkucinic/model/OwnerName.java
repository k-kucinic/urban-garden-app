package hr.java.garden.projektkucinic.model;

import java.io.Serializable;

public class OwnerName implements Serializable {
    private String firstName;
    private String lastName;

    public OwnerName(String firstName, String lastName) {
        char firstChar = Character.toUpperCase(firstName.charAt(0));
        String restOfName = firstName.substring(1).toLowerCase();
        this.firstName = firstChar + restOfName;
        char firstCharLastName = Character.toUpperCase(lastName.charAt(0));
        String restOfLastName = lastName.substring(1).toLowerCase();
        this.lastName = firstCharLastName + restOfLastName;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
