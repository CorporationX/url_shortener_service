package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequest {

    @NotNull(message = "URL must not be null")
    @NotBlank(message = "URL must not be blank")
    @URL(message = "URL must be a valid URL")
    private String url;
}
