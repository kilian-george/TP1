package questionAndAnswer;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Comment implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private String text;
private LocalDateTime timestamp;
public Comment(String text) {
	this.text = text;
	this.timestamp = LocalDateTime.now();
}
	public String getText() {
		return text;
	}
	public LocalDateTime gettimestamp() {
		return timestamp;
	}
}
