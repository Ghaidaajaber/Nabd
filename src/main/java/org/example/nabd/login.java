
package org.example.nabd;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.sql.*;

public class login {

    public Scene createScene() {

        StackPane root = new StackPane();
        root.setPrefSize(900, 600);

        // Background
        URL backgroundUrl = getClass().getResource(
                "/images/login-background.png"
        );

        if (backgroundUrl != null) {
            ImageView background = new ImageView(
                    new Image(backgroundUrl.toExternalForm())
            );

            background.fitWidthProperty().bind(root.widthProperty());
            background.fitHeightProperty().bind(root.heightProperty());

            root.getChildren().add(background);
        } else {
            root.setStyle("-fx-background-color: #EAF3FC;");
        }

        // Card
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30, 40, 25, 40));

        card.prefWidthProperty().bind(
                Bindings.min(470, root.widthProperty().multiply(0.80))
        );

        card.prefHeightProperty().bind(
                Bindings.min(520, root.heightProperty().multiply(0.90))
        );

        card.setMaxSize(470, 520);
        card.setMinSize(0, 0);

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 25;"
        );

        card.setEffect(
                new DropShadow(25, Color.rgb(20, 45, 80, 0.18))
        );

        // Logo
        URL logoUrl = getClass().getResource(
                "/images/nabd-logo.png"
        );

        VBox logoBox;

        if (logoUrl != null) {

            ImageView logo = new ImageView(
                    new Image(logoUrl.toExternalForm())
            );

            logo.setFitWidth(180);
            logo.setFitHeight(180);
            logo.setPreserveRatio(true);

            logoBox = new VBox(0, logo);

        } else {
            logoBox = new VBox(0);
        }

        logoBox.setAlignment(Pos.CENTER);

        // Username
        TextField username = new TextField();
        username.setPromptText("Username");
        username.setPrefHeight(40);
        username.setMaxWidth(265);

        String fieldStyle = "-fx-background-color: #F4F7FC;" + "-fx-background-radius: 20;" +
                "-fx-border-color: #DCE5F0;" + "-fx-border-radius: 20;" +
                        "-fx-padding: 0 15;" + "-fx-font-size: 13;";

        username.setStyle(fieldStyle);

        // Password
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setStyle(fieldStyle);
        password.setPrefHeight(40);

        TextField visiblePassword = new TextField();
        visiblePassword.setPromptText("Password");
        visiblePassword.setStyle(fieldStyle);
        visiblePassword.setPrefHeight(40);

        visiblePassword.textProperty().bindBidirectional(
                password.textProperty()
        );

        visiblePassword.setVisible(false);
        visiblePassword.setManaged(false);

        // Eye Icon
        FontIcon eyeIcon = new FontIcon(
                FontAwesomeSolid.EYE_SLASH
        );

        eyeIcon.setIconSize(13);
        eyeIcon.setIconColor(Color.web("#0B2545"));

        Button eyeButton = new Button();
        eyeButton.setGraphic(eyeIcon);
        eyeButton.setPrefSize(20, 20);
        eyeButton.setFocusTraversable(false);

        eyeButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-cursor: hand;"
        );

        eyeButton.setOnAction(e -> {

            boolean show = !visiblePassword.isVisible();

            visiblePassword.setVisible(show);
            visiblePassword.setManaged(show);

            password.setVisible(!show);
            password.setManaged(!show);

            eyeIcon.setIconCode(show ? FontAwesomeSolid.EYE : FontAwesomeSolid.EYE_SLASH);
        });

        StackPane passwordBox = new StackPane(password, visiblePassword, eyeButton);

        passwordBox.setMaxWidth(265);

        StackPane.setAlignment(eyeButton, Pos.CENTER_RIGHT);

        StackPane.setMargin(
                eyeButton, new Insets(0, 7, 0, 0)
        );

        // Login Button
        Button loginButton = new Button("Login  →");
        loginButton.setPrefSize(265, 40);

        loginButton.setStyle(
                "-fx-background-color: #0B2545;" +
                        "-fx-background-radius: 20;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14;" +
                        "-fx-cursor: hand;"
        );

        // Message
        Label message = new Label();
        message.setFont(Font.font("Arial", 11));
        message.setTextFill(Color.web("#C62828"));
        message.setWrapText(true);

        // Login Action
        loginButton.setOnAction(e -> {
            String user = username.getText().trim();
            String pass = password.getText();

            if (user.isEmpty() ) {
                message.setText("Please enter username");
                message.setTextFill(Color.RED);
                return;
            }
             else if(pass.isEmpty()){
                message.setText("Please enter password!");
                message.setTextFill(Color.RED);
                return;

            }


            String role = checkLogin(user, pass);

            if (role != null) {
                TestConnection.openDashboard(role);
            } else {
                message.setText("Invalid username or password");
            }
        });

        username.setOnAction(e -> password.requestFocus());
        password.setOnAction(e -> loginButton.fire());
        visiblePassword.setOnAction(e -> loginButton.fire());

        card.getChildren().addAll(logoBox, username, passwordBox, loginButton, message);
        root.getChildren().add(card);

        return new Scene(root, 900, 600);
    }


    private String checkLogin(String username, String password) {

        String sql = "SELECT passwordhash, role FROM Users " + "WHERE username = '" + username + "' AND isactive = 1";

        try (Connection con = TestConnection.makeConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {

                String storedHash = rs.getString("passwordhash");
                String role = rs.getString("role");

                if (storedHash != null && BCrypt.checkpw(password, storedHash)) {
                    return role;
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid BCrypt hash.");
        }

        return null;
    }


}
