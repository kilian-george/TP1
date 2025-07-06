module TP1 {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	requires junit;
	requires org.junit.jupiter.api;
	exports questionAndAnswer;
	//exports testing;
	opens applicationMainMethodClasses to javafx.graphics, javafx.fxml;
}
