/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xal.extension.jelog;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

/**
 *
 * @author nataliamilas
 */
public class AuthenticationPaneFX extends Dialog {

    private TextField tfUsername;
    private PasswordField pfPassword;

    /**
     * Constructs a new authentication pane.
     */
    public AuthenticationPaneFX() {

    }

    /**
     * Creates a dialog that contains this pane.
     *
     * @param parent the parent of this dialog
     * @return the dialog returns <code>true</code>
     */
    public Dialog createDialog(Parent parent) {
        Dialog<Pair<String, char[]>> dialog = new Dialog<>();
        dialog.setTitle("ESS Logbook Login Dialog");
        dialog.setHeaderText("Authenticate user:");
        // Set the icon (must be included in the project).
        dialog.setGraphic(new ImageView(this.getClass().getResource("/pictures/login.png").toString()));

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        tfUsername = new TextField();
        tfUsername.setPromptText("Username");
        pfPassword = new PasswordField();
        pfPassword.setPromptText("Password");

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Username:"), 0, 0);
        grid.add(tfUsername, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(pfPassword, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Do some validation (using the Java 8 lambda syntax).
        tfUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        // Request focus on the username field by default.
        Platform.runLater(() -> tfUsername.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(tfUsername.getText(), pfPassword.getText().toCharArray());
            }
            return null;
        });

        return dialog;
    }

}
