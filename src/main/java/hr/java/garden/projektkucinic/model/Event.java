package hr.java.garden.projektkucinic.model;

import hr.java.garden.projektkucinic.utils.DatabaseUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;

public class Event implements AllEvents {
    private String eventName;
    private LocalDate eventDate;
    private String eventDescription;
    private int numberOfPeopleInvited;
    private Long id;
    private int gardenId;

    public Event(Long id, String eventName, LocalDate eventDate, String eventDescription, int numberOfPeopleInvited, int gardenID) {
        this.id = id;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
        this.numberOfPeopleInvited = numberOfPeopleInvited;
        this.gardenId = gardenID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }


    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public int getNumberOfPeopleInvited() {
        return numberOfPeopleInvited;
    }

    public void setNumberOfPeopleInvited(int numberOfPeopleInvited) {
        this.numberOfPeopleInvited = numberOfPeopleInvited;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventName, eventDate);
    }

    public String getName() {
        return eventName;
    }

    @Override
    public LocalDate getDate() {
        return eventDate;
    }

    @Override
    public String getDescription() {
        return eventDescription;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public int getGardenId() {
        return gardenId;
    }

    public String getGardenLocation() {
        return DatabaseUtils.getUrbanGardenByID(new HashSet<>(), this.gardenId).getLocation();
    }

    public void setGardenId(int gardenId) {
        this.gardenId = gardenId;
    }

    @Override
    public String toString() {
        return eventName;
    }
}

