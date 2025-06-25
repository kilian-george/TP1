package privateMessages;
import java.time.LocalDateTime;
public class Message {
	private int id;
	private String sender;
	private String receiver;
	private String message;
	private LocalDateTime timestamp;
	private boolean isRead;
	
	public Message(int id, String sender, String receiver, String message, LocalDateTime timestamp, boolean isRead) {
		this.id=id;
		this.sender=sender;
		this.receiver = receiver;
		this.message= message;
		this.timestamp = timestamp;
		this.isRead = isRead;
	}
	public Message(String sender, String receiver, String message, LocalDateTime timestamp, boolean isread) {
		this(0, sender, receiver, message, timestamp, isread);
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id=id;
	}
	public String getSender() {
		return sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public String getMessage() {
		return message;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean read) {
		this.isRead = read;
	}
}
