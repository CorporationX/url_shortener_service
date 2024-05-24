package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class UrlDto {

    @URL(message = "Invalid URL")
    @NotBlank(message = "URL cannot be blank")
    private String longUrl;
}
