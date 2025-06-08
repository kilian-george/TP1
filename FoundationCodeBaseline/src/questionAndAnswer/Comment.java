package questionAndAnswer;

import java.time.LocalDateTime;

public class Comment{

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
