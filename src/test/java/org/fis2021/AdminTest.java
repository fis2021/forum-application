package org.fis2021;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.fis2021.controllers.DisplayProfileController;
import org.fis2021.controllers.DisplayThreadController;
import org.fis2021.controllers.HomeController;
import org.fis2021.exceptions.ThreadAlreadyExistsException;
import org.fis2021.exceptions.UserNotFoundException;
import org.fis2021.exceptions.UsernameAlreadyExistsException;
import org.fis2021.models.ForumThread;
import org.fis2021.models.ThreadReply;
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
public class AdminTest {

    private Stage stage;
    private HomeController homeController;
    private DisplayThreadController displayThreadController;
    private DisplayProfileController displayProfileController;

    @BeforeAll
    static void beforeAll() throws IOException, UsernameAlreadyExistsException, ThreadAlreadyExistsException, InterruptedException {
        FileSystemService.initTestDirectory();
        FileUtils.cleanDirectory(FileSystemService.getHomeFolderTest().toFile());
        DatabaseService.initDatabaseTest();
        UserService.initService();
        ThreadService.initService();

        UserService.addUser("user", "user", "User");
        UserService.addUser("user1", "user", "User");
        UserService.addUser("user2", "user", "User");
        UserService.addUser("admin", "admin", "Moderator");

        ThreadService.addThread("title1", "content", "user");
        Thread.sleep(500);
        ThreadService.addThread("title2", "content", "user2");
        Thread.sleep(500);
        ThreadService.addThread("title3", "content", "user1");
        Thread.sleep(500);
        ThreadService.addThread("title4", "content", "user1");
        ForumThread t = ThreadService.getThread("title4");
        t.getReplies().add(new ThreadReply("user", "aaaa"));
        t.getReplies().add(new ThreadReply("user", "bbbb"));
        ThreadService.updateThread(t);
        Thread.sleep(500);
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
        Parent root = loader.load();
        HomeController controller = loader.getController();
        this.homeController =  controller;
        controller.setUser(UserService.getUser("admin"));
        stage.setUserData(UserService.getUser("admin"));
        controller.setThreads();
        stage.setTitle("Forum App - Home");
        stage.setScene(new Scene(root, 640, 800));
        stage.show();
    }

    @Test
    void testHomeFunctions(FxRobot robot) {
        assertThat(ThreadService.getThread("title5").isClosed()).isEqualTo(false);
        robot.rightClickOn(homeController.getThreadsList().getItems().get(0));
        robot.type(KeyCode.ENTER);
        robot.type(KeyCode.ENTER);
        assertThat(ThreadService.getThread("title5").isClosed()).isEqualTo(true);
        robot.rightClickOn(homeController.getThreadsList().getItems().get(0));
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.type(KeyCode.ENTER);

        assertThat(ThreadService.getThread("title5").isDeleted()).isEqualTo(false);
        robot.rightClickOn(homeController.getThreadsList().getItems().get(0));
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.type(KeyCode.ENTER);
        assertThat(ThreadService.getThread("title5").isDeleted()).isEqualTo(true);
        assertThat(homeController.getThreadsList().getItems().get(0)).isEqualTo("Title: [Deleted]\nAuthor: user");
        robot.rightClickOn(homeController.getThreadsList().getItems().get(0));
    }

    @Test
    void testCloseMessageOnDisplayThread(FxRobot robot) {
        robot.clickOn(homeController.getThreadsList().getItems().get(1));
        displayThreadController = (DisplayThreadController) stage.getScene().getUserData();
        assertThat(displayThreadController.getClosedMessage().isVisible()).isEqualTo(false);
        robot.clickOn("#backButton");

        robot.rightClickOn(homeController.getThreadsList().getItems().get(1));
        robot.type(KeyCode.ENTER);
        robot.type(KeyCode.ENTER);
        assertThat(ThreadService.getThread("title4").isClosed()).isEqualTo(true);

        robot.clickOn(homeController.getThreadsList().getItems().get(1));
        displayThreadController = (DisplayThreadController) stage.getScene().getUserData();
        assertThat(displayThreadController.getClosedMessage().isVisible()).isEqualTo(true);
    }

    @Test
    void testFunctionsOnDisplayThread(FxRobot robot) {
        robot.clickOn(homeController.getThreadsList().getItems().get(1));
        displayThreadController = (DisplayThreadController) stage.getScene().getUserData();
        assertThat(ThreadService.getThread("title4").getReplies().get(0).isDeleted()).isEqualTo(false);
        robot.rightClickOn(displayThreadController.getRepliesList().getItems().get(0));
        robot.type(KeyCode.ENTER);
        robot.type(KeyCode.ENTER);
        assertThat(ThreadService.getThread("title4").getReplies().get(0).isDeleted()).isEqualTo(true);
    }

    @Test
    void testBanUserPermanent(FxRobot robot) throws UserNotFoundException{
        assertThat(UserService.getUser("user1").isBanned()).isEqualTo(false);
        robot.clickOn(homeController.getThreadsList().getItems().get(1));
        robot.clickOn("#author");
        robot.clickOn("#permaBan");
        robot.type(KeyCode.ENTER);
        assertThat(UserService.getUser("user1").isBanned()).isEqualTo(true);
        robot.clickOn("#backButton");
        robot.clickOn("#backButton");
        homeController = (HomeController) stage.getScene().getUserData();
        assertThat(homeController.getThreadsList().getItems().get(1)).isEqualTo("Title: title4\nAuthor: [Banned]");
    }

    @Test
    void testBanUserTemp(FxRobot robot) throws UserNotFoundException {
        assertThat(UserService.getUser("user").isTempBanned()).isEqualTo(false);
        robot.clickOn(homeController.getThreadsList().getItems().get(4));
        robot.clickOn("#author");
        robot.clickOn("#tempBan");
        robot.type(KeyCode.ENTER);
        assertThat(UserService.getUser("user").isTempBanned()).isEqualTo(true);
    }

    @Test
    void testFunctionsDisplayProfile(FxRobot robot){
        robot.clickOn(homeController.getThreadsList().getItems().get(3));
        robot.clickOn("#author");
        displayProfileController = (DisplayProfileController) stage.getScene().getUserData();
        assertThat(ThreadService.getAllByUser("user2").get(0).isClosed()).isEqualTo(false);
        robot.rightClickOn(displayProfileController.getThreads().getItems().get(0));
        robot.type(KeyCode.ENTER);
        robot.type(KeyCode.ENTER);
        assertThat(ThreadService.getAllByUser("user2").get(0).isClosed()).isEqualTo(true);
        robot.rightClickOn(displayProfileController.getThreads().getItems().get(0));
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.type(KeyCode.ENTER);

        assertThat(ThreadService.getAllByUser("user2").get(0).isDeleted()).isEqualTo(false);
        robot.rightClickOn(displayProfileController.getThreads().getItems().get(0));
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.DOWN);
        robot.type(KeyCode.ENTER);
        robot.type(KeyCode.ENTER);
        assertThat(ThreadService.getAllByUser("user2").get(0).isDeleted()).isEqualTo(true);
        displayProfileController = (DisplayProfileController) stage.getScene().getUserData();
        assertThat(displayProfileController.getThreads().getItems().get(0)).contains("[Deleted]");
        robot.clickOn(displayProfileController.getThreads().getItems().get(0));
    }
}
