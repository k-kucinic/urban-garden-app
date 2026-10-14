package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.genericsi.ScheduleForGarden;
import hr.java.garden.projektkucinic.model.Event;
import hr.java.garden.projektkucinic.model.UrbanGarden;
import hr.java.garden.projektkucinic.utils.DatabaseUtils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UpcomingSocialEventsController {
    @FXML
    private TableView<Event> events;
    @FXML
    private TableColumn<Event, String> eventDateColumn;
    @FXML
    private TableColumn<Event, String> eventNameColumn;
    @FXML
    private TableColumn<Event, String> locationColumn;
    @FXML
    private TableColumn<Event, Integer> numberOfPeopleInvitedColumn;
    @FXML
    private ComboBox<String> gardenComboBox;


    public void initialize() {
        List<UrbanGarden> urbanGardens = DatabaseUtils.getGardensFromDatabase();
        List<String> gardenLocations = urbanGardens
                .stream()
                .map(urbanGarden -> urbanGarden.getLocation())
                .collect(Collectors.toList());
        gardenComboBox.setItems(FXCollections.observableArrayList(gardenLocations));
        eventDateColumn.setCellValueFactory(event -> new ReadOnlyStringWrapper(event.getValue().getDate()
                .format(ScheduleForGarden.DATE_FORMATTER)));
        eventNameColumn.setCellValueFactory(event -> new ReadOnlyStringWrapper(event.getValue().getEventName()));
        locationColumn.setCellValueFactory(event -> new ReadOnlyStringWrapper(event.getValue().getGardenLocation()));
        numberOfPeopleInvitedColumn.setCellValueFactory(event -> new ReadOnlyObjectWrapper<>(event.getValue().getNumberOfPeopleInvited()));


    }

    public void choose() {
        if (Optional.ofNullable(gardenComboBox.getValue()).isEmpty()) {
            List<Event> allEvents = DatabaseUtils.getEventsFromDatabase();
            events.setItems(FXCollections.observableArrayList(allEvents));
        } else {
            List<Event> filteredEvents = DatabaseUtils.getEventsByGardenLocation(gardenComboBox.getValue());
            events.setItems(FXCollections.observableArrayList(filteredEvents));
        }

    }


}
