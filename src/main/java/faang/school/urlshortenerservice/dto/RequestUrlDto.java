package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class RequestUrlDto {

    @URL(message = "Invalid URL")
    @NotBlank
    @Size(max = 2048)
    private String url;
}
