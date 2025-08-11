package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class UrlDto {
    String hash;
    String url;
    LocalDateTime createdAt;
}
