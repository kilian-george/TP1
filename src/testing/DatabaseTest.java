package testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.util.*;
import databaseClasses.Database;
import entityClasses.User;
import questionAndAnswer.Answer;
import questionAndAnswer.Question;

public class DatabaseTest {
    private Database db;

    @BeforeEach
    public void setup() throws SQLException {
        db = new Database();  // Initialize here
        db.connectToDatabase();
      //  db.dropDatabase();
        db.createTables();
    }

   @Test
   public void testHiddenAnswers() {
	   User student = new User("Stud.1", "pswd", false, true, false, false, false);
	   User studentTwo = new User("Stud.2", "pswd", false, true, false, false, false);
	   User staff = new User("Staff.1", "pswd", false, false, false, false, true);
	   
	   try {
		   db.register(staff);
		   db.register(student);
		   db.register(studentTwo);
	   } catch (SQLException e) {}
	   
	   Question q = new Question("Stud.1", "Code", "How compile??");
	   
	   Answer ans = new Answer("Stud.2", "Terminal!", 0, false);
	   
	   Answer ansTwo = new Answer("Staff.1", "javac", 0, true);
	   
	   q.addAnswer(ans);
	   q.addAnswer(ansTwo);
	   
	   List<Answer> sortAnswers = new ArrayList<>(q.getAnswers());
	   
	   // sets currentUser info
	   db.getUserAccountDetails("Stud.1");
	   
		for (int i = 0; i < sortAnswers.size(); i++) {
			if(sortAnswers.get(i).getIsPrivate()) {
				// 
				if (q.getName() != "Stud.1" && !(db.getCurrentInstructorRole() || db.getCurrentReviewerRole() || db.getCurrentStaffRole())) {
					sortAnswers.remove(i);
				}
			}
		}
		
		assertTrue(sortAnswers.contains(ans), "asking student, contains public answer");
		assertTrue(sortAnswers.contains(ansTwo), "asking student, contains private answer");
		
		db.getUserAccountDetails("Staff.1");
		
		for (int i = 0; i < sortAnswers.size(); i++) {
			if(sortAnswers.get(i).getIsPrivate()) {
				// 
				if (q.getName() != db.getCurrentUsername() && !(db.getCurrentInstructorRole() || db.getCurrentReviewerRole() || db.getCurrentStaffRole())) {
					sortAnswers.remove(i);
				}
			}
		}
		
		assertTrue(sortAnswers.contains(ans), "staff, contains public answer");
		assertTrue(sortAnswers.contains(ansTwo), "staff, contains private answer");
		
		db.getUserAccountDetails("Stud.2");
		
		for (int i = 0; i < sortAnswers.size(); i++) {
			if(sortAnswers.get(i).getIsPrivate()) {
				// 
				if (q.getName() != db.getCurrentUsername() && !(db.getCurrentInstructorRole() || db.getCurrentReviewerRole() || db.getCurrentStaffRole())) {
					sortAnswers.remove(i);
				}
			}
		}
		
		assertTrue(sortAnswers.contains(ans), "other student, contains public answer");
		assertFalse(sortAnswers.contains(ansTwo), "other student, does not contain private answer");
		
		assertTrue(sortAnswers.contains(ansTwo), "Intentional fail");
	   
			   
   }
    
}