
package org.example.nabd;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import java.sql.SQLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

        VBox logoBox = UIComponents.createLogo(140, 140);
        logoBox.setPadding(new Insets(0, 0, 30, 5));

        Button dashboard = menuButton("Dashboard");
        Button departments = menuButton("Departments");
        Button doctors = menuButton("Doctors");
        Button patients = menuButton("Patients");
        Button appointments = menuButton("Appointments");
        Button medicalRecords = menuButton("Medical Records");
        Button users = menuButton("Users");

        dashboard.setOnAction(e -> showPage("Dashboard"));
        departments.setOnAction(e -> showPage("Departments"));
        doctors.setOnAction(e -> showPage("Doctors"));
        patients.setOnAction(e -> showPage("Patients"));
        appointments.setOnAction(e -> showPage("Appointments"));
        medicalRecords.setOnAction(e -> showPage("Medical Records"));
        users.setOnAction(e -> showPage("Users"));

        menu = new VBox(8, dashboard, departments, doctors, patients, appointments, medicalRecords, users);

        VBox.setVgrow(menu, Priority.ALWAYS);

        Button logout = menuButton("Logout");

        logout.setOnAction(e -> TestConnection.openLogin());

        VBox sidebar = new VBox(10, logoBox, menu, logout);

        sidebar.setPadding(new Insets(30, 15, 25, 15));
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);

        sidebar.setStyle("-fx-background-color: " + PRIMARY + ";");

        root.setLeft(sidebar);
    }


    // ================= MENU BUTTON =================

    private Button menuButton(String text) {

        Button button = new Button(text);

        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(45);

        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-alignment: CENTER_LEFT;" +
                        "-fx-padding: 12 18;" +
                        "-fx-background-radius: 9;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: #187C7F;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 15px;" +
                                "-fx-alignment: CENTER_LEFT;" +
                                "-fx-padding: 12 18;" +
                                "-fx-background-radius: 9;" +
                                "-fx-cursor: hand;"
                )
        );

        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 15px;" +
                                "-fx-alignment: CENTER_LEFT;" +
                                "-fx-padding: 12 18;" +
                                "-fx-background-radius: 9;" +
                                "-fx-cursor: hand;"
                )
        );

        return button;
    }

    // ================= HEADER =================

    private void createHeader() {

        pageTitle = new Label("Dashboard");

        pageTitle.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        Label admin = new Label("Administrator");

        admin.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #555555;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(20, pageTitle, spacer, admin);

        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 30, 0, 30));
        header.setPrefHeight(75);

        header.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        root.setTop(header);
    }

    // ================= CONTENT =================

    private void setContent(Node node) {

        content = new VBox(node);
        content.setPadding(new Insets(30));
        content.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + BACKGROUND + ";" + "-fx-background-color: transparent;");

        root.setCenter(scrollPane);
    }

    // ================= DASHBOARD =================

    private void showDashboard() {

        pageTitle.setText("Dashboard");

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

        HBox firstRow = new HBox(
                20,
                dashboardCard("Departments", "Departments"),
                dashboardCard("Doctors", "Doctors"),
                dashboardCard("Patients", "Patients")
        );

        HBox secondRow = new HBox(
                20,
                dashboardCard("Appointments", "Appointments"),
                dashboardCard("Medical Records", "Medical Records"),
                dashboardCard("Users", "Users")
        );

        VBox dashboardContent = new VBox(
                25,
                welcome,
                description,
                firstRow,
                secondRow
        );

        setContent(dashboardContent);
    }

    // ================= DASHBOARD CARD =================

    private Button dashboardCard(String title, String destination) {

        Button card = new Button(title);

        card.setPrefHeight(140);
        card.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(card, Priority.ALWAYS);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: " + PRIMARY + ";" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-radius: 12;" +
                        "-fx-cursor: hand;"
        );

        card.setOnAction(e -> showPage(destination));

        return card;
    }

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
        TableView<ObservableList<String>> table = new TableView<>();

        String[] columnNames = {"Department ID", "Department Name", "Location", "Phone"};

        for (int i = 0; i < columnNames.length; i++) {

            final int index = i;

            TableColumn<ObservableList<String>, String> column =
                    new TableColumn<>(columnNames[i]);

            column.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(data.getValue().get(index))
            );

            table.getColumns().add(column);
        }


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

        TableView<ObservableList<String>> table = new TableView<>();

        String[] columnNames = {"Doctor ID", "Full Name", "Specialization", "Phone", "Email", "Department ID"};
        for (int i = 0; i < columnNames.length; i++) {

            final int index = i;

            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnNames[i]);

            column.setCellValueFactory(data -> {

                ObservableList<String> row = data.getValue();

                if (index == 1) {
                    return new ReadOnlyStringWrapper(row.get(1) + " " + row.get(2));
                }

                int dataIndex = index > 1 ? index + 1 : index;

                return new ReadOnlyStringWrapper(row.get(dataIndex));
            });

            table.getColumns().add(column);
        }
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

        TableView<ObservableList<String>> table = new TableView<>();

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

        for (int i = 0; i < columnNames.length; i++) {
            final int index = i;
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnNames[i]);
            column.setCellValueFactory(data -> {
                ObservableList<String> row = data.getValue();

                if (index == 1) {
                    return new ReadOnlyStringWrapper(row.get(1) + " " + row.get(2));
                }

                int dataIndex = index > 1 ? index + 1 : index;

                return new ReadOnlyStringWrapper(row.get(dataIndex));
            });
            table.getColumns().add(column);
        }
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


    private void showAppointmentDialog(ObservableList<String> row) {

        boolean isEdit = row != null;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Appointment" : "Add Appointment");
        dialog.setHeaderText("Appointment Information");

        HospitalOperations operations = new HospitalOperations();

        ComboBox<String> patientBox = new ComboBox<>();
        TextField patientSearch = new TextField();
        patientSearch.setPromptText("Search by ID, name or phone...");

        ComboBox<String> doctorBox = new ComboBox<>();
        TextField doctorSearch = new TextField();
        doctorSearch.setPromptText("Search by ID, name or specialization...");

        DatePicker datePicker = new DatePicker();
        datePicker.setEditable(false);

        ComboBox<LocalTime> timeBox = new ComboBox<>();

        TextField reasonField = new TextField();

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Scheduled", "Completed", "Cancelled");
        statusBox.setValue("Scheduled");

        // Load patients
        UIComponents.loadPatients(patientBox,
                isEdit ? Integer.parseInt(row.get(7)) : -1);

        UIComponents.setupComboBoxSearch(
                patientBox,
                patientSearch,
                () -> UIComponents.loadPatients(patientBox, -1)
        );

        // Add new patient
        Button newPatientButton = new Button("+ New Patient");

        newPatientButton.setOnAction(e -> {

            int newPatientId = UIComponents.showPatientDialog(null);

            if (newPatientId > 0) {
                patientSearch.clear();
                UIComponents.loadPatients(patientBox, newPatientId);
            }
        });

        // Load doctors
        UIComponents.loadDoctors(doctorBox,
                isEdit ? Integer.parseInt(row.get(8)) : -1);

        UIComponents.setupComboBoxSearch(
                doctorBox,
                doctorSearch,
                () -> UIComponents.loadDoctors(doctorBox, -1)
        );

        // Available time options
        for (int i = 0; i < 48; i++) {
            timeBox.getItems().add(
                    LocalTime.MIDNIGHT.plusMinutes(i * 30L)
            );
        }

        // Fill existing data when editing
        if (isEdit) {

            datePicker.setValue(LocalDate.parse(row.get(3)));

            LocalTime existingTime = LocalTime.parse(row.get(4));

            if (!timeBox.getItems().contains(existingTime)) {
                timeBox.getItems().add(existingTime);
            }

            timeBox.setValue(existingTime);
            reasonField.setText(row.get(5));
            statusBox.setValue(row.get(6));
        }

        patientBox.setPrefWidth(250);
        doctorBox.setPrefWidth(250);
        datePicker.setPrefWidth(250);
        timeBox.setPrefWidth(250);
        statusBox.setPrefWidth(250);

        HBox patientRow = new HBox(10, patientBox, newPatientButton);
        patientRow.setAlignment(Pos.CENTER_LEFT);

        VBox patientContainer = new VBox(8, patientSearch, patientRow);
        VBox doctorContainer = new VBox(8, doctorSearch, doctorBox);

        GridPane form = new GridPane();

        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(20));

        form.addRow(0, new Label("Patient"), patientContainer);
        form.addRow(1, new Label("Doctor"), doctorContainer);
        form.addRow(2, new Label("Appointment Date"), datePicker);
        form.addRow(3, new Label("Appointment Time"), timeBox);
        form.addRow(4, new Label("Reason"), reasonField);
        form.addRow(5, new Label("Status"), statusBox);

        dialog.getDialogPane().setContent(form);

        ButtonType saveType = new ButtonType(
                "Save", ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                saveType, ButtonType.CANCEL
        );

        Button save = (Button) dialog.getDialogPane().lookupButton(saveType);

        save.setDefaultButton(false);

        UIComponents.setupKeyboardNavigation(
                dialog,
                patientSearch,
                patientBox,
                doctorSearch,
                doctorBox,
                datePicker,
                timeBox,
                reasonField,
                statusBox,
                save
        );

        save.addEventFilter(ActionEvent.ACTION, event -> {

            // Required fields validation
            if (patientBox.getValue() == null
                    || doctorBox.getValue() == null
                    || datePicker.getValue() == null
                    || timeBox.getValue() == null
                    || statusBox.getValue() == null) {

                UIComponents.showAlert(
                        Alert.AlertType.WARNING,
                        "Please fill in all required fields."
                );

                event.consume();
                return;
            }

            int patientId = Integer.parseInt(
                    patientBox.getValue().split(" - ", 2)[0]
            );

            int doctorId = Integer.parseInt(
                    doctorBox.getValue().split(" - ", 2)[0]
            );

            LocalDateTime appointmentDate = LocalDateTime.of(
                    datePicker.getValue(),
                    timeBox.getValue()
            );

            String reason = reasonField.getText().trim();
            String status = statusBox.getValue();

            // Date validation
            if (status.equals("Scheduled")
                    && appointmentDate.isBefore(LocalDateTime.now())) {

                UIComponents.showAlert(
                        Alert.AlertType.WARNING,
                        "Scheduled appointments cannot be in the past."
                );

                event.consume();
                return;
            }

            if (status.equals("Completed")
                    && appointmentDate.isAfter(LocalDateTime.now())) {

                UIComponents.showAlert(
                        Alert.AlertType.WARNING,
                        "Completed appointments cannot be in the future."
                );

                event.consume();
                return;
            }

            // Reason validation
            if (reason.length() > 255) {

                UIComponents.showAlert(
                        Alert.AlertType.WARNING,
                        "Reason cannot exceed 255 characters."
                );

                event.consume();
                return;
            }

            int appointmentId = isEdit
                    ? Integer.parseInt(row.get(0))
                    : 0;

            try {

                // Protect appointments linked to medical records
                if (isEdit && operations.hasMedicalRecord(appointmentId)) {

                    if (patientId != Integer.parseInt(row.get(7))
                            || doctorId != Integer.parseInt(row.get(8))
                            || status.equals("Cancelled")) {

                        UIComponents.showAlert(
                                Alert.AlertType.WARNING,
                                "Cannot change the patient, doctor or cancel an appointment linked to a medical record."
                        );

                        event.consume();
                        return;
                    }
                }

                // Check appointment conflicts
                if (!status.equals("Cancelled")
                        && operations.hasAppointmentConflict(
                        appointmentId,
                        patientId,
                        doctorId,
                        appointmentDate
                )) {

                    UIComponents.showAlert(
                            Alert.AlertType.WARNING,
                            "The doctor or patient already has an appointment at this time."
                    );

                    event.consume();
                    return;
                }

            } catch (SQLException | ClassNotFoundException e) {

                UIComponents.showAlert(
                        Alert.AlertType.ERROR,
                        "Unable to validate appointment."
                );

                event.consume();
                return;
            }

            boolean success;

            if (isEdit) {

                success = operations.updateAppointment(
                        appointmentId,
                        patientId,
                        doctorId,
                        appointmentDate,
                        reason,
                        status
                );

            } else {

                success = operations.addAppointment(
                        patientId,
                        doctorId,
                        appointmentDate,
                        reason,
                        status
                );
            }

            if (success) {

                UIComponents.showAlert(
                        Alert.AlertType.INFORMATION,
                        isEdit
                                ? "Appointment updated successfully."
                                : "Appointment added successfully."
                );

                showAppointments();

            } else {

                UIComponents.showAlert(
                        Alert.AlertType.ERROR,
                        "Failed to save appointment."
                );

                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void showAppointments() {

        pageTitle.setText("Appointments");

        Label title = new Label("Appointments");
        Button addButton = new Button("+ Add Appointment");
        addButton.setOnAction(e -> showAppointmentDialog(null));

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
        TableView<ObservableList<String>> table = new TableView<>();

        String[] columnNames = {"ID", "Patient", "Doctor", "Date", "Time", "Reason", "Status"};

        for (int i = 0; i < columnNames.length; i++) {

            final int index = i;

            TableColumn<ObservableList<String>, String> column =
                    new TableColumn<>(columnNames[i]);

            column.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(data.getValue().get(index))
            );

            table.getColumns().add(column);
        }
        table.getColumns().add(createActionsColumn(
                row -> showAppointmentDialog(row),
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
        TableView<ObservableList<String>> table = new TableView<>();

        String[] columnNames = {
                "Record ID",
                "Patient",
                "Doctor",
                "Appointment ID",
                "Record Date"
        };

        for (int i = 0; i < columnNames.length; i++) {

            final int index = i;

            TableColumn<ObservableList<String>, String> column =
                    new TableColumn<>(columnNames[i]);

            column.setCellValueFactory(data ->
                    new ReadOnlyStringWrapper(data.getValue().get(index))
            );

            table.getColumns().add(column);
        }

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


}
