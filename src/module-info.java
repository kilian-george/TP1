module TP1{
    requires javafx.controls;
    requires java.sql;
    requires javafx.graphics;
    exports passwordEvaluationTestbed;
    exports questionAndAnswer;
    requires org.junit.jupiter.api;
    
    opens applicationMainMethodClasses to javafx.graphics, javafx.fxml;
    opens test to org.junit.jupiter.api;
}
