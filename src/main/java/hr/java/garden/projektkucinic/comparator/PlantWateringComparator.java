package hr.java.garden.projektkucinic.comparator;

import hr.java.garden.projektkucinic.model.Plant;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PlantWateringComparator implements Comparator<Plant> {
    @Override
    public int compare(Plant plant1, Plant plant2) {
        long days1 = daysUntilNextWatering(plant1);
        long days2 = daysUntilNextWatering(plant2);
        return Long.compare(days1, days2);
    }

    private long daysUntilNextWatering(Plant plant) {
        List<LocalDate> wateringDates = plant.allWateringDates(plant);
        Optional<LocalDate> nextClosestDay = findClosestDate(wateringDates);
        return nextClosestDay.map(date -> ChronoUnit.DAYS.between(LocalDate.now(), date)).orElse(0L);
    }

    private Optional<LocalDate> findClosestDate(List<LocalDate> dates) {
        return dates.stream()
                .filter(date -> !date.isBefore(LocalDate.now()))
                .min(Comparator.naturalOrder());
    }
}
