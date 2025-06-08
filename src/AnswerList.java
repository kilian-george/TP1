package questionAndAnswer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AnswerList implements Serializable{
	//creates a list of answers to fill out the text that jfx will pull from 
	// TODO store the list permanently on initial startup onto something external for now
	private List<Answer> answerList = new ArrayList<>();
	public void addAnswer(Answer ans) {
		answerList.add(ans);
	}
	public List<Answer> getAllAnswers(){
		return answerList;
	}
	public List<Answer> getAnswers(String questionName){
		return answerList.stream().filter(a->a.getName().equals(questionName)).collect(Collectors.toList());
	}
}
