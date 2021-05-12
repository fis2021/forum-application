package org.fis2021.services;

import org.dizitart.no2.Nitrite;
import org.fis2021.models.ForumThread;

public class DatabaseService {

    private static Nitrite database;

    public static void initDatabase() {
        database = Nitrite.builder()
                .filePath(FileSystemService.getPathToProdFile("forumapp.db").toFile())
                .openOrCreate("admin", "admin");
    }

    public static Nitrite getDatabase(){
        return database;
    }

}
