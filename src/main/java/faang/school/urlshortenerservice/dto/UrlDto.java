package faang.school.urlshortenerservice.dto;

import faang.school.urlshortenerservice.validation.ValidUrl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlDto {

    @ValidUrl
    private String url;
}
