package com.infinityhr.auth.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    /** The requested thing does not exist. Will become HTTP 404 Not Found. */
    public ResourceNotFoundException(String message){
        super(message);
    }
}
