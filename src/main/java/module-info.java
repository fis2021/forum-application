module org.fis2021 {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.fis2021 to javafx.fxml;
    exports org.fis2021;
}