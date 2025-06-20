package guiPageClasses;

import applicationMainMethodClasses.FCMainClass;
import databaseClasses.Database;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * GUICreateReviewPage - Reviewer writes a review for a question or answer.
 */
public class GUICreateReviewPage {

    private Stage primaryStage;
    private Pane theRootPane;
    private Database theDatabase;
    private User theUser;

    private Label label_Title = new Label("Create a Review");
    private Label label_TargetId = new Label("Target Question/Answer ID:");
    private TextField text_TargetId = new TextField();

    private Label label_Content = new Label("Review Content:");
    private TextArea textArea_Content = new TextArea();

    private Button button_Submit = new Button("Submit Review");
    private Button button_Back = new Button("Back");

    public GUICreateReviewPage(Stage ps, Pane root, Database db, User user) {
        primaryStage = ps;
        theRootPane = root;
        theDatabase = db;
        theUser = user;

        setup();
    }

    public void setup() {
        theRootPane.getChildren().clear();

        double W = FCMainClass.WINDOW_WIDTH;

        label_Title.setFont(Font.font("Arial", 26));
        label_Title.setLayoutX(W / 2 - 100);

        label_Title.setLayoutY(10);

        label_TargetId.setLayoutX(20);
        label_TargetId.setLayoutY(60);
        text_TargetId.setLayoutX(250);
        text_TargetId.setLayoutY(60);
        text_TargetId.setPrefWidth(300);

        label_Content.setLayoutX(20);
        label_Content.setLayoutY(100);
        textArea_Content.setLayoutX(20);
        textArea_Content.setLayoutY(130);
        textArea_Content.setPrefSize(550, 200);

        button_Submit.setLayoutX(20);
        button_Submit.setLayoutY(350);
        button_Submit.setOnAction(e -> handleSubmit());

        button_Back.setLayoutX(180);
        button_Back.setLayoutY(350);
        button_Back.setOnAction(e -> returnToReviewerHomePage());

        theRootPane.getChildren().addAll(
            label_Title, label_TargetId, text_TargetId,
            label_Content, textArea_Content,
            button_Submit, button_Back
        );
    }

    private void handleSubmit() {
        String targetId = text_TargetId.getText().trim();
        String content = textArea_Content.getText().trim();

        if (targetId.isEmpty() || content.isEmpty()) {
            showAlert("Error", "All fields must be filled in.");
            return;
        }

        boolean success = theDatabase.addReview(theUser.getUserName(), targetId, content);
        if (success) {
            showAlert("Success", "Review submitted.");
            returnToReviewerHomePage();
          //These lines were used for testing
//            text_TargetId.clear();
//            textArea_Content.clear();
        } else {
            showAlert("Error", "Failed to submit review.");
        }
    }

    private void returnToReviewerHomePage() {
        if (GUISystemStartUpPage.theReviewerHomePage != null)
            GUISystemStartUpPage.theReviewerHomePage.setup();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
