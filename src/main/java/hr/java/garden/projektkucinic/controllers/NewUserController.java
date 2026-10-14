package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.UrbanGardenApplication;
import hr.java.garden.projektkucinic.login.Role;
import hr.java.garden.projektkucinic.login.User;
import hr.java.garden.projektkucinic.model.UrbanGarden;
import hr.java.garden.projektkucinic.utils.DatabaseUtils;
import hr.java.garden.projektkucinic.utils.FileUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static hr.java.garden.projektkucinic.utils.DatabaseUtils.logger;

public class NewUserController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatedPasswordField;
    @FXML
    private ComboBox<Role> roleComboBox;
    @FXML
    private ComboBox<String> gardenComboBox;

    public void initialize() {
        List<UrbanGarden> gardens = DatabaseUtils.getGardensFromDatabase();
        List<String> gardenLocationStrings = gardens.stream()
                .map(UrbanGarden::getLocation)
                .collect(Collectors.toList());
        roleComboBox.setItems(FXCollections.observableArrayList(Role.values()));
        gardenComboBox.setItems(FXCollections.observableArrayList(gardenLocationStrings));
    }

    public void createNewAccount() {
        List<UrbanGarden> gardens = DatabaseUtils.getGardensFromDatabase();
        Optional<String> userName = Optional.empty();
        Optional<String> password = Optional.empty();
        Optional<String> passwordRepeated = Optional.empty();
        Optional<Role> role = Optional.empty();
        Optional<User> user = Optional.empty();
        Optional<UrbanGarden> garden = Optional.empty();
        StringBuilder message = new StringBuilder();
        if (!usernameTextField.getText().isBlank()) {
            if (usernameTextField.getText().length() < 7) {
                message.append("Username must at least have 8 characters!\n");
            } else {
                if (isUsernameTaken(usernameTextField.getText())) {
                    message.append("This username is already taken\n");
                } else {
                    userName = Optional.of(usernameTextField.getText());
                }
            }
        } else {
            message.append("Username must not be empty\n");
        }
        if (!passwordField.getText().isBlank()) {
            if (passwordField.getText().length() < 7)
                message.append("Password must at least have 8 characters!\n");
            else password = Optional.of(passwordField.getText());
        } else message.append("Password field must not be empty\n");
        if (!repeatedPasswordField.getText().isBlank()) {
            passwordRepeated = Optional.of(repeatedPasswordField.getText());
        } else message.append("Please repeat your password\n");
        if (Optional.ofNullable(roleComboBox.getValue()).isPresent()) {
            Role requestedRole = roleComboBox.getValue();
            if ((requestedRole.equals(Role.ADMIN) || requestedRole.equals(Role.GARDENER)) && userName.isPresent()) {
                DatabaseUtils.insertAdminRequest(userName.get(), requestedRole, false);
                message.append("Your admin request has been sent, for now you are visitor.");
                role = Optional.of(Role.VISITOR);

            } else {
                role = Optional.of(roleComboBox.getValue());
            }

        } else message.append("Please choose a role!\n");
        if (Optional.ofNullable(gardenComboBox.getValue()).isPresent()) {
            garden = Optional.of(UrbanGarden.getGardenByLocation(gardenComboBox.getValue(), gardens));
            UrbanGardenApplication.setUrbanGarden(garden.get());

        } else message.append("Please choose a garden!\n");
        if (userName.isPresent() && passwordRepeated.isPresent() && password.isPresent() && role.isPresent() && garden.isPresent()) {
            if (passwordRepeated.get().compareTo(password.get()) == 0) {
                user = Optional.of(new User(userName.get(), password.get(), role.get(), garden.get()));
            } else {
                message.append("Passwords are not matching!\n");

            }
        }
        if (user.isPresent()) {
            List<User> users = FileUtils.getUsersFromFile();
            users.add(user.get());
            FileUtils.writeUserInFile(users);
            UrbanGardenApplication.setUser(user.get());
            UrbanGardenApplication.setGardenID(garden.get().getId());
            if (message.length() > 1) {
                errors(message);
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/garden/projektkucinic/welcome-screen-2.fxml"));
                Scene scene = new Scene(loader.load());
                UrbanGardenApplication.getMainStage().setScene(scene);
                UrbanGardenApplication.getMainStage().show();

            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        } else {
            errors(message);
        }

    }

    private void errors(StringBuilder message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Please try again");
        alert.setContentText(message.toString());
        alert.showAndWait();
    }

    private Boolean isUsernameTaken(String usernameNew) {
        List<User> users = FileUtils.getUsersFromFile();
        List<String> usernames = users.stream().map(user1 -> user1.getUsername()).collect(Collectors.toList());
        for (String username : usernames) {
            if (username.compareTo(usernameNew) == 0) {
                return true;
            }
        }
        return false;

    }

}
