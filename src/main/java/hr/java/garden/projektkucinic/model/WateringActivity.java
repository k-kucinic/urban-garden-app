package hr.java.garden.projektkucinic.model;

import java.io.Serializable;
import java.time.LocalDate;

public class WateringActivity extends GardeningActivity implements Serializable {


    public WateringActivity(LocalDate date, Plant plant) {
        super(plant, date, "Watering");
        plant.setHowManyTimesHasPlantBeenWatered(plant.getHowManyTimesHasPlantBeenWatered() + 1);

    }

    public static int days(Plant plant) {
        return plant.getHowManyTimesHasPlantBeenWatered() * plant.getWateringFrequencyDays().days();
    }

    public static LocalDate calculateNextWateringDate(Plant plant) {
        if (plant.getHowManyTimesHasPlantBeenWatered() == 0) {
            return plant.getPlantingDate().plusDays(plant.getWateringFrequencyDays().days());
        } else {
            return plant.getPlantingDate().plusDays(days(plant));
        }
    }

    @Override
    public String toString() {
        return "watering the plant ";
    }
}
