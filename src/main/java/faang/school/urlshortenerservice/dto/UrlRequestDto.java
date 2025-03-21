package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record UrlRequestDto(
        //@URL(protocol = "http", message = "Incorrect URL")
        @NotBlank
        @Pattern(regexp = "^(http|https)://[a-zA-Z0-9-.]+\\.[a-zA-Z]{2,}(/\\S*)?$", message = "Incorrect URL format")
        String url

//        @URL(message = "Incorrect URL")
//        String shortUrl,

//        String hash

        //@Future(message = "Expiration date must be in the future")
        //LocalDateTime expiredAtDate
) {
}
