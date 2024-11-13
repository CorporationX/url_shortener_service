package faang.school.urlshortenerservice.dto.url;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class UrlRequestDto {

    @URL
    @NotNull
    private String url;
}
