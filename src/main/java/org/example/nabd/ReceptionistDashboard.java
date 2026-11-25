package org.example.nabd;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;

import java.time.LocalDate;

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

        HospitalOperations operations = new HospitalOperations();

        ObservableList<ObservableList<String>> todayAppointments =
                operations.getAppointments(
                        "PatientName",
                        "",
                        "All Status",
                        LocalDate.now()
                );

        long total = todayAppointments.size();

        long scheduled = todayAppointments.stream()
                .filter(row -> "Scheduled".equals(row.get(6)))
                .count();

        long completed = todayAppointments.stream()
                .filter(row -> "Completed".equals(row.get(6)))
                .count();

        long cancelled = todayAppointments.stream()
                .filter(row -> "Cancelled".equals(row.get(6)))
                .count();

        HBox stats = new HBox(15,

                UIComponents.createStatCard(
                        "Today's Appointments", total, PRIMARY),

                UIComponents.createStatCard(
                        "Scheduled", scheduled, "#2563EB"),

                UIComponents.createStatCard(
                        "Completed", completed, "#16803C"),

                UIComponents.createStatCard(
                        "Cancelled", cancelled, "#B91C1C")
        );

        Button addPatient = UIComponents.createPrimaryButton(
                "+ Add Patient"
        );

        Button addAppointment = UIComponents.createPrimaryButton(
                "+ Add Appointment"
        );

        addPatient.setOnAction(e -> {

            if (UIComponents.showPatientDialog(null) > 0) {
                showDashboard();
            }

        });

        addAppointment.setOnAction(e -> UIComponents.showAppointmentDialog(null, this::showDashboard)
        );

        addPatient.setMaxWidth(Double.MAX_VALUE);
        addAppointment.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(addPatient, Priority.ALWAYS);
        HBox.setHgrow(addAppointment, Priority.ALWAYS);

        addPatient.setMinWidth(0);
        addPatient.setPrefWidth(0);

        addAppointment.setMinWidth(0);
        addAppointment.setPrefWidth(0);

        HBox quickActions = new HBox(15, addPatient, addAppointment);

        Label tableTitle = new Label("Today's Appointments");

        tableTitle.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        String[] columnNames = {
                "Time",
                "Patient",
                "Doctor",
                "Status"
        };

        TableView<ObservableList<String>> todayTable = UIComponents.createTable(columnNames, false);

        ObservableList<ObservableList<String>> tableData =
                FXCollections.observableArrayList();

        for (ObservableList<String> row : todayAppointments) {

            tableData.add(FXCollections.observableArrayList(
                    row.get(4),
                    row.get(1),
                    row.get(2),
                    row.get(6)
            ));
        }

        todayTable.setItems(tableData);

        todayTable.setPrefHeight(300);

        todayTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        todayTable.setPlaceholder(
                new Label("No appointments scheduled for today.")
        );

        VBox appointmentsCard = new VBox(15, tableTitle, todayTable);

        appointmentsCard.setPadding(new Insets(20));

        appointmentsCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 10;"
        );

        VBox dashboardContent = new VBox(20, welcome, description, stats, quickActions,
                appointmentsCard
        );

        setContent(dashboardContent);

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

        TableColumn<ObservableList<String>, Void> scheduleColumn =
                UIComponents.createActionColumn(
                        "View Schedule",
                        row -> showDoctorSchedule(row)
                );

        scheduleColumn.setText("Schedule");

        table.getColumns().add(scheduleColumn);

        UIComponents.setupSearch(
                table, searchBy, searchField, "Doctor"
        );

        VBox page = new VBox(15, title, searchBox, table);
        page.setPadding(new Insets(20));

        setContent(page);
    }

    private void showDoctorSchedule(ObservableList<String> row) {

        String doctorId = row.get(0);
        String doctorName = row.get(1) + " " + row.get(2);

        Dialog<Void> dialog = new Dialog<>();

        dialog.setTitle("Doctor's Schedule");
        dialog.setHeaderText("Dr. " + doctorName);

        DatePicker datePicker = new DatePicker(LocalDate.now());

        HBox dateBox = new HBox(
                10, new Label("Select Date:"), datePicker
        );

        dateBox.setAlignment(Pos.CENTER_LEFT);

        String[] columns = {
                "Time", "Patient", "Status"
        };

        TableView<ObservableList<String>> table =
                UIComponents.createTable(columns, false);

        table.setPrefHeight(350);

        HospitalOperations operations = new HospitalOperations();

        Runnable refresh = () -> {

            table.getItems().clear();

            if (datePicker.getValue() == null) {
                return;
            }

            ObservableList<ObservableList<String>> appointments =
                    operations.getAppointments(
                            "DoctorName",
                            "",
                            "All Status",
                            datePicker.getValue()
                    );

            for (ObservableList<String> appointment : appointments) {

                if (doctorId.equals(appointment.get(8))) {

                    table.getItems().add(
                            FXCollections.observableArrayList(
                                    appointment.get(4),
                                    appointment.get(1),
                                    appointment.get(6)
                            )
                    );
                }
            }
        };

        datePicker.valueProperty().addListener(
                (obs, oldValue, newValue) -> refresh.run()
        );

        table.setPlaceholder(
                new Label("No appointments for this date")
        );

        refresh.run();

        VBox form = new VBox(15, dateBox, table);

        form.setPadding(new Insets(20));

        dialog.getDialogPane().setPrefWidth(600);
        dialog.getDialogPane().setContent(form);

        dialog.getDialogPane().getButtonTypes().add(
                ButtonType.CLOSE
        );

        dialog.showAndWait();
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