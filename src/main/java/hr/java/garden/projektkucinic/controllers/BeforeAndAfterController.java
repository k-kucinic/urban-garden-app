package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.enUm.ChangeType;
import hr.java.garden.projektkucinic.genericsi.ScheduleForGarden;
import hr.java.garden.projektkucinic.login.ChangeLogging;
import hr.java.garden.projektkucinic.model.Plant;
import hr.java.garden.projektkucinic.utils.FileUtils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BeforeAndAfterController {
    @FXML
    private TableView<ChangeLogging> tableViewAfter;
    @FXML
    private TableView<Plant> tableViewBefore;
    @FXML
    private TableColumn<Plant, String> plantBeforeColumn;
    @FXML
    private TableColumn<ChangeLogging, String> plantAfterColumn;
    @FXML
    private TableColumn<ChangeLogging, String> userTableColumn;
    @FXML
    private TableColumn<ChangeLogging, String> dateOfChangeColumn;
    @FXML
    private TableColumn<ChangeLogging, String> timeOfChangeColumn;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public void initialize() {
        plantBeforeColumn.setCellValueFactory(plantStringCellDataFeatures -> new ReadOnlyStringWrapper(plantStringCellDataFeatures.getValue().toString()));
        plantAfterColumn.setCellValueFactory(plantStringCellDataFeatures -> new ReadOnlyObjectWrapper<>(plantStringCellDataFeatures.getValue().getPlant().toString()));
        userTableColumn.setCellValueFactory(user -> new ReadOnlyStringWrapper(user.getValue().getUser().getRole().getRoleName()));
        dateOfChangeColumn.setCellValueFactory(localDateTime -> new ReadOnlyStringWrapper(localDateTime.getValue().getLocalDate().toLocalDate().format(ScheduleForGarden.DATE_FORMATTER)));
        timeOfChangeColumn.setCellValueFactory(localDateTime -> new ReadOnlyStringWrapper(localDateTime.getValue().getLocalDate().format(formatter)));

    }

    public void deletedPlants() {
        List<ChangeLogging> changeLogging = FileUtils.deserializePlants(ChangeType.DELETION);
        tableViewAfter.setItems(FXCollections.observableArrayList(changeLogging));
        tableViewBefore.setVisible(false);
    }

    public void changedPlants() {
        tableViewBefore.setVisible(true);
        List<ChangeLogging> allChanges = FileUtils.deserializePlants(ChangeType.UPDATE);
        List<ChangeLogging> before = new ArrayList<>();
        List<ChangeLogging> after = new ArrayList<>();
        for (int i = 0; i < allChanges.size(); i++) {
            if (i % 2 != 0) {
                after.add(allChanges.get(i));
            } else {
                before.add(allChanges.get(i));
            }
        }
        List<Plant> plants = before
                .stream()
                .map(changeLogging -> changeLogging.getPlant())
                .toList();
        tableViewBefore.setItems(FXCollections.observableArrayList(plants));
        tableViewAfter.setItems(FXCollections.observableArrayList(after));

    }
}
