package org.fis2021.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.fis2021.models.ForumThread;
import org.fis2021.models.ThreadReply;
import org.fis2021.models.User;
import org.fis2021.services.ThreadService;

import java.io.IOException;
import java.util.ArrayList;

public class DisplayThreadController {

    private User user;

    public void setUser(User u){
        user = u;
    }

    private User getUser(){
        return user;
    }

    private ForumThread forumThread;

    public ForumThread getForumThread() {
        return forumThread;
    }

    @FXML
    private Hyperlink author;

    @FXML
    private ListView<String> repliesList;

    @FXML
    private TextArea textArea;

    public void setListValues(){
        repliesList.getItems().clear();
        if(forumThread.getReplies()!=null){
            for(ThreadReply t : forumThread.getReplies()){
                repliesList.getItems().add(t.getContent() + "\nAuthor: " + t.getAuthor());
            }
        }
    }

    @FXML
    public void handleReplyButton(){
        String message = textArea.getText();
        ArrayList<ThreadReply> replyAuxiliary = forumThread.getReplies();
        if(replyAuxiliary == null){
            forumThread.setReplies(new ArrayList<ThreadReply>());
        }
        forumThread.getReplies().add(new ThreadReply(user.getUsername(), message));
        setListValues();
        textArea.clear();
        ThreadService.updateThread(forumThread);
    }

    public void setForumThread(ForumThread forumThread) {
        this.forumThread = forumThread;
        title.setText(forumThread.getTitle());
        webView.getEngine().loadContent(forumThread.getContent());
        author.setText(forumThread.getAuthor());
    }

    @FXML
    public BorderPane borderPane;

    @FXML
    public Label title;

    @FXML
    public WebView webView;

    @FXML
    private void loadHomePage() {
        try {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent homeRoot = loader.load();
            HomeController controller = loader.getController();
            controller.setUser(user);
            controller.setThreads();
            Scene scene = new Scene(homeRoot, 640, 480);
            stage.setTitle("Forum App - Home");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHyperLink(){
        loadProfilePage(forumThread.getAuthor());
    }

    @FXML
    private void loadProfilePage(String displayUsername){
        try {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/displayProfile.fxml"));
            Parent displayProfileRoot = loader.load();
            DisplayProfileController controller = loader.getController();
            controller.setUser(user);
            controller.setPreviousThreadTitle(forumThread.getTitle());
            controller.setDisplayedUsername(forumThread.getAuthor());
            Scene scene = new Scene(displayProfileRoot, 640, 480);
            stage.setTitle("Forum App - " + displayUsername);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
