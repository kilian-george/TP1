package privateMessages;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import databaseClasses.Database;
import guiPageClasses.GUIStudentHomePage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import questionAndAnswer.Question;
import questionAndAnswer.QuestionGui;

public class MessageGUI {
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
	 * Description: This creates the initial view for the message system
	 * sections. It creates the buttons that then activate the helper methods. it
	 * also displays a list of messages by order of time
	 * </p>
	 * 
	 * @throws SQLException
	 * 
	 */
	public Parent getView(Database database) throws SQLException {
		 this.database = database;

		    // Create the ListView to show questions
		    ListView<Message> messageList = new ListView<>();
		    ObservableList<Message> messages = FXCollections.observableArrayList(database.getUsersMessages(database.getCurrentUsername()));
		    messageList.setItems(messages);
		    messageList.setCellFactory(param -> new ListCell<Message>() {
		        protected void updateItem(Message item, boolean notHere) {
		            super.updateItem(item, notHere);
		            setText((notHere || item == null) ? null : item.getMessage());
		        }
		    });

		    messageList.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2) {
		            Message selected = messageList.getSelectionModel().getSelectedItem();
		            if (selected != null) {
		                showDetailView(selected); 
		            }
		        }
		    });

		    // Create buttons and controls for the top area
		    Button newMessageButton = new Button("Type new message");
		    newMessageButton.setOnAction(e -> showNewMessage());
	Button cancelButton = new Button("Cancel");
	// goes back to main view
	cancelButton.setOnAction(e -> {
		QuestionGui questionGui = new QuestionGui();
		questionGui.setOnViewSwitch(setViewCallback);
		try {
			Parent home = questionGui.getView(database);
			switchView(home);
		}catch(SQLException ex) {
			ex.printStackTrace();
		}
	});
		    HBox unreadButtonBox = createUnreadListButton();
		    HBox userSpecificButtonBox = createUserSpecificButton(database);

		    HBox topBar = new HBox(10, newMessageButton, unreadButtonBox, userSpecificButtonBox, cancelButton);
		    topBar.setPadding(new Insets(10));

		    // Create main content container
		    VBox fullContent = new VBox(10);
		    fullContent.setPadding(new Insets(10));
		    fullContent.getChildren().addAll(topBar, messageList);

		    
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
	/**
	 * <p>Creates a UI component that allows searching for a specific user and displays all messages
	 * sent by that user in a popup window if found.</p>
	 *
	 * <p>The method adds a text input for entering a username and a search button. When a valid user is found,
	 * it retrieves all messages from that user using {@code database.getMessagesFromUser()}, displays them
	 * in a {@code ListView}, and opens a new window to view and interact with those messages.
	 * Double-clicking a message opens a detailed view using {@code showDetailView()}.</p>
	 *
	 * @param database the {@code Database} instance used to check user existence and retrieve their messages
	 * @return an {@code HBox} containing the user search field, button, and result label
	 */
	private HBox createUserSpecificButton(Database database) {
	    TextField searchUser = new TextField();
	    searchUser.setPromptText("Enter username to search");

	    Button searchButton = new Button("Search!");
	    Label resLabel = new Label();

	    searchButton.setOnAction(e -> {
	        String query = searchUser.getText().trim();
	        if (!query.isEmpty()) {
	            boolean userFound = database.doesUserExist(query);
	            if (userFound) {
	                resLabel.setText("User found: " + query);

	                // Retrieve messages sent by this user
	                List<Message> messagesFromUser = database.getMessagesFromUser(query);
	                ObservableList<Message> messages = FXCollections.observableArrayList(messagesFromUser);

	                ListView<Message> listView = new ListView<>(messages);
	                listView.setCellFactory(param -> new ListCell<Message>() {
	                    @Override
	                    protected void updateItem(Message item, boolean empty) {
	                        super.updateItem(item, empty);
	                        setText((empty || item == null) ? null : item.getMessage());
	                    }
	                });

	                listView.setOnMouseClicked(event -> {
	                    if (event.getClickCount() == 2) {
	                        Message selected = listView.getSelectionModel().getSelectedItem();
	                        if (selected != null) {
	                            showDetailView(selected); // your existing detail view
	                        }
	                    }
	                });

	                // Show messages in a popup
	                VBox popupContent = new VBox(10, new Label("Messages from: " + query), listView);
	                popupContent.setPadding(new Insets(10));
	                Scene scene = new Scene(popupContent, 400, 300);
	                Stage popup = new Stage();
	                popup.setTitle("Messages from " + query);
	                popup.setScene(scene);
	                popup.show();

	            } else {
	                resLabel.setText("No user found with that username");
	            }
	        } else {
	            resLabel.setText("Enter username");
	        }
	    });

	    VBox retBox = new VBox(5, new HBox(10, searchUser, searchButton), resLabel);
	    return new HBox(retBox);
	}
	/**
	 * <p>Creates a UI component that adds a button to filter and display unread messages for the current user.</p>
	 *
	 * <p>When the "filter by unread" button is clicked, this method retrieves all messages for the
	 * current user from the database, filters for those that are unread, and displays them in a
	 * new popup window using a {@code ListView}. Double-clicking a message in the list opens its
	 * detailed view via {@code showDetailView()}.</p>
	 *
	 * @return an {@code HBox} containing the "filter by unread" button
	 */
	private HBox createUnreadListButton() {
		 HBox retv = new HBox();
		Button searchButton = new Button("filter by unread");
		
		searchButton.setOnAction(e -> {
			 List<Message> messageList = database.getUsersMessages(database.getCurrentUsername());
			 List<Message> unreadOnly = messageList.stream().filter(m ->!m.isRead()).toList();
			 ObservableList<Message> messages = FXCollections.observableArrayList(unreadOnly);
			  ListView<Message> unreadLV = new ListView<>();
			  unreadLV.setItems(messages);
			   unreadLV.setCellFactory(param -> new ListCell<Message>() {
			  
			        protected void updateItem(Message item, boolean notHere) {
			            super.updateItem(item, notHere);
			            setText((notHere || item == null) ? null : item.getMessage());
			        }
			    });

			    unreadLV.setOnMouseClicked(event -> {
			        if (event.getClickCount() == 2) {
			            Message selected = unreadLV.getSelectionModel().getSelectedItem();
			            if (selected != null) {
			                showDetailView(database.getMessageById(selected.getId())); 
			            }
			        }
			    });
			   VBox popup = new VBox(unreadLV);
			   Scene scene = new Scene(popup, 400, 300);
			   Stage popUPp = new Stage();
			   popUPp.setTitle("unread");
			   popUPp.setScene(scene);
			   popUPp.show();
					   
			   
		});
		 retv.getChildren().add(searchButton);
		return retv;
	}
	/**
	 * <p>Displays the detailed view of a selected message.</p>
	 *
	 * <p>This method marks the message as read in the database, constructs a visual layout showing
	 * the message details (sender, receiver, timestamp, and content), and provides a button to 
	 * send a reply. The detail view is then displayed by calling {@code switchView()}.</p>
	 *
	 * @param selected the {@code Message} to be displayed in detail
	 */
	private void showDetailView(Message selected) {
		selected.setRead(true);
		database.markAsRead(selected.getId());
		VBox detailBox = new VBox(10);
		detailBox.setPadding(new Insets(10));
		HBox topBox = new HBox();
		
		topBox.setAlignment(Pos.TOP_RIGHT);
		// makes the delete, createQuestion, answer,ect buttons
		topBox.getChildren().add(createSendMessageButton(selected));
		topBox.setPadding(new Insets(10));
		
		List<Node> messageLabels = createMessageData(selected);
		
		HBox showUnreadMessages = createUnreadListButton();
		detailBox.getChildren().add(topBox);
		detailBox.getChildren().addAll(messageLabels);
		switchView(detailBox);
	}
	/**
	 * <p>Creates a list of JavaFX nodes representing the details of a message.</p>
	 *
	 * <p>This includes labels for the sender, receiver, timestamp, and the message content itself.</p>
	 *
	 * @param selected the {@code Message} whose details are to be displayed
	 * @return a {@code List<Node>} containing labeled information about the message
	 */
	private List<Node> createMessageData(Message selected) {
		Label from = new Label("From: "+selected.getSender());
		Label to = new Label("To: "+selected.getReceiver());
		Label timestamp = new Label("sent at: "+selected.getTimestamp());
		Label message = new Label(selected.getMessage());
		message.setWrapText(true);
		return List.of(from, to, timestamp, message);
	}
	/**
	 * <p>Creates a button that opens the message drafting interface when clicked.</p>
	 *
	 * <p>The button allows the user to initiate a new message. Although the method receives
	 * a {@code Message} object, it currently does not use it — but it could be extended
	 * in the future to prefill fields (e.g., for replies).</p>
	 *
	 * @param selected the {@code Message} related to the action (currently unused)
	 * @return a {@code Node} representing the button that triggers the new message popup
	 */
	private Node createSendMessageButton(Message selected) {
		Button sendTo = new Button("Click to draft a new message");
		sendTo.setOnAction(e -> showNewMessage());
		return sendTo;
	}
	/**
	 * <p>Displays a popup window that allows the user to draft and send a new message.</p>
	 *
	 * <p>The popup includes a text field to enter the recipient's username, a label showing the sender,
	 * a text field for the message body, and buttons to send or cancel the message.</p>
	 *
	 * <p>If the message body is empty, a warning alert is shown. Otherwise, the message is sent,
	 * the popup is closed, and the main view is refreshed to reflect the new message.</p>
	 */
	private void showNewMessage() {
		Stage popStage = new Stage();
		popStage.setTitle("new message");
		
		TextField rec = new TextField();
		rec.setPromptText("to: ");
		String curUser = database.getCurrentUsername();
		Label sender = new Label("from: "+curUser);
		
		TextField messageText = new TextField();
		messageText.setPromptText("Enter message here");
		Button send = new Button("send");
		send.setOnAction(e ->{String response = messageText.getText().trim();
		if(!response.isEmpty()) {
			LocalDateTime timeStp = LocalDateTime.now();
			Message sending = new Message(curUser, rec.getText(), 
					messageText.getText(), timeStp, false);
			database.sendMessage(sending);
			popStage.close();
			try {
				switchView(getView(database));
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else {
			Alert newMsg = new Alert(Alert.AlertType.WARNING);
			newMsg.setTitle("Incomplete message");
			newMsg.setHeaderText(null);
			newMsg.setContentText("must have all fields filled out");
			newMsg.showAndWait();
		}
		});

		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(e -> popStage.close());
		VBox form = new VBox(10, rec, sender, messageText, new HBox(10,send, cancelButton));
		form.setPadding(new Insets(10));
		form.setAlignment(Pos.CENTER_LEFT);
		Scene scene = new Scene(form, 400, 300);
		popStage.setScene(scene);
		popStage.show();
	}
	/**
	 * <p>Switches the current view to the given {@code Parent} node.</p>
	 *
	 * <p>This method uses the {@code setViewCallback} to update the main display area
	 * if the callback has been set. It is typically used to navigate between different
	 * UI screens in the application.</p>
	 *
	 * @param parent the new view to be displayed
	 */
	private void switchView(Parent parent) {
		if(setViewCallback !=null) {
			setViewCallback.accept(parent);
		}
		
	}
	

}
