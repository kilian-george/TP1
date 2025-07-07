package reviewerScorecardSettings;

public class ReviewerScorecardSettings {

    private double weightReviewCount;
    private double weightRatingAverage;
    private double weightRecency;

    public ReviewerScorecardSettings(double weightReviewCount, double weightRatingAverage, double weightRecency) {
        this.weightReviewCount = weightReviewCount;
        this.weightRatingAverage = weightRatingAverage;
        this.weightRecency = weightRecency;
    }

    public double getWeightReviewCount() {
        return weightReviewCount;
    }

    public void setWeightReviewCount(double weightReviewCount) {
        this.weightReviewCount = weightReviewCount;
    }

    public double getWeightRatingAverage() {
        return weightRatingAverage;
    }

    public void setWeightRatingAverage(double weightRatingAverage) {
        this.weightRatingAverage = weightRatingAverage;
    }

    public double getWeightRecency() {
        return weightRecency;
    }

    public void setWeightRecency(double weightRecency) {
        this.weightRecency = weightRecency;
    }

    @Override
    public String toString() {
        return "ReviewerScorecardSettings{" +
                "weightReviewCount=" + weightReviewCount +
                ", weightRatingAverage=" + weightRatingAverage +
                ", weightRecency=" + weightRecency +
                '}';
    }
}
