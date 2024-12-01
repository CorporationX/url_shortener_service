package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequest {
    @NotEmpty(message = "URL must not be empty")
    @URL(message = "Uncorrected URL")
    private String url;
}
