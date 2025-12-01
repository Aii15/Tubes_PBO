module tubes_pbo {
    requires javafx.controls;
    requires javafx.fxml;

    opens tubes_pbo to javafx.fxml;
    exports tubes_pbo;
}
