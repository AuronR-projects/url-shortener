package com.auron.urlshortener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlMappingRepository repository;

    @InjectMocks
    private UrlService service;

    @Test
    void createShortUrlEncodesTheIdAsTheShortCode() {
        // pretend the database assigns id 125 on the first save
        when(repository.save(any(UrlMapping.class))).thenAnswer(invocation -> {
            UrlMapping m = invocation.getArgument(0);
            if (m.getId() == null) {
                ReflectionTestUtils.setField(m, "id", 125L);
            }
            return m;
        });

        UrlMapping result = service.createShortUrl("https://example.com");

        assertEquals("21", result.getShortCode());
        assertEquals("https://example.com", result.getOriginalUrl());
    }

    @Test
    void resolveReturnsOriginalUrlAndCountsTheClick() {
        UrlMapping mapping = new UrlMapping("https://example.com");
        mapping.setShortCode("abc");
        when(repository.findByShortCode("abc")).thenReturn(Optional.of(mapping));

        String target = service.resolve("abc");

        assertEquals("https://example.com", target);
        assertEquals(1, mapping.getClickCount());
    }

    @Test
    void resolveThrowsForUnknownCode() {
        when(repository.findByShortCode("nope")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> service.resolve("nope"));
    }
}