package faang.school.urlshortenerservice.dto;

import lombok.Getter;
import org.hibernate.validator.constraints.URL;

@Getter
public class UrlDto {
    @URL
    private String url;
}
