package org.fis2021.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.fis2021.models.ForumThread;
import org.fis2021.models.User;
import org.fis2021.services.ThreadService;
import org.fis2021.services.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class HomeController{
    @FXML
    private BorderPane borderPane;

    @FXML
    private ListView<String> threadsList;

    @FXML
    private ChoiceBox<String> sortChoice;

    private User user;
    private ArrayList<ForumThread> threads;

    public void setUser(User u){
        user = u;
    }

    @FXML
    public void initialize() {
        sortChoice.getItems().addAll("Newest", "Oldest");
        sortChoice.getSelectionModel().select(0);
        sortChoice.setOnAction((event -> {
            int selection = sortChoice.getSelectionModel().getSelectedIndex();
            if(selection == 0){
                sortThreadsAscending();
            }
            else{
                sortThreadsDescending();
            }
        }));
    }

    @FXML
    public void sortThreadsAscending(){
        Collections.sort(threads, (o1, o2) -> o2.getCreationDate().compareTo(o1.getCreationDate()));
        threadsList.getItems().clear();
        for(ForumThread t : threads){
            threadsList.getItems().add("Title: " + t.getTitle() + "\n" + "Author: " + t.getAuthor().getUsername());
        }
    }

    @FXML
    public void sortThreadsDescending(){
        Collections.sort(threads, (o1, o2) -> o1.getCreationDate().compareTo(o2.getCreationDate()));
        threadsList.getItems().clear();
        for(ForumThread t : threads){
            threadsList.getItems().add("Title: " + t.getTitle() + "\n" + "Author: " + t.getAuthor().getUsername());
        }
        borderPane.setCenter(threadsList);
    }

    public void setThreads(){
        threads = ThreadService.getAll();

        if(threads.isEmpty()){
            Label emptyMessage = new Label();
            emptyMessage.setText("Looks like there are no threads :c");
            borderPane.setCenter(emptyMessage);
            sortChoice.setDisable(true);
            return;
        }
        sortThreadsAscending();
    }

    public void handleListSelectAction(){
        loadDisplayThreadPage();
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

    @FXML
    private void loadLoginPage(){
        try {
            ThreadService.closeDatabase();
            UserService.initDatabase();
            Stage stage = (Stage) borderPane.getScene().getWindow();
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loginRoot, 640, 480);
            stage.setTitle("Forum App - Login");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadDisplayThreadPage(){
        try {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/displayThread.fxml"));
            Parent displayThreadRoot = loader.load();
            DisplayThreadController controller = loader.getController();
            controller.setForumThread(threads.get(0));
            Scene scene = new Scene(displayThreadRoot, 640, 480);
            stage.setTitle("Forum App - Display Thread");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
