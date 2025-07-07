package guiPageClasses;

import databaseClasses.Database;
import entityClasses.AdminRequest;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class GUIAdminResponsePage {
    private Stage stage;
    private Database theDatabase;
    private User currentUser;

    public GUIAdminResponsePage(Stage stage, Database db, User user) {
        this.stage = stage;
        this.theDatabase = db;
        this.currentUser = user;
        setup();
    }

    public void setup() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label header = new Label("Respond to Admin Requests");
        TableView<AdminRequest> requestTable = new TableView<>();

        TableColumn<AdminRequest, String> userCol = new TableColumn<>("From");
        userCol.setCellValueFactory(data -> data.getValue().createdByProperty());

        TableColumn<AdminRequest, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> data.getValue().descriptionProperty());

        requestTable.getColumns().addAll(userCol, descCol);

        TextArea responseArea = new TextArea();
        responseArea.setPromptText("Enter your response or action taken...");

        Button closeRequestButton = new Button("Close Request");
        closeRequestButton.setOnAction(e -> {
            AdminRequest selected = requestTable.getSelectionModel().getSelectedItem();
            if (selected != null && !responseArea.getText().trim().isEmpty()) {
                theDatabase.respondToRequest(selected.getRequestId(), responseArea.getText().trim());
                responseArea.clear();
                refreshTable(requestTable);
            }
        });

        root.getChildren().addAll(header, requestTable, responseArea, closeRequestButton);
        refreshTable(requestTable);

        stage.setScene(new Scene(root, 600, 500));
        stage.show();
    }

    private void refreshTable(TableView<AdminRequest> table) {
        List<AdminRequest> openRequests = theDatabase.getRequestsByStatus("open");
        ObservableList<AdminRequest> data = FXCollections.observableArrayList(openRequests);
        table.setItems(data);
    }
}
