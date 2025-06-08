package questionAndAnswer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable{
	private int Id;
	private String questionAsked;
	private boolean resolved= false;
	private String name;
	private String category;
	private LocalDateTime timestamp = LocalDateTime.now();
	private List<Answer> answers = new ArrayList<>();
public Question(String name, String category, String questionAsked, int id) {
	this.name = name;
	this.Id=id;
	this.category = category;
	this.questionAsked=questionAsked;
	this.resolved = false;
}
public Question(String name, String category, String questionAsked) {
	this.name = name;
	this.category=category;
	this.questionAsked = questionAsked;
}

public void setId(int val) {
	Id=val;
}
public int getId() {
	return Id;
}
public void setAnswers(List ansList) {
	answers.addAll(ansList);
}
public LocalDateTime getTime() {
	return timestamp;
}
public void setResolved(boolean res) {
	resolved=res;
}
public boolean isResolved() {
	return resolved;
}
public String getQuestionText() {
	return questionAsked;
}
public String getName() {
	return name;
}
public String getCategory() {
	return category;
}
public List<Answer> getAnswers(){
	return answers;
}
public void addAnswer(Answer answer) {
	answers.add(answer);
}

}