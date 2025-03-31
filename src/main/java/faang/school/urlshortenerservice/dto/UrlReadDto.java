package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UrlReadDto {
    private String hash;
    private String url;
}
