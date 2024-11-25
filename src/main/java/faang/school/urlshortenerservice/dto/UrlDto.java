package faang.school.urlshortenerservice.dto;


import org.hibernate.validator.constraints.URL;

public record UrlDto(

        @URL(message = "Valid url required")
        String url) {
}
