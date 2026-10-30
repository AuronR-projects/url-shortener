package com.auron.urlshortener;

import java.time.Instant;

public record UrlStatsResponse(String shortCode, String originalUrl, Instant createdAt, int clickCount) {
}