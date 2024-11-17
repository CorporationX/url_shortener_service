package faang.school.urlshortenerservice.dto.url;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class UrlRequestDto {

    @URL
    @NotNull
    @Size(min = 1, max = 4096)
    private String url;
}
