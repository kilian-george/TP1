package testing;

import static org.junit.Assert.*;

import java.util.List;

import questionAndAnswer.Question;
import questionAndAnswer.QuestionList;
import userNameRecognizerTestbed.UserNameRecognizer;
import questionAndAnswer.Answer;
import questionAndAnswer.Comment;


import org.junit.Test;

import passwordEvaluationTestbed.PasswordEvaluator;

public class CoreTests {
	
	

	@Test
	public void testQuestionAndAnswer() {
		String name = "Bob";
		String category = "Homework";
		String content = "What is the purpose of the third user story? It seems like it's redundant?";
		int id = 1;
		Question questionOne = new Question(name, category, content, id);
		assertFalse("Asserts that questionOne is not resolved", questionOne.isResolved());
		questionOne.setResolved(true);;
		assertTrue("Asserts that questionOne is resolved", questionOne.isResolved());
		
		assertEquals("Asserts that the content is properly assigned", questionOne.getQuestionText(), content);
		
		assertEquals("Asserts that the name was properly assigned", questionOne.getName(), name);
		
		assertEquals("Asserts that the category is properly assigned", questionOne.getCategory(), category);
		
		String nameTwo = "Cob";
		String ansText = "The third user story expands on the user experience surrounding Q&A";
		int score = 0;
		
		Answer answerOne = new Answer(nameTwo, ansText, score, false);
		
		questionOne.addAnswer(answerOne);
		
		assertEquals("Ensures questions can store answers and then retrieve them", questionOne.getAnswers().getFirst(), answerOne);
		
		
		assertFalse("Asserts that answer does not resolve", answerOne.getResolved());
		answerOne.setResolved(true);
		assertTrue("Asserts that answer does resolve", answerOne.getResolved());
		
		assertEquals("Asserts score as entered", score, answerOne.getScore());
		answerOne.setScore(2);
		assertEquals("Asserts setScore works as intended", 2, answerOne.getScore());
		
		assertEquals("Asserts answer text is properly stored", ansText, answerOne.getAnswerText());
		
		String commentText = "Wow I never thought of it that way!";
		
		Comment commentOne = new Comment("Tom Bombadil", commentText);
		
		assertEquals("Ensures comment stores input correctly", commentText, commentOne.getText());
		
		answerOne.addComment(commentOne);
		List<Comment> comments = answerOne.getComments();
		
		assertEquals("Ensures comments are stored on answers", comments.get(0), commentOne);
		
		String q2Name = "Rob";
		String q3Name = "Lob";
		String q4Name = "Fob";
		
		Question questionTwo = new Question(q2Name, category, content, 2);
		Question questionThree = new Question(q3Name, category, content, 3);
		Question questionFour = new Question(q4Name, category, content, 4);
		
		questionTwo.setResolved(true);
		
		QuestionList qList = new QuestionList();
		
		qList.addQuestion(questionOne);
		qList.addQuestion(questionTwo);
		qList.addQuestion(questionThree);
		qList.addQuestion(questionFour);
		
		assertEquals("Ensures that QuestionList can sort by resolved", qList.getUnresolvedQuestions().size(), 2);
		assertEquals("Ensures that QuestionList can sort by name", qList.getQuestionByName(q4Name).size(), 1);
	
		
	}
	
	
	@Test
	public void passwordAndUsernameTest() {
		String correct = "!GoodPassord12";
		String incorrect = "Nope!";
		String incorrectTwo = "Nope1";
		String incorrectThree = "Nope!1";
		String goodUser = "Admin.1";
		String badUser = "bob";
		String badUserTwo = "4lice";
		String badUserThree = "Lee--Lo";
		
		assertEquals("Shows an empty string return for no error", PasswordEvaluator.evaluatePassword(correct), "");
		assertNotEquals("Shows the string is not empty and therefore has an error", "", PasswordEvaluator.evaluatePassword(incorrect));
		assertNotEquals("Shows the string is not empty and therefore has an error", "", PasswordEvaluator.evaluatePassword(incorrectTwo));
		assertNotEquals("Shows the string is not empty and therefore has an error", "", PasswordEvaluator.evaluatePassword(incorrectThree));
		
		assertEquals("Shows an empty string for valid username", "", UserNameRecognizer.checkForValidUserName(goodUser));
		assertNotEquals("Shows an nonempty string for invalid username", "", UserNameRecognizer.checkForValidUserName(badUser));
		assertNotEquals("Shows an nonempty string for invalid username", "", UserNameRecognizer.checkForValidUserName(badUserTwo));
		assertNotEquals("Shows an nonempty string for invalid username", "", UserNameRecognizer.checkForValidUserName(badUserThree));
		
	}
	@Test
	public void adminTest() {
		
	}
	

}









































