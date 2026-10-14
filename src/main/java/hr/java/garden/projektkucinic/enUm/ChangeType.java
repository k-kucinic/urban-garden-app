package hr.java.garden.projektkucinic.enUm;

import java.io.Serializable;

public enum ChangeType implements Serializable {
    DELETION("Deletion"), UPDATE("Update");
    String changeType;

    ChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getChangeType() {
        return changeType;
    }
}
