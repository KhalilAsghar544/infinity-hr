package com.infinityhr.auth.exceptions;

public class DuplicateResourceException extends RuntimeException {
    /** Something with the same unique value already exists. Will become HTTP 409 Conflict. */
    public DuplicateResourceException(String message){
        super(message);
    }
}
