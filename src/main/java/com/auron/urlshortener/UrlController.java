package com.auron.urlshortener;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @PostMapping("/api/urls")
    public ResponseEntity<ShortUrlResponse> create(@Valid @RequestBody CreateUrlRequest request) {
        UrlMapping mapping = service.createShortUrl(request.url());
        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{code}")
                .buildAndExpand(mapping.getShortCode())
                .toUriString();
        ShortUrlResponse body = new ShortUrlResponse(
                mapping.getShortCode(), shortUrl, mapping.getOriginalUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String target = service.resolve(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(target))
                .build();
    }

    @GetMapping("/api/urls/{shortCode}/stats")
    public UrlStatsResponse stats(@PathVariable String shortCode) {
        UrlMapping m = service.getStats(shortCode);
        return new UrlStatsResponse(
                m.getShortCode(), m.getOriginalUrl(), m.getCreatedAt(), m.getClickCount());
    }
}