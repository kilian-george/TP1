package guiPageClasses;

import questionAndAnswer.Answer;
import questionAndAnswer.Comment;
import questionAndAnswer.Question;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databaseClasses.Database;

import java.util.List;


public class StaffReviewGui {
    private Database database;

    public StaffReviewGui(Database database) {
        this.database = database;
    }

    public void reviewQuestion(Question question) {
        System.out.println("Reviewing Question ID: " + question.getId());
        Stage stage = new Stage();
        VBox layout = new VBox(10);

        Label questionLabel = new Label("Question: " + question.getQuestionText());
        List<Answer> answers = question.getAnswers();
        for (Answer answer : answers) {
            Label answerLabel = new Label("Answer: " + answer.getAnswerText());
            layout.getChildren().add(answerLabel);
        }

        // Comments (feedback)
        for (Answer answer : answers) {
            for (Comment comment : answer.getComments()) {
                Label commentLabel = new Label("Comment: " + comment.getText());
                layout.getChildren().add(commentLabel);
            }
        }

        Button forwardButton = new Button("Forward to Instructor");
        forwardButton.setOnAction(e -> forwardQuestionWithNote(question));

        Button alertButton = new Button("Set Alert on this Thread");
        alertButton.setOnAction(e -> setAlert(question));

        layout.getChildren().addAll(forwardButton, alertButton);

        stage.setScene(new Scene(layout, 500, 400));
        stage.setTitle("Review Question");
        stage.show();
    }
    public void reviewAnswer(Answer answer, int questionId) {
    	Question q = database.getQuestionById(questionId);
        reviewQuestion(q);
    }

    public void reviewComment(Comment comment, int answerId) {
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        Label commentLabel = new Label("Comment: " + comment.getText());

        Button forwardButton = new Button("Forward to Instructor");
        forwardButton.setOnAction(e -> forwardComment(comment));

        Button alertButton = new Button("Set Alert on Comment");
        alertButton.setOnAction(e -> setAlert(comment));

        layout.getChildren().addAll(commentLabel, forwardButton, alertButton);
        stage.setScene(new Scene(layout, 400, 200));
        stage.setTitle("Review Comment");
        stage.show();
    }
    
    public void messageStaff() {
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        TextField recipient = new TextField();
        recipient.setPromptText("Recipient username");
        TextArea message = new TextArea();
        message.setPromptText("Enter message here...");

        Button send = new Button("Send Message");
        send.setOnAction(e -> {
            String user = recipient.getText().trim();
            String msg = message.getText().trim();
            if (!user.isEmpty() && !msg.isEmpty()) {
            	database.sendPrivateMessage(user, msg);
                stage.close();
            }
        });

        layout.getChildren().addAll(new Label("Send Message"), recipient, message, send);
        stage.setScene(new Scene(layout, 400, 300));
        stage.setTitle("Message Staff/Instructor");
        stage.show();
    }
    
    private void forwardQuestionWithNote(Question question) {
        // popup for instructor + optional notes
        // save to 'forwarded' table maybe
    	//QuestionGui -> 
    	//Button send = new Button("Forward to Instructor");
    	//send.setOnAction(e -> {
    		   		
    	//}
    }
    private void forwardComment(Comment comment) {
        // same concept as forwardQuestionWithNote
    }
    private void setAlert(Object item) {
        // mark item as monitored in DB
        // could be Question, Answer, or Comment
    }
}
