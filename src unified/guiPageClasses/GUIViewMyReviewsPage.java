package guiPageClasses;

import applicationMainMethodClasses.FCMainClass;
import databaseClasses.Database;
import entityClasses.User;
import entityClasses.Review;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

public class GUIViewMyReviewsPage {

    private Stage primaryStage;
    private Pane theRootPane;
    private Database theDatabase;
    private User theUser;

    private TableView<Review> reviewTable = new TableView<>();
    private Button button_Back = new Button("Back");

    public GUIViewMyReviewsPage(Stage ps, Pane root, Database db, User user) {
        primaryStage = ps;
        theRootPane = root;
        theDatabase = db;
        theUser = user;

        setup();
    }

    public void setup() {
        theRootPane.getChildren().clear();

        Label label = new Label("Your Reviews:");
        label.setLayoutX(20);
        label.setLayoutY(10);
        label.setStyle("-fx-font-size: 24px");

        setupTable();
        reviewTable.setLayoutX(20);
        reviewTable.setLayoutY(50);
        reviewTable.setPrefSize(560, 300);

        button_Back.setLayoutX(20);
        button_Back.setLayoutY(370);
        button_Back.setOnAction(e -> returnToReviewerHomePage());

        theRootPane.getChildren().addAll(label, reviewTable, button_Back);
    }

    private void setupTable() {
        TableColumn<Review, String> idCol = new TableColumn<>("Target ID");
        idCol.setCellValueFactory(c -> c.getValue().targetIdProperty());

        TableColumn<Review, String> contentCol = new TableColumn<>("Review");
        contentCol.setCellValueFactory(c -> c.getValue().contentProperty());

        TableColumn<Review, Void> updateCol = new TableColumn<>("Update");
        updateCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Edit");

            {
                btn.setOnAction(e -> {
                  //This wasn't needed but I could use it later when we incorporate more coding into the reviewer role functions
                  // Review r = getTableView().getItems().get(getIndex());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        TableColumn<Review, Void> deleteCol = new TableColumn<>("Delete");
        deleteCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Delete");

            {
                btn.setOnAction(e -> {
                    Review r = getTableView().getItems().get(getIndex());
                    theDatabase.deleteReview(r.getReviewID());
                    setup(); // Refresh
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        reviewTable.getColumns().setAll(idCol, contentCol, updateCol, deleteCol);

        List<Review> userReviews = theDatabase.getReviewsByUser(theUser.getUserName());
        ObservableList<Review> data = FXCollections.observableArrayList(userReviews);
        reviewTable.setItems(data);
    }

    private void returnToReviewerHomePage() {
        if (GUISystemStartUpPage.theReviewerHomePage != null)
            GUISystemStartUpPage.theReviewerHomePage.setup();
    }
}
