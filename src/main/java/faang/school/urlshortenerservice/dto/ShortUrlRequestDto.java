package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Builder
public record ShortUrlRequestDto(

        @URL(message = "Invalid URL")
        @NotBlank(message = "URL cannot be empty")
        @Length(max = 1024, message = "URL length must be less than 2048 characters")
        String url
) {}