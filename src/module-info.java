module TP1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;           
    requires java.sql;

    requires org.junit.jupiter.api; 
    exports applicationMainMethodClasses;
    exports databaseClasses;
    exports entityClasses;
    exports guiPageClasses;

    opens guiPageClasses to javafx.fxml;
}
