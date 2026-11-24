package org.example.nabd;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class DoctorDashboard {

    public Scene createScene() {

        Label title = new Label("Doctor Dashboard");

        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        StackPane root = new StackPane(title);
        root.setAlignment(Pos.CENTER);

        return new Scene(root, 900, 600);
    }
}