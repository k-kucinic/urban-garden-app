package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.UrbanGardenApplication;
import hr.java.garden.projektkucinic.model.Event;
import hr.java.garden.projektkucinic.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.Optional;

public class OrganizeNewEventController {
    @FXML
    private TextField eventNameTextField;
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private TextArea eventDescriptionTextArea;
    @FXML
    private TextField numberOfPeopleInvitedTextField;

    public void save() {
        StringBuilder message = new StringBuilder("");
        Optional<String> eventName = Optional.empty();
        Optional<LocalDate> eventDate = Optional.empty();
        Optional<String> eventDescription = Optional.empty();
        Optional<String> numberOfPeopleString = Optional.empty();
        Optional<Integer> numberOfPeopleInvited = Optional.empty();
        Optional<Event> newEvent = Optional.empty();
        if (!eventNameTextField.getText().isBlank()) {
            eventName = Optional.ofNullable(eventNameTextField.getText());
        } else {
            message.append("Event name must not be empty!\n");
        }
        if (Optional.ofNullable(eventDatePicker.getValue()).isPresent()) {
            eventDate = Optional.ofNullable(eventDatePicker.getValue());
        } else {
            message.append("Date of event must not be empty!\n");
        }
        if (!eventDescriptionTextArea.getText().isBlank()) {
            eventDescription = Optional.of(eventDescriptionTextArea.getText());
        } else {
            message.append(("Description must not be empty;"));
        }
        if (!numberOfPeopleInvitedTextField.getText().isBlank()) {
            numberOfPeopleString = Optional.of(numberOfPeopleInvitedTextField.getText());
            try {
                numberOfPeopleInvited = Optional.of(Integer.valueOf(numberOfPeopleString.get()));
            } catch (NumberFormatException e) {
                message.append("Number of people that are invited must have numeric format!");
            }
        }
        if (eventName.isPresent() && eventDate.isPresent() && eventDescription.isPresent() && numberOfPeopleInvited.isPresent()) {
            newEvent = Optional.ofNullable(new Event(null, eventName.get(), eventDate.get(), eventDescription.get(), numberOfPeopleInvited.get(), UrbanGardenApplication.getGardenID().intValue()));
            if (newEvent.isPresent()) {
                DatabaseUtils.writeEventIntoDatabase(newEvent.get());
                confirmationOfSavedChanges();

            }
        } else {

        }
    }

    private void confirmationOfSavedChanges() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Changes saved");
        alert.setHeaderText(" Changes successfully saved!");
        alert.showAndWait();
    }

    public void message(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("There were several mistakes: ");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
