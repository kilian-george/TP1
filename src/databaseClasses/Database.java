package databaseClasses;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
//Do we need to have all the "import java.sql...;" if we are already using the "import java.sql.*;"? -Ronaldo
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entityClasses.User;
//Make sure to add this ↓ entity class 
import entityClasses.Review;
import questionAndAnswer.Answer;
import questionAndAnswer.Comment;
import questionAndAnswer.Question;

/*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.)  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by on a previous
 * 							version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentStudentRole;
	private boolean currentReviewerRole;
	private boolean currentInstructorRole;
	private boolean currentStaffRole;

	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

public void dropDatabase() {
	    try (Statement stmt = connection.createStatement()) {
	        // Drop all tables
	        stmt.executeUpdate("DROP TABLE IF EXISTS InvitationCodes");
	        stmt.executeUpdate("DROP TABLE IF EXISTS userDB");
	        stmt.executeUpdate("DROP TABLE IF EXISTS Questions");
	        stmt.executeUpdate("DROP TABLE IF EXISTS Answers");
	        stmt.executeUpdate("DROP TABLE IF EXISTS Comments");

	        // Recreate tables fresh
	        createTables();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the two database tables used by this class.</p>
 * 
 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "studentRole BOOL DEFAULT FALSE, "
				+ "reviewerRole BOOL DEFAULT FALSE, "
				+ "instructorRole BOOL DEFAULT FALSE, "
				+ "staffRole BOOL DEFAULT FALSE)";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	    		+ "emailAddress VARCHAR(255), "
	            + "role VARCHAR(10))";
	    statement.execute(invitationCodesTable);
	    //create the Questions table
	    
	    String questionsTable = "CREATE TABLE IF NOT EXISTS Questions ("
	    	    + "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+"isResolved BOOLEAN DEFAULT FALSE,"
	    	    + "username VARCHAR(255), "
	    	    + "category VARCHAR(255), "
	    	    + "questionText TEXT)";
	    	statement.execute(questionsTable);
	    	// Create the Answers table
	    	String answersTable = "CREATE TABLE IF NOT EXISTS Answers ("
	    	    + "id INT AUTO_INCREMENT PRIMARY KEY, "
	    	    + "questionId INT, "
	    	    +"username VARCHAR(255),"
	    	    + "answerText TEXT, "
	    	    + "score INT,"
	    	    + "FOREIGN KEY (questionId) REFERENCES Questions(id) ON DELETE CASCADE)";
	    	statement.execute(answersTable);
	    	

	    	String commentsTable = "CREATE TABLE IF NOT EXISTS Comments ("
	    		+"id INT PRIMARY KEY AUTO_INCREMENT,"
	    		+"answer_id INT,"
	    		+"text TEXT,"
	    		+"timestamp TEXT,"
	    		+"FOREIGN KEY (answer_id) REFERENCES Answers(id) ON DELETE CASCADE"
	    		+")";
	    		statement.execute(commentsTable);

		//Added this for the reviewer class
		// Create the Reviews table
		String reviewsTable = "CREATE TABLE IF NOT EXISTS Reviews (" +
		        "reviewId INT AUTO_INCREMENT PRIMARY KEY, " +
		        "reviewerUsername VARCHAR(255), " +
		        "targetId VARCHAR(255), " +  // could be question ID or answer ID
		        "content TEXT, " +
		        "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
		        "FOREIGN KEY (reviewerUsername) REFERENCES userDB(userName) ON DELETE CASCADE" +
		        ")";
		statement.execute(reviewsTable);
	    	
	}

	public List<Question> getAllQuestions() {
	    List<Question> questions = new ArrayList<>();

	    String query = "SELECT * FROM Questions";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            int id = rs.getInt("id");
	            String username = rs.getString("username");
	            String category = rs.getString("category");
	            String questionText = rs.getString("questionText");
	            boolean isResolved = rs.getBoolean("isResolved");

	            Question q = new Question(username, category, questionText, id);
	            q.setId(id); 
	            q.setResolved(isResolved);
	            q.setAnswers(getAnswersForQuestion(id));

	            questions.add(q);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return questions;
	}

	private List<Answer> getAnswersForQuestion(int questionId) {
	    List<Answer> answers = new ArrayList<>();
	    String query = "SELECT * FROM Answers WHERE questionId = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	    	pstmt.setInt(1, questionId);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            String username = rs.getString("username");
	            String answerText = rs.getString("answerText");
	            int score = rs.getInt("score");
	            answers.add(new Answer(username, answerText, score));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return answers;
	}
	
	public int saveQuestion(Question question) {
	    String insert = "INSERT INTO Questions (username, category, questionText, isResolved) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setString(1, question.getName());
	        pstmt.setString(2, question.getCategory());
	        pstmt.setString(3, question.getQuestionText());
	        pstmt.setBoolean(4, question.isResolved());
	        pstmt.executeUpdate();

	        ResultSet keys = pstmt.getGeneratedKeys();
	        if (keys.next()) {
	            return keys.getInt(1); // return generated question ID
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1;
	}
	
	public void deleteQuestion(int id) throws SQLException {
	    String deleteAnswers = "DELETE FROM Answers WHERE questionId = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteAnswers)) {
	        pstmt.setInt(1, id);
	        pstmt.executeUpdate();
	    }

	    String deleteQuestion = "DELETE FROM Questions WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteQuestion)) {
	        pstmt.setInt(1, id);
	        pstmt.executeUpdate();
	    }
	}
	
	public void saveAnswer(Answer answer, int questionId) {
	    String insert = "INSERT INTO Answers (questionId, username, answerText) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insert)) {
	        pstmt.setInt(1, questionId);
	        pstmt.setString(2, answer.getName());
	        pstmt.setString(3, answer.getAnswerText());
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void saveComment(int answerId, Comment comment) {
	    String insert = "INSERT INTO Comments (answer_id, text, timestamp) VALUES (?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insert))
	          {

	        pstmt.setInt(1, answerId);
	        pstmt.setString(2, comment.getText());
	        pstmt.setString(3, comment.gettimestamp().toString());  // ISO format is safe
	        pstmt.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public List<Comment> getCommentsByAnswerId(int answerId) {
	    List<Comment> comments = new ArrayList<>();
	    String query = "SELECT text, timestamp FROM Comments WHERE answer_id = ? ORDER BY id";

	    try ( PreparedStatement pstmt = connection.prepareStatement(query))
	         {

	        pstmt.setInt(1, answerId);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            String text = rs.getString("text");
	            comments.add(new Comment(text));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return comments;
	}

/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count") == 0;
			}
		}  catch (SQLException e) {
	        return false;
	    }
		return true;
	}
	
//The following codes were the codes that I try to make to implement the "manage invitation " button GUI page.
	
	//This public method is good to go to implement for the GUIManageInvitationpage.java *Lines 317-357*
 
	public boolean invitationCodeExists(String code, String email, String role) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ? AND emailAddress = ? AND role = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, email);
	        pstmt.setString(3, role);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("count") > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	//Adding a code to get the invitation list to hopefully display the list of invitations from using the project
	/*******
	 * <p> Method: getInvitationList </p>
	 * 
	 * <p> Description: Returns a list of invitation codes with their corresponding email addresses and roles. </p>
	 * 
	 * @return a list of invitation details as formatted strings.
	 * 
	 */
	public List<String> getInvitationList() {
	    List<String> invitationList = new ArrayList<>();
	    String query = "SELECT emailAddress, code, role FROM InvitationCodes";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            String email = rs.getString("emailAddress");
	            String code = rs.getString("code");
	            String role = rs.getString("role");
	            invitationList.add(email + " - Code: " + code + " - Role: " + role);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return invitationList;
	}

/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, adminRole, studentRole,"
				+ "reviewerRole, instructorRole, staffRole) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setBoolean(3, user.getAdminRole());
			pstmt.setBoolean(4, user.getStudentRole());
			pstmt.setBoolean(5, user.getReviewerRole());
			pstmt.setBoolean(6, user.getInstructorRole());
			pstmt.setBoolean(7, user.getStaffRole());
			pstmt.executeUpdate();
		}
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "<Select User>" at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
	        return null;
	    }
		System.out.println(userList);
		//I uncommented this line of code in order to get the GUIListUserPage.java to display the list of users implemented in the database of the foundation code
		return userList;
	}

/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user caan login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginStudent(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginStudent(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "studentRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginReviewer(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginReviewer(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "reviewerRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}


	/*******
	 * <p> Method: boolean loginInstructor(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Instructor role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates an instructors user's login credentials.
	public boolean loginInstructor(User user){
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "instructorRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	
	/*******
	 * <p> Method: boolean loginStaff(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Staff role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates an staff user's login credentials.
	public boolean loginStaff(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "staffRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getStudentRole()) numberOfRoles++;
		if (user.getReviewerRole()) numberOfRoles++;
		if (user.getInstructorRole()) numberOfRoles++;
		if (user.getStaffRole()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(String emailAddress, String role) {
		String code = UUID.randomUUID().toString().substring(0, 6); // Generate a random 6-character code
		//Here is where I add some code to satisfy Admin Story 1:
		 LocalDateTime deadline = LocalDateTime.now().plusHours(24);
	 	Timestamp deadlineTimestamp = Timestamp.valueOf(deadline);
		
		String query = "INSERT INTO InvitationCodes (code, emailaddress, role, deadline) VALUES (?, ?, ?, ?)";
		//To make sure that the query implements the deadline pstmt, where gonna add "deadline" and another "?" 
	    
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.setString(2, emailAddress);
	        pstmt.setString(3, role);
		pstmt.setTimestamp(4, deadlineTimestamp);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return code;
	}
//These lines of codes were used to test an Automation test to check if the invitation codes work

//The next set of codes were implemented to satisfy the 3rd-4th Admin stories
	
	//Admin Story 2: Adding methods to set a OTP for the user, check the OTP for validation, and an expiration
	public boolean setOneTimePassword(String username, String otp) {
        String query = "UPDATE userDB SET oneTimePassword = ? WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, otp);
            pstmt.setString(2, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

 //    // Check one-time password and expire it
    public boolean checkOneTimePassword(String username, String otp) {
        String query = "SELECT oneTimePassword FROM userDB WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedOtp = rs.getString("oneTimePassword");
                if (storedOtp != null && storedOtp.equals(otp)) {
                    expireOneTimePassword(username);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Expire one-time password (set it to NULL)
    public boolean expireOneTimePassword(String username) {
        String query = "UPDATE userDB SET oneTimePassword = NULL WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    

    
	
	//Admin Story 3: Setting a method to delete a user
 // Delete user
	//Updated the list to verify that user is deleted
     public boolean deleteUserByUsername(String username) {
        String query = "DELETE FROM userDB WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
	// Number of invitations in the database
	public int getNumberOfInvitations() {
		String query = "SELECT COUNT(*) AS count FROM InvitationCodes";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return 0;
	}
	
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
	// Check to see if an email address is already in the database
	public boolean emailaddressHasBeenUsed(String emailAddress) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE emailAddress = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        ResultSet rs = pstmt.executeQuery();
	        System.out.println(rs);
	        if (rs.next()) {
	            // Mark the code as used
	        	return rs.getInt("count")>0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
	// Obtain the roles associated with an invitation code.
	public String getRoleGivenAnInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("role");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return "";
	}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
	// For a given invitation code, return the associated email address of an empty string
	public String getEmailAddressUsingCode (String code ) {
	    String query = "SELECT emailAddress FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("emailAddress");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return "";
	}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("LastName"); // Return last name role if user exists
	        } //Fixed this bug
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("preferredFirstName"); // Return the preferred first name if user exists
	        } //Fixed this minor typo
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();			
			rs.next();
	    	currentUsername = rs.getString(2);
	    	currentPassword = rs.getString(3);
	    	currentFirstName = rs.getString(4);
	    	currentMiddleName = rs.getString(5);
	    	currentLastName = rs.getString(6);
	    	currentPreferredFirstName = rs.getString(7);
	    	currentEmailAddress = rs.getString(8);
	    	currentAdminRole = rs.getBoolean(9);
	    	currentStudentRole = rs.getBoolean(10);
	    	currentReviewerRole = rs.getBoolean(11);
	    	currentInstructorRole = rs.getBoolean(12);
	    	currentStaffRole = rs.getBoolean(13);
			return true;
	    } catch (SQLException e) {
			return false;
	    }
	}

	//The next set of comment will help implement the Admin story 4 
	//Admin Story 4 that will List and update user accounts
	 // Get all usernames
	    public List<String> getAllUsernames() {
	        List<String> usernames = new ArrayList<>();
	        String query = "SELECT username FROM userDB";
	        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	            ResultSet rs = pstmt.executeQuery();
	            while (rs.next()) {
	                usernames.add(rs.getString("username"));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return usernames;
	    }
	
	//This is where I added all the methods needed for the reviewer role
	/**
    	 * Adds a new review to the database.
	 @param reviewerUsername the user writing the review
    	 * @param targetId the question or answer ID being reviewed
    	 * @param content the content of the review
 	 * @return true if the review was successfully added, false otherwise
 	 */
    public boolean addReview(String reviewerUsername, String targetId, String content) {
    	String sql = "INSERT INTO Reviews (reviewerUsername, targetId, content, timestamp) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
   try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
       pstmt.setString(1, reviewerUsername);
       pstmt.setString(2, targetId);
       pstmt.setString(3, content);
       pstmt.executeUpdate();
       return true;
   } catch (SQLException e) {
       e.printStackTrace();
       return false;
   }
    }

    /**
     * Retrieves all reviews written by the given reviewer.
     * @param username the reviewer's username
     * @return a list of Review objects
     */
    public List<Review> getReviewsByUser(String username) {
    	List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM Reviews WHERE reviewerUsername = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Review review = new Review(
                    rs.getInt("reviewId"),
                    rs.getString("reviewerUsername"),
                    rs.getString("targetId"),
                    rs.getString("content"),
                    rs.getTimestamp("timestamp").toString()
                );
                reviews.add(review);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;

    }

     /**
      * Deletes a review from the database by its ID.
      *
      * @param reviewId the ID of the review to delete
      * @return true if deleted successfully, false otherwise
      */
    public boolean deleteReview(int reviewId) {
        String sql = "DELETE FROM Reviews WHERE reviewId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);  
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	//Ends here
//Secondary edits by me(Ronaldo):
	public boolean submitReviewerRequest(String studentUsername, String message) {
        String sql = "INSERT INTO ReviewerRequests (studentUsername, message) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentUsername);
            pstmt.setString(2, message);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
	//**This method fully supports the Admin Story 5 task** -Ronaldo
	public boolean updateUserRole(String username, String role, String value) {
		if (role.compareTo("Admin") == 0) {
			String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentAdminRole = true;
				else
					currentAdminRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Student") == 0) {
			String query = "UPDATE userDB SET studentRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentStudentRole = true;
				else
					currentStudentRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Reviewer") == 0) {
			String query = "UPDATE userDB SET reviewerRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentReviewerRole = true;
				else
					currentReviewerRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Instructor") == 0) {
			String query = "UPDATE userDB SET instructorRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentInstructorRole = true;
				else
					currentInstructorRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		if (role.compareTo("Staff") == 0) {
			String query = "UPDATE userDB SET staffRole = ? WHERE username = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, value);
				pstmt.setString(2, username);
				pstmt.executeUpdate();
				if (value.compareTo("true") == 0)
					currentStaffRole = true;
				else
					currentStaffRole = false;
				return true;
			} catch (SQLException e) {
				return false;
			}
		}
		return false;
	}
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentStudentRole() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentStudentRole() { return currentStudentRole;};

	
	/*******
	 * <p> Method: boolean getCurrentReviewerRole() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentReviewerRole() { return currentReviewerRole;};

	
	/*******
	 * <p> Method: boolean getCurrentInstructorRole() </p>
	 * 
	 * <p> Description: Get the current user's Instructor role attribute.</p>
	 * 
	 * @return true if this user plays a Instructor role, else false
	 *  
	 */
	public boolean getCurrentInstructorRole() { return currentInstructorRole;};

	
	/*******
	 * <p> Method: boolean getCurrentStaffRole() </p>
	 * 
	 * <p> Description: Get the current user's Staff role attribute.</p>
	 * 
	 * @return true if this user plays a Staff role, else false
	 *  
	 */
	public boolean getCurrentStaffRole() { return currentStaffRole;};
	
	
	/*******
	 * <p> Debugging method</p>
	 * 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}

	//The following two methods will help verify that the admin story 1 works for the code invitations, will verify if there are any codes available in  the db
	//
	/*******
	 * <p>Method: void createInvitationCodesTable() </p>
	 * 
	 * <p> Description: This will create a InvitationCodes table if it doesn't already exist. </p>
	 * 
	 */
	
	public void createInvitationCodesTable() {
		 String createTableSQL = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
		            + "code TEXT PRIMARY KEY, "
		            + "emailaddress TEXT NOT NULL, "
		            + "role TEXT NOT NULL, "
		            + "deadline TIMESTAMP NOT NULL"
		            + ")";

		    try (Statement stmt = connection.createStatement()) {
		        stmt.execute(createTableSQL);

		        //Adding this to validate if the Invitation codes exist and are set in a table
		        System.out.println("[Database] ✅ InvitationCodes table created (or already exists).");

		    } catch (SQLException e) {
			    //Sends an error message if the database was not able to create the InvitationCodes table
		        System.err.println("[Database] ❌ Error creating InvitationCodes table.");
		        e.printStackTrace();
		    }
		}
	public void updateInvitationCodesTable() {
	    String alterTableSQL = "ALTER TABLE InvitationCodes ADD COLUMN deadline TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP";

	    try (Statement stmt = connection.createStatement()) {
	        stmt.execute(alterTableSQL);
	        System.out.println("[Database] ✅ InvitationCodes table updated with deadline column.");
	    } catch (SQLException e) {
	        System.out.println("[Database] ⚠️ InvitationCodes table already has deadline column or error occurred.");
	        //This will verify if the database gives an error bc there is already a table made that cannot be erased so easily
	    }
	}

	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
}
