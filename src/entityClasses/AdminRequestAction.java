package entityClasses;

public class AdminRequestAction {
    private int requestId;
    private String requesterUsername;
    private String requestDescription;
    private String status;
    private String response;
    private Integer linkedRequestId;

    public AdminRequestAction(int requestId, String requesterUsername, String requestDescription, String status, String response, Integer linkedRequestId) {
        this.requestId = requestId;
        this.requesterUsername = requesterUsername;
        this.requestDescription = requestDescription;
        this.status = status;
        this.response = response;
        this.linkedRequestId = linkedRequestId;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public String getRequestDescription() {
        return requestDescription;
    }

    public String getStatus() {
        return status;
    }

    public String getResponse() {
        return response;
    }

    public Integer getLinkedRequestId() {
        return linkedRequestId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setLinkedRequestId(Integer linkedRequestId) {
        this.linkedRequestId = linkedRequestId;
    }

    @Override
    public String toString() {
        return "RequestID: " + requestId + ", From: " + requesterUsername + ", Description: " + requestDescription + ", Status: " + status;
    }
}
