package com.auron.urlshortener;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Service
public class UrlService {

    private final UrlMappingRepository repository;

    public UrlService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UrlMapping createShortUrl(String originalUrl) {
        validate(originalUrl);
        UrlMapping mapping = new UrlMapping(originalUrl);
        mapping = repository.save(mapping); // database assigns the id
        mapping.setShortCode(Base62.encode(mapping.getId()));
        return repository.save(mapping); // save again with the code
    }

    @Transactional
    public String resolve(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
        mapping.incrementClickCount();
        repository.save(mapping);
        return mapping.getOriginalUrl();
    }

    @Transactional(readOnly = true)
    public UrlMapping getStats(String shortCode) {
        return repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException(shortCode));
    }

    private void validate(String url) {
        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException e) {
            throw new InvalidUrlException(url);
        }
        if (uri.getHost() == null) {
            throw new InvalidUrlException(url);
        }
    }
}