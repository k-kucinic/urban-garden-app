package hr.java.garden.projektkucinic.genericsi;

import hr.java.garden.projektkucinic.model.GardeningActivity;
import hr.java.garden.projektkucinic.model.Plant;
import hr.java.garden.projektkucinic.model.WateringActivity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ScheduleForPlant<T extends GardeningActivity> implements Serializable {
    protected List<T> events;

    public ScheduleForPlant() {
        this.events = new ArrayList<>();
    }

    public void addEvent(T event) {
        events.add(event);
    }

    public List<GardeningActivity> getEvents() {
        return (List<GardeningActivity>) events;
    }

    public LocalDate getLastTimePlantWasWatered(Plant plant) {
        Optional<LocalDate> ld = plant.getEvents().stream()
                .filter(activity -> activity instanceof WateringActivity)
                .map(activity -> ((WateringActivity) activity).getDate())
                .reduce((previous, current) -> current);

        if (ld.isPresent()) {
            return ld.get();
        } else {
            return plant.getPlantingDate();
        }

    }

    public Optional<LocalDate> getNewWateringDay(Plant plant) {

        return Optional.of(getLastTimePlantWasWatered(plant).plusDays(plant.getWateringFrequencyDays().days()));

    }

    public List<LocalDate> allWateringDates(Plant plant) {
        List<LocalDate> allWateringDates = new ArrayList<>();
        for (T event : events) {
            if (event instanceof WateringActivity wateringActivity) {
                if (wateringActivity.getDate().isAfter(LocalDate.now())) {
                    allWateringDates.add(wateringActivity.getDate());
                }

            }
        }
        return allWateringDates;
    }

}

