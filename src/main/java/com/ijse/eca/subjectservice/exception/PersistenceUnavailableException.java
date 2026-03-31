package com.ijse.eca.subjectservice.exception;

public class PersistenceUnavailableException extends RuntimeException {

    public PersistenceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
