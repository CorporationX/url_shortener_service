package faang.school.urlshortenerservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UrlDto {
    String hash;
    String url;
    LocalDateTime createdAt;
}
