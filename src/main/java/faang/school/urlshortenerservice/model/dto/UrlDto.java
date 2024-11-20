package faang.school.urlshortenerservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
public class UrlDto {
    @NotBlank(message = "URL can't be empty or null!")
    @URL(message = "Invalid URL format!")
    private String url;

    @Null
    @JsonIgnore
    private String hash;
}
