package com.edp.library.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String s) {
        super(s);
    }
}