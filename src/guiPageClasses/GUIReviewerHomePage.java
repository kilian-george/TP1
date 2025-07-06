package guiPageClasses;

import java.sql.SQLException;

import applicationMainMethodClasses.FCMainClass;
import databaseClasses.Database;
import entityClasses.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import privateMessages.MessageGUI;
import questionAndAnswer.QuestionGui;

/*******
 * <p> Title: GUIReviewerHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Single Role Home Page.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-04-20 Initial version
 *  
 */

public class GUIReviewerHomePage {
	
	/**********************************************************************************************

	Attributes
	
	**********************************************************************************************/
	
	// These are the application values required by the user interface
	private Label label_PageTitle = new Label();
	private Label label_UserDetails = new Label();
	private Button button_UpdateThisUser = new Button("Account Update");
	private Button button_CreateReview = new Button("Create Review");
	private Button button_ViewReviews = new Button("View My Reviews");
	private Button button_ViewProfile = new Button("My Profile");
	
	private Line line_Separator1 = new Line(20, 95, FCMainClass.WINDOW_WIDTH-20, 95);
	
	private Line line_Separator4 = new Line(20, 525, FCMainClass.WINDOW_WIDTH-20,525);
	
	private Button button_Logout = new Button("Logout");
	private Button button_Quit = new Button("Quit");

	private StackPane questionGuiHolder = new StackPane();
	private StackPane messageGuiHolder = new StackPane();
	
	private Stage primaryStage;	
	private Pane theRootPane;
	private Database theDatabase;
	private User theUser;

	/**********************************************************************************************

	Constructors
	
	**********************************************************************************************/

	
	/**********
	 * <p> Method: GUIReviewerHomePage(Stage ps, Pane theRoot, Database database, User user) </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object. </p>
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param theRoot specifies the JavaFX Pane to be used for this GUI and it's methods
	 * 
	 * @param database specifies the Database to be used by this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	@SuppressWarnings("unused")
	public GUIReviewerHomePage(Stage ps, Pane theRoot, Database database, User user) {
		GUISystemStartUpPage.theReviewerHomePage = this;
		
		FCMainClass.activeHomePage = 3;

		primaryStage = ps;
		theRootPane = theRoot;
		theDatabase = database;
		theUser = user;
		
		double WINDOW_WIDTH = FCMainClass.WINDOW_WIDTH;	
		
		primaryStage.setTitle("CSE 360 Foundation Code: User Home Page");

		// Label the window with the title and other common titles and buttons
		
		label_PageTitle.setText("Reviewer Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, WINDOW_WIDTH, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + user.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, WINDOW_WIDTH, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((event) -> {performUpdate(); });
		
		//Add the following button UIs to the page for "Create Review" and "View my Review"
		setupButtonUI(button_CreateReview, "Dialog", 18, 250, Pos.CENTER, 580, 540);
		button_CreateReview.setOnAction(e -> {
		    GUISystemStartUpPage.theCreateReviewPage = new GUICreateReviewPage(primaryStage, theRootPane, theDatabase, theUser);
		});

		setupButtonUI(button_ViewReviews, "Dialog", 18, 170, Pos.CENTER, 250, 120);
		button_ViewReviews.setOnAction((event) -> { performViewMyReviews(); });

		setupButtonUI(button_ViewProfile, "Dialog", 18, 170, Pos.CENTER, 450, 120);
		button_ViewProfile.setOnAction(e -> {
			new GUIReviewerProfilePage(primaryStage, theRootPane, theDatabase, theUser, theUser);
		});
		
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> {performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((event) -> {performQuit(); });
        
        setup();
	}

	
	/**********
	 * <p> Method: setup() </p>
	 * 
	 * <p> Description: This method is called to reset the page and then populate it with new
	 * content.</p>
	 * 
	 */
	//adding button_CreateReview, button_ViewReviews to the setup() method
	public void setup() {
		QuestionGui questionGui = new QuestionGui();
		questionGui.setOnViewSwitch(view -> {
			questionGuiHolder.getChildren().setAll(view);
		});
		// creates the parent and the holder so that a scrollpane is 
		//possible
		Parent questionMod = null;
		try {
			questionMod = questionGui.getView(theDatabase);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		questionGuiHolder.getChildren().setAll(questionMod);
		ScrollPane scrollPane = new ScrollPane(questionGuiHolder);
		//sets the scrollpane to fit the outer window
		 scrollPane.setFitToWidth(true);
		    scrollPane.setFitToHeight(true);
		    scrollPane.setPannable(true);
		    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		    scrollPane.setPrefHeight(400);
		   StackPane centerPane = new StackPane(scrollPane);
		   //sets the location of the scrollpane
		   centerPane.setPrefWidth(FCMainClass.WINDOW_WIDTH);
		   centerPane.setLayoutX(0);
		   centerPane.setLayoutY(90);
		   centerPane.setPadding(new Insets(20));
		theRootPane.getChildren().clear();		
	    theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
	        line_Separator4, 
	        button_Logout,
	        button_Quit,
	        createMessageNotif(),
	        button_ViewReviews,  
	        button_CreateReview,
	        button_ViewProfile     
	    );
			
	}
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 */
	
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	private StackPane createMessageNotif() {
		Button topMessageButton = new Button("Messages");
		
		topMessageButton.setOnAction(e -> {
			MessageGUI messageGui = new MessageGUI();
			messageGui.setOnViewSwitch(view -> {
				questionGuiHolder.getChildren().setAll(view);
			});
			try {
	            Parent messageView = messageGui.getView(theDatabase);
	            questionGuiHolder.getChildren().setAll(messageView);  // switch to message view
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    });

		    StackPane stack = new StackPane(topMessageButton);
		return stack;
	}
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	
	/**********************************************************************************************

	User Interface Actions for this page
	
	**********************************************************************************************/
	
	private void performUpdate () {
		if (GUISystemStartUpPage.theUserUpdatePage == null)
			GUISystemStartUpPage.theUserUpdatePage = 
				new GUIUserUpdatePage(primaryStage, theRootPane, theDatabase, theUser);
		else
			GUISystemStartUpPage.theUserUpdatePage.setup();	
	}
	//This method will call the GUICreateReviewPage class
	private void performCreateReview() {
		if (GUISystemStartUpPage.theCreateReviewPage == null)
			GUISystemStartUpPage.theCreateReviewPage =
				new GUICreateReviewPage(primaryStage, theRootPane, theDatabase, theUser);
		else
			GUISystemStartUpPage.theCreateReviewPage.setup();
	}
	//This method will call the GUIViewMyReviewPage class
	private void performViewMyReviews() {
		if (GUISystemStartUpPage.theViewMyReviewsPage == null)
			GUISystemStartUpPage.theViewMyReviewsPage =
				new GUIViewMyReviewsPage(primaryStage, theRootPane, theDatabase, theUser);
		else
			GUISystemStartUpPage.theViewMyReviewsPage.setup();
	}

	private void performLogout() {
		GUISystemStartUpPage.theSystemStartupPage.setup();
	}
	
	private void performQuit() {
		System.exit(0);
	}
}
