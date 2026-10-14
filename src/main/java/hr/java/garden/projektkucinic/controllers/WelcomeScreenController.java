package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.UrbanGardenApplication;
import hr.java.garden.projektkucinic.model.UrbanGarden;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WelcomeScreenController {
    @FXML
    private Label usernameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label ownerLabel;
    @FXML
    private Label areaLabel;

    public void initialize() {
        usernameLabel.setText(UrbanGardenApplication.user.getUsername());
        roleLabel.setText(UrbanGardenApplication.user.getRole().getRoleName());
        UrbanGarden garden = UrbanGardenApplication.getUrbanGarden();
        locationLabel.setText(garden.getLocation());
        ownerLabel.setText(garden.getOwner().fullName());
        areaLabel.setText(garden.getArea().toString());

    }


}
