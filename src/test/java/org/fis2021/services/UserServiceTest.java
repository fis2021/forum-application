package org.fis2021.services;

import org.apache.commons.io.FileUtils;
import org.fis2021.exceptions.UserNotFoundException;
import org.fis2021.exceptions.UsernameAlreadyExistsException;
import org.fis2021.models.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testfx.assertions.api.Assertions.assertThat;


class UserServiceTest {

    @BeforeAll
    static void beforeAll() {
        FileSystemService.initTestDirectory();
    }

    @BeforeEach
    void setUp() throws Exception{
        FileUtils.cleanDirectory(FileSystemService.getHomeFolderTest().toFile());
        DatabaseService.initDatabaseTest();
        UserService.initService();
    }

    @AfterEach
    void tearDown() throws Exception{
        DatabaseService.closeDatabase();
    }

    @Test
    @DisplayName("Service connects to database")
    void testInitService() {
        UserService.initService();
        assertThat(UserService.getUserRepository()).isNotNull();
    }

    @Test
    @DisplayName("User can be added and retrieved from database")
    void testAddUser() {
        try {
            UserService.addUser("user", "user", "user");
            assertThat(UserService.getUser("user")).isNotNull();
            assertThat(UserService.getUser("user").getUsername()).isEqualTo("user");
            assertThat(UserService.getUser("user").getPassword()).isEqualTo(UserService.getHashedUserPassword("user"));
            assertThat(UserService.getUser("user").getRole()).isEqualTo("user");
        }
        catch(UsernameAlreadyExistsException | UserNotFoundException ignored){}
    }

    @Test
    @DisplayName("No duplicate usernames")
    void noDuplicateUsername() {
        assertThrows(UsernameAlreadyExistsException.class, () -> {
            UserService.addUser("user1", "user", "user");
            UserService.addUser("user1", "user", "user");
        });
    }

    @Test
    @DisplayName("User can be updated")
    void testUpdateUser() {
        try {
            UserService.addUser("user", "user", "user");
            assertThat(UserService.getUser("user").getRole()).isEqualTo("user");
            User u = UserService.getUser("user");
            u.setRole("admin");
            UserService.updateUser(u);
            assertThat(UserService.getUser("user").getRole()).isEqualTo("admin");
        } catch(UsernameAlreadyExistsException | UserNotFoundException ignored){}
    }
}