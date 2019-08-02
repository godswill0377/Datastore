package com.dataexo.zblog.exception;

public class RequestFailedException extends RuntimeException {
    public RequestFailedException() {
        super("Request Failed - Parameters were valid but request failed");
    }
}
