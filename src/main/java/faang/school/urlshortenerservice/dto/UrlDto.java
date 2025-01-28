package faang.school.urlshortenerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UrlDto(@JsonProperty("url") String url) {
}
