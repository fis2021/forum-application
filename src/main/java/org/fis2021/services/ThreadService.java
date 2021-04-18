package org.fis2021.services;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.fis2021.exceptions.ThreadAlreadyExistsException;
import org.fis2021.models.ForumThread;
import org.fis2021.models.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

import static org.fis2021.services.FileSystemService.getPathToFile;

public class ThreadService {

    private static ObjectRepository<ForumThread> threadRepository;

    private static Nitrite database;

    public static void initDatabase() {
        database = Nitrite.builder()
                .filePath(getPathToFile("forumapp.db").toFile())
                .openOrCreate("admin", "admin");

        threadRepository = database.getRepository(ForumThread.class);
    }

    public static void closeDatabase(){
        database.close();
    }

    public static void addThread(String title, String content, User user) throws ThreadAlreadyExistsException{
        checkThreadDoesNotAlreadyExist(title);
        threadRepository.insert(new ForumThread(title, content, user));
    }

    public static ArrayList<ForumThread> getAll(){
        ArrayList<ForumThread> threads = new ArrayList<ForumThread>();
        Cursor<ForumThread> cursor = threadRepository.find();
        for(ForumThread f : cursor){
            threads.add(f);
        }
        return threads;
    }

    public static void checkThreadDoesNotAlreadyExist(String title) throws ThreadAlreadyExistsException{
        for(ForumThread thread : threadRepository.find()){
            if(Objects.equals(title, thread.getTitle())){
                throw new ThreadAlreadyExistsException();
            }
        }
    }
}