package org.fis2021.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.fis2021.models.ForumThread;
import org.fis2021.models.User;
import org.fis2021.services.ThreadService;

import java.io.IOException;
import java.util.ArrayList;

public class HomeController{
    @FXML
    private BorderPane borderPane;

    @FXML
    private Label placeholderText;

    private User user;
    private ArrayList<ForumThread> threads;

    public void setUser(User u){
        user = u;
        placeholderText.setText(String.format("You are logged in as user %s and your role is %s!", u.getUsername(), u.getRole()));
    }

    public void setThreads(){
        threads = ThreadService.getAll();
    }

    public ArrayList<ForumThread> getThreads(){return threads;}

    private User getUser(){
        return user;
    }

    @FXML
    private void loadCreateThreadPage(){
        try{
            Stage stage = (Stage) borderPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/createThread.fxml"));
            Parent createThreadRoot = loader.load();
            CreateThreadController controller = loader.getController();
            controller.setUser(user);
            Scene scene = new Scene(createThreadRoot, 640, 480);
            stage.setTitle("Forum App - Create Thread");
            stage.setScene(scene);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
