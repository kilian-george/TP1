package questionAndAnswer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionList implements Serializable{
	private static final long serialVersionUID = 1L;
private List<Question> questions = new ArrayList<>();
public void addQuestion(Question input) {
	questions.add(input);
}
public List<Question> getQuestions(){
	return questions;
}
public List<Question> getQuestionByName(String name){
	return questions.stream().filter(q->q.getName().equals(name)).collect(Collectors.toList());
}
public List<Question> getUnresolvedQuestions(){
	return questions.stream().filter(q->!q.isResolved()).collect(Collectors.toList());
}
public List<Question> getResolvedQuestions(){
	return questions.stream().filter(q->q.isResolved()).collect(Collectors.toList());
}
public void  deleteQuestion(Question question) {
	questions.remove(question);
}
}
