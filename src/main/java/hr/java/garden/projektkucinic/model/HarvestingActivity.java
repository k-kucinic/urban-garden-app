package hr.java.garden.projektkucinic.model;

import hr.java.garden.projektkucinic.exceptions.PlantIsNotPlantedException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class HarvestingActivity extends GardeningActivity implements Serializable {
    private int daysBetweenPlantingAndHarvesting;

    public HarvestingActivity(Plant plant, LocalDate date, String description) {
        super(plant, date, description);
        try {
            this.daysBetweenPlantingAndHarvesting = calculateDaysBetweenPlantingAndHarvesting();
        } catch (PlantIsNotPlantedException e) {
            this.daysBetweenPlantingAndHarvesting = 0;
        }
        if (plant instanceof MedicalHerbs) {
            ((MedicalHerbs) plant).setDaysUntilOptimalHarvest(daysBetweenPlantingAndHarvesting);
        }
    }

    public int calculateDaysBetweenPlantingAndHarvesting() {
        if (plant.getPlantingDate().isAfter(LocalDate.now())) {
            throw new PlantIsNotPlantedException("Plant is not planted yet");
        }
        return (int) ChronoUnit.DAYS.between(date, plant.getPlantingDate());
    }

    @Override
    public String toString() {
        return description;
    }
}
