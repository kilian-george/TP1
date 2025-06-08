package questionAndAnswer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.ListCell;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class QuestionGuiStandalone {
	private QuestionList bank;
	private int currentIndex = 0;
	private boolean selection = false;
	private Consumer<Parent> setViewCallback;
	public void setOnViewSwitch(Consumer<Parent> callback) {
		this.setViewCallback = callback;
	}

	public Parent getView() {
		// Load data will have to be changed for group project
		bank = FileStorage.load();
		// Layout
		BorderPane layout = new BorderPane();
		layout.setPadding(new Insets(20));

		// make the list of questions into a single viewer
		ListView<Question> questionsList = new ListView<>();
		ObservableList<Question> questions = FXCollections.observableArrayList(bank.getQuestions());
		questionsList.setItems(questions);
		questionsList.setCellFactory(param -> new ListCell<Question>() {
			@Override
			protected void updateItem(Question item, boolean notHere) {
				super.updateItem(item, notHere);
				setText((notHere || item == null) ? null : item.getQuestionText());
			}
		});
		questionsList.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				Question selected = questionsList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					showDetailView(selected);
				}
			}
		});
		// new question button
		Button newQuestionButton = new Button("Ask New Question");
		newQuestionButton.setOnAction(e -> showNewQuestion());
		HBox resolvedButtonBox = createResloveListButton();
		HBox UnresolvedButtonBox = createUnresloveListButton();
		HBox topBar = new HBox(10, newQuestionButton, resolvedButtonBox, UnresolvedButtonBox);
		topBar.setPadding(new Insets(10));
		layout.setTop(topBar);
		layout.setCenter(questionsList);
		return layout;
	}

	private void showDetailView(Question question) {
		VBox detailBox = new VBox(10);
		detailBox.setPadding(new Insets(10));
		HBox topBox = new HBox();
		topBox.setAlignment(Pos.TOP_RIGHT);
		topBox.getChildren().add(createDeleteButton(question));
		List<Node> questionLabels = createQuestionData(question);
		VBox answersBox = createAnswersBox(question);
		HBox navigationButtons = createNavigationButtons(question);
		HBox showResolvedQuestions = createResloveListButton();
		HBox showUnresolvedQuestions = createUnresloveListButton();
		detailBox.getChildren().add(topBox);
		detailBox.getChildren().addAll(questionLabels);
		detailBox.getChildren().add(new Label("Answers:"));
		detailBox.getChildren().add(answersBox);
		detailBox.getChildren().add(navigationButtons);
		detailBox.getChildren().add(showResolvedQuestions);
		detailBox.getChildren().add(showUnresolvedQuestions);
		switchView(detailBox);
	}

	private Button createDeleteButton(Question question) {
		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> {
			Alert delAlert = new Alert(AlertType.CONFIRMATION, "are you sure?");
			delAlert.setHeaderText("confirm delete");
			delAlert.setTitle("Delete question");
			Optional<ButtonType> resAlert = delAlert.showAndWait();
			if (resAlert.isPresent() && resAlert.get() == ButtonType.OK) {
				bank.deleteQuestion(question);
				FileStorage.save(bank);
				switchView(getView());
			}

		});
		return deleteButton;
	}

	private List<Node> createQuestionData(Question question) {
		String questionTime = question.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		Label questionLabel = new Label("Question is: " + question.getQuestionText() + " (" + questionTime + ")");
		questionLabel.setWrapText(true);

		Label categoryLabel = new Label("Category is: " + question.getCategory());
		questionLabel.setWrapText(true);

		Label nameLabel = new Label("Name of questioner: " + question.getName());
		return List.of(questionLabel, nameLabel, categoryLabel);
	}

	private VBox createAnswersBox(Question question) {
		VBox answersBox = new VBox(5);
		if (question.getAnswers().isEmpty()) {
			answersBox.getChildren().add(new Label("nobody has answered yet"));
		}
		else {
			List<Answer> sortAnswers = new ArrayList<>(question.getAnswers());
			sortAnswers.sort((a1, a2) -> {
				if (a1.getResolved() != a2.getResolved()) {
					return Boolean.compare(!a1.getResolved(), !a2.getResolved());
				}
				return Integer.compare(a2.getScore(), a1.getScore());
			});
			
			//boolean hideResolve = question.getAnswers().stream().anyMatch(Answer::getResolved);
			
			for (Answer a : sortAnswers) {
				VBox commentBox = commentHelper(a, question);
				String answerTime = a.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
				Label answerLabel = new Label("*" + a.getName() + ":" + a.getAnswerText() + " (" + answerTime + ")");
				answerLabel.setWrapText(true);
				
				Label scoreLabel = new Label("score: " + a.getScore());
				
				Label resolvedLabel = new Label("\u2714");
				resolvedLabel.setStyle("-fx-text-fill: green; -fx-font-size:20px");
				resolvedLabel.setVisible(a.getResolved());
				
				Button upvoteButton = new Button("\u25B2");
				Button downvoteButton = new Button("\u25BC");
				upvoteButton.setOnAction(e -> {
					a.setScore(1);
					scoreLabel.setText("score: " + a.getScore());
					upvoteButton.setDisable(true);
					downvoteButton.setDisable(true);
					FileStorage.save(bank);
				});
				downvoteButton.setOnAction(e -> {
		            a.setScore(-1);
		            scoreLabel.setText("score: " + a.getScore());
		            upvoteButton.setDisable(true);
		            downvoteButton.setDisable(true);
		            FileStorage.save(bank);
		        });
				
				Button resolveButton = new Button("resolved question");
				resolveButton.setOnAction(e -> {
					question.setResolved(true);
					for (Answer other : question.getAnswers()) {
						other.setResolved(false);
					}
					a.setResolved(true);
					FileStorage.save(bank);
					showDetailView(question);
				});
				HBox answerHorz = new HBox(upvoteButton, downvoteButton, scoreLabel, resolvedLabel, 
						resolveButton);
				answersBox.getChildren().addAll(answerHorz, answerLabel, commentBox);

			}
		}
		return answersBox;
	}
	
	private HBox createResloveListButton() {
		Button listResolvedQuestionButton = new Button("click for resolved questions");
		listResolvedQuestionButton.setOnAction(e ->{
			List<Question> questionResList = new ArrayList<>();
			QuestionList bank = FileStorage.load();
			
			for(Question q:bank.getQuestions()) {
				if(q.isResolved()) {
					questionResList.add(q);
				}
			}
			VBox resolvedQuestionBox = new VBox(10);
			for(Question q :questionResList) {
				Label qLabel = new Label(q.getQuestionText());
				resolvedQuestionBox.getChildren().add(qLabel);
			}
			Stage resolvedStage = new Stage();
			Scene scene = new Scene(new ScrollPane(resolvedQuestionBox), 400, 300);
			resolvedStage.setTitle("ResolvedQuestions");
			resolvedStage.setScene(scene);
			resolvedStage.show();
		});
		return new HBox(listResolvedQuestionButton);
	}
	
	private HBox createUnresloveListButton() {
		Button listUnesolvedQuestionButton = new Button("click for unresolved questions");
		listUnesolvedQuestionButton.setOnAction(e ->{
			List<Question> questionResList = new ArrayList<>();
			QuestionList bank = FileStorage.load();
			
			for(Question q:bank.getQuestions()) {
				if(!q.isResolved()) {
					questionResList.add(q);
				}
			}
			VBox unresolvedQuestionBox = new VBox(10);
			for(Question q :questionResList) {
				Label qLabel = new Label(q.getQuestionText());
				unresolvedQuestionBox.getChildren().add(qLabel);
			}
			Stage resolvedStage = new Stage();
			Scene scene = new Scene(new ScrollPane(unresolvedQuestionBox), 400, 300);
			resolvedStage.setTitle("UnesolvedQuestions");
			resolvedStage.setScene(scene);
			resolvedStage.show();
		});
		return new HBox(listUnesolvedQuestionButton);
	}
	
	private HBox createNavigationButtons(Question question) {
		Button addAnswerButton = new Button("Add Answer");
		addAnswerButton.setOnAction(e -> showaddAnswer(question));

		Button backButton = new Button("Back");
		backButton.setOnAction(e -> switchView(getView()));
		HBox navBox = new HBox(10, addAnswerButton, backButton);
		return navBox;

	}

	private VBox commentHelper(Answer answer, Question question) {
		VBox commentBox = new VBox(5);
		for (Comment comment : answer.getComments()) {
			String time = comment.gettimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
			Label commentLabel = new Label("-" + comment.getText() + " (" + time + ") ");
			commentBox.getChildren().add(commentLabel);
		}
		TextField commentField = new TextField();
		commentField.setPromptText("enter comment");
		Button commentButton = new Button("post!");
		commentButton.setOnAction(e -> {
			String text = commentField.getText().trim();
			if (!text.isEmpty()) {
				answer.addComment(new Comment(text));
				FileStorage.save(bank);
				showDetailView(question);
			}
		});
		VBox commentArea = new VBox(5, commentBox, commentField, commentButton);
		commentArea.setPadding(new Insets(5, 0, 10, 20));
		return commentArea;
	}

	private void showaddAnswer(Question current) {
		Label nameLabel = new Label("name is:");
		Label questionLabel = new Label("question is:"+current.getQuestionText());
		VBox answersBox = new VBox(10);
		// setting up naming area
		nameLabel.setText("name:");
		TextField newName = new TextField();
		newName.setPromptText("Enter name");
		// setting up question area

		TextArea newAnswer = new TextArea();
		newAnswer.setPromptText("Answer here!");
		newAnswer.setPrefWidth(400);
		newAnswer.setPrefRowCount(5);

		// button functionality
		Button submitButton = new Button("Submit");
		submitButton.setOnAction(e -> {
			addAnswer(current, newName.getText(), newAnswer.getText());
			QuestionList updatebank = FileStorage.load();
			Question updated = updatebank.getQuestions().stream().filter(q -> q.getQuestionText().equals(current.getQuestionText()))
		.findFirst().orElse(current);
			showDetailView(updated);
		});
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e ->switchView(buildMainView()));
		VBox inputBox = new VBox(10, nameLabel, newName, questionLabel, newAnswer, submitButton, cancelButton);
		answersBox.getChildren().add(inputBox);
		switchView(answersBox);
	}

	private void addAnswer(Question current, String name, String answerText) {
		if (name != null && !name.isEmpty() && answerText != null && !answerText.isEmpty()) {
			Answer ans = new Answer(name, answerText, 0);
			current.addAnswer(ans);
			FileStorage.save(bank);
			bank= FileStorage.load();
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR, "must input name and answer");
			alert.showAndWait();
		}
	}

	private void showNewQuestion() {
		ComboBox<String> questionBox = questionComboBox(bank.getQuestions(),
				matchedQuestion -> showDetailView(matchedQuestion));

		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20));

		TextField nameField = new TextField();
		nameField.setPromptText("Your name");

		TextField categoryField = new TextField();
		categoryField.setPromptText("Category (e.g., Homework, Project)");

		Button submit = new Button("Submit");
		submit.setOnAction(e -> {
			String name = nameField.getText().trim();
			String category = categoryField.getText().trim();
			String questionText = questionBox.getEditor().getText();

			if (!name.isEmpty() && !category.isEmpty() && !questionText.isEmpty()) {
				Question q = new Question(name, category, questionText);
				bank.getQuestions().add(q);
				FileStorage.save(bank);
				switchView(getView()); // Return to main view
			} else {
				new Alert(Alert.AlertType.ERROR, "All fields must be filled.").showAndWait();
			}
		});

		Button cancel = new Button("Cancel");
		cancel.setOnAction(e -> switchView(getView()));

		layout.getChildren().addAll(new Label("Ask a New Question"), nameField, categoryField, questionBox, submit,
				cancel);

		switchView(layout); // Trigger the view update via the callback
	}

	private ComboBox<String> questionComboBox(List<Question> existingQuestions, Consumer<Question> onQuestionSelected) {
		ComboBox<String> questionBox = new ComboBox<>();
		questionBox.setEditable(true);
		questionBox.setPromptText("Type your question here");
		questionBox.setPrefWidth(400);

		List<String> existing = existingQuestions.stream().map(Question::getQuestionText).collect(Collectors.toList());

		// Live search as user types
		questionBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal == null || newVal.isBlank()) {
				questionBox.getItems().clear();
				return;
			}

			Set<String> inputWords = Arrays.stream(newVal.toLowerCase().split("\\W+")).filter(word -> !word.isBlank())
					.collect(Collectors.toSet());

			List<String> rankedMatches = existing.stream().map(q -> {
				Set<String> questionWords = Arrays.stream(q.toLowerCase().split("\\W+")).collect(Collectors.toSet());
				long overlap = inputWords.stream().filter(questionWords::contains).count();
				return Map.entry(q, overlap);
			}).filter(entry -> entry.getValue() > 0).sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
					.map(Map.Entry::getKey).limit(5).collect(Collectors.toList());

			Platform.runLater(() -> {
				questionBox.getItems().setAll(rankedMatches);
				questionBox.show();
			});
		});

		// Jump to question on selection
		questionBox.setOnAction(e -> {
			String selectedText = questionBox.getValue();
			if (selectedText != null && !selectedText.isBlank()) {
				Optional<Question> match = existingQuestions.stream()
						.filter(q -> q.getQuestionText().equals(selectedText)).findFirst();

				match.ifPresent(onQuestionSelected); // 💡 callback to show detail view
			}
		});

		return questionBox;
	}

	private Parent buildMainView() {
		BorderPane layout = new BorderPane();
		layout.setPadding(new Insets(20));

		ObservableList<Question> questions = FXCollections.observableArrayList(bank.getQuestions());
		ListView<Question> questionsList = new ListView<>(questions);

		questionsList.setCellFactory(param -> new ListCell<Question>() {
			@Override
			protected void updateItem(Question item, boolean notHere) {
				super.updateItem(item, notHere);
				setText((notHere || item == null) ? null : item.getQuestionText());
			}
		});
		questionsList.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				Question selected = questionsList.getSelectionModel().getSelectedItem();
				if (selected != null) {
					showDetailView(selected);
				}
			}
		});
		Button newQuestionButton = new Button("Ask New Question");
		newQuestionButton.setOnAction(e -> showNewQuestion());
		HBox resolvedButtonBox = createResloveListButton();
		HBox topBar = new HBox(10, newQuestionButton, resolvedButtonBox);
		topBar.setPadding(new Insets(10));

		layout.setTop(topBar);
		layout.setCenter(questionsList);

		return layout;
	}
	
	private void switchView(Parent newView) {
		if(setViewCallback !=null) {
			setViewCallback.accept(newView);
		}
		    
	}
}
