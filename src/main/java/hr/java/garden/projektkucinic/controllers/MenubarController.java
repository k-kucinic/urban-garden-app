package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.UrbanGardenApplication;
import hr.java.garden.projektkucinic.exceptions.InvalidRoleException;
import hr.java.garden.projektkucinic.login.Role;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import java.io.IOException;

import static hr.java.garden.projektkucinic.utils.DatabaseUtils.logger;

public class MenubarController {
    public void logOut() {
        UrbanGardenApplication.setUser(null);
        UrbanGardenApplication.setGardenID(null);
        UrbanGardenApplication.setUrbanGarden(null);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/garden/projektkucinic/home.fxml"));
            Scene scene = new Scene(loader.load());
            UrbanGardenApplication.getMainStage().setScene(scene);
            UrbanGardenApplication.getMainStage().show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void allPlantsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/garden/projektkucinic/all-plants.fxml"));
            Scene scene = new Scene(loader.load());
            UrbanGardenApplication.getMainStage().setScene(scene);
            UrbanGardenApplication.getMainStage().show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    public void allCommunityActivities() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/garden/projektkucinic/upcoming-social-events.fxml"));
            Scene scene = new Scene(loader.load());
            UrbanGardenApplication.getMainStage().setScene(scene);
            UrbanGardenApplication.getMainStage().show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void allPlanedActivities() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/garden/projektkucinic/all-planed-activities.fxml"));
            Scene scene = new Scene(loader.load());
            UrbanGardenApplication.getMainStage().setScene(scene);
            UrbanGardenApplication.getMainStage().show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void newEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/garden/projektkucinic/organize-new-event.fxml"));
            Scene scene = new Scene(loader.load());
            UrbanGardenApplication.getMainStage().setScene(scene);
            UrbanGardenApplication.getMainStage().show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void newPlant() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/garden/projektkucinic/plant-a-new-plant.fxml"));
            Scene scene = new Scene(loader.load());
            UrbanGardenApplication.getMainStage().setScene(scene);
            UrbanGardenApplication.getMainStage().show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void seeChanges() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/garden/projektkucinic/before-and-after.fxml"));
            Scene scene = new Scene(loader.load());
            UrbanGardenApplication.getMainStage().setScene(scene);
            UrbanGardenApplication.getMainStage().show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void editEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/garden/projektkucinic/edit-event.fxml"));
            Scene scene = new Scene(loader.load());
            UrbanGardenApplication.getMainStage().setScene(scene);
            UrbanGardenApplication.getMainStage().show();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void manage() {
        try {
            roleChecker();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/hr/java/garden/projektkucinic/manage-roles.fxml"));
                Scene scene = new Scene(loader.load());
                UrbanGardenApplication.getMainStage().setScene(scene);
                UrbanGardenApplication.getMainStage().show();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        } catch (InvalidRoleException e) {
            invalidRole();
            logger.error(e.getMessage());
        }

    }


    public void invalidRole() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Role");
        alert.setContentText("You do not have a permission to access this source");
        alert.showAndWait();
    }

    private void roleChecker() throws InvalidRoleException {
        if (UrbanGardenApplication.getUser().getRole().compareTo(Role.ADMIN) != 0) {
            throw new InvalidRoleException("You do not have the right role to access this source.");
        }

    }
}
