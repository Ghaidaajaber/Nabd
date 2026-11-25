package org.example.nabd;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class DoctorDashboard {

    private BorderPane root;
    private Label pageTitle;
    private VBox content;
    private VBox menu;

    private final String PRIMARY = "#075D61";
    private final String BACKGROUND = "#F5F7FA";

    private final HospitalOperations operations =
            new HospitalOperations();

    private final int ROWS_PER_PAGE = 7;

    private LocalDate weekStart = LocalDate.now()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));

    private String patientSearch = "";
    private String selectedStatus = "All Status";

    private boolean weeklyView = true;

    private LocalDate fromDate;
    private LocalDate toDate;

    public Scene createScene() {

        root = new BorderPane();

        root.setStyle("-fx-background-color: " + BACKGROUND + ";");

        createSidebar();
        createHeader();
        showDashboard();

        return new Scene(root, 1100, 700);
    }

    // Sidebar

    private void createSidebar() {

        VBox logoBox = UIComponents.createLogo(140, 140);

        logoBox.setPadding(new Insets(0, 0, 30, 5));

        Button dashboard = UIComponents.menuButton("Dashboard");
        Button appointments = UIComponents.menuButton("My Appointments");
        Button patients = UIComponents.menuButton("Patients");
        Button medicalRecords = UIComponents.menuButton("Medical Records");

        dashboard.setOnAction(e -> showPage("Dashboard"));
        appointments.setOnAction(e -> showPage("My Appointments"));
        patients.setOnAction(e -> showPage("Patients"));
        medicalRecords.setOnAction(e -> showPage("Medical Records"));

        menu = new VBox(
                8, dashboard, appointments,
                patients, medicalRecords
        );

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

    // Header

    private void createHeader() {

        pageTitle = new Label("Dashboard");

        root.setTop(
                UIComponents.createHeader(pageTitle, "Doctor")
        );
    }

    private void setContent(Node node) {

        content = new VBox(node);

        root.setCenter(
                UIComponents.createContent(content)
        );
    }

    // Navigation

    private void showPage(String page) {

        pageTitle.setText(page);

        switch (page) {

            case "Dashboard" -> showDashboard();

            case "My Appointments" -> showAppointments();

            case "Patients" -> showPatients();

            case "Medical Records" -> showMedicalRecords();
        }
    }

    // Dashboard

    // Dashboard

    private void showDashboard() {

        pageTitle.setText("Dashboard");

        Label welcome = new Label("Welcome, Doctor!");

        welcome.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        Label description = new Label(
                "Here is an overview of your daily activities."
        );

        description.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #777777;"
        );

        // Load Data

        ObservableList<ObservableList<String>> todayAppointments =
                operations.getAppointments(
                        "PatientName",
                        "",
                        "All Status",
                        LocalDate.now()
                );

        int totalPatients =
                operations.getDoctorPatients("Name", "").size();

        int totalRecords =
                operations.getMedicalRecords("PatientName", "").size();

        long completedToday = todayAppointments.stream()
                .filter(row -> "Completed".equals(row.get(6)))
                .count();

        // Statistics Cards

        HBox firstRow = new HBox(
                15,
                createStatisticCard(
                        "Today's Appointments",
                        todayAppointments.size()
                ),
                createStatisticCard(
                        "My Patients",
                        totalPatients
                )
        );

        HBox secondRow = new HBox(
                15,
                createStatisticCard(
                        "Completed Today",
                        completedToday
                ),
                createStatisticCard(
                        "Medical Records",
                        totalRecords
                )
        );

        // Today's Appointments Header

        Label appointmentsTitle =
                new Label("Today's Appointments");

        appointmentsTitle.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        Button viewAllButton = new Button("View All");

        styleScheduleButton(viewAllButton, true);

        viewAllButton.setOnAction(e -> {

            weeklyView = false;

            patientSearch = "";

            selectedStatus = "All Status";

            fromDate = null;

            toDate = null;

            showPage("My Appointments");
        });

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(
                10,
                appointmentsTitle,
                spacer,
                viewAllButton
        );

        header.setAlignment(Pos.CENTER_LEFT);

        // Today's Appointments Table

        TableView<ObservableList<String>> table =
                new TableView<>();

        table.getColumns().addAll(

                createColumn("Appointment ID", 0),

                createColumn("Patient", 1),

                createColumn("Time", 4),

                createStatusColumn(),

                UIComponents.createViewColumn(
                        this::showAppointmentDetails
                )
        );

        table.setItems(todayAppointments);

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        table.setPrefHeight(280);

        table.setPlaceholder(
                new Label("No appointments for today")
        );

        VBox appointmentsBox = new VBox(
                15,
                header,
                table
        );

        stylePanel(appointmentsBox);

        // Dashboard Layout

        VBox dashboardContent = new VBox(
                20,
                welcome,
                description,
                firstRow,
                secondRow,
                appointmentsBox
        );

        dashboardContent.setPadding(new Insets(20));

        setContent(dashboardContent);
    }

// Shared Statistics Card

    private VBox createStatisticCard(String title, long value) {

        Label titleLabel = new Label(title);

        titleLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #64748B;"
        );

        Label valueLabel = new Label(String.valueOf(value));

        valueLabel.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        VBox card = new VBox(10, titleLabel, valueLabel);

        stylePanel(card);

        card.setMinWidth(0);
        card.setPrefWidth(0);
        card.setMaxWidth(Double.MAX_VALUE);

        card.setMinHeight(110);
        card.setPrefHeight(110);
        card.setMaxHeight(110);

        HBox.setHgrow(card, Priority.ALWAYS);

        return card;
    }
    // My Appointments

    private void showAppointments() {

        pageTitle.setText("My Appointments");

        if (weeklyView) {

            showWeeklySchedule();

        } else {

            showAllAppointments();
        }
    }

    // Appointment Tabs

    private HBox createAppointmentTabs() {

        Button weeklyButton = new Button("Weekly Schedule");
        Button allButton = new Button("All Appointments");

        styleScheduleButton(weeklyButton, weeklyView);
        styleScheduleButton(allButton, !weeklyView);

        weeklyButton.setMaxWidth(Double.MAX_VALUE);
        allButton.setMaxWidth(Double.MAX_VALUE);

        weeklyButton.setPrefWidth(200);
        allButton.setPrefWidth(200);

        weeklyButton.setPrefHeight(45);
        allButton.setPrefHeight(45);

        HBox.setHgrow(weeklyButton, Priority.ALWAYS);
        HBox.setHgrow(allButton, Priority.ALWAYS);

        weeklyButton.setOnAction(e -> {

            weeklyView = true;

            showAppointments();
        });

        allButton.setOnAction(e -> {

            weeklyView = false;

            showAppointments();
        });

        HBox tabs = new HBox(
                5, weeklyButton, allButton
        );

        tabs.setPadding(new Insets(5));

        tabs.setStyle(
                "-fx-background-color: #E8EFF0;" +
                        "-fx-background-radius: 10;"
        );

        return tabs;
    }

    // Shared Appointment Search

    private TextField createPatientSearch() {

        TextField searchField = new TextField(patientSearch);

        searchField.setPromptText("Search by patient name...");
        searchField.setMinWidth(100);

        styleFilter(searchField);

        HBox.setHgrow(searchField, Priority.ALWAYS);

        return searchField;
    }

    // Shared Status Filter

    private ComboBox<String> createStatusFilter() {

        ComboBox<String> statusFilter = new ComboBox<>();

        statusFilter.getItems().addAll(
                "All Status",
                "Scheduled",
                "Completed",
                "Cancelled"
        );

        statusFilter.setValue(selectedStatus);
        statusFilter.setPrefWidth(130);

        styleFilter(statusFilter);

        return statusFilter;
    }

    // Shared Appointment Data

    private ObservableList<ObservableList<String>>
    getDoctorAppointments() {

        return operations.getAppointments(
                "PatientName",
                patientSearch.trim(),
                selectedStatus,
                null
        );
    }

    // All Appointments

    private void showAllAppointments() {

        TextField searchField = createPatientSearch();

        ComboBox<String> statusFilter = createStatusFilter();

        DatePicker fromPicker = new DatePicker(fromDate);
        DatePicker toPicker = new DatePicker(toDate);

        fromPicker.setPromptText("From Date");
        toPicker.setPromptText("To Date");

        fromPicker.setEditable(false);
        toPicker.setEditable(false);

        fromPicker.setPrefWidth(125);
        toPicker.setPrefWidth(125);

        styleFilter(fromPicker);
        styleFilter(toPicker);

        Button clearButton = new Button("Clear");

        styleScheduleButton(clearButton, true);

        HBox filters = new HBox(
                8,
                searchField,
                statusFilter,
                fromPicker,
                new Label("—"),
                toPicker,
                clearButton
        );

        filters.setAlignment(Pos.CENTER_LEFT);

        stylePanel(filters);

        // Appointments Table

        TableView<ObservableList<String>> table =
                new TableView<>();

        TableColumn<ObservableList<String>, String> idColumn =
                createColumn("#", 0);

        TableColumn<ObservableList<String>, String> patientColumn =
                createColumn("Patient", 1);

        TableColumn<ObservableList<String>, String> dateColumn =
                createColumn("Date", 3);

        TableColumn<ObservableList<String>, String> timeColumn =
                createColumn("Time", 4);

        idColumn.setPrefWidth(45);
        patientColumn.setPrefWidth(190);
        dateColumn.setPrefWidth(125);
        timeColumn.setPrefWidth(85);

        table.getColumns().addAll(
                idColumn,
                patientColumn,
                dateColumn,
                timeColumn,
                createStatusColumn(),
                UIComponents.createViewColumn(
                        this::showAppointmentDetails
                )
        );

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );

        table.setFixedCellSize(43);
        table.setPrefHeight(345);

        table.setPlaceholder(
                new Label("No appointments found")
        );

        table.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: transparent;" +
                        "-fx-table-cell-border-color: #E8EFF0;" +
                        "-fx-font-size: 13px;"
        );

        // Pagination

        Label countLabel = new Label();

        countLabel.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #64748B;"
        );

        Pagination pagination = new Pagination(1, 0);

        pagination.setMaxPageIndicatorCount(5);
        pagination.setPrefHeight(45);
        pagination.setMaxWidth(250);

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox footer = new HBox(
                10, countLabel, spacer, pagination
        );

        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(8));

        VBox tableBox = new VBox(
                5, table, footer
        );

        stylePanel(tableBox);

        Runnable refresh = () -> refreshAllAppointments(
                table, pagination, countLabel
        );

        searchField.textProperty().addListener(
                (obs, oldValue, newValue) -> {

                    patientSearch = newValue;

                    refresh.run();
                }
        );

        statusFilter.setOnAction(e -> {

            selectedStatus = statusFilter.getValue();

            refresh.run();
        });

        fromPicker.setOnAction(e -> {

            fromDate = fromPicker.getValue();

            refresh.run();
        });

        toPicker.setOnAction(e -> {

            toDate = toPicker.getValue();

            refresh.run();
        });

        clearButton.setOnAction(e -> {

            patientSearch = "";
            selectedStatus = "All Status";

            fromDate = null;
            toDate = null;

            showAllAppointments();
        });

        VBox page = new VBox(
                15,
                createAppointmentTabs(),
                filters,
                tableBox
        );

        page.setPadding(new Insets(15));

        refresh.run();

        setContent(page);
    }

    // Load and Filter All Appointments

    private void refreshAllAppointments(
            TableView<ObservableList<String>> table,
            Pagination pagination,
            Label countLabel) {

        ObservableList<ObservableList<String>> filtered =
                FXCollections.observableArrayList();

        boolean invalidRange =
                fromDate != null &&
                        toDate != null &&
                        fromDate.isAfter(toDate);

        if (!invalidRange) {

            ObservableList<ObservableList<String>> data =
                    getDoctorAppointments();

            for (ObservableList<String> row : data) {

                LocalDate date = LocalDate.parse(row.get(3));

                if (fromDate != null &&
                        date.isBefore(fromDate)) {

                    continue;
                }

                if (toDate != null &&
                        date.isAfter(toDate)) {

                    continue;
                }

                filtered.add(row);
            }
        }

        int pageCount = Math.max(
                1,
                (int) Math.ceil(
                        (double) filtered.size() / ROWS_PER_PAGE
                )
        );

        pagination.setPageFactory(null);

        pagination.setCurrentPageIndex(0);

        pagination.setPageCount(pageCount);

        pagination.setPageFactory(pageIndex -> {

            int start = Math.min(
                    pageIndex * ROWS_PER_PAGE,
                    filtered.size()
            );

            int end = Math.min(
                    start + ROWS_PER_PAGE,
                    filtered.size()
            );

            table.setItems(
                    FXCollections.observableArrayList(
                            filtered.subList(start, end)
                    )
            );

            if (invalidRange) {

                countLabel.setText("Invalid date range");

            } else {

                countLabel.setText(
                        "Showing " +
                                (filtered.isEmpty() ? 0 : start + 1) +
                                " - " + end +
                                " of " + filtered.size() +
                                " appointments"
                );
            }

            return new Region();
        });
    }

    // Shared Table Column

    private TableColumn<ObservableList<String>, String>
    createColumn(String title, int index) {

        TableColumn<ObservableList<String>, String> column =
                new TableColumn<>(title);

        column.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().get(index)
                )
        );

        column.setSortable(false);

        return column;
    }

    // Status Column

    private TableColumn<ObservableList<String>, String>
    createStatusColumn() {

        TableColumn<ObservableList<String>, String> column =
                createColumn("Status", 6);

        column.setPrefWidth(140);

        column.setCellFactory(col -> new TableCell<>() {

            @Override
            protected void updateItem(
                    String status, boolean empty) {

                super.updateItem(status, empty);

                setText(null);
                setGraphic(null);

                if (empty || status == null) {

                    return;
                }

                String symbol = switch (status) {

                    case "Completed" -> "✓";

                    case "Cancelled" -> "✕";

                    default -> "▣";
                };

                Label label = new Label(
                        symbol + " " + status
                );

                label.setStyle(
                        "-fx-background-color: " +
                                getStatusColor(status) + ";" +

                                "-fx-text-fill: " +
                                getStatusTextColor(status) + ";" +

                                "-fx-background-radius: 6;" +
                                "-fx-padding: 5 10;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;"
                );

                setAlignment(Pos.CENTER);

                setGraphic(label);
            }
        });

        return column;
    }

    // Shared Status Colors

    private String getStatusColor(String status) {

        return switch (status) {

            case "Completed" -> "#DDF2E2";

            case "Cancelled" -> "#FBE4E4";

            default -> "#D7EEEE";
        };
    }

    private String getStatusTextColor(String status) {

        return switch (status) {

            case "Completed" -> "#246A40";

            case "Cancelled" -> "#A83F3F";

            default -> PRIMARY;
        };
    }

    // Appointment Details

    private void showAppointmentDetails(
            ObservableList<String> row) {

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle("Appointment Details");

        alert.setHeaderText(row.get(1));

        alert.setContentText(
                "Appointment ID: " + row.get(0) +
                        "\nPatient: " + row.get(1) +
                        "\nDoctor: " + row.get(2) +
                        "\nDate: " + row.get(3) +
                        "\nTime: " + row.get(4) +
                        "\nReason: " + row.get(5) +
                        "\nStatus: " + row.get(6)
        );

        alert.showAndWait();
    }

    // Weekly Schedule

    private void showWeeklySchedule() {

        DateTimeFormatter monthFormatter =
                DateTimeFormatter.ofPattern(
                        "MMMM yyyy", Locale.ENGLISH
                );

        LocalDate weekEnd = weekStart.plusDays(6);

        String monthText = weekStart.format(monthFormatter);

        if (!weekStart.getMonth().equals(weekEnd.getMonth())) {

            monthText += " - " + weekEnd.format(monthFormatter);
        }

        Label monthLabel = new Label(monthText);

        monthLabel.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + PRIMARY + ";"
        );

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "MMM dd, yyyy", Locale.ENGLISH
                );

        Label weekLabel = new Label(
                weekStart.format(formatter) + " - " +
                        weekEnd.format(formatter)
        );

        weekLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: #64748B;"
        );

        VBox dateInfo = new VBox(
                5, monthLabel, weekLabel
        );

        // Week Navigation

        Button previousButton = new Button("❮");
        Button nextButton = new Button("❯");
        Button todayButton = new Button("Today");

        styleScheduleButton(previousButton, false);
        styleScheduleButton(nextButton, false);
        styleScheduleButton(todayButton, true);

        previousButton.setTooltip(
                new Tooltip("Previous week")
        );

        nextButton.setTooltip(
                new Tooltip("Next week")
        );

        todayButton.setTooltip(
                new Tooltip("Go to current week")
        );

        previousButton.setOnAction(e -> {

            weekStart = weekStart.minusWeeks(1);

            showAppointments();
        });

        nextButton.setOnAction(e -> {

            weekStart = weekStart.plusWeeks(1);

            showAppointments();
        });

        todayButton.setOnAction(e -> {

            weekStart = LocalDate.now().with(
                    TemporalAdjusters.previousOrSame(
                            DayOfWeek.SATURDAY
                    )
            );

            showAppointments();
        });

        DatePicker datePicker = new DatePicker(weekStart);

        datePicker.setEditable(false);
        datePicker.setPrefWidth(135);

        styleFilter(datePicker);

        datePicker.setOnAction(e -> {

            if (datePicker.getValue() != null) {

                weekStart = datePicker.getValue().with(
                        TemporalAdjusters.previousOrSame(
                                DayOfWeek.SATURDAY
                        )
                );

                showAppointments();
            }
        });

        HBox navigation = new HBox(
                8,
                previousButton,
                nextButton,
                datePicker,
                todayButton
        );

        navigation.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(
                10, dateInfo, spacer, navigation
        );

        header.setAlignment(Pos.CENTER_LEFT);

        // Search Patient

        Label searchLabel = new Label("Search Patient");

        searchLabel.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-text-fill: #334155;"
        );

        TextField searchField = createPatientSearch();

        VBox searchBox = new VBox(
                6, searchLabel, searchField
        );

        HBox.setHgrow(searchBox, Priority.ALWAYS);

        // Status Filter

        Label statusLabel = new Label("Appointment Status");

        statusLabel.setStyle(
                "-fx-font-weight: bold;" +
                        "-fx-text-fill: #334155;"
        );

        ComboBox<String> statusFilter = createStatusFilter();

        statusFilter.setPrefWidth(170);

        VBox statusBox = new VBox(
                6, statusLabel, statusFilter
        );

        Button resetButton = new Button("Reset Filters");

        styleScheduleButton(resetButton, false);

        HBox filters = new HBox(
                15, searchBox, statusBox, resetButton
        );

        filters.setAlignment(Pos.BOTTOM_LEFT);

        // Weekly Grid

        GridPane schedule = new GridPane();

        VBox[][] cells = new VBox[7][10];

        schedule.setMaxWidth(Double.MAX_VALUE);

        ColumnConstraints dayColumn = new ColumnConstraints();

        dayColumn.setPercentWidth(14);

        schedule.getColumnConstraints().add(dayColumn);

        for (int i = 0; i < 10; i++) {

            ColumnConstraints column = new ColumnConstraints();

            column.setPercentWidth(8.6);

            schedule.getColumnConstraints().add(column);
        }

        schedule.setHgap(1);
        schedule.setVgap(1);

        schedule.setStyle(
                "-fx-background-color: #DDE6E7;" +
                        "-fx-border-color: #DDE6E7;"
        );

        schedule.add(
                createScheduleHeader("Day / Time"),
                0, 0
        );

        // Hours

        for (int hour = 8; hour <= 17; hour++) {

            String time = LocalTime.of(hour, 0).format(
                    DateTimeFormatter.ofPattern(
                            "h a", Locale.ENGLISH
                    )
            );

            schedule.add(
                    createScheduleHeader(time),
                    hour - 7,
                    0
            );
        }

        // Days and Cells

        for (int day = 0; day < 7; day++) {

            LocalDate date = weekStart.plusDays(day);

            String dayName = date.format(
                    DateTimeFormatter.ofPattern(
                            "EEEE", Locale.ENGLISH
                    )
            );

            Label dayLabel = new Label(
                    dayName + "\n" +
                            date.format(
                                    DateTimeFormatter.ofPattern(
                                            "dd MMM", Locale.ENGLISH
                                    )
                            )
            );

            dayLabel.setMinSize(0, 45);
            dayLabel.setPrefHeight(45);

            dayLabel.setMaxSize(
                    Double.MAX_VALUE,
                    Double.MAX_VALUE
            );

            dayLabel.setAlignment(Pos.CENTER);

            boolean isToday = date.equals(LocalDate.now());

            dayLabel.setStyle(
                    "-fx-background-color: " +
                            (isToday ? "#D5EAE9" : "#EFF6F6") + ";" +

                            "-fx-text-fill: " + PRIMARY + ";" +
                            "-fx-font-weight: bold;"
            );

            schedule.add(dayLabel, 0, day + 1);

            for (int hour = 8; hour <= 17; hour++) {

                VBox cell = new VBox(3);

                cell.setMinSize(0, 45);
                cell.setPrefHeight(45);

                cell.setMaxSize(
                        Double.MAX_VALUE,
                        Double.MAX_VALUE
                );

                cell.setPadding(new Insets(3));

                cell.setStyle(
                        "-fx-background-color: " +
                                (isToday ? "#F3FAFA" : "#FFFFFF") + ";"
                );

                cells[day][hour - 8] = cell;

                schedule.add(
                        cell,
                        hour - 7,
                        day + 1
                );
            }
        }

        // Appointments Outside Schedule Hours

        Label outsideHoursLabel = new Label();

        outsideHoursLabel.setWrapText(true);

        outsideHoursLabel.setStyle(
                "-fx-font-size: 12px;" +
                        "-fx-text-fill: #A16207;"
        );

        outsideHoursLabel.setVisible(false);
        outsideHoursLabel.setManaged(false);

        // Refresh Weekly Appointments

        Runnable refresh = () ->
                loadWeeklyAppointments(
                        cells,
                        outsideHoursLabel
                );

        searchField.textProperty().addListener(
                (obs, oldValue, newValue) -> {

                    patientSearch = newValue;

                    refresh.run();
                }
        );

        statusFilter.setOnAction(e -> {

            selectedStatus = statusFilter.getValue();

            refresh.run();
        });

        resetButton.setOnAction(e -> {

            patientSearch = "";
            selectedStatus = "All Status";

            showWeeklySchedule();
        });

        // Status Legend

        HBox legend = new HBox(
                20,
                createStatusLegend(
                        "Scheduled",
                        getStatusColor("Scheduled")
                ),
                createStatusLegend(
                        "Completed",
                        getStatusColor("Completed")
                ),
                createStatusLegend(
                        "Cancelled",
                        getStatusColor("Cancelled")
                )
        );

        legend.setAlignment(Pos.CENTER);

        VBox page = new VBox(
                12,
                createAppointmentTabs(),
                header,
                filters,
                schedule,
                legend,
                outsideHoursLabel
        );

        stylePanel(page);

        refresh.run();

        setContent(page);
    }

    // Load Weekly Appointments

    private void loadWeeklyAppointments(
            VBox[][] cells,
            Label outsideHoursLabel) {

        for (VBox[] dayCells : cells) {

            for (VBox cell : dayCells) {

                cell.getChildren().clear();

                cell.setPrefHeight(45);
            }
        }

        ObservableList<ObservableList<String>> appointments =
                getDoctorAppointments();

        int outsideHours = 0;

        for (ObservableList<String> row : appointments) {

            LocalDate date = LocalDate.parse(row.get(3));

            LocalTime time = LocalTime.parse(row.get(4));

            if (date.isBefore(weekStart) ||
                    date.isAfter(weekStart.plusDays(6))) {

                continue;
            }

            int day = (int) ChronoUnit.DAYS.between(
                    weekStart,
                    date
            );

            int hour = time.getHour() - 8;

            if (hour < 0 || hour >= 10) {

                outsideHours++;

                continue;
            }

            VBox cell = cells[day][hour];

            cell.getChildren().add(
                    createAppointmentCard(row)
            );

            cell.setPrefHeight(
                    Math.max(
                            45,
                            cell.getChildren().size() * 40 + 6
                    )
            );
        }

        outsideHoursLabel.setVisible(outsideHours > 0);

        outsideHoursLabel.setManaged(outsideHours > 0);

        outsideHoursLabel.setText(
                outsideHours +
                        " appointment(s) outside schedule hours. " +
                        "View them in All Appointments."
        );
    }

    // Weekly Appointment Card

    private VBox createAppointmentCard(
            ObservableList<String> row) {

        String status = row.get(6);

        Label patientName = new Label(row.get(1));

        patientName.setMaxWidth(Double.MAX_VALUE);

        patientName.setMinWidth(0);

        patientName.setTextOverrun(
                OverrunStyle.ELLIPSIS
        );

        patientName.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " +
                        getStatusTextColor(status) + ";"
        );

        Label time = new Label(
                LocalTime.parse(row.get(4)).format(
                        DateTimeFormatter.ofPattern("HH:mm")
                )
        );

        time.setStyle(
                "-fx-font-size: 10px;" +
                        "-fx-text-fill: " +
                        getStatusTextColor(status) + ";"
        );

        VBox card = new VBox(
                2, patientName, time
        );

        card.setMinWidth(0);

        card.setMaxWidth(Double.MAX_VALUE);

        card.setPadding(new Insets(4));

        card.setStyle(
                "-fx-background-color: " +
                        getStatusColor(status) + ";" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );

        card.setOnMouseClicked(e ->
                showAppointmentDetails(row)
        );

        Tooltip.install(
                card,
                new Tooltip(
                        row.get(1) + "\n" +
                                row.get(4) + "\n" +
                                status
                )
        );

        return card;
    }

    // Patients

    private void showPatients() {

        pageTitle.setText("Patients");

        Label title = new Label("Patients");

        // Search

        ComboBox<String> searchBy = new ComboBox<>();

        searchBy.getItems().addAll(
                "PatientID",
                "Name"
        );

        searchBy.setValue("Name");

        TextField searchField = new TextField();

        searchField.setPromptText("Search patients...");

        HBox searchBox = new HBox(
                10, searchBy, searchField
        );

        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Patients Table

        TableView<ObservableList<String>> table =
                new TableView<>();

        TableColumn<ObservableList<String>, String> nameColumn =
                new TableColumn<>("Full Name");

        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().get(1) + " " +
                                data.getValue().get(2)
                )
        );

        table.getColumns().addAll(
                createColumn("Patient ID", 0),
                nameColumn,
                createColumn("Date of Birth", 3),
                createColumn("Gender", 4),
                createColumn("Phone", 5),
                createColumn("Email", 6),
                createColumn("Address", 7),
                createColumn("Blood Type", 8),
                UIComponents.createViewColumn(
                        this::showPatientDetails
                )
        );

        table.setPlaceholder(
                new Label("No patients found")
        );

        // Load and Search Patients

        Runnable refresh = () -> {

            table.setItems(
                    operations.getDoctorPatients(
                            searchBy.getValue(),
                            searchField.getText().trim()
                    )
            );
        };

        searchField.textProperty().addListener(
                (obs, oldValue, newValue) -> refresh.run()
        );

        searchBy.setOnAction(e -> refresh.run());

        refresh.run();

        // Page Layout

        VBox page = new VBox(
                15, title, searchBox, table
        );

        page.setPadding(new Insets(20));

        VBox.setVgrow(table, Priority.ALWAYS);

        setContent(page);
    }

    // Patient Details

    private void showPatientDetails(
            ObservableList<String> row) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Patient Details");

        alert.setHeaderText(row.get(1) + " " + row.get(2));

        alert.setContentText(
                "Patient ID: " + row.get(0) +
                        "\nFirst Name: " + row.get(1) +
                        "\nLast Name: " + row.get(2) +
                        "\nDate of Birth: " + row.get(3) +
                        "\nGender: " + row.get(4) +
                        "\nPhone: " + row.get(5) +
                        "\nEmail: " + row.get(6) +
                        "\nAddress: " + row.get(7) +
                        "\nBlood Type: " + row.get(8)
        );

        ButtonType historyButton = new ButtonType(
                "Medical History",
                ButtonBar.ButtonData.OTHER
        );

        alert.getButtonTypes().setAll(
                historyButton,
                ButtonType.CLOSE
        );

        alert.showAndWait().ifPresent(result -> {

            if (result == historyButton) {

                showMedicalRecords(row.get(0));
            }
        });
    }

    // Medical Records
    // NEW: Missing overloaded method

    private void showMedicalRecords() {

        showMedicalRecords(null);
    }

    // Medical Records and Patient Medical History

    private void showMedicalRecords(String patientId) {

        boolean historyMode = patientId != null;

        String titleText = historyMode
                ? "Patient Medical History"
                : "Medical Records";

        pageTitle.setText(titleText);

        Label title = new Label(titleText);

        Button addButton = new Button("+ Add Medical Record");

        Button backButton = new Button("Back to Patients");

        backButton.setOnAction(e -> showPage("Patients"));

        Region spacer = new Region();

        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(
                15,
                title,
                spacer,
                historyMode ? backButton : addButton
        );

        header.setAlignment(Pos.CENTER_LEFT);

        // Search

        ComboBox<String> searchBy = new ComboBox<>();

        searchBy.getItems().addAll(
                "RecordID",
                "PatientID",
                "PatientName",
                "AppointmentID",
                "Diagnosis",
                "RecordDate"
        );

        searchBy.setValue("PatientName");

        TextField searchField = new TextField();

        searchField.setPromptText("Search medical records...");

        // Patient Medical History Mode

        if (historyMode) {

            searchBy.setValue("PatientID");

            searchField.setText(patientId);

            searchBy.setDisable(true);

            searchField.setEditable(false);
        }

        HBox searchBox = new HBox(
                10, searchBy, searchField
        );

        searchBox.setAlignment(Pos.CENTER_LEFT);

        // Medical Records Table

        TableView<ObservableList<String>> table =
                new TableView<>();

        // Shared Refresh

        Runnable refresh = () -> {

            ObservableList<ObservableList<String>> records =
                    operations.getMedicalRecords(
                            searchBy.getValue(),
                            searchField.getText().trim()
                    );

            if (historyMode) {

                records.removeIf(record ->
                        !patientId.equals(record.get(9))
                );
            }

            table.setItems(records);
        };

        table.getColumns().addAll(

                createColumn("Record ID", 0),

                createColumn("Patient Name", 1),

                createColumn("Appointment ID", 3),

                createColumn("Record Date", 4),

                createColumn("Diagnosis", 6),

                UIComponents.createViewColumn(row -> {

                    if (UIComponents.showMedicalRecordDetails(
                            row, "Doctor")) {

                        refresh.run();
                    }
                })
        );

        table.setPlaceholder(
                new Label("No medical records found")
        );

        // Search Events

        searchField.textProperty().addListener(
                (obs, oldValue, newValue) -> refresh.run()
        );

        searchBy.setOnAction(e -> refresh.run());

        // Add Medical Record

        addButton.setOnAction(e -> {

            if (UIComponents.showAddMedicalRecordDialog()) {

                refresh.run();
            }
        });

        // Page Layout

        VBox page = new VBox(
                15,
                header,
                searchBox,
                table
        );

        page.setPadding(new Insets(20));

        VBox.setVgrow(table, Priority.ALWAYS);

        refresh.run();

        setContent(page);
    }

    // Shared Filter Style

    private void styleFilter(Control control) {

        control.setPrefHeight(38);

        control.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #DDE6E7;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
        );
    }

    // Shared Panel Style

    private void stylePanel(Pane panel) {

        panel.setPadding(new Insets(15));

        panel.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #DDE6E7;" +
                        "-fx-border-radius: 12;"
        );
    }

    // Schedule Header Style

    private Label createScheduleHeader(String text) {

        Label label = new Label(text);

        label.setMinSize(0, 32);

        label.setPrefHeight(32);

        label.setMaxSize(
                Double.MAX_VALUE,
                Double.MAX_VALUE
        );

        label.setAlignment(Pos.CENTER);

        label.setStyle(
                "-fx-background-color: " + PRIMARY + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;"
        );

        return label;
    }

    // Button Style

    private void styleScheduleButton(
            Button button, boolean primary) {

        button.setPrefHeight(38);

        button.setStyle(
                "-fx-background-color: " +
                        (primary ? PRIMARY : "#FFFFFF") + ";" +

                        "-fx-text-fill: " +
                        (primary ? "#FFFFFF" : PRIMARY) + ";" +

                        "-fx-border-color: #DDE6E7;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 14;" +
                        "-fx-cursor: hand;"
        );
    }

    // Status Legend

    private HBox createStatusLegend(
            String text, String color) {

        Region indicator = new Region();

        indicator.setMinSize(14, 14);
        indicator.setMaxSize(14, 14);

        indicator.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 4;"
        );

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: #475569;" +
                        "-fx-font-size: 12px;"
        );

        HBox item = new HBox(
                6, indicator, label
        );

        item.setAlignment(Pos.CENTER_LEFT);

        return item;
    }
}