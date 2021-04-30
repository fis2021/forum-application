package org.fis2021.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.fis2021.exceptions.UserNotFoundException;
import org.fis2021.models.ForumThread;
import org.fis2021.models.User;
import org.fis2021.services.ThreadService;
import org.fis2021.services.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class HomeController{
    @FXML
    private BorderPane borderPane;

    @FXML
    private ListView<String> threadsList;

    @FXML
    private ChoiceBox<String> sortChoice;

    private User user;
    private ArrayList<ForumThread> threads;
    private ContextMenu menu;

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
        menu = new ContextMenu();
        threadsList.setContextMenu(menu);
    }

    @FXML
    public void sortThreadsAscending(){
        Collections.sort(threads, (o1, o2) -> o2.getCreationDate().compareTo(o1.getCreationDate()));
        threadsList.getItems().clear();
        ThreadService.closeDatabase();
        UserService.initDatabase();
        for(ForumThread t : threads) {
            if (!t.isDeleted()) {
                try{
                    if(UserService.getUser(t.getAuthor()).isBanned()){
                        threadsList.getItems().add("Title: " + t.getTitle() + "\n" + "Author: [Banned]");
                    }
                    else{
                        threadsList.getItems().add("Title: " + t.getTitle() + "\n" + "Author: " + t.getAuthor());
                    }
                }catch (UserNotFoundException ignored){ }
            }
            else{
                try{
                    if(UserService.getUser(t.getAuthor()).isBanned()){
                        threadsList.getItems().add("Title: [Deleted]\n" + "Author: [Banned]");
                    }
                    else{
                        threadsList.getItems().add("Title: [Deleted]\n" + "Author: " + t.getAuthor());
                    }
                }catch (UserNotFoundException ignored){ }
            }
        }
        UserService.closeDatabase();
        ThreadService.initDatabase();
        if(41 * threads.size() <= 697){
            threadsList.setPrefHeight(41 * threads.size());
        }
        else{
            threadsList.setPrefHeight(697);
        }
    }

    @FXML
    public void sortThreadsDescending(){
        Collections.sort(threads, (o1, o2) -> o1.getCreationDate().compareTo(o2.getCreationDate()));
        threadsList.getItems().clear();
        ThreadService.closeDatabase();
        UserService.initDatabase();
        for(ForumThread t : threads) {
            if (!t.isDeleted()) {
                try{
                    if(UserService.getUser(t.getAuthor()).isBanned()){
                        threadsList.getItems().add("Title: " + t.getTitle() + "\n" + "Author: [Banned]");
                    }
                    else{
                        threadsList.getItems().add("Title: " + t.getTitle() + "\n" + "Author: " + t.getAuthor());
                    }
                }catch (UserNotFoundException ignored){ }
            }
            else{
                try{
                    if(UserService.getUser(t.getAuthor()).isBanned()){
                        threadsList.getItems().add("Title: [Deleted]\n" + "Author: [Banned]");
                    }
                    else{
                        threadsList.getItems().add("Title: [Deleted]\n" + "Author: " + t.getAuthor());
                    }
                }catch (UserNotFoundException ignored){ }
            }
        }
        UserService.closeDatabase();
        ThreadService.initDatabase();
        if(41 * threads.size() <= 697){
            threadsList.setPrefHeight(41 * threads.size());
        }
        else{
            threadsList.setPrefHeight(697);
        }
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

    public void handleListSelectAction(MouseEvent event){
        threadsList.getContextMenu().getItems().clear();
        try {
            if(event.getButton() == MouseButton.PRIMARY) {
                if (!threads.get(threadsList.getSelectionModel().getSelectedIndex()).isDeleted()) {
                    loadDisplayThreadPage(threads.get(threadsList.getSelectionModel().getSelectedIndex()).getTitle());
                }
            }
            else if(event.getButton() == MouseButton.SECONDARY && user.getRole().equals("Moderator") && !threads.get(threadsList.getSelectionModel().getSelectedIndex()).isDeleted()) {
                threadsList.getContextMenu().getItems().clear();
                if (threadsList.getSelectionModel().getSelectedIndex() >= 0) {
                    threadsList.getContextMenu().getItems().add(new MenuItem("Close thread"));
                    threadsList.getContextMenu().getItems().get(0).setOnAction(
                            (x) -> {
                                if(threads.get(threadsList.getSelectionModel().getSelectedIndex()).isClosed()){
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
                                        threads.get(threadsList.getSelectionModel().getSelectedIndex()).setClosed(true);
                                        ThreadService.updateThread(threads.get(threadsList.getSelectionModel().getSelectedIndex()));
                                    }
                                }
                            }
                    );
                    threadsList.getContextMenu().getItems().add(new MenuItem("Delete thread"));
                    threadsList.getContextMenu().getItems().get(1).setOnAction(
                            (x) -> {
                                if(threads.get(threadsList.getSelectionModel().getSelectedIndex()).isDeleted()){
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
                                        threads.get(threadsList.getSelectionModel().getSelectedIndex()).setDeleted(true);
                                        ThreadService.setThreadAsDeleted(threads.get(threadsList.getSelectionModel().getSelectedIndex()).getTitle());
                                        if(sortChoice.getSelectionModel().getSelectedIndex() == 0) {
                                            sortThreadsAscending();
                                        }
                                        else{
                                            sortThreadsDescending();
                                        }
                                        threadsList.getContextMenu().getItems().clear();
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
            Scene scene = new Scene(createThreadRoot, 640, 800);
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
            Scene scene = new Scene(loginRoot, 640, 800);
            stage.setTitle("Forum App - Login");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
