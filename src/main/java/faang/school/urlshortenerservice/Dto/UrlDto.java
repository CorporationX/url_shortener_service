package faang.school.urlshortenerservice.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlDto {

    @URL(message = "Невалидный формат URL")
    @NotBlank(message = "URL не может быть пустым")
    private String url;
}
