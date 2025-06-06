module foundationCodeBaseline {
	requires javafx.controls;
	requires java.sql;
	
	opens applicationMainMethodClasses to javafx.graphics, javafx.fxml;
}
