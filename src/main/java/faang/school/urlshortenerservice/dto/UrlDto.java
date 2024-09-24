package faang.school.urlshortenerservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlDto {
    @NotNull
    @Size(min = 7, max = 2048)
    @URL
    String url;
}