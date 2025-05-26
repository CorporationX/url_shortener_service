package faang.school.urlshortenerservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUrlDto {

    private String originalUrl;
    private String shortUrl;
}
