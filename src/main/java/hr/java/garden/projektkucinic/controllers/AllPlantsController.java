package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.UrbanGardenApplication;
import hr.java.garden.projektkucinic.comparator.PlantWateringComparator;
import hr.java.garden.projektkucinic.constants.SelectedPlant;
import hr.java.garden.projektkucinic.enUm.ChangeType;
import hr.java.garden.projektkucinic.enUm.PlantType;
import hr.java.garden.projektkucinic.exceptions.InvalidRoleException;
import hr.java.garden.projektkucinic.genericsi.ScheduleForGarden;
import hr.java.garden.projektkucinic.login.ChangeLogging;
import hr.java.garden.projektkucinic.model.Plant;
import hr.java.garden.projektkucinic.threads.DeserializationThread;
import hr.java.garden.projektkucinic.threads.PlantRefreshingThread;
import hr.java.garden.projektkucinic.threads.SerializationThread;
import hr.java.garden.projektkucinic.utils.DatabaseUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static hr.java.garden.projektkucinic.controllers.EditPlantController.yesOrCancelButton;
import static hr.java.garden.projektkucinic.utils.DatabaseUtils.logger;

public class AllPlantsController {
    @FXML
    private TableView<Plant> plantTableView;
    @FXML
    private TextField plantNameTextField;
    @FXML
    private ComboBox<String> plantTypeComboBoxString;
    @FXML
    private TableColumn<Plant, String> plantNameTableColumn;
    @FXML
    private TableColumn<Plant, String> plantTypeTableColumn;
    @FXML
    private TableColumn<Plant, String> plantingDateStringTableColumn;
    @FXML
    private TableColumn<Plant, String> wateringFrequencyTableColumn;
    @FXML
    private TableColumn<Plant, String> nextWateringDaysTableColumn;
    @FXML
    private ContextMenu contextMenu;


    public void initialize() {
        plantNameTableColumn.setCellValueFactory(plant -> new ReadOnlyStringWrapper(plant.getValue().getName()));
        plantTypeTableColumn.setCellValueFactory(plant -> new ReadOnlyStringWrapper(plant.getValue().getPlantType().getDisplayName()));
        plantingDateStringTableColumn.setCellValueFactory(plant -> new ReadOnlyStringWrapper(plant.getValue().getPlantingDate().format(ScheduleForGarden.DATE_FORMATTER)));
        wateringFrequencyTableColumn.setCellValueFactory(plantStringCellDataFeatures -> new ReadOnlyStringWrapper("every " + plantStringCellDataFeatures.getValue().getWateringFrequencyDays().days() + " days"));
        nextWateringDaysTableColumn.setCellValueFactory(plant -> new ReadOnlyStringWrapper(plant.getValue().daysUntilPlantNeedsToBeWatered()));
        plantTypeComboBoxString.setItems(FXCollections.observableArrayList(PlantType.allPlantTypes()));
        plantTableView.setRowFactory(categoryTableView1 -> {
            TableRow<Plant> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (!row.isEmpty() && mouseEvent.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(plantTableView, mouseEvent.getSceneX(), mouseEvent.getSceneY());
                }
            });
            return row;
        });
    }

    @FXML
    public void search() {
        PlantRefreshingThread plantRefreshingThread = new PlantRefreshingThread(this);
        Thread starter = new Thread(plantRefreshingThread);
        starter.start();
        List<Long> idsOfPlants = DatabaseUtils.plantIDsFromGardenInDatabase(UrbanGardenApplication.getGardenID());
        List<Plant> plants = DatabaseUtils.getPlants(idsOfPlants);
        List<Plant> finalPlants = new ArrayList<>();
        String name = plantNameTextField.getText();
        Optional<String> typeString = Optional.ofNullable(plantTypeComboBoxString.getValue());
        if (typeString.isPresent()) {
            PlantType plantType = PlantType.getPlantTypeByName(typeString.get());
            finalPlants = plants.stream()
                    .filter(plant -> plant.getName().contains(name))
                    .toList()
                    .stream()
                    .filter(plant -> plant.getPlantType().equals(plantType))
                    .collect(Collectors.toList());
        } else {
            finalPlants = plants.stream()
                    .filter(plant -> plant.getName().contains(name))
                    .collect(Collectors.toList());
        }
        finalPlants.sort(new PlantWateringComparator());
        plantTableView.setItems(FXCollections.observableArrayList(finalPlants));

    }

    public void deletePlant() {
        Plant selectedPlant = plantTableView.getSelectionModel().getSelectedItem();
        if (selectedPlant != null) {
            boolean confirmation = confirmDeleting(selectedPlant);
            if (confirmation == true) {
                ChangeLogging changeLogging = new ChangeLogging(ChangeType.DELETION, selectedPlant, UrbanGardenApplication.getUser(), LocalDateTime.now());
                List<ChangeLogging> changeLoggingList = new ArrayList<>();
                DeserializationThread deserializationThread = new DeserializationThread(ChangeType.DELETION);
                Thread thread = new Thread(deserializationThread);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
                changeLoggingList = deserializationThread.getChangeLoggingList();
                changeLoggingList.add(changeLogging);

                try {
                    SerializationThread serializationThread = new SerializationThread(changeLoggingList, ChangeType.DELETION);
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(serializationThread);
                    DatabaseUtils.deletePlantFromGarden(selectedPlant.getID());
                    DatabaseUtils.deletePlantFromDatabase(selectedPlant.getID());
                    confirmationOfChanges();
                    search();
                    executorService.shutdown();
                } catch (InvalidRoleException e) {
                    logger.error(e.getMessage());
                    invalidRole();
                }


            }

        }
    }

    private void confirmationOfChanges() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Changes saved");
        alert.setHeaderText(" Changes successfully saved!");
        alert.showAndWait();
    }

    private boolean confirmDeleting(Plant plant) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm the deletion of a plant " + plant.getName());
        alert.setHeaderText("Attention! The object will be deleted. Please confirm if you want to proceed with the deletion. ");
        return yesOrCancelButton(alert);
    }

    public void editPlant() {
        Plant selectedPlant = plantTableView.getSelectionModel().getSelectedItem();
        if (Optional.ofNullable(selectedPlant).isPresent()) {
            SelectedPlant.getSelectedPlant().setPlant(selectedPlant);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/garden/projektkucinic/edit-plant.fxml"));
                Scene scene = new Scene(loader.load());
                UrbanGardenApplication.getMainStage().setScene(scene);
                UrbanGardenApplication.getMainStage().show();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }


    }

    public void invalidRole() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("The plant was not deleted");
        alert.setContentText("Your role: " + UrbanGardenApplication.user.getRole().getRoleName() + " does not have a permission to delete a plant");
        alert.showAndWait();
    }

}



