package com.auron.urlshortener;

public class InvalidUrlException extends RuntimeException {

    public InvalidUrlException(String url) {
        super("Invalid URL: " + url);
    }
}