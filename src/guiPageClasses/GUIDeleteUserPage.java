package guiPageClasses;

import java.sql.SQLException;

import applicationMainMethodClasses.FCMainClass;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import databaseClasses.Database;
import entityClasses.User;

public class GUIDeleteUserPage {

    private Stage primaryStage;
    private Pane theRootPane;
    private Database theDatabase;
    private User theUser;

    private Label label_PageTitle = new Label("Delete User");
    private Label label_Username = new Label("Enter Username to Delete:");
    private TextField textField_Username = new TextField();
    private Button button_Delete = new Button("Delete User");
    private Button button_Back = new Button("Back");

    public GUIDeleteUserPage(Stage ps, Pane theRoot, Database database, User user) {
        primaryStage = ps;
        theRootPane = theRoot;
        theDatabase = database;
        theUser = user;

        label_PageTitle.setFont(Font.font("Arial", 24));
        label_PageTitle.setLayoutX(20);
        label_PageTitle.setLayoutY(20);

        label_Username.setLayoutX(20);
        label_Username.setLayoutY(80);

        textField_Username.setLayoutX(220);
        textField_Username.setLayoutY(75);
        textField_Username.setPrefWidth(200);

        button_Delete.setLayoutX(20);
        button_Delete.setLayoutY(130);
        button_Delete.setOnAction(e -> deleteUser());

        button_Back.setLayoutX(20);
        button_Back.setLayoutY(180);
        button_Back.setOnAction(e -> {
			try {
				performBack();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

        setup();
    }

    public void setup() {
        theRootPane.getChildren().clear();
        
        theRootPane.getChildren().addAll(label_PageTitle, label_Username, textField_Username, button_Delete, button_Back);
    }


    private void deleteUser() {
        String username = textField_Username.getText().trim();
        if (username.isEmpty()) {
            showAlert("Error", "Username cannot be empty.");
            return;
        }

        // Sends an alert to let us know if a user was successfully deleted
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete user: " + username + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            // Deletes user from the database
            boolean success = theDatabase.deleteUserByUsername(username); // Needed to make sure that this existed

            if (success) {
                showAlert("Success", "User '" + username + "' has been deleted.");
                textField_Username.clear();
                
                //This should keep the user list up-to-date
            } else {
                showAlert("Error", "User '" + username + "' could not be deleted.");
            }
        }
    }
    
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    private void performBack() throws SQLException {
        GUISystemStartUpPage.theAdminHomePage.setup();
    }
}
