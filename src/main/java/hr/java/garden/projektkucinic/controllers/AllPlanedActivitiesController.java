package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.UrbanGardenApplication;
import hr.java.garden.projektkucinic.genericsi.ScheduleForGarden;
import hr.java.garden.projektkucinic.model.AllEvents;
import hr.java.garden.projektkucinic.model.Event;
import hr.java.garden.projektkucinic.model.Plant;
import hr.java.garden.projektkucinic.model.UrbanGarden;
import hr.java.garden.projektkucinic.threads.EventRefreshingThread;
import hr.java.garden.projektkucinic.utils.DatabaseUtils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AllPlanedActivitiesController {
    @FXML
    private ComboBox<String> activityType;
    @FXML
    private TableView<Map.Entry<LocalDate, String>> allEventsTableView;
    @FXML
    private TableColumn<Map.Entry<LocalDate, String>, String> datesForEventsTableColumn;
    @FXML
    private TableColumn<Map.Entry<LocalDate, String>, String> allEventsForDateTableColumn;


    public void initialize() {
        datesForEventsTableColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getKey().format(ScheduleForGarden.DATE_FORMATTER)));
        allEventsForDateTableColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getValue()));
        List<String> comboBoxValues = List.of("Watering Activity", "Planting Activity", "Harvesting Activity", "Events");
        activityType.setItems(FXCollections.observableArrayList(comboBoxValues));
    }

    public void show() {
        UrbanGarden urbanGarden = UrbanGardenApplication.getUrbanGarden();
        Map<LocalDate, String> eventsAndActivities = getLocalDateStringMap(urbanGarden);
        Map<LocalDate, String> filteredMap = new TreeMap<>(eventsAndActivities
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().isBefore(LocalDate.now()))
                .filter(entry -> !entry.getKey().isAfter(LocalDate.now().plusMonths(3)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        ObservableMap<LocalDate, String> observableMap = FXCollections.observableMap(filteredMap);
        allEventsTableView.setItems(FXCollections.observableArrayList(observableMap.entrySet()));
        EventRefreshingThread eventRefreshingThread = new EventRefreshingThread(this);
        Thread starter = new Thread(eventRefreshingThread);
        starter.start();

    }

    private Map<LocalDate, String> getLocalDateStringMap(UrbanGarden urbanGarden) {
        List<Event> events = DatabaseUtils.getEventsFromGarden();
        ScheduleForGarden<AllEvents, Plant> scheduleForGarden = new ScheduleForGarden<>(urbanGarden);
        String activityTypeString = "";
        scheduleForGarden.addEvents(events);
        Map<LocalDate, String> eventsAndActivities = new TreeMap<>();
        if (Optional.ofNullable(activityType.getValue()).isPresent()) {
            activityTypeString = activityType.getValue();
            switch (activityTypeString) {
                case "Watering Activity":
                    eventsAndActivities = scheduleForGarden.datesAndEventsStringFiltered(false, true, false, false);
                    break;
                case "Harvesting Activity":
                    eventsAndActivities = scheduleForGarden.datesAndEventsStringFiltered(false, false, true, false);
                    break;
                case "Events":
                    eventsAndActivities = scheduleForGarden.datesAndEventsStringFiltered(false, false, false, true);
                    break;
                case "Planting Activity":
                    eventsAndActivities = scheduleForGarden.datesAndEventsStringFiltered(true, false, false, false);
                    break;
            }
        } else {
            eventsAndActivities = scheduleForGarden.datesAndEventsString();
        }
        return eventsAndActivities;
    }

}
