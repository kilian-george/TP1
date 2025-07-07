package guiPageClasses;

import entityClasses.AdminRequest;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databaseClasses.Database;

import java.util.List;

public class GUIAdminRequestPage {
    private Stage stage;
    private Database theDatabase;
    private User currentUser;

    public GUIAdminRequestPage(Stage stage, Database db, User user) {
        this.stage = stage;
        this.theDatabase = db;
        this.currentUser = user;
        setup();
    }

    public void setup() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label header = new Label("Submit New Admin Request");
        header.setStyle("-fx-font-size: 16pt; -fx-font-weight: bold;");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Enter your request description...");
        descriptionField.setWrapText(true);

        TableView<AdminRequest> openTable = new TableView<>();
        openTable.setPrefHeight(250);

        TableColumn<AdminRequest, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> data.getValue().descriptionProperty());
        descCol.setPrefWidth(450);
        openTable.getColumns().add(descCol);

        Button submitButton = new Button("Submit Request");
        submitButton.setOnAction(e -> {
            String description = descriptionField.getText().trim();
            if (!description.isEmpty()) {
                theDatabase.createRequest(currentUser.getUserName(), description);
                descriptionField.clear();
                refreshTable(openTable);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a description before submitting.");
                alert.showAndWait();
            }
        });

        Label openLabel = new Label("Open Requests");
        openLabel.setStyle("-fx-font-size: 14pt; -fx-font-weight: bold;");

        root.getChildren().addAll(header, descriptionField, submitButton, openLabel, openTable);

        refreshTable(openTable);
        stage.setScene(new Scene(root, 500, 550));
        stage.show();
    }

    private void refreshTable(TableView<AdminRequest> table) {
        List<AdminRequest> openRequests = theDatabase.getRequestsByStatus("open");
        ObservableList<AdminRequest> data = FXCollections.observableArrayList(openRequests);
        table.setItems(data);
    }
}
