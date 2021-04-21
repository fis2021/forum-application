package org.fis2021.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.fis2021.exceptions.UsernameAlreadyExistsException;
import org.fis2021.services.UserService;

import java.io.IOException;

public class RegistrationController {

    @FXML
    private Text registrationMessage;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;
    @FXML
    private ChoiceBox role;

    @FXML
    public void initialize() {
        role.getItems().addAll("User", "Moderator");
        role.getSelectionModel().select(0);
    }

    @FXML
    public void handleRegisterAction() {
        try {
            if(usernameField.getText().isEmpty() || passwordField.getText().isEmpty()){
                registrationMessage.setText("Username/Password cannot be blank!");
                return;
            }
            UserService.addUser(usernameField.getText(), passwordField.getText(), (String) role.getValue());
            Stage stage = (Stage) registrationMessage.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginRoot = loader.load();
            LoginController controller = loader.getController();
            controller.setRegistrationConfirmation();
            Scene scene = new Scene(loginRoot, 640, 480);
            stage.setTitle("Forum App - Login");
            stage.setScene(scene);

        } catch (UsernameAlreadyExistsException e) {
            registrationMessage.setText(e.getMessage());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void loadLoginPage(){
        try {
            Stage stage = (Stage) registrationMessage.getScene().getWindow();
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loginRoot, 640, 480);
            stage.setTitle("Forum App - Login");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
