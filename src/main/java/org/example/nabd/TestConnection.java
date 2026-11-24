package org.example.nabd;

import java.sql.*;

import javafx.application.Application;
import javafx.stage.Stage;

public class TestConnection extends Application {
    private static Stage mainStage;
    private static AdminDashboard adminDashboard;

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
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/nabd", "root", "");
        return connection;
    }
    public static void openLogin() {

        mainStage.setTitle("Nabd - Login");

        mainStage.setScene(new login().createScene());

    }
    public static void openAdminPage(String pageName) {
        if (adminDashboard != null) {
            adminDashboard.showPage(pageName);
        }
    }

}