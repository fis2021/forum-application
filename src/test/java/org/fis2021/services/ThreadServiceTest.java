package org.fis2021.services;

import org.apache.commons.io.FileUtils;
import org.fis2021.exceptions.ThreadAlreadyExistsException;
import org.fis2021.models.ForumThread;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testfx.assertions.api.Assertions.assertThat;

class ThreadServiceTest {

    @BeforeAll
    static void beforeAll() {
        FileSystemService.initTestDirectory();
    }

    @BeforeEach
    void setUp() throws Exception{
        FileUtils.cleanDirectory(FileSystemService.getHomeFolderTest().toFile());
        DatabaseService.initDatabaseTest();
        ThreadService.initService();
    }

    @AfterEach
    void tearDown(){
        DatabaseService.closeDatabase();
    }

    @Test
    @DisplayName("Service connects to database")
    void testInitService() {
        assertThat(ThreadService.getThreadRepository()).isNotNull();
    }

    @Test
    @DisplayName("Thread can be added and retrieved from database")
    void testAddThread() throws ThreadAlreadyExistsException {
        ThreadService.addThread("title", "content", "user");
        assertThat(ThreadService.getThread("title").getTitle()).isEqualTo("title");
        assertThat(ThreadService.getThread("title").getContent()).isEqualTo("content");
        assertThat(ThreadService.getThread("title").getAuthor()).isEqualTo("user");
    }

    @Test
    @DisplayName("Can get all threads from database")
    void testGetAll() throws ThreadAlreadyExistsException {
        ThreadService.addThread("title1", "content", "user");
        ThreadService.addThread("title2", "content", "user");
        ThreadService.addThread("title3", "content", "user");
        assertThat(ThreadService.getAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Can get all threads created by a user")
    void testGetAllByUser() throws ThreadAlreadyExistsException {
        ThreadService.addThread("title1", "content", "user1");
        ThreadService.addThread("title2", "content", "user1");
        ThreadService.addThread("title3", "content", "user2");
        assertThat(ThreadService.getAllByUser("user1").size()).isEqualTo(2);
        assertThat(ThreadService.getAllByUser("user2").size()).isEqualTo(1);
    }

    @Test
    @DisplayName("No duplicate thread titles")
    void checkNoDuplicates() {
        assertThrows(ThreadAlreadyExistsException.class, () -> {
            ThreadService.addThread("title", "content", "user");
            ThreadService.addThread("title", "content", "user");
        });
    }

    @Test
    @DisplayName("Thread can be updated")
    void testUpdateThread() throws ThreadAlreadyExistsException {
        ThreadService.addThread("title", "content", "user");
        ForumThread thread = ThreadService.getThread("title");
        assertThat(thread.getContent()).isEqualTo("content");
        thread.setContent("new content");
        ThreadService.updateThread(thread);
        assertThat(ThreadService.getThread("title").getContent()).isEqualTo("new content");
    }

    @Test
    @DisplayName("Thread can be marked as deleted")
    void testSetDeleted() throws ThreadAlreadyExistsException {
        ThreadService.addThread("title", "content", "user");
        assertThat(ThreadService.getThread("title").isDeleted()).isEqualTo(false);
        ThreadService.setThreadAsDeleted("title");
        assertThat(ThreadService.getThread("title").isDeleted()).isEqualTo(true);
    }
}