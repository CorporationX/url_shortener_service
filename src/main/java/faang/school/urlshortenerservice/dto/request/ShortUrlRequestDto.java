package faang.school.urlshortenerservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Getter
public class ShortUrlRequestDto {
    @NotNull
    @Length(max = 100)
    @URL
    private String fullUrl;
}
