package org.fis2021.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.fis2021.exceptions.ThreadAlreadyExistsException;
import org.fis2021.exceptions.UsernameAlreadyExistsException;
import org.fis2021.models.ForumThread;
import org.fis2021.models.ThreadReply;
import org.fis2021.services.DatabaseService;
import org.fis2021.services.FileSystemService;
import org.fis2021.services.ThreadService;
import org.fis2021.services.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.testfx.assertions.api.Assertions.assertThat;

import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
class HomeTest {
    private Stage stage;
    private HomeController controller;

    @BeforeAll
    static void beforeAll() throws IOException, UsernameAlreadyExistsException, ThreadAlreadyExistsException, InterruptedException {
        FileSystemService.initTestDirectory();
        FileUtils.cleanDirectory(FileSystemService.getHomeFolderTest().toFile());
        DatabaseService.initDatabaseTest();
        UserService.initService();
        ThreadService.initService();

        UserService.addUser("user", "user", "User");
        UserService.addUser("admin", "admin", "Moderator");
        ThreadService.addThread("title 1", "content 1", "user");
        Thread.sleep(500);
        ThreadService.addThread("title 2", "content 1", "user");
        Thread.sleep(500);
        ThreadService.addThread("title 3", "content 1", "user");
        Thread.sleep(500);

        ForumThread t1 = ThreadService.getThread("title 1");
        t1.getReplies().add(new ThreadReply("user", "aaa"));
        ThreadService.updateThread(t1);

        ForumThread t2 = ThreadService.getThread("title 2");
        t2.getReplies().add(new ThreadReply("user", "aaa"));
        t2.getReplies().add(new ThreadReply("user", "bbb"));
        ThreadService.updateThread(t2);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
        Parent root = loader.load();
        HomeController controller = loader.getController();
        this.controller = controller;
        controller.setUser(UserService.getUser("admin"));
        stage.setUserData(UserService.getUser("admin"));
        controller.setThreads();
        stage.setTitle("Forum App - Home");
        stage.setScene(new Scene(root, 640, 800));
        stage.show();
    }

    @Test
    void testBackButton(FxRobot robot) {
        robot.clickOn("#backButton");
        assertThat(stage.getTitle()).isEqualTo("Forum App - Login");
    }

    @Test
    void testCreateThreadButton(FxRobot robot) {
        robot.clickOn("#loadCreateThread");
        assertThat(stage.getTitle()).isEqualTo("Forum App - Create Thread");
    }

    @Test
    void testSortListview(FxRobot robot){
        assertThat(controller.getThreadsList().getItems().get(0)).isEqualTo("Title: title 3\nAuthor: user");

        robot.clickOn("#sortChoice");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        assertThat(controller.getThreadsList().getItems().get(0)).isEqualTo("Title: title 1\nAuthor: user");

        robot.clickOn("#sortChoice");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        assertThat(controller.getThreadsList().getItems().get(0)).isEqualTo("Title: title 2\nAuthor: user");

        robot.clickOn("#sortChoice");
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        assertThat(controller.getThreadsList().getItems().get(0)).isEqualTo("Title: title 3\nAuthor: user");
    }

    @Test
    void testLoadThreadPage(FxRobot robot) {
        robot.clickOn(controller.getThreadsList().getItems().get(0));
        assertThat(stage.getTitle()).isEqualTo("Forum App - title 3");
    }

    @Test
    void testBanAndClose(FxRobot robot){
        assertThat(ThreadService.getThread("title 3").isClosed()).isEqualTo(false);
        robot.rightClickOn(controller.getThreadsList().getItems().get(0));
        robot.type(KeyCode.ENTER);
        robot.type(KeyCode.ENTER);
        assertThat(ThreadService.getThread("title 3").isClosed()).isEqualTo(true);

        assertThat(ThreadService.getThread("title 3").isDeleted()).isEqualTo(false);
        robot.rightClickOn(controller.getThreadsList().getItems().get(0));
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.type(KeyCode.ENTER);
        assertThat(ThreadService.getThread("title 3").isDeleted()).isEqualTo(true);
        assertThat(controller.getThreadsList().getItems().get(0)).isEqualTo("Title: [Deleted]\nAuthor: user");
    }
}