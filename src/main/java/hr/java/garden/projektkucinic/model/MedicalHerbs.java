package hr.java.garden.projektkucinic.model;

import hr.java.garden.projektkucinic.enUm.PlantType;
import hr.java.garden.projektkucinic.enUm.SeasonOfInterest;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class MedicalHerbs extends Plant implements Harvestable, Serializable {
    private SeasonOfInterest seasonOfInterest;
    private int daysUntilOptimalHarvest;
    public static final BigDecimal FINAL_HEIGHT = BigDecimal.valueOf(0.120);

    public MedicalHerbs(String name, PlantType plantType, LocalDate plantingDate, BigDecimal requiredSpace,
                        BigDecimal waterNeeded, WateringFrequency wateringFrequencyDays, SeasonOfInterest season, Long id) {
        super();
        super.setName(name);
        super.setPlantType(plantType);
        super.setPlantingDate(plantingDate);
        super.setRequiredSpace(requiredSpace);
        super.setWaterNeeded(waterNeeded);
        super.setWateringFrequencyDays(wateringFrequencyDays);
        super.setID(id);
        this.daysUntilOptimalHarvest = calculateHarvestDate();
        this.seasonOfInterest = season;
        LocalDate nextWateringDate = WateringActivity.calculateNextWateringDate(this);
        while (nextWateringDate.isBefore(super.getPlantingDate().plusMonths(4))) {
            this.addEvent(new WateringActivity(nextWateringDate, this));
            nextWateringDate = WateringActivity.calculateNextWateringDate(this);
        }


        this.addEvent(new HarvestingActivity(this, seasonOfInterest.getStartingDate().atYear(2024).plusDays(daysUntilOptimalHarvest),
                "Leaves of " + this.getName() + " ready to harvest"));

    }

    public SeasonOfInterest getSeasonOfInterest() {
        return seasonOfInterest;
    }

    public LocalDate harvestDate() {
        return LocalDate.now().plusDays(daysUntilOptimalHarvest);
    }

    public void setSeasonOfInterest(SeasonOfInterest seasonOfInterest) {
        this.seasonOfInterest = seasonOfInterest;
    }

    public int getDaysUntilOptimalHarvest() {
        return daysUntilOptimalHarvest;
    }

    public void setDaysUntilOptimalHarvest(int daysUntilOptimalHarvest) {
        this.daysUntilOptimalHarvest = daysUntilOptimalHarvest;
    }

    @Override
    public int calculateHarvestDate() {

        BigDecimal howManyTimesItNeedsToBeWatered = FINAL_HEIGHT.divide(BigDecimal.valueOf(0.02), 0, RoundingMode.DOWN);
        return howManyTimesItNeedsToBeWatered.intValue() * super.getWateringFrequencyDays().days();
    }

    @Override
    public String getState(LocalDate date) {
        if (Plant.getSeasonForDate(date).compareTo(getSeasonOfInterest()) != 0) {
            return "Not in season.";
        } else {
            List<GardeningActivity> gardeningActivities = super.getEvents();
            Optional<GardeningActivity> event1 = gardeningActivities.stream()
                    .min(Comparator.comparingLong(event -> Math.abs(ChronoUnit.DAYS.between(event.getDate(), date))))
                    .map(Optional::ofNullable)
                    .orElse(Optional.empty());
            if (event1.isPresent()) {
                if (getHeightOnDate(event1.get().getDate()).compareTo(FINAL_HEIGHT) != 0) {
                    return "Still growing.";
                } else {
                    return "The leaves of plant " + this.getName() + " are ready to harvest!";
                }
            } else {
                return "Not enough data provided to calculate the current state of the plant.";
            }
        }
    }
}
