package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.UrbanGardenApplication;
import hr.java.garden.projektkucinic.login.PasswordHashing;
import hr.java.garden.projektkucinic.login.Role;
import hr.java.garden.projektkucinic.login.User;
import hr.java.garden.projektkucinic.model.Plant;
import hr.java.garden.projektkucinic.utils.DatabaseUtils;
import hr.java.garden.projektkucinic.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static hr.java.garden.projektkucinic.utils.DatabaseUtils.logger;

public class UrbanGardenController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    private static final String visitorPath="/hr/java/garden/projektkucinic/welcome-screen-2.fxml";
    private static final String administrativePath="/hr/java/garden/projektkucinic/welcome-screen-1.fxml";


    public void initialize() {
        loginButton.setDefaultButton(true);
    }

    public void login() {
        String username = usernameTextField.getText();
        String passwordNormal = passwordField.getText();
        List<User> users = FileUtils.getUsersFromFile();
        Optional<User> finalUser = Optional.empty();
        boolean isUserNameCorrect = false;
        for (User user : users) {
            if (user.getUsername().compareTo(username) == 0) {
                isUserNameCorrect = true;
                finalUser = Optional.of(user);
                break;
            }
        }

        if (!isUserNameCorrect) {
            wrongPasswordAlert();

        } else {
            if (PasswordHashing.checkIfPasswordIsCorrect(passwordNormal, finalUser.get().getPassword())) {
                UrbanGardenApplication.setUser(finalUser.get());
                UrbanGardenApplication.setUrbanGarden(finalUser.get().getGarden());
                UrbanGardenApplication.setGardenID(finalUser.get().getGarden().getId());
                Set<Plant> plantsInGarden = new HashSet<>();
                List<Long> plantIDs = DatabaseUtils.plantIDsFromGardenInDatabase(finalUser.get().getGarden().getId());
                plantsInGarden = DatabaseUtils.getPlants(plantIDs).stream().collect(Collectors.toSet());
                UrbanGardenApplication.getUrbanGarden().setPlants(plantsInGarden);
                String path="";
                if(finalUser.get().getRole()== Role.VISITOR){
                    path=visitorPath;
                }
                else{
                    path=administrativePath;
                }
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
                    Scene scene = new Scene(loader.load());
                    UrbanGardenApplication.getMainStage().setScene(scene);
                    UrbanGardenApplication.getMainStage().show();

                } catch (IOException e) {
                    logger.error(e.getMessage());
                }

            } else {
                wrongPasswordAlert();
            }
        }

    }

    public void newUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/garden/projektkucinic/new-user.fxml"));
            Scene scene = new Scene(loader.load());
            UrbanGardenApplication.getMainStage().setScene(scene);
            UrbanGardenApplication.getMainStage().show();

        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void wrongPasswordAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Wrong login information");
        alert.setHeaderText("Please try again");
        alert.showAndWait();
    }


}