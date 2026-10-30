package com.auron.urlshortener;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlService {

    private final UrlMappingRepository repository;

    public UrlService(UrlMappingRepository repository) {
        this.repository = repository;
    }


    @Transactional 
    public UrlMapping createShortUrl(String originalUrl) {
        UrlMapping mapping = new UrlMapping(originalUrl);
        mapping = repository.save(mapping);     // database assigns the id 
        mapping.setShortCode(Base62.encode(mapping.getId()));
        return repository.save(mapping);     // save again with the code
    }

    @Transactional 
    public String resolve(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode)
        .orElseThrow(() -> new IllegalArgumentException("Unknown short code: " + shortCode));
        mapping.incrementClickCount();
        repository.save(mapping);
        return mapping.getOriginalUrl();
    }

}