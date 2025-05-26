package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class RequestUrlDto {

    @NotBlank(message = "URL should not be empty")
    @URL(message = "Invalid URL format")
    @Size(max = 2048, message = "URL length should not exceed 2048 characters")
    private String url;
}
