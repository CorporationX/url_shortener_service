package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
public class UrlCreateDto {
    @NotNull(message = "URL не может быть пустым")
    @URL(message = "Неверный формат URL")
    private String originalUrl;
}
