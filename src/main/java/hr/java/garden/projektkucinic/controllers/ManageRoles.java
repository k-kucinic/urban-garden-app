package hr.java.garden.projektkucinic.controllers;

import hr.java.garden.projektkucinic.login.AdminRequest;
import hr.java.garden.projektkucinic.login.Role;
import hr.java.garden.projektkucinic.login.User;
import hr.java.garden.projektkucinic.utils.DatabaseUtils;
import hr.java.garden.projektkucinic.utils.FileUtils;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static hr.java.garden.projektkucinic.utils.DatabaseUtils.logger;

public class ManageRoles {
    @FXML
    private TableView<AdminRequest> requestTableView;
    @FXML
    private TableColumn<AdminRequest, String> usernameColumn;
    @FXML
    private TableColumn<AdminRequest, String> roleColumn;

    public void initialize() {
        List<AdminRequest> adminRequests = DatabaseUtils.getAdminRequestsFromDatabase();
        List<AdminRequest> notApprovedRequests=adminRequests
                .stream()
                .filter(adminRequest -> !adminRequest.isApproved())
                .collect(Collectors.toList());
        requestTableView.setItems(FXCollections.observableArrayList(notApprovedRequests));
        usernameColumn.setCellValueFactory(request -> new ReadOnlyStringWrapper(request.getValue().getUsername()));
        roleColumn.setCellValueFactory(request -> new ReadOnlyStringWrapper(request.getValue().getRequestedRole().getRoleName()));
        requestTableView.setRowFactory(requestTableView -> {
            TableRow<AdminRequest> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (!row.isEmpty() && mouseEvent.getClickCount() == 2) {
                    AdminRequest request = row.getItem();
                    conformation(request);
                }
            });
            return row;

        });
    }

    private void conformation(AdminRequest adminRequest) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Approval");
        alert.setContentText("Are you sure you want user" + adminRequest.getUsername() + " to have the role of: " + adminRequest.getRequestedRole());
        ButtonType saveButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(saveButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            ButtonType buttonType = result.get();
            if (buttonType == saveButton) {
                approve(adminRequest.getUsername(), adminRequest.getRequestedRole());
            }
        } else {
            Optional<Integer> idOptional = Optional.ofNullable(DatabaseUtils.findRequestID(adminRequest.getUsername()));
            if (idOptional.isPresent()) {
                DatabaseUtils.approveRequest(adminRequest.getUsername());
                initialize();
            }

        }
    }

    private void approve(String username, Role requestedRole) {
        List<User> allUsers = FileUtils.getUsersFromFile();
        for (User user : allUsers) {
            if (user.getUsername().compareTo(username) == 0) {
                user.setRole(requestedRole);
            }
        }
        logger.info("Promijenjeni user "+ username);
        FileUtils.writeUserInFile(allUsers);
        Optional<Integer> idOptional = Optional.ofNullable(DatabaseUtils.findRequestID(username));
        if (idOptional.isPresent()) {
            DatabaseUtils.approveRequest(username);
            initialize();
        }

    }
}
