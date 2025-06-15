package guiPageClasses;

import applicationMainMethodClasses.FCMainClass;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import databaseClasses.Database;
import entityClasses.User;

import java.util.List;

public class GUIListUsersPage {

    private Stage primaryStage;
    private Pane theRootPane;
    private Database theDatabase;
    private User theUser;

    private Label label_PageTitle = new Label("List All Users");
    private ListView<String> listView_Users = new ListView<>();
    private Button button_Back = new Button("Back");

    public GUIListUsersPage(Stage ps, Pane theRoot, Database database, User user) {
        primaryStage = ps;
        theRootPane = theRoot;
        theDatabase = database;
        theUser = user;

        label_PageTitle.setFont(Font.font("Arial", 24));
        label_PageTitle.setLayoutX(20);
        label_PageTitle.setLayoutY(20);

        listView_Users.setLayoutX(20);
        listView_Users.setLayoutY(70);
        listView_Users.setPrefSize(500, 300);

        List<String> usersFromDatabase = theDatabase.getUserList();
        if (usersFromDatabase != null) {
            listView_Users.getItems().addAll(usersFromDatabase);
        } else {
            listView_Users.getItems().add("Error retrieving user list from database.");
        }

        button_Back.setLayoutX(20);
        button_Back.setLayoutY(400);
        button_Back.setOnAction(e -> performBack());

        setup();
    }

    public void setup() {
        theRootPane.getChildren().clear();
        refreshUserList();
        theRootPane.getChildren().addAll(label_PageTitle, listView_Users, button_Back);
    }
    
    //Adding a refresh list method that will refresh the User list in case a user is deleted
    private void refreshUserList() {
        listView_Users.getItems().clear();
        List<String> usersFromDatabase = theDatabase.getUserList();
        if (usersFromDatabase != null) {
            listView_Users.getItems().addAll(usersFromDatabase);
        } else {
            listView_Users.getItems().add("Error retrieving user list from database.");
        }
    }
    private void performBack() {
        GUISystemStartUpPage.theAdminHomePage.setup();
    }
}
