package questionAndAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*******
 * <p>
 * Class: QuestionList
 * </p>
 * 
 * <p>
 * Description: Maintains an in-memory list of {@link Question} objects.
 * Provides utility methods for adding, removing, and filtering questions.
 * </p>
 */
public class QuestionList {
	private List<Question> questions = new ArrayList<>();

	/*******
	 * <p>
	 * Constructor: QuestionList
	 * </p>
	 * 
	 * <p>
	 * Description: Initializes an empty question list.
	 * </p>
	 */
	public QuestionList() {
		this.questions = new ArrayList<>();
	}

	/*******
	 * <p>
	 * Constructor: QuestionList
	 * </p>
	 * 
	 * <p>
	 * Description: Initializes the question list with an existing list of
	 * {@link Question} objects.
	 * </p>
	 * 
	 * @param questions the list of questions to populate this list with.
	 */
	public QuestionList(List<Question> questions) {
		this.questions = new ArrayList<>(questions);
	}

	/*******
	 * <p>
	 * Method: addQuestion
	 * </p>
	 * 
	 * <p>
	 * Description: Adds a new {@link Question} to the list.
	 * </p>
	 * 
	 * @param input the {@link Question} to add.
	 */
	public void addQuestion(Question input) {
		questions.add(input);
	}

	/*******
	 * <p>
	 * Method: getQuestions
	 * </p>
	 * 
	 * <p>
	 * Description: Retrieves all questions in the list.
	 * </p>
	 * 
	 * @return a list of {@link Question} objects.
	 */
	public List<Question> getQuestions() {
		return questions;
	}

	/*******
	 * <p>
	 * Method: getQuestionByName
	 * </p>
	 * 
	 * <p>
	 * Description: Retrieves all questions asked by a specific user.
	 * </p>
	 * 
	 * @param name the username to filter by.
	 * @return a list of {@link Question} objects asked by the given user.
	 */
	public List<Question> getQuestionByName(String name) {
		return questions.stream().filter(q -> q.getName().equals(name)).collect(Collectors.toList());
	}

	/*******
	 * <p>
	 * Method: getUnresolvedQuestions
	 * </p>
	 * 
	 * <p>
	 * Description: Retrieves all questions in the list that are not resolved.
	 * </p>
	 * 
	 * @return a list of unresolved {@link Question} objects.
	 */
	public List<Question> getUnresolvedQuestions() {
		return questions.stream().filter(q -> !q.isResolved()).collect(Collectors.toList());
	}

	/*******
	 * <p>
	 * Method: getResolvedQuestions
	 * </p>
	 * 
	 * <p>
	 * Description: Retrieves all questions in the list that are resolved.
	 * </p>
	 * 
	 * @return a list of resolved {@link Question} objects.
	 */
	public List<Question> getResolvedQuestions() {
		return questions.stream().filter(q -> q.isResolved()).collect(Collectors.toList());
	}

	/*******
	 * <p>
	 * Method: deleteQuestion
	 * </p>
	 * 
	 * <p>
	 * Description: Removes a specific {@link Question} from the list.
	 * </p>
	 * 
	 * @param question the {@link Question} object to remove.
	 */
	public void deleteQuestion(Question question) {
		questions.remove(question);
	}
}
