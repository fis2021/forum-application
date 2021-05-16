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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class DisplayProfileTest {
    private Stage stage;

    private DisplayProfileController controller;

    @BeforeAll
    static void beforeAll() throws IOException, UsernameAlreadyExistsException, ThreadAlreadyExistsException {
        FileSystemService.initTestDirectory();
        FileUtils.cleanDirectory(FileSystemService.getHomeFolderTest().toFile());
        DatabaseService.initDatabaseTest();
        UserService.initService();
        ThreadService.initService();

        UserService.addUser("user", "user", "User");
        UserService.addUser("user1", "user", "User");
        UserService.addUser("user2", "user", "User");
        ThreadService.addThread("title1", "content", "user");
        ThreadService.addThread("title2", "content", "user");
        ThreadService.addThread("title3", "content", "user1");
        ThreadService.addThread("title4", "content", "user2");
        ThreadService.addThread("title5", "content", "user");
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/displayProfile.fxml"));
        Parent root = loader.load();
        DisplayProfileController controller = loader.getController();
        this.controller = controller;
        controller.setUser(UserService.getUser("user1"));
        controller.setDisplayedUsername("user");
        controller.setPreviousThreadTitle("title1");
        stage.setTitle("Forum App - user");
        stage.setScene(new Scene(root, 640, 800));
        stage.show();
    }

    @Test
    void testbackButton(FxRobot robot) {
        robot.clickOn("#backButton");
        assertThat(stage.getTitle()).isEqualTo("Forum App - title1");
    }

    @Test
    void testView(FxRobot robot) {
        assertThat(controller.getAuthor().getText()).isEqualTo("user");
        assertThat(ThreadService.getAll().size()).isEqualTo(5);
        assertThat(controller.getThreads().getItems().size()).isEqualTo(3);
        robot.clickOn(controller.getThreads().getItems().get(1));
        assertThat(stage.getTitle()).isEqualTo("Forum App - title2");
    }
}