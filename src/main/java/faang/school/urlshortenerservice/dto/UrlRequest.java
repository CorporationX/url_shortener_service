package faang.school.urlshortenerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequest {
    @JsonProperty("url")
    @NotEmpty(message = "URL не может быть пустым")
    @URL(message = "Неверный формат URL")
    private String url;
}