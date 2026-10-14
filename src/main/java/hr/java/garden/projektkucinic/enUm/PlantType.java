package hr.java.garden.projektkucinic.enUm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PlantType implements Serializable {
    FLOWER("Flower", 1),
    VEGETABLE("Vegetable", 2),
    FRUIT("Fruit", 3),
    HERBS("Herbs", 4),
    ORNAMENTAL_PLANT("Ornamental plant", 5);

    private String displayName;
    private int plantTypeId;

    PlantType(String displayName, int plantTypeId) {
        this.displayName = displayName;
        this.plantTypeId = plantTypeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPlantTypeId() {
        return plantTypeId;
    }

    public static PlantType getPlantTypeByName(String nameOfType) {
        for (PlantType p : PlantType.values()) {
            if (nameOfType.compareTo(p.getDisplayName()) == 0) {
                return p;
            }
        }
        return PlantType.FLOWER;
    }

    public static List<String> allPlantTypes() {
        return Arrays.stream(PlantType.values()).toList().stream().map(plantType -> plantType.getDisplayName()).collect(Collectors.toList());

    }
}
