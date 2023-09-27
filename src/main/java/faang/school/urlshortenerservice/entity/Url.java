package faang.school.urlshortenerservice.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Url {
    private String hash;

    private String url;

    private LocalDateTime createdAt;
}
