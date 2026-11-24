package org.example.nabd;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ReceptionistDashboard {

    private BorderPane root;
    private Label pageTitle;
    private VBox content;
    private VBox menu;

    private final String PRIMARY = "#075D61";
    private final String BACKGROUND = "#F5F7FA";

    public Scene createScene() {

        root = new BorderPane();

        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        createSidebar();

        return new Scene(root, 1100, 700);
    }

    private void createSidebar() {

        VBox logoBox = UIComponents.createLogo(140, 140);
        logoBox.setPadding(new Insets(0, 0, 30, 5));

        Button dashboard = UIComponents.menuButton("Dashboard");
        Button departments = UIComponents.menuButton("Departments");
        Button doctors = UIComponents.menuButton("Doctors");
        Button patients = UIComponents.menuButton("Patients");
        Button appointments = UIComponents.menuButton("Appointments");

        menu = new VBox(8, dashboard, departments, doctors,
                patients, appointments);

        VBox.setVgrow(menu, Priority.ALWAYS);

        Button logout = UIComponents.menuButton("Logout");

        logout.setOnAction(e -> TestConnection.openLogin());

        VBox sidebar = new VBox(10, logoBox, menu, logout);

        sidebar.setPadding(new Insets(30, 15, 25, 15));
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);

        sidebar.setStyle("-fx-background-color: " + PRIMARY + ";");

        root.setLeft(sidebar);
    }
}