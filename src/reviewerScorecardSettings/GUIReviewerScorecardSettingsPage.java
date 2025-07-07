package reviewerScorecardSettings;

import databaseClasses.Database;
import entityClasses.User;
import reviewerScorecardSettings.ReviewerScorecardSettings;
import reviewerScorecardSettings.ScorecardParameters;
import guiPageClasses.GUISystemStartUpPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUIReviewerScorecardSettingsPage {
    
    private Stage primaryStage;
    private Pane theRootPane; 
    private Database database;
    private User instructorUser;
    
    private TextField avgRatingWeightField = new TextField();
    private TextField reviewCountWeightField = new TextField();
    private TextField responseTimeWeightField = new TextField();
    private TextField feedbackQualityWeightField = new TextField();

    public GUIReviewerScorecardSettingsPage(Stage primaryStage, Pane theRootPane, Database database, User instructorUser) {
        this.primaryStage = primaryStage;
        this.theRootPane = theRootPane;
        this.database = database;
        this.instructorUser = instructorUser;

        setupUI();
        loadParameters();
    }

    private void setupUI() {
        Label title = new Label("Reviewer Scorecard Settings");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        avgRatingWeightField.setPromptText("Weight: Average Rating");
        reviewCountWeightField.setPromptText("Weight: Review Count");
        responseTimeWeightField.setPromptText("Weight: Response Time");
        feedbackQualityWeightField.setPromptText("Weight: Feedback Quality");

        Button saveButton = new Button("Save Settings");
        saveButton.setOnAction(e -> {
            System.out.println("Saved parameters (hook this into DB)");
        });

        Button backButton = new Button("Back to Home");
        backButton.setOnAction(e -> {
        	  GUISystemStartUpPage.theInstructorHomePage.setup();
        });

        VBox layout = new VBox(12, title, 
        		avgRatingWeightField, 
        		reviewCountWeightField, 
        		responseTimeWeightField, 
        		feedbackQualityWeightField, 
        		saveButton, 
        		backButton
        );
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        theRootPane.getChildren().clear();
        theRootPane.getChildren().add(layout);
    }
    
    
    private void loadParameters() {
        try {
            ScorecardParameters params = database.getScorecardParameters();
            if (params != null) {
                avgRatingWeightField.setText(String.valueOf(params.getAverageRatingWeight()));
                reviewCountWeightField.setText(String.valueOf(params.getReviewCountWeight()));
                responseTimeWeightField.setText(String.valueOf(params.getResponseTimeWeight()));
                feedbackQualityWeightField.setText(String.valueOf(params.getFeedbackQualityWeight()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading scorecard parameters.");
        }
    }
    
    
    private void saveParameters() {
        try {
            double avg = Double.parseDouble(avgRatingWeightField.getText());
            double count = Double.parseDouble(reviewCountWeightField.getText());
            double time = Double.parseDouble(responseTimeWeightField.getText());
            double quality = Double.parseDouble(feedbackQualityWeightField.getText());

            ScorecardParameters params = new ScorecardParameters(avg, count, time, quality);
            boolean success = database.updateScorecardParameters(params);
            if (success) {
                showAlert("Scorecard parameters saved successfully.");
            } else {
                showAlert("Failed to save parameters.");
            }
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numeric values for all fields.");
        }
    }
    
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }
}

  
