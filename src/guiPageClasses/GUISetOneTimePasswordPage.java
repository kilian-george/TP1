package guiPageClasses;

import java.sql.SQLException;

import applicationMainMethodClasses.FCMainClass;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import databaseClasses.Database;
import entityClasses.User;


public class GUISetOneTimePasswordPage {

    private Stage primaryStage;
    private Pane theRootPane;
    private Database theDatabase;
    private User theUser;
    
    private Label label_PageTitle = new Label("Set One-Time Password Page");
    private Label label_Username = new Label("Enter Username:");
    private TextField textField_Username = new TextField();
    private Button button_SetOTP = new Button("Set One-Time Password");
    private Button button_Back = new Button("Back");

    public GUISetOneTimePasswordPage(Stage ps, Pane theRoot, Database database, User user) {
        primaryStage = ps;
        theRootPane = theRoot;
        theDatabase = database;
        theUser = user;

        label_PageTitle.setFont(Font.font("Arial", 24));
        label_PageTitle.setLayoutX(20);
        label_PageTitle.setLayoutY(20);

        label_Username.setLayoutX(20);
        label_Username.setLayoutY(80);

        textField_Username.setLayoutX(150);
        textField_Username.setLayoutY(75);
        textField_Username.setPrefWidth(200);

        button_SetOTP.setLayoutX(20);
        button_SetOTP.setLayoutY(130);
        button_SetOTP.setOnAction(e -> setOTP());
        
        button_Back.setLayoutX(20);
        button_Back.setLayoutY(100);
        button_Back.setOnAction((event) -> { try {
			performBack();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} });

        setup();
    }

    public void setup() {
        theRootPane.getChildren().clear();
        theRootPane.getChildren().addAll(label_PageTitle, label_Username, textField_Username, button_SetOTP, button_Back);
    }
    
    private void setOTP() {
        String username = textField_Username.getText().trim();
        if (username.isEmpty()) {
            showAlert("Error", "Username cannot be empty.");
            return;
        }

        // This will give the user a random 6 digit code for the OTP
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);

        boolean success = theDatabase.setOneTimePassword(username, otp);
        if (success) {
        	//I set an alert if it sent but it never showed up before, so this success alert should work now
            showAlert("Success", "One-time password set for user: " + username + "\nOTP: " + otp);
            textField_Username.clear();
        } else {
            showAlert("Error", "Failed to set OTP. Username may not exist.");
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
