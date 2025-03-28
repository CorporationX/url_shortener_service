package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import org.hibernate.validator.constraints.URL;

@Getter
public class UrlRequestDto {
    @NotBlank
    @URL(message = "Некорректный URL")
    String url;
}
