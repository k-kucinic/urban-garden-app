package hr.java.garden.projektkucinic.model;

import hr.java.garden.projektkucinic.exceptions.PlantAlreadyPlantedException;
import hr.java.garden.projektkucinic.exceptions.PlantIsNotPlantedException;

import java.math.BigDecimal;
import java.time.LocalDate;


public class PlantingActivity extends GardeningActivity {
    private int quantity;

    private BigDecimal usedSpace;

    public PlantingActivity(LocalDate date, int quantity, Plant plant) throws PlantAlreadyPlantedException, PlantIsNotPlantedException {
        super(plant, date, "Planting");
        this.quantity = quantity;
        plant.setPlantingDate(date);
        plant.addEvent(this);
        LocalDate nextWateringDate = WateringActivity.calculateNextWateringDate(this.plant);
        while (nextWateringDate.isBefore(date.plusMonths(2))) {
            this.plant.addEvent(new WateringActivity(nextWateringDate, this.plant));
            nextWateringDate = WateringActivity.calculateNextWateringDate(this.plant);
        }
        this.usedSpace = plant.getRequiredSpace().multiply(BigDecimal.valueOf(quantity));
        plant.setRequiredSpace(usedSpace);
//        if (plant instanceof MedicalHerbs) {
//            plant.addEvent(new HarvestingActivity(plant,
//                    ((MedicalHerbs) plant).getSeasonOfInterest().getStartingDate().atYear(2024).plusDays
//                            (((MedicalHerbs) plant).getDaysUntilOptimalHarvest()),
//                    "Leaves of " + plant.getName() + " ready to harvest."));
//
//        }
    }

    public void plantingNewPlant() {
        plant.setPlantingDate(super.getDate());
    }


    public void setPlant(Plant plant) {
        this.plant = plant;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public Plant getPlant() {
        return plant;
    }


    @Override
    public String toString() {
        return "planting the plant ";
    }
}
