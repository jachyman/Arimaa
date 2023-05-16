module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.pieces;
    opens com.example.demo.pieces to javafx.fxml;
    exports com.example.demo.board;
    opens com.example.demo.board to javafx.fxml;
}