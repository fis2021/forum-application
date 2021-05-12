package org.fis2021.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemService {
    private static final String APPLICATION_FOLDER_PROD = ".forum-app-prod";
    private static final String APPLICATION_FOLDER_TEST = ".forum-app-test";
    private static final String USER_FOLDER = System.getProperty("user.home");

    public static Path getPathToProdFile(String... path) {
        return Paths.get(USER_FOLDER, APPLICATION_FOLDER_PROD).resolve(Paths.get(".", path));
    }

    public static Path getPathToTestFile(String... path) {
        return Paths.get(USER_FOLDER, APPLICATION_FOLDER_TEST).resolve(Paths.get(".", path));
    }

    public static void initProdDirectory() {
        Path applicationHomePath = Paths.get(USER_FOLDER, APPLICATION_FOLDER_PROD);
        if (!Files.exists(applicationHomePath))
            applicationHomePath.toFile().mkdirs();
    }

    public static void initTestDirectory(){
        Path applicationHomePath = Paths.get(USER_FOLDER, APPLICATION_FOLDER_TEST);
        if (!Files.exists(applicationHomePath))
            applicationHomePath.toFile().mkdirs();
    }
}
