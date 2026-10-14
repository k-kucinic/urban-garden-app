package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.UrbanGardenApplication;
import hr.java.garden.projektkucinic.enUm.PlantType;
import hr.java.garden.projektkucinic.exceptions.NotEnoughSpaceInGarden;
import hr.java.garden.projektkucinic.exceptions.PlantAlreadyPlantedException;
import hr.java.garden.projektkucinic.model.Plant;
import hr.java.garden.projektkucinic.model.UrbanGarden;
import hr.java.garden.projektkucinic.model.WateringFrequency;
import hr.java.garden.projektkucinic.utils.DatabaseUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static hr.java.garden.projektkucinic.utils.DatabaseUtils.findPlantID;
import static hr.java.garden.projektkucinic.utils.DatabaseUtils.logger;

public class PlantANewPlantController {
    @FXML
    private TextField plantNameTextField;
    @FXML
    private ComboBox<String> plantTypeComboBox;
    @FXML
    private DatePicker plantingDatePicker;
    @FXML
    private TextField requiredSpaceTextField;
    @FXML
    private TextField waterNeededTextField;
    @FXML
    private TextField wateringFrequencyTextField;

    public void initialize() {
        List<String> plantTypes = PlantType.allPlantTypes();
        plantTypeComboBox.setItems(FXCollections.observableArrayList(plantTypes));
    }

    public void newPlant() {
        Optional<String> plantName = Optional.empty();
        Optional<String> plantTypeString = Optional.empty();
        Optional<LocalDate> plantingDate = Optional.empty();
        StringBuilder message = new StringBuilder();
        Optional<PlantType> plantType = Optional.empty();
        Optional<WateringFrequency> wateringFrequency = Optional.empty();
        Optional<BigDecimal> waterNeeded = Optional.empty();
        Optional<BigDecimal> requiredSpace = Optional.empty();
        int numberOfMistakes = 0;
        List<Plant> plants = DatabaseUtils.getPlantsFromDatabase();
        UrbanGarden urbanGarden = UrbanGardenApplication.getUrbanGarden();
        Optional<Plant> plant = Optional.empty();
        if (!plantNameTextField.getText().isBlank()) {
            plantName = Optional.ofNullable(plantNameTextField.getText());
            if (plantName.isPresent()) {
                try {
                    Plant.
                            isPlantNameSame(plantName.get(), plants);
                } catch (PlantAlreadyPlantedException e) {
                    logger.error(e.getMessage());
                    message.append(e.getMessage());
                    plantName = Optional.empty();
                    numberOfMistakes++;
                }
            }
        } else {
            message.append("Plant name must not be empty!\n");
            numberOfMistakes++;
        }
        if (Optional.ofNullable(plantTypeComboBox.getValue()).isPresent()) {
            plantType = Optional.ofNullable(PlantType.getPlantTypeByName(plantTypeComboBox.getValue()));
        } else {
            message.append("Plant type must be selected.\n");
            numberOfMistakes++;
        }
        if (Optional.ofNullable(plantingDatePicker.getValue()).isPresent()) {
            plantingDate = Optional.ofNullable(plantingDatePicker.getValue());
        } else {
            message.append("Planting date must be selected!\n");
            numberOfMistakes++;
        }
        if (!requiredSpaceTextField.getText().isBlank()) {
            try {
                requiredSpace = Optional.of(new BigDecimal(requiredSpaceTextField.getText()));
                if (requiredSpace.isPresent()) {
                    try {
                        if (urbanGarden.isThereEnoughSpaceForANewPlant(requiredSpace.get())) {
                        }
                    } catch (NotEnoughSpaceInGarden e) {
                        logger.error(e.getMessage());
                        message.append(e.getMessage());
                        numberOfMistakes++;
                        requiredSpace = Optional.empty();
                    }
                }
            } catch (NumberFormatException e) {
                message.append("Required space must have numeric value!\n");
                numberOfMistakes++;
                logger.error(e.getMessage());
            }
        } else {
            message.append("Required space must not be empty!\n");
            numberOfMistakes++;
        }
        if (!wateringFrequencyTextField.getText().isBlank()) {
            try {
                wateringFrequency = Optional.of(new WateringFrequency(Integer.parseInt(wateringFrequencyTextField.getText())));
            } catch (NumberFormatException e) {
                message.append("Watering frequency must have numeric value!\n");
                numberOfMistakes++;
                logger.error(e.getMessage());
            }
        } else {
            message.append("Watering frequency must not be empty!\n");
            numberOfMistakes++;
        }
        if (!waterNeededTextField.getText().isBlank()) {
            try {
                waterNeeded = Optional.of(new BigDecimal(waterNeededTextField.getText()));
            } catch (NumberFormatException e) {
                message.append("Water needed must have numeric value!\n");
                numberOfMistakes++;
                logger.error(e.getMessage());
            }
        } else {
            message.append("Water needed must not be empty!\n");
            numberOfMistakes++;
        }
        if (message.toString().length() > 1) {
            showMistakes(message, numberOfMistakes);
            logger.info(message.toString());
        } else {
            if (plantName.isPresent() && plantingDate.isPresent() && plantType.isPresent()
                    && requiredSpace.isPresent() && waterNeeded.isPresent() && wateringFrequency.isPresent()) {
                plant = Optional.of(new Plant.PlantBuilder()
                        .withId(1L)
                        .withName(plantName.get())
                        .withType(plantType.get())
                        .withPlantingDate(plantingDate.get())
                        .requiredSpace(requiredSpace.get())
                        .withWaterNeeded(waterNeeded.get())
                        .withWateringFrequency(wateringFrequency.get())
                        .build());
                if (plant.isPresent()) {
                    DatabaseUtils.writePlantIntoDatabase(plant.get());
                    long plantID = findPlantID(plantName.get());
                    DatabaseUtils.writePlantIntoGarden(plantID);
                    confirmationOfSavedChanges();
                }

            }
        }
    }

    private void showMistakes(StringBuilder message, int numberOfMistakes) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        if (numberOfMistakes > 1) {
            alert.setHeaderText("Several mistakes were made:\n");
            alert.setContentText(message.toString());
        } else {
            alert.setHeaderText(message.toString());
        }
        alert.showAndWait();
    }

    private void confirmationOfSavedChanges() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Changes saved");
        alert.setHeaderText(" Changes successfully saved!");
        alert.showAndWait();
    }


}

