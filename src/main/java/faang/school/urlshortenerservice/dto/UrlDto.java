package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlDto {
    @Null
    private String hash;

    @NotNull
    @URL
    private String url;
}
