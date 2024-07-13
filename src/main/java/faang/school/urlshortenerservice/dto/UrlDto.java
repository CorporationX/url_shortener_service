package faang.school.urlshortenerservice.dto;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlDto {

    @URL(protocol = "http", host = "localhost:8080")
    private String url;
}
