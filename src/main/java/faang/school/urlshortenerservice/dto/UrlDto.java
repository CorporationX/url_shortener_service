package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlDto {

    @URL(protocol = "https")
    @NotBlank(message = "URL cannot be blank")
    @Size(max = 2048, message = "URL cannot be longer than 2048 characters")
    private String url;
}
