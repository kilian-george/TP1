package questionAndAnswer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*******
 * <p>
 * Class: Answer
 * </p>
 * 
 * <p>
 * Description: Represents an answer to a question in the Q&A system. Each
 * answer includes the author's name, answer text, score, resolution status,
 * timestamp, and a list of comments.
 * </p>
 */
public class Answer {
	private String name;
	private int id;
	private String answerText;
	private boolean resolved = false;
	private int score = 0;
	private LocalDateTime timestamp = LocalDateTime.now();
	private List<Comment> comments = new ArrayList<>();

	/*******
	 * <p>
	 * Constructor: Answer
	 * </p>
	 * 
	 * <p>
	 * Description: Creates a new {@link Answer} with the specified author name,
	 * text, and initial score.
	 * </p>
	 * 
	 * @param name       the username of the person who wrote the answer.
	 * @param answerText the content of the answer.
	 * @param score      the initial score of the answer.
	 */
	public Answer( String name, String answerText, int score, boolean resolved) {
		this.name = name;
		this.resolved=resolved;
		this.answerText = answerText;
		this.score = score;
	}

	/*******
	 * <p>
	 * Method: getComments
	 * </p>
	 * 
	 * <p>
	 * Description: Retrieves all comments associated with this answer.
	 * </p>
	 * 
	 * @return a list of {@link Comment} objects.
	 */
	public List<Comment> getComments() {
		return comments;
	}

	/*******
	 * <p>
	 * Method: getTime
	 * </p>
	 * 
	 * <p>
	 * Description: Returns the timestamp of when the answer was created.
	 * </p>
	 * 
	 * @return a {@link LocalDateTime} representing the creation time.
	 */
	public LocalDateTime getTime() {
		return timestamp;
	}

	/*******
	 * <p>
	 * Method: setResolved
	 * </p>
	 * 
	 * <p>
	 * Description: Sets whether this answer resolves the question.
	 * </p>
	 * 
	 * @param res true if the answer is accepted as the resolution, false otherwise.
	 */
	public void setResolved(boolean res) {
		resolved = res;
	}

	/*******
	 * <p>
	 * Method: getResolved
	 * </p>
	 * 
	 * <p>
	 * Description: Checks if this answer has been marked as the resolution to a
	 * question.
	 * </p>
	 * 
	 * @return true if resolved, false otherwise.
	 */
	public boolean getResolved() {
		return resolved;
	}

	/*******
	 * <p>
	 * Method: getScore
	 * </p>
	 * 
	 * <p>
	 * Description: Returns the current score of the answer.
	 * </p>
	 * 
	 * @return the integer score.
	 */
	public int getScore() {
		return score;
	}

	/*******
	 * <p>
	 * Method: setScore
	 * </p>
	 * 
	 * <p>
	 * Description: Modifies the answer's score by adding the specified value.
	 * </p>
	 * 
	 * @param num the value to add to the current score (can be positive or
	 *            negative).
	 */
	public void setScore(int num) {
		score += num;
	}

	/*******
	 * <p>
	 * Method: getName
	 * </p>
	 * 
	 * <p>
	 * Description: Returns the username of the person who wrote the answer.
	 * </p>
	 * 
	 * @return a string representing the author's name.
	 */
	public String getName() {
		return name;
	}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
	/*******
	 * <p>
	 * Method: getAnswerText
	 * </p>
	 * 
	 * <p>
	 * Description: Returns the text of the answer.
	 * </p>
	 * 
	 * @return a string containing the answer content.
	 */
	public String getAnswerText() {
		return answerText;
	}
	public void setAnswerText(String ans) {
		answerText = ans;
	}
	/*******
	 * <p>
	 * Method: toString
	 * </p>
	 * 
	 * <p>
	 * Description: Returns a string representation of the answer, typically the
	 * answer text itself.
	 * </p>
	 * 
	 * @return the answer's text.
	 */
	@override
	public String toString() {
		return this.answerText;
	}

	/*******
	 * <p>
	 * Method: addComment
	 * </p>
	 * 
	 * <p>
	 * Description: Adds a comment to this answer.
	 * </p>
	 * 
	 * @param comment the {@link Comment} to add.
	 */
	public void addComment(Comment comment) {
		comments.add(comment);
	}
}
