package org.example.nabd;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.nabd.TestConnection;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
public class UIComponents {

    public static Button createLogoutButton() {

        Button button = new Button("Logout");

        button.setStyle("-fx-background-color: #176B68;" + "-fx-text-fill: white;" + "-fx-font-size: 14px;" +
                        "-fx-background-radius: 8;" + "-fx-padding: 10 20;");

        button.setOnAction(e -> TestConnection.openLogin());

        return button;
    }

    public static VBox createLogo(double width, double height) {

        URL logoUrl = UIComponents.class.getResource(
                "/images/nabd-logo.png"
        );

        VBox logoBox = new VBox(0);

        if (logoUrl != null) {

            ImageView logo = new ImageView(
                    new Image(logoUrl.toExternalForm())
            );

            logo.setFitWidth(width);
            logo.setFitHeight(height);
            logo.setPreserveRatio(true);

            logoBox.getChildren().add(logo);
        }

        logoBox.setAlignment(Pos.CENTER);

        return logoBox;
    }

    public static void showAlert(Alert.AlertType type, String message) {

        Alert alert = new Alert(type);
        alert.setTitle("Nabd");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void updateSearch(
            TableView<ObservableList<String>> table,
            ComboBox<String> searchBy,
            TextField searchField,
            String tableName) {

        HospitalOperations operations = new HospitalOperations();

        String search = searchField.getText().trim();

        if (search.isEmpty()) {
            table.setItems(operations.getData(tableName));
        } else {
            table.setItems(
                    operations.searchData(tableName, searchBy.getValue(), search)
            );
        }
    }

    public static void setupSearch(
            TableView<ObservableList<String>> table,
            ComboBox<String> searchBy,
            TextField searchField,
            String tableName) {

        searchField.textProperty().addListener((obs, oldValue, newValue) ->
                updateSearch(table, searchBy, searchField, tableName)
        );

        searchBy.setOnAction(e ->
                updateSearch(table, searchBy, searchField, tableName)
        );
    }
    public static void setupKeyboardNavigation(Dialog<?> dialog, Node... fields) {

        if (fields.length == 0) {
            return;
        }

        dialog.setOnShown(e -> Platform.runLater(() -> fields[0].requestFocus()));

        for (int i = 0; i < fields.length; i++) {

            final int index = i;

            fields[i].addEventFilter(KeyEvent.KEY_PRESSED, event -> {

                if (fields[index] instanceof ComboBoxBase<?> combo && combo.isShowing()) {
                    return;
                }

                if (event.getCode() == KeyCode.ENTER && index < fields.length - 1) {

                    fields[index + 1].requestFocus();
                    event.consume();

                } else if (event.getCode() == KeyCode.DOWN && fields[index] instanceof TextField && index < fields.length - 1) {

                    fields[index + 1].requestFocus();
                    event.consume();

                } else if (event.getCode() == KeyCode.UP && fields[index] instanceof TextField && index > 0) {

                    fields[index - 1].requestFocus();
                    event.consume();
                }
            });
        }
    }
    protected    static int showPatientDialog(ObservableList<String> row) {
        boolean isEdit = row != null;
        int[] savedPatientId = {-1};
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Patient" : "Add Patient");

        TextField firstName = new TextField();
        TextField lastName = new TextField();
        DatePicker dateOfBirth = new DatePicker();

        ComboBox<String> gender = new ComboBox<>();
        gender.getItems().addAll("Male", "Female", "Other");

        TextField phone = new TextField();
        TextField email = new TextField();
        TextField address = new TextField();

        ComboBox<String> bloodType = new ComboBox<>();
        bloodType.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

        dateOfBirth.setEditable(false);

        // Fill existing data when editing
        if (isEdit) {
            firstName.setText(row.get(1));
            lastName.setText(row.get(2));
            dateOfBirth.setValue(LocalDate.parse(row.get(3)));
            gender.setValue(row.get(4));
            phone.setText(row.get(5));
            email.setText(row.get(6));
            address.setText(row.get(7));
            bloodType.setValue(row.get(8));
        }

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        grid.addRow(0, new Label("First Name:"), firstName);
        grid.addRow(1, new Label("Last Name:"), lastName);
        grid.addRow(2, new Label("Date of Birth:"), dateOfBirth);
        grid.addRow(3, new Label("Gender:"), gender);
        grid.addRow(4, new Label("Phone:"), phone);
        grid.addRow(5, new Label("Email:"), email);
        grid.addRow(6, new Label("Address:"), address);
        grid.addRow(7, new Label("Blood Type:"), bloodType);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveType);
        saveButton.setDefaultButton(false);

        UIComponents.setupKeyboardNavigation(dialog, firstName, lastName, dateOfBirth, gender, phone, email, address, bloodType, saveButton);        saveButton.addEventFilter(ActionEvent.ACTION, event -> {

            String first = firstName.getText().trim();
            String last = lastName.getText().trim();
            String patientPhone = phone.getText().trim();
            String patientEmail = email.getText().trim();
            String patientAddress = address.getText().trim();

            // Required fields
            if (first.isEmpty() || last.isEmpty() || dateOfBirth.getValue() == null || gender.getValue() == null) {

                UIComponents.showAlert(Alert.AlertType.ERROR, "Please fill all required fields.");
                event.consume();
                return;
            }

            // Date validation
            if (dateOfBirth.getValue().isAfter(LocalDate.now())) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Date of birth cannot be in the future.");
                event.consume();
                return;
            }

            // Phone validation
            if (!patientPhone.isEmpty() && !patientPhone.matches("[0-9+() -]{7,25}")) {

                UIComponents.showAlert(Alert.AlertType.ERROR, "Invalid phone number.");
                event.consume();
                return;
            }

            // Email validation
            if (!patientEmail.isEmpty() && !patientEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "Invalid email address.");
                event.consume();
                return;
            }

            // Length validation
            if (first.length() > 50 || last.length() > 50 || patientEmail.length() > 120 || patientAddress.length() > 255) {
                UIComponents.showAlert(Alert.AlertType.ERROR, "One or more fields exceed the allowed length.");
                event.consume();
                return;
            }

            HospitalOperations operations = new HospitalOperations();

            boolean success;

            if (isEdit) {

                int patientId = Integer.parseInt(row.get(0));
                success = operations.updatePatient(patientId, first, last, dateOfBirth.getValue(), gender.getValue(), patientPhone, patientEmail, patientAddress, bloodType.getValue());
                if (success) {
                    savedPatientId[0] = patientId;
                }

            } else {
                savedPatientId[0] = operations.addPatient(first, last, dateOfBirth.getValue(), gender.getValue(), patientPhone, patientEmail, patientAddress, bloodType.getValue());
                success = savedPatientId[0] > 0;
            }
            if (success) {

                UIComponents.showAlert(Alert.AlertType.INFORMATION, isEdit ? "Patient updated successfully." : "Patient added successfully.");


            } else {

                UIComponents.showAlert(Alert.AlertType.ERROR, "Unable to save patient. Check the entered data.");

                event.consume();
            }
        });
        dialog.showAndWait();
        return savedPatientId[0];

    }
    public static void loadPatients(ComboBox<String> patientBox, int selectedPatientId) {

        HospitalOperations operations = new HospitalOperations();

        patientBox.getItems().clear();
        patientBox.setValue(null);

        String role = TestConnection.getCurrentRole();

        ObservableList<ObservableList<String>> patients;

        if ("Doctor".equals(role)) {
            patients = operations.getDoctorPatients("Name", "");
        } else if ("Administrator".equals(role) || "Receptionist".equals(role)) {
            patients = operations.getData("Patient");
        } else {
            return;
        }

        for (ObservableList<String> patient : patients) {

            String item = patient.get(0) + " - " + patient.get(1) + " " + patient.get(2) + " - " + (patient.get(5) == null ? "" : patient.get(5));

            patientBox.getItems().add(item);

            if (Integer.parseInt(patient.get(0)) == selectedPatientId) {
                patientBox.setValue(item);
            }
        }
    }
    public static void setupComboBoxSearch(ComboBox<String> comboBox, TextField searchField, Runnable reload) {

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {

            reload.run();

            String search = newValue.trim().toLowerCase();

            comboBox.getItems().removeIf(item ->
                    !item.toLowerCase().contains(search));

            if (!search.isEmpty()) {
                comboBox.show();
            }
        });
    }
    public static void loadDoctors(ComboBox<String> doctorBox, int selectedDoctorId) {

        HospitalOperations operations = new HospitalOperations();

        doctorBox.getItems().clear();
        doctorBox.setValue(null);

        for (ObservableList<String> doctor : operations.getData("Doctor")) {

            String item = doctor.get(0) + " - " + doctor.get(1) + " " + doctor.get(2) + " - " + doctor.get(3);

            doctorBox.getItems().add(item);

            if (Integer.parseInt(doctor.get(0)) == selectedDoctorId) {
                doctorBox.setValue(item);
            }
        }
    }
    public static boolean showMedicalRecordDetails(ObservableList<String> row, String role) {

        String currentRole = TestConnection.getCurrentRole();

        boolean isAdmin = "Administrator".equals(currentRole);

        boolean isDoctor = "Doctor".equals(currentRole)
                && TestConnection.getCurrentDoctorId() != null;

        if (!isAdmin && !isDoctor) {
            showAlert(Alert.AlertType.ERROR,
                    "You are not authorized to view medical records.");
            return false;
        }

        if (row == null || row.isEmpty()) {
            return false;
        }

        HospitalOperations operations = new HospitalOperations();

        ObservableList<String> authorizedRow =
                operations.getMedicalRecords("RecordID", row.get(0))
                        .stream()
                        .filter(record -> record.get(0).equals(row.get(0)))
                        .findFirst()
                        .orElse(null);

        if (authorizedRow == null) {
            showAlert(Alert.AlertType.ERROR,
                    "You are not authorized to view this record.");
            return false;
        }

        row.setAll(authorizedRow);

        boolean canEdit = isAdmin || isDoctor;

        boolean[] updated = {false};

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Medical Record Details");
        dialog.setHeaderText(null);

        // Header
        Label title = new Label("Medical Record Details");

        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #075D61;");

        Label recordNumber = new Label("Record #" + row.get(0));

        VBox header = new VBox(5, title, recordNumber);

        // Patient information
        Label patientName = new Label(row.get(1));

        patientName.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label patientId = new Label("Patient ID: " + row.get(9));

        DatePicker recordDate = new DatePicker(LocalDate.parse(row.get(4)));

        recordDate.setEditable(false);
        recordDate.setDisable(true);

        GridPane information = new GridPane();

        information.setHgap(50);
        information.setVgap(15);

        information.addRow(0, new Label("Doctor"), new Label(row.get(2)));

        information.addRow(1, new Label("Specialization"), new Label(row.get(10)));

        information.addRow(2, new Label("Record Date"), recordDate);

        information.addRow(3, new Label("Appointment ID"), new Label(row.get(3)));

        VBox patientCard = new VBox(12,
                patientName,
                patientId,
                new Separator(),
                information
        );

        patientCard.setPadding(new Insets(20));

        patientCard.setStyle("-fx-background-color: #EAF3F2; -fx-background-radius: 10;");

        // Clinical information
        Label clinicalTitle = new Label("Clinical Information");

        clinicalTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #075D61;");

        String[] medicalLabels = {
                "Symptoms",
                "Diagnosis",
                "Treatment",
                "Notes"
        };

        TextArea[] medicalFields = new TextArea[4];

        VBox clinicalContent = new VBox(15);

        for (int i = 0; i < medicalLabels.length; i++) {

            Label label = new Label(medicalLabels[i]);

            label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            String value = row.get(i + 5);

            TextArea area = new TextArea(value == null ? "" : value);

            area.setEditable(false);
            area.setWrapText(true);
            area.setPrefRowCount(3);
            area.setMaxWidth(Double.MAX_VALUE);

            medicalFields[i] = area;

            VBox section = new VBox(8, label, area);

            section.setPadding(new Insets(12));

            section.setStyle("-fx-border-color: #DCE5E5; -fx-border-radius: 8;");

            clinicalContent.getChildren().add(section);
        }

        // Buttons
        Button editButton = new Button("Edit");
        Button saveButton = new Button("Save Changes");
        Button cancelButton = new Button("Cancel");

        editButton.setVisible(canEdit);
        editButton.setManaged(canEdit);

        saveButton.setVisible(false);
        saveButton.setManaged(false);

        cancelButton.setVisible(false);
        cancelButton.setManaged(false);

        HBox buttons = new HBox(10,
                editButton,
                cancelButton,
                saveButton
        );

        buttons.setAlignment(Pos.CENTER_RIGHT);

        // Change between View and Edit mode
        Consumer<Boolean> setEditMode = editing -> {

            title.setText(editing
                    ? "Edit Medical Record"
                    : "Medical Record Details");

            recordDate.setDisable(!editing || !isAdmin);

            for (TextArea field : medicalFields) {

                field.setEditable(editing && canEdit);
            }

            editButton.setVisible(!editing && canEdit);
            editButton.setManaged(!editing && canEdit);

            saveButton.setVisible(editing && canEdit);
            saveButton.setManaged(editing && canEdit);

            cancelButton.setVisible(editing);
            cancelButton.setManaged(editing);
        };

        // Edit
        editButton.setOnAction(e -> {

            if (!canEdit) {
                return;
            }

            setEditMode.accept(true);

            if (isAdmin) {
                recordDate.requestFocus();
            } else {
                medicalFields[0].requestFocus();
            }
        });

        // Cancel editing
        cancelButton.setOnAction(e -> {

            recordDate.setValue(LocalDate.parse(row.get(4)));

            for (int i = 0; i < medicalFields.length; i++) {

                String value = row.get(i + 5);

                medicalFields[i].setText(value == null ? "" : value);
            }

            setEditMode.accept(false);
        });

        // Save changes
        saveButton.setOnAction(e -> {

            if (!canEdit) {

                showAlert(Alert.AlertType.ERROR,
                        "You are not authorized to edit this record.");

                return;
            }

            if (recordDate.getValue() == null) {

                showAlert(Alert.AlertType.WARNING,
                        "Please select a record date.");

                return;
            }

            if (recordDate.getValue().isAfter(LocalDate.now())) {

                showAlert(Alert.AlertType.WARNING,
                        "Record date cannot be in the future.");

                return;
            }

            String symptoms = medicalFields[0].getText().trim();
            String diagnosis = medicalFields[1].getText().trim();
            String treatment = medicalFields[2].getText().trim();
            String notes = medicalFields[3].getText().trim();

            if (diagnosis.isEmpty()) {

                showAlert(Alert.AlertType.WARNING,
                        "Diagnosis is required.");

                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);

            confirm.setTitle("Confirm Changes");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to update this medical record?");

            if (confirm.showAndWait().orElse(ButtonType.CANCEL)
                    != ButtonType.OK) {

                return;
            }

            boolean success = operations.updateMedicalRecord(
                    Integer.parseInt(row.get(0)),
                    recordDate.getValue(),
                    symptoms,
                    diagnosis,
                    treatment,
                    notes
            );

            if (success) {

                // Update current row
                row.set(4, recordDate.getValue().toString());
                row.set(5, symptoms);
                row.set(6, diagnosis);
                row.set(7, treatment);
                row.set(8, notes);

                updated[0] = true;

                setEditMode.accept(false);

                showAlert(Alert.AlertType.INFORMATION,
                        "Medical record updated successfully.");

            } else {

                showAlert(Alert.AlertType.ERROR,
                        "Failed to update medical record.");
            }
        });

        // Complete layout
        VBox content = new VBox(20,
                header,
                new Separator(),
                patientCard,
                clinicalTitle,
                clinicalContent,
                buttons
        );

        content.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        dialog.getDialogPane().setContent(scrollPane);

        dialog.getDialogPane().setPrefSize(750, 650);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();

        return updated[0];
    }
    public static TableColumn<ObservableList<String>, Void> createViewColumn(Consumer<ObservableList<String>> onView) {

        TableColumn<ObservableList<String>, Void> column =
                new TableColumn<>("Actions");

        column.setCellFactory(col -> new TableCell<>() {

            private final Button viewButton = new Button("View");

            {
                viewButton.setOnAction(e -> {

                    ObservableList<String> row = getTableRow().getItem();

                    if (row != null) {

                        onView.accept(row);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {

                super.updateItem(item, empty);

                setGraphic(empty ? null : viewButton);
            }
        });

        return column;
    }
    public static boolean showAddMedicalRecordDialog() {

        String role = TestConnection.getCurrentRole();

        if (!"Administrator".equals(role)
                && !("Doctor".equals(role)
                && TestConnection.getCurrentDoctorId() != null)) {

            showAlert(Alert.AlertType.ERROR,
                    "You are not authorized to add medical records.");

            return false;
        }

        boolean[] added = {false};

        HospitalOperations operations = new HospitalOperations();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Medical Record");
        dialog.setHeaderText("Enter Medical Record Information");

        // Patient
        ComboBox<String> patientBox = new ComboBox<>();

        TextField patientSearch = new TextField();
        patientSearch.setPromptText("Search by ID, name or phone...");

        UIComponents.loadPatients(patientBox, -1);

        UIComponents.setupComboBoxSearch(
                patientBox,
                patientSearch,
                () -> UIComponents.loadPatients(patientBox, -1)
        );

        // Appointment
        ComboBox<String> appointmentBox = new ComboBox<>();
        appointmentBox.setDisable(true);

        ObservableList<ObservableList<String>> appointments = FXCollections.observableArrayList();

        // Doctor information
        Label doctorName = new Label("—");
        Label specialization = new Label("—");
        Label appointmentDate = new Label("—");

        // Record date
        DatePicker recordDate = new DatePicker(LocalDate.now());
        recordDate.setEditable(false);

        // Clinical information
        String[] labels = {
                "Symptoms",
                "Diagnosis",
                "Treatment",
                "Notes"
        };

        TextArea[] medicalFields = new TextArea[4];

        VBox clinicalContent = new VBox(15);

        for (int i = 0; i < labels.length; i++) {

            Label label = new Label(labels[i]);

            label.setStyle("-fx-font-weight: bold;");

            TextArea area = new TextArea();

            area.setPromptText("Enter " + labels[i].toLowerCase() + "...");
            area.setWrapText(true);
            area.setPrefRowCount(3);

            medicalFields[i] = area;

            clinicalContent.getChildren().add(
                    new VBox(5, label, area)
            );
        }

        Label appointmentMessage = new Label();
        appointmentMessage.setStyle("-fx-text-fill: #B45309;");

        // Load appointments when selecting a patient
        patientBox.valueProperty().addListener((obs, oldValue, newValue) -> {

            appointmentBox.getItems().clear();
            appointmentBox.setValue(null);

            appointments.clear();

            doctorName.setText("—");
            specialization.setText("—");
            appointmentDate.setText("—");

            appointmentMessage.setText("");

            appointmentBox.setDisable(true);

            if (newValue == null) {
                return;
            }

            int patientId = Integer.parseInt(
                    newValue.split(" - ", 2)[0]
            );

            appointments.setAll(
                    operations.getAvailableAppointments(patientId)
            );

            for (ObservableList<String> appointment : appointments) {

                String item = appointment.get(0)
                        + " - " + appointment.get(4)
                        + " - " + appointment.get(5)
                        + " - " + appointment.get(2);

                appointmentBox.getItems().add(item);
            }

            if (appointments.isEmpty()) {

                appointmentMessage.setText(
                        "No eligible appointments found for this patient."
                );

            } else {

                appointmentBox.setDisable(false);
            }
        });

        // Display doctor information automatically
        appointmentBox.valueProperty().addListener((obs, oldValue, newValue) -> {

            doctorName.setText("—");
            specialization.setText("—");
            appointmentDate.setText("—");

            if (newValue == null) {
                return;
            }

            String appointmentId = newValue.split(" - ", 2)[0];

            for (ObservableList<String> appointment : appointments) {

                if (appointment.get(0).equals(appointmentId)) {

                    doctorName.setText(appointment.get(2));

                    specialization.setText(appointment.get(3));

                    appointmentDate.setText(appointment.get(4));

                    break;
                }
            }
        });

        // Layout
        patientBox.setMaxWidth(Double.MAX_VALUE);
        appointmentBox.setMaxWidth(Double.MAX_VALUE);

        VBox patientContainer = new VBox(
                8, patientSearch, patientBox
        );

        VBox appointmentContainer = new VBox(
                8, appointmentBox, appointmentMessage
        );

        GridPane information = new GridPane();

        information.setHgap(20);
        information.setVgap(15);

        information.addRow(0, new Label("Patient"), patientContainer);
        information.addRow(1, new Label("Appointment"), appointmentContainer);
        information.addRow(2, new Label("Doctor"), doctorName);
        information.addRow(3, new Label("Specialization"), specialization);
        information.addRow(4, new Label("Appointment Date"), appointmentDate);
        information.addRow(5, new Label("Record Date"), recordDate);

        VBox informationCard = new VBox(information);

        informationCard.setPadding(new Insets(20));

        informationCard.setStyle("-fx-background-color: #EAF3F2; -fx-background-radius: 10;");

        Label clinicalTitle = new Label("Clinical Information");

        clinicalTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #075D61;");

        VBox content = new VBox(
                20,
                informationCard,
                clinicalTitle,
                clinicalContent
        );

        content.setPadding(new Insets(20));

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().setPrefSize(750, 650);

        // Buttons
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
                appointmentBox,
                recordDate,
                medicalFields[0],
                medicalFields[1],
                medicalFields[2],
                medicalFields[3],
                save
        );

        // Validation and saving
        save.addEventFilter(ActionEvent.ACTION, event -> {

            if (patientBox.getValue() == null
                    || appointmentBox.getValue() == null
                    || recordDate.getValue() == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Please fill in all required fields."
                );

                event.consume();
                return;
            }

            if (recordDate.getValue().isAfter(LocalDate.now())) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Record date cannot be in the future."
                );

                event.consume();
                return;
            }

            LocalDate selectedAppointmentDate =
                    LocalDate.parse(appointmentDate.getText());

            if (recordDate.getValue().isBefore(selectedAppointmentDate)) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Record date cannot be before the appointment date."
                );

                event.consume();
                return;
            }

            String symptoms = medicalFields[0].getText().trim();
            String diagnosis = medicalFields[1].getText().trim();
            String treatment = medicalFields[2].getText().trim();
            String notes = medicalFields[3].getText().trim();

            if (diagnosis.isEmpty()) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Diagnosis is required."
                );

                event.consume();
                return;
            }

            int appointmentId = Integer.parseInt(
                    appointmentBox.getValue().split(" - ", 2)[0]
            );

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);

            confirm.setTitle("Confirm Addition");
            confirm.setHeaderText(null);
            confirm.setContentText(
                    "Are you sure you want to add this medical record?"
            );

            if (confirm.showAndWait().orElse(ButtonType.CANCEL)
                    != ButtonType.OK) {

                event.consume();
                return;
            }

            added[0] = operations.addMedicalRecord(
                    appointmentId,
                    recordDate.getValue(),
                    symptoms,
                    diagnosis,
                    treatment,
                    notes
            );

            if (added[0]) {

                showAlert(
                        Alert.AlertType.INFORMATION,
                        "Medical record added successfully."
                );

            } else {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Failed to add medical record. Please check appointment eligibility."
                );

                event.consume();
            }
        });

        dialog.showAndWait();

        return added[0];
    }

    public static Button menuButton(String text) {

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

    public static HBox createHeader(Label pageTitle, String role) {

        pageTitle.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #075D61;"
        );

        Label roleLabel = new Label(role);

        roleLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #555555;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(20, pageTitle, spacer, roleLabel);

        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 30, 0, 30));
        header.setPrefHeight(75);

        header.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #E5E7EB;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        return header;
    }

    public static ScrollPane createContent(VBox content) {

        content.setPadding(new Insets(30));
        content.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.setStyle(
                "-fx-background: #F5F7FA;" +
                        "-fx-background-color: transparent;"
        );

        return scrollPane;
    }

    public static TableView<ObservableList<String>> createTable(
            String[] columnNames, boolean combineName) {

        TableView<ObservableList<String>> table = new TableView<>();

        for (int i = 0; i < columnNames.length; i++) {

            final int index = i;

            TableColumn<ObservableList<String>, String> column =
                    new TableColumn<>(columnNames[i]);

            column.setCellValueFactory(data -> {

                ObservableList<String> row = data.getValue();

                if (combineName && index == 1) {
                    return new ReadOnlyStringWrapper(
                            row.get(1) + " " + row.get(2)
                    );
                }

                int dataIndex = combineName && index > 1
                        ? index + 1 : index;

                return new ReadOnlyStringWrapper(row.get(dataIndex));
            });

            table.getColumns().add(column);
        }

        return table;
    }

    public static TableColumn<ObservableList<String>, Void>
    createEditColumn(Consumer<ObservableList<String>> onEdit) {

        TableColumn<ObservableList<String>, Void> column =
                new TableColumn<>("Actions");

        column.setCellFactory(param -> new TableCell<>() {

            private final Button editButton = new Button("Edit");

            {
                editButton.setOnAction(e -> {
                    ObservableList<String> row =
                            getTableView().getItems().get(getIndex());

                    onEdit.accept(row);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                setGraphic(empty ? null : editButton);
            }
        });

        return column;
    }

    public static void showAppointmentDialog(
            ObservableList<String> row, Runnable onSuccess) {

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

                onSuccess.run();

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


}