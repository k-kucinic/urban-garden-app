package hr.java.garden.projektkucinic.model;

import hr.java.garden.projektkucinic.exceptions.NotEnoughSpaceInGarden;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

public class UrbanGarden implements Serializable {
    private String location;
    private OwnerName owner;
    protected BigDecimal length;
    protected BigDecimal width;
    private Set<Plant> plants;
    private List<Event> eventsInGarden;
    private long id;

    public UrbanGarden(long id, String location, String ownerName, String ownerLastName, BigDecimal length,
                       BigDecimal width, Set<Plant> plants) {
        this.id = id;
        this.location = location;
        this.owner = new OwnerName(ownerName, ownerLastName);
        this.length = length;
        this.width = width;
        Set<Plant> plantsThatFit = plantsThatFit(plants);
        this.plants = plantsThatFit;
        this.eventsInGarden = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    private Set<Plant> plantsThatFit(Set<Plant> plants) {
        Set<Plant> plantsThatFit = new HashSet<>();
        BigDecimal remainingArea = getArea();
        for (Plant plant : plants) {
            remainingArea.subtract(plant.getRequiredSpace());
            if (remainingArea.compareTo(BigDecimal.ZERO) > 0) {
                plantsThatFit.add(plant);
            }
        }
        return plantsThatFit;
    }

    public boolean isThereEnoughSpaceForANewPlant(BigDecimal plant) {
        if (availableSpace().subtract(plant).intValue() >= 0) {
            return true;
        } else {
            throw new NotEnoughSpaceInGarden("This plant cannot be plated as there is not enough space.");
        }
    }

    private BigDecimal availableSpace() {
        return getArea().subtract(occupiedSpace());
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public OwnerName getOwner() {
        return owner;
    }

    public void setOwner(OwnerName owner) {
        this.owner = owner;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public Set<Plant> getPlants() {
        return plants;
    }

    public void setPlants(Set<Plant> plants) {
        this.plants = plants;
    }

    public BigDecimal getArea() {
        return width.multiply(length);
    }

    public BigDecimal occupiedSpace() {
        return plants.stream()
                .map(plant -> plant.getRequiredSpace())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getRemainingSpace() {
        return getArea().subtract(occupiedSpace());

    }

    public void organiseAnEvent(Event event) {
        eventsInGarden.add(event);
    }

    public List<Event> getEventsInGarden() {
        return eventsInGarden;
    }

    public static UrbanGarden getGardenByLocation(String location, List<UrbanGarden> gardens) {
        Optional<UrbanGarden> urbanGarden = Optional.empty();
        for (UrbanGarden garden : gardens) {
            if (garden.getLocation().compareTo(location) == 0)
                urbanGarden = Optional.of(garden);
        }
        return urbanGarden.get();
    }
}
