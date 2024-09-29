package faang.school.urlshortenerservice.dto;

import lombok.*;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UrlDto {
    private Long id;
    @URL(message = "Invalid URL format")
    private String url;
}
