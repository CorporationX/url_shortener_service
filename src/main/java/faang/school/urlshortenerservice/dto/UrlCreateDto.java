package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
public class UrlCreateDto {

    @NotBlank(message = "URL must not be blank")
    @URL(message = "Invalid URL format")
    private String url;
}
