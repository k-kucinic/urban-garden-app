package hr.java.garden.projektkucinic.model;

import java.time.LocalDate;

public sealed interface Harvestable permits MedicalHerbs {
    int calculateHarvestDate();

    String getState(LocalDate date);
}
