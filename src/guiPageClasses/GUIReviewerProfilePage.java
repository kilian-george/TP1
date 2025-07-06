package guiPageClasses;

import databaseClasses.Database;
import entityClasses.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUIReviewerProfilePage {

    private Stage primaryStage;
    private Pane theRootPane;
    private Database database;
    private User currentUser;
    private User profileUser; // This will be for the reviewer whose profile is being viewed

    private Label labelTitle = new Label("Reviewer Profile");
    private Label labelUsername = new Label();
    private TextArea experienceArea = new TextArea();
    private Button saveExperienceButton = new Button("Save Experience");
    private Button button_BackToHome = new Button("Back to Home");
    
    public GUIReviewerProfilePage(Stage primaryStage, Pane theRootPane ,Database database, User currentUser, User profileUser) {
        this.primaryStage = primaryStage;
        this.theRootPane = theRootPane;
        this.database = database;
        this.currentUser = currentUser;
        this.profileUser = profileUser;

        setupUI();
    }

    private void setupUI() {
        primaryStage.setTitle("Reviewer Profile: " + profileUser.getUserName());

        labelTitle.setFont(Font.font("Arial", 28));
        labelUsername.setFont(Font.font("Arial", 18));
        labelUsername.setText("Username: " + profileUser.getUserName());

        experienceArea.setWrapText(true);
        experienceArea.setPrefWidth(600);
        experienceArea.setPrefHeight(150);
        experienceArea.setFont(Font.font("Arial", 14));

        String experience = database.getReviewerExperience(profileUser.getUserName());
        experienceArea.setText(experience != null ? experience : "");

        VBox experienceBox = new VBox(10);
        experienceBox.getChildren().addAll(new Label("Experience:"), experienceArea);

        if (currentUser.getUserName().equals(profileUser.getUserName())) {
            experienceArea.setEditable(true);
            saveExperienceButton.setFont(Font.font("Dialog", 14));
            saveExperienceButton.setOnAction(e -> {
                String updated = experienceArea.getText().trim();
                database.updateReviewerExperience(currentUser.getUserName(), updated);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Experience updated successfully.");
                alert.showAndWait();
            });
            experienceBox.getChildren().add(saveExperienceButton);
        } else {
            experienceArea.setEditable(false);
            saveExperienceButton.setVisible(false);
        }
        
        button_BackToHome.setFont(Font.font("Dialog", 14));
        button_BackToHome.setLayoutX(20);
        button_BackToHome.setLayoutY(271);
        button_BackToHome.setOnAction(event -> {
            try {
                performBack();
            } catch (Exception e) {
                e.printStackTrace(); 
            }
        });

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.getChildren().addAll(labelTitle, labelUsername, new Separator(), experienceBox, button_BackToHome);

        theRootPane.getChildren().setAll(layout);
    }
    private void performBack() {
        GUIReviewerHomePage homePage = new GUIReviewerHomePage(primaryStage, theRootPane, database, currentUser);
        homePage.setup();
    }
}
