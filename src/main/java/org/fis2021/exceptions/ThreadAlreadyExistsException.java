package org.fis2021.exceptions;

public class ThreadAlreadyExistsException extends Exception{
    public ThreadAlreadyExistsException(){
        super("A thread with this title already exists!");
    }
}
