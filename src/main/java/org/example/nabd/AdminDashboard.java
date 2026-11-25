package org.example.nabd;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.function.Consumer;

public class AdminDashboard {

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

    // ================= SIDEBAR =================



    private void createSidebar() {

        VBox logoBox = UIComponents.createLogo(110, 110);
        logoBox.setPadding(new Insets(0, 0, 10, 5));

        Button dashboard = UIComponents.menuButton("Dashboard");
        Button departments = UIComponents.menuButton("Departments");
        Button doctors = UIComponents.menuButton("Doctors");
        Button patients = UIComponents.menuButton("Patients");
        Button appointments = UIComponents.menuButton("Appointments");
        Button medicalRecords = UIComponents.menuButton("Medical Records");
        Button users = UIComponents.menuButton("Users");

        dashboard.setOnAction(e -> showPage("Dashboard"));
        departments.setOnAction(e -> showPage("Departments"));
        doctors.setOnAction(e -> showPage("Doctors"));
        patients.setOnAction(e -> showPage("Patients"));
        appointments.setOnAction(e -> showPage("Appointments"));
        medicalRecords.setOnAction(e -> showPage("Medical Records"));
        users.setOnAction(e -> showPage("Users"));

        menu = new VBox(5, dashboard, departments, doctors, patients, appointments, medicalRecords, users);

        VBox.setVgrow(menu, Priority.ALWAYS);

        Button logout = UIComponents.menuButton("Logout");

        logout.setOnAction(e -> TestConnection.openLogin());

        VBox sidebar = new VBox(10, logoBox, menu, logout);

        sidebar.setPadding(new Insets(15, 15, 15, 15));
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);

        sidebar.setStyle("-fx-background-color: " + PRIMARY + ";");

        root.setLeft(sidebar);
    }

    // ================= HEADER =================

    private void createHeader() {
        pageTitle = new Label("Dashboard");
        root.setTop(UIComponents.createHeader(pageTitle, "Administrator"));
    }

    // ================= CONTENT =================

    private void setContent(Node node) {
        content = new VBox(node);
        root.setCenter(UIComponents.createContent(content));
    }

    // ================= DASHBOARD =================

    private void showDashboard() {

        pageTitle.setText("Dashboard");

        HospitalOperations operations = new HospitalOperations();

        // Welcome Section

        Label welcome = new Label("Welcome, Administrator!");

        welcome.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        Label description = new Label(
                "Manage your hospital operations from one place."
        );

        description.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #777777;"
        );

        VBox welcomeSection = new VBox(5, welcome, description);

        // Statistics Cards

        HBox firstRow = new HBox(15,

                dashboardCard(
                        "Total Patients",
                        operations.getCount("Patient"),
                        "Patients"
                ),

                dashboardCard(
                        "Total Doctors",
                        operations.getCount("Doctor"),
                        "Doctors"
                )
        );

        HBox secondRow = new HBox(15,

                dashboardCard(
                        "Departments",
                        operations.getCount("Department"),
                        "Departments"
                ),

                dashboardCard(
                        "Appointments",
                        operations.getCount("Appointment"),
                        "Appointments"
                )
        );

        firstRow.setPrefHeight(100);
        firstRow.setMinHeight(80);
        firstRow.setMaxHeight(Double.MAX_VALUE);

        secondRow.setPrefHeight(100);
        secondRow.setMinHeight(80);
        secondRow.setMaxHeight(Double.MAX_VALUE);

        VBox statistics = new VBox(12, firstRow, secondRow);

        statistics.setMaxHeight(Double.MAX_VALUE);

        VBox.setVgrow(firstRow, Priority.ALWAYS);
        VBox.setVgrow(secondRow, Priority.ALWAYS);

        // Quick Actions

        Label quickActionsTitle = new Label("Quick Actions");

        quickActionsTitle.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        Button addPatient = quickActionButton(
                "+ Add Patient",
                () -> {
                    UIComponents.showPatientDialog(null);
                    showDashboard();
                }
        );

        Button addDoctor = quickActionButton(
                "+ Add Doctor",
                () -> {
                    showAddDoctorDialog();
                    showDashboard();
                }
        );

        Button addAppointment = quickActionButton(
                "+ Add Appointment",
                () -> {
                    UIComponents.showAppointmentDialog(null, this::showAppointments);
                    showDashboard();
                }
        );

        Button addUser = quickActionButton(
                "+ Add User",
                () -> {
                    showUserDialog(null);
                    showDashboard();
                }
        );

        HBox firstActionsRow = new HBox(
                15, addPatient, addDoctor
        );

        HBox secondActionsRow = new HBox(
                15, addAppointment, addUser
        );

        VBox quickActions = new VBox(
                8,
                quickActionsTitle,
                firstActionsRow,
                secondActionsRow
        );

        // Notifications

        Label notificationsTitle = new Label("Notifications");

        notificationsTitle.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        int requestsCount = operations.getPasswordRequests().size();

        Label requestsLabel = new Label("Pending Password Requests");

        requestsLabel.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        Label countLabel = new Label(String.valueOf(requestsCount));

        countLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button viewRequests = new Button("View Requests");

        viewRequests.setOnAction(e -> {
            showPasswordRequests();
            showDashboard();
        });

        HBox notificationCard = new HBox(
                15,
                requestsLabel,
                spacer,
                countLabel,
                viewRequests
        );

        notificationCard.setAlignment(Pos.CENTER_LEFT);

        notificationCard.setPadding(new Insets(12));

        notificationCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 10;"
        );

        VBox notifications = new VBox(
                8,
                notificationsTitle,
                notificationCard
        );

        // Dashboard Content

        VBox dashboardContent = new VBox(
                15,
                welcomeSection,
                statistics,
                quickActions,
                notifications
        );

        dashboardContent.setPadding(new Insets(20));

        dashboardContent.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );

        VBox.setVgrow(statistics, Priority.ALWAYS);

        root.setCenter(dashboardContent);
    }
    // ================= DASHBOARD CARD =================

    // ================= PAGES =================

    public void showPage(String name) {

        if (name.equals("Dashboard")) {
            showDashboard();
            return;
        }
        else if (name.equals("Departments")) {
            showDepartments();
            return;
        }
        else if (name.equals("Doctors")) {
            showDoctors();
            return;
        }
        else if (name.equals("Patients")) {
            showPatients();
            return;
        }
        else if (name.equals("Appointments")) {
            showAppointments();
            return;
        }
        else if (name.equals("Medical Records")) {
            showMedicalRecords();
            return;
        }
        else if(name.equals("Users")){
            showUsers();
            return;
        }

        Label title = new Label("Manage " + name);

        title.setStyle("-fx-font-size: 26px;" + "-fx-font-weight: bold;" + "-fx-text-fill: " + PRIMARY + ";");

        Label description = new Label("This section will display and manage " + name.toLowerCase() + ".");

        description.setStyle("-fx-font-size: 14px;" + "-fx-text-fill: #777777;");

        VBox page = new VBox(15, title, description);

        page.setPadding(new Insets(25));
        page.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;"
        );

        setContent(page);
    }


    private void showDepartments() {

        pageTitle.setText("Departments");
        Label title = new Label("Departments");
        Button addButton = new Button("+ Add Department");
        addButton.setOnAction(e -> showAddDepartmentDialog());

        HBox header = new HBox(15, title, addButton);
        header.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(1, spacer);

        // Search
        ComboBox<String> searchBy = new ComboBox<>();

        searchBy.getItems().addAll("DepartmentID", "DepartmentName", "Location", "Phone");

        searchBy.setValue("DepartmentName");

        TextField searchField = new TextField();
        searchField.setPromptText("Search departments...");

        HBox searchBox = new HBox(10, searchBy, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Table
        String[] columnNames = {"Department ID", "Department Name", "Location", "Phone"};
        TableView<ObservableList<String>> table = UIComponents.createTable(columnNames, false);


        TableColumn<ObservableList<String>, Void> actionsColumn = createActionsColumn(
                row -> showEditDepartmentDialog(row),
                row -> confirmDelete("Department", "DepartmentID", Integer.parseInt(row.get(0)), "Departments"));


        table.getColumns().add(actionsColumn);

        // Load departments
        HospitalOperations operations = new HospitalOperations();

        table.setItems(operations.getData("Department"));
        UIComponents.setupSearch(table, searchBy, searchField, "Department");
        VBox page = new VBox(15, header, searchBox, table);
        page.setPadding(new Insets(20));

        setContent(page);
    }


    private void showAddDepartmentDialog() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Department");
        dialog.setHeaderText("Enter Department Information");

        TextField nameField = new TextField();
        nameField.setPromptText("Department Name");

        TextField locationField = new TextField();
        locationField.setPromptText("Location");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone: 02-*******");

        Label name1 = new Label("Department Name");
        Label location1 = new Label("Location");
        Label phone1 = new Label("Phone");

        name1.setPrefWidth(130);
        location1.setPrefWidth(130);
        phone1.setPrefWidth(130);

        nameField.setPrefWidth(230);
        locationField.setPrefWidth(230);
        phoneField.setPrefWidth(230);

        HBox n = new HBox(15, name1, nameField);
        HBox l = new HBox(15, location1, locationField);
        HBox p = new HBox(15, phone1, phoneField);

        n.setAlignment(Pos.CENTER_LEFT);
        l.setAlignment(Pos.CENTER_LEFT);
        p.setAlignment(Pos.CENTER_LEFT);

        VBox form = new VBox(20, n, l, p);
        form.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(form);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        // منع Enter من تشغيل Save تلقائيًا
        Button save = (Button) dialog.getDialogPane().lookupButton(saveButton);
        // التنقل بين الحقول باستخدام Enter
        save.setDefaultButton(false);
        UIComponents.setupKeyboardNavigation(dialog, nameField, locationField, phoneField, save);



        // التحقق والحفظ
        save.addEventFilter(ActionEvent.ACTION, event -> {

            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || location.isEmpty() || phone.isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
                event.consume();
                return;
            }

            if (!phone.matches("\\d{10}")) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Phone number must contain exactly 10 digits.");
                event.consume();
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Addition");
            confirmation.setHeaderText("Add Department?");
            confirmation.setContentText("Are you sure you want to add " + name + "?");

            if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                event.consume();
                return;
            }

            HospitalOperations operations = new HospitalOperations();

            if (operations.addDepartment(name, location, phone)) {
                UIComponents.showAlert(Alert.AlertType.INFORMATION, "Department added successfully.");
                showDepartments();

            } else {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Failed to add department.");
                event.consume();
            }
        });

        dialog.showAndWait();
    }


    private void showEditDepartmentDialog(ObservableList<String> row) {

        int id = Integer.parseInt(row.get(0));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Department");
        dialog.setHeaderText("Edit Department Information");

        TextField nameField = new TextField(row.get(1));
        TextField locationField = new TextField(row.get(2));
        TextField phoneField = new TextField(row.get(3));

        Label nameLabel = new Label("Department Name");
        Label locationLabel = new Label("Location");
        Label phoneLabel = new Label("Phone");

        nameLabel.setPrefWidth(130);
        locationLabel.setPrefWidth(130);
        phoneLabel.setPrefWidth(130);

        nameField.setPrefWidth(230);
        locationField.setPrefWidth(230);
        phoneField.setPrefWidth(230);

        HBox n = new HBox(15, nameLabel, nameField);
        HBox l = new HBox(15, locationLabel, locationField);
        HBox p = new HBox(15, phoneLabel, phoneField);

        n.setAlignment(Pos.CENTER_LEFT);
        l.setAlignment(Pos.CENTER_LEFT);
        p.setAlignment(Pos.CENTER_LEFT);

        VBox form = new VBox(20, n, l, p);
        form.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(form);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        Button save = (Button) dialog.getDialogPane().lookupButton(saveButton);
        save.setDefaultButton(false);

        UIComponents.setupKeyboardNavigation(dialog, nameField, locationField, phoneField, save);
        save.addEventFilter(ActionEvent.ACTION, event -> {

            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || location.isEmpty() || phone.isEmpty()) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
                event.consume();
                return;
            }

            if (!phone.matches("\\d{10}")) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Phone number must contain exactly 10 digits.");
                event.consume();
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Update");
            confirm.setHeaderText("Update Department?");
            confirm.setContentText("Are you sure you want to save these changes?");

            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                event.consume();
                return;
            }

            HospitalOperations operations = new HospitalOperations();
            boolean updated = operations.updateDepartment(id, name, location, phone);
            UIComponents.showAlert(updated ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, updated ? "Department updated successfully." : "Failed to update department.");

            if (updated) {
                showDepartments();
            } else {
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private TableColumn<ObservableList<String>, Void> createActionsColumn(Consumer<ObservableList<String>> onEdit, Consumer<ObservableList<String>> onDelete) {

        TableColumn<ObservableList<String>, Void> actionsColumn = new TableColumn<>("Actions");

        actionsColumn.setCellFactory(col -> new TableCell<>() {

            Button editButton = new Button("Edit");
            Button deleteButton = new Button("Delete");

            HBox buttons = new HBox(10, editButton, deleteButton);

            {
                editButton.setOnAction(e -> {
                    ObservableList<String> row = getTableRow().getItem();
                    if (row != null) {
                        onEdit.accept(row);
                    }
                });

                deleteButton.setOnAction(e -> {
                    ObservableList<String> row = getTableRow().getItem();
                    if (row != null) {
                        onDelete.accept(row);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                setGraphic(empty ? null : buttons);
            }

            @Override
            public void updateIndex(int index) {
                super.updateIndex(index);

                if (index < 0 || index >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        return actionsColumn;
    }




    private void showDoctors() {

        pageTitle.setText("Doctors");

        Label title = new Label("Doctors");
        Button addButton = new Button("+ Add Doctor");
        addButton.setOnAction(e -> showAddDoctorDialog());

        HBox header = new HBox(15, title, addButton);
        header.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(1, spacer);

        ComboBox<String> searchBy = new ComboBox<>();
        searchBy.getItems().addAll("DoctorID", "Name", "Specialization", "Phone", "Email", "DepartmentID");
        searchBy.setValue("Name");

        TextField searchField = new TextField();
        searchField.setPromptText("Search doctors...");

        HBox searchBox = new HBox(10, searchBy, searchField);

        String[] columnNames = {"Doctor ID", "Full Name", "Specialization", "Phone", "Email", "Department ID"};
        TableView<ObservableList<String>> table = UIComponents.createTable(columnNames, true);
        TableColumn<ObservableList<String>, Void> actionsColumn =
                createActionsColumn(row -> showEditDoctorDialog(row),
                        row -> confirmDelete("Doctor", "DoctorID", Integer.parseInt(row.get(0)), "Doctors"));

        table.getColumns().add(actionsColumn);

        HospitalOperations operations = new HospitalOperations();

        table.setItems(operations.getData("Doctor"));

        UIComponents.setupSearch(table, searchBy, searchField, "Doctor");
        VBox page = new VBox(15, header, searchBox, table);
        page.setPadding(new Insets(20));

        setContent(page);
    }

    private void showEditDoctorDialog(ObservableList<String> row) {

        int doctorId = Integer.parseInt(row.get(0));

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Doctor");
        dialog.setHeaderText("Edit Doctor Information");

        TextField firstNameField = new TextField(row.get(1));
        TextField lastNameField = new TextField(row.get(2));
        TextField specializationField = new TextField(row.get(3));
        TextField phoneField = new TextField(row.get(4));
        TextField emailField = new TextField(row.get(5));
        ComboBox<String> departmentBox = new ComboBox<>();
        departmentBox.setPrefWidth(230);

        HospitalOperations operations = new HospitalOperations();

        ObservableList<ObservableList<String>> departments =
                operations.getData("Department");

        for (ObservableList<String> department : departments) {

            String item = department.get(0) + " - " + department.get(1);

            departmentBox.getItems().add(item);

            if (department.get(0).equals(row.get(6))) {
                departmentBox.setValue(item);
            }
        }

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(20));

        form.addRow(0, new Label("First Name"), firstNameField);
        form.addRow(1, new Label("Last Name"), lastNameField);
        form.addRow(2, new Label("Specialization"), specializationField);
        form.addRow(3, new Label("Phone"), phoneField);
        form.addRow(4, new Label("Email"), emailField);
        form.addRow(5, new Label("Department"), departmentBox);
        dialog.getDialogPane().setContent(form);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        Button save = (Button) dialog.getDialogPane().lookupButton(saveButton);
        save.setDefaultButton(false);
        UIComponents.setupKeyboardNavigation(dialog, firstNameField, lastNameField, specializationField, phoneField, emailField, departmentBox, save);

        save.addEventFilter(ActionEvent.ACTION, event -> {

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String specialization = specializationField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();


            if (firstName.isEmpty() || lastName.isEmpty() || specialization.isEmpty() || phone.isEmpty() || email.isEmpty()|| departmentBox.getValue() == null ) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
                event.consume();
                return;
            }
            int departmentId = Integer.parseInt(
                    departmentBox.getValue().split(" - ", 2)[0]
            );

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Update");
            confirm.setHeaderText("Update Doctor?");
            confirm.setContentText("Are you sure you want to save these changes?");

            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                event.consume();
                return;
            }


            boolean updated = operations.updateDoctor(doctorId, firstName, lastName, specialization, phone, email, departmentId);

            UIComponents.showAlert(updated ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, updated ?
                    "Doctor updated successfully." : "Failed to update doctor.");

            if (updated) {
                showDoctors();
            } else {
                event.consume();
            }
        });

        dialog.showAndWait();
    }




    private void confirmDelete(String tableName, String idColumn, int id, String pageName) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete " + tableName);
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this " + tableName + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        if (tableName.equals("Appointment")) {

            try {

                if (new HospitalOperations().hasMedicalRecord(id)) {

                    UIComponents.showAlert(Alert.AlertType.WARNING,
                            "Cannot delete an appointment linked to a medical record.");

                    return;
                }

            } catch (SQLException | ClassNotFoundException e) {

                UIComponents.showAlert(Alert.AlertType.ERROR,
                        "Unable to check linked medical records.");

                return;
            }
        }

        HospitalOperations operations = new HospitalOperations();
        boolean deleted = operations.deleteData(tableName, idColumn, id);
        UIComponents.showAlert(deleted ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, deleted ? tableName +
                " deleted successfully." : "Failed to delete " + tableName + ".");

        if (deleted) {
            showPage(pageName);
        }
    }

    private void showAddDoctorDialog() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Doctor");
        dialog.setHeaderText("Enter Doctor Information");

        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField specializationField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();

        ComboBox<String> departmentBox = new ComboBox<>();
        departmentBox.setPrefWidth(230);

        HospitalOperations operations = new HospitalOperations();

        ObservableList<ObservableList<String>> departments =
                operations.getData("Department");

        for (ObservableList<String> department : departments) {
            departmentBox.getItems().add(
                    department.get(0) + " - " + department.get(1)
            );
        }

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(20));

        form.addRow(0, new Label("First Name"), firstNameField);
        form.addRow(1, new Label("Last Name"), lastNameField);
        form.addRow(2, new Label("Specialization"), specializationField);
        form.addRow(3, new Label("Phone"), phoneField);
        form.addRow(4, new Label("Email"), emailField);
        form.addRow(5, new Label("Department"), departmentBox);

        dialog.getDialogPane().setContent(form);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        Button save = (Button) dialog.getDialogPane().lookupButton(saveButton);
        save.setDefaultButton(false);
        UIComponents.setupKeyboardNavigation(dialog, firstNameField, lastNameField, specializationField, phoneField, emailField, departmentBox, save);
        save.addEventFilter(ActionEvent.ACTION, event -> {

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String specialization = specializationField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || specialization.isEmpty() || phone.isEmpty() || email.isEmpty() || departmentBox.getValue() == null) {

                UIComponents.showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
                event.consume();
                return;
            }

            if (!phone.matches("\\d{10}")) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Phone number must contain exactly 10 digits.");
                event.consume();
                return;
            }

            if (!email.matches("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
                UIComponents.showAlert(Alert.AlertType.WARNING, "Enter a valid email address.");
                event.consume();
                return;
            }

            String selectedDepartment = departmentBox.getValue();

            int departmentId = Integer.parseInt(selectedDepartment.split(" - ", 2)[0]);

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Addition");
            confirm.setHeaderText("Add Doctor?");
            confirm.setContentText("Are you sure you want to add " + firstName + " " + lastName + "?");

            if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                event.consume();
                return;
            }

            boolean added = operations.addDoctor(firstName, lastName, specialization, phone, email, departmentId);
            UIComponents.showAlert(added ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR, added ?
                    "Doctor added successfully." : "Failed to add doctor.");

            if (added) {
                showDoctors();
            } else {
                event.consume();
            }
        });

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
        searchBy.getItems().addAll("PatientID", "Name", "DateOfBirth", "Gender", "Phone", "Email", "Address", "BloodType");
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
        TableView<ObservableList<String>> table = UIComponents.createTable(columnNames, true);
        table.getColumns().add(createActionsColumn(
                row -> {
                    if (UIComponents.showPatientDialog(row) > 0) {
                        showPatients();
                    }
                },
                row -> confirmDelete("Patient", "PatientID",
                        Integer.parseInt(row.get(0)), "Patients")));

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
        addButton.setOnAction(e -> UIComponents.showAppointmentDialog(null, this::showAppointments));

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
        String[] columnNames = {"ID", "Patient", "Doctor", "Date", "Time", "Reason", "Status"};
        TableView<ObservableList<String>> table = UIComponents.createTable(columnNames, false);
        table.getColumns().add(createActionsColumn(
                row -> UIComponents.showAppointmentDialog(row, this::showAppointments),
                row -> confirmDelete("Appointment", "AppointmentID",
                        Integer.parseInt(row.get(0)), "Appointments")
        ));

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

        searchField.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());
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

    private void showMedicalRecords() {

        pageTitle.setText("Medical Records");

        Label title = new Label("Medical Records");

        Button addButton = new Button("+ Add Medical Record");

        HBox header = new HBox(15, title, addButton);
        header.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(1, spacer);

        // Search
        ComboBox<String> searchBy = new ComboBox<>();

        searchBy.getItems().addAll(
                "RecordID",
                "PatientID",
                "PatientName",
                "DoctorName",
                "AppointmentID",
                "Diagnosis",
                "RecordDate"
        );

        searchBy.setValue("PatientName");

        TextField searchField = new TextField();
        searchField.setPromptText("Search medical records...");

        Button clearButton = new Button("Clear");

        HBox searchBox = new HBox(10, searchBy, searchField, clearButton);

        searchBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // Table
        String[] columnNames = {
                "Record ID",
                "Patient",
                "Doctor",
                "Appointment ID",
                "Record Date"
        };
        TableView<ObservableList<String>> table = UIComponents.createTable(columnNames, false);

        // Load data
        HospitalOperations operations = new HospitalOperations();

        Runnable refresh = () -> table.setItems(
                operations.getMedicalRecords(
                        searchBy.getValue(),
                        searchField.getText().trim()
                )
        );

        // Add medical record
        addButton.setOnAction(e -> {

            if (UIComponents.showAddMedicalRecordDialog()) {

                searchField.clear();

                refresh.run();
            }
        });

        // View and edit
        table.getColumns().add(
                UIComponents.createViewColumn(row -> {

                    boolean updated =
                            UIComponents.showMedicalRecordDetails(row, "Admin");

                    if (updated) {

                        refresh.run();
                    }
                })
        );

        // Search events
        searchField.textProperty().addListener(
                (obs, oldValue, newValue) -> refresh.run()
        );

        searchBy.setOnAction(e -> refresh.run());

        clearButton.setOnAction(e -> {

            searchBy.setValue("PatientName");

            searchField.clear();

            refresh.run();
        });

        refresh.run();

        VBox page = new VBox(15, header, searchBox, table);

        page.setPadding(new Insets(20));

        setContent(page);
    }
    private void showUsers() {

        pageTitle.setText("Users Management");

        Label title = new Label("Users Management");

        Button addButton = new Button("+ Add User");
        addButton.setOnAction(e -> showUserDialog(null));
        Button requestsButton = new Button("Password Requests (" + new HospitalOperations().getPasswordRequests().size() + ")");
        requestsButton.setOnAction(e -> showPasswordRequests());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(15, title, spacer,
                requestsButton, addButton);

        header.setAlignment(Pos.CENTER_LEFT);

        // Search
        TextField searchField = new TextField();
        searchField.setPromptText("Search by username or full name...");

        // Role filter
        ComboBox<String> roleFilter = new ComboBox<>();

        roleFilter.getItems().addAll("All Roles", "Administrator", "Doctor", "Receptionist");

        roleFilter.setValue("All Roles");

        // Status filter
        ComboBox<String> statusFilter = new ComboBox<>();

        statusFilter.getItems().addAll("All Status", "Active", "Inactive");

        statusFilter.setValue("All Status");

        Button clearButton = new Button("Clear Filters");

        HBox searchBox = new HBox(10, searchField,
                roleFilter, statusFilter, clearButton);

        searchBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // Users table
        TableView<ObservableList<String>> table = new TableView<>();

        String[] columnNames = {"ID", "Username", "Full Name", "Role", "Status"};

        for (int i = 0; i < columnNames.length; i++) {

            final int index = i;

            TableColumn<ObservableList<String>, String> column =
                    new TableColumn<>(columnNames[i]);

            column.setCellValueFactory(data -> {

                String value = data.getValue().get(index);

                if (index == 4) {
                    value = ("1".equals(value) || "true".equalsIgnoreCase(value)) ? "Active" : "Inactive";
                }

                return new ReadOnlyStringWrapper(value);
            });

            table.getColumns().add(column);
        }
        table.getColumns().add(createUserActionsColumn());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HospitalOperations operations = new HospitalOperations();

        Runnable refresh = () -> table.setItems(operations.getUsers(searchField.getText().trim(), roleFilter.getValue(), statusFilter.getValue()));

        searchField.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());

        roleFilter.setOnAction(e -> refresh.run());

        statusFilter.setOnAction(e -> refresh.run());

        clearButton.setOnAction(e -> {
            searchField.clear();
            roleFilter.setValue("All Roles");
            statusFilter.setValue("All Status");
            refresh.run();
        });

        refresh.run();

        VBox page = new VBox(15, header, searchBox, table);

        page.setPadding(new Insets(20));

        setContent(page);
    }
    private void showUserDialog(ObservableList<String> row) {

        boolean isEdit = row != null;

        int userId = isEdit ? Integer.parseInt(row.get(0)) : 0;

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle(isEdit ? "Manage User" : "Add User");
        dialog.setHeaderText("User Information");

        HospitalOperations operations = new HospitalOperations();

        ComboBox<String> roleBox = new ComboBox<>();

        roleBox.getItems().addAll(
                "Administrator",
                "Doctor",
                "Receptionist"
        );

        roleBox.setPromptText("Select Role");

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Enter full name");

        TextField usernameField = new TextField();

        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        CheckBox activeBox = new CheckBox("Active Account");
        activeBox.setSelected(true);

        ObservableList<ObservableList<String>> doctors =
                operations.getDoctorsWithoutAccounts(userId);


        ContextMenu doctorSuggestions = new ContextMenu();
        doctorSuggestions.setAutoHide(false);

        Integer[] selectedDoctorId = {null};
        boolean[] selectingDoctor = {false};

        Runnable showDoctorSuggestions = () -> {

            doctorSuggestions.hide();
            doctorSuggestions.getItems().clear();

            if (!"Doctor".equals(roleBox.getValue())) {
                return;
            }

            String search = fullNameField.getText().trim()
                    .toLowerCase(java.util.Locale.ROOT);

            for (ObservableList<String> doctor : doctors) {

                String fullName =
                        doctor.get(1) + " " + doctor.get(2);

                String specialization = doctor.get(3);

                if (fullName.toLowerCase(java.util.Locale.ROOT)
                        .contains(search)) {

                    MenuItem item = new MenuItem(
                            fullName + " - " + specialization
                    );

                    item.setOnAction(e -> {

                        selectingDoctor[0] = true;

                        fullNameField.setText(fullName);

                        selectedDoctorId[0] =
                                Integer.parseInt(doctor.get(0));

                        selectingDoctor[0] = false;

                        doctorSuggestions.hide();
                    });

                    doctorSuggestions.getItems().add(item);
                }
            }

            if (doctorSuggestions.getItems().isEmpty()) {

                MenuItem noResults =
                        new MenuItem("No available doctors found");

                noResults.setDisable(true);

                doctorSuggestions.getItems().add(noResults);
            }

            doctorSuggestions.show(
                    fullNameField,
                    javafx.geometry.Side.BOTTOM,
                    0, 0
            );
        };

        // Handle role changes

        roleBox.valueProperty().addListener(
                (obs, oldValue, newValue) -> {

                    doctorSuggestions.hide();

                    selectedDoctorId[0] = null;

                    selectingDoctor[0] = true;

                    if ("Doctor".equals(newValue) ||
                            "Doctor".equals(oldValue)) {
                        fullNameField.clear();
                    }

                    selectingDoctor[0] = false;

                    fullNameField.setPromptText(
                            "Doctor".equals(newValue)
                                    ? "Search or select doctor..."
                                    : "Enter full name"
                    );
                }
        );

        fullNameField.setOnMouseClicked(e -> {

            if ("Doctor".equals(roleBox.getValue())) {
                showDoctorSuggestions.run();
            }
        });

        fullNameField.textProperty().addListener(
                (obs, oldValue, newValue) -> {

                    if (selectingDoctor[0]) {
                        return;
                    }

                    selectedDoctorId[0] = null;

                    if ("Doctor".equals(roleBox.getValue())
                            && fullNameField.isFocused()) {

                        showDoctorSuggestions.run();
                    }
                }
        );

        // Load existing user information

        if (isEdit) {

            roleBox.setValue(row.get(3));

            selectingDoctor[0] = true;

            usernameField.setText(row.get(1));
            fullNameField.setText(row.get(2));

            activeBox.setSelected(
                    "1".equals(row.get(4)) ||
                            "true".equalsIgnoreCase(row.get(4))
            );

            if ("Doctor".equals(row.get(3))) {

                try {

                    selectedDoctorId[0] =
                            operations.getLinkedDoctor(userId);

                    for (ObservableList<String> doctor : doctors) {

                        if (Integer.valueOf(doctor.get(0))
                                .equals(selectedDoctorId[0])) {

                            fullNameField.setText(
                                    doctor.get(1) + " " + doctor.get(2)
                            );

                            break;
                        }
                    }

                } catch (SQLException | ClassNotFoundException e) {

                    UIComponents.showAlert(
                            Alert.AlertType.ERROR,
                            "Unable to load linked doctor."
                    );

                    return;
                }
            }

            selectingDoctor[0] = false;
        }

        roleBox.setPrefWidth(250);
        fullNameField.setPrefWidth(250);
        usernameField.setPrefWidth(250);
        passwordField.setPrefWidth(250);
        confirmPasswordField.setPrefWidth(250);

        GridPane form = new GridPane();

        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(20));

        int index = 0;

        if (isEdit) {

            TextField idField = new TextField(
                    String.valueOf(userId)
            );

            idField.setEditable(false);

            form.addRow(index++, new Label("User ID"), idField);
        }

        form.addRow(index++, new Label("Role"), roleBox);
        form.addRow(index++, new Label("Full Name"), fullNameField);
        form.addRow(index++, new Label("Username"), usernameField);

        if (!isEdit) {

            form.addRow(index++, new Label("Temporary Password"), passwordField);

            form.addRow(index++, new Label("Confirm Password"),
                    confirmPasswordField);
        }

        form.add(activeBox, 1, index);

        dialog.getDialogPane().setContent(form);

        ButtonType saveType = new ButtonType(
                isEdit ? "Save Changes" : "Save",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                saveType, ButtonType.CANCEL
        );

        Button save = (Button) dialog.getDialogPane()
                .lookupButton(saveType);

        save.setDefaultButton(false);

        save.addEventFilter(ActionEvent.ACTION, event -> {

            doctorSuggestions.hide();

            String role = roleBox.getValue();
            String fullName = fullNameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (role == null || fullName.isEmpty() ||
                    username.isEmpty()) {

                UIComponents.showAlert(
                        Alert.AlertType.WARNING,
                        "Please fill in all required fields."
                );

                event.consume();
                return;
            }

            if ("Doctor".equals(role) &&
                    selectedDoctorId[0] == null) {

                UIComponents.showAlert(
                        Alert.AlertType.WARNING,
                        "Please select a doctor from the search results."
                );

                event.consume();
                return;
            }

            if (username.length() > 50 ||
                    fullName.length() > 120) {

                UIComponents.showAlert(
                        Alert.AlertType.WARNING,
                        "Username or full name is too long."
                );

                event.consume();
                return;
            }

            // Validate password only when adding a user

            if (!isEdit) {

                if (password.isBlank()) {
                    UIComponents.showAlert(Alert.AlertType.WARNING, "Please enter a password.");
                    event.consume();
                    return;
                }

                if (!password.equals(confirmPasswordField.getText())) {

                    UIComponents.showAlert(
                            Alert.AlertType.WARNING,
                            "Passwords do not match."
                    );

                    event.consume();
                    return;
                }

                if (password.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8
                ).length > 72) {
                    UIComponents.showAlert(Alert.AlertType.WARNING, "Password exceeds the supported length.");
                    event.consume();
                    return;
                }
            }

            try {

                if (operations.isUsernameExists(username, userId)) {
                    UIComponents.showAlert(Alert.AlertType.WARNING, "Username already exists.");
                    event.consume();
                    return;
                }

            } catch (SQLException | ClassNotFoundException e) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Unable to check username availability.");
                event.consume();
                return;
            }
            // Protect the last active administrator

            if (isEdit && (!role.equals("Administrator") || !activeBox.isSelected())) {

                try {

                    if (operations.isLastActiveAdministrator(userId)) {

                        UIComponents.showAlert(
                                Alert.AlertType.WARNING,
                                "Cannot deactivate or change the role of the last active administrator."
                        );

                        event.consume();
                        return;
                    }

                } catch (SQLException | ClassNotFoundException e) {

                    UIComponents.showAlert(
                            Alert.AlertType.ERROR,
                            "Unable to verify administrator status."
                    );

                    event.consume();
                    return;
                }
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, isEdit ?
                    "Are you sure you want to update this user?" : "Are you sure you want to add this user?",
                    ButtonType.YES, ButtonType.NO);

            if (confirmation.showAndWait().orElse(ButtonType.NO)
                    != ButtonType.YES) {

                event.consume();
                return;
            }

            boolean success;

            if (isEdit) {
                success = operations.updateUser(userId, username, fullName, role, activeBox.isSelected(), selectedDoctorId[0]);
            } else {
                success = operations.addUser(username, fullName, password, role, activeBox.isSelected(), selectedDoctorId[0]);
            }

            if (success) {
                UIComponents.showAlert(Alert.AlertType.INFORMATION, isEdit ? "User updated successfully." : "User added successfully.");
                showUsers();

            } else {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Failed to save user.");
                event.consume();
            }});

        dialog.setOnHidden(e -> doctorSuggestions.hide());

        dialog.showAndWait();
    }
    private TableColumn<ObservableList<String>, Void> createUserActionsColumn() {

        TableColumn<ObservableList<String>, Void> column =
                new TableColumn<>("Actions");

        column.setCellFactory(param -> new TableCell<>() {

            private final Button manageButton = new Button("Manage");

            private final Button resetButton = new Button("Reset Password");

            private final HBox buttons =
                    new HBox(10, manageButton, resetButton);

            {
                buttons.setAlignment(Pos.CENTER);

                manageButton.setOnAction(e -> {

                    ObservableList<String> row =
                            getTableRow().getItem();

                    if (row != null) {
                        showUserDialog(row);
                    }
                });

                resetButton.setOnAction(e -> {

                    ObservableList<String> row =
                            getTableRow().getItem();

                    if (row != null) {

                        if (showResetPasswordDialog(row)) {
                            showUsers();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {

                super.updateItem(item, empty);

                ObservableList<String> row =
                        getTableRow() == null
                                ? null
                                : getTableRow().getItem();

                if (empty || row == null) {

                    setGraphic(null);

                } else {

                    boolean isActive =
                            "1".equals(row.get(4)) ||
                                    "true".equalsIgnoreCase(row.get(4));

                    resetButton.setDisable(!isActive);

                    setGraphic(buttons);
                }
            }
        });

        column.setPrefWidth(230);
        column.setSortable(false);

        return column;
    }
    private boolean showResetPasswordDialog(ObservableList<String> row) {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Reset Password");
        dialog.setHeaderText("Set Temporary Password");

        int userId = Integer.parseInt(row.get(0));

        HospitalOperations operations = new HospitalOperations();

        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        passwordField.setPromptText("Enter temporary password");
        confirmPasswordField.setPromptText("Confirm password");

        passwordField.setPrefWidth(250);
        confirmPasswordField.setPrefWidth(250);

        GridPane form = new GridPane();

        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(20));

        form.addRow(0, new Label("Username"), new Label(row.get(1)));
        form.addRow(1, new Label("Full Name"), new Label(row.get(2)));
        form.addRow(2, new Label("Role"), new Label(row.get(3)));
        form.addRow(3, new Label("Temporary Password"), passwordField);
        form.addRow(4, new Label("Confirm Password"), confirmPasswordField);

        Label note = new Label(
                "The user must change their password on the next login."
        );

        note.setWrapText(true);

        form.add(note, 0, 5, 2, 1);

        dialog.getDialogPane().setContent(form);

        ButtonType resetType = new ButtonType(
                "Reset Password",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                resetType, ButtonType.CANCEL
        );

        Button resetButton = (Button) dialog.getDialogPane()
                .lookupButton(resetType);

        resetButton.setDefaultButton(false);

        UIComponents.setupKeyboardNavigation(
                dialog,
                passwordField,
                confirmPasswordField,
                resetButton
        );

        boolean[] success = {false};

        resetButton.addEventFilter(ActionEvent.ACTION, event -> {

            String password = passwordField.getText();

            if (password.isBlank()) {

                UIComponents.showAlert(
                        Alert.AlertType.WARNING,
                        "Please enter a temporary password."
                );

                event.consume();
                return;
            }

            if (!password.equals(confirmPasswordField.getText())) {

                UIComponents.showAlert(
                        Alert.AlertType.WARNING,
                        "Passwords do not match."
                );

                event.consume();
                return;
            }

            if (password.getBytes(
                    java.nio.charset.StandardCharsets.UTF_8
            ).length > 72) {

                UIComponents.showAlert(
                        Alert.AlertType.WARNING,
                        "Password exceeds the supported length."
                );

                event.consume();
                return;
            }

            Alert confirmation = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to reset this user's password?",
                    ButtonType.YES,
                    ButtonType.NO
            );

            if (confirmation.showAndWait().orElse(ButtonType.NO)
                    != ButtonType.YES) {

                event.consume();
                return;
            }

            success[0] = operations.resetPassword(userId, password);

            if (success[0]) {

                UIComponents.showAlert(
                        Alert.AlertType.INFORMATION,
                        "Temporary password set successfully."
                );

            } else {

                UIComponents.showAlert(Alert.AlertType.ERROR, "Failed to reset password.");

                event.consume();
            }
        });

        dialog.showAndWait();

        return success[0];
    }


    private void showPasswordRequests() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Password Requests");
        dialog.setHeaderText("Pending Password Reset Requests");
        HospitalOperations operations = new HospitalOperations();
        VBox requestsBox = new VBox(15);
        requestsBox.setPadding(new Insets(15));
        ObservableList<ObservableList<String>> requests = operations.getPasswordRequests();

        for (ObservableList<String> row : requests) {
            int userId = Integer.parseInt(row.get(0));
            Label fullName = new Label(row.get(2));
            Label username = new Label("Username: " + row.get(1));
            Label role = new Label("Role: " + row.get(3));
            Button rejectButton = new Button("Reject");
            Button resetButton = new Button("Set Temporary Password");
            HBox buttons = new HBox(10, rejectButton, resetButton);
            buttons.setAlignment(Pos.CENTER_RIGHT);
            VBox requestCard = new VBox(8, fullName, username, role, buttons);
            requestCard.setPadding(new Insets(15));
            requestCard.setStyle("-fx-border-color: #DDE5E5;-fx-border-radius: 8;");

            Runnable removeRequest = () -> {
                requestsBox.getChildren().remove(requestCard);
                if (requestsBox.getChildren().isEmpty()) {
                    requestsBox.getChildren().add(new Label("No pending password reset requests."));
                }
            };

            rejectButton.setOnAction(e -> {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to reject this request?", ButtonType.YES, ButtonType.NO);
                if (confirmation.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
                    return;
                }
                if (operations.rejectPasswordRequest(userId)) {
                    UIComponents.showAlert(Alert.AlertType.INFORMATION, "Request rejected successfully.");
                    removeRequest.run();
                } else {
                    UIComponents.showAlert(Alert.AlertType.ERROR, "Failed to reject request.");
                }
            });

            resetButton.setOnAction(e -> {
                if (showResetPasswordDialog(row)) {
                    removeRequest.run();
                }
            });
            requestsBox.getChildren().add(requestCard);
        }
        if (requestsBox.getChildren().isEmpty()) {
            requestsBox.getChildren().add(new Label("No pending password reset requests."));
        }
        ScrollPane scrollPane = new ScrollPane(requestsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportWidth(500);
        scrollPane.setPrefViewportHeight(400);
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
        showUsers();
    }
    private Button dashboardCard(String title, int count, String destination) {

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        Label countLabel = new Label(
                count == -1 ? "Unavailable" : String.valueOf(count)
        );

        countLabel.setStyle(
                "-fx-font-size: 30px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        VBox cardContent = new VBox(8, titleLabel, countLabel);
        cardContent.setAlignment(Pos.CENTER);

        Button card = new Button();
        card.setGraphic(cardContent);

        card.setPrefHeight(100);
        card.setMinHeight(80);
        card.setMaxHeight(Double.MAX_VALUE);

        card.setPrefWidth(0);
        card.setMinWidth(0);
        card.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(card, Priority.ALWAYS);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 12;" +
                        "-fx-cursor: hand;"
        );

        card.setOnAction(e -> showPage(destination));

        return card;
    }
    private Button quickActionButton(String title, Runnable action) {

        Button button = new Button(title);

        button.setPrefHeight(40);
        button.setMinHeight(40);
        button.setMaxHeight(40);

        button.setMinWidth(0);
        button.setPrefWidth(0);
        button.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(button, Priority.ALWAYS);

        button.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: " + PRIMARY + ";" +
                        "-fx-font-size: 14px;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;"
        );

        button.setOnAction(e -> action.run());

        return button;
    }
}
