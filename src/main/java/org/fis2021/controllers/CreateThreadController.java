package org.fis2021.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.fis2021.models.User;

import java.io.IOException;

public class CreateThreadController {

    @FXML
    private BorderPane borderPane;

    private User user;

    public void setUser(User u){
        user = u;
    }

    private User getUser(){
        return user;
    }

    @FXML
    private void loadHomePage() {
        try {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent homeRoot = loader.load();
            HomeController controller = loader.getController();
            controller.setUser(user);
            Scene scene = new Scene(homeRoot, 640, 480);
            stage.setTitle("Forum App - Home");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
