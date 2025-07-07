package guiPageClasses;

import databaseClasses.Database;
import entityClasses.AdminRequest;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class GUIClosedRequestsPage {

    private Stage primaryStage;
    private Database database;
    private User currentUser;

    public GUIClosedRequestsPage(Stage stage, Database db, User user) {
        this.primaryStage = stage;
        this.database = db;
        this.currentUser = user;
        setup();
    }

    private void setup() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        Label label = new Label("Closed Admin Requests:");
        TableView<AdminRequest> table = new TableView<>();

        TableColumn<AdminRequest, Number> idCol = new TableColumn<>("Request ID");
        idCol.setCellValueFactory(data -> data.getValue().requestIdProperty());

        TableColumn<AdminRequest, String> userCol = new TableColumn<>("Requested By");
        userCol.setCellValueFactory(data -> data.getValue().requestedByUsernameProperty());

        TableColumn<AdminRequest, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> data.getValue().descriptionProperty());

        TableColumn<AdminRequest, String> respCol = new TableColumn<>("Admin Response");
        respCol.setCellValueFactory(data -> data.getValue().adminResponseProperty());

        TableColumn<AdminRequest, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());

        table.getColumns().addAll(idCol, userCol, descCol, respCol, statusCol);

        // Conditionally add Reopen button for instructors only
        if (currentUser.getInstructorRole()) {
            TableColumn<AdminRequest, Void> reopenCol = new TableColumn<>("Reopen");
            reopenCol.setCellFactory(col -> new TableCell<>() {
                private final Button reopenButton = new Button("Reopen");

                {
                    reopenButton.setOnAction(e -> {
                        AdminRequest request = getTableView().getItems().get(getIndex());
                        showReopenDialog(request);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : reopenButton);
                }
            });
            table.getColumns().add(reopenCol);
        }

        List<AdminRequest> closedRequests = database.getAllClosedRequests();
        ObservableList<AdminRequest> data = FXCollections.observableArrayList(closedRequests);
        table.setItems(data);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> returnToHome());

        layout.getChildren().addAll(label, table, backButton);
        primaryStage.setScene(new Scene(layout, 800, 500));
        primaryStage.show();
    }

    private void showReopenDialog(AdminRequest originalRequest) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reopen Request");
        dialog.setHeaderText("Enter updated description for the reopened request:");
        dialog.setContentText("Description:");

        dialog.showAndWait().ifPresent(updatedDescription -> {
            database.reopenRequest(originalRequest.getRequestId(), currentUser.getUserName(), updatedDescription);
            setup();
        });
    }

    private void returnToHome() {
        if (currentUser.getInstructorRole()) {
            new GUIInstructorHomePage(primaryStage, new VBox(), database, currentUser);
        } else if (currentUser.getAdminRole()) {
            new GUIAdminHomePage(primaryStage, new VBox(), database, currentUser);
        } else if (currentUser.getStaffRole()) {
            new GUIStaffHomePage(primaryStage, new VBox(), database, currentUser);
        }
    }
}
