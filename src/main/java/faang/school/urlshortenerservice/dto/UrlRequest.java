package faang.school.urlshortenerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequest {
    @JsonProperty("url")
    @NotEmpty(message = "URL не может быть пустым")
    @Pattern(regexp = "^(http|https)://.+", message = "Неверный формат URL ")
    private String url;
}