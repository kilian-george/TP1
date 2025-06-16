package questionAndAnswer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*******
 * <p>
 * Class: Question
 * </p>
 * 
 * <p>
 * Description: Represents a question submitted by a user. Each question
 * includes a username, category, content, timestamp, resolution status, and a
 * list of answers.
 * </p>
 */
public class Question implements Serializable {
	private int Id;
	private String questionAsked;
	private boolean resolved = false;
	private String name;
	private String category;
	private LocalDateTime timestamp = LocalDateTime.now();
	private List<Answer> answers = new ArrayList<>();

	/*******
	 * <p>
	 * Constructor: Question
	 * </p>
	 * 
	 * <p>
	 * Description: Constructs a question with full initialization including ID.
	 * </p>
	 * 
	 * @param name          the name of the user who asked the question.
	 * @param category      the category of the question.
	 * @param questionAsked the content of the question.
	 * @param id            the unique identifier of the question.
	 */
	public Question(String name, String category, String questionAsked, int id) {
		this.name = name;
		this.Id = id;
		this.category = category;
		this.questionAsked = questionAsked;
		this.resolved = false;
	}

	/*******
	 * <p>
	 * Constructor: Question
	 * </p>
	 * 
	 * <p>
	 * Description: Constructs a question without specifying an ID.
	 * </p>
	 * 
	 * @param name          the name of the user who asked the question.
	 * @param category      the category of the question.
	 * @param questionAsked the content of the question.
	 */
	public Question(String name, String category, String questionAsked) {
		this.name = name;
		this.category = category;
		this.questionAsked = questionAsked;
	}

	/*******
	 * <p>
	 * Method: setId
	 * </p>
	 * 
	 * <p>
	 * Description: Sets the unique ID for the question.
	 * </p>
	 * 
	 * @param val the ID to set.
	 */
	public void setId(int val) {
		Id = val;
	}

	/*******
	 * <p>
	 * Method: getId
	 * </p>
	 * 
	 * <p>
	 * Description: Gets the unique ID of the question.
	 * </p>
	 * 
	 * @return the ID of the question.
	 */
	public int getId() {
		return Id;
	}

	/*******
	 * <p>
	 * Method: setAnswers
	 * </p>
	 * 
	 * <p>
	 * Description: Adds a list of answers to this question.
	 * </p>
	 * 
	 * @param ansList a list of answers to associate with the question.
	 */
	public void setAnswers(List ansList) {
		answers.addAll(ansList);
	}

	/*******
	 * <p>
	 * Method: getTime
	 * </p>
	 * 
	 * <p>
	 * Description: Gets the timestamp of when the question was created.
	 * </p>
	 * 
	 * @return the {@link LocalDateTime} this question was created.
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
	 * Description: Sets the resolved status of the question.
	 * </p>
	 * 
	 * @param res true if the question is resolved, false otherwise.
	 */
	public void setResolved(boolean res) {
		resolved = res;
	}

	/*******
	 * <p>
	 * Method: isResolved
	 * </p>
	 * 
	 * <p>
	 * Description: Checks whether the question is marked as resolved.
	 * </p>
	 * 
	 * @return true if resolved, false otherwise.
	 */
	public boolean isResolved() {
		return resolved;
	}

	/*******
	 * <p>
	 * Method: getQuestionText
	 * </p>
	 * 
	 * <p>
	 * Description: Gets the text/content of the question.
	 * </p>
	 * 
	 * @return the question text.
	 */
	public String getQuestionText() {
		return questionAsked;
	}

	public void setQuestionText(String question) {
		questionAsked=question;
	}
	
	
	/*******
	 * <p>
	 * Method: getName
	 * </p>
	 * 
	 * <p>
	 * Description: Gets the name of the user who submitted the question.
	 * </p>
	 * 
	 * @return the username associated with the question.
	 */
	public String getName() {
		return name;
	}

	/*******
	 * <p>
	 * Method: getCategory
	 * </p>
	 * 
	 * <p>
	 * Description: Gets the category of the question.
	 * </p>
	 * 
	 * @return the question's category.
	 */
	public String getCategory() {
		return category;
	}

	/*******
	 * <p>
	 * Method: getAnswers
	 * </p>
	 * 
	 * <p>
	 * Description: Gets all answers associated with this question.
	 * </p>
	 * 
	 * @return a list of {@link Answer} objects.
	 */
	public List<Answer> getAnswers() {
		return answers;
	}

	/*******
	 * <p>
	 * Method: addAnswer
	 * </p>
	 * 
	 * <p>
	 * Description: Adds an individual answer to the list of this question's
	 * answers.
	 * </p>
	 * 
	 * @param answer the {@link Answer} to add.
	 */
	public void addAnswer(Answer answer) {
		answers.add(answer);
	}

}