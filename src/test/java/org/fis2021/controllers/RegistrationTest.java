package org.fis2021.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.fis2021.exceptions.UserNotFoundException;
import org.fis2021.exceptions.UsernameAlreadyExistsException;
import org.fis2021.services.DatabaseService;
import org.fis2021.services.FileSystemService;
import org.fis2021.services.ThreadService;
import org.fis2021.services.UserService;
import org.junit.jupiter.api.*;
import static org.testfx.assertions.api.Assertions.assertThat;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
class RegistrationTest {

    private Stage stage;

    @BeforeEach
    void setUp() throws Exception{
        FileUtils.cleanDirectory(FileSystemService.getHomeFolderTest().toFile());
        DatabaseService.initDatabaseTest();
        UserService.initService();
    }

    @AfterEach
    void tearDown() throws IOException {
        DatabaseService.closeDatabase();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
    }

    @Start
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setHeight(800);
        stage.setWidth(640);
        stage.setResizable(false);
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
        stage.setTitle("Forum App - Register");
        stage.setScene(new Scene(root, 640, 800));
        stage.show();
    }

    @Test
    public void testRegister(FxRobot robot) throws UsernameAlreadyExistsException, UserNotFoundException {
        robot.clickOn("#registerButton");
        assertThat(robot.lookup("#message").queryText()).hasText("Username/Password cannot be blank!");

        robot.clickOn("#usernameField");
        robot.write("user");
        robot.clickOn("#registerButton");
        assertThat(robot.lookup("#message").queryText()).hasText("Username/Password cannot be blank!");
        robot.clickOn("#usernameField");
        robot.eraseText(4);

        robot.clickOn("#passwordField");
        robot.write("user");
        robot.clickOn("#registerButton");
        assertThat(robot.lookup("#message").queryText()).hasText("Username/Password cannot be blank!");
        robot.clickOn("#passwordField");
        robot.eraseText(4);


        UserService.addUser("user", "user", "user");
        robot.clickOn("#usernameField");
        robot.write("user");
        robot.clickOn("#passwordField");
        robot.write("user");
        robot.clickOn("#registerButton");
        assertThat(robot.lookup("#message").queryText()).hasText("An account with the username user already exists!");

        robot.clickOn("#usernameField");
        robot.write("1");
        robot.clickOn("#registerButton");
        assertThat(UserService.getUser("user1")).isNotNull();
        assertThat(UserService.getUser("user")).isNotNull();
        assertThat(stage.getTitle()).isEqualTo("Forum App - Login");

    }

    @Test
    void testNavigateToLogin(FxRobot robot) {
        robot.clickOn("#loadLoginButton");
        assertThat(stage.getTitle()).isEqualTo("Forum App - Login");
    }
}