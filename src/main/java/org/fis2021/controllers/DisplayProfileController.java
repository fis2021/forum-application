package org.fis2021.controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.fis2021.models.ForumThread;
import org.fis2021.models.User;
import org.fis2021.services.ThreadService;

import java.io.IOException;
import java.util.ArrayList;

public class DisplayProfileController {

    @FXML
    private Label author;

    @FXML
    private BorderPane borderPane;

    @FXML
    private ListView<String> threads;

    private String displayedUsername;

    private User user;

    private ArrayList<ForumThread> threadsList;

    private String previousThreadTitle;

    public String getDisplayedUsername() {
        return displayedUsername;
    }

    public void setDisplayedUsername(String displayedUser) {
        this.displayedUsername = displayedUser;
        this.author.setText(displayedUser);
        threadsList = ThreadService.getAllByUser(displayedUser);
        for(ForumThread t : threadsList){
            threads.getItems().add("Title: " + t.getTitle() + "\n" + "Author: " + t.getAuthor());
        }
    }

    public String getPreviousThreadTitle() {
        return previousThreadTitle;
    }

    public void setPreviousThreadTitle(String previousThreadTitle) {
        this.previousThreadTitle = previousThreadTitle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    private void handleBackButton(){
        loadDisplayThreadPage(previousThreadTitle);
    }

    @FXML
    private void loadDisplayThreadPage(String title){
        try {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/displayThread.fxml"));
            Parent displayThreadRoot = loader.load();
            DisplayThreadController controller = loader.getController();
            controller.setForumThread(ThreadService.getThread(title));
            controller.setUser(user);
            controller.setListValues();
            Scene scene = new Scene(displayThreadRoot, 640, 480);
            stage.setTitle("Forum App - Display Thread");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleListSelectAction(){
        try {
            loadDisplayThreadPage(threadsList.get(threads.getSelectionModel().getSelectedIndex()).getTitle());
        }catch(IndexOutOfBoundsException e){
            return;
        }
    }

}
