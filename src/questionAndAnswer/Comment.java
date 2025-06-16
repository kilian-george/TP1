package questionAndAnswer;

import java.time.LocalDateTime;

public class Comment{

private String text;
private LocalDateTime timestamp;
private String name;
public Comment(String name, String text) {
	this.name= name;
	this.text = text;
	this.timestamp = LocalDateTime.now();
}
public String getName() {
	return name;
}
	public String getText() {
		return text;
	}
	public LocalDateTime gettimestamp() {
		return timestamp;
	}
}