package org.fis2021.controllers;


import com.sun.javafx.scene.control.ContextMenuContent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.fis2021.models.ForumThread;
import org.fis2021.models.User;
import org.fis2021.services.ThreadService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

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

    public ContextMenu menu;

    public void initialize(){
        ContextMenu menu = new ContextMenu();
        threads.setContextMenu(menu);
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
            Scene scene = new Scene(displayThreadRoot, 640, 800);
            stage.setTitle("Forum App - " + title);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleListSelectAction(MouseEvent event){
        try {
            if(event.getButton() == MouseButton.PRIMARY){
                loadDisplayThreadPage(threadsList.get(threads.getSelectionModel().getSelectedIndex()).getTitle());
            }
            else if(event.getButton() == MouseButton.SECONDARY && user.getRole().equals("Moderator")) {
                threads.getContextMenu().getItems().clear();
                if (threads.getSelectionModel().getSelectedIndex() >= 0) {
                    threads.getContextMenu().getItems().add(new MenuItem("Close thread"));
                    threads.getContextMenu().getItems().get(0).setOnAction(
                            (x) -> {
                                if(threadsList.get(threads.getSelectionModel().getSelectedIndex()).isClosed()){
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Close thread");
                                    alert.setHeaderText(null);
                                    alert.setContentText("This thread has already been closed!");
                                    Button yes_button = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                                    yes_button.setDefaultButton(false);
                                    alert.showAndWait();
                                }
                                else {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Close thread");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Are you sure you want to close this thread?\nThis action is permanent!");
                                    Button yes_button = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                                    yes_button.setDefaultButton(false);
                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.get().equals(ButtonType.OK)) {
                                        threadsList.get(threads.getSelectionModel().getSelectedIndex()).setClosed(true);
                                        ThreadService.updateThread(threadsList.get(threads.getSelectionModel().getSelectedIndex()));
                                    }
                                }
                            }
                    );
                }
            }
        }catch(IndexOutOfBoundsException e){
            return;
        }
    }

}
