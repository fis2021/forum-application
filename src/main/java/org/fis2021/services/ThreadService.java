package org.fis2021.services;

import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.fis2021.exceptions.ThreadAlreadyExistsException;
import org.fis2021.models.ForumThread;
import java.util.*;

public class ThreadService {

    private static ObjectRepository<ForumThread> threadRepository;

    public static void initService() {
        threadRepository = DatabaseService.getDatabase().getRepository(ForumThread.class);
    }

    public static void addThread(String title, String content, String user) throws ThreadAlreadyExistsException{
        checkThreadDoesNotAlreadyExist(title);
        threadRepository.insert(new ForumThread(title, content, user, new Date()));
    }

    public static ArrayList<ForumThread> getAll(){
        ArrayList<ForumThread> threads = new ArrayList<ForumThread>();
        Cursor<ForumThread> cursor = threadRepository.find();
        for(ForumThread f : cursor){
            threads.add(f);
        }
        return threads;
    }

    public static ArrayList<ForumThread> getAllByUser(String owner){
            ArrayList<ForumThread> threads = new ArrayList<ForumThread>();
            Cursor<ForumThread> cursor = threadRepository.find(ObjectFilters.eq("author", owner));
            for (ForumThread f : cursor) {
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

    public static ForumThread getThread(String title) {
        Cursor<ForumThread> cursor = threadRepository.find(ObjectFilters.eq("title", title));
        return cursor.firstOrDefault();
    }

    public static void setThreadAsDeleted(String title){
        ForumThread f = getThread(title);
        if(f!=null) {
            f.setDeleted(true);
            threadRepository.update(f);
        }
    }

    public static void updateThread(ForumThread forumThread){
        threadRepository.update(forumThread);
    }

}
