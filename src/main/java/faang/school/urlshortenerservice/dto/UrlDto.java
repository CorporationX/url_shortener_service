package faang.school.urlshortenerservice.dto;

import faang.school.urlshortenerservice.annotation.Url;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {

    @Url
    private String url;
}
