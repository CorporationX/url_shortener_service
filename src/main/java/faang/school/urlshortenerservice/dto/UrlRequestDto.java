package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlRequestDto {

    @NotBlank(message = "URL не должен быть пустым")
    @URL(message = "Некорректный URL.")
    private String originalUrl;
}