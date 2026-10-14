package hr.java.garden.projektkucinic.constants;

import hr.java.garden.projektkucinic.model.Plant;

public class SelectedPlant {
    private static final SelectedPlant SELECTED_PLANT = new SelectedPlant();

    private Plant plant;

    private SelectedPlant() {
    }

    public static SelectedPlant getSelectedPlant() {
        return SELECTED_PLANT;
    }

    public Plant getPlant() {
        return plant;
    }

    public void setPlant(Plant plant) {
        this.plant = plant;
    }
}
