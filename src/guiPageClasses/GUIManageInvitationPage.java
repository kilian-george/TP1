package guiPageClasses;

import applicationMainMethodClasses.FCMainClass;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import databaseClasses.Database;
import entityClasses.User;

public class GUIManageInvitationPage {

    private Stage primaryStage;
    private Pane theRootPane;
    private Database theDatabase;
    private User theUser;
    private Label label_PageTitle = new Label("Manage Invitations Page");
    private ListView<String> listView_Invitations = new ListView<>();
    private Button button_Back = new Button("Back");

    public GUIManageInvitationPage(Stage ps, Pane theRoot, Database database, User user) {
        primaryStage = ps;
        theRootPane = theRoot;
        theDatabase = database;
        theUser = user;

        label_PageTitle.setFont(Font.font("Arial", 24));
        label_PageTitle.setLayoutX(20);
        label_PageTitle.setLayoutY(20);


        listView_Invitations.setLayoutX(20);
        listView_Invitations.setLayoutY(70);
        listView_Invitations.setPrefSize(400, 200);
        
        listView_Invitations.getItems().addAll(theDatabase.getInvitationList());

        
        button_Back.setLayoutX(20);
        button_Back.setLayoutY(271);
        button_Back.setOnAction((event) -> { performBack(); });

        setup();
    }

    public void setup() {
        theRootPane.getChildren().clear();
        theRootPane.getChildren().addAll(label_PageTitle, listView_Invitations, button_Back);
    }

    private void performBack() {
        GUISystemStartUpPage.theAdminHomePage.setup();
    }
}