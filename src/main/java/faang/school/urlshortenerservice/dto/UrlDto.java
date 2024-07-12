package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

@Getter
public class UrlDto {
    @NotNull
    @URL(regexp = "(https?):\\/\\/)*")
    private String baseUrl;
}