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
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setId(int int1) {
		// TODO Auto-generated method stub
	}

	public void setTimestamp(LocalDateTime timestamp2) {
		// TODO Auto-generated method stub	
	}
}
