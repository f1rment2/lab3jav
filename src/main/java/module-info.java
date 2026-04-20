module com.viewer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.viewer to javafx.fxml;
    exports com.viewer;
}