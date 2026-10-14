package hr.java.garden.projektkucinic.model;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class GardeningActivity implements AllEvents, Serializable {
    protected Plant plant;
    protected LocalDate date;
    protected String description;

    public GardeningActivity(Plant plant, LocalDate date, String description) {

        this.plant = plant;
        this.date = date;
        this.description = description;

    }

    @Override
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    @Override
    public String toString() {
        return "GardenActivity" +
                "datum" + date +
                ", description='" + description + '\'' +
                '}';
    }
}
