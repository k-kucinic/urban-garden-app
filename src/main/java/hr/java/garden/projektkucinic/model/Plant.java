package hr.java.garden.projektkucinic.model;

import hr.java.garden.projektkucinic.enUm.PlantType;
import hr.java.garden.projektkucinic.enUm.SeasonOfInterest;
import hr.java.garden.projektkucinic.exceptions.PlantAlreadyPlantedException;
import hr.java.garden.projektkucinic.exceptions.PlantIsNotPlantedException;
import hr.java.garden.projektkucinic.genericsi.ScheduleForPlant;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static hr.java.garden.projektkucinic.utils.DatabaseUtils.logger;


public class Plant extends ScheduleForPlant implements Serializable {
    public static class PlantBuilder {
        private Long ID;
        private String name;
        private PlantType plantType;
        private LocalDate plantingDate;
        private BigDecimal requiredSpace;
        private BigDecimal waterNeeded;

        private WateringFrequency wateringFrequencyDays;


        public PlantBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public PlantBuilder withType(PlantType plantType) {
            this.plantType = plantType;
            return this;
        }

        public PlantBuilder withPlantingDate(LocalDate plantingDate) {
            this.plantingDate = plantingDate;

            return this;
        }

        public PlantBuilder requiredSpace(BigDecimal requiredSpace) {
            this.requiredSpace = requiredSpace;
            return this;
        }

        public PlantBuilder withWaterNeeded(BigDecimal waterNeeded) {
            this.waterNeeded = waterNeeded;
            return this;
        }

        public PlantBuilder withWateringFrequency(WateringFrequency wateringFrequencyDays) {
            this.wateringFrequencyDays = wateringFrequencyDays;
            return this;
        }

        public PlantBuilder withId(Long ID) {
            this.ID = ID;
            return this;
        }

        public Plant build() {
            Plant plant = new Plant();
            plant.name = this.name;
            plant.plantType = this.plantType;
            plant.plantingDate = this.plantingDate;
            plant.requiredSpace = this.requiredSpace;
            plant.waterNeeded = this.waterNeeded;
            plant.wateringFrequencyDays = this.wateringFrequencyDays;
            plant.ID = this.ID;
            LocalDate nextWateringDate = WateringActivity.calculateNextWateringDate(plant);
            while (nextWateringDate.isBefore(this.plantingDate.plusMonths(10))) {
                plant.addEvent(new WateringActivity(nextWateringDate, plant));
                nextWateringDate = WateringActivity.calculateNextWateringDate(plant);
            }
            try {
                plant.addEvent(new PlantingActivity(plantingDate, 1, plant));
            } catch (PlantAlreadyPlantedException | PlantIsNotPlantedException e) {
                logger.error(e.getMessage());
            }
            return plant;
        }


        public String getName() {
            return name;
        }

        public PlantType getPlantType() {
            return plantType;
        }

        public LocalDate getPlantingDate() {
            return plantingDate;
        }

        public BigDecimal getRequiredSpace() {
            return requiredSpace;
        }

        public BigDecimal getWaterNeeded() {
            return waterNeeded;
        }

        public WateringFrequency getWateringFrequencyDays() {
            return wateringFrequencyDays;
        }

        public Long getID() {
            return ID;
        }

        public void setID(Long ID) {
            this.ID = ID;
        }
    }

    private Long ID;
    private String name;
    private PlantType plantType;
    private LocalDate plantingDate;
    private BigDecimal requiredSpace;
    private BigDecimal waterNeeded;
    private WateringFrequency wateringFrequencyDays;
    private Map<LocalDate, BigDecimal> heightOnExactDate;
    private int howManyTimesHasPlantBeenWatered;

    protected Plant() {
    }


    public BigDecimal getHeightOnDate(LocalDate date) {
        List<LocalDate> eventList = getHeightOnExactDate().keySet().stream().toList();
        Optional<LocalDate> localDateOptional = eventList.stream()
                .min(Comparator.comparingLong(event -> Math.abs(ChronoUnit.DAYS.between(event, date))))
                .map(Optional::of)
                .orElse(Optional.empty());
        if (localDateOptional.isPresent()) {
            if (Optional.ofNullable(this.getHeightOnExactDate().get(localDateOptional.get())).isPresent()) {
                logger.debug(String.valueOf(this.getHeightOnExactDate().get(localDateOptional.get())));
                return this.getHeightOnExactDate().get(localDateOptional.get());
            } else {
                return BigDecimal.valueOf(0);
            }

        } else {
            return BigDecimal.valueOf(0);
        }


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlantType getPlantType() {
        return plantType;
    }

    public void setPlantType(PlantType plantType) {
        this.plantType = plantType;
    }

    public LocalDate getPlantingDate() {
        return plantingDate;
    }


    public BigDecimal getRequiredSpace() {
        return requiredSpace;
    }

    public void setRequiredSpace(BigDecimal requiredSpace) {
        this.requiredSpace = requiredSpace;
    }

    public BigDecimal getWaterNeeded() {
        return waterNeeded;
    }

    public void setWaterNeeded(BigDecimal waterNeeded) {
        this.waterNeeded = waterNeeded;
    }


    public void setPlantingDate(LocalDate plantingDate) {
        this.plantingDate = plantingDate;
    }

    public WateringFrequency getWateringFrequencyDays() {
        return wateringFrequencyDays;
    }

    public Map<LocalDate, BigDecimal> getHeightOnExactDate() {
        return heightOnExactDate;
    }

    public void setHeightOnExactDate(Map<LocalDate, BigDecimal> heightOnExactDate) {
        this.heightOnExactDate = heightOnExactDate;
    }

    public void setWateringFrequencyDays(WateringFrequency wateringFrequencyDays) {
        this.wateringFrequencyDays = wateringFrequencyDays;
    }


    public int getHowManyTimesHasPlantBeenWatered() {
        return howManyTimesHasPlantBeenWatered;
    }

    public void setHowManyTimesHasPlantBeenWatered(int howManyTimesHasPlantBeenWatered) {
        this.howManyTimesHasPlantBeenWatered = howManyTimesHasPlantBeenWatered;
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public static SeasonOfInterest getSeasonForDate(LocalDate date) {
        MonthDay monthDay = MonthDay.from(date);
        for (SeasonOfInterest season : SeasonOfInterest.values()) {
            if (monthDay.isBefore(season.getEndingDate()) && monthDay.isAfter(season.getStartingDate())) {
                return season;
            } else if (monthDay.compareTo(season.getStartingDate()) == 0) {
                return season;
            } else if (monthDay.compareTo(season.getEndingDate()) == 0) {
                return season;
            }
        }

        return SeasonOfInterest.WINTER;
    }

    @Override
    public String toString() {
        return name + " " + requiredSpace + " " + waterNeeded + " " + wateringFrequencyDays.days();
    }

    public static Plant getCopyFromOtherPlant(Plant otherPlant) {
        return new PlantBuilder()
                .withName(otherPlant.getName())
                .withId(otherPlant.getID())
                .withPlantingDate(otherPlant.getPlantingDate())
                .withType(otherPlant.getPlantType())
                .withWateringFrequency(otherPlant.getWateringFrequencyDays())
                .withWaterNeeded(otherPlant.getWaterNeeded())
                .requiredSpace(otherPlant.getRequiredSpace())
                .build();

    }


    public String daysUntilPlantNeedsToBeWatered() {
        List<LocalDate> wateringDates = super.allWateringDates(this);
        Optional<LocalDate> nextClosestDay = findClosestDate(wateringDates);
        if (nextClosestDay.isPresent()) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), nextClosestDay.get());
            if (days == 0) {
                return "Today";
            } else if (days == 1) {
                return "Tomorrow";
            } else return ChronoUnit.DAYS.between(LocalDate.now(), nextClosestDay.get()) + " days";
        } else {
            return "Today";
        }

    }

    private static Optional<LocalDate> findClosestDate(List<LocalDate> dateList) {
        Optional<LocalDate> closestDate = Optional.empty();
        long minDifference = Long.MAX_VALUE;
        for (LocalDate date : dateList) {
            long difference = Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), date));

            if (difference < minDifference) {
                minDifference = difference;
                closestDate = Optional.ofNullable(date);
            }
        }
        return closestDate;
    }

    public static boolean isPlantNameSame(String plant, List<Plant> plants) throws PlantAlreadyPlantedException {
        List<String> names = plants
                .stream()
                .map(plant1 -> plant1.getName())
                .toList();
        for (String name : names) {
            if (name.equals(plant)) {
                throw new PlantAlreadyPlantedException("Plant with this name has already been planted!");
            }
        }
        return false;
    }

}

