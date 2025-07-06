package questionAndAnswer;

import java.util.function.Consumer;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class QuestionGuiTest extends Application {
private static QuestionList sharedQuestions;
private static Consumer<Parent> sharedSwitch;
public static void setData(QuestionList list, Consumer<Parent> switchView) {
	sharedQuestions = list;
	sharedSwitch = switchView;
}
    @Override
    public void start(Stage stage) {        
        
        StackPane root = new StackPane();
        Consumer<Parent> switchView = newView ->root.getChildren().setAll(newView);
        if(sharedQuestions == null) {
        	sharedQuestions = new QuestionList();
        }
        if(sharedSwitch == null) {
        	sharedSwitch = switchView;
        }
        QuestionGuiStandalone gui = new QuestionGuiStandalone(sharedQuestions, sharedSwitch);
        Parent view = gui.getView();
        root.getChildren().add(view);
        Scene scene = new Scene(root, 700, 700);
        stage.setTitle("question qui testing");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);  
    }
}
