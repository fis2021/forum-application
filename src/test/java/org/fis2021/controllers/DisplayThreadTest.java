package org.fis2021.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.fis2021.exceptions.ThreadAlreadyExistsException;
import org.fis2021.exceptions.UsernameAlreadyExistsException;
import org.fis2021.models.User;
import org.fis2021.services.DatabaseService;
import org.fis2021.services.FileSystemService;
import org.fis2021.services.ThreadService;
import org.fis2021.services.UserService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
@ExtendWith(ApplicationExtension.class)
class DisplayThreadTest {
    private Stage stage;
    private DisplayThreadController controller;

    @BeforeAll
    static void beforeAll() throws IOException, UsernameAlreadyExistsException, ThreadAlreadyExistsException {
        FileSystemService.initTestDirectory();
        FileUtils.cleanDirectory(FileSystemService.getHomeFolderTest().toFile());
        DatabaseService.initDatabaseTest();
        UserService.initService();
        ThreadService.initService();

        UserService.addUser("user", "user", "User");
        ThreadService.addThread("title1", "content", "user");
    }

    @AfterAll
    static void afterAll() {
        DatabaseService.closeDatabase();
    }

    @Start
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setHeight(800);
        stage.setWidth(640);
        stage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/displayThread.fxml"));
        Parent root = loader.load();
        DisplayThreadController controller = loader.getController();
        this.controller = controller;
        controller.setForumThread(ThreadService.getThread("title1"));
        controller.setUser(UserService.getUser("user"));
        controller.setListValues();
        stage.setTitle("Forum App - title1");
        stage.setScene(new Scene(root, 640, 800));
        stage.show();
    }

    @Test
    void testBackButton(FxRobot robot) {
        robot.clickOn("#backButton");
        assertThat(stage.getTitle()).isEqualTo("Forum App - Home");
    }

    @Test
    void testReply(FxRobot robot){
        assertThat(controller.getRepliesList().getItems().size()).isEqualTo(0);
        robot.clickOn("#replyArea");
        robot.write("aaaa");
        robot.clickOn("#replyButton");
        robot.clickOn("#replyArea");
        robot.write("bbbbb");
        robot.clickOn("#replyButton");
        assertThat(controller.getRepliesList().getItems().size()).isEqualTo(2);
    }

    @Test
    void clickOnUser(FxRobot robot) {
        robot.clickOn("#author");
        assertThat(stage.getTitle()).isEqualTo("Forum App - user");
    }
}