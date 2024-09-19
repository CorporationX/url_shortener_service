package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
public class UrlRequestDto {
    @URL(message = "Invalid format URL")
    private String longUrl;
}