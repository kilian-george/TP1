module HW1 {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	exports questionAndAnswer;
	
	opens applicationMainMethodClasses to javafx.graphics, javafx.fxml;
}
