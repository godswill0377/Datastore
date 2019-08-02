package com.dataexo.zblog.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Not Found - The requested item doesn’t exist");
    }
}
