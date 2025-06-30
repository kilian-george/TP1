package entityClasses;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Review {
    private SimpleIntegerProperty reviewId;
    private SimpleStringProperty reviewerUsername;
    private SimpleStringProperty targetId;
    private SimpleStringProperty content;
    private SimpleStringProperty timestamp;

    public Review(int reviewId, String reviewerUsername, String targetId, String content, String timestamp) {
        this.reviewId = new SimpleIntegerProperty(reviewId);
        this.reviewerUsername = new SimpleStringProperty(reviewerUsername);
        this.targetId = new SimpleStringProperty(targetId);
        this.content = new SimpleStringProperty(content);
        this.timestamp = new SimpleStringProperty(timestamp);
    }

    public int getReviewID() {
        return reviewId.get();
    }

    public String getTargetId() {
        return targetId.get();
    }

    public String getContent() {
        return content.get();
    }

    public String getReviewerUsername() {
        return reviewerUsername.get();
    }

    public String getTimestamp() {
        return timestamp.get();
    }

    public SimpleStringProperty targetIdProperty() {
        return targetId;
    }

    public SimpleStringProperty contentProperty() {
        return content;
    }

    public SimpleIntegerProperty reviewIdProperty() {
        return reviewId;
    }
    
    @Override
    public String toString() {
        return "Review{" +
                "reviewId=" + reviewId +
                ", reviewerUsername='" + reviewerUsername + '\'' +
                ", targetId='" + targetId + '\'' +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}