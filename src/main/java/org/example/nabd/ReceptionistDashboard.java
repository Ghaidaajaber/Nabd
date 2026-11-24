package org.example.nabd;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Node;
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
        createHeader();
        showDashboard();

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

        dashboard.setOnAction(e -> showPage("Dashboard"));
        departments.setOnAction(e -> showPage("Departments"));
        doctors.setOnAction(e -> showPage("Doctors"));
        patients.setOnAction(e -> showPage("Patients"));
        appointments.setOnAction(e -> showPage("Appointments"));

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

    private void createHeader() {

        pageTitle = new Label("Dashboard");

        root.setTop(
                UIComponents.createHeader(pageTitle, "Receptionist")
        );
    }

    private void setContent(Node node) {

        content = new VBox(node);

        root.setCenter(UIComponents.createContent(content));
    }

    private void showPage(String page) {

        pageTitle.setText(page);

        switch (page) {

            case "Dashboard" -> showDashboard();

            case "Departments" -> setContent(new Label("Departments"));

            case "Doctors" -> setContent(new Label("Doctors"));

            case "Patients" -> setContent(new Label("Patients"));

            case "Appointments" -> setContent(new Label("Appointments"));
        }
    }
    private void showDashboard() {

        pageTitle.setText("Dashboard");

        Label welcome = new Label("Welcome, Receptionist!");

        welcome.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        Label description = new Label(
                "Manage patients and appointments from one place."
        );

        description.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #777777;"
        );

        VBox dashboardContent = new VBox(15, welcome, description);

        setContent(dashboardContent);
    }
}