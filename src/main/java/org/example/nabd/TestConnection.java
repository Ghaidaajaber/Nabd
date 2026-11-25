package org.example.nabd;

import java.sql.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestConnection extends Application {

    private static Stage mainStage;
    private static AdminDashboard adminDashboard;
    private static Integer currentUserId;
    private static Integer currentDoctorId;
    private static String currentRole;

    public static void main(String[] args) {
        launch(args);
    }

    public static void openDashboard(String role) {

        switch (role) {

            case "Administrator":
                adminDashboard = new AdminDashboard();
                mainStage.setScene(adminDashboard.createScene());
                break;

            case "Doctor":
                mainStage.setScene(new DoctorDashboard().createScene());
                break;

            case "Receptionist":
                mainStage.setScene(new ReceptionistDashboard().createScene());
                break;

            default:
                System.out.println("Unknown role: " + role);
        }
    }

    public static void openDashboard(String role,
                                     boolean mustChangePassword,
                                     String currentPassword) {

        openDashboard(role);

        if (mustChangePassword) {

            javafx.scene.Parent root = mainStage.getScene().getRoot();

            root.setDisable(true);

            try {
                UIComponents.showMandatoryPasswordDialog(
                        mainStage,
                        currentUserId,
                        currentPassword
                );
            } finally {
                root.setDisable(false);
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        mainStage = primaryStage;

        login loginPage = new login();

        mainStage.setTitle("Nabd - Login");
        mainStage.setScene(loginPage.createScene());
        mainStage.setMaximized(true);
        mainStage.show();
    }

    public static Connection makeConnection() throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.cj.jdbc.Driver");

        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/nabd", "root", ""
        );

        return connection;
    }

    public static void openLogin() {

        currentUserId = null;
        currentDoctorId = null;
        currentRole = null;
        adminDashboard = null;

        mainStage.setTitle("Nabd - Login");
        mainStage.setScene(new login().createScene());
    }

    public static void openAdminPage(String pageName) {

        if (adminDashboard != null) {
            adminDashboard.showPage(pageName);
        }
    }

    public static void setCurrentUser(int userId, String role, Integer doctorId) {

        currentUserId = userId;
        currentRole = role;
        currentDoctorId = doctorId;
    }

    public static Integer getCurrentUserId() {
        return currentUserId;
    }

    public static Integer getCurrentDoctorId() {
        return currentDoctorId;
    }

    public static String getCurrentRole() {
        return currentRole;
    }
}