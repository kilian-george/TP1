package reviewerScorecardSettings;

public class ScorecardParameters {
    public int minReviews;
    public double minRating;
    public double reviewCountWeight;
    public double averageRatingWeight;
    public double responseTimeWeight;
    public double feedbackQualityWeight;

    public ScorecardParameters(int minReviews, double minRating, double reviewCountWeight, double averageRatingWeight,
                               double responseTimeWeight, double feedbackQualityWeight) {
        this.minReviews = minReviews;
        this.minRating = minRating;
        this.reviewCountWeight = reviewCountWeight;
        this.averageRatingWeight = averageRatingWeight;
        this.responseTimeWeight = responseTimeWeight;
        this.feedbackQualityWeight = feedbackQualityWeight;
    }

    public ScorecardParameters(double averageRatingWeight, double reviewCountWeight,
                               double responseTimeWeight, double feedbackQualityWeight) {
        this.minReviews = 0;
        this.minRating = 0.0;
        this.reviewCountWeight = reviewCountWeight;
        this.averageRatingWeight = averageRatingWeight;
        this.responseTimeWeight = responseTimeWeight;
        this.feedbackQualityWeight = feedbackQualityWeight;
    }

    public double getAverageRatingWeight() {
        return averageRatingWeight;
    }

    public double getReviewCountWeight() {
        return reviewCountWeight;
    }

    public double getResponseTimeWeight() {
        return responseTimeWeight;
    }

    public double getFeedbackQualityWeight() {
        return feedbackQualityWeight;
    }
}
