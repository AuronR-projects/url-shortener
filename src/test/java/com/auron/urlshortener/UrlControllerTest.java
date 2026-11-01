package com.auron.urlshortener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UrlControllerTest {

    private UrlService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(UrlService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new UrlController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createReturns201AndTheShortCode() throws Exception {
        UrlMapping mapping = new UrlMapping("https://example.com");
        mapping.setShortCode("1");
        when(service.createShortUrl("https://example.com")).thenReturn(mapping);

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("1"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"));
    }

    @Test
    void createRejectsAnInvalidUrlWith400() throws Exception {
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"hello\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRejectsABlankUrlWith400() throws Exception {
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void redirectReturns302WithLocationHeader() throws Exception {
        when(service.resolve("1")).thenReturn("https://example.com");

        mockMvc.perform(get("/1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void unknownCodeReturns404() throws Exception {
        when(service.resolve("zzzz")).thenThrow(new UrlNotFoundException("zzzz"));

        mockMvc.perform(get("/zzzz"))
                .andExpect(status().isNotFound());
    }
}