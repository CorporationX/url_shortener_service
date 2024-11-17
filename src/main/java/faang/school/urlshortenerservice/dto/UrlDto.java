package faang.school.urlshortenerservice.dto;


import jakarta.validation.constraints.Pattern;

public record UrlDto(
    @Pattern(regexp = "^http.+", message = "Incorrect link format")
    String url
) {
}
