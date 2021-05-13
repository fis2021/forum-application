package org.fis2021.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.fis2021.exceptions.UserNotFoundException;
import org.fis2021.models.User;
import org.fis2021.services.ThreadService;
import org.fis2021.services.UserService;

import java.io.IOException;

public class LoginController {

    @FXML
    public Text loginMessage;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField usernameField;

    public void setRegistrationConfirmation(){
        loginMessage.setText("Account created successfully!");
    }

    @FXML
    public void handleLoginAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isEmpty()) {
            loginMessage.setText("Please type in a username!");
            return;
        }

        if (password == null || password.isEmpty()) {
            loginMessage.setText("Password cannot be empty");
            return;
        }

        String encoded_password = UserService.encodePassword(username, password);

        try{
            String stored_password = UserService.getHashedUserPassword(username);
            if(stored_password.equals(encoded_password)){
                if(!UserService.getUser(username).isBanned() && !UserService.getUser(username).isTempBanned()) {
                    loadHomePage();
                }
                else if(UserService.getUser(username).isBanned()){
                    loginMessage.setText("You have been banned!");
                }
                else if(UserService.getUser(username).isTempBanned()){
                    if(UserService.getUser(username).getUnlockDate() > (System.currentTimeMillis()/1000L)){
                        loginMessage.setText("You have been temporarily banned!");
                    }
                    else{
                        User u = UserService.getUser(username);
                        u.setTempBanned(false);
                        u.setUnlockDate(0);
                        UserService.updateUser(u);
                        loadHomePage();
                    }
                }
                return;
            }

        } catch(UserNotFoundException e){
            loginMessage.setText(e.getMessage());
            return;
        }

        loginMessage.setText("Incorrect login!");
    }

    @FXML
    public void loadRegisterPage(){
        try {
            Stage stage = (Stage) loginMessage.getScene().getWindow();
            Parent registerRoot = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
            Scene scene = new Scene(registerRoot, 640, 800);
            stage.setTitle("Forum App - Register");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadHomePage(){
        try{
            User u = UserService.getUser(usernameField.getText());
            Stage stage = (Stage) loginMessage.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent homeRoot = loader.load();
            HomeController controller = loader.getController();
            controller.setUser(u);
            stage.setUserData(u);
            controller.setThreads();
            Scene scene = new Scene(homeRoot, 640, 800);
            stage.setTitle("Forum App - Home");
            stage.setScene(scene);
        } catch (UserNotFoundException e){
            loginMessage.setText(e.getMessage());
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
