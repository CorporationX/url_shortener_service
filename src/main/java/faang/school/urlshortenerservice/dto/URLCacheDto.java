package faang.school.urlshortenerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class URLCacheDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String hash;
    private String originalUrl;
}
