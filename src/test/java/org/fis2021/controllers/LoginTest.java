package org.fis2021.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.fis2021.exceptions.UserNotFoundException;
import org.fis2021.exceptions.UsernameAlreadyExistsException;
import org.fis2021.models.User;
import org.fis2021.services.DatabaseService;
import org.fis2021.services.FileSystemService;
import org.fis2021.services.ThreadService;
import org.fis2021.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class LoginTest {
    private Stage stage;

    @BeforeAll
    static void beforeAll() {
        FileSystemService.initTestDirectory();
    }

    @BeforeEach
    void setUp() throws Exception{
        FileUtils.cleanDirectory(FileSystemService.getHomeFolderTest().toFile());
        DatabaseService.initDatabaseTest();
        UserService.initService();
        ThreadService.initService();
    }

    @AfterEach
    void tearDown(){
        DatabaseService.closeDatabase();
    }

    @Start
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setHeight(800);
        stage.setWidth(640);
        stage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        stage.setTitle("Forum App - Login");
        stage.setScene(new Scene(root, 640, 800));
        stage.show();
    }

    @Test
    public void testLogin(FxRobot robot) throws UsernameAlreadyExistsException, UserNotFoundException {
        UserService.addUser("user", "user", "User");
        UserService.addUser("user1", "user", "User");
        UserService.addUser("user2", "user", "User");

        User u = UserService.getUser("user");
        u.setBanned(true);
        UserService.updateUser(u);

        User u1 = UserService.getUser("user1");
        u1.setTempBanned(true);
        u1.setUnlockDate(System.currentTimeMillis()/1000L + 86400);
        UserService.updateUser(u1);

        robot.clickOn("#loginButton");
        assertThat(robot.lookup("#message").queryText()).hasText("Please type in a username!");

        robot.clickOn("#passwordField");
        robot.write("user");
        robot.clickOn("#loginButton");
        assertThat(robot.lookup("#message").queryText()).hasText("Please type in a username!");
        robot.clickOn("#passwordField");
        robot.eraseText(4);

        robot.clickOn("#usernameField");
        robot.write("user");
        robot.clickOn("#loginButton");
        assertThat(robot.lookup("#message").queryText()).hasText("Password cannot be empty");


        robot.clickOn("#passwordField");
        robot.write("user");
        robot.clickOn("#loginButton");
        assertThat(robot.lookup("#message").queryText()).hasText("You have been banned!");

        robot.clickOn("#usernameField");
        robot.write("1");
        robot.clickOn("#loginButton");
        assertThat(robot.lookup("#message").queryText()).hasText("You have been temporarily banned!");

        robot.clickOn("#usernameField");
        robot.eraseText(1);
        robot.write("2");
        robot.clickOn("#passwordField");
        robot.write("2");
        robot.clickOn("#loginButton");
        assertThat(robot.lookup("#message").queryText()).hasText("Incorrect login!");

        robot.clickOn("#usernameField");
        robot.eraseText(1);
        robot.write("3");
        robot.clickOn("#loginButton");
        assertThat(robot.lookup("#message").queryText()).hasText("The username user3 is not registered!");

        robot.clickOn("#usernameField");
        robot.eraseText(1);
        robot.write("2");
        robot.clickOn("#passwordField");
        robot.eraseText(1);
        robot.clickOn("#loginButton");
        assertThat(stage.getTitle()).isEqualTo("Forum App - Home");
    }

    @Test
    void testNavigateRegister(FxRobot robot) {
        robot.clickOn("#loadRegisterButton");
        assertThat(stage.getTitle()).isEqualTo("Forum App - Register");
    }
}