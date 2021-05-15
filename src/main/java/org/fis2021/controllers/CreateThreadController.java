package org.fis2021.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import org.fis2021.exceptions.ThreadAlreadyExistsException;
import org.fis2021.models.User;
import org.fis2021.services.ThreadService;
import java.io.IOException;

public class CreateThreadController {

    @FXML
    private BorderPane borderPane;

    @FXML
    private HTMLEditor htmlEditor;

    @FXML
    private TextField titleField;

    @FXML
    private Label errorMessage;

    public Label getErrorMessage() {
        return errorMessage;
    }

    private User user;

    public void setUser(User u){
        user = u;
    }

    private User getUser(){
        return user;
    }

    @FXML
    public void handleCreateThreadButton(){
        String content = htmlEditor.getHtmlText();
        String title = titleField.getText();

        if(title.isEmpty()
                || content.equals("<html dir=\"ltr\"><head></head><body contenteditable=\"true\"><p><br></p></body></html>")
                || content.equals("<html dir=\"ltr\"><head></head><body contenteditable=\"true\"></body></html>")){
            errorMessage.setText("Please fill all fields!");
            return;
        }

        try {
            if(user == null){
                user = (User) ((Stage) borderPane.getScene().getWindow()).getUserData();
            }
            ThreadService.addThread(title, content, user.getUsername());
            loadHomePage();
        } catch(ThreadAlreadyExistsException e){
            errorMessage.setText(e.getMessage());
        }
    }

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
}
