package guiPageClasses;

import databaseClasses.Database;
import entityClasses.User;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GUIReviewerRequestPopup {

    public static void display(Stage parentStage, Database theDatabase, User theUser) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Request Reviewer Role");

        Label label = new Label("Enter your message explaining why you'd like to become a reviewer:");
        TextArea messageArea = new TextArea();

        Button submitButton = new Button("Submit Request");
        Button cancelButton = new Button("Cancel");

        submitButton.setOnAction(e -> {
            String msg = messageArea.getText().trim();
            if (!msg.isEmpty()) {
                boolean success = theDatabase.submitReviewerRequest(theUser.getUserName(), msg);
                if (success) {
                    showAlert("Success", "Your request has been sent.");
                    popup.close();
                } else {
                    showAlert("Error", "Could not send request. Try again.");
                }
            } else {
                showAlert("Warning", "Please enter a message.");
            }
        });

        cancelButton.setOnAction(e -> popup.close());

        VBox layout = new VBox(10, label, messageArea, submitButton, cancelButton);
        layout.setPadding(new javafx.geometry.Insets(20));
        popup.setScene(new Scene(layout, 400, 300));
        popup.initOwner(parentStage);
        popup.showAndWait();
    }

    private static void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
