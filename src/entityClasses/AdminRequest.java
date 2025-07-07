package entityClasses;

import javafx.beans.property.*;

public class AdminRequest {
    private final IntegerProperty requestId;
    private final StringProperty requestedByUsername;
    private final StringProperty description;
    private final StringProperty adminResponse;
    private final StringProperty status;

    public AdminRequest(int requestId, String requestedByUsername, String description, String adminResponse, String status) {
        this.requestId = new SimpleIntegerProperty(requestId);
        this.requestedByUsername = new SimpleStringProperty(requestedByUsername);
        this.description = new SimpleStringProperty(description);
        this.adminResponse = new SimpleStringProperty(adminResponse);
        this.status = new SimpleStringProperty(status);
    }

    // Getters for JavaFX TableView bindings
    public IntegerProperty requestIdProperty() {
        return requestId;
    }

    public StringProperty requestedByUsernameProperty() {
        return requestedByUsername;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty adminResponseProperty() {
        return adminResponse;
    }

    public StringProperty statusProperty() {
        return status;
    }

    // Alias for GUI usage
    public StringProperty createdByProperty() {
        return requestedByUsername;
    }

    // Standard getters
    public int getRequestId() {
        return requestId.get();
    }

    public String getRequestedByUsername() {
        return requestedByUsername.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getAdminResponse() {
        return adminResponse.get();
    }

    public String getStatus() {
        return status.get();
    }
}
