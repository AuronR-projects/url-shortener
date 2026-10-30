package com.auron.urlshortener;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUrlRequest(
        @NotBlank(message = "URL must not be blank")
        @Size(max = 2048, message = "URL must not be over 2048 characters")
        @Pattern(regexp = "^https?://.+", message = "URL must start with http:// or https://")
        String url
) {
}