package questionAndAnswer;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class QuestionGuiStandalone {
	private QuestionList bank;
	private ObservableList<Question> questionsObservable;
	private Consumer<Parent> setViewCallback;
	private ListView<Question> questionsList;

	/**********
	 * <p>
	 * Method: setOnViewSwitch
	 * </p>
	 * 
	 * <p>
	 * Description: Sets the view-switching callback used by this class to update
	 * the main content area. This allows other parts of the application to replace
	 * the center of the window.
	 * </p>
	 * 
	 * @param callback A Consumer functional interface that accepts a Parent node to
	 *                 display
	 */
	public void setOnViewSwitch(Consumer<Parent> callback) {
		this.setViewCallback = callback;
	}
	/**********
	 * <p>
	 * Method: QuestionGuiStandalone()
	 * </p>
	 * 
	 * <p>
	 * Description: This creates the initial view for the question and answer
	 * sections. It creates the buttons that then activate the helper methods. it
	 * also displays a list of questions by order of time
	 * </p>
	 * 
	 */
	public QuestionGuiStandalone(QuestionList bank, Consumer<Parent> switchView) {
		this.bank = bank;
		this.setViewCallback = switchView;
	}

	public Parent getView() {
		// Layout
		BorderPane layout = new BorderPane();
		layout.setPadding(new Insets(20));

		// make the list of questions into a single viewer
		questionsList = new ListView<>();
		questionsObservable = FXCollections.observableArrayList(bank.getQuestions());
		questionsList.setItems(questionsObservable);
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
	/**********
	 * <p>
	 * Method: showDetailView
	 * </p>
	 * 
	 * <p>
	 * Description: Displays a detailed view of a specific question, including the
	 * question text, metadata, associated answers, and navigation controls. Allows
	 * deletion of the question and re-displays the main list.
	 * </p>
	 * 
	 * @param question The Question object to display in detail
	 */
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
	/**********
	 * <p>
	 * Method: createDeleteButton
	 * </p>
	 * 
	 * <p>
	 * Description: Generates a delete button for the input question. The button is
	 * only visible if the current user is the original author or an admin. When
	 * clicked, the method prompts the user to confirm, then deletes the question
	 * from the database and refreshes the view.
	 * </p>
	 * 
	 * @param question The Question object to potentially delete
	 * @return A Button object with delete logic
	 */
	private Button createDeleteButton(Question question) {
		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> {
			Alert delAlert = new Alert(AlertType.CONFIRMATION, "are you sure?");
			delAlert.setHeaderText("confirm delete");
			delAlert.setTitle("Delete question");
			Optional<ButtonType> resAlert = delAlert.showAndWait();
			if (resAlert.isPresent() && resAlert.get() == ButtonType.OK) {
				bank.deleteQuestion(question);
				switchView(getView());
			}

		});
		return deleteButton;
	}
	/**********
	 * <p>
	 * Method: createQuestionData
	 * </p>
	 * 
	 * <p>
	 * Description: Generates a list of labeled UI nodes containing information
	 * about a question. This includes the question text with timestamp, category,
	 * and the name of the current user.
	 * </p>
	 * 
	 * @param question The question to display data from
	 * @return A list of UI nodes representing the question's metadata
	 */
	private List<Node> createQuestionData(Question question) {
		String questionTime = question.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		Label questionLabel = new Label("Question is: " + question.getQuestionText() + " (" + questionTime + ")");
		questionLabel.setWrapText(true);

		Label categoryLabel = new Label("Category is: " + question.getCategory());
		questionLabel.setWrapText(true);

		Label nameLabel = new Label("Name of questioner: " + question.getName());
		return List.of(questionLabel, nameLabel, categoryLabel);
	}
	/**********
	 * <p>
	 * Method: createAnswersBox
	 * </p>
	 * 
	 * <p>
	 * Description: Constructs a VBox containing all answers to a question. Answers
	 * are sorted for score. Each answer has voting, resovled checkmark, and an
	 * optional resolve action button. Comment sections per answer.
	 * </p>
	 * 
	 * @param question The question for which answers should be displayed
	 * @return A VBox containing all rendered answer elements and their controls
	 */
	private VBox createAnswersBox(Question question) {
		VBox answersBox = new VBox(5);
		if (question.getAnswers().isEmpty()) {
			answersBox.getChildren().add(new Label("nobody has answered yet"));
		} else {
			List<Answer> sortAnswers = new ArrayList<>(question.getAnswers());
			sortAnswers.sort((a1, a2) -> {
				if (a1.getResolved() != a2.getResolved()) {
					return Boolean.compare(!a1.getResolved(), !a2.getResolved());
				}
				return Integer.compare(a2.getScore(), a1.getScore());
			});

			// boolean hideResolve =
			// question.getAnswers().stream().anyMatch(Answer::getResolved);

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

				});
				downvoteButton.setOnAction(e -> {
					a.setScore(-1);
					scoreLabel.setText("score: " + a.getScore());
					upvoteButton.setDisable(true);
					downvoteButton.setDisable(true);

				});

				Button resolveButton = new Button("resolved question");
				if (question.isResolved()) {
					resolveButton.setDisable(true);
				}
				resolveButton.setOnAction(e -> {
					question.setResolved(true);
					for (Answer other : question.getAnswers()) {
						other.setResolved(false);
					}
					a.setResolved(true);
					showDetailView(question);
				});
				HBox answerHorz = new HBox(upvoteButton, downvoteButton, scoreLabel, resolvedLabel, resolveButton);
				answersBox.getChildren().addAll(answerHorz, answerLabel, commentBox);

			}
		}
		return answersBox;
	}
	/**********
	 * <p>
	 * Method: createResloveListButton
	 * </p>
	 * 
	 * <p>
	 * Description: Creates a button opens a popup window displaying only the
	 * questions submitted by the current user that have been marked as resolved.
	 * </p>
	 * 
	 * @return A horizontal box containing the resolved questions button
	 */
	private HBox createResloveListButton() {
		Button listResolvedQuestionButton = new Button("click for resolved questions");

		listResolvedQuestionButton.setOnAction(e -> {
			List<Question> questionResList = bank.getQuestions().stream().filter(Question::isResolved)
					.collect(Collectors.toList());

			VBox resolvedQuestionBox = new VBox(10);

			for (Question q : questionResList) {
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
	/**********
	 * <p>
	 * Method: createUnresloveListButton
	 * </p>
	 * 
	 * <p>
	 * Description: Creates a button that opens a popup displaying unresolved
	 * questions submitted by the current user.
	 * </p>
	 * 
	 * @return A horizontal box containing the unresolved questions button
	 */
	private HBox createUnresloveListButton() {
		Button listUnesolvedQuestionButton = new Button("click for unresolved questions");
		listUnesolvedQuestionButton.setOnAction(e -> {
			List<Question> questionResList = bank.getUnresolvedQuestions().stream().filter(q -> !q.isResolved())
					.collect(Collectors.toList());

			VBox unresolvedQuestionBox = new VBox(10);
			for (Question q : questionResList) {
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
	/**********
	 * <p>
	 * Method: createNavigationButtons
	 * </p>
	 * 
	 * <p>
	 * Description: Creates the navigation buttons for the detailed view of a
	 * question. Includes Add Answer button that opens an answer form, back button
	 * that returns to the main question list view.
	 * </p>
	 * 
	 * @param question The current question being viewed
	 * @return An HBox containing the Add Answer and Back buttons
	 */
	private HBox createNavigationButtons(Question question) {
		Button addAnswerButton = new Button("Add Answer");
		addAnswerButton.setOnAction(e -> showaddAnswer(question));

		Button backButton = new Button("Back");
		backButton.setOnAction(e -> switchView(getView()));
		HBox navBox = new HBox(10, addAnswerButton, backButton);
		return navBox;

	}
	/**********
	 * <p>
	 * Method: commentHelper
	 * </p>
	 * 
	 * <p>
	 * Description: Constructs a comment section UI for a given answer. Displays
	 * existing comments with timestamps and provides text field, button for user to
	 * add a new comment, which is then saved and the view is reset.
	 * </p>
	 * 
	 * @param answer   The answer to comment
	 * @param question The question containing the answer
	 * @return A VBox representing the full comment interface for the given answer
	 */
	private VBox commentHelper(Answer answer, Question question) {
		VBox commentBox = new VBox(5);
		for (Comment comment : answer.getComments()) {
			String time = comment.gettimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
			Label commentLabel = new Label("-By"+comment.getName() +" "+ comment.getText() + " (" + time + ") ");
			commentBox.getChildren().add(commentLabel);
		}
		TextField commentField = new TextField();
		commentField.setPromptText("enter comment");
		Button commentButton = new Button("post!");
		commentButton.setOnAction(e -> {
			String text = commentField.getText().trim();
			if (!text.isEmpty()) {
				answer.addComment(new Comment(answer.getName(), text));
				showDetailView(question);
			}
		});
		VBox commentArea = new VBox(5, commentBox, commentField, commentButton);
		commentArea.setPadding(new Insets(5, 0, 10, 20));
		return commentArea;
	}
	/**********
	 * <p>
	 * Method: showaddAnswer
	 * </p>
	 * 
	 * <p>
	 * Description: Displays new answer to a given question. Includes the question
	 * text, input area for the answer, and submit/cancel buttons.After submission,
	 * the question view is refreshed to show the newly added answer.
	 * </p>
	 * 
	 * @param current The question to which the new answer will be added
	 */
	private void showaddAnswer(Question current) {
		Label nameLabel = new Label("name is:");
		Label questionLabel = new Label("question is:" + current.getQuestionText());
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
			Question updated = updatebank.getQuestions().stream()
					.filter(q -> q.getQuestionText().equals(current.getQuestionText())).findFirst().orElse(current);
			showDetailView(updated);
		});
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> switchView(buildMainView()));
		VBox inputBox = new VBox(10, nameLabel, newName, questionLabel, newAnswer, submitButton, cancelButton);
		answersBox.getChildren().add(inputBox);
		switchView(answersBox);
	}
	/**********
	 * <p>
	 * Method: addAnswer
	 * </p>
	 * 
	 * <p>
	 * Description: Validates and adds a new answer to cur question. If either the
	 * name or answer text is missing, an error alert is shown. Otherwise, the
	 * answer is added and saved in the database.
	 * </p>
	 * 
	 * @param current    The question the answer belongs to
	 * @param name       The name of the user submitting the answer
	 * @param answerText text of the answer
	 */
	private void addAnswer(Question current, String name, String answerText) {
		if (name != null && !name.isEmpty() && answerText != null && !answerText.isEmpty()) {
			Answer ans = new Answer(name, answerText, 0, false);
			current.addAnswer(ans);
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR, "must input name and answer");
			alert.showAndWait();
		}
	}
	/**********
	 * <p>
	 * Method: showNewQuestion
	 * </p>
	 * 
	 * <p>
	 * Description: displays view for user to submit a new question. Includes
	 * category selection, text box for question entry, and live suggestions for
	 * similar questions. On submission, the question is saved to the database and
	 * the view is updated.
	 * </p>
	 */
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
				bank.addQuestion(q);
				questionsObservable.setAll(bank.getQuestions());
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
	/**********
	 * <p>
	 * Method: questionComboBox
	 * </p>
	 * 
	 * <p>
	 * Description: Creates ComboBox that allows the user to enter a new question
	 * while showing suggestions for existing similar questions. Selecting an
	 * existing question will trigger a callback to display details.
	 * </p>
	 * 
	 * @param existingQuestions  A list of existing questions to compare against
	 * @param onQuestionSelected Callback function to run when a match is selected
	 * @return A configured ComboBox with live search and selection behavior
	 */
	private ComboBox<String> questionComboBox(List<Question> existingQuestions, Consumer<Question> onQuestionSelected) {
		ComboBox<String> questionBox = new ComboBox<>();
		questionBox.setEditable(true);
		questionBox.setPromptText("Type your question here");
		questionBox.setPrefWidth(400);
		
		List<String> existing = existingQuestions.stream()
				.map(Question::getQuestionText).collect(Collectors.toList());
		final boolean[] updateItem = {false};
		// Live search as user types
		questionBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
			if(updateItem[0])return;
			if (newVal == null || newVal.isBlank()) {
				questionBox.getItems().clear();
				questionBox.hide();
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
			String currentText = questionBox.getEditor().getText();
			Platform.runLater(() -> {
				updateItem[0] = true;
				questionBox.hide();
				questionBox.getSelectionModel().clearSelection();
				questionBox.getItems().setAll(rankedMatches);
				questionBox.getEditor().setText(currentText);
				questionBox.getEditor().positionCaret(currentText.length());
				if(!rankedMatches.isEmpty()) {
					PauseTransition pause = new PauseTransition(Duration.millis(100));
					pause.setOnFinished(ev -> questionBox.show());
					pause.play();
				}
				updateItem[0] = false;
			});
		});

		// Jump to question on selection
		questionBox.getEditor().addEventFilter(KeyEvent.KEY_PRESSED,event -> {
			if(event.getCode() == KeyCode.ENTER) {
			String selectedText = questionBox.getEditor().getText();
			
			if (selectedText != null && !selectedText.isBlank()) {
				Optional<Question> match = existingQuestions.stream()
						.filter(q -> q.getQuestionText().equals(selectedText)).findFirst();

				match.ifPresent(onQuestionSelected); // 💡 callback to show detail view
			}
			event.consume();
			}
		});
		questionBox.setOnAction(null);
		return questionBox;
	}
	/**********
	 * <p>
	 * Method: buildMainView
	 * </p>
	 * 
	 * <p>
	 * Description: Builds the primary view that shows the list of all questions.
	 * Button to ask a new question, a button to filter resolved questions. Clicking
	 * a question in the list shows its detailed view.
	 * </p>
	 * 
	 * @return The main UI component for the question list
	 */
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
	/**********
	 * <p>
	 * Method: switchView
	 * </p>
	 * 
	 * <p>
	 * Description: Replaces the currently displayed view with a new one using the
	 * view-switching callback if it's available.
	 * </p>
	 * 
	 * @param newView The new JavaFX Parent node to display
	 */
	private void switchView(Parent newView) {
		if (setViewCallback != null) {
			setViewCallback.accept(newView);
		}

	}
}