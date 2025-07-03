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
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import databaseClasses.Database;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class QuestionGui {
	private int currentIndex = 0;
	Database database;
	private Consumer<Parent> setViewCallback;
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
	 * Method: getView()
	 * </p>
	 * 
	 * <p>
	 * Description: This creates the initial view for the question and answer
	 * sections. It creates the buttons that then activate the helper methods. it
	 * also displays a list of questions by order of time
	 * </p>
	 * 
	 * @throws SQLException
	 * 
	 */
	public Parent getView(Database database) throws SQLException {
		 this.database = database;

		    // Create the ListView to show questions
		    ListView<Question> questionsList = new ListView<>();
		    ObservableList<Question> questions = FXCollections.observableArrayList(database.getAllQuestions());
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

		    // Create buttons and controls for the top area
		    Button newQuestionButton = new Button("Ask New Question");
		    newQuestionButton.setOnAction(e -> showNewQuestion());

		    HBox resolvedButtonBox = createResloveListButton();
		    HBox UnresolvedButtonBox = createUnresloveListButton();
		    HBox allUnresolvedButtonBox = createAllUnresolvedButton();

		    HBox topBar = new HBox(10, newQuestionButton, resolvedButtonBox, UnresolvedButtonBox, allUnresolvedButtonBox);
		    topBar.setPadding(new Insets(10));

		    // Create main content container
		    VBox fullContent = new VBox(10);
		    fullContent.setPadding(new Insets(10));
		    fullContent.getChildren().addAll(topBar, questionsList);

		    
		    // Wrap in ScrollPane
		    ScrollPane scrollPane = new ScrollPane(fullContent);
		    scrollPane.setFitToWidth(true);
		    scrollPane.setFitToHeight(true);
		    scrollPane.setPannable(true);
		    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		    scrollPane.setPrefViewportHeight(500);
		    scrollPane.setPrefViewportWidth(700);

		    return scrollPane;
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
		// makes the delete, createQuestion, answer,ect buttons
		topBox.getChildren().add(createDeleteButton(question));
		topBox.setPadding(new Insets(10));
		topBox.getChildren().add(createEditQuestionButton(question));
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
		// if the username dosnt match or the user isnt an admin, the delete button is
		// hidden for security
		if (!database.getCurrentUsername().equals(question.getName()) && !database.getCurrentAdminRole()) {
			deleteButton.setVisible(false);
		}
		deleteButton.setOnAction(e -> {
			// creates a conformation when attempting to delete a question
			Alert delAlert = new Alert(AlertType.CONFIRMATION, "are you sure?");
			delAlert.setHeaderText("confirm delete");
			delAlert.setTitle("Delete question");
			// makes the popup with an ok button, if clicked it will
			// delete the question
			Optional<ButtonType> resAlert = delAlert.showAndWait();
			if (resAlert.isPresent() && resAlert.get() == ButtonType.OK) {
				try {
					// attempts to delete the question
					database.deleteQuestion(question.getId());
					// if not found then it it will error out
				} catch (SQLException e1) {

					e1.printStackTrace();
				}
				try {
					// resets the view
					switchView(getView(database));
				} catch (SQLException e1) {

					e1.printStackTrace();
				}
			}

		});
		return deleteButton;
	}
	private Button createEditQuestionButton(Question question) {
		Button editButton = new Button("Edit answer");
		if(!question.getName().equals(database.getCurrentUsername())) {
			editButton.setVisible(false);
		}
		editButton.setOnAction(e ->{
			TextArea editArea = new TextArea(question.getQuestionText());
			Button saveButton = new Button("Save edit");
			VBox editBox = new VBox(5, editArea, saveButton);
			saveButton.setOnAction(ev -> {
			    String newText = editArea.getText().trim();

			    if (newText.isEmpty()) {
			        try {
			            database.deleteQuestion(question.getId()); 
			           switchView(getView(database));         
			        } catch (SQLException ex) {
			            ex.printStackTrace();
			            new Alert(Alert.AlertType.ERROR, "Failed to delete answer.").showAndWait();
			        }
			    } else {
			        question.setQuestionText(newText);
			        database.updateQuestion( question);  
			        showDetailView(question); 
			    }
			});
			switchView(editBox);
		});
		return editButton;
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
		// makes a time stamp in the format specified
		String questionTime = question.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		// creates the label and question text with added timestamp
		Label questionLabel = new Label("Question is: " + question.getQuestionText() + " (" + questionTime + ")");
		questionLabel.setWrapText(true);
		// shows the category of question
		Label categoryLabel = new Label("Category is: " + question.getCategory());
		questionLabel.setWrapText(true);
		// displays username
		Label nameLabel = new Label(database.getCurrentUsername());
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
			// checks if any answers exist
			answersBox.getChildren().add(new Label("nobody has answered yet"));
		} else {
			List<Answer> sortAnswers = new ArrayList<>(question.getAnswers());
			// sorts the answers by score
			sortAnswers.sort((a1, a2) -> {
				// if an answer is marked as resolved then it will always be on top
				if (a1.getResolved() != a2.getResolved()) {
					// compares the resolve states of all answers
					return Boolean.compare(!a1.getResolved(), !a2.getResolved());
				}
				// compares the score states so that it organizes by score
				return Integer.compare(a2.getScore(), a1.getScore());
			});

			for (Answer a : sortAnswers) {
				// for all the answers in the question, it makes comments
				VBox commentBox = commentHelper(a, question);
				// time stamp maker
				String answerTime = a.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
				Label answerLabel = new Label("*" + a.getName() + ":" + a.getAnswerText() + " (" + answerTime + ")");
				answerLabel.setWrapText(true);
				// creates the labels for score
				Label scoreLabel = new Label("score: " + a.getScore());
				// creates a green check if answer is marked resolved
				Label resolvedLabel = new Label("\u2714");
				resolvedLabel.setStyle("-fx-text-fill: green; -fx-font-size:20px");
				resolvedLabel.setVisible(a.getResolved());
				
				// upvote and downvote
				Button upvoteButton = new Button("\u25B2");
				Button downvoteButton = new Button("\u25BC");
				String currentUser = database.getCurrentUsername();
				int voted = database.getUserVote(currentUser, a.getId());
				if(voted !=0) {
					upvoteButton.setDisable(true);
					downvoteButton.setDisable(true);
				}
				// if upvote clicked, it disables downvote for answer
				upvoteButton.setOnAction(e -> {
					a.setScore(a.getScore()+1);
					scoreLabel.setText("score: " + a.getScore());
					upvoteButton.setDisable(true);
					downvoteButton.setDisable(true);
					database.updateAnswer(a, question.getId());
					database.saveUserVote(currentUser, a.getId(), 1);
				});
				// same for downvote, only one vote per user
				downvoteButton.setOnAction(e -> {
					a.setScore(a.getScore()-1);
					scoreLabel.setText("score: " + a.getScore());
					upvoteButton.setDisable(true);
					downvoteButton.setDisable(true);
					database.updateAnswer(a, question.getId());
					database.saveUserVote(currentUser,a.getId(),-1);
				});
				
				// marks the answer as having resolved the question
				Button resolveButton = new Button("resolved question");
				if(database.getCurrentUsername()!=question.getName()) {
					resolveButton.setVisible(false);
				}
				resolveButton.setOnAction(e -> {
					// disables all other resolve buttons
					question.setResolved(true);
					for (Answer other : question.getAnswers()) {
						other.setResolved(false);
					}
					a.setResolved(true);
					database.updateAnswer(a, question.getId());
					showDetailView(question);
				});
				//edit button
				Button editButton = new Button("Edit answer");
				if(!a.getName().equals(database.getCurrentUsername())) {
					editButton.setVisible(false);
				}
				editButton.setOnAction(e ->{
					TextArea editArea = new TextArea(a.getAnswerText());
					Button saveButton = new Button("Save edit");
					VBox editBox = new VBox(5, editArea, saveButton);
					saveButton.setOnAction(ev -> {
					    String newText = editArea.getText().trim();

					    if (newText.isEmpty()) {
					        try {
					            database.deleteAnswer(a.getId());  
					            question.getAnswers().remove(a);   
					            showDetailView(question);         
					        } catch (SQLException ex) {
					            ex.printStackTrace();
					            new Alert(Alert.AlertType.ERROR, "Failed to delete answer.").showAndWait();
					        }
					    } else {
					        a.setAnswerText(newText);
					        database.updateAnswer(a, question.getId());  
					        showDetailView(question); 
					    }
					});
					answersBox.getChildren().add(editBox);
				});
				HBox answerHorz = new HBox(upvoteButton, downvoteButton, scoreLabel, resolvedLabel, resolveButton);
				answersBox.getChildren().addAll(answerHorz, answerLabel, commentBox, editButton);

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
		Button listResolvedQuestionButton = new Button("click for YOUR resolved questions");
		listResolvedQuestionButton.setOnAction(e -> {
			String currentUser = database.getCurrentUsername();
			List<Question> allQ = database.getAllQuestions();
			// this takes the questions and filters them by if they are resolved or not as
			// well
			// as by username and puts it into the myResolved list
			List<Question> myResolved = allQ.stream().filter(q -> q.isResolved() && q.getName().equals(currentUser))
					.collect(Collectors.toList());
			VBox resolvedQuestionBox = new VBox(10);
			for (Question q : myResolved) {
				// for each question in my resolved questions it makes a label to display
				// the text of each
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
		String currentUser = database.getCurrentUsername();
		Button listUnesolvedQuestionButton = new Button("click for YOUR unresolved questions");
		listUnesolvedQuestionButton.setOnAction(e -> {
			List<Question> bank = database.getAllQuestions();
			List<Question> myUnresolved = bank.stream()
					// this takes all the questions and filters them by username and
					// if they ahve been unresolved, then it puts them in a list
					.filter(q -> !q.isResolved() && q.getName().equals(currentUser)).collect(Collectors.toList());

			VBox unresolvedQuestionBox = new VBox(10);
			// for all the questions it makes the text display
			for (Question q : myUnresolved) {
				Label qLabel = new Label(q.getQuestionText());
				unresolvedQuestionBox.getChildren().add(qLabel);
			}
			// sets the view state
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
	 * Method: createAllUnresolvedButton
	 * </p>
	 * 
	 * <p>
	 * Description: Creates a button that shows a popup containing all unresolved
	 * questions in the database. Clicking on a question in the list opens a
	 * detailed view of that question.
	 * </p>
	 * 
	 * @return A horizontal box containing the button for all unresolved questions
	 */
	private HBox createAllUnresolvedButton() {
		Button unresolvedButton = new Button("view ALL unresolved questions");
		HBox box = new HBox(unresolvedButton);
		unresolvedButton.setOnAction(e -> {
			// when pressed
			try {
				// create a list and filter the questions by if its resolved or not
				List<Question> allQ = database.getAllQuestions();
				List<Question> unresolved = allQ.stream().filter(q -> !q.isResolved()).collect(Collectors.toList());
				if (unresolved.isEmpty()) {
					// if there are not unresolved answers then it puts an error out
					new Alert(Alert.AlertType.INFORMATION, "no unresolved").showAndWait();
					return;
				}
				ListView<Question> listView = new ListView<>(FXCollections.observableArrayList(unresolved));
				listView.setCellFactory(param -> new ListCell<>() {
					// this modifies the way javafx displays the data for getting the question text
					@Override
					protected void updateItem(Question item, boolean empty) {
						// it overrides the update so that the text is what i want it to be
						super.updateItem(item, empty);
						setText(empty || item == null ? null : item.getQuestionText());
					}
				});
				listView.setOnMouseClicked(click -> {
					if (click.getClickCount() == 2) {
						// when the specific question is clicked it sends it to get detail view
						// so that the selected question is displayed
						Question selected = listView.getSelectionModel().getSelectedItem();
						if (selected != null) {
							showDetailView(selected);
						}
					}
				});
				// setting up the popup with direction
				Stage popup = new Stage();
				popup.setTitle("Unresolved Questions");
				VBox layout = new VBox(10, new Label("Unresolved Questions:"), listView);
				layout.setPadding(new Insets(10));
				popup.setScene(new Scene(layout, 500, 400));
				popup.show();
			} catch (Exception ex) {
				ex.printStackTrace();
				new Alert(Alert.AlertType.ERROR, "failed to load");
			}
		});
		return box;
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
		// sends data to showAddAnswer
		addAnswerButton.setOnAction(e -> showaddAnswer(question));
		// creates the nav buttons for adding or going back
		Button backButton = new Button("Back");
		backButton.setOnAction(e -> {
			try {
				// switches to main view of program
				switchView(getView(database));
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});
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
			// looks for all the comments and makes timestamps for them
			String time = comment.gettimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
			Label commentLabel = new Label("-By:"+comment.getName() +" "+ comment.getText() + " (" + time + ") ");
			commentBox.getChildren().add(commentLabel);
		}
		TextField commentField = new TextField();
		commentField.setPromptText("enter comment");
		Button commentButton = new Button("post!");
		commentButton.setOnAction(e -> {
			// when button is pushed it looks for text and if its not empty
			// puts it into the comment table which is associated with the answers and
			// question
			// table
			String text = commentField.getText().trim();
			if (!text.isEmpty()) {
				answer.addComment(new Comment(answer.getName(), text));
				database.updateAnswer(answer, question.getId());
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
		VBox answersBox = new VBox(10);
		// setting up naming area
		Label nameLabel = new Label(database.getCurrentUsername());
		// setting up question area
		Label questionLabel = new Label("question is:" + current.getQuestionText());

		TextArea newAnswer = new TextArea();
		newAnswer.setPromptText("Answer here!");
		newAnswer.setPrefWidth(400);
		newAnswer.setPrefRowCount(5);

		// button functionality
		Button submitButton = new Button("Submit");
		submitButton.setOnAction(e -> {
			// gets the text of the answer given to the question
			addAnswer(current, database.getCurrentUsername(), newAnswer.getText());
			List<Question> updatebank = database.getAllQuestions();
			// filters the questions so they are updated or otherwise just puts the current
			// one in first
			Question updated = updatebank.stream().filter(q -> q.getQuestionText().equals(current.getQuestionText()))
					.findFirst().orElse(current);
			showDetailView(updated);
		});
		Button cancelButton = new Button("Cancel");
		// goes back to main view
		cancelButton.setOnAction(e -> switchView(buildMainView()));
		VBox inputBox = new VBox(10, nameLabel, questionLabel, newAnswer, submitButton, cancelButton);
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
		// if all fields are filled
		if (name != null && !name.isEmpty() && answerText != null && !answerText.isEmpty()) {
			// adds a new answer to the database
			Answer ans = new Answer(name, answerText, 0, false);
			database.saveNewAnswer(ans, current.getId());
			current.addAnswer(ans);
			showDetailView(current);
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
		// combo box is the drop down menu that has a list of similar questions to yours
		Node questionField = customQuestionSearchField(database.getAllQuestions(),
				matchedQuestion -> showDetailView(matchedQuestion));

		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20));

		Label nameLabel = new Label(database.getCurrentUsername());

		TextField categoryField = new TextField();
		categoryField.setPromptText("Category (e.g., Homework, Project)");

		Button submit = new Button("Submit");
		// when submitting, it gathers all the info
		submit.setOnAction(e -> {
			String name = nameLabel.getText().trim();
			String category = categoryField.getText().trim();
			String questionText = ((TextField) questionField).getText().trim();
			// and if all info is there it creates a new question
			if (!name.isEmpty() && !category.isEmpty() && !questionText.isEmpty()) {
				Question q = new Question(name, category, questionText, currentIndex);
				// save to database
				int questionId = database.saveQuestion(q);
				q.setId(questionId);
				showDetailView(q);
			} else {
				new Alert(Alert.AlertType.ERROR, "All fields must be filled.").showAndWait();
			}
		});

		Button cancel = new Button("Cancel");
		cancel.setOnAction(e -> {
			try {
				// goes back
				switchView(getView(database));
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		layout.getChildren().addAll(new Label("Ask a New Question"), nameLabel, categoryField, questionField, submit,
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
	private Node customQuestionSearchField(List<Question> existingQuestions, Consumer<Question> onQuestionSelected) {
	    TextField inputField = new TextField();
	    inputField.setPromptText("Type your question here");
	    inputField.setPrefWidth(400);

	    // Create a popup dropdown
	    Popup popup = new Popup();
	    ListView<String> suggestionList = new ListView<>();
	    suggestionList.setPrefWidth(400);
	    suggestionList.setMaxHeight(150);
	    popup.getContent().add(suggestionList);
	    popup.setAutoHide(true);

	    List<String> allQuestions = existingQuestions.stream()
	            .map(Question::getQuestionText)
	            .collect(Collectors.toList());

	    // Update suggestions as user types
	    inputField.textProperty().addListener((obs, oldVal, newVal) -> {
	        if (newVal == null || newVal.isBlank()) {
	            popup.hide();
	            return;
	        }

	        Set<String> inputWords = Arrays.stream(newVal.toLowerCase().split("\\W+"))
	                .filter(word -> !word.isBlank())
	                .collect(Collectors.toSet());

	        List<String> rankedMatches = allQuestions.stream()
	                .map(q -> {
	                    Set<String> qWords = Arrays.stream(q.toLowerCase().split("\\W+"))
	                            .collect(Collectors.toSet());
	                    long score = inputWords.stream().filter(qWords::contains).count();
	                    return Map.entry(q, score);
	                })
	                .filter(entry -> entry.getValue() > 0)
	                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
	                .map(Map.Entry::getKey)
	                .limit(5)
	                .collect(Collectors.toList());

	        suggestionList.getItems().setAll(rankedMatches);

	        if (!popup.isShowing() && !rankedMatches.isEmpty()) {
	            // Position the popup under the input field
	            Bounds bounds = inputField.localToScreen(inputField.getBoundsInLocal());
	            popup.show(inputField, bounds.getMinX(), bounds.getMaxY());
	        } else if (rankedMatches.isEmpty()) {
	            popup.hide();
	        }
	    });

	    // Handle click selection
	    suggestionList.setOnMouseClicked(event -> {
	        String selected = suggestionList.getSelectionModel().getSelectedItem();
	        if (selected != null) {
	            inputField.setText(selected);
	            popup.hide();
	            existingQuestions.stream()
	                    .filter(q -> q.getQuestionText().equals(selected))
	                    .findFirst()
	                    .ifPresent(onQuestionSelected);
	        }
	    });

	    // Handle Enter key to select the first suggestion
	    inputField.setOnKeyPressed(event -> {
	        if (event.getCode() == KeyCode.DOWN) {
	            suggestionList.requestFocus();
	            suggestionList.getSelectionModel().selectFirst();
	        } else if (event.getCode() == KeyCode.ENTER) {
	            String selected = suggestionList.getItems().isEmpty() ? inputField.getText()
	                    : suggestionList.getItems().get(0);
	            inputField.setText(selected);
	            popup.hide();
	            existingQuestions.stream()
	                    .filter(q -> q.getQuestionText().equals(selected))
	                    .findFirst()
	                    .ifPresent(onQuestionSelected);
	        }
	    });

	    // Allow keyboard nav inside list
	    suggestionList.setOnKeyPressed(event -> {
	        if (event.getCode() == KeyCode.ENTER) {
	            String selected = suggestionList.getSelectionModel().getSelectedItem();
	            if (selected != null) {
	                inputField.setText(selected);
	                popup.hide();
	                existingQuestions.stream()
	                        .filter(q -> q.getQuestionText().equals(selected))
	                        .findFirst()
	                        .ifPresent(onQuestionSelected);
	            }
	        }
	    });

	    return inputField;
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
		// makes a list into a viewable thing for javafx to display
		ObservableList<Question> questions = FXCollections.observableArrayList(database.getAllQuestions());
		ListView<Question> questionsList = new ListView<>(questions);
		// this takes the cells and allows them to be viewed correctly by javafx
		questionsList.setCellFactory(param -> new ListCell<Question>() {
			@Override
			// overrides the original to make it viewable in the way i want it to
			protected void updateItem(Question item, boolean notHere) {
				super.updateItem(item, notHere);
				setText((notHere || item == null) ? null : item.getQuestionText());
			}
		});
		// when a question in the list is clicked it slects it and shows the details of
		// it
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
