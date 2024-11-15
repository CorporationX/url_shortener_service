package faang.school.urlshortenerservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class UrlDto {

    @NotBlank(message = "URL cannot be empty")
    @URL(message = "Invalid URL format")
    private String url;
}
