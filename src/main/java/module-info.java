module org.example.nabd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires jbcrypt;
    requires java.desktop;

    opens org.example.nabd to javafx.fxml;
    exports org.example.nabd;
}