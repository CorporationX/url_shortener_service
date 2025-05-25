package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class RequestUlrDto {

    @NotBlank(message = "URL should not be empty")
    @URL(message = "The wrong URL format")
    private String url;
}
