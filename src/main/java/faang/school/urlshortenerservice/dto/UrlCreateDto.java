package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Setter
@Getter
public class UrlCreateDto {

    @NotBlank(message = "URL must not be blank")
    @URL(message = "Invalid URL format")
    @Size(max = 2048, message = "URL is too long")
    private String url;
}
