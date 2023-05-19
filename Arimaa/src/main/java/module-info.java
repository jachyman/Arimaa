module com.arimaa {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.arimaa to javafx.fxml;
    exports com.arimaa;
    exports com.arimaa.pieces;
    opens com.arimaa.pieces to javafx.fxml;
}