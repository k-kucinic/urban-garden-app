package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.UrbanGardenApplication;
import hr.java.garden.projektkucinic.constants.SelectedPlant;
import hr.java.garden.projektkucinic.enUm.ChangeType;
import hr.java.garden.projektkucinic.login.ChangeLogging;
import hr.java.garden.projektkucinic.model.Plant;
import hr.java.garden.projektkucinic.model.WateringFrequency;
import hr.java.garden.projektkucinic.utils.DatabaseUtils;
import hr.java.garden.projektkucinic.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static hr.java.garden.projektkucinic.utils.DatabaseUtils.logger;

public class EditPlantController {
    private Plant plant;
    @FXML
    private TextField nameTextFieldBefore;
    @FXML
    private TextField requiredSpaceTextFieldBefore;
    @FXML
    private TextField wateringFrequencyTextFieldBefore;
    @FXML
    private TextField waterNeededTextFieldBefore;

    @FXML
    private TextField nameTextFieldAfter;
    @FXML
    private TextField requiredSpaceTextFieldAfter;
    @FXML
    private TextField wateringFrequencyTextFieldAfter;
    @FXML
    private TextField waterNeededTextFieldAfter;
    private final Plant plantBefore = SelectedPlant.getSelectedPlant().getPlant();

    public void initialize() {
        plant = SelectedPlant.getSelectedPlant().getPlant();
        nameTextFieldBefore.setText(plant.getName());
        requiredSpaceTextFieldBefore.setText(plant.getRequiredSpace().toString());
        wateringFrequencyTextFieldBefore.setText(String.valueOf(plant.getWateringFrequencyDays().days()));
        waterNeededTextFieldBefore.setText(plant.getWaterNeeded().toString());
    }

    public void saveChanges() {
        Plant plantAfterChanges = Plant.getCopyFromOtherPlant(plant);
        List<ChangeLogging> plantsSerialized = new ArrayList<>();
        Plant.PlantBuilder plantBuilder = new Plant.PlantBuilder();
        StringBuilder messageForError = new StringBuilder();
        Optional<String> name = Optional.empty();
        Optional<BigDecimal> requiredSpace = Optional.empty();
        Optional<BigDecimal> waterNeeded = Optional.empty();
        Optional<Integer> wateringFrequency = Optional.empty();
        plantBuilder.withId(plant.getID());
        if (!nameTextFieldAfter.getText().isBlank()) {
            name = Optional.of(nameTextFieldAfter.getText());
            if (name.get().compareTo(plant.getName()) != 0) {
                plantBuilder.withName(name.get());
                plantAfterChanges.setName(name.get());
            } else {
                messageForError.append("The old name is the same as the new name!\n");
            }
        }
        if (!requiredSpaceTextFieldAfter.getText().isBlank()) {
            try {
                requiredSpace = Optional.of(new BigDecimal(requiredSpaceTextFieldAfter.getText()));
                if (requiredSpace.get().compareTo(new BigDecimal(requiredSpaceTextFieldBefore.getText())) != 0) {
                    plantBuilder.requiredSpace(requiredSpace.get());
                    plantAfterChanges.setRequiredSpace(requiredSpace.get());
                } else {
                    messageForError.append("The old required space is same as the new required space");
                }
            } catch (NumberFormatException e) {
                messageForError.append("Required space must have numeric format!\n");
                logger.error(e.getMessage());
            }
        }
        if (!waterNeededTextFieldAfter.getText().isBlank()) {
            try {
                waterNeeded = Optional.of(new BigDecimal(waterNeededTextFieldAfter.getText()));
                if (waterNeeded.get().compareTo(new BigDecimal(waterNeededTextFieldBefore.getText())) != 0) {
                    plantBuilder.withWaterNeeded(waterNeeded.get());
                    plantAfterChanges.setWaterNeeded(waterNeeded.get());
                } else {
                    messageForError.append("Old value for water needed is the same as the new one!\n");
                }
            } catch (NumberFormatException e) {
                messageForError.append("Water needed must have numeric format!\n");
                logger.error(e.getMessage());
            }
        }
        if (!wateringFrequencyTextFieldAfter.getText().isBlank()) {
            try {
                wateringFrequency = Optional.of((Integer.valueOf(wateringFrequencyTextFieldAfter.getText())));
                WateringFrequency wateringFrequency1 = new WateringFrequency(wateringFrequency.get());
                if (wateringFrequency.get().compareTo(Integer.valueOf(wateringFrequencyTextFieldBefore.getText())) != 0) {
                    plantBuilder.withWateringFrequency(wateringFrequency1);
                    plantAfterChanges.setWateringFrequencyDays(wateringFrequency1);
                } else {
                    messageForError.append("The old watering frequency is same as the new!\n");
                }
            } catch (NumberFormatException e) {
                messageForError.append("Watering frequency must have numeric format!\n");
                logger.error(e.getMessage());
            }
        }
        if (messageForError.length() < 1 && !isBuilderEmpty(plantBuilder)) {
            if (confirmChanges()) {
                plantsSerialized = FileUtils.deserializePlants(ChangeType.UPDATE);
                ChangeLogging changeLoggingBefore = new ChangeLogging(ChangeType.UPDATE, this.plantBefore, UrbanGardenApplication.getUser(), LocalDateTime.now());
                ChangeLogging changeLoggingAfter = new ChangeLogging(ChangeType.UPDATE, plantAfterChanges, UrbanGardenApplication.getUser(), LocalDateTime.now());
                plantsSerialized.add(changeLoggingBefore);
                plantsSerialized.add(changeLoggingAfter);
                FileUtils.serializePlantChanges(plantsSerialized, ChangeType.UPDATE);
                DatabaseUtils.editPlantInDatabase(plantBuilder);
                confirmationOfSavedChanges();
            }
        } else {
            mistakes(messageForError);
        }

    }

    private boolean isBuilderEmpty(Plant.PlantBuilder plantBuilder) {
        if (nameTextFieldAfter.getText().isBlank() && requiredSpaceTextFieldAfter.getText().isBlank() &&
                wateringFrequencyTextFieldAfter.getText().isBlank() && waterNeededTextFieldAfter.getText().isBlank()) {
            return true;
        } else return false;
    }


    private boolean confirmChanges() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm the changes made to a plant. ");
        alert.setHeaderText("Attention! The object will be changed. Please confirm if you want to proceed with the changes. ");
        return yesOrCancelButton(alert);
    }

    static boolean yesOrCancelButton(Alert alert) {
        ButtonType saveButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(saveButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            ButtonType buttonType = result.get();
            if (buttonType == saveButton) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    private void confirmationOfSavedChanges() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Changes saved");
        alert.setHeaderText(" Changes successfully saved!");
        alert.showAndWait();
    }

    private void mistakes(StringBuilder stringBuilder) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("There were several mistakes: ");
        alert.setHeaderText(stringBuilder.toString());
        alert.showAndWait();
    }


}
