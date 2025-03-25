package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequestDto {

    @NotBlank(message = "URL не может быть пустым")
    @URL(message = "Некорректный URL")
    @Size(max = 2048, message = "URL слишком длинный")
    private String originalUrl;
}
