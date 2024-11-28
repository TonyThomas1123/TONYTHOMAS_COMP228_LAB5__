module com.example.tonythomas__comp228__lab___5_____ {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.tonythomas__comp228__lab___5_____ to javafx.fxml;
    exports com.example.tonythomas__comp228__lab___5_____;
}