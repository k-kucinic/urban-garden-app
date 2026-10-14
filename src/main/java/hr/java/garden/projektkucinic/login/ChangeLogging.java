package hr.java.garden.projektkucinic.login;

import hr.java.garden.projektkucinic.enUm.ChangeType;
import hr.java.garden.projektkucinic.model.Plant;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChangeLogging implements Serializable {
    private ChangeType changeType;
    private Plant plant;
    private User user;
    private LocalDateTime localDateTime;

    public ChangeLogging(ChangeType changeType, Plant plant, User user, LocalDateTime localDate) {
        this.changeType = changeType;
        this.plant = plant;
        this.user = user;
        this.localDateTime = localDate;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getLocalDate() {
        return localDateTime;
    }

    public void setLocalDate(LocalDateTime localDate) {
        this.localDateTime = localDate;
    }
}
