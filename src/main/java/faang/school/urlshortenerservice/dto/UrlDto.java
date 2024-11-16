package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UrlDto {
    @NotNull
    private String url;
    @Length(min = 1, max = 6)
    private String hash;
}
