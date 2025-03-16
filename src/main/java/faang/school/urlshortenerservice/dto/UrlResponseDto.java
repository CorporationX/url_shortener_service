package faang.school.urlshortenerservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UrlResponseDto(

        @NotBlank
        String hash,

        @NotBlank
        String url,

        String shortUrl

        //LocalDateTime expiredAtDate
        ) {

        private static final String URL_PREFIX = "http://site.com/";

        @JsonProperty("shortUrl")
        public String getShortUrl() {
                if (hash != null) {
                        return URL_PREFIX + hash;
                }else {
                        return "";
                }
        }
}
