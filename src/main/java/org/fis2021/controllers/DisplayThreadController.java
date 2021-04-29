package org.fis2021.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.fis2021.models.ForumThread;
import org.fis2021.models.ThreadReply;
import org.fis2021.models.User;
import org.fis2021.services.ThreadService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

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

    @FXML
    private VBox threadVBox;

    @FXML
    private Label closedMessage;

    @FXML
    private Button replyButton;

    private int BASE_SIZE = 640;

    private ContextMenu contextMenu;


    public void initialize(){
        textArea.setPromptText("Type in your reply...");
        textArea.getParent().requestFocus();
        contextMenu = new ContextMenu();
        repliesList.setContextMenu(contextMenu);
    }

    public void setListValues(){
        long modifier = 0;
        repliesList.getItems().clear();
        if(forumThread.getReplies()!=null){
            for(ThreadReply t : forumThread.getReplies()){
                modifier += t.getContent().chars().filter(ch -> ch == '\n').count() + 1;
                repliesList.getItems().add(t.getContent() + "\nAuthor: " + t.getAuthor());
            }
            if(20 * (modifier + forumThread.getReplies().size()) <= 440) {
                repliesList.setPrefHeight(20 * (modifier + forumThread.getReplies().size()));
                borderPane.setPrefHeight(BASE_SIZE + 50 + 20 * (modifier + forumThread.getReplies().size()));
            }
            else{
                repliesList.setPrefHeight(440);
                borderPane.setPrefHeight(440 + BASE_SIZE + 50);
            }
        }
        else{
            threadVBox.getChildren().remove(3);
            threadVBox.getChildren().remove(3);
            borderPane.setPrefHeight(BASE_SIZE);
        }
    }

    private void loadDisplayThreadPage(String title){
        try {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/displayThread.fxml"));
            Parent displayThreadRoot = loader.load();
            DisplayThreadController controller = loader.getController();
            controller.setForumThread(ThreadService.getThread(title));
            controller.setUser(user);
            controller.setListValues();
            Scene scene = new Scene(displayThreadRoot, 640, 800);
            stage.setTitle("Forum App - " + title);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
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
        if(forumThread.getReplies().size() == 1){
            loadDisplayThreadPage(forumThread.getTitle());
        }
    }

    @FXML
    public void handleListAction(MouseEvent event){
        if(event.getButton() == MouseButton.SECONDARY && user.getRole().equals("Moderator")){
            repliesList.getContextMenu().getItems().clear();
            if (repliesList.getSelectionModel().getSelectedIndex() >= 0) {
                repliesList.getContextMenu().getItems().add(new MenuItem("Delete reply"));
                repliesList.getContextMenu().getItems().get(0).setOnAction(
                        (x) -> {
                            if(forumThread.getReplies().get(repliesList.getSelectionModel().getSelectedIndex()).isDeleted()){
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Delete reply");
                                alert.setHeaderText(null);
                                alert.setContentText("This reply has already been deleted!");
                                Button yes_button = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                                yes_button.setDefaultButton(false);
                                alert.showAndWait();
                            }
                            else {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                alert.setTitle("Delete reply");
                                alert.setHeaderText(null);
                                alert.setContentText("Are you sure you want to delte this reply?\nThis action is permanent!");
                                Button yes_button = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                                yes_button.setDefaultButton(false);
                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get().equals(ButtonType.OK)) {
                                    forumThread.getReplies().get(repliesList.getSelectionModel().getSelectedIndex()).setDeleted(true);
                                    forumThread.getReplies().get(repliesList.getSelectionModel().getSelectedIndex()).setContent("[Deleted]");
                                    ThreadService.updateThread(forumThread);
                                    setListValues();
                                }
                            }
                        }
                );
            }
        }
    }

    public void setForumThread(ForumThread forumThread) {
        this.forumThread = forumThread;
        title.setText(forumThread.getTitle());
        webView.getEngine().loadContent(forumThread.getContent());
        author.setText(forumThread.getAuthor());
        closedMessage.setVisible(forumThread.isClosed());
        textArea.setDisable(forumThread.isClosed());
        replyButton.setDisable(forumThread.isClosed());
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
            Scene scene = new Scene(homeRoot, 640, 800);
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
            Scene scene = new Scene(displayProfileRoot, 640, 800);
            stage.setTitle("Forum App - " + displayUsername);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
