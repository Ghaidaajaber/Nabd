package org.example.nabd;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
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

            case "Departments" -> showDepartments();
            case "Doctors" -> showDoctors();

            case "Patients" -> showPatients();
            case "Appointments" -> showAppointments();        }
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

    private void showDepartments() {

        pageTitle.setText("Departments");

        Label title = new Label("Departments");

        // Search
        ComboBox<String> searchBy = new ComboBox<>();

        searchBy.getItems().addAll(
                "DepartmentID",
                "DepartmentName",
                "Location",
                "Phone"
        );

        searchBy.setValue("DepartmentName");

        TextField searchField = new TextField();
        searchField.setPromptText("Search departments...");

        HBox searchBox = new HBox(10, searchBy, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);



        String[] columnNames = {
                "Department ID",
                "Department Name",
                "Location",
                "Phone"
        };

        TableView<ObservableList<String>> table =
                UIComponents.createTable(columnNames, false);



        // Load departments
        HospitalOperations operations = new HospitalOperations();

        table.setItems(operations.getData("Department"));

        UIComponents.setupSearch(
                table, searchBy, searchField, "Department"
        );

        VBox page = new VBox(15, title, searchBox, table);
        page.setPadding(new Insets(20));

        setContent(page);
    }

    private void showDoctors() {

        pageTitle.setText("Doctors");

        Label title = new Label("Doctors");

        ComboBox<String> searchBy = new ComboBox<>();

        searchBy.getItems().addAll(
                "DoctorID",
                "Name",
                "Specialization",
                "Phone",
                "Email",
                "DepartmentID"
        );

        searchBy.setValue("Name");

        TextField searchField = new TextField();
        searchField.setPromptText("Search doctors...");

        HBox searchBox = new HBox(10, searchBy, searchField);

        String[] columnNames = {
                "Doctor ID",
                "Full Name",
                "Specialization",
                "Phone",
                "Email",
                "Department ID"
        };

        TableView<ObservableList<String>> table =
                UIComponents.createTable(columnNames, true);

        HospitalOperations operations = new HospitalOperations();

        table.setItems(operations.getData("Doctor"));

        UIComponents.setupSearch(
                table, searchBy, searchField, "Doctor"
        );

        VBox page = new VBox(15, title, searchBox, table);
        page.setPadding(new Insets(20));

        setContent(page);
    }

    private void showPatients() {

        pageTitle.setText("Patients");

        Label title = new Label("Patients");

        Button addButton = new Button("+ Add Patient");

        addButton.setOnAction(e -> {
            if (UIComponents.showPatientDialog(null) > 0) {
                showPatients();
            }
        });

        HBox header = new HBox(15, title, addButton);
        header.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(1, spacer);

        ComboBox<String> searchBy = new ComboBox<>();

        searchBy.getItems().addAll(
                "PatientID",
                "Name",
                "DateOfBirth",
                "Gender",
                "Phone",
                "Email",
                "Address",
                "BloodType"
        );

        searchBy.setValue("Name");

        TextField searchField = new TextField();
        searchField.setPromptText("Search patients...");

        HBox searchBox = new HBox(10, searchBy, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        String[] columnNames = {
                "Patient ID",
                "Full Name",
                "Date of Birth",
                "Gender",
                "Phone",
                "Email",
                "Address",
                "Blood Type"
        };

        TableView<ObservableList<String>> table =
                UIComponents.createTable(columnNames, true);

        table.getColumns().add(
                UIComponents.createEditColumn(row -> {

                    if (UIComponents.showPatientDialog(row) > 0) {
                        showPatients();
                    }

                })
        );

        HospitalOperations operations = new HospitalOperations();

        table.setItems(operations.getData("Patient"));

        UIComponents.setupSearch(
                table, searchBy, searchField, "Patient"
        );

        VBox page = new VBox(15, header, searchBox, table);
        page.setPadding(new Insets(20));

        setContent(page);
    }

    private void showAppointments() {

        pageTitle.setText("Appointments");

        Label title = new Label("Appointments");

        Button addButton = new Button("+ Add Appointment");

        addButton.setOnAction(e ->
                UIComponents.showAppointmentDialog(
                        null, this::showAppointments
                )
        );

        HBox header = new HBox(15, title, addButton);
        header.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(1, spacer);

        // Search
        ComboBox<String> searchBy = new ComboBox<>();

        searchBy.getItems().addAll(
                "AppointmentID",
                "PatientName",
                "DoctorName",
                "Reason"
        );

        searchBy.setValue("PatientName");

        TextField searchField = new TextField();
        searchField.setPromptText("Search appointments...");

        // Status Filter
        ComboBox<String> statusFilter = new ComboBox<>();

        statusFilter.getItems().addAll(
                "All Status",
                "Scheduled",
                "Completed",
                "Cancelled"
        );

        statusFilter.setValue("All Status");

        // Date Filter
        DatePicker dateFilter = new DatePicker();
        dateFilter.setPromptText("Date");

        Button clearButton = new Button("Clear Filters");

        HBox searchBox = new HBox(
                10, searchBy, searchField,
                statusFilter, dateFilter, clearButton
        );

        searchBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // Table
        String[] columnNames = {
                "ID",
                "Patient",
                "Doctor",
                "Date",
                "Time",
                "Reason",
                "Status"
        };

        TableView<ObservableList<String>> table =
                UIComponents.createTable(columnNames, false);

        table.getColumns().add(
                UIComponents.createEditColumn(row ->
                        UIComponents.showAppointmentDialog(
                                row, this::showAppointments
                        )
                )
        );

        // Load appointments
        HospitalOperations operations = new HospitalOperations();

        Runnable refresh = () -> table.setItems(
                operations.getAppointments(
                        searchBy.getValue(),
                        searchField.getText().trim(),
                        statusFilter.getValue(),
                        dateFilter.getValue()
                )
        );

        searchField.textProperty().addListener(
                (obs, oldValue, newValue) -> refresh.run()
        );

        searchBy.setOnAction(e -> refresh.run());
        statusFilter.setOnAction(e -> refresh.run());
        dateFilter.setOnAction(e -> refresh.run());

        clearButton.setOnAction(e -> {

            searchBy.setValue("PatientName");
            searchField.clear();
            statusFilter.setValue("All Status");
            dateFilter.setValue(null);

            refresh.run();
        });

        refresh.run();

        VBox page = new VBox(15, header, searchBox, table);
        page.setPadding(new Insets(20));

        setContent(page);
    }
}