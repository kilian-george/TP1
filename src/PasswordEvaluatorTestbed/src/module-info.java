module PasswordEvaluatorTestbed {
	requires javafx.controls;
	requires java.desktop;
	opens passwordEvaluationTestbed to javafx.graphics, javafx.fxml;
	exports passwordEvaluationTestbed;
}
