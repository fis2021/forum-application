package org.fis2021.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.fis2021.exceptions.ThreadAlreadyExistsException;
import org.fis2021.exceptions.UsernameAlreadyExistsException;
import org.fis2021.services.DatabaseService;
import org.fis2021.services.FileSystemService;
import org.fis2021.services.ThreadService;
import org.fis2021.services.UserService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class CreateThreadTest {
    private Stage stage;
    private CreateThreadController controller;

    @BeforeAll
    static void beforeAll() throws IOException, UsernameAlreadyExistsException, ThreadAlreadyExistsException {
        FileSystemService.initTestDirectory();
        FileUtils.cleanDirectory(FileSystemService.getHomeFolderTest().toFile());
        DatabaseService.initDatabaseTest();
        UserService.initService();
        ThreadService.initService();

        UserService.addUser("user", "user", "User");
        UserService.addUser("user1", "user", "User");
        ThreadService.addThread("title", "content", "user1");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/createThread.fxml"));
        Parent root = loader.load();
        CreateThreadController controller = loader.getController();
        this.controller = controller;
        controller.setUser(UserService.getUser("user"));
        stage.setTitle("Forum App - Create Thread");
        stage.setScene(new Scene(root, 640, 800));
        stage.show();
    }

    @Test
    void testCreateThread(FxRobot robot) {
        assertThat(controller.getErrorMessage().getText()).isEqualTo("");
        robot.clickOn("#createThread");
        assertThat(controller.getErrorMessage().getText()).isEqualTo("Please fill all fields!");

        robot.clickOn("#title");
        robot.write("title");
        robot.clickOn("#createThread");
        assertThat(controller.getErrorMessage().getText()).isEqualTo("Please fill all fields!");
        robot.clickOn("#title");
        robot.eraseText(5);

        robot.clickOn("#editor");
        robot.write("content");
        robot.clickOn("#createThread");
        assertThat(controller.getErrorMessage().getText()).isEqualTo("Please fill all fields!");

        robot.clickOn("#title");
        robot.write("title");
        robot.clickOn("#createThread");
        assertThat(controller.getErrorMessage().getText()).isEqualTo("A thread with this title already exists!");

        robot.clickOn("#title");
        robot.write("1");
        robot.clickOn("#createThread");
        assertThat(stage.getTitle()).isEqualTo("Forum App - Home");
        assertThat(ThreadService.getThread("title")).isNotNull();
        assertThat(ThreadService.getThread("title1")).isNotNull();
        assertThat(ThreadService.getThread("title1").getAuthor()).isEqualTo("user");
        assertThat(ThreadService.getThread("title").getAuthor()).isEqualTo("user1");
    }

    @Test
    void testBackButton(FxRobot robot) {
        robot.clickOn("#backButton");
        assertThat(stage.getTitle()).isEqualTo("Forum App - Home");
    }
}