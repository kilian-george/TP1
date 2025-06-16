module TP1 {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires junit;
	exports questionAndAnswer;
	//exports testing;
	opens applicationMainMethodClasses to javafx.graphics, javafx.fxml;
}
