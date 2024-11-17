package faang.school.urlshortenerservice.dto;

import faang.school.urlshortenerservice.validate.ValidateUrl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlDto {
    @ValidateUrl
    private String url;
}
