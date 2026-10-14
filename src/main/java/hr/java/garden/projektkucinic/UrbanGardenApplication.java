package hr.java.garden.projektkucinic;

import hr.java.garden.projektkucinic.login.User;
import hr.java.garden.projektkucinic.model.UrbanGarden;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;


public class UrbanGardenApplication extends Application {
    public static Stage mainStage;
    public static User user;
    public static UrbanGarden urbanGarden;
    public static Long gardenID;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(UrbanGardenApplication.class.getResource("home.fxml"));
        stage.getIcons().add(new Image(new FileInputStream("images/icon.png")));
        Scene scene = new Scene(fxmlLoader.load(), 610, 400);
        stage.setTitle("Welcome!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setUser(User user) {
        UrbanGardenApplication.user = user;
    }

    public static User getUser() {
        return user;
    }

    public static UrbanGarden getUrbanGarden() {
        return urbanGarden;
    }

    public static void setUrbanGarden(UrbanGarden urbanGarden) {
        UrbanGardenApplication.urbanGarden = urbanGarden;
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static Long getGardenID() {
        return gardenID;
    }

    public static void setGardenID(Long gardenID) {
        UrbanGardenApplication.gardenID = gardenID;
    }
}