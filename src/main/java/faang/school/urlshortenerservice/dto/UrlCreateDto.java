package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class UrlCreateDto {
    @NotBlank(message = "URL не может быть пустым")
    @URL(message = "Некорректный формат URL")
    private String originalUrl;
}
