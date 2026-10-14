package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.UrbanGardenApplication;
import hr.java.garden.projektkucinic.genericsi.ScheduleForGarden;
import hr.java.garden.projektkucinic.model.Event;
import hr.java.garden.projektkucinic.utils.DatabaseUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EditEventController {
    @FXML
    private ComboBox<Event> eventComboBox;
    @FXML
    private TextField eventNameTextField;
    @FXML
    private TextField eventDatePicker;
    @FXML
    private TextArea eventDescriptionTextArea;
    @FXML
    private TextField numberOfPeopleInvitedTextField;
    @FXML
    private TextField eventNameTextFieldAfter;
    @FXML
    private DatePicker eventDatePickerAfter;
    @FXML
    private TextArea eventDescriptionTextAreaAfter;
    @FXML
    private TextField numberOfPeopleInvitedTextFieldAfter;
    private static Event selectedEvent;

    public void initialize() {
        List<Event> eventList = DatabaseUtils.getEventsFromGarden();
        eventComboBox.setItems(FXCollections.observableArrayList(eventList));
    }

    public void choose() {
        Optional<Event> chosenEvent;
        if (Optional.ofNullable(eventComboBox.getValue()).isPresent()) {
            chosenEvent = Optional.of(eventComboBox.getValue());
            selectedEvent = chosenEvent.get();
            eventNameTextField.setText(chosenEvent.get().getEventName());
            eventDescriptionTextArea.setText(chosenEvent.get().getDescription());
            eventDatePicker.setEditable(false);
            eventDatePicker.setText(chosenEvent.get().getDate().format(ScheduleForGarden.DATE_FORMATTER));
            numberOfPeopleInvitedTextField.setText(String.valueOf(chosenEvent.get().getNumberOfPeopleInvited()));
        }

    }

    public void save() {
        StringBuilder message = new StringBuilder("");
        Optional<String> eventName = Optional.empty();
        Optional<LocalDate> eventDate = Optional.empty();
        Optional<String> eventDescription = Optional.empty();
        Optional<String> numberOfPeopleString = Optional.empty();
        Optional<Integer> numberOfPeopleInvited = Optional.empty();
        Optional<Event> newEvent = Optional.empty();
        if (!eventNameTextFieldAfter.getText().isBlank()) {
            eventName = Optional.ofNullable(eventNameTextFieldAfter.getText());
        } else {
            eventName = Optional.of(eventNameTextField.getText());
        }
        if (Optional.ofNullable(eventDatePickerAfter.getValue()).isPresent()) {
            eventDate = Optional.ofNullable(eventDatePickerAfter.getValue());
        } else {
            eventDate = Optional.of(LocalDate.parse(eventDatePicker.getText(), ScheduleForGarden.DATE_FORMATTER));
        }
        if (!eventDescriptionTextAreaAfter.getText().isBlank()) {
            eventDescription = Optional.of(eventDescriptionTextAreaAfter.getText());
        } else {
            eventDescription = Optional.of(eventDescriptionTextArea.getText());
        }
        if (!numberOfPeopleInvitedTextFieldAfter.getText().isBlank()) {
            numberOfPeopleString = Optional.ofNullable(numberOfPeopleInvitedTextFieldAfter.getText());
            if (numberOfPeopleString.isPresent()) {
                try {
                    numberOfPeopleInvited = Optional.of(Integer.valueOf(numberOfPeopleString.get()));
                } catch (NumberFormatException e) {
                    message.append("Number of people that are invited must have numeric format!");
                }
            }
        } else {
            numberOfPeopleString = Optional.of(numberOfPeopleInvitedTextField.getText());
            numberOfPeopleInvited = Optional.of(Integer.valueOf(numberOfPeopleString.get()));
        }
        if (eventName.isPresent() && eventDate.isPresent() && eventDescription.isPresent() && numberOfPeopleInvited.isPresent()) {
            newEvent = Optional.ofNullable(new Event(null, eventName.get(), eventDate.get(), eventDescription.get(), numberOfPeopleInvited.get(), Math.toIntExact(UrbanGardenApplication.getUrbanGarden().getId())));
            if (newEvent.isPresent()) {
                DatabaseUtils.editEvent(selectedEvent, newEvent.get());
                confirmationOfSavedChanges();
            }
        } else {

        }
    }

    public void message(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("There were several mistakes: ");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void confirmationOfSavedChanges() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Changes saved");
        alert.setHeaderText(" Changes successfully saved!");
        alert.showAndWait();
    }
}
