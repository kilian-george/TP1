package questionAndAnswer;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class QuestionGuiTest extends Application {

    @Override
    public void start(Stage stage) {
        QuestionGui gui = new QuestionGui(); 
        
        
        StackPane root = new StackPane();
        
        Scene scene = new Scene(root, 600, 400);
        
        gui.setOnViewSwitch(newview ->root.getChildren().setAll(newview));
        Parent initalView = gui.getView();
        root.getChildren().add(initalView);
        stage.setTitle("Test: Question Viewer");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);  
    }
}