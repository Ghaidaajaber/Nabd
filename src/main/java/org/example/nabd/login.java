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
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.sql.*;

public class login {

    private boolean mustChangePassword;

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

        String fieldStyle =
                "-fx-background-color: #F4F7FC;" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: #DCE5F0;" +
                        "-fx-border-radius: 20;" +
                        "-fx-padding: 0 15;" +
                        "-fx-font-size: 13;";

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

            eyeIcon.setIconCode(
                    show ? FontAwesomeSolid.EYE
                            : FontAwesomeSolid.EYE_SLASH
            );
        });

        StackPane passwordBox = new StackPane(
                password, visiblePassword, eyeButton
        );

        passwordBox.setMaxWidth(265);

        StackPane.setAlignment(eyeButton, Pos.CENTER_RIGHT);

        StackPane.setMargin(
                eyeButton, new Insets(0, 7, 0, 0)
        );

        // Forgot Password
        Hyperlink forgotPassword = new Hyperlink(
                "Forgot Password?"
        );

        forgotPassword.setStyle(
                "-fx-text-fill: #0B2545;" +
                        "-fx-font-size: 12px;"
        );

        forgotPassword.setOnAction(
                e -> showForgotPasswordDialog()
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

            if (user.isEmpty()) {

                message.setText("Please enter username");
                message.setTextFill(Color.RED);
                return;

            } else if (pass.isEmpty()) {

                message.setText("Please enter password!");
                message.setTextFill(Color.RED);
                return;
            }

            String role = checkLogin(user, pass, message);

            if (role != null) {

                TestConnection.openDashboard(
                        role,
                        mustChangePassword,
                        pass
                );
            }
        });

        username.setOnAction(e -> password.requestFocus());

        password.setOnAction(e -> loginButton.fire());

        visiblePassword.setOnAction(e -> loginButton.fire());

        card.getChildren().addAll(
                logoBox,
                username,
                passwordBox,
                forgotPassword,
                loginButton,
                message
        );

        root.getChildren().add(card);

        return new Scene(root, 900, 600);
    }

    private String checkLogin(
            String username,
            String password,
            Label message) {

        message.setText("Invalid username or password");

        int userId;
        boolean mustChange;
        String role;
        Integer doctorId;

        String sql = "SELECT u.UserID, u.PasswordHash, u.Role, u.MustChangePassword, d.DoctorID FROM Users u LEFT JOIN Doctor d ON u.UserID = d.UserID WHERE u.Username = ? AND u.IsActive = TRUE";

        try (Connection con = TestConnection.makeConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                String hash = rs.getString("PasswordHash");

                if (hash == null || !BCrypt.checkpw(password, hash)) {
                    return null;
                }

                userId = rs.getInt("UserID");
                role = rs.getString("Role");
                doctorId = rs.getObject("DoctorID", Integer.class);
                mustChange = rs.getBoolean("MustChangePassword");

                if (!java.util.Set.of(
                        "Administrator",
                        "Doctor",
                        "Receptionist"
                ).contains(role)) {

                    return null;
                }

                if ("Doctor".equals(role) && doctorId == null) {
                    return null;
                }
            }

        } catch (SQLException |
                 ClassNotFoundException |
                 IllegalArgumentException e) {

            message.setText("Unable to sign in.");
            return null;
        }

        mustChangePassword = mustChange;

        TestConnection.setCurrentUser(
                userId,
                role,
                doctorId
        );

        message.setText("");

        return role;
    }

    private void showForgotPasswordDialog() {

        Dialog<ButtonType> dialog = new Dialog<>();

        dialog.setTitle("Forgot Password");
        dialog.setHeaderText("Request Password Reset");

        TextField usernameField = new TextField();

        usernameField.setPromptText("Username");

        Label description = new Label(
                "Send a password reset request to the administrator."
        );

        description.setWrapText(true);

        VBox content = new VBox(
                12,
                description,
                new Label("Username:"),
                usernameField
        );

        content.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(content);

        ButtonType sendType = new ButtonType(
                "Send Request",
                ButtonBar.ButtonData.OK_DONE
        );

        dialog.getDialogPane().getButtonTypes().addAll(
                sendType,
                ButtonType.CANCEL
        );

        Button send = (Button) dialog.getDialogPane()
                .lookupButton(sendType);

        send.setDefaultButton(false);

        UIComponents.setupKeyboardNavigation(
                dialog,
                usernameField,
                send
        );

        send.addEventFilter(
                javafx.event.ActionEvent.ACTION, event -> {

                    String username = usernameField.getText().trim();

                    if (username.isEmpty() || username.length() > 50) {

                        UIComponents.showAlert(
                                Alert.AlertType.WARNING,
                                "Please enter a valid username."
                        );

                        event.consume();
                        return;
                    }

                    HospitalOperations operations =
                            new HospitalOperations();

                    if (operations.requestPasswordReset(username)) {

                        UIComponents.showAlert(Alert.AlertType.INFORMATION, "If an active account exists, your request has been submitted. Please contact the administrator.");

                    } else {

                        UIComponents.showAlert(
                                Alert.AlertType.ERROR,
                                "Unable to submit password reset request."
                        );

                        event.consume();
                    }
                });

        dialog.showAndWait();
    }
}