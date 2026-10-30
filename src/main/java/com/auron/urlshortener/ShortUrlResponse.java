package com.auron.urlshortener;

public record ShortUrlResponse(String shortCode, String shortUrl, String originalUrl) {
    
}
