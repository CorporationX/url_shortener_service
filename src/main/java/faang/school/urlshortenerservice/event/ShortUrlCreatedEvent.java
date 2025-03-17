package faang.school.urlshortenerservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortUrlCreatedEvent {
    private String hash;
    private String originalUrl;
    private String userId;
}
