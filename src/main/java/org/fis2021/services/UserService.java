package org.fis2021.services;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.Cursor;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.fis2021.exceptions.UserNotFoundException;
import org.fis2021.exceptions.UsernameAlreadyExistsException;
import org.fis2021.models.User;
import org.w3c.dom.ls.LSOutput;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class UserService {

    private static ObjectRepository<User> userRepository;

    public static ObjectRepository<User> getUserRepository() {
        return userRepository;
    }

    public static void initService() {
        userRepository = DatabaseService.getDatabase().getRepository(User.class);
    }

    public static void addUser(String username, String password, String role) throws UsernameAlreadyExistsException {
        checkUserDoesNotAlreadyExist(username);
        userRepository.insert(new User(username, encodePassword(username, password), role));
    }

    public static User getUser(String username) throws UserNotFoundException {
        Cursor<User> cursor = userRepository.find(ObjectFilters.eq("username", username));
        for(User u : cursor){
            return u;
        }

        throw new UserNotFoundException(username);
    }

    public static void updateUser(User u){
        userRepository.update(u);
    }

    public static String getHashedUserPassword(String username) throws UserNotFoundException{
        return getUser(username).getPassword();
    }

    private static void checkUserDoesNotAlreadyExist(String username) throws UsernameAlreadyExistsException {
        for (User user : userRepository.find()) {
            if (Objects.equals(username, user.getUsername()))
                throw new UsernameAlreadyExistsException(username);
        }
    }

    public static String encodePassword(String salt, String password) {
        MessageDigest md = getMessageDigest();
        md.update(salt.getBytes(StandardCharsets.UTF_8));

        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        return new String(hashedPassword, StandardCharsets.UTF_8)
                .replace("\"", "");
    }

    private static MessageDigest getMessageDigest() {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("It fucked up :c");
        }
        return md;
    }


}