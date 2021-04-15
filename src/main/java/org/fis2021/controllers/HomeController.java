package org.fis2021.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.fis2021.models.User;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController{
    @FXML
    public Text placeholderMessage;

    private User user;

    public void setUser(User u){
        user = u;
        placeholderMessage.setText(String.format("Successful login! Your username is: %s!", user.getUsername()));
    }

    private User getUser(){
        return user;
    }
}
