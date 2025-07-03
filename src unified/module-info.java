open module HW1 {
	requires javafx.controls;
	requires java.sql;
	requires javafx.graphics;
	
	requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
    requires org.junit.platform.commons;
    requires org.junit.platform.engine;
    requires org.junit.platform.launcher;
    requires org.opentest4j;
    requires org.apiguardian.api;
	requires HW1;

	exports questionAndAnswer;
	
	//opens applicationMainMethodClasses to javafx.graphics, javafx.fxml;
}
