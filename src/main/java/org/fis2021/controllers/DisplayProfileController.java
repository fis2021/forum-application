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
import javafx.stage.Stage;
import org.fis2021.exceptions.UserNotFoundException;
import org.fis2021.models.ForumThread;
import org.fis2021.models.User;
import org.fis2021.services.ThreadService;
import org.fis2021.services.UserService;

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

    @FXML
    private Button banUserButton;

    @FXML
    private VBox vBox;

    public String getDisplayedUsername() {
        return displayedUsername;
    }

    public Label getAuthor() {
        return author;
    }

    public ListView<String> getThreads() {
        return threads;
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
            if (!t.isDeleted()) {
                threads.getItems().add("Title: " + t.getTitle() + "\n" + "Author: " + t.getAuthor());
            }
            else{
                threads.getItems().add("Title: [Deleted]\n" + "Author: " + t.getAuthor());
            }
        }
        try {
            if (UserService.getUser(displayedUsername).getRole().equals("Moderator") && vBox.getChildren().size() > 2) {
                vBox.getChildren().remove(2);
            }
        }catch(UserNotFoundException ignored){ }
        if(41 * threadsList.size() <= 697){
            threads.setPrefHeight(41 * threadsList.size());
        }
        else{
            threads.setPrefHeight(697);
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
        if(!user.getRole().equals("Moderator") && vBox.getChildren().size() > 2){
            vBox.getChildren().remove(2);
        }
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
    private void loadProfilePage(String displayUsername){
        try {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/displayProfile.fxml"));
            Parent displayProfileRoot = loader.load();
            DisplayProfileController controller = loader.getController();
            controller.setUser(user);
            controller.setPreviousThreadTitle(previousThreadTitle);
            controller.setDisplayedUsername(displayedUsername);
            Scene scene = new Scene(displayProfileRoot, 640, 800);
            scene.setUserData(controller);
            stage.setTitle("Forum App - " + displayUsername);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleListSelectAction(MouseEvent event){
        threads.getContextMenu().getItems().clear();
        try {
            if(event.getButton() == MouseButton.PRIMARY && !threadsList.get(threads.getSelectionModel().getSelectedIndex()).isDeleted()){
                loadDisplayThreadPage(threadsList.get(threads.getSelectionModel().getSelectedIndex()).getTitle());
            }
            else if(event.getButton() == MouseButton.SECONDARY && user.getRole().equals("Moderator") && !threadsList.get(threads.getSelectionModel().getSelectedIndex()).isDeleted()) {
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
                    threads.getContextMenu().getItems().add(new MenuItem("Delete thread"));
                    threads.getContextMenu().getItems().get(1).setOnAction(
                            (x) -> {
                                if(threadsList.get(threads.getSelectionModel().getSelectedIndex()).isDeleted()){
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Delete thread");
                                    alert.setHeaderText(null);
                                    alert.setContentText("This thread has already been deleted!");
                                    Button yes_button = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                                    yes_button.setDefaultButton(false);
                                    alert.showAndWait();
                                }
                                else {
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Delete thread");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Are you sure you want to delete this thread?\nThis action is permanent!");
                                    Button yes_button = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                                    yes_button.setDefaultButton(false);
                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.get().equals(ButtonType.OK)) {
                                        threadsList.get(threads.getSelectionModel().getSelectedIndex()).setDeleted(true);
                                        ThreadService.setThreadAsDeleted(threadsList.get(threads.getSelectionModel().getSelectedIndex()).getTitle());
                                        threads.getContextMenu().getItems().clear();
                                        loadProfilePage(displayedUsername);
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

    @FXML
    public void handleBanAction(){
        User userAux = new User();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ban User");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to ban this user?\nThis action is permanent!");
        Button yes_button = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        yes_button.setDefaultButton(false);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get().equals(ButtonType.OK)) {
            try {
                userAux = UserService.getUser(displayedUsername);
            }catch(UserNotFoundException ignored){ }
            userAux.setBanned(true);
            UserService.updateUser(userAux);
        }
    }

    @FXML
    public void handleTempBanAction(){
        User userAux = new User();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Ban User");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to ban this user for 24 hours?");
        Button yes_button = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        yes_button.setDefaultButton(false);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get().equals(ButtonType.OK)) {
            try {
                userAux = UserService.getUser(displayedUsername);
            }catch(UserNotFoundException ignored){ }
            userAux.setTempBanned(true);
            userAux.setUnlockDate((System.currentTimeMillis()/1000L) + 86400);
            UserService.updateUser(userAux);
        }
    }
}
