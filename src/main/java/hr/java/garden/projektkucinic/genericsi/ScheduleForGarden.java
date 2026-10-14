package hr.java.garden.projektkucinic.genericsi;

import hr.java.garden.projektkucinic.comparator.GardenEventComparator;
import hr.java.garden.projektkucinic.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static hr.java.garden.projektkucinic.utils.DatabaseUtils.logger;


public class ScheduleForGarden<T extends AllEvents, V extends Plant> {
    private UrbanGarden garden;
    private List<T> events;
    private Map<V, List<T>> plantEventsMap;
    private Map<LocalDate, List<T>> datesAndAllEventsSorted = new TreeMap<>(Comparator.naturalOrder());
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    public ScheduleForGarden(UrbanGarden garden) {
        this.garden = garden;
        this.events = new ArrayList<>();
        this.plantEventsMap = new HashMap<>();
        for (Plant plant : garden.getPlants()) {
            List<T> gardenActivities = new ArrayList<>();

            for (GardeningActivity gardenActivity : (List<GardeningActivity>) plant.getEvents()) {
                gardenActivities.add((T) gardenActivity);
                this.events.add((T) gardenActivity);
            }
            this.plantEventsMap.put((V) plant, gardenActivities);
        }
        for (Event event : garden.getEventsInGarden()) {
            this.events.add((T) event);

        }
        sortAllEventsByDate();
        this.datesAndAllEventsSorted = events.stream()
                .collect(Collectors.groupingBy(AllEvents::getDate, TreeMap::new, Collectors.toList()));

    }

    public static BigDecimal completeAreaNeededToPlantAllPlants(Set<Plant> plants) {
        return plants
                .stream().
                map(v -> v.getRequiredSpace())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public UrbanGarden getGarden() {
        return garden;
    }

    public void setGarden(UrbanGarden garden) {
        this.garden = garden;
    }

    public List<T> getEvents() {
        return events;
    }

    public Map<LocalDate, List<T>> getDatesAndAllEventsSorted() {
        return datesAndAllEventsSorted;
    }


    public void setEvents(List<T> events) {
        this.events = events;
    }

    public Map<V, List<T>> getPlantEventsMap() {
        return plantEventsMap;
    }

    public void setPlantEventsMap(Map<V, List<T>> plantEventsMap) {
        this.plantEventsMap = plantEventsMap;
    }


    public void sortAllEventsByDate() {
        events.sort(new GardenEventComparator());
    }

    public Map<LocalDate, String> datesAndEventsString() {
        Map<LocalDate, String> datesAndEventsString = new TreeMap<>(Comparator.naturalOrder());
        String eventDescString = "";
        for (Map.Entry<LocalDate, List<T>> entry : datesAndAllEventsSorted.entrySet()) {
            LocalDate date = entry.getKey();
            List<AllEvents> gardeningActivityList = (List<AllEvents>) entry.getValue();
            int size = gardeningActivityList.size();
            for (int i = 0; i < size; i++) {
                AllEvents gardeningActivity = gardeningActivityList.get(i);
                if (gardeningActivity instanceof GardeningActivity) {
                    if (gardeningActivity instanceof HarvestingActivity harvestingActivity) {
                        eventDescString += harvestingActivity.getDescription();
                    } else {
                        eventDescString += gardeningActivity + " " + ((GardeningActivity) gardeningActivity).getPlant().getName();
                    }

                }
                if (gardeningActivity instanceof Event) {
                    eventDescString += gardeningActivity.toString();
                }

                if (i < size - 1) {
                    eventDescString += ", ";
                }
            }
            datesAndEventsString.put(date, eventDescString);
            eventDescString = "";

        }
        return datesAndEventsString;
    }

    public Map<LocalDate, String> datesAndEventsStringFiltered(boolean isPlantingActivity, boolean isWateringActivity, boolean isHarvestingActivity, boolean isEvent) {
        Map<LocalDate, String> datesAndEventsString = new TreeMap<>(Comparator.naturalOrder());

        for (Map.Entry<LocalDate, List<T>> entry : datesAndAllEventsSorted.entrySet()) {
            LocalDate date = entry.getKey();
            StringBuilder eventDescStringBuilder = new StringBuilder();

            if (isEvent) {
                List<Event> eventsOnly = filterAllEventsWithEvent(entry.getValue());
                for (int i = 0; i < eventsOnly.size(); i++) {
                    Event gardeningActivity = eventsOnly.get(i);
                    eventDescStringBuilder.append(gardeningActivity);
                    if (i < eventsOnly.size() - 1) {
                        eventDescStringBuilder.append(", ");
                    }
                }
            } else if (isPlantingActivity || isWateringActivity || isHarvestingActivity) {
                List<GardeningActivity> gardeningActivitiesOnly = filterGardeningActivities(entry.getValue(), isPlantingActivity, isWateringActivity, isHarvestingActivity);
                for (int i = 0; i < gardeningActivitiesOnly.size(); i++) {
                    GardeningActivity gardeningActivity = gardeningActivitiesOnly.get(i);
                    if (gardeningActivity instanceof HarvestingActivity harvestingActivity) {
                        eventDescStringBuilder.append(harvestingActivity.getDescription());
                    } else {
                        eventDescStringBuilder.append(gardeningActivity);
                        eventDescStringBuilder.append(" ").append(gardeningActivity.getPlant().getName());
                    }
                    if (i < gardeningActivitiesOnly.size() - 1) {
                        eventDescStringBuilder.append(", ");
                    }
                }
            } else {
                return datesAndEventsString();
            }
            if (!eventDescStringBuilder.toString().isBlank()) {
                datesAndEventsString.put(date, eventDescStringBuilder.toString());
            }

        }

        return datesAndEventsString;
    }

    private List<GardeningActivity> filterGardeningActivities(List<T> events, boolean isPlantingActivity, boolean isWateringActivity, boolean isHarvestingActivity) {
        List<GardeningActivity> gardeningActivities = new ArrayList<>();
        for (T event : events) {
            if (isWateringActivity) {
                if (event instanceof WateringActivity wateringActivity)
                    gardeningActivities.add(wateringActivity);
            } else if (isPlantingActivity) {
                if (event instanceof PlantingActivity plantingActivity)
                    gardeningActivities.add(plantingActivity);
            } else if (isHarvestingActivity) {
                if (event instanceof HarvestingActivity harvestingActivity) {
                    gardeningActivities.add(harvestingActivity);
                }

            }
        }
        return gardeningActivities;
    }


    private List<Event> filterAllEventsWithEvent(List<T> events) {
        List<Event> socialEvents = new ArrayList<>();
        for (T event : events) {
            if (event instanceof Event event1) {
                socialEvents.add(event1);
            }
        }
        return socialEvents;
    }


    private List<HarvestingActivity> filterAllEventsWithHarvestingActivity(List<T> events) {
        List<HarvestingActivity> socialEvents = new ArrayList<>();
        for (T event : events) {
            if (event instanceof HarvestingActivity event1) {
                socialEvents.add(event1);
            }
        }
        return socialEvents;
    }

    @Override
    public String toString() {
        String output = "";

        for (Map.Entry<LocalDate, List<T>> entry : datesAndAllEventsSorted.entrySet()) {
            LocalDate date = entry.getKey();
            List<AllEvents> gardeningActivityList = (List<AllEvents>) entry.getValue();

            System.out.print(date.format(DATE_FORMATTER) + ": ");

            int size = gardeningActivityList.size();
            for (int i = 0; i < size; i++) {
                AllEvents gardeningActivity = gardeningActivityList.get(i);
                if (gardeningActivity instanceof GardeningActivity) {
                    System.out.print(gardeningActivity + " " + ((GardeningActivity) gardeningActivity).getPlant().getName());
                }
                if (gardeningActivity instanceof Event) {
                    System.out.print(gardeningActivity);
                }

                if (i < size - 1) {
                    System.out.print(", ");
                }
            }

            System.out.println();

            System.out.println();
        }
        return output;
    }

    public void addEvents(List<Event> events) {
        if (events.size() == 0) {
            logger.debug("LISTA JE PRAZNA (metoda addEvents)");
        }
        List<T> events1 = new ArrayList<>();
        for (Event event : events) {
            if (datesAndAllEventsSorted.containsKey(event.getDate())) {
                List<T> eventsOfDate = datesAndAllEventsSorted.get(event.getDate());
                eventsOfDate.add((T) event);
                datesAndAllEventsSorted.put(event.getDate(), eventsOfDate);
            } else {
                events1.add((T) event);
                datesAndAllEventsSorted.put(event.getDate(), events1);
            }
        }
        sortAllEventsByDate();
    }


    private Optional<List<T>> isAnythingPlannedForThisDateList(LocalDate localDate) {
        List<T> plannedEvents = events.stream()
                .filter(event -> event.getDate().equals(localDate))
                .collect(Collectors.toList());

        return plannedEvents.isEmpty() ? Optional.empty() : Optional.of(plannedEvents);
    }

    public boolean isAnythingPlannedForThisDate(LocalDate localDate) {
        return isAnythingPlannedForThisDateList(localDate).isPresent();
    }

    public static List<LocalDate> nextSevenDays() {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            dateList.add(currentDate.plusDays(i));
        }

        return dateList;
    }

}
