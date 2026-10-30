package com.auron.urlshortener;

public class UrlNotFoundException extends RuntimeException {

    public UrlNotFoundException(String shortCode) {
        super("Unknown short code: " + shortCode);
    }
}
