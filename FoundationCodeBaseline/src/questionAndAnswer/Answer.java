package questionAndAnswer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Answer {
	private String name;
	private String answerText;
	private boolean resolved=false;
	private int score=0;
	private LocalDateTime timestamp = LocalDateTime.now();
	private List<Comment> comments = new ArrayList<>();
public Answer(String name, String answerText, int score) {
	this.name=name;
	this.answerText=answerText;
	this.score=score;
}
public List<Comment> getComments(){
	return comments;
}
public LocalDateTime getTime() {
	return timestamp;
}
public void setResolved(boolean res) {
	resolved = res;
}
public boolean getResolved() {
	return resolved;
}
public int getScore() {
	return score;
}
public void setScore(int num) {
	score+=num;
}
public String getName() {
	return name;
}

public String getAnswerText() {
	return answerText;
}
@override
public String toString() {
	return this.answerText;
}
public void addComment(Comment comment) {
	comments.add(comment);
}
}
