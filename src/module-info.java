module TP1{
    requires javafx.controls;
    requires java.sql;
    requires javafx.graphics;
    exports passwordEvaluationTestbed;
    exports questionAndAnswer;
    
    opens applicationMainMethodClasses to javafx.graphics, javafx.fxml;
}