package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlDto {
    @NotNull(message = "Invalid input data")
    @NotEmpty(message = "URL is Empty")
    @URL(message = "Invalid URL format")
    private String url;
}
