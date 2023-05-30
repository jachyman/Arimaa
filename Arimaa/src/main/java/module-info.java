module com.arimaa {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.arimaa to javafx.fxml;
    exports com.arimaa;
    exports com.arimaa.pieces_src;
    opens com.arimaa.pieces_src to javafx.fxml;
}