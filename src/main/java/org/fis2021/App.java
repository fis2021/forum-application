package org.fis2021;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.fis2021.services.DatabaseService;
import org.fis2021.services.FileSystemService;
import org.fis2021.services.ThreadService;
import org.fis2021.services.UserService;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setHeight(800);
        stage.setWidth(640);
        stage.setResizable(false);
        FileSystemService.initProdDirectory();
        DatabaseService.initDatabaseProd();
        UserService.initService();
        ThreadService.initService();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        stage.setTitle("Forum App - Login");
        stage.setScene(new Scene(root, 640, 800));
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }

}