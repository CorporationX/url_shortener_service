package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@RequiredArgsConstructor
public class UrlDto {

    @URL
    @NotEmpty
    private String longUrl;
}
