package faang.school.urlshortenerservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlDto {
    @NotNull(message = "Url can't be null or empty!")
    @URL(message = "Invalid URL format!")
    private String url;

    @Null
    @JsonIgnore
    private String hash;
}
